package madfox.colhh.quotessender;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class QuotesFragment extends Fragment {

	private Activity activity;

	private String category;
	private String selected_quote = null;
	private int category_int = 0;
	private ListView list;
	private String[] quotes;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		list=(ListView)getView().findViewById(R.id.quote_list);
		//list.setBackgroundColor(Color.rgb(102, 153, 00));
		getQuotes();
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(activity, R.layout.quote_list,this.quotes);
		this.list.setAdapter(adapter);
		
		this.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int index,
					long id) {
				QT.onQuoteSelect(((TextView)view).getText().toString());
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.activity=null;
		this.category=null;
		this.selected_quote=null;
		this.list=null;
		this.quotes=null;
	}

	@Override
	public void onResume() {
		super.onResume();
		/* set Title */
		ObjectsInApp.WhereAmI=ObjectsInApp.CHOOSE_QUOTE;
		((MainActivity)activity).lbl_title.setText(ObjectsInApp.WhereAmI+" from "+category);
	}

	private void getQuotes()
	{
		switch(category)
		{
			case "Friendship":
				category_int=R.array.Friendship;
				break;
			case "Motivation":
				category_int=R.array.Motivation;
				break;
			case "Success":
				category_int=R.array.Success;
				break;
			case "Positive":
				category_int=R.array.Positive;
				break;
			case "Leadership":
				category_int=R.array.Leadership;
				break;
			case "Life":
				category_int=R.array.Life;
				break;
			case "Education":
				category_int=R.array.Education;
				break;
			case "Smile":
				category_int=R.array.Smile;
				break;
			case "Wisdom":
				category_int=R.array.Wisdom;
				break;
			case "Change":
				category_int=R.array.Change;
				break;
		}
		this.quotes=getResources().getStringArray(category_int);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		this.category = MainActivity.category;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			this.category = savedInstanceState.getString("category");
		}
		return inflater.inflate(R.layout.quote_layout, container,false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (selected_quote == null) {
			outState.putString("category", this.category);
		}
	}
	
	/*Interface stuff*/
	QuoteTransporter QT;
	interface QuoteTransporter
	{
		public void onQuoteSelect(String quote);
	}
	
	public void registerQuoteTransporter(QuoteTransporter QT)
	{
		this.QT=QT;
	}
	
}
