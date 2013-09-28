package com.latebutlucky.beemote_home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.latebutlucky.beemote_controller.R;


public class Beemote_uploadpage extends Activity {
	
	public static final String EXIST_NAME = "exit_name";

	DisplayMetrics metrics;
	int width;
	ExpandableListView expList;
	private static final int SPORTS = 1;
	private static final int ECONOMY = 2;
	private static final int LIFE = 3;
	private static final int CULTURE = 4;
	private static final int FUN = 5;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beemote_home);
        
        expList = (ExpandableListView) findViewById(R.id.list);

        
        metrics = new DisplayMetrics(); 
        getWindowManager().getDefaultDisplay().getMetrics(metrics);         
        width = metrics.widthPixels;
        
        expList.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(10));
        expList.setAdapter(new ExpAdapter(this));         
        expList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				Log.e("onGroupExpand", "OK");
			}
		});

		expList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				Log.e("onGroupCollapse", "OK");
			}
		});

		expList.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Log.e("OnChildClickListener", Integer.toString(childPosition));
				
				Intent i = new Intent(Beemote_uploadpage.this, DownloadSubList.class);
				String tmp = groupPosition + "-" + childPosition;
				i.putExtra(EXIST_NAME, tmp);
				startActivityForResult(i, 0);
				
		        Toast awesomeToast = Toast.makeText(Beemote_uploadpage.this, "hello", Toast.LENGTH_SHORT);		
		        awesomeToast.show();
				return false;
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.beemote, menu);
        return true;
    }    

    public int GetDipsFromPixel(float pixels)     
	{      // Get the screen's density scale      
		final float scale = getResources().getDisplayMetrics().density;      
		// Convert the dps to pixels, based on density scale      
		return (int) (pixels * scale + 0.5f);     
	} 
}
