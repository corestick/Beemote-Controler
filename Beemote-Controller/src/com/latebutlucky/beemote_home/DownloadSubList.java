package com.latebutlucky.beemote_home;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.latebutlucky.beemote_controller.R;

public class DownloadSubList extends Activity implements OnItemClickListener {

	private ListView listview;
	DataAdapter adapter;
	ArrayList<String> alist;
	ListData listdata;
	ArrayList<String[][]> subList;
	DownDialog homeDialog;


	// private int[] mImageID = {
	private int[] mImagePath = {R.drawable.tmp1,R.drawable.tmp2,R.drawable.tmp3
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sublist);
		homeDialog = new DownDialog(this);
		listview = (ListView) findViewById(R.id.sub_listview);
		listdata = new ListData();

		subList = new ArrayList<String[][]>();
		subList.add(listdata.sports);
		subList.add(listdata.economy);
		subList.add(listdata.fun);

		alist = new ArrayList<String>();
		adapter = new DataAdapter(this, alist);
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		
		String getText = getIntent().getStringExtra(Beemote_uploadpage.EXIST_NAME);

		int groupPosition = Integer.parseInt(String.valueOf(getText.charAt(0)));
		int childPosition =Integer.parseInt(String.valueOf(getText.charAt(2)));
		String[][] category = subList.get(groupPosition);
		for(int i=0; i <category[childPosition].length; i++){
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
		homeDialog.setImg(mImagePath[position]);
		homeDialog.show();
	}
}
