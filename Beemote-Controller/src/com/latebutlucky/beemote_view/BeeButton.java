package com.latebutlucky.beemote_view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class BeeButton extends ImageButton implements OnClickListener {

	String strBee = "";
	
	public BeeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		
	}
	
	public void setText(String str) {
		
		strBee = str;
		invalidate();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint paint = new Paint();
 		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);
 		paint.setTextSize(10);
		paint.setFakeBoldText(true);
		paint.setAntiAlias(true);
//		paint.setShadowLayer(5, 3, 3, Color.RED);
		
		strBee = TextUtils.ellipsize(strBee, new TextPaint(), this.getWidth() / 2, TruncateAt.END).toString();
		
		Rect b = new Rect();
		paint.getTextBounds(strBee, 0, strBee.length(), b);
		
		canvas.drawText(strBee, this.getWidth() / 2 - b.width() / 2, this.getHeight() / 2, paint);
	}
	
	
}
