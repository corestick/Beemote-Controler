package com.latebutlucky.beemote_view;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
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
	private final float TEXT_LENGTH = 38;
	private final int IMAGE_SIZE_X = 50;
	private final int IMAGE_SIZE_Y = 50;

	public ItemInfo itemInfo;

	public BeeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		this.setMaxLines(1);
		this.setTextSize(TEXT_SIZE);
		this.setTextColor(Color.WHITE);
		this.setTextE();
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
	
	public byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, outputStream);
		return outputStream.toByteArray();
	}
	
	public Bitmap byteArrayToBitmap(byte[] $byteArray) {
		Bitmap bitmap = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length);
		return bitmap;
	}
	
	public void setTextE(){
		super.setText(null);
	}

	public void setTextE(String text) {
		// TODO Auto-generated method stub

		String str = TextUtils.ellipsize(text, new TextPaint(), TEXT_LENGTH,
				TruncateAt.END).toString();

		super.setText(str);
	}

	public void initButton() {
		this.itemInfo.beemoteType = BGlobal.BEEBUTTON_TYPE_NONE;
		this.itemInfo.channelNo = null;
		this.itemInfo.appId = null;
		this.itemInfo.appName = null;
		this.itemInfo.contentId = null;
		this.itemInfo.keyWord = null;
		this.itemInfo.functionKey = null;
	}
}
