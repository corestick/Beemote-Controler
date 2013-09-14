package com.latebutlucky.beemote_view;

import com.latebutlucky.beemote_controller.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class ButtonMenu extends RelativeLayout implements OnClickListener {
	private int mVisibleState = INVISIBLE;
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public Button btn4;

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

		btn1 = (Button) findViewById(R.id.selmenu_btn1);
		btn2 = (Button) findViewById(R.id.selmenu_btn2);
		btn3 = (Button) findViewById(R.id.selmenu_btn3);
		btn4 = (Button) findViewById(R.id.selmenu_btn4);
	}

	@Override
	public void onClick(View v) {

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
}
