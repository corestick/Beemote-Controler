package com.latebutlucky.beemote_controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.latebutlucky.beemote_home.Beemote_downloadpage;
import com.latebutlucky.beemote_home.Beemote_uploadpage;
import com.latebutlucky.beemote_view.BeeButton;
import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;
import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClientManager;
import com.lge.tv.a2a.client.A2AMessageListener;

public class BeemoteMain extends Activity implements OnClickListener,
		OnLongClickListener, A2AMessageListener {

	public SlidingView slidingView;
	BeemoteDB beemoteDB;
	Vector<ItemInfo> beeInfo;

	LayoutInflater mInflater;
	EditText edtMsg;
	public A2AClient mA2AClient = null;
	Handler m_handler = new Handler();
	String AppAction = null;
	String AppDetail = null;

	// BackPressCloseHandler backPressCloseHandler; // back버튼 두번누를때 종료
	public String TextState;
	private static final int UPLOADPAGE = 0;
	private static final int DOWNPAGE = 1;

	@Override
	protected void onStart() {
		Log.e("RESTART", "RESTART");
		setDB();
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		beemoteDB = new BeemoteDB(this);

		slidingView = new SlidingView(this);

		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));

		mA2AClient = A2AClientManager.getDefaultClient();
		mA2AClient.setMessageListener(this);

		System.setProperty("http.keepAlive", "false");

		// backPressCloseHandler = new BackPressCloseHandler(this);
		for (int i = 0; i < slidingView.getChildCount(); i++) {
			BeeView bView = (BeeView) slidingView.getChildAt(i);
			bView.initBeeView(this, i);
		}

		setContentView(slidingView);
		setDB();
	}

	public void setDB() {
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

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, UPLOADPAGE, 0, "Upload").setIcon(R.drawable.arrow_up);
		menu.add(0, DOWNPAGE, 0, "Download").setIcon(R.drawable.arrow_down);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UPLOADPAGE:
			startUploadpage();
			break;
		case DOWNPAGE:
			startDownpage();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.bee_btn10) {
			Intent intent = new Intent(BeemoteMain.this, TVList.class);
			intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
			BeemoteMain.this.startActivity(intent);
			return;
		} else {
			if (mA2AClient.getCurrentTV() == null) {
				Toast.makeText(BeemoteMain.this, "먼저 TV와 연결하세요.",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		final BeeButton bButton;
		// bee버튼
		if (v instanceof BeeButton) {
			
			bButton = (BeeButton) v;
			BeeView bView = (BeeView) slidingView.getChildAt(slidingView
					.getCurrentPage());
			
			Log.e("RRR",  "==>" + bButton.itemInfo.beemoteIdx);

			try {
				switch (bButton.itemInfo.beemoteType) {
				case BGlobal.BEEBUTTON_TYPE_NONE:
					bView.btnMenu.showButtonMenu(bButton);
					break;
				case BGlobal.BEEBUTTON_TYPE_APP:
					if (bButton.itemInfo.appId.length() > 0) {
						// if (mA2AClient.app_Errstate.action != null
						// && mA2AClient.app_Errstate.action.equals("Execute"))
						// {
						// mA2AClient.TvAppTerminate(bButton.itemInfo.appId,
						// bButton.itemInfo.appName);
						// return;
						//
						// } else {
						Log.e("Exe", "exeApp");
						mA2AClient.TvAppExe(bButton.itemInfo.appId,
								bButton.itemInfo.appName,
								bButton.itemInfo.contentId);
						// }
					}
					break;
				case BGlobal.BEEBUTTON_TYPE_CH:
					if (bButton.itemInfo.channelNo.length() > 1) {
						final Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								for (int i = 0; i < bButton.itemInfo.channelNo
										.length(); i++) {
									int ChannelNum = Integer.parseInt(String
											.valueOf(bButton.itemInfo.channelNo
													.charAt(i)));
									ChannelNum = ChannelNum + 2;
									Log.e("ChannelNUM", ChannelNum + "");
									try {
										mA2AClient.KeyCodeSend(String
												.valueOf(ChannelNum));
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}, 500);
					} else {
						int ChannelNum = Integer.parseInt(String
								.valueOf(bButton.itemInfo.channelNo.charAt(0)));
						ChannelNum = ChannelNum + 2;
						Log.e("ChannelNUM", ChannelNum + "");
						mA2AClient.KeyCodeSend(String.valueOf(ChannelNum));
					}
					break;
				case BGlobal.BEEBUTTON_TYPE_FUNC:
					mA2AClient.KeyCodeSend(bButton.itemInfo.functionKey);
					break;
				case BGlobal.BEEBUTTON_TYPE_SEARCH:
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (v instanceof Button) {
				BeeView bView = (BeeView) slidingView.getChildAt(slidingView
						.getCurrentPage());

				bButton = bView.btnMenu.getBeeButton();

				// 메뉴 기능
				switch (v.getId()) {
				case R.id.selmenu_btn1:
					refreshTVAppList();
					InfoListDialog("TvApp", bButton);
					break;
				case R.id.selmenu_btn2:
					try {
						mA2AClient.TvChannelList.clear();
						mA2AClient.tvListQuery();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					InfoListDialog("TvChannel", bButton);
					break;
				case R.id.selmenu_btn3:
					showMsgDialog("Keyword");
					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_SEARCH;
					break;
				case R.id.selmenu_btn4:
					FunctionDialog funcDial = new FunctionDialog(
							BeemoteMain.this, bButton);
					funcDial.setCancelable(true);
					android.view.WindowManager.LayoutParams params = funcDial
							.getWindow().getAttributes();
					params.width = LayoutParams.FILL_PARENT;
					params.height = LayoutParams.FILL_PARENT;
					funcDial.getWindow().setAttributes(params);
					funcDial.show();
					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_FUNC;
					break;
				default:
					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_NONE;
					break;
				}
			} else if (v instanceof ImageButton) {
				try {
					switch (v.getId()) {
					case R.id.ch_up:
						mA2AClient.KeyCodeSend(BGlobal.KEYCODE_CHANNEL_UP);
						break;
					case R.id.ch_down:
						mA2AClient.KeyCodeSend(BGlobal.KEYCODE_CHANNEL_DOWN);
						break;
					case R.id.vol_up:
						mA2AClient.KeyCodeSend(BGlobal.KEYCODE_VOLUME_UP);
						break;
					case R.id.vol_down:
						mA2AClient.KeyCodeSend(BGlobal.KEYCODE_VOLUME_DOWN);
						break;
					case R.id.bee_keyboard:

						break;
					case R.id.bee_mouse:
						Intent intent = new Intent(BeemoteMain.this,
								TouchPad.class);
						intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
						getApplicationContext().startActivity(intent);

						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void refreshTVAppList() {
		// TODO Auto-generated method stub
		try {
			mA2AClient.TvAppList.clear();

			mA2AClient.tvAppQuery();
			// Bitmap bitmap;

			// for (int i = 0; i < mA2AClient.TvAppList.size(); i++) {
			// bitmap = mA2AClient.tvAppIconQuery(
			// mA2AClient.TvAppList.get(i).auid,
			// URLEncoder.encode(mA2AClient.TvAppList.get(i).name));
			// mA2AClient.TvAppList.get(i).appIcon = bitmap;
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public void showMsgDialog(final String msg) {
		m_handler.post(new Runnable() {
			@Override
			public void run() {
				// final Dialog dial = new Dialog(mBeemote,
				// R.style.Theme_dialog);
				final Dialog dial = new Dialog(BeemoteMain.this,
						R.style.Theme_dialog);
				View dial_view = getLayoutInflater().inflate(
						R.layout.input_text, null);
				dial.setContentView(dial_view);

				TextView txtTitle = (TextView) dial_view
						.findViewById(R.id.txtTitle);

				final EditText t = (EditText) dial_view
						.findViewById(R.id.textinput_edit);
				Button b1 = (Button) dial_view.findViewById(R.id.custom_btnOK);
				Button b2 = (Button) dial_view
						.findViewById(R.id.custom_btncancle);

				if (msg.equals("KeyboardVisible")) {
					txtTitle.setText("텍스트 입력 후, 전송 버튼을 누르세요.");
					b1.setText("전송");
					b2.setText("완료");
				} else if (msg.equals("Keyword")) {
					txtTitle.setText("검색어를 입력하세요.");
					b1.setText("확인");
					b2.setText("취소");
				}

				t.setFocusable(true);
				t.requestFocus();

				b1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View click_v) {
						try {
							if (msg.equals("KeyboardVisible")) {
								mA2AClient.keywordSend("Editing", t.getText()
										.toString());
							} else if (msg.equals("Keyword")) {
								BeeView bView = (BeeView) slidingView
										.getChildAt(slidingView
												.getCurrentPage());
								BeeButton bButton = bView.btnMenu
										.getBeeButton();
								bButton.itemInfo.keyWord = t.getText()
										.toString();
								// bButton.setIcon();
								BeemoteMain.this
										.updateInfo(BGlobal.BEEBUTTON_TYPE_SEARCH);
								dial.dismiss();
							}
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
						try {
							// if (TextState.equals("TextEdited"))
							mA2AClient.keywordSend("EditEnd", t.getText()
									.toString());

							dial.dismiss();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
				dial.show();
			}
		});

		//
		// if (msg.length() > 0) {
		// InputMethodManager imm = (InputMethodManager) mBeemote
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);

	}

	public void InfoListDialog(String Type, BeeButton mBeebutton) {

		InfoListDialog infoDialog = null;
		if (Type.equals("TvApp")) {
			infoDialog = new InfoListDialog(BeemoteMain.this, "TvApp",
					mBeebutton);
		} else if (Type.equals("TvChannel")) {
			infoDialog = new InfoListDialog(BeemoteMain.this, "TvChannel",
					mBeebutton);
		}
		infoDialog.setCancelable(true);
		android.view.WindowManager.LayoutParams params = infoDialog.getWindow()
				.getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		params.height = LayoutParams.FILL_PARENT;
		infoDialog.getWindow().setAttributes(params);
		infoDialog.show();
	}

	@Override
	public void onRecieveMessage(String nameType, Object object) {

		if (nameType.equals("KeyboardVisible")) {
			KeyboardInfo keyboardInfo = (KeyboardInfo) object;
			if (keyboardInfo.name.equals("KeyboardVisible")) {
				TextState = "KeyboardVisible";
				if (keyboardInfo.value.equals("true")) {
					showMsgDialog("KeyboardVisible");
				}
			} else if (keyboardInfo.name.equals("TextEdited")) {
				TextState = "TextEdited";
			}
		} else if (nameType.equals("AppErrstate")) {
			App_Errstate appErrstate = (App_Errstate) object;
			AppAction = appErrstate.action;
			AppDetail = appErrstate.detail;
			Log.e("AppAction", AppAction);
			Log.e("AppDetail", AppDetail);
		}

	}

	@Override
	public boolean onLongClick(View v) {
		BeeButton bButton;
		if (v instanceof BeeButton) {
			Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibe.vibrate(100);
			bButton = (BeeButton) v;
			beemoteDB.delete(bButton.itemInfo);
			bButton.initButton();
			BeeView bView = (BeeView) slidingView.getChildAt(slidingView
					.getCurrentPage());
			bView.refreshBeemoteState(bButton);
		}
		return false;
	}

	// @Override
	// public void onBackPressed() {
	// this.finish();
	// backPressCloseHandler.onBackPressed();
	//
	// }

	public class BackPressCloseHandler {

		private long backKeyPressedTime = 0;
		private Toast toast;
		private Activity activity;

		public BackPressCloseHandler(Activity context) {
			this.activity = context;
		}

		public void onBackPressed() {
			if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
				if (AppAction == null) {
					backKeyPressedTime = System.currentTimeMillis();
					showGuide();
					return;
				} else if (AppAction.equals("Execute")) {
					try {
						mA2AClient.KeyCodeSend(BGlobal.KEYCODE_REDO);
						AppAction = null;
						AppDetail = null;
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
				activity.finish();
				toast.cancel();
			}
		}

		private void showGuide() {
			toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
					Toast.LENGTH_SHORT);
			toast.show();

		}

	}

	public void startDownpage() {
		Bitmap captureView[] = null;
		Intent intent = new Intent(this, Beemote_downloadpage.class);
		this.startActivity(intent);
	}

	public void startUploadpage() {
		Bitmap captureView[] = null;
		int count = slidingView.getChildCount();

		if (captureView == null || captureView.length != count)
			captureView = new Bitmap[count];

		String sdcard = Environment.getExternalStorageDirectory()
				.getAbsolutePath();

		File cfile = new File(sdcard + "/Beemote");
		cfile.mkdirs(); // 폴더가 없을 경우 ScreenShotTest 폴더생성
		for (int i = 0; i < count; i++) {
			View tempCapture = slidingView.getChildAt(i);
			tempCapture.buildDrawingCache();
			captureView[i] = tempCapture.getDrawingCache();

			String path = sdcard + "/Beemote/screen" + i + ".jpg";
			try {
				FileOutputStream fos = new FileOutputStream(path);
				captureView[i].compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Intent intent = new Intent(this, Beemote_uploadpage.class);
		intent.putExtra("ChildCount", this.Child_Count());
		this.startActivity(intent);
	}

	public int Child_Count() {
		return slidingView.getChildCount();
	}

}
