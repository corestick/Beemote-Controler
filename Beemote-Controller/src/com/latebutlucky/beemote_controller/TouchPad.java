package com.latebutlucky.beemote_controller;

import java.io.IOException;

import com.lge.tv.a2a.client.A2AClientManager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class TouchPad extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_pad);
		
		TextView tv = (TextView) findViewById(R.id.txtTEST);
		tv.setText(A2AClientManager.getDefaultClient().getCurrentTV().getTvName() + " : " + A2AClientManager.getDefaultClient().getCurrentTV().getIpAddress());
		
		try {
			A2AClientManager.getDefaultClient().executeApp(Long.parseLong("11"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.touch_pad, menu);
		return true;
	}

}
