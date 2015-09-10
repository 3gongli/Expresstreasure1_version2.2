package com.expresstreasure.activity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.expresstreasure.R;
import com.expresstreasure.tils.BitmapUtil;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.SharePrefUtil;
import com.expresstreasure.tils.UploadFile;
import com.expresstreasure.tils.UploadUtil;
import com.expresstreasure.tils.Urllist;

@SuppressLint("HandlerLeak")
public class Camera_activity extends Activity implements
		OnGetGeoCoderResultListener {

	private final double EARTH_RADIUS = 6378137.0;// 地球半径
	// private final String url = "http://119.40.9.37/mobile/upload_img";

	private double myLongitude;// 快递员经度
	private double myLatitude;// 快递员纬度
	private double thisLongitude;// 商家经度
	private double thisLatitude;// 商家纬度
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int SCAN_IMG = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGES = "IMG_/*";
	private Uri fileUri;
	private Bitmap bitmap;
	public ImageView imageView_camera1, imageView_back_login;
	private int position;
	private Button button_confirm;

	private TextView textView_order_id, textView_shipper_name,
			textView_shipper_address, textView_shipper_phone,
			textView_goods_type, textView_shipper_remarks, textView_upload;
	private String shipper_name, shipper_address, shipper_phone, goods_type,
			shipper_remarks, errors;
	private String id;

	private EditText editText_sp_fee, editText_buyer_fee;

	BitmapUtil bitmapUtil = new BitmapUtil();
	private GeoCoder mSearch = null;
	UploadUtil uploadUtil = new UploadUtil();
	SharePrefUtil sp = new SharePrefUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_camera);
		// getWindow().setLayout(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);

		myLongitude = LocationService.thisY;
		myLatitude = LocationService.thisX;
		addressToXY();
		Bundle bundle = getIntent().getExtras();
		position = bundle.getInt("position");
		id = bundle.getString("id");
		shipper_name = bundle.getString("shipper_name");
		shipper_address = bundle.getString("shipper_address");
		shipper_phone = bundle.getString("shipper_phone");
		goods_type = bundle.getString("goods_type");
		shipper_remarks = bundle.getString("remarks");

		initView();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(Camera_activity.this, "上传成功", Toast.LENGTH_LONG)
						.show();
				break;
			case 2:
				Toast.makeText(Camera_activity.this, errors, Toast.LENGTH_LONG)
						.show();
				break;
			case 3:
				Toast.makeText(Camera_activity.this, "取件成功！",
						Toast.LENGTH_SHORT).show();
				// new GetDataTask().execute();
				break;
			}
		}
	};

	public void initView() {
		imageView_back_login = (ImageView) findViewById(R.id.imageView_back_login);

		textView_order_id = (TextView) findViewById(R.id.textView_order_id);
		textView_shipper_name = (TextView) findViewById(R.id.textView_shipper_name);
		textView_shipper_address = (TextView) findViewById(R.id.textView_shipper_address);
		textView_shipper_phone = (TextView) findViewById(R.id.textView_shipper_phone);
		textView_goods_type = (TextView) findViewById(R.id.textView_goods_type);
		textView_shipper_remarks = (TextView) findViewById(R.id.textView_shipper_remarks);
		textView_upload = (TextView) findViewById(R.id.textView_upload);
		editText_sp_fee = (EditText) findViewById(R.id.editText_sp_fee);
		editText_buyer_fee = (EditText) findViewById(R.id.editText_buyer_fee);

		textView_order_id.setText(id);
		textView_shipper_name.setText(shipper_name);
		textView_shipper_address.setText(shipper_address);
		textView_shipper_phone.setText(shipper_phone);
		textView_goods_type.setText(goods_type);
		textView_shipper_remarks.setText(shipper_remarks);

		Log.i("----------->", "当前经度:" + myLongitude + "; 当前纬度:" + myLatitude);
		imageView_camera1 = (ImageView) findViewById(R.id.imageView_camera1);
		button_confirm = (Button) findViewById(R.id.button_confirm);

		// 返回列表
		imageView_back_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});

		// 确定取件
		button_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageView_camera1.getDrawable() == null) {
					Toast.makeText(Camera_activity.this, "您还没有拍照,无法上传", 0)
							.show();
				} else {
					new Thread(new Runnable() {
						@Override
						public void run() {
							Httptool http = new Httptool();
							Log.i("-------------->", "执行了");
							JSONObject obj;
							try {
								String status = "1";
								String goods_id = textView_order_id.getText()
										.toString();
								String cid = sp.getString(Camera_activity.this,
										"cid", null);
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("cid", cid));
								params.add(new BasicNameValuePair("id",
										goods_id));
								params.add(new BasicNameValuePair("status",
										status));
								obj = new JSONObject(http.httppost(
										Urllist.handle_waybill, params));
								if (obj.getString("success").equals("true")
										&& obj.getString("errors").equals("OK")) {
								} else {// 如果失败,输入失败的原因
									errors = obj.getString("errors");
									Message msg = new Message();
									msg.what = 2;
									handler.sendMessage(msg);
									return;
								}

							} catch (JSONException e) {
								e.printStackTrace();
							} catch (ConnectTimeoutException e) {
								e.printStackTrace();
							}
							Message msg = new Message();
							msg.what = 3;
							handler.sendMessage(msg);

						}
					}).start();
					finish();

				}
			}
		});
		// 拍照
		imageView_camera1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (imageView_camera1.getDrawable() == null) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(intent,
							CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				} else {
					View view = LayoutInflater.from(Camera_activity.this)
							.inflate(R.layout.big_camera, null);
					ImageView imageView1 = (ImageView) view
							.findViewById(R.id.imageView1);
					String path = fileUri.getPath();
					Log.i("----------------->", path);
					InputStream inputStream = null;
					try {
						inputStream = new FileInputStream(path);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

					imageView1.setImageBitmap(bitmap);
					Dialog dialog = new Dialog(Camera_activity.this,
							R.style.Dialog_Fullscreen);
					dialog.setContentView(view);
					dialog.show();
				}
			}
		});

		// 上传
		textView_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 判断是否有图片
				if (imageView_camera1.getDrawable() == null) {
					Toast.makeText(Camera_activity.this, "您还没有拍照,无法上传", 0)
							.show();
				} else {
					// 将图片压缩
					Bitmap bitmap = bitmapUtil.createThumbnail(
							fileUri.getPath(), 480, 800);
					// 将bitmap转换为jpg文件
					String path = Environment.getExternalStorageDirectory()
							+ "/" + "ksbPicture";
					File dirFile = new File(path);
					if (!dirFile.exists()) {
						dirFile.mkdirs();
					}
					try {
						final File myCaptureFile = new File(dirFile.getPath()
								+ File.separator + "xia" + ".jpg");
						if (myCaptureFile.exists()) {
							myCaptureFile.delete();
						}
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(myCaptureFile));

						bitmap.compress(CompressFormat.JPEG, 60, bos);
						bos.flush();
						bos.close();
						final String filePathString = dirFile.getPath()
								+ File.separator + "xia" + ".jpg";
						// Log.i("--------------->", filePathString + "..");
						final String md5String = checkImageFile(filePathString);
						// Log.i("--------------->", md5String + "..");
						SharePrefUtil sp = new SharePrefUtil();
						final String cid = sp.getString(Camera_activity.this,
								"cid", null);
						final String goods_id = id;
						final String sp_fee = editText_sp_fee.getText()
								.toString();
						final String buyer_fee = editText_buyer_fee.getText()
								.toString();
						Log.i("--------------->", cid + "..");
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								String result = UploadFile.postFile(
										Urllist.uploadFile, filePathString,
										md5String, cid, goods_id, sp_fee,
										buyer_fee);
								if (result.equals("success")) {
									Message message = new Message();
									message.what = 1;
									handler.sendMessage(message);
								}
								Log.i("--------->", "上传结果:" + result);
							}
						}).start();

						// bitmap.recycle();
						// bitmap = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

	}

	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = null;
		try {
			mediaStorageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"MyCameraApp");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {

				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {

			if (RESULT_OK == resultCode) {
				// Check if the result includes a thumbnail Bitmap
				if (data != null) {
					// 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
					// Image captured and saved to fileUri specified in the
					// Intent
					Toast.makeText(this, "Image saved to:\n" + data.getData(),
							Toast.LENGTH_LONG).show();

					if (data.hasExtra("data")) {
						Bitmap thumbnail = data.getParcelableExtra("data");

						imageView_camera1.setImageBitmap(thumbnail);
					}
				} else {
					// If there is no thumbnail image data, the image
					// will have been stored in the target output URI.

					// Resize the full image to fit in out image view.
					bitmap = bitmapUtil.createThumbnail(fileUri.getPath(),
							imageView_camera1.getWidth(),
							imageView_camera1.getHeight());
					imageView_camera1.setImageBitmap(bitmap);
				}
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}
	}

	// 由地址转换为经纬度
	public void addressToXY() {

		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		mSearch.geocode(new GeoCodeOption().city("北京").address("海淀区大恒科技大厦"));

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			// 没有检索到结果

		}
		// 获取地理编码结果
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Log.i("------------->", strInfo + "..");

		thisLongitude = result.getLocation().longitude;
		thisLatitude = result.getLocation().latitude;
		double distance = gps2m(myLatitude, myLongitude, thisLatitude,
				thisLongitude);
		Log.i("---------->", "距离是:" + distance);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	// 由经纬度计算两地距离
	// a=Lat1 – Lat2 为两点纬度之差 b=Lung1 -Lung2 为两点经度之差；
	private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	// 生成MD5
	public static String checkImageFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int length = -1;
				while ((length = fis.read(buf)) > -1) {
					baos.write(buf, 0, length);
				}

				md.update(baos.toByteArray());
				byte[] result = md.digest();

				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < result.length; i++) {
					byte now = result[i];
					String hex = Integer.toHexString(now & 0xff);
					if (hex.length() < 2) {
						stringBuilder.append("0");
					}
					stringBuilder.append(hex);
				}
				String md5ValidteStr = stringBuilder.toString();
				return md5ValidteStr;
			} catch (Exception e) {
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {

					}
				}
			}
		} else {
			Log.i("------------->", "图片不存在, path = " + filePath);
		}
		return null;
	}

}
