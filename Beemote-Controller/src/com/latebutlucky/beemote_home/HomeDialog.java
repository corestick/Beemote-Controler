package com.latebutlucky.beemote_home;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.latebutlucky.beemote_controller.BeemoteDB;
import com.latebutlucky.beemote_controller.BeemoteMain;
import com.latebutlucky.beemote_controller.R;

public class HomeDialog extends Dialog implements android.view.View.OnClickListener {

	BeemoteDB beeDB;
	Button donwBtn ;
	Button cancleBtn ;
	ImageView mainImg ;
	public HomeDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		beeDB = new BeemoteDB(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_dialog);
		
		mainImg = (ImageView) findViewById(R.id.home_img);
		donwBtn = (Button) findViewById(R.id.home_btn_down);
		cancleBtn  = (Button) findViewById(R.id.home_btn_cancel);	
		
		donwBtn.setOnClickListener(this);
		cancleBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == donwBtn){	
			BeemoteMain.DownLoad = true;
			beeDB.write_DB(2);
			donwBtn.setText("ok");
		}
		else if(v == cancleBtn){
			dismiss();
		}
	}
	void setImg(int img){
		mainImg.setImageResource(img);
	}
}
