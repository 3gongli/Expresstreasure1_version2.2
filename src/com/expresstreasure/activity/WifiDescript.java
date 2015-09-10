package com.expresstreasure.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.expresstreasure.R;

public class WifiDescript extends Activity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_descrip);
		initView();
	}

	public void initView() {
		webView = (WebView) findViewById(R.id.webView1);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportMultipleWindows(true);

		webView.loadUrl("http://open.3gongli.com/notice/wifi_notice.html");
		// webView.loadUrl("http://www.baidu.com/");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(WifiDescript.this, Login_activity.class);
			startActivity(intent);
			finish();
		}
		return true;
	}
}
