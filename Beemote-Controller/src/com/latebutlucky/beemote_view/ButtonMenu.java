package com.latebutlucky.beemote_view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.latebutlucky.beemote_controller.BeemoteMain;
import com.latebutlucky.beemote_controller.R;

public class ButtonMenu extends RelativeLayout {
	private int mVisibleState = INVISIBLE;
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public Button btn4;

	public BeeButton curBeeButton;

	public ButtonMenu(Context context) {
		super(context);
		create(context);
	}

	public ButtonMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	private void create(Context context) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context.getSystemService(service);
		li.inflate(R.layout.menu, this, true);

		setVisibility(View.GONE);

		btn1 = (Button) findViewById(R.id.selmenu_btn1);
		btn2 = (Button) findViewById(R.id.selmenu_btn2);
		btn3 = (Button) findViewById(R.id.selmenu_btn3);
		btn4 = (Button) findViewById(R.id.selmenu_btn4);

		BeemoteMain bMain = (BeemoteMain) context;

		btn1.setOnClickListener(bMain);
		btn2.setOnClickListener(bMain);
		btn3.setOnClickListener(bMain);
		btn4.setOnClickListener(bMain);
	}

	public void setVisibleState() {
		if (this.mVisibleState == INVISIBLE) {
			this.setVisibility(View.VISIBLE);
			this.mVisibleState = VISIBLE;
		} else {
			this.setVisibility(View.GONE);
			this.mVisibleState = INVISIBLE;
		}
	}

	public void setBeeButton(BeeButton v) {
		// TODO Auto-generated method stub
		this.curBeeButton = v;
	}

	public BeeButton getBeeButton() {
		// TODO Auto-generated method stub
		return this.curBeeButton;
	}
	
	public void showButtonMenu(BeeButton v) {
		this.curBeeButton = v;
		
		setPadding(v.getLeft() - 70, v.getTop() - 75, 0, 0);
		setVisibleState();
	}
	
	public void hideButtonMenu() {
		setVisibility(View.INVISIBLE);
	}
}
