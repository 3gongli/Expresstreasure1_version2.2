package com.expresstreasure.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.expresstreasure.R;

public class BigPicture extends Activity {
	private ImageView imageView1;
	private String path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big_camera);
		Bundle bundle = getIntent().getExtras();
		path = bundle.getString("path");
		initView();
	}

	public void initView() {
		imageView1 = (ImageView) findViewById(R.id.imageView1);
	}
}
