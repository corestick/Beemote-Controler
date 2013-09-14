package com.latebutlucky.beemote_view;

import java.util.HashMap;
import com.latebutlucky.beemote_controller.R;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ItemView extends RelativeLayout {

//	private int mVisibleState = INVISIBLE;

	ImageView itemImage;
	TextView itemText;

	public ItemView(Context context) {
		super(context);
		create(context);
		// TODO Auto-generated constructor stub
	}

	public ItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
		// TODO Auto-generated constructor stub
	}
	
	private void create(Context context) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context.getSystemService(service);
		li.inflate(R.layout.itemview, this, true);
		
		itemImage = (ImageView)findViewById(R.id.item_image);
		itemText = (TextView)findViewById(R.id.item_text);
	}	
	public void setImage(int img){
		itemImage.setImageResource(img);
	}

	public void setText(String msg) {
		itemText.setText(msg);
	}

	public void setVisible() {
//		if(this.mVisibleState == INVISIBLE){
//			this.setVisibility(View.VISIBLE);
//			this.mVisibleState = VISIBLE;
//		}
//		else{
//			this.setVisibility(INVISIBLE);
//			this.mVisibleState = INVISIBLE;
//		}
		this.setVisibility(VISIBLE);
	}
}
