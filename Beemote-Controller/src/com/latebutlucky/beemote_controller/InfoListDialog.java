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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.latebutlucky.beemote_view.BeeButton;
import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClientManager;

public class InfoListDialog extends Dialog {

	IndexableListView listview;
	App_Adapter App_Adapter;
	Channel_Adapter channel_Adapter;
	ArrayList<TvAppInfo> appInfoArry;
	ArrayList<TvChannelListInfo> ChannelInfoArry;
	BeeButton mBeebutton;
	Dialog mDialog;
	BeemoteMain bMain;

	String mType; // 채널 or Tv앱 구분

	public InfoListDialog(Context context, String Type, BeeButton beebutton) {
		super(context);

		bMain = (BeemoteMain) context;
		mType = Type;

		// setTheme(R.style.Theme_dialog); //테두리 없애기위해
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_dialog);
		// getWindow().setBackgroundDrawable(
		// new ColorDrawable(Color.TRANSPARENT));
		// getWindow().setBackgroundDrawable(
		// getResources().getDrawable(R.drawable.gridback));
		mDialog = this;
		listview = (IndexableListView) findViewById(R.id.applist_listview);

		A2AClient mA2AClient = A2AClientManager.getDefaultClient();
		if (mType.equals("TvApp")) {
			appInfoArry = new ArrayList<TvAppInfo>();
			appInfoArry = mA2AClient.TvAppList;
			App_Adapter = new App_Adapter();
			listview.setAdapter(App_Adapter);
			Comparator<TvAppInfo> myComparator = new Comparator<TvAppInfo>() {
				Collator app_Collator = Collator.getInstance();

				@Override
				public int compare(TvAppInfo a, TvAppInfo b) {
					return app_Collator.compare(a.name, b.name);
				}
			};
			Collections.sort(appInfoArry, myComparator);
		} else if (mType.equals("TvChannel")) {
			ChannelInfoArry = new ArrayList<TvChannelListInfo>();
			ChannelInfoArry = mA2AClient.TvChannelList;
			channel_Adapter = new Channel_Adapter();
			listview.setAdapter(channel_Adapter);
//			Comparator<TvChannelListInfo> myComparator = new Comparator<TvChannelListInfo>() {
//				Collator app_Collator = Collator.getInstance();
//
//				@Override
//				public int compare(TvChannelListInfo a, TvChannelListInfo b) {
//					return app_Collator.compare(a.chname, b.chname);
//				}
//			};
//			Collections.sort(ChannelInfoArry, myComparator);
		}
		mBeebutton = beebutton;		
		
		listview.setFastScrollEnabled(true);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentView, View view,

			int position, long id) {			
				// Log.e("RRRR", appInfoArry.get(position).appIcon);
				// mBeebutton.setTextE(appInfoArry.get(position).name);
				if (mType.equals("TvApp")) {
					mBeebutton.itemInfo.appName = appInfoArry.get(position).name;
					mBeebutton.itemInfo.appId = appInfoArry.get(position).auid;
					mBeebutton.itemInfo.contentId = appInfoArry.get(position).cpid;
					mBeebutton.setIcon(appInfoArry.get(position).appIcon);
					bMain.updateInfo(BGlobal.BEEBUTTON_TYPE_APP);
				}
				else if (mType.equals("TvChannel")) {
					mBeebutton.itemInfo.channelNo = Integer.valueOf(ChannelInfoArry.get(position).Major);
					bMain.updateInfo(BGlobal.BEEBUTTON_TYPE_CH);					
				}
				mDialog.dismiss();
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
			name = (TextView) convertView.findViewById(R.id.tv_name);

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
	
	
	public class Channel_Adapter extends BaseAdapter implements SectionIndexer {

		private String mSections = "#ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎABCDEFGHIJKLMNOPQRSTUVWXYZ";

		TextView name;
		TextView major;
		TextView minor;
		LayoutInflater inflater;

		public Channel_Adapter() {
			inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ChannelInfoArry.size();
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
				convertView = inflater.inflate(R.layout.tvlist_layout,
						parent, false);
			}
			
			name = (TextView) convertView.findViewById(R.id.tv_name);
			major = (TextView) convertView.findViewById(R.id.tv_major);
			minor = (TextView) convertView.findViewById(R.id.tv_minor);

			Log.e("ChannelName", ChannelInfoArry.get(position).chname);
			name.setText(ChannelInfoArry.get(position).chname);
			major.setText(ChannelInfoArry.get(position).Major);
			minor.setText(ChannelInfoArry.get(position).Minor);
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
									String.valueOf(ChannelInfoArry.get(j).chname),
									String.valueOf(k)))
								return j;
						}
					} else {
						if (StringMatcher.matchInitial(
								String.valueOf(ChannelInfoArry.get(j).chname),
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
