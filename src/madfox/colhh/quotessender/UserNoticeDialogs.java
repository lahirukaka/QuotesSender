package madfox.colhh.quotessender;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public final class UserNoticeDialogs extends android.app.DialogFragment {
	
	private String title;	private int icon;
	private String msg;
	
	private boolean positive=false;	private boolean negative;
	private String[] btn_texts;		private OnClickListener listener;
	
	public UserNoticeDialogs(String title,int icon,String msg) {
		this.title=title;	this.icon=icon;
		this.msg=msg;
	}
	
	public UserNoticeDialogs() {
		// Zero Args Constructor
	}
	
	public void setButtons(boolean positive,boolean negative,String[] btn_texts,OnClickListener listeners)
	{
		this.positive=positive;
		this.negative=negative;
		this.btn_texts=btn_texts;
		this.listener=listeners;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
		builder.setTitle(title)	//setting title
				.setIcon(icon) 	//setting icon
				.setInverseBackgroundForced(true)
				.setMessage(msg);// setting msg
		
		if(positive)builder.setPositiveButton(btn_texts[0], listener);
		if(negative)builder.setNegativeButton(btn_texts[1], listener);
		
		return builder.create();
	}
	
	public void onDismiss(android.content.DialogInterface dialog) {
		super.onDismiss(dialog);
		if(NDL!=null){NDL.onNoticeDismiss();}
	};
	
	/*interface stuff*/ 
	NoticeDialogListener NDL;
	interface NoticeDialogListener
	{
		public void onNoticeDismiss();
	}
	
	public void setNoticeDialogListener(NoticeDialogListener NDL)
	{
		this.NDL=NDL;
	}
}
