package com.latebutlucky.beemote_view;

import java.io.IOException;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;

public class BeeView extends GLSurfaceView {
	Display display;
	float width;
	float height;

	private MyRenderer renderer;

	public BeeView(Context context) {
		super(context);
		renderer = new MyRenderer();
		WindowManager wm = (WindowManager) context
				.getSystemService(context.WINDOW_SERVICE);
		display = wm.getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		setRenderer(renderer);
	}

	public boolean onTouchEvent(final MotionEvent event) {
		queueEvent(new Runnable() {
			public void run() {
				float x = event.getX();
				float y = event.getY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					int index = selectedButton(x, y);
					if (index == 19)
						break;
					else {
						renderer.hexagon[index].setColor(index / 10f, 0.0f,
								0.0f, 0.0f);
						renderer.setColor(index / 10f, index / 10f, 0.0f);
					}
				}
			}
		});

		return true;
	}

	public int selectedButton(float x, float y) {

		int btn = 0;
		float migrationX = x - (width / 2); // �������� ������ �̵�
		float migrationY = (y - (height / 2)) * (-1); // �������� ������ �̵�

		float small = 1000000;
		float rateCircleX = 0;
		float rateCircleY = 0;
		for (int i = 0; i < renderer.pointSet.size(); i++) {
			float tmp;
			rateCircleX = (renderer.pointSet.get(i).x) * (width / 2) / 2.8f;
			rateCircleY = (renderer.pointSet.get(i).y) * (height / 2) / 4.2f;
			tmp = (rateCircleX - migrationX) * (rateCircleX - migrationX)
					+ (rateCircleY - migrationY) * (rateCircleY - migrationY);
			if (small > tmp) {
				small = tmp;
				btn = i;
			}
		}
		if (btn >= 0 && btn < 19) {
			return btn;
		} else
			return 19;
	}

}
