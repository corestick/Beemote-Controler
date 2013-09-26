package com.latebutlucky.beemote_view;

import android.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.Button;

import com.latebutlucky.beemote_controller.BGlobal;
import com.latebutlucky.beemote_controller.ItemInfo;

public class BeeButton extends Button {

	private final float TEXT_SIZE = 14;
	private final float TEXT_LENGTH = 40;
	private final int IMAGE_SIZE_X = 60;
	private final int IMAGE_SIZE_Y = 60;

	public ItemInfo itemInfo;

	public BeeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		this.setMaxLines(1);
		this.setTextSize(TEXT_SIZE);
		this.setTextColor(Color.WHITE);
	}

	public void setItemInfo(ItemInfo info) {
		this.itemInfo = info;
	}

	public void setIcon(Bitmap b) {

		Bitmap resize = Bitmap.createScaledBitmap(b, IMAGE_SIZE_X,
				IMAGE_SIZE_Y, true);

		Drawable dr = (BitmapDrawable) new BitmapDrawable(getResources(),
				resize);

		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

		super.setCompoundDrawables(null, dr, null, null);
	}

	public void setIcon(Drawable drawable) {

		BitmapDrawable bd = (BitmapDrawable) drawable;

		Bitmap resize = Bitmap.createScaledBitmap(bd.getBitmap(), IMAGE_SIZE_X,
				IMAGE_SIZE_Y, true);

		Drawable dr = (BitmapDrawable) new BitmapDrawable(getResources(),
				resize);

		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

		super.setCompoundDrawables(null, dr, null, null);
	}
	
	public void setIcon() {
		super.setCompoundDrawables(null, null, null, null);
	}

	public void setTextE(String text) {
		// TODO Auto-generated method stub

		String str = TextUtils.ellipsize(text, new TextPaint(), TEXT_LENGTH,
				TruncateAt.END).toString();

		super.setText(str);
	}
}
