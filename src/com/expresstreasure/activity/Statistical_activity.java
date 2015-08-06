package com.expresstreasure.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.expresstreasure.R;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.Jsontool;
import com.expresstreasure.tils.Urllist;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class Statistical_activity extends Activity {
	TextView total_shipper_fees, total_buyer_fees, total_pay_fees,
			total_waybill, average_time, textTile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.statistical_activity);
		init();
		update();
	}

	private void init() {
		total_shipper_fees = (TextView) findViewById(R.id.total_shipper_fees);
		total_buyer_fees = (TextView) findViewById(R.id.total_buyer_fees);
		total_pay_fees = (TextView) findViewById(R.id.total_pay_fees);
		total_waybill = (TextView) findViewById(R.id.total_waybill);
		average_time = (TextView) findViewById(R.id.average_time);
		textTile = (TextView) findViewById(R.id.textTile);
		textTile.setText("今日配送情况");
	}

	//
	public void update() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Httptool http = new Httptool();
					Jsontool js = new Jsontool();
					JSONObject obj;
					try {
						String cid = DataManger.instance.getCid();
						String type = "2";// o2o为2，电商为1

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("cid", cid));
						params.add(new BasicNameValuePair("t", type));
						obj = new JSONObject(http.httppost(
								Urllist.delivery_count, params));
						if (obj.getString("success").equals("true")
								&& obj.getString("errors").equals("OK")) {
							js.all_count(obj);
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
					msg.what = 1;
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
			case 1:
				total_waybill.setText(DataManger.instance.getTotal_waybill());
				total_shipper_fees.setText(DataManger.instance
						.getTotal_shipper_fees());
				total_buyer_fees.setText(DataManger.instance
						.getTotal_buyer_fees());
				total_pay_fees.setText(DataManger.instance.getTotal_pay_fees());
				average_time.setText(DataManger.instance.getAverage_time());
				break;
			case 2:
				Toast.makeText(Statistical_activity.this, "获取每日统计失败！",
						Toast.LENGTH_LONG).show();
				break;

			}
		}
	};

	@Override
	public void onResume() {

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
