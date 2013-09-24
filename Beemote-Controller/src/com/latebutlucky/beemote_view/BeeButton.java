package com.latebutlucky.beemote_view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.latebutlucky.beemote_controller.BGlobal;

public class BeeButton extends Button implements OnClickListener {

	private final float TEXT_SIZE = 14;
	private final float TEXT_LENGTH = 40;
	private int currentType;

	public BeeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		currentType = BGlobal.BEEBUTTON_TYPE_NONE;

		this.setMaxLines(1);
		this.setTextSize(TEXT_SIZE);
		this.setTextColor(Color.WHITE);
	}

	public void setIcon(Drawable dr) {

		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
//		dr.setBounds(0, 0, 40, 40);
		
		super.setCompoundDrawables(null, dr, null, null);
	}

	public void setType(int arg) {
		currentType = arg;

		switch (currentType) {
		case BGlobal.BEEBUTTON_TYPE_NONE:
			break;
		case BGlobal.BEEBUTTON_TYPE_APP:

			break;
		case BGlobal.BEEBUTTON_TYPE_FUNC:
			break;
		case BGlobal.BEEBUTTON_TYPE_SEARCH:
			break;
		}
	}

	public void setTextE(String text) {
		// TODO Auto-generated method stub

		String str = TextUtils.ellipsize(text, new TextPaint(), TEXT_LENGTH,
				TruncateAt.END).toString();

		super.setText(str);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}
}
