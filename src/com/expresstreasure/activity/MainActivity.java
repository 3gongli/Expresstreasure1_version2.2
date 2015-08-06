package com.expresstreasure.activity;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.expresstreasure.R;
import com.expresstreasure.tils.SharePrefUtil;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements
		OnCheckedChangeListener {

	/** Called when the activity is first created. */
	private TabHost mHost;
	private RadioGroup radioderGroup;
	private String status = "";
	private SharePrefUtil share = new SharePrefUtil();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.maintabs);
		// 实例化TabHost
		mHost = this.getTabHost();
		// 添加选项卡
		// mHost.addTab(mHost.newTabSpec("ONE").setIndicator("ONE").setContent(new
		// Intent(this, Es_activity.class)));
		mHost.addTab(mHost.newTabSpec("TWO").setIndicator("TWO")
				.setContent(new Intent(this, O2O_activity.class)));
		mHost.addTab(mHost.newTabSpec("THREE").setIndicator("THREE")
				.setContent(new Intent(this, My_activity.class)));
		mHost.setCurrentTab(0);
		radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
		radioderGroup.setOnCheckedChangeListener(this);
		RadioButton radioButton1 = (RadioButton) findViewById(R.id.radio_button1);
		final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio_button2);
		radioButton1.setChecked(true);
		status = share.getString(getApplicationContext(), "work_status", null);
		if (status.equals("2")) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setMessage("您未开工,点击确认去开工");
			ab.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mHost.setCurrentTabByTag("THREE");
					radioButton2.setChecked(true);
				}
			});
			ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			ab.create().show();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		// case R.id.radio_button0:
		// mHost.setCurrentTabByTag("ONE");
		// break;
		case R.id.radio_button1:
			mHost.setCurrentTabByTag("TWO");
			break;
		case R.id.radio_button2:
			mHost.setCurrentTabByTag("THREE");
			break;

		}
	}

	//
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
