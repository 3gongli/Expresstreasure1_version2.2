package com.expresstreasure.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.expresstreasure.R;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.Jsontool;
import com.expresstreasure.tils.Urllist;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class O2O_activity extends TabActivity implements
		OnCheckedChangeListener {
	public static final int MSG_GET_ES_COUNT = 216; // 电商-数据
	public static final int MSG_GET_O2O_COUNT = 217; // O2O-数据
	private TabHost mHost;
	private RadioGroup radioderGroup;
	ImageButton btn_scan;
	Context mContext = O2O_activity.this;
	TextView head_title, top_btn;
	public static final String ACTION_REFRESH_WAITSEND_REDIOBUTTON = "action_refresh_waitdend_rediobutton";
	public static final String ACTION_REFRESH_SEND_REDIOBUTTON = "action_refresh_sign_rediobutton";
	public static final String ACTION_REFRESH_SENDOVER_REDIOBUTTON = "action_refresh_sendover_rediobutton";
	private RadioButton radioButton0, radioButton1, radioButton2;
	private String state = "2"; // o2o
	public static O2O_activity o2oactiviy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toptabs_o2o);
		TestinAgent.setLocalDebug(true);
		head_title = (TextView) findViewById(R.id.textTile_oto);
		head_title.setText("我的订单");
		// 实例化TabHost
		mHost = this.getTabHost();

		// 添加选项卡
		mHost.addTab(mHost.newTabSpec("ONE").setIndicator("ONE")
				.setContent(new Intent(this, O2OWaitsend_activity.class)));
		mHost.addTab(mHost.newTabSpec("TWO").setIndicator("TWO")
				.setContent(new Intent(this, O2OSend_activity.class)));
		mHost.addTab(mHost.newTabSpec("THREE").setIndicator("THREE")
				.setContent(new Intent(this, O2OSendover_activity.class)));
		mHost.setCurrentTab(0);
		radioderGroup = (RadioGroup) findViewById(R.id.top_radio);
		radioderGroup.setOnCheckedChangeListener(this);
		radioButton0 = (RadioButton) findViewById(R.id.radio_button0);
		radioButton0.setChecked(true);
		radioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		radioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		registerBoradcastReceiver();
		mHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if (tabId.equals("ONE")) { // 第一个标签

				}
				if (tabId.equals("TWO")) { // 第二个标签

				}
				if (tabId.equals("THREE")) { // 第三个标签

				}
				update(state);
			}

		});

		findViewById(R.id.top_tj_btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// 点击右上角统计,显示返回
				startActivity(new Intent(O2O_activity.this,
						Statistical_activity.class));
			}
		});
		o2oactiviy = this;
	}

	@Override
	public void onResume() {
		// 返回列表刷新数据
		update(state);
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_button0:
			mHost.setCurrentTabByTag("ONE");
			break;
		case R.id.radio_button1:
			mHost.setCurrentTabByTag("TWO");
			break;
		case R.id.radio_button2:
			mHost.setCurrentTabByTag("THREE");
			break;

		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_REFRESH_WAITSEND_REDIOBUTTON)) {
				radioButton0.setText("待配送（" + O2OWaitsend_activity.TotalNum
						+ "）");

			} else if (action.equals(ACTION_REFRESH_SEND_REDIOBUTTON)) {
				radioButton1.setText("配送中（" + O2OSend_activity.TotalNum + "）");
			} else if (action.equals(ACTION_REFRESH_SENDOVER_REDIOBUTTON)) {
				radioButton2.setText("完成（" + O2OSendover_activity.TotalNum
						+ "）");
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_REFRESH_WAITSEND_REDIOBUTTON);
		myIntentFilter.addAction(ACTION_REFRESH_SEND_REDIOBUTTON);
		myIntentFilter.addAction(ACTION_REFRESH_SENDOVER_REDIOBUTTON);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	public void update(final String state) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Httptool http = new Httptool();
					Jsontool js = new Jsontool();
					JSONObject obj;
					try {
						String cid = DataManger.instance.getCid();
						String type = state;// o2o为2，电商为1
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("cid", cid));
						params.add(new BasicNameValuePair("t", type));
						obj = new JSONObject(http.httppost(Urllist.today_count,
								params));
						if (obj.getString("success").equals("true")
								&& obj.getString("errors").equals("OK")) {
							js.day_count(obj, state);
						} else {
							Message msg = new Message();
							msg.what = 2;
							handler.sendMessage(msg);
							return;
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					Message msg = new Message();
					if (state == "1") {
						msg.what = MSG_GET_ES_COUNT;
					} else {
						msg.what = MSG_GET_O2O_COUNT;
					}
					handler.sendMessage(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_GET_O2O_COUNT:
				radioButton0.setText("待配送（"
						+ DataManger.instance.getToday_o2o_take() + "）");
				radioButton1.setText("配送中（"
						+ DataManger.instance.getToday_o2o_seding() + "）");
				radioButton2.setText("完成（"
						+ DataManger.instance.getToday_o2o_over() + "）");
				break;
			default:
				break;

			}
		}
	};

}
