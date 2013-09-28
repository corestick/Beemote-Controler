package com.latebutlucky.beemote_home;

import java.io.File;

import com.latebutlucky.beemote_controller.R;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	Uri uri;
	private String sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath(); // sdcard 경로	

	// private int[] mImageID = {
	private String[] mImagePath = {
			sdcard + "/Beemote/screen0.jpg",
			sdcard + "/Beemote/screen1.jpg", 
			sdcard + "/Beemote/screen2.jpg"};

	public ImageAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return mImagePath.length;
		// return mImageID.length;
	}

	@Override
	public Object getItem(int position) {
		return mImagePath[position];
		// return mImageID[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);		
		Uri uri = Uri.fromFile(new File(mImagePath[position]));		
		imageView.setImageURI(uri);

		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		imageView.setLayoutParams(new Gallery.LayoutParams(300, 450));		

		return imageView;
	}
}