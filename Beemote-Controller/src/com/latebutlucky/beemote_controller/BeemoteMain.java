package com.latebutlucky.beemote_controller;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.latebutlucky.beemote_view.BeeButton;
import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;

public class BeemoteMain extends Activity implements OnClickListener {

	public SlidingView slidingView;
	BeemoteDB beemoteDB;
	Vector<ItemInfo> beeInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		beemoteDB = new BeemoteDB(this);
		
		slidingView = new SlidingView(this);
		
		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));
		slidingView.addView(new BeeView(this));
		
		for(int i = 0; i < slidingView.getChildCount(); i++) {
			BeeView bView = (BeeView) slidingView.getChildAt(i);
			bView.initBeeView(this, i);
		}
		
		// DB정보 불러오기
		beeInfo = beemoteDB.select();
		
		// DB정보 적용
		for(int i = 0; i < beeInfo.size(); i++) {
			ItemInfo info = beeInfo.get(i);
			
			if(info.screenIdx > -1 && info.screenIdx < slidingView.getChildCount()) {
				
				BeeView bView = (BeeView) slidingView.getChildAt(info.screenIdx);
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

					bButton.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_APP;
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
		}
	}
}
