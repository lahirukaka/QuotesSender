package madfox.colhh.quotessender;

import madfox.colhh.quotessender.SendProgressFragment.SendingResult;
import madfox.colhh.saleslib.Packages;
import madfox.colhh.saleslib.adsdisplayer.AdsFlasher;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ironsource.mobilcore.MobileCore;

public class SendFragment extends Fragment implements OnTouchListener,SendingResult {

	private Activity activity;
	private SendProgressFragment spfrag;
	
	MainActivity main;

	private EditText txt_quote;		private EditText txt_phone;		private TextView btn_send;
	private TextView btn_schedule;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity=activity;
		this.main=((MainActivity)activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		txt_phone=(EditText)getView().findViewById(R.id.send_txt_phone);
		txt_quote=(EditText)getView().findViewById(R.id.send_txt_quote);
		btn_send=(TextView)getView().findViewById(R.id.send_btn_send);
		btn_schedule=(TextView)getView().findViewById(R.id.schedule_btn_send);
		btn_send.setOnTouchListener(this);
		btn_schedule.setOnTouchListener(this);
		
		if(savedInstanceState!=null)
		{
			txt_phone.setText(savedInstanceState.getString("number"));
			txt_quote.setText(savedInstanceState.getString("quote"));
		}else
		{
			if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
			{
				getFragmentManager().beginTransaction()
						.replace(R.id.adz, new AdsFlasher(3000, 0, 
								Packages.BD_WISHES_SENDER,
								Packages.JOKES_SENDER,
								Packages.QUOTES_SENDER,
								Packages.XMAS_SENDER))
						.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.send_layout, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity)activity).lbl_title.setText(ObjectsInApp.SEND_READY);
		txt_quote.setText(MainActivity.quote);		txt_phone.setText(MainActivity.number);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("quote", this.txt_quote.getText().toString());
		outState.putString("number", this.txt_phone.getText().toString());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/*Events*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		TextView view=(TextView)v;
		switch(view.getId())
		{
			case R.id.send_btn_send:
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					btn_send.setBackgroundColor(Color.WHITE);
					btn_send.setTextColor(Color.rgb(102, 153, 0));
					return true;
				}else if(event.getAction()==MotionEvent.ACTION_UP)
				{
					btn_send.setBackgroundColor(Color.rgb(102, 185, 0));
					btn_send.setTextColor(Color.WHITE);
					int layout=R.id.send_fragment;
					
					if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE)
					{
						layout=R.id.main_fragment_window2;
					}
					
					if(spfrag==null)
					{
						/*set fragment*/
						spfrag=new SendProgressFragment(txt_phone.getText().toString(),txt_quote.getText().toString());
						getFragmentManager().beginTransaction()
								.replace(layout, spfrag, "spfrag")
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
								.commit();
						spfrag.registerSendingResult(this);
					}
					return true;
				}
				break;
				
			case R.id.schedule_btn_send:
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					btn_schedule.setBackgroundColor(Color.WHITE);
					btn_schedule.setTextColor(Color.rgb(102, 153, 0));
					return true;
				}else if(event.getAction()==MotionEvent.ACTION_UP)
				{
					btn_schedule.setBackgroundColor(Color.rgb(102, 185, 0));
					btn_schedule.setTextColor(Color.WHITE);
					
					ScheduleAdapter schedule=new ScheduleAdapter(activity);
					if(schedule.isInstalled())
					{
						if(schedule.setData(this.txt_quote.getText().toString(), 
								this.txt_phone.getText().toString()))
						schedule.start();
					}
					
					return true;
				}
				break;
			default:
				return false;
		}
		return false;
	}

	@Override
	public void onSendingCompleted(boolean result) {
		try{
		getFragmentManager().beginTransaction()
				.remove(spfrag)
				.commit();
		}catch(NullPointerException er){}
		spfrag=null;
		
		if(result)
		{
			Toast toast=Toast.makeText(getActivity(), "Quote Sent Successfully...", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			 //ad show
			MobileCore.showInterstitial(activity, null);
		}else
		{
			Toast toast=Toast.makeText(getActivity(), "Quote Sending failed, Please resend...", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			 //ad show
			MobileCore.showInterstitial(activity, null);
		}
	}
	
}
