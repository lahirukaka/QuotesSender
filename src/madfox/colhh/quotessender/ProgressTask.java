package madfox.colhh.quotessender;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressTask extends AsyncTask<Void, Integer, Boolean> {

	private ProgressBar pb;
	//private TextView txt;
	private SendProgressFragment sfarg;
	
	public ProgressTask(ProgressBar pb,TextView txt,SendProgressFragment sfarg) {
		this.pb=pb;	//this.txt=txt;
		this.sfarg=sfarg;
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		int count=0;
		while(count<10)
		{
			if(this.isCancelled())
			{
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			finally{
				count++;
				publishProgress(count*10);
			}
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		this.sfarg.onCompletion(null,null);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		pb.setProgress(values[0]);
	}

}
