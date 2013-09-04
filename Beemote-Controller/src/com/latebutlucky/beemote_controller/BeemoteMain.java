package com.latebutlucky.beemote_controller;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;
import com.lge.tv.a2a.client.A2AClientManager;

public class BeemoteMain extends Activity {

	public BeeView[] bv = new BeeView[3];
	Button btnTvList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainview);
		
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.activity_beemote, null);
		addContentView(v, new RelativeLayout.LayoutParams(480, 800));
		
		SlidingView sv = new SlidingView(this);
		for (int i = 0; i < bv.length; i++) {
			bv[i] = new BeeView(this);
			sv.addView(bv[i]);
		}	
		
		FrameLayout framelayout = (FrameLayout) findViewById(R.id.main_view);
		framelayout.addView(sv);
		
		btnTvList = (Button) findViewById(R.id.btnTVList);
		btnTvList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						TVList.class);
				startActivity(intent);
			}
		});

		Button btnTouchPad = (Button) findViewById(R.id.btnTouchPad);
		btnTouchPad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),
						TouchPad.class);
				startActivity(intent);
			}
		});

		Button btnUp = (Button) findViewById(R.id.btnup);
		btnUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					A2AClientManager.getDefaultClient().handleKey();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

}
