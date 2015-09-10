package com.expresstreasure.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.expresstreasure.R;

public class AboutUs_activity extends Activity {
	private ImageView imageView_back_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		imageView_back_login = (ImageView) findViewById(R.id.imageView_back_login);
		imageView_back_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AboutUs_activity.this,
						My_activity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}
