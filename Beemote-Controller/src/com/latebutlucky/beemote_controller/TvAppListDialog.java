package com.latebutlucky.beemote_controller;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class TvAppListDialog extends Dialog {

	IndexableListView listview;
	App_Adapter App_Adapter;
	ArrayList<TvAppInfo> appInfoArry = new ArrayList<TvAppInfo>();;

	public TvAppListDialog(Context context, ArrayList<TvAppInfo> TvList) {
		super(context);
		// setTheme(R.style.Theme_dialog); //테두리 없애기위해
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_dialog);
		// getWindow().setBackgroundDrawable(
		// new ColorDrawable(Color.TRANSPARENT));
		// getWindow().setBackgroundDrawable(
		// getResources().getDrawable(R.drawable.gridback));
		listview = (IndexableListView) findViewById(R.id.applist_listview);
		appInfoArry = TvList;
		App_Adapter = new App_Adapter();
		listview.setAdapter(App_Adapter);
		listview.setFastScrollEnabled(true);
		
		Comparator<TvAppInfo> myComparator = new Comparator<TvAppInfo>() {
			Collator app_Collator = Collator.getInstance();

			@Override
			public int compare(TvAppInfo a, TvAppInfo b) {
				return app_Collator.compare(a.name, b.name);
			}
		};
		Collections.sort(appInfoArry, myComparator);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentView, View view,

			int position, long id) {

			}

		});
	}

	public class App_Adapter extends BaseAdapter implements SectionIndexer {

		private String mSections = "#ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎABCDEFGHIJKLMNOPQRSTUVWXYZ";

		ImageView image;
		TextView name;
		LayoutInflater inflater;

		public App_Adapter() {
			inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return appInfoArry.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.app_list_layout,
						parent, false);
			}
			image = (ImageView) convertView.findViewById(R.id.applist_image);
			name = (TextView) convertView.findViewById(R.id.applist_name);

			Bitmap bitmap = appInfoArry.get(position).appIcon;
			Bitmap bit = Bitmap.createBitmap(bitmap);
			Drawable drawable = new BitmapDrawable(bit);
			Drawable icon = Utilities.createIconThumbnail(drawable,
					getContext());
			image.setImageDrawable(icon);
			name.setText(appInfoArry.get(position).name);
			return convertView;

		}

		@Override
		public int getPositionForSection(int section) {
			// If there is no item for current section, previous section
			// will be selected
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						// For numeric section
						for (int k = 0; k <= 9; k++) {
							if (StringMatcher.matchInitial(
									String.valueOf(appInfoArry.get(j).name),
									String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.matchInitial(
								String.valueOf(appInfoArry.get(j).name),
								String.valueOf(mSections.charAt(i))))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}
	}
}
