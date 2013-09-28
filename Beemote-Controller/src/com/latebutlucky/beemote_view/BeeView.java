package com.latebutlucky.beemote_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.latebutlucky.beemote_controller.BGlobal;
import com.latebutlucky.beemote_controller.BeemoteMain;
import com.latebutlucky.beemote_controller.ItemInfo;
import com.latebutlucky.beemote_controller.R;

public class BeeView extends RelativeLayout {
	public ButtonMenu btnMenu;

	public BeeButton[] btnBee = new BeeButton[19];

	ImageButton btnChUp;
	ImageButton btnChDown;
	ImageButton btnVolUp;
	ImageButton btnVolDown;

	int btn_R[] = { R.id.bee_btn1, R.id.bee_btn2, R.id.bee_btn3, R.id.bee_btn4,
			R.id.bee_btn5, R.id.bee_btn6, R.id.bee_btn7, R.id.bee_btn8,
			R.id.bee_btn9, R.id.bee_btn10, R.id.bee_btn11, R.id.bee_btn12,
			R.id.bee_btn13, R.id.bee_btn14, R.id.bee_btn15, R.id.bee_btn16,
			R.id.bee_btn17, R.id.bee_btn18, R.id.bee_btn19 };

	public BeeView(Context context) {
		super(context);
	}

	public BeeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initBeeView(BeemoteMain bMain, int idx) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) bMain.getSystemService(service);
		li.inflate(R.layout.beemote_main, this, true);

		btnMenu = new ButtonMenu(bMain);
		addView(btnMenu);

		for (int i = 0; i < btnBee.length; i++) {
			btnBee[i] = (BeeButton) findViewById(btn_R[i]);
			btnBee[i].setOnClickListener(bMain);
			btnBee[i].setOnLongClickListener(bMain);
			ItemInfo info = new ItemInfo();

			info.screenIdx = idx;
			info.beemoteIdx = i;
			info.beemoteType = BGlobal.BEEBUTTON_TYPE_NONE;
			btnBee[i].setItemInfo(info);
		}

		btnChUp = (ImageButton) findViewById(R.id.ch_up);
		btnChDown = (ImageButton) findViewById(R.id.ch_down);
		btnVolUp = (ImageButton) findViewById(R.id.vol_up);
		btnVolDown = (ImageButton) findViewById(R.id.vol_down);
		btnChUp.setOnClickListener(bMain);
		btnChDown.setOnClickListener(bMain);
		btnVolUp.setOnClickListener(bMain);
		btnVolDown.setOnClickListener(bMain);
	}

	public void refreshBeemoteState(BeeButton beeButton) {
		// TODO Auto-generated method stub

		ItemInfo info = beeButton.itemInfo;

		switch (info.beemoteType) {
		case BGlobal.BEEBUTTON_TYPE_NONE:
			beeButton.setBackgroundResource(R.drawable.btn_pink_hexagon);
			beeButton.setIcon();
			beeButton.setTextE();
			break;
		case BGlobal.BEEBUTTON_TYPE_APP:

			beeButton.setBackgroundResource(R.drawable.btn_pink_hexagon);
			// Log.e("INFO", info.appName);
			beeButton.setTextE(info.appName);
			beeButton.setIcon(getResources().getDrawable(R.drawable.app));
			break;
		case BGlobal.BEEBUTTON_TYPE_CH:
			beeButton.setTextE("" + info.channelNo);
			beeButton.setBackgroundResource(R.drawable.hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.ch));
			break;
			
		case BGlobal.BEEBUTTON_TYPE_SEARCH:
			beeButton.setTextE(beeButton.itemInfo.keyWord);
			beeButton.setBackgroundResource(R.drawable.btn_pink_hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.serach));
			break;
		case BGlobal.BEEBUTTON_TYPE_FUNC:
			beeButton.setBackgroundResource(R.drawable.btn_sky_hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.func));
			break;
		default:

			beeButton.setBackgroundResource(R.drawable.hexagon);

			beeButton.setIcon();
			beeButton.setTextE();
			break;
		}
	}
}
