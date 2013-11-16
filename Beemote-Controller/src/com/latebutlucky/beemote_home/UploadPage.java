package com.latebutlucky.beemote_home;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.latebutlucky.beemote_controller.BeemoteDB;
import com.latebutlucky.beemote_controller.R;

public class UploadPage extends Activity implements
		android.view.View.OnClickListener , OnCheckedChangeListener{

	private String sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath(); // sdcard 경로
	BeemoteDB beeDB;
	RadioGroup radioGroup;
	RadioButton rb0;
	RadioButton rb1;
	RadioButton rb2;

	// private int[] mImageID = {
	private String[] mImagePath = { sdcard + "/Beemote/screen0.jpg",
			sdcard + "/Beemote/screen1.jpg", sdcard + "/Beemote/screen2.jpg" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_page);
		
		Button upBtn = (Button) findViewById(R.id.upload);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		rb0 = (RadioButton) findViewById(R.id.radio0);
		rb1 = (RadioButton) findViewById(R.id.radio1);
		rb2 = (RadioButton) findViewById(R.id.radio2);
		radioGroup.setOnCheckedChangeListener(this);
		upBtn.setOnClickListener(this);
		
		beeDB = new BeemoteDB(this);

		ImageView imgScreen1 = (ImageView) findViewById(R.id.up_screen1);
		ImageView imgScreen2 = (ImageView) findViewById(R.id.up_screen2);
		ImageView imgScreen3 = (ImageView) findViewById(R.id.up_screen3);

		imgScreen1.setImageURI(Uri.fromFile(new File(mImagePath[0])));
		imgScreen2.setImageURI(Uri.fromFile(new File(mImagePath[1])));
		imgScreen3.setImageURI(Uri.fromFile(new File(mImagePath[2])));
	}

	@Override
	public void onClick(View arg0) {
		int screenNum;
        if (radioGroup.getCheckedRadioButtonId() == R.id.radio0) {
        	screenNum = 0;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radio1) {
        	screenNum = 1;
        } else {
        	screenNum = 2;
        }
        
		beeDB.get_DB(screenNum);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}

}
