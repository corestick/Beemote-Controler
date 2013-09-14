package com.latebutlucky.beemote_controller;

import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MAvatarMenu extends LinearLayout implements OnClickListener {

	private int mVisibleState = INVISIBLE;

	ImageView imgCall;
	ImageView imgSMS;

	EditText edtMsg;

	public MAvatarMenu(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MAvatarMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void initMAvatarMenu() {
		setVisible(mVisibleState);


		imgCall = (ImageView) findViewById(R.id.imgCall);
		imgSMS = (ImageView) findViewById(R.id.imgSMS);

		imgCall.setOnClickListener(this);
		imgSMS.setOnClickListener(this);
	}

	public int getVisible() {
		return mVisibleState;
	}

	public void setVisible(int mVisible) {
		mVisibleState = mVisible;
		this.setVisibility(mVisibleState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.equals(imgCall)) {
		}
	}
}
