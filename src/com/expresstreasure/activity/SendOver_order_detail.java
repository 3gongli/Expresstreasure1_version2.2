package com.expresstreasure.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.expresstreasure.R;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.SharePrefUtil;
import com.expresstreasure.tils.Urllist;

public class SendOver_order_detail extends Activity {
	private TextView textView_order_id, textView_shipper_name,
			textView_shipper_address, textView_shipper_phone,
			textView_goods_type, textView_shipper_remarks;
	private String shipper_name, shipper_address, shipper_phone, goods_type,
			shipper_remarks;
	private String id;
	private int position;

	private ImageView imageView_back_login;

	private EditText editText_goods_address, editText_goods_name,
			editText_goods_phone;
	private String fail_message;

	private Button button_confirm;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_order_detail);
		Bundle bundle = getIntent().getExtras();
		position = bundle.getInt("position");
		id = bundle.getString("id");
		shipper_name = bundle.getString("shipper_name");
		shipper_address = bundle.getString("shipper_address");
		shipper_phone = bundle.getString("shipper_phone");
		goods_type = bundle.getString("goods_type");
		shipper_remarks = bundle.getString("remarks");

		initView();
	};

	private void initView() {
		button_confirm = (Button) findViewById(R.id.button_confirm);
		imageView_back_login = (ImageView) findViewById(R.id.imageView_back_login);
		textView_order_id = (TextView) findViewById(R.id.textView_order_id);
		textView_shipper_name = (TextView) findViewById(R.id.textView_shipper_name);
		textView_shipper_address = (TextView) findViewById(R.id.textView_shipper_address);
		textView_shipper_phone = (TextView) findViewById(R.id.textView_shipper_phone);
		textView_goods_type = (TextView) findViewById(R.id.textView_goods_type);
		textView_shipper_remarks = (TextView) findViewById(R.id.textView_shipper_remarks);

		editText_goods_address = (EditText) findViewById(R.id.editText_goods_address);
		editText_goods_name = (EditText) findViewById(R.id.editText_goods_name);
		editText_goods_phone = (EditText) findViewById(R.id.editText_goods_phone);

		textView_order_id.setText(id);
		textView_shipper_name.setText(shipper_name);
		textView_shipper_address.setText(shipper_address);
		textView_shipper_phone.setText(shipper_phone);
		textView_goods_type.setText(goods_type);
		textView_shipper_remarks.setText(shipper_remarks);
		button_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(run).start();
			}
		});

		imageView_back_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	Runnable run = new Runnable() {

		@Override
		public void run() {

			// TODO Auto-generated method stub

			Httptool httptool = new Httptool();
			SharePrefUtil sp = new SharePrefUtil();
			String cid = sp.getString(SendOver_order_detail.this, "cid", null);
			String sp_x = sp
					.getString(SendOver_order_detail.this, "sp_x", null);
			String sp_y = sp
					.getString(SendOver_order_detail.this, "sp_y", null);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("address", editText_goods_address
					.getText().toString()));
			params.add(new BasicNameValuePair("name", editText_goods_name
					.getText().toString()));
			params.add(new BasicNameValuePair("phone", editText_goods_phone
					.getText().toString()));
			params.add(new BasicNameValuePair("wbid", textView_order_id
					.getText().toString()));
			params.add(new BasicNameValuePair("cid", cid));
			params.add(new BasicNameValuePair("sp_x", sp_x));
			params.add(new BasicNameValuePair("sp_y", sp_y));
			try {
				JSONObject jsonObject = new JSONObject(httptool.httppost(
						Urllist.completeInfo, params));
				// 解析json,结果赋给bean;
				if (jsonObject.getString("success").equals("true")) {

					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				} else {
					fail_message = jsonObject.getString("errors");
					Message message = new Message();
					message.what = 2;
					handler.sendMessage(message);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(SendOver_order_detail.this, "提交成功", 0).show();
				break;
			case 2:
				Toast.makeText(SendOver_order_detail.this,
						"提交失败," + fail_message, 0).show();
				break;
			default:
				break;
			}
		};
	};
}
