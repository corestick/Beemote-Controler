package com.latebutlucky.beemote_controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.lge.tv.a2a.client.A2AClientDefault;
import com.lge.tv.a2a.client.A2AClientManager;
import com.lge.tv.a2a.client.A2ATVInfo;

public class TouchPad extends Activity {

	A2ATVInfo tvInfo;
	A2AClientDefault A2Aclient;
	float x, y;
	Button TouchClick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_touch_pad);
		A2Aclient = (A2AClientDefault) A2AClientManager.getDefaultClient();
		TouchClick = (Button) findViewById(R.id.btnTouchClick);
		TouchClick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					A2Aclient.HandleTouchClick();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// Log.e("RRR", "Down");
			try {
				A2Aclient.cursorVisible();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x = event.getX();
			y = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_MOVE:
			try {
				if ((event.getX() - x > 5) || (event.getY() - y > 5)) {
					A2Aclient.moveMouse((int) (event.getX() - x),
							(int) (event.getY() - y));
				}
				x = event.getX();
				y = event.getY();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return super.onTouchEvent(event);

	}
}
