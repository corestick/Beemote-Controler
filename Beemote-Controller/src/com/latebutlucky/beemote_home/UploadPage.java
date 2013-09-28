package com.latebutlucky.beemote_home;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Gallery;

import com.latebutlucky.beemote_controller.R;

public class UploadPage extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_page);
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery1);
		gallery.setAdapter(new ImageAdapter(this));
		gallery.setSelection(1);
	}
	
}
