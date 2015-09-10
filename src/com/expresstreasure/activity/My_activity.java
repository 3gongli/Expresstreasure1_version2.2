package com.expresstreasure.activity;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.expresstreasure.R;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.HttptoolGet;
import com.expresstreasure.tils.Urllist;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class My_activity extends Activity {
	JSONObject obj;
	TextView name, phone, start, realname;
	Button btnstart, btnstop;
	TextView work;
	TextView about_us;
	private Throwable throwable = new Throwable();
	int lat = 0;// 纬度
	int lng = 0;// 经度
	public static final String INTENT_ACTION = "com.expresstreasure.activity.LocationService";
	public static final String ACTION_SHOW_START_INFO = "action_show_start_info";
	public static final String ACTION_OPEN_GPS = "action_open_GPS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.activity_my);
		try {
			init();
			OnClick();
			registerBoradcastReceiver();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 友盟上传错误日志
			MobclickAgent.reportError(getApplicationContext(), e);
			// 云测
			TestinAgent.uploadException(this, e.toString(), throwable);

		}
	}

	void init() {
		realname = (TextView) findViewById(R.id.realname);
		realname.setText(DataManger.instance.getRealname());
		name = (TextView) findViewById(R.id.name);
		name.setText(DataManger.instance.getName());
		phone = (TextView) findViewById(R.id.phone);
		phone.setText(DataManger.instance.getPhone());
		start = (TextView) findViewById(R.id.start);
		start.setText(DataManger.instance.getStart());
		btnstart = (Button) findViewById(R.id.start_work);
		btnstop = (Button) findViewById(R.id.stop_work);
		work = (TextView) findViewById(R.id.state);

		about_us = (TextView) findViewById(R.id.about_us);
		about_us.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(My_activity.this,
						AboutUs_activity.class);
				startActivity(intent);
			}
		});

		Log.i("My_activity", "工作中" + DataManger.instance.getIswork());
		if (DataManger.instance.getIswork()) {
			work.setText("工作状态:工作中");
			btnstart.setBackgroundColor(getResources().getColor(
					R.color.btn_bottom_textbgcolor));
			btnstop.setBackgroundColor(getResources().getColor(
					R.color.btn_bottom_bgcolor));
		} else {
			work.setText("工作状态:收工");
			btnstop.setBackgroundColor(getResources().getColor(
					R.color.btn_bottom_textbgcolor));
			btnstart.setBackgroundColor(getResources().getColor(
					R.color.btn_bottom_bgcolor));
		}
		findViewById(R.id.exit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(My_activity.this, Login_activity.class));
				finish();
			}
		});

	}

	void OnClick() {

		findViewById(R.id.start_work).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (LocationService.GPSIsOPen(My_activity.this)) {
					// 开工
					// 访问网络监察
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (!DataManger.instance.getIswork()) {
								Intent intent = new Intent();
								intent.setAction(My_activity.INTENT_ACTION);
								intent.setPackage(getPackageName());
								startService(intent);
								// startService(new Intent(
								// My_activity.INTENT_ACTION));
							} else {
								Message msg = new Message();
								msg.what = 3;// 已经开工
								handler.sendMessage(msg);
							}
						}

					}).start();
				} else {
					Toast.makeText(My_activity.this, "无法获取地理信息，请检查是否开启GPS！！",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException ex) {
						intent.setAction(Settings.ACTION_SETTINGS);
						try {
							startActivity(intent);
						} catch (Exception e) {
						}
					}
				}
			}
		});

		findViewById(R.id.stop_work).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 收工
				// 访问网络监察
				new Thread(new Runnable() {
					@Override
					public void run() {
						HttptoolGet http = new HttptoolGet();
						try {
							if (DataManger.instance.getIswork()) {
								String strurl = Urllist.work_status
										+ "?cid="
										+ DataManger.instance.getCid()
										+ "&t=2"
										+ "&eid="
										+ DataManger.instance
												.getEnterprise_id();
								obj = new JSONObject(http.httpget(strurl));
								Log.e("obj", obj.toString());
								String success = obj.getString("success");
								if (success.equals("true")) {
									// 收工
									DataManger.instance.setIswork(false);
								}
							} else {
								Message msg = new Message();
								msg.what = 4;
								handler.sendMessage(msg);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ConnectTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message msg = new Message();
						msg.what = 2;
						handler.sendMessage(msg);
					}

				}).start();

			}
		});

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// case 1:
			// if (DataManger.instance.getIswork()) {
			// work.setText("工作状态:工作中");
			// btnstart.setBackgroundColor(getResources().getColor(R.color.btn_bottom_textbgcolor));
			// btnstop.setBackgroundColor(getResources().getColor(R.color.btn_bottom_bgcolor));
			// } else {
			// Toast.makeText(My_activity.this, "开工失败请及时联系客服！",
			// Toast.LENGTH_LONG).show();
			// }
			//
			// break;
			case 2:
				if (!DataManger.instance.getIswork()) {
					Intent intent = new Intent();
					intent.setAction(My_activity.INTENT_ACTION);
					intent.setPackage(getPackageName());
					stopService(intent);
					btnstop.setBackgroundColor(getResources().getColor(
							R.color.btn_bottom_textbgcolor));
					btnstart.setBackgroundColor(getResources().getColor(
							R.color.btn_bottom_bgcolor));
					work.setText("工作状态:收工");
				} else {
					Toast.makeText(My_activity.this, "收工失败请及时联系客服！",
							Toast.LENGTH_LONG).show();
				}
				break;
			case 3:
				Toast.makeText(My_activity.this, "您已经开工了！", Toast.LENGTH_LONG)
						.show();
				break;
			case 4:
				Toast.makeText(My_activity.this, "您已经收工了！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		}
	};

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_SHOW_START_INFO)) {
				if (DataManger.instance.getIswork()) {
					work.setText("工作状态:工作中");
					btnstart.setBackgroundColor(getResources().getColor(
							R.color.btn_bottom_textbgcolor));
					btnstop.setBackgroundColor(getResources().getColor(
							R.color.btn_bottom_bgcolor));
				} else {
					Toast.makeText(My_activity.this, "开工失败请及时联系客服！",
							Toast.LENGTH_LONG).show();
				}
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_SHOW_START_INFO);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
