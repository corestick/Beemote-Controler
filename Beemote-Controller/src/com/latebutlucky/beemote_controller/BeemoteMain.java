package com.latebutlucky.beemote_controller;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lge.tv.a2a.client.A2AClientManager;

public class BeemoteMain extends Activity {

	public BeeView[] bv = new BeeView[3];
	Button btnTvList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		SlidingView sv = new SlidingView(this);
		for (int i = 0; i < bv.length; i++) {
			bv[i] = new BeeView(this);
		}

		FrameLayout frm = new FrameLayout(this);
		RelativeLayout rlRelativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams lpRelativeLayout_Property = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		rlRelativeLayout.setLayoutParams(lpRelativeLayout_Property);

		btnTvList = new Button(this);
		RelativeLayout.LayoutParams btn_Property = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		btnTvList.setText("CH+");
		btn_Property.topMargin = 300;
		btn_Property.leftMargin = 100;
		btnTvList.setLayoutParams(btn_Property);
		rlRelativeLayout.addView(btnTvList);

		for (int i = 0; i < bv.length; i++) {
			sv.addView(bv[i]);
		}
		frm.addView(sv);
		frm.addView(rlRelativeLayout);
		setContentView(frm);

		btnTvList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						TVList.class);
				startActivity(intent);
			}
		});

//		Button btnTouchPad = (Button) findViewById(R.id.btnTouchPad);
//		btnTouchPad.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(getApplicationContext(),
//						TouchPad.class);
//				startActivity(intent);
//			}
//		});
//
//		Button btnUp = (Button) findViewById(R.id.btnup);
//		btnUp.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				try {
//					A2AClientManager.getDefaultClient().handleKey();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

}
