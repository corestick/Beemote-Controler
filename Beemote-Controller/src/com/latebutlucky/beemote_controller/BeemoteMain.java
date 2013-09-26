package com.latebutlucky.beemote_controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.latebutlucky.beemote_view.BeeButton;
import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;
import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClientManager;
import com.lge.tv.a2a.client.A2AMessageListener;

public class BeemoteMain extends Activity implements OnClickListener,
		A2AMessageListener {

	public SlidingView slidingView;
	BeemoteDB beemoteDB;
	Vector<ItemInfo> beeInfo;

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
		mBeemote = this;
		beemoteDB = new BeemoteDB(this);

		slidingView = new SlidingView(this);

		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));

		mA2AClient = A2AClientManager.getDefaultClient();
		mA2AClient.setMessageListener(this);

		for (int i = 0; i < slidingView.getChildCount(); i++) {
			BeeView bView = (BeeView) slidingView.getChildAt(i);
			bView.initBeeView(this, i);
		}

		// DB정보 불러오기
		beeInfo = beemoteDB.select();

		// DB정보 적용
		for (int i = 0; i < beeInfo.size(); i++) {
			ItemInfo info = beeInfo.get(i);

			if (info.screenIdx > -1
					&& info.screenIdx < slidingView.getChildCount()) {

				BeeView bView = (BeeView) slidingView
						.getChildAt(info.screenIdx);
				bView.btnBee[info.beemoteIdx].itemInfo = info;
				bView.refreshBeemoteState(bView.btnBee[info.beemoteIdx]);
			}
		}

		setContentView(slidingView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub

		// bee버튼
		if (v instanceof BeeButton) {
			BeeButton bButton = (BeeButton) v;
			BeeView bView = (BeeView) slidingView.getChildAt(slidingView
					.getCurrentPage());
			bView.btnMenu.showButtonMenu(bButton);
			try {
				mA2AClient.TvAppExe(bButton.itemInfo.appId,bButton.itemInfo.appName , bButton.itemInfo.contentId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Toast.makeText(
					BeemoteMain.this,
					"스크린 : " + bButton.itemInfo.screenIdx + "\n 버튼 : "
							+ bButton.itemInfo.beemoteIdx + "\n 타입 : "
							+ bButton.itemInfo.beemoteType, Toast.LENGTH_SHORT)
					.show();
		} else {

			if (v instanceof Button) {
				BeeView bView = (BeeView) slidingView.getChildAt(slidingView
						.getCurrentPage());

				BeeButton bButton = bView.btnMenu.getBeeButton();

				// 메뉴 기능
				switch (v.getId()) {
				case R.id.selmenu_btn1:
					Toast.makeText(BeemoteMain.this, "앱 매칭", Toast.LENGTH_SHORT)
							.show();
					try {
						mA2AClient.tvAppQuery();
						Bitmap bitmap;
						for (int i = 0; i < mA2AClient.TvAppList.size(); i++) {
							bitmap = mA2AClient.tvAppIconQuery(
									mA2AClient.TvAppList.get(i).auid,
									URLEncoder.encode(mA2AClient.TvAppList
											.get(i).name));
							mA2AClient.TvAppList.get(i).appIcon = bitmap;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					TvAppListDialog appDialog = new TvAppListDialog(
							BeemoteMain.this, mA2AClient.TvAppList, bButton);
					appDialog.setCancelable(true);
					android.view.WindowManager.LayoutParams params = appDialog
							.getWindow().getAttributes();
					params.width = LayoutParams.FILL_PARENT;
					params.height = LayoutParams.FILL_PARENT;
					appDialog.getWindow().setAttributes(params);
					appDialog.show();			
					break;
				case R.id.selmenu_btn2:
					Toast.makeText(BeemoteMain.this, "채널", Toast.LENGTH_SHORT)
							.show();

					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_CH;
					bButton.itemInfo.channelNo = 10;
					break;
				case R.id.selmenu_btn3:
					Toast.makeText(BeemoteMain.this, "검색어", Toast.LENGTH_SHORT)
							.show();

					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_SEARCH;
					bButton.itemInfo.keyWord = "설리 최자";
					break;
				case R.id.selmenu_btn4:
					Toast.makeText(BeemoteMain.this, "기능키", Toast.LENGTH_SHORT)
							.show();

					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_FUNC;
					break;
				default:
					Toast.makeText(BeemoteMain.this, "지우기", Toast.LENGTH_SHORT)
							.show();

					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_NONE;
					break;
				}
			} else if (v instanceof ImageButton) {
				switch (v.getId()) {
				case R.id.ch_up:
					Intent intent = new Intent(getApplicationContext(),
							TVList.class);
					intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
					getApplicationContext().startActivity(intent);
					break;
				}
			}
		}
	}
	
	public void updateInfo(int beeType) {
		
		BeeView bView = (BeeView) slidingView.getChildAt(slidingView
				.getCurrentPage());

		BeeButton bButton = bView.btnMenu.getBeeButton();
		
		bButton.itemInfo.beemoteType = beeType;
		
		if (beeInfo.contains(bButton.itemInfo)) {
			int idx = beeInfo.indexOf(bButton.itemInfo);
			beeInfo.remove(idx);
			beeInfo.add(bButton.itemInfo);

			beemoteDB.update(bButton.itemInfo);
		} else {
			beeInfo.add(bButton.itemInfo);
			beemoteDB.insert(bButton.itemInfo);
		}

		bView.refreshBeemoteState(bButton);
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
				final EditText t = (EditText) dial_view
						.findViewById(R.id.textinput_edit);
				Button b1 = (Button) dial_view.findViewById(R.id.custom_btnOK);
				Button b2 = (Button) dial_view
						.findViewById(R.id.custom_btncancle);
				t.setFocusable(true);
				t.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						Log.e("RRR", String.valueOf(keyCode));
						// mA2AClient.keywordSend(String.valueOf(keyCode));

						return false;
					}
				});
				b1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View click_v) {
						try {
							mA2AClient.keywordSend(t.getText().toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// dial.dismiss();
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
