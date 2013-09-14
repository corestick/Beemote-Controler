package com.latebutlucky.beemote_controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.Window;

import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;

public class BeemoteMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		
		SlidingView slidingView = new SlidingView(this);
		BeeView v1 = new BeeView(this);
		BeeView v2 = new BeeView(this);
		BeeView v3 = new BeeView(this);

		slidingView.addView(v1);
		slidingView.addView(v2);
		slidingView.addView(v3);
		setContentView(slidingView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

}
