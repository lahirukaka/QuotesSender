package madfox.colhh.quotessender;

import java.util.HashMap;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class ContactsFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private Activity activity;
	private ExpandableListView elist;

	/*Contact Phone Data*/
	private static final String[] PHONE_PROJECTION = new String[] {
			ContactsContract.CommonDataKinds.Phone._ID,
			ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.TYPE };
	/*Contact Data*/
	private static final String[] CONTACT_PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME };

	ContactsListAdapter adapter; //Adapter

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/*ExpandableList*/
		elist = (ExpandableListView) getView().findViewById(R.id.contacts_list);
		elist.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView eview, View view, int arg2,
					int arg3, long arg4) {
				TextView num=(TextView)view.findViewById(R.id.contacts_child_txt);
				if(CT!=null){CT.onContactPick(num.getText().toString());}				
				return false;
			}
		});

		adapter = new ContactsListAdapter(this, R.layout.contacts_group_layout, //group layout
				R.layout.contacts_child_layout,									//child layout
				new String[] { ContactsContract.Contacts.DISPLAY_NAME },		//group:from
				new int[] { R.id.contacts_group_txt },							//group:to
				new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },	//child:from
				new int[] { R.id.contacts_child_txt });							//child:to
		
		elist.setAdapter(adapter);
		Loader<Cursor> loader = getLoaderManager().getLoader(-1);
		if (loader != null && !loader.isReset()) 
		{
			getLoaderManager().restartLoader(-1, null, this);
		} else 
		{
			getLoaderManager().initLoader(-1, null, this);
		}
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.contacts_layout, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		/* set Title */
		ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CONTACT;
		((MainActivity) activity).lbl_title.setText(ObjectsInApp.WhereAmI);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/* LoaderCallBack */

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		 CursorLoader cl;
		 if (id != -1) 
		 {
			 // child cursor
			 Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			 String selection = "(" + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? )";
			 String sortOrder = ContactsContract.CommonDataKinds.Phone.TYPE + " COLLATE LOCALIZED ASC";
			 String[] selectionArgs = new String[] { String.valueOf(id) };
			 
			 cl = new CursorLoader(getActivity(), contactsUri, PHONE_PROJECTION,selection, selectionArgs, sortOrder);
		 } else
		 {
			 // group cursor
			 Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
			 String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
			 + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
			 + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";
			 
			 String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
			 cl = new CursorLoader(getActivity(), contactsUri, CONTACT_PROJECTION,selection, null, sortOrder);
		 }
		 
		 return cl;
		 
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Setting the new cursor onLoadFinished. (Old cursor would be closed
		 // automatically)
		 int id = loader.getId();
		 if (id != -1) {
			 // child cursor
			 if (!data.isClosed())
			 {
				 HashMap<Integer, Integer> groupMap = adapter.getGroupMap();
				 try {
				 int groupPos = groupMap.get(id);
				 adapter.setChildrenCursor(groupPos, data);
				 } catch (NullPointerException e) {}
			 }
		 } else
		 {
			 adapter.setGroupCursor(data);
		 }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Called just before the cursor is about to be closed.
		 int id = loader.getId();
		 if (id != -1) 
		 {
			 // child cursor
			 try {
			 adapter.setChildrenCursor(id, null);
			 } catch (NullPointerException e) { }
		 } else
		 {
			 adapter.setGroupCursor(null);
		 }
	}
	/* .................................................................. */
	
	/*Interface stuff*/
	ContactsTransporter CT;
	interface ContactsTransporter
	{
		public void onContactPick(String number);
	}
	
	public void registerContactTransporter(ContactsTransporter CT)
	{
		this.CT=CT;
	}

}
