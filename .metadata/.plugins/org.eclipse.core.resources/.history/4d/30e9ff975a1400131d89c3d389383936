package com.latebutlucky.beemote_controller;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClient.A2ACmdError;
import com.lge.tv.a2a.client.A2AClientManager;
import com.lge.tv.a2a.client.A2AEventListener;
import com.lge.tv.a2a.client.A2ATVInfo;

public class TVList extends Activity {

	ListView mTVListView = null;
	ArrayAdapter<A2ATVInfo> mTVListAdapter = null;
	A2AClient mA2AClient = null;
	Handler mHandler = new Handler();

	private ProgressDialog mProgressDialog = null;
	
	EditText edtPairingCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvlist);

		mA2AClient = A2AClientManager.getDefaultClient();
		Log.e("A2A", mA2AClient.toString());

		mTVListView = (ListView) findViewById(R.id.tvList);
		mTVListAdapter = new ArrayAdapter<A2ATVInfo>(TVList.this,
				android.R.layout.simple_list_item_single_choice);
		mTVListView.setAdapter(mTVListAdapter);
		mTVListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mTVListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Object item = parent.getItemAtPosition(position);
						if (item != null) {
							Log.e("CSnopy", item + "");
							if (item != null && item instanceof A2ATVInfo) {
								mA2AClient.setCurrentTV((A2ATVInfo) item);
								showPairingDialog();
								
							}
						}
					}
				});

		Button btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mProgressDialog != null)
					return;
				mProgressDialog = new ProgressDialog(TVList.this);
				mProgressDialog.setCancelable(false);
				mProgressDialog.setMessage("Search...");

				mA2AClient.setEventListener(new A2AEventListener() {
					List<A2ATVInfo> m_a2atvInfos = null;

					@Override
					public void onSearchEvent(List<A2ATVInfo> infos,
							boolean isEnd) {
						if (!isEnd) {
							this.m_a2atvInfos = infos;
							for (A2ATVInfo info : m_a2atvInfos) {
								Log.e("info", info.toString());
								mTVListAdapter.add(info);
								
								
								//자동 페어링
								if (info != null && info instanceof A2ATVInfo) {
									mA2AClient.setCurrentTV((A2ATVInfo) info);
									
									A2ACmdError ret = null;
									try {
										ret = A2AClientManager.getDefaultClient().connect("591855");
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Log.e("RRR", "페어링 결과 : " + ret);
									TVList.this.finish();
								}
							}

							mTVListView.requestLayout();
						} else {
							if (mProgressDialog != null) {
								mProgressDialog.dismiss();
								mProgressDialog = null;
							}

							if (mTVListAdapter.getCount() == 0) {
								Toast.makeText(TVList.this, "TV not found",
										Toast.LENGTH_SHORT).show();
							}
						}
					}

					@Override
					public void onRecieveEvent(String message) {
						Log.e("CSnopy", message);
					}
				});

				mTVListAdapter.clear();
				mTVListView.requestLayout();

				//페어링 관련
//				mProgressDialog.show();
				
				boolean result = mA2AClient.searchTV(TVList.this);
				if (!result) {
					mProgressDialog.dismiss();
					mProgressDialog = null;

					Toast.makeText(TVList.this, "fail to start !",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
	
	private void showPairingDialog() {
		// TODO Auto-generated method stub
		
		// 페어링 코드 보기
		try {
			A2AClientManager.getDefaultClient().showPasscode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		final LinearLayout linear = (LinearLayout) View.inflate(TVList.this, R.layout.dialog_pairing, null);
		edtPairingCode = (EditText) linear.findViewById(R.id.edtPairingCode);
		
		new AlertDialog.Builder(TVList.this)
		.setTitle(R.string.input_pairing)
		.setView(linear)
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					A2ACmdError ret = A2AClientManager.getDefaultClient().connect(edtPairingCode.getText().toString());
					Log.e("RRR", "페어링 결과 : " + ret);
					TVList.this.finish();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tvlist, menu);
		return true;
	}

}
