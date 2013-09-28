package com.latebutlucky.beemote_home;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.latebutlucky.beemote_controller.R;

public class ExpAdapter extends BaseExpandableListAdapter {

	/** * strings for group elements */
	public static final String arrGroupelements[] = { "sports", "economy", "fun"};
	/** * strings for child elements */
	public static final String arrChildelements[][] = {
			{ "야구", "축구","농구","골프" },
			{ "sub1", "sub2","sub3" },
			{ "sub1", "sub2","sub3" }};
		
	private Context myContext;

	public ExpAdapter(Context context) {
		myContext = context;
	}
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.child_row, null);
		}
		TextView tvPlayerName = (TextView) convertView
				.findViewById(R.id.tvPlayerName);
		
		tvPlayerName.setText(arrChildelements[groupPosition][childPosition]);
		//Spanned data = Html.fromHtml("<font color=red>" + arrChildelements[groupPosition][childPosition]+"</font>");
		//tvPlayerName.setText(data);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return arrChildelements[groupPosition].length;
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		return arrGroupelements.length; 
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.group_row, null);
		}
		TextView tvGroupName = (TextView) convertView
				.findViewById(R.id.tvGroupName);
		tvGroupName.setText(arrGroupelements[groupPosition]);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
