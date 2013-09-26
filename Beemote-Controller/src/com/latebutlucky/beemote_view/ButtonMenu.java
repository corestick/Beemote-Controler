package com.latebutlucky.beemote_view;

import java.io.IOException;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.latebutlucky.beemote_controller.R;
import com.latebutlucky.beemote_controller.TvAppListDialog;
import com.lge.tv.a2a.client.A2AClient;
import com.lge.tv.a2a.client.A2AClientManager;

public class ButtonMenu extends RelativeLayout implements OnClickListener {
	private int mVisibleState = INVISIBLE;
	public Button btn1;
	public Button btn2;
	public Button btn3;
	public Button btn4;

	A2AClient mA2AClient = null;

	public ButtonMenu(Context context) {
		super(context);
		create(context);
	}

	public ButtonMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		create(context);
	}

	private void create(Context context) {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) context.getSystemService(service);
		li.inflate(R.layout.menu, this, true);

		btn1 = (Button) findViewById(R.id.selmenu_btn1);
		btn2 = (Button) findViewById(R.id.selmenu_btn2);
		btn3 = (Button) findViewById(R.id.selmenu_btn3);
		btn4 = (Button) findViewById(R.id.selmenu_btn4);

		mA2AClient = A2AClientManager.getDefaultClient();

		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				try {
//					mA2AClient.TvAppexe();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});

		btn1.setOnClickListener(new View.OnClickListener() {
			Bitmap bitmap;

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				try {
					mA2AClient.tvAppQuery();
					for (int i = 0; i < mA2AClient.TvAppList.size(); i++) {
						bitmap = mA2AClient.tvAppIconQuery(mA2AClient.TvAppList
								.get(i).auid, URLEncoder
								.encode(mA2AClient.TvAppList.get(i).name));
						mA2AClient.TvAppList.get(i).appIcon = bitmap;
					}

					TvAppListDialog appDialog = new TvAppListDialog(getContext(),
							mA2AClient.TvAppList);
					appDialog.setCancelable(true);
					android.view.WindowManager.LayoutParams params = appDialog
							.getWindow().getAttributes();
					params.width = LayoutParams.FILL_PARENT;
					params.height = LayoutParams.FILL_PARENT;
					appDialog.getWindow().setAttributes(params);
					appDialog.show();

					// bitmap = mA2AClient.TvAppList.get(0).appIcon = bitmap;
					// Bitmap bit = Bitmap.createBitmap(bitmap);
					//
					// Drawable drawable = new BitmapDrawable(bit);
					// btn3.setBackgroundDrawable(drawable);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		
		btn2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 try {
					mA2AClient.tvListQuery();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	public void setVisibleState() {
		if (this.mVisibleState == INVISIBLE) {
			this.setVisibility(View.VISIBLE);
			this.mVisibleState = VISIBLE;
		} else {
			this.setVisibility(View.GONE);
			this.mVisibleState = INVISIBLE;
		}
	}
}
