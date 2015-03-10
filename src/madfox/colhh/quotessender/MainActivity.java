package madfox.colhh.quotessender;

import madfox.colhh.quotessender.CategoryFragment.ValueTransporter;
import madfox.colhh.quotessender.ContactsFragment.ContactsTransporter;
import madfox.colhh.quotessender.QuotesFragment.QuoteTransporter;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ironsource.mobilcore.MobileCore;
import com.ironsource.mobilcore.MobileCore.AD_UNITS;
import com.ironsource.mobilcore.MobileCore.LOG_TYPE;

public class MainActivity extends Activity implements ValueTransporter,
		QuoteTransporter, ContactsTransporter, OnBackStackChangedListener {

	public static final String MC_DEV_HASH = "5RKJ8UBJW6X8MDNDSPE6F8JIBSGDP";
	public static boolean INTERESTITIAL_READY = false;
	public static boolean STICKEEZ_READY = false;

	/* Objects Declaration */
	private FragmentManager FM;
	private CategoryFragment cfrag;
	private QuotesFragment qfrag;
	private ContactsFragment confrag;
	private SendFragment sfrag;
	
	//public DataBaseAdapter DBA;
	//private ProgressDialog PD;

	/* Variables Declaration */
	public static String category;
	public static String quote;
	public static String number;

	/* Views Declaration */
	public TextView lbl_title;
	public ProgressBar pbr;
	public TextView p_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Mobilecore */
		MobileCore.init(this, MC_DEV_HASH, LOG_TYPE.DEBUG,
				AD_UNITS.INTERSTITIAL);

		/* Views Initialization */
		this.lbl_title = (TextView) findViewById(R.id.main_lbl_title);
		this.pbr = (ProgressBar) findViewById(R.id.progress_round);
		this.p_txt = (TextView) findViewById(R.id.progress_txt);

		/* Objects initialization */
		this.FM = getFragmentManager();
		this.FM.addOnBackStackChangedListener(this);
		/* Check entrance */
		if (savedInstanceState != null) {
			category = savedInstanceState.getString("category");
			quote = savedInstanceState.getString("quote");
			number = savedInstanceState.getString("number");
			ObjectsInApp.WhereAmI = savedInstanceState.getString("window");

			switch ((ObjectsInApp.WhereAmI.toString())) {
			case ObjectsInApp.SEND_READY:
				sfrag = (SendFragment) FM.findFragmentByTag("sfrag");
				// sfrag.registerContactTransporter(this);
			case ObjectsInApp.CHOOSE_CONTACT:
				confrag = (ContactsFragment) FM.findFragmentByTag("confrag");
				confrag.registerContactTransporter(this);
			case ObjectsInApp.CHOOSE_QUOTE:
				qfrag = (QuotesFragment) FM.findFragmentByTag("qfrag");
				qfrag.registerQuoteTransporter(this);
			case ObjectsInApp.CHOOSE_CATEGORY:
				cfrag = (CategoryFragment) FM.findFragmentByTag("cfrag");
				cfrag.registerValueTransporter(this);
			}
		} else {
			ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CATEGORY;
			cfrag = new CategoryFragment();
			FM.beginTransaction()
					.add(R.id.main_fragment_window, cfrag, "cfrag")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
			cfrag.registerValueTransporter(this);
		}
		reAttachFragments();
	}

	private void reAttachFragments() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			FM.popBackStackImmediate(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} else {
			this.pbr.setVisibility(View.VISIBLE);
			this.p_txt.setText(R.string.land_loading_txt);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.FM = null;
		this.cfrag = null;
		this.qfrag = null;
		this.confrag = null;
		this.sfrag = null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("category", category);
		outState.putString("quote", quote);
		outState.putString("number", number);
		outState.putString("window", ObjectsInApp.WhereAmI);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setCustomView(R.layout.custom_actionbar);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCategorySelect(String category) {
		this.category = category;
		ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_QUOTE;

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			qfrag = new QuotesFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window, qfrag, "qfrag")
					.addToBackStack("queryfrag").commit();
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			FM.popBackStackImmediate(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			qfrag = new QuotesFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window2, qfrag, "qfrag")
					.addToBackStack("queryfrag").commit();
		}

		qfrag.registerQuoteTransporter(this);
	}

	@Override
	public void onQuoteSelect(String quote) {
		this.quote = quote;
		ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CONTACT;

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			confrag = new ContactsFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window, confrag, "confrag")
					.addToBackStack("confrag").commit();
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (FM.findFragmentByTag("confrag") != null) {
				confrag = null;
				if (FM.getBackStackEntryCount() > 1) {
					FM.popBackStackImmediate();
				}
			}
			confrag = new ContactsFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window2, confrag, "confrag")
					.addToBackStack("confrag").commit();
		}
		confrag.registerContactTransporter(this);
	}

	@Override
	public void onContactPick(String number) {
		this.number = number;
		ObjectsInApp.WhereAmI = ObjectsInApp.SEND_READY;

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			sfrag = new SendFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window, sfrag, "sfrag")
					.addToBackStack("sfrag").commit();
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (FM.findFragmentByTag("sfrag") != null) {
				sfrag = null;
				if (FM.getBackStackEntryCount() > 2) {
					FM.popBackStackImmediate();
				}
			}
			sfrag = new SendFragment();
			FM.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
					.replace(R.id.main_fragment_window, sfrag, "sfrag")
					.addToBackStack("sfrag").commit();
		}
	}

	int backstack_count = 0;

	@Override
	public void onBackStackChanged() {
		backstack_count = FM.getBackStackEntryCount();
		try {
			pbr.setVisibility(View.GONE);
			p_txt.setVisibility(View.GONE);
		} catch (Exception err) {
		}
		switch (backstack_count) {
		case 0:
			ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CATEGORY;
			try {
				pbr.setVisibility(View.VISIBLE);
				p_txt.setVisibility(View.VISIBLE);
			} catch (Exception err) {
			}
			break;
		case 1:
			ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_QUOTE;
			break;
		case 2:
			ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CONTACT;
			break;
		case 3:
			ObjectsInApp.WhereAmI = ObjectsInApp.SEND_READY;
		}
		this.lbl_title.setText(ObjectsInApp.WhereAmI);
	}

}
