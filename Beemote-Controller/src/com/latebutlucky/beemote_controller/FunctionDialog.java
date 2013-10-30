package com.latebutlucky.beemote_controller;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.latebutlucky.beemote_view.BeeButton;

public class FunctionDialog extends Dialog {

	String[] function = new String[] { "홈", "음소거", "녹화", "방송목록" };
	Context mContext;
	BeeButton mBeebutton;

	public FunctionDialog(Context context, BeeButton beebutton) {
		super(context);
		mContext = context;
		mBeebutton = beebutton;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.functionlist);
		ListView listView = (ListView) findViewById(R.id.list_func);
		listView.setAdapter(new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, function));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if (position == 0) {
					mBeebutton.itemInfo.functionKey = BGlobal.KEYCODE_HOME;
					mBeebutton.setTextE("홈");				
				} else if (position == 1) {
					mBeebutton.itemInfo.functionKey = BGlobal.KEYCODE_MUTE;
					mBeebutton.setTextE("음소거");
				} else if (position == 2) {					
					
				} else if (position == 3) {
				
					
				}
				((BeemoteMain) mContext).updateInfo(BGlobal.BEEBUTTON_TYPE_FUNC);
				FunctionDialog.this.dismiss();
			}
		});
	}
}
