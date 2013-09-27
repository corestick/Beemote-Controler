package com.latebutlucky.beemote_controller;

import java.util.Vector;

import com.latebutlucky.beemote_view.BeeView;
import com.latebutlucky.beemote_view.SlidingView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Beemote extends Activity {

	Vector<ItemInfo> itemList;
	BeemoteDB beemoteDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		// Button btnSelect = (Button) findViewById(R.id.btnSelect);
		// btnSelect.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// itemList = beemoteDB.select();
		// }
		// });

		// Button btnInsert = (Button) findViewById(R.id.btnInsert);
		// btnInsert.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// ItemInfo info = new ItemInfo();
		// info.screenIdx = 3;
		// info.beemoteIdx = 3;
		// info.appId = 44;
		// info.appName = "�����Ľm12";
		// info.contentId = "12";
		//
		// beemoteDB.insert(info);
		// }
		// });
		//
		// Button btnUpdate = (Button) findViewById(R.id.btnUpdate);
		// btnUpdate.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// ItemInfo info = new ItemInfo();
		// info.screenIdx = 3;
		// info.beemoteIdx = 3;
		// info.appId = 55;
		// info.appName = "�����Ľm12333";
		// info.contentId = "12333";
		//
		// beemoteDB.update(info);
		// }
		// });
		//
		// Button btnDelete = (Button) findViewById(R.id.btnDelete);
		// btnDelete.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// for(int i = 0; i < itemList.size(); i++)
		// {
		// if(itemList.get(i).screenIdx == 2 && itemList.get(i).beemoteIdx == 2)
		// {
		// beemoteDB.delete(itemList.get(i));
		// break;
		// }
		// }
		// }
		// });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beemote, menu);
		return true;
	}

}
