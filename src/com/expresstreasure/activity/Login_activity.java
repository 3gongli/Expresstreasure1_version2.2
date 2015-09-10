package com.expresstreasure.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.expresstreasure.R;
import com.expresstreasure.tils.APNUtil;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.SharePrefUtil;
import com.expresstreasure.tils.Urllist;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class Login_activity extends Activity {
	// 1111111
	EditText pass, phone;
	private TextView textView_version;
	Button btn;
	JSONObject obj;
	DataManger dt;
	boolean islgoin = false;
	// 检查版本用
	public static final int UPDATA_CLIENT = 4;
	public static final int GET_UNDATAINFO_ERROR = 5;
	public static final int DOWN_ERROR = 6;
	private double localVersion;
	private double serverVersion;
	private SharePrefUtil share = new SharePrefUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.activity_login);
		// share.saveString(getApplicationContext(), "version_code", "3");
		checkWifi();
		// 启动线程,下载服务器版本,比较是否需要更新
		new Thread(runn).start();
		// checkNetwork();
		SharedPreferences sp = this.getSharedPreferences("registered",
				MODE_PRIVATE);
		Log.d("SharedPreferences", "获取数据...");
		String token = sp.getString("token", null);
		if (token != null) {
			this.startActivity(new Intent(this, MainActivity.class));
			this.finish();
			return;
		}
		phone = (EditText) findViewById(R.id.phone);
		pass = (EditText) findViewById(R.id.psd);
		textView_version = (TextView) findViewById(R.id.textView_version);
		try {
			textView_version
					.setText("版本号:V"
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// phone.setText("13412345678");
		// pass.setText("12345");
		btn = (Button) findViewById(R.id.submit);
		phone.setText(share.getString(getApplicationContext(), "phone", null));
		pass.setText(share.getString(getApplicationContext(), "pass", null));

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				share.saveString(getApplicationContext(), "phone", phone
						.getText().toString());
				share.saveString(getApplicationContext(), "pass", pass
						.getText().toString());
				if (pass.getText().length() > 0 && phone.getText().length() > 0) {
					// 访问网络监察
					new Thread(new Runnable() {
						@Override
						public void run() {
							Httptool http = new Httptool();
							// Jsontool js = new Jsontool();
							try {
								List<NameValuePair> params = new ArrayList<NameValuePair>();

								params.add(new BasicNameValuePair("name", phone
										.getText().toString()));
								params.add(new BasicNameValuePair("password",
										pass.getText().toString()));
								obj = new JSONObject(http.httppost(
										Urllist.login, params));
								Log.i("------>Login", String.valueOf(obj));
								if (obj.getString("success").equals("true")) {
									islgoin = true;
									if (!phone.getText().equals("")
											&& !pass.getText().equals("")) {
										DataManger.instance.setCid(obj
												.getJSONObject("obj")
												.getString("id"));
										SharedPreferences sp = Login_activity.this
												.getSharedPreferences(
														"registered",
														MODE_PRIVATE);
										share.saveString(Login_activity.this,
												"cid", obj.getJSONObject("obj")
														.getString("id"));
										Editor editor = sp.edit();
										editor.putString("name",
												String.valueOf(phone.getText()));
										editor.putString("password",
												String.valueOf(pass.getText()));
										editor.putString("id", obj
												.getJSONObject("obj")
												.getString("id"));
										editor.putString("real_name", obj
												.getJSONObject("obj")
												.getString("real_name"));
										editor.putString("name", obj
												.getJSONObject("obj")
												.getString("name"));
										editor.putString("enterprise_name", obj
												.getJSONObject("obj")
												.getString("enterprise_name"));
										editor.putString("work_status", obj
												.getJSONObject("obj")
												.getString("work_status"));
										editor.putString("sid", obj
												.getJSONObject("obj")
												.getString("sid"));
										editor.putString("phone", obj
												.getJSONObject("obj")
												.getString("phone"));
										editor.putString("status", obj
												.getJSONObject("obj")
												.getString("status"));
										editor.putString("star_level", obj
												.getJSONObject("obj")
												.getString("star_level"));
										DataManger.instance.setName(obj
												.getJSONObject("obj")
												.getString("name"));
										DataManger.instance.setPhone(obj
												.getJSONObject("obj")
												.getString("phone"));
										DataManger.instance.setRealname(obj
												.getJSONObject("obj")
												.getString("real_name"));
										DataManger.instance
												.setEnterprise_id(obj
														.getJSONObject("obj")
														.getString(
																"enterprise_id"));
										// share.saveString(
										// getApplicationContext(),
										// "delivery_status ",
										// obj.getJSONObject("obj")
										// .getString(
										// "delivery_status "));
										if (obj.getJSONObject("obj")
												.getString("work_status")
												.equals("1")) {
											DataManger.instance.setIswork(true);
											// startService(new Intent(
											// My_activity.INTENT_ACTION));
											Intent intent = new Intent();
											intent.setAction(My_activity.INTENT_ACTION);
											intent.setPackage(getPackageName());
											startService(intent);
											share.saveString(
													getApplicationContext(),
													"work_status", "1");
										} else {
											DataManger.instance
													.setIswork(false);
											share.saveString(
													getApplicationContext(),
													"work_status", "2");

										}
										editor.commit();
									}
								} else {
									Message msg = new Message();
									msg.what = 2;
									handler.sendMessage(msg);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							} catch (ConnectTimeoutException e) {
								e.printStackTrace();
							}
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}

					}).start();

				} else {
					Toast.makeText(Login_activity.this, "用户名和手机都不能为空",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * 进行网络检查
	 */
	public boolean checkNetwork() {
		// !!!在调用SDK初始化前进行网络检查
		// 当前没有拥有网络
		if (false == APNUtil.isNetworkAvailable(this)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setMessage("网络未连接,请设置网络");
			ab.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.settings.SETTINGS");
					startActivityForResult(intent, 0);
				}
			});
			ab.setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});
			ab.show();
			return false;
		} else {
			return true;
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (islgoin) {
					Toast.makeText(Login_activity.this, "登录成功", 0).show();
					Login_activity.this.startActivity(new Intent(
							Login_activity.this, MainActivity.class));
					Login_activity.this.finish();
				} else {
					Toast.makeText(Login_activity.this, "登录失败，请检查手机号和密码",
							Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case 2:
				Toast.makeText(Login_activity.this, "登录失败，请检查手机号和密码",
						Toast.LENGTH_LONG).show();
				break;
			case 4:
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case 5:
				// 服务器超时
				Toast.makeText(getApplicationContext(), "获取服务器更新信息失败", 1)
						.show();
				break;
			case 6:
				// 下载apk失败
				Toast.makeText(getApplicationContext(), "下载新版本失败", 1).show();
				break;
			}
		}
	};
	/*
	 * 从服务器获取解析并进行比对版本号
	 */
	Runnable runn = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("-------->localVersion", localVersion + "");
			try {
				// String str = share.getString(getApplicationContext(),
				// "version_code", null);
				// localVersion = Double.parseDouble(str);
				localVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionCode;
				Httptool http = new Httptool();
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				JSONObject obj = new JSONObject(http.httppost(
						Urllist.sender_check_version, params));
				if (obj.getString("success").equals("true")) {
					JSONObject jsonObject = obj.getJSONObject("obj");
					serverVersion = jsonObject.getDouble("version_num");
					String version_code = jsonObject.getString("version_code");
					// Log.i("--------->app_version", version_code + "");
					share.saveString(getApplicationContext(), "version_code",
							version_code);

				} else {

				}
				Log.i("-------->localVersion", localVersion + "");
				Log.i("-------->serverVersion", serverVersion + "");
				if (localVersion == serverVersion) {
					Log.i("--------->", "版本号相同无需升级");
					// LoginMain();
				} else {
					// Log.i(TAG,"版本号不同 ,提示用户升级 ");
					Message msg = new Message();
					msg.what = UPDATA_CLIENT;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				// 待处理
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				handler.sendMessage(msg);
				e.printStackTrace();
			}

		}
	};

	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new Builder(this);
		builer.setTitle("版本升级");
		builer.setMessage("快送宝"
				+ share.getString(getApplicationContext(), "version_code", null));
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Log.i(TAG,"下载apk,更新");
				downLoadApk();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// LoginMain();
				System.exit(0);
			}
		});
		AlertDialog dialog = builer.create();
		dialog.show();
	}

	/*
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = getFileFromServer(
							Urllist.sender_downloadnewversion
									+ share.getString(getApplicationContext(),
											"version_code", null), pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = DOWN_ERROR;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");// 编者按：此处Android应为android，否则造成安装不了
		startActivity(intent);
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());

			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(),
					"ksb.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量

				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
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

	public void checkWifi() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		Log.i("-------->wifi", mWifi.isConnected() + "..");
		if (!mWifi.isConnected()) {
			Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("提示:");

			ab.setMessage("请连接WiFi");
			ab.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.settings.SETTINGS");
					startActivityForResult(intent, 0);

				}
			});
			ab.setNeutralButton("说明", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Login_activity.this,
							WifiDescript.class);
					startActivity(intent);
					finish();
				}
			});
			ab.setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});
			ab.show();

		}
	}

}
