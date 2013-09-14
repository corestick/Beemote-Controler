package com.latebutlucky.beemote_view;

import com.latebutlucky.beemote_controller.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class BeeView extends RelativeLayout implements View.OnClickListener {
	
	ShapeDrawable shapeRed;
	ShapeDrawable shapeGreen;
	Button[] btn = new Button[19];
	ButtonMenu btnMenu;

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
		create(context);
	}

	public BeeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	@SuppressWarnings("deprecation")
	private void create(Context context) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context.getSystemService(service);
		li.inflate(R.layout.bemote_main, this, true);

		btnMenu = new ButtonMenu(context);
		addView(btnMenu);
		btnMenu.setVisibility(View.GONE);

		Path path = new Path();
		drawHaxgon(path);

		shapeRed = new ShapeDrawable(new PathShape(path, 10, 10));
		shapeRed.getPaint().setColor(Color.RED);
		shapeGreen = new ShapeDrawable(new PathShape(path, 10, 10));
		shapeGreen.getPaint().setColor(Color.GREEN);

		for (int i = 0; i < btn.length; i++) {
			btn[i] = (Button) findViewById(btn_R[i]);
			btn[i].setBackgroundDrawable(shapeRed);
			btn[i].setOnClickListener(this);
		}
		btnChUp = (ImageButton) findViewById(R.id.ch_up);
		btnChDown = (ImageButton) findViewById(R.id.ch_down);
		btnVolUp = (ImageButton) findViewById(R.id.vol_up);
		btnVolDown = (ImageButton) findViewById(R.id.vol_down);
	}

	public void drawHaxgon(Path p) {
		p.moveTo(2.5f, 0f);
		p.lineTo(7.5f, 0f);
		p.lineTo(10f, 4.5f);
		p.lineTo(7.5f, 9f);
		p.lineTo(2.5f, 9f);
		p.lineTo(0, 4.5f);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int left = v.getLeft() - 70;
		int top = v.getTop() - 75;
		if(left <0)
			left =0;
		else if (left > 300)
			left = 300;
		btnMenu.setPadding(left, top, 0, 0);
		btnMenu.setVisibleState();

		v.setBackgroundDrawable(shapeGreen);
	}

}
