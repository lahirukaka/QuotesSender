package madfox.colhh.quotessender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import madfox.colhh.saleslib.Packages;
import madfox.colhh.saleslib.install.SalesKit;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ironsource.mobilcore.MobileCore;

public class ScheduleAdapter implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, OnClickListener {

	private static final String AUTHORITY=Packages.SSP.getPackage() + ".provider";
	private static final String TABLE="schedules";
	private static final String PATH="content://" + AUTHORITY + "/" + TABLE;
	private static final Uri CONTENT_URI=Uri.parse(PATH);
	
	private static final String COLUMN_NAME_NUMBER = "number";
	private static final String COLUMN_NAME_MSG = "message";
	private static final String COLUMN_NAME_TIME = "time";
	
	private Activity activity;
	private Calendar cal;
	private ContentResolver client;
	private UserNoticeDialogs UND;
	private DatePickerDialog datepick;
	private TimePickerDialog timepick;
	
	private String date_string=null;
	private String msg=null;
	private String number=null;
	private long time=0;
	
	public ScheduleAdapter(Activity activity) {
		this.activity=activity;
		client=activity.getContentResolver();
	}
	public boolean isInstalled()
	{
		if(SalesKit.isInstalled(Packages.SSP, activity))
		{
			return true;
		}else
		{
			SalesKit.askUserToInstall(Packages.SSP, activity);
			return false;
		}
	}
	/**
	 * set data
	 */
	public boolean setData(String msg , String number)
	{
		if(!msg.isEmpty() && !number.isEmpty())
		{
			this.msg=msg;	this.number=number;
			return true;
		}else
		{
			UND=new UserNoticeDialogs("Incomplete Details", R.drawable.error_dark, 
					"You must complete Message and Number First");
			UND.setButtons(true, false, new String[] {"Ok"}, null);
			UND.show(activity.getFragmentManager(), "incompletestatus");
			return false;
		}
	}
	/**
	 * Start Setting Info
	 */
	public void start()
	{
		cal=Calendar.getInstance();
		datepick=new DatePickerDialog(activity, 
					android.R.style.Theme_Holo_Light_Dialog, 
					this, 
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					cal.get(Calendar.DATE));
		
		datepick.setTitle("Set the date");
		timepick=new TimePickerDialog(activity, 
					android.R.style.Theme_Holo_Light_Dialog, 
					this, 
					cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE), 
					true);
		timepick.setTitle("Set the time");
		/*gather date*/
		datepick.show();
	}
	/**
	 * get long date
	 */
	@SuppressLint("SimpleDateFormat") private static long getLongDate(String date)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy-H:m");
		Date dateo;
		try{
			dateo= sdf.parse(date);
		}catch (Exception e) {return -1;}
		return dateo.getTime();
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hour, int minute) {
		if(view.isShown())
		{
			date_string+="-"+hour+":"+minute;
			time=getLongDate(date_string);
			
			/*insert into CP*/
			ContentValues values=new ContentValues();
			values.put(COLUMN_NAME_MSG, msg);
			values.put(COLUMN_NAME_NUMBER, number);
			values.put(COLUMN_NAME_TIME, time);
			
			Uri uri=client.insert(CONTENT_URI, values);
			if(uri.getLastPathSegment().equals("-1"))
			{
				Toast.makeText(activity, "Failed Scheduling Message...", Toast.LENGTH_LONG).show();
				//ad show
				MobileCore.showInterstitial(activity, null);
			}else
			{
				Toast.makeText(activity, "Message Scheduled Successfully", Toast.LENGTH_LONG).show();
				//ad show
				MobileCore.showInterstitial(activity, null);
			}
		}
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if(view.isShown())
		{
			date_string=dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
			/*show time picker dialog*/
			timepick.show();
		}
	}

	/*get TSM*/
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which==DialogInterface.BUTTON_POSITIVE)
		{
			Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("http:///play.google.com/store/apps/details?id=madfox.colhh.timeschedulemessenger"));
			activity.startActivity(intent);
		}
	}
	
}
