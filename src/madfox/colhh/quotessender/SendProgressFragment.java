package madfox.colhh.quotessender;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SendProgressFragment extends Fragment {

	private Activity activity;
	private ProgressBar pb;
	private TextView txt;
	
	public boolean sent_status;	public boolean delivery_status;
	
	private ProgressTask ptask;
	private BroadcastReceiver b1;	private BroadcastReceiver b2;
	
	private String number;		private String quote;
	
	public SendProgressFragment(String number,String quote) {
		this.number=number;
		this.quote=quote;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		try{
			sendSms();
		}catch(Exception er){}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pb=(ProgressBar)getView().findViewById(R.id.send_progress);
		txt=(TextView)getView().findViewById(R.id.send_progress_txt);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity=activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.send_progress_layout, container,false);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.activity=null;
		this.pb=null;
		this.ptask=null;
		this.b1=null;
		this.b2=null;
		try{
			activity.unregisterReceiver(b1);
			activity.unregisterReceiver(b2);
		}catch(Exception er){}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		try{
			activity.unregisterReceiver(b1);
			activity.unregisterReceiver(b2);
		}catch(Exception er){}
	}
	boolean finish_process=false;
	public void sendSms() throws Exception
	{
		final String SENT="SMS_SENT";
		final String DELIVER="SMS_DELIVERED";
		

		txt.setText("Start sending...");	pb.setProgress(0);
		ptask=new ProgressTask(pb,txt,this);
		ptask.execute();
		
		SmsManager sms = SmsManager.getDefault(); //get sms manager
		
		/*preparing msgs*/
		ArrayList<String> msgs=sms.divideMessage(quote);
		ArrayList<PendingIntent> sent_pis=new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliver_pis=new ArrayList<PendingIntent>();
		for(String msg:msgs)
		{
			/*pending intents*/
			sent_pis.add(PendingIntent.getBroadcast(activity, 0, new Intent(SENT), 0));
			deliver_pis.add(PendingIntent.getBroadcast(activity, 0, new Intent(DELIVER), 0));
		}
		
		pb.setProgress(10);
		
		activity.registerReceiver(b1=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent arg1) {
				switch (getResultCode())
                {

                    case Activity.RESULT_OK:
                        //send_status=("Sent SMS and Waiting for delivery");
                        onCompletion(true,null);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        //send_status=("Generic failure");
                        onCompletion(false,null);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    	//send_status=("No service");
                    	onCompletion(false,null);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    	//send_status=("Null PDU");
                    	onCompletion(false,null);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    	//send_status=("Radio off");
                    	onCompletion(false,null);
                        break;
                }
				
				//txt.setText(send_status);
			}
		}, new IntentFilter(SENT));
		
		activity.registerReceiver(b2=new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent arg1) 
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                    	//deliver_status=("SMS delivered");
                    	onCompletion(null,true);
                        break;
                    case Activity.RESULT_CANCELED:
                    	//deliver_status=("SMS not delivered");
                    	onCompletion(null,false);
                        break;                        
                }
                
               // txt.setText(deliver_status);
            }
        }, new IntentFilter(DELIVER)); 
		
		try{
        sms.sendMultipartTextMessage(number, null, msgs, sent_pis, deliver_pis); 
		}catch(IllegalArgumentException er)
		{
			onCompletion(false, null);
		}
	}
	
	public void onCompletion(Boolean sent,Boolean delivery)
	{
		if(delivery==null && sent==null)
		{
			try{
				this.activity.unregisterReceiver(b1);
			}catch(Exception er){}
			ptask.cancel(true);
			
			if(SR!=null && sent_status){this.SR.onSendingCompleted(true);}
			else if(SR!=null && !sent_status){this.SR.onSendingCompleted(false);}
		}else if(delivery==null)
		{
			try{
				this.activity.unregisterReceiver(b1);
			}catch(Exception er){}
			if(sent)
			{
				txt.setText("Waiting for Delivery Report");
				sent_status=true;
			}else
			{
				sent_status=false;
				ptask.cancel(true);
				if(SR!=null){this.SR.onSendingCompleted(false);}
			}
		}else
		{
			try{
				this.activity.unregisterReceiver(b2);
			}catch(Exception er){}
			
			if(delivery)
			{
				ptask.cancel(true);
				if(SR!=null){this.SR.onSendingCompleted(true);}
			}else
			{
				ptask.cancel(true);
				if(SR!=null){this.SR.onSendingCompleted(false);}
			}
		}
	}
	
	/*interface stuff*/
	SendingResult SR;
	interface SendingResult
	{
		public void onSendingCompleted(boolean result);
	}
	
	public void registerSendingResult(SendingResult SR)
	{
		this.SR=SR;
	}
}
