package madfox.colhh.quotessender;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Loader;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.SimpleCursorTreeAdapter;

public class ContactsListAdapter extends SimpleCursorTreeAdapter {

	private ContactsFragment confrag;
	protected final HashMap<Integer, Integer> mGroupMap;

	/* Constructor */
	@SuppressLint("UseSparseArrays") public ContactsListAdapter(Fragment fragment, int groupLayout,
			int childLayout, String[] groupFrom, int[] groupTo,
			String[] childrenFrom, int[] childrenTo) {

		super(fragment.getActivity(), null, groupLayout, groupFrom, groupTo,
				childLayout, childrenFrom, childrenTo);
		confrag = (ContactsFragment) fragment;
		mGroupMap = new HashMap<Integer, Integer>();
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		// Logic to get the child cursor on the basis of selected group.
		int groupPos = groupCursor.getPosition();
		int groupId = groupCursor.getInt(groupCursor
				.getColumnIndex(ContactsContract.Contacts._ID));

		//Log.d(LOG_TAG, "getChildrenCursor() for groupPos " + groupPos);
		//Log.d(LOG_TAG, "getChildrenCursor() for groupId " + groupId);

		mGroupMap.put(groupId, groupPos);
		Loader<Cursor> loader = confrag.getLoaderManager().getLoader(groupId);
		if (loader != null && !loader.isReset()) {
			confrag.getLoaderManager().restartLoader(groupId, null, confrag);
		} else {
			confrag.getLoaderManager().initLoader(groupId, null, confrag);
		}

		return null;
	}

	public HashMap<Integer, Integer> getGroupMap() {
		return mGroupMap;
	}

}
