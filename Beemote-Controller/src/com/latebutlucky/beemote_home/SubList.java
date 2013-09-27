package com.latebutlucky.beemote_home;

import java.io.File;
import java.util.ArrayList;

import com.latebutlucky.beemote_controller.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SubList extends Activity implements OnItemClickListener {

	private ListView listview;
	DataAdapter adapter;
	ArrayList<String> alist;
	ListData listdata;
	ArrayList<String[][]> subList;
	HomeDialog homeDialog;
	private String sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath(); // sdcard 경로	

	// private int[] mImageID = {
	private String[] mImagePath = {
			sdcard + "/Beemote/screen0.jpg",
			sdcard + "/Beemote/screen1.jpg", 
			sdcard + "/Beemote/screen2.jpg"
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sublist);
		homeDialog = new HomeDialog(this);
		listview = (ListView) findViewById(R.id.sub_listview);
		listdata = new ListData();

		subList = new ArrayList<String[][]>();
		subList.add(listdata.sports);
		subList.add(listdata.eonomy);
		subList.add(listdata.life);
		subList.add(listdata.culture);
		subList.add(listdata.fun);

		alist = new ArrayList<String>();
		adapter = new DataAdapter(this, alist);
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		
		String getText = getIntent().getStringExtra(Beemote_home.EXIST_NAME);

		int groupPosition = Integer.parseInt(String.valueOf(getText.charAt(0)));
		int childPosition =Integer.parseInt(String.valueOf(getText.charAt(2)));
		String[][] category = subList.get(groupPosition);
		for(int i=0; i <3; i++){
			adapter.add(category[childPosition][i]);
		}
		Log.e("gettext",getText);
		Log.e("group", ""+groupPosition);
		Log.e("child", ""+childPosition);
	}

	private class DataAdapter extends ArrayAdapter<String> {
		private LayoutInflater mInflater;

		public DataAdapter(Context context, ArrayList<String> object) {
			super(context, 0, object);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		 @Override
		 public View getView(int position, View v, ViewGroup parent) {
		 View view = null;
		 if (v == null) {
		 view = mInflater.inflate(R.layout.custom_list, null);
		 } else {
		 view = v;
		 }
		 final String str = this.getItem(position);
		
		 if (str != null) {
		 TextView m_text = (TextView) view.findViewById(R.id.custom_list_text);
		 m_text.setText(str);
		 }
		 return view;
		 }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Uri uri = Uri.fromFile(new File(mImagePath[position]));
		Bitmap bmp = BitmapFactory.decodeFile(uri.getPath());
		homeDialog.setImg(bmp);
		homeDialog.show();
	}
}
