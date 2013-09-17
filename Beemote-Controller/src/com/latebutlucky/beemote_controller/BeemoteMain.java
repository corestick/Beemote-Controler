package com.latebutlucky.beemote_controller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;
import com.lge.tv.a2a.client.A2AMessageListener;

public class BeemoteMain extends Activity implements A2AMessageListener {

	int m_recvMsgType = 0;
	String m_recvMessage = null;
	Handler m_handler = new Handler();

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

	@Override
	public void onRecieveMessage(int type, String message) {
		// TODO Auto-generated method stub
		m_recvMsgType = type;
		m_recvMessage = message;
		Log.e("In", "In");
		m_handler.post(new Runnable() {
			@Override
			public void run() {
				// if (m_recMsgView != null) {
				// m_recMsgView.setText(m_recvMsgType + "," + m_recvMessage);
				// if (m_recvMsgType == 100 && m_recvMessage.compareTo("READY")
				// == 0) {
				// }
				// }
			}
		});
	}

}
