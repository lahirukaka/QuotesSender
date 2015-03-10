package madfox.colhh.quotessender;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ironsource.mobilcore.MobileCore;

public class CategoryFragment extends Fragment {

	private ListView list;
	private Activity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list = (ListView) getView().findViewById(R.id.cat_list);
		list.setAdapter(new CustomBaseAdapter(activity));

		/* list events */
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int index, long id) {
				TextView text = (TextView) view
						.findViewById(R.id.cat_list_category);
				MobileCore.showInterstitial(activity, null);
				if (VT != null) {
					VT.onCategorySelect(text.getText().toString());
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		/* set Title */
		ObjectsInApp.WhereAmI = ObjectsInApp.CHOOSE_CATEGORY;
		((MainActivity) activity).lbl_title.setText(ObjectsInApp.WhereAmI);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.list = null;
		this.activity = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			// this.list=(ListView)savedInstanceState.get("list");
			// this.activity=(MainActivity)savedInstanceState.get("activity");
		}
	}

	/* Interface stuff */
	ValueTransporter VT;

	interface ValueTransporter {
		public void onCategorySelect(String category);
	}

	public void registerValueTransporter(ValueTransporter valuetransporter) {
		VT = valuetransporter;
	}

	/*
	 * ...........................................................................
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.select_cat, container, false);
	}

	/* List Resources stuff */
	class SingleRow {
		String category_name;
		Drawable category_image;

		SingleRow(String category_name, Drawable category_image) {
			this.category_name = category_name;
			this.category_image = category_image;
		}
	}

	/* Adapter Stuff */
	class CustomBaseAdapter extends BaseAdapter {
		public ArrayList<SingleRow> lrow;
		Activity activity;

		@SuppressLint("Recycle")
		public CustomBaseAdapter(Activity activity) {
			this.activity = activity;
			lrow = new ArrayList<SingleRow>();

			Resources res = getResources();
			 String[] cate_name = res.getStringArray(R.array.categories);
			TypedArray cat_img = res.obtainTypedArray(R.array.cat_images);

			for (int r = 0; r < cate_name.length; r++) {
				lrow.add(new SingleRow(cate_name[r], cat_img.getDrawable(r)));
			}
		}

		@Override
		public int getCount() {
			return lrow.size();
		}

		@Override
		public Object getItem(int position) {
			return lrow.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			RowViewsHolder viewholder = null;
			if (convertView == null) {
				LayoutInflater inflater = activity.getLayoutInflater();
				v = inflater.inflate(R.layout.cat_list, parent, false);
				viewholder = new RowViewsHolder(v);
				v.setTag(viewholder);
			} else {
				viewholder = (RowViewsHolder) v.getTag();
			}
			viewholder.txt.setText(lrow.get(position).category_name);
			viewholder.img.setImageDrawable(lrow.get(position).category_image);
			return v;
		}

		class RowViewsHolder {
			ImageView img;
			TextView txt;

			public RowViewsHolder(View v) {
				this.txt = (TextView) v.findViewById(R.id.cat_list_category);
				this.img = (ImageView) v.findViewById(R.id.cat_list_img);
			}
		}

	}

}
