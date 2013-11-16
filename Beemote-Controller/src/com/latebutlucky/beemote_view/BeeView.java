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
	ImageButton btnMouse;
	ImageButton btnKeybroad;

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
		btnMouse = (ImageButton) findViewById(R.id.bee_mouse);
		btnKeybroad = (ImageButton) findViewById(R.id.bee_keyboard);

		btnChUp.setOnClickListener(bMain);
		btnChDown.setOnClickListener(bMain);
		btnVolUp.setOnClickListener(bMain);
		btnVolDown.setOnClickListener(bMain);
		btnMouse.setOnClickListener(bMain);
		btnKeybroad.setOnClickListener(bMain);
	}

	public void refreshBeemoteState(BeeButton beeButton) {
		// TODO Auto-generated method stub

		btnMenu.hideButtonMenu();

		ItemInfo info = beeButton.itemInfo;

		switch (info.beemoteType) {
		case BGlobal.BEEBUTTON_TYPE_NONE:
			beeButton.setIcon();
			beeButton.setTextE();

			switch (beeButton.itemInfo.beemoteIdx) {
			case 9:
				break;
			case 4:
			case 5:
			case 6:
			case 10:
			case 13:
			case 14:
				beeButton.setBackgroundResource(R.drawable.btn_in_hexagon);
				break;
			default:
				beeButton.setBackgroundResource(R.drawable.btn_out_hexagon);
				break;
			}
			break;
		case BGlobal.BEEBUTTON_TYPE_APP:
			beeButton.setTextE(info.appName);
			beeButton.setBackgroundResource(R.drawable.btn_sky_hexagon);
			beeButton.setIcon(beeButton.byteArrayToBitmap(info.appImg));
			break;
		case BGlobal.BEEBUTTON_TYPE_CH:
			beeButton.setTextE("" + info.channelNo);
			beeButton.setBackgroundResource(R.drawable.btn_pink_hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.ch));
			break;
		case BGlobal.BEEBUTTON_TYPE_SEARCH:
			beeButton.setTextE(beeButton.itemInfo.keyWord);
			beeButton.setBackgroundResource(R.drawable.btn_green_hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.serach));
			break;
		case BGlobal.BEEBUTTON_TYPE_FUNC:
			if (beeButton.itemInfo.functionKey.equals(BGlobal.KEYCODE_HOME)) {
				beeButton.setTextE(BGlobal.FUNC_HOME);
			} else if (beeButton.itemInfo.functionKey
					.equals(BGlobal.KEYCODE_MUTE))
				beeButton.setTextE(BGlobal.FUNC_MUTE);
			beeButton.setBackgroundResource(R.drawable.btn_yellow_hexagon);
			beeButton.setIcon(getResources().getDrawable(R.drawable.func));
			break;
		default:
			break;
		}
	}
}
