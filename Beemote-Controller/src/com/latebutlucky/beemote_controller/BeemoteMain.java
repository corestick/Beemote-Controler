package com.latebutlucky.beemote_controller;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;
import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClientManager;
import com.lge.tv.a2a.client.A2AMessageListener;

public class BeemoteMain extends Activity implements A2AMessageListener {

	LayoutInflater mInflater;
	static BeemoteMain mBeemote;
	EditText edtMsg;
	A2AClient mA2AClient = null;
	Handler m_handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		mInflater = getLayoutInflater();
		mBeemote = this;
		SlidingView slidingView = new SlidingView(this);
		BeeView v1 = new BeeView(this);
		BeeView v2 = new BeeView(this);
		BeeView v3 = new BeeView(this);

		slidingView.addView(v1);
		slidingView.addView(v2);
		slidingView.addView(v3);
		setContentView(slidingView);

		mA2AClient = A2AClientManager.getDefaultClient();
		mA2AClient.setMessageListener(this);
	}

	public void showMsgDialog() {
		m_handler.post(new Runnable() {
			@Override
			public void run() {
				Log.e("dia", "dia");
				// final Dialog dial = new Dialog(mBeemote,
				// R.style.Theme_dialog);
				final Dialog dial = new Dialog(mBeemote);
				View dial_view = getLayoutInflater().inflate(
						R.layout.input_text, null);
				dial.setContentView(dial_view);
				EditText t = (EditText) dial_view
						.findViewById(R.id.textinput_edit);
				Button b1 = (Button) dial_view.findViewById(R.id.custom_btnOK);
				Button b2 = (Button) dial_view
						.findViewById(R.id.custom_btncancle);
				t.setFocusable(true);
				b1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View click_v) {
						dial.dismiss();
					}
				});
				b2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dial.dismiss();
					}
				});
				dial.show();
			}
		});
		Log.e("dialog", "dialog");

		//
		// if (msg.length() > 0) {
		// InputMethodManager imm = (InputMethodManager) mBeemote
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

	@Override
	public void onRecieveMessage(KeyboardInfo keyboardInfo) {

		if (keyboardInfo.name.equals("KeyboardVisible")) {
			Log.e("In3", keyboardInfo.name);
			Log.e("In3", keyboardInfo.value);
			Log.e("In3", keyboardInfo.mode);
			showMsgDialog();
		} else if (keyboardInfo.name.equals("TextEdited")) {
			Log.e("In3", keyboardInfo.state);
			Log.e("In3", keyboardInfo.value);
		}
	}
}
