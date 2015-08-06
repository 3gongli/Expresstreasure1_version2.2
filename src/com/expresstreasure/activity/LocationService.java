package com.expresstreasure.activity;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.HttptoolGet;
import com.expresstreasure.tils.Urllist;

public class LocationService extends Service {
	private static final String TAG = "LocalTestService";
	private int i = 0;
	private boolean work = false;
	private LocationClient mLocationClient;
	// private static final int GET_LOCATION_INFO = 301;
	private static final int UPDATE_LOCATION_INFO = 303;
	private static final int CANNOT_GET_LOCATION = 305;
	// private static final int GPS_IS_NOT_OPEN = 306;
	private static final int BEGIN_LOCATION = 307;
	private int time = 2;
	private long lastTime = 0;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onBind");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onCreate");
		work = true;
		initLocation();
		localThread.start();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		work = true;
		Log.i(TAG, "LocalTestService--------onStart");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onDestroy");
		work = false;
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onRebind");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "LocalTestService--------onUnbind");
		return super.onUnbind(intent);
	}

	// 初始化 location
	private void initLocation() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(new MyLocationListener());// 注册监听函数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开GPS
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		// option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		// option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		// BDLocation类，封装了定位SDK的定位结果，通过该类用户可以获取error code，位置的坐标，精度半径等信息
		public void onReceiveLocation(BDLocation location) {

			if (location == null) {
				Log.i("LocationService", "定位失败");
				if (time > 0) {
					mLocationClient.start();
					time--;
				} else {
					if (!DataManger.instance.getIswork()) {
						Message msg = new Message();
						msg.what = CANNOT_GET_LOCATION;
						handler.sendMessage(msg);
					}
				}
				return;
			} else {
				Log.i("LocationService",
						"获取到定位信息纬度：" + String.valueOf(location.getLatitude()));
				Log.i("LocationService",
						"获取到定位信息经度：" + String.valueOf(location.getLongitude()));
				DataManger.instance.setLatitude((double) (location
						.getLatitude()));// 纬度
				DataManger.instance.setLongitude((double) (location
						.getLongitude()));// 经度
				if (System.currentTimeMillis() - lastTime > 5000) {
					Message msg = new Message();
					msg.what = UPDATE_LOCATION_INFO;
					handler.sendMessage(msg);
				}
				lastTime = System.currentTimeMillis();
			}

		}

	}

	private Thread localThread = new Thread(new Runnable() {

		public void run() {
			if (work) {
				handler.postDelayed(this, 5 * 60 * 1000);// 五分钟一次
				Message msg = new Message();
				msg.what = BEGIN_LOCATION;
				handler.sendMessage(msg);
				Log.i(TAG, "LocalTestService" + i++);
			}
		}
	});

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BEGIN_LOCATION:
				mLocationClient.start();// 开始定位
				time = 2;
				break;
			case CANNOT_GET_LOCATION:
				Toast.makeText(LocationService.this, "获取定位信息失败，开工失败！",
						Toast.LENGTH_LONG).show();
				break;
			case UPDATE_LOCATION_INFO:
				new Thread(new Runnable() {
					@Override
					public void run() {
						HttptoolGet http = new HttptoolGet();
						try {
							if (!DataManger.instance.getIswork()) {// 未开工变开工
								String strurl_start_work = Urllist.work_status
										+ "?cid="
										+ DataManger.instance.getCid()
										+ "&t=1"
										+ "&x="
										+ DataManger.instance.getLongitude()
										+ "&y="
										+ DataManger.instance.getLatitude()
										+ "&eid="
										+ DataManger.instance
												.getEnterprise_id();
								Log.i("LocationService", strurl_start_work);
								JSONObject obj_start_work = new JSONObject(http
										.httpget(strurl_start_work));
								String success = obj_start_work
										.getString("success");
								if (success.equals("true")) {
									// 开工
									Log.i("LocationService", "obj_start_work"
											+ String.valueOf(obj_start_work));
									DataManger.instance.setIswork(true);
								}
								Intent mIntent = new Intent(
										My_activity.ACTION_SHOW_START_INFO);
								sendBroadcast(mIntent);
							} else {// 开工获取地址
								// getLongitude经度 ，getLatitude纬度。只有开工的时候发送。
								String strurl_update_location = Urllist.report_gps
										+ "?cid="
										+ DataManger.instance.getCid()
										+ "&x="
										+ DataManger.instance.getLongitude()
										+ "&y="
										+ DataManger.instance.getLatitude()
										+ "&eid="
										+ DataManger.instance
												.getEnterprise_id();
								Log.i("LocationService", strurl_update_location);
								JSONObject obj_update_location = new JSONObject(
										http.httpget(strurl_update_location));
								Log.i("LocationService",
										"obj"
												+ "obj_update_location"
												+ String.valueOf(obj_update_location));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ConnectTimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;
			default:
				break;
			}
		}
	};

	public static final boolean GPSIsOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps || network) {
			return true;
		}
		return false;
	}
}