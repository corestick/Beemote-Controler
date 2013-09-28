package com.latebutlucky.beemote_home;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Gallery;
import android.widget.ImageView;

import com.latebutlucky.beemote_controller.R;

public class UploadPage extends Activity{

	private String sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath(); // sdcard 경로	

	// private int[] mImageID = {
	private String[] mImagePath = {
			sdcard + "/Beemote/screen0.jpg",
			sdcard + "/Beemote/screen1.jpg", 
			sdcard + "/Beemote/screen2.jpg"};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_page);
				
		ImageView imgScreen1 = (ImageView) findViewById(R.id.up_screen1);
		ImageView imgScreen2 = (ImageView) findViewById(R.id.up_screen2);
		ImageView imgScreen3 = (ImageView) findViewById(R.id.up_screen3);
		
		imgScreen1.setImageURI(Uri.fromFile(new File(mImagePath[0])));
		imgScreen2.setImageURI(Uri.fromFile(new File(mImagePath[1])));
		imgScreen3.setImageURI(Uri.fromFile(new File(mImagePath[2])));
	}
	
}
