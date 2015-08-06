package com.expresstreasure.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.expresstreasure.R;
import com.expresstreasure.tils.DataManger;
import com.expresstreasure.tils.Httptool;
import com.expresstreasure.tils.Jsontool;
import com.expresstreasure.tils.Urllist;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class O2OSend_activity extends ListActivity {

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;
	private PullToRefreshListView mPullRefreshListView;
	Context mContext = O2OSend_activity.this;
	private PopupWindow mPopupMenu = null;
	TextView btnsign, abnormal;
	View btnsign_view, abnormal_view;
	public int intpositon;
	public static String TotalNum = "";
	public boolean isRefresh = false;

	public int intstate;// 5为签收，1为异常

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_o2o_list);
		TestinAgent.setLocalDebug(true);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setMode(Mode.BOTH);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute();

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute();
					}

				});
		new GetDataTask().execute();
	}

	@Override
	public void onResume() {
		// 返回列表刷新数据
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, List<Map<String, Object>>> {

		@Override
		protected List<Map<String, Object>> doInBackground(Void... params) {
			// Simulates a background job.
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						if (!isRefresh) {
							isRefresh = true;
							List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
							Httptool http = new Httptool();
							Jsontool js = new Jsontool();
							JSONObject obj;
							try {
								String cid = DataManger.instance.getCid();
								String type = "2";// o2o为2，电商为1
								String status = "1";// 状态(0：待配送；状态取值 1: 取件配送；5:
													// 配送完成
													// e:
													// 异常件)
								if (DataManger.instance.getPage().equals("0")) {
									DataManger.instance.setPage("1");
								}
								String page = DataManger.instance.getPage();
								List<NameValuePair> params = new ArrayList<NameValuePair>();
								params.add(new BasicNameValuePair("cid", cid));
								params.add(new BasicNameValuePair("t", type));
								params.add(new BasicNameValuePair("page", page));
								params.add(new BasicNameValuePair("status",
										status));

								obj = new JSONObject(http.httppost(
										Urllist.o2o_waitsend, params));
								if (obj.getString("success").equals("true")
										&& obj.getString("errors").equals("OK")) {
									js.o2olist(obj);
									List<DataManger> dtli = DataManger.instance
											.getO2OSend_list();
									for (int i = 0; i < dtli.size(); i++) {
										DataManger dt;
										Map<String, Object> map = new HashMap<String, Object>();

										dt = dtli.get(i);
										map.put("shipper_address",
												dt.getShipper_address());
										map.put("shipper_phone",
												dt.getShipper_phone());
										map.put("shipper_name",
												dt.getShipper_name() + "/");
										map.put("buyer_name",
												dt.getBuyer_name() + "/");
										map.put("buyer_phone",
												dt.getBuyer_phone());
										map.put("buyer_address",
												dt.getBuyer_address());
										map.put("remarks", dt.getRemarks());
										map.put("goods_type",
												dt.getGoods_type());
										map.put("goods_num", dt.getGoods_num());
										map.put("total_num", dt.getTotal_num());
										map.put("create_time",
												dt.getCreate_time());
										// map.put("fetch_buyer_fee",
										// dt.getFetch_buyer_fee());
										// map.put("pay_shipper_fee",
										// dt.getPay_shipper_fee());
										map.put("id", dt.getId());
										// map.put("handover_fee",
										// dt.getHandover_fee());
										// map.put("distance",
										// dt.getDistance());
										// map.put("pay_type",
										// dt.getPay_type());
										// map.put("handover_fee",
										// dt.getHandover_fee());
										map.put("shipper_origin_id",
												dt.getShipper_origin_id());
										li.add(map);
									}
									DataManger.instance.setO2osendlist(li);
								} else {
									isRefresh = false;
									Message msg = new Message();
									msg.what = 2;
									handler.sendMessage(msg);
									DataManger.instance.setO2osendlist(null);
								}

							} catch (JSONException e) {
								isRefresh = false;
								e.printStackTrace();
								return;
							}
							TotalNum = String.valueOf(DataManger.instance
									.getTotalCount());
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}
					} catch (IOException e) {
						isRefresh = false;
						e.printStackTrace();
						// 友盟上传错误日志
						MobclickAgent.reportError(getApplicationContext(), e);
						Toast.makeText(getApplicationContext(), e.getMessage(),
								1).show();
					}
				}
			}).start();
			return DataManger.instance.getO2osendlist();
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			// mListItems.addFirst("Added after refresh...");
			// mAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	public class O2OSend_adapter extends BaseAdapter {
		private LayoutInflater mInflater = null;
		private List<Map<String, Object>> data;

		private O2OSend_adapter(List<Map<String, Object>> list, Context context) {
			// 根据context上下文加载布局，这里的是Demo17Activity本身，即this
			this.data = new ArrayList<Map<String, Object>>();
			this.data.addAll(list);
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// How many items are in the data set represented by this Adapter.
			// 在此适配器中所代表的数据集中的条目数
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// Get the data item associated with the specified position in the
			// data set.
			// 获取数据集中与指定索引对应的数据项
			return position;
		}

		@Override
		public long getItemId(int position) {
			// Get the row id associated with the specified position in the
			// list.
			// 获取在列表中与指定索引对应的行id
			return position;
		}

		// Get a View that displays the data at the specified position in the
		// data set.
		// 获取一个在数据集中指定索引的视图来显示数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			intpositon = position;
			// 如果缓存convertView为空，则需要创建View
			if (convertView == null) {
				holder = new ViewHolder();
				// 根据自定义的Item布局加载布局
				convertView = mInflater.inflate(
						R.layout.activity_o2osend_list_item, null);
				holder.shipper_address = (TextView) convertView
						.findViewById(R.id.shipper_address);
				holder.shipper_name_phone = (TextView) convertView
						.findViewById(R.id.shipper_name_phone);
				// holder.buyer_name_phone = (TextView) convertView
				// .findViewById(R.id.buyer_name_phone);
				// holder.buyer_address = (TextView) convertView
				// .findViewById(R.id.buyer_address);
				holder.remarks = (TextView) convertView
						.findViewById(R.id.remarks);
				holder.goods_type = (TextView) convertView
						.findViewById(R.id.goods_type);
				holder.create_time = (TextView) convertView
						.findViewById(R.id.create_time);
				holder.goods_num = (TextView) convertView
						.findViewById(R.id.goods_num);
				holder.total_num = (TextView) convertView
						.findViewById(R.id.total_num);
				holder.sender_name_phone = (TextView) convertView
						.findViewById(R.id.sender_name_phone);
				holder.maijia_address = (TextView) convertView
						.findViewById(R.id.maijia_address);
				holder.linearLayout_sender_name_phone = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_sender_name_phone);
				holder.linearLayout_maijia_address = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_maijia_address);
				// holder.pay_shipper_fee = (TextView) convertView
				// .findViewById(R.id.pay_shipper_fee);
				// holder.fetch_buyer_fee = (TextView) convertView
				// .findViewById(R.id.fetch_buyer_fee);
				// holder.distance = (TextView) convertView
				// .findViewById(R.id.distance);
				// holder.handover_fee = (TextView) convertView
				// .findViewById(R.id.handover_fee);
				// holder.order_time = (TextView) convertView
				// .findViewById(R.id.order_time);
				// holder.isonline = (LinearLayout) convertView
				// .findViewById(R.id.isonline);
				// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.shipper_address.setText((String) data.get(position).get(
					"shipper_address"));

			String shipper_name_phone = (String) data.get(position).get(
					"shipper_name")
					+ (String) data.get(position).get("shipper_phone");
			holder.shipper_name_phone.setText(shipper_name_phone);
			String buyer_name_phone = (String) data.get(position).get(
					"buyer_phone");
			if (buyer_name_phone.equals("")) {
				holder.linearLayout_sender_name_phone.setVisibility(View.GONE);
			} else {
				holder.sender_name_phone.setText(buyer_name_phone);
			}
			String adrString = (String) data.get(position).get("buyer_address");
			if (adrString.equals("")) {
				holder.linearLayout_maijia_address.setVisibility(View.GONE);
			} else {
				holder.maijia_address.setText(adrString);
			}

			// holder.maijia_address.setText((String) data.get(position).get(
			// "buyer_address"));
			holder.remarks.setText((String) data.get(position).get("remarks"));
			holder.goods_type.setText((String) data.get(position).get(
					"goods_type"));
			holder.goods_num.setText((String) data.get(position).get(
					"goods_num"));
			holder.total_num.setText((String) data.get(position).get(
					"total_num"));
			holder.create_time.setText((String) data.get(position).get(
					"create_time"));

			// holder.pay_shipper_fee.setText("￥"
			// + (String) data.get(position).get("pay_shipper_fee"));
			// holder.fetch_buyer_fee.setText("￥"
			// + (String) data.get(position).get("fetch_buyer_fee"));
			// holder.handover_fee.setText("￥"
			// + (String) data.get(position).get("handover_fee"));
			// holder.distance
			// .setText((String) data.get(position).get("distance"));
			// String isString = (String) data.get(position).get("is_booking");
			// if (isString.equals("0")) {
			// // 0代表实时订单
			// holder.is_booking.setText("实");
			// holder.is_booking.setBackgroundColor(getResources().getColor(
			// R.color.orange));
			// holder.order_time.setText("下单时间");
			// } else {
			// // 1代表预定订单
			// holder.is_booking.setText("预");
			// holder.is_booking.setBackgroundColor(getResources().getColor(
			// R.color.yellow));
			// holder.order_time.setText("预约时间");
			// }
			// String isOnline = (String) data.get(position).get("pay_type");
			// if (isOnline.equals("1")) {
			// // 1代表x线下支付
			// holder.isonline.setVisibility(View.GONE);
			//
			// } else {
			// // 2代表在线支付
			// holder.isonline.setVisibility(View.VISIBLE);
			// }

			btnsign = (TextView) convertView.findViewById(R.id.sign);
			convertView.findViewById(R.id.sign).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View view) {
							btnsign_view = view;
							// 签收
							// 访问网络监察
							new Thread(new Runnable() {
								@Override
								public void run() {
									Httptool http = new Httptool();
									// Jsontool js = new Jsontool();
									JSONObject obj;
									try {
										String cid = DataManger.instance
												.getCid();
										String id = data.get(intpositon)
												.get("id").toString();
										Log.e(" shipper_origin_id", id);
										String status = "5";
										List<NameValuePair> params = new ArrayList<NameValuePair>();
										params.add(new BasicNameValuePair(
												"cid", cid));
										params.add(new BasicNameValuePair("id",
												id));
										params.add(new BasicNameValuePair(
												"status", status));
										obj = new JSONObject(http.httppost(
												Urllist.handle_waybill, params));
										if (obj.getString("success").equals(
												"true")
												&& obj.getString("errors")
														.equals("OK")) {

										} else {
											Message msg = new Message();
											msg.what = 5;
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

						}
					});
			abnormal = (TextView) convertView.findViewById(R.id.abnormal);
			convertView.findViewById(R.id.abnormal).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View view) {
							btnsign_view = view;
							// 异常
							// 访问网络监察
							new Thread(new Runnable() {
								@Override
								public void run() {
									Httptool http = new Httptool();
									Jsontool js = new Jsontool();
									JSONObject obj;
									try {
										String cid = DataManger.instance
												.getCid();
										String id = data.get(intpositon)
												.get("id").toString();
										String status = "-1";
										List<NameValuePair> params = new ArrayList<NameValuePair>();
										params.add(new BasicNameValuePair(
												"cid", cid));
										params.add(new BasicNameValuePair("id",
												id));
										params.add(new BasicNameValuePair(
												"status", status));
										obj = new JSONObject(http.httppost(
												Urllist.handle_waybill, params));
										if (obj.getString("success").equals(
												"true")
												&& obj.getString("errors")
														.equals("OK")) {

										} else {
											Message msg = new Message();
											msg.what = 6;
											handler.sendMessage(msg);
											return;
										}

									} catch (JSONException e) {
										e.printStackTrace();
									} catch (ConnectTimeoutException e) {
										e.printStackTrace();
									}
									Message msg = new Message();
									msg.what = 7;
									handler.sendMessage(msg);
								}

							}).start();

						}
					});
			return convertView;
		}

		// ViewHolder静态类
		class ViewHolder {
			TextView shipper_address; // 发货商家地址
			TextView shipper_name_phone; // 发货商家名称和电话
			// TextView buyer_name_phone; // 收货人名称和电话
			// TextView buyer_address; // 收货人地址
			TextView remarks; // 备注
			TextView goods_type; // 物品类型
			TextView create_time; // 下单时间
			TextView goods_num; // 物品数量
			TextView total_num; // 总单量
			public TextView maijia_address; // 买家地址
			public TextView sender_name_phone; // 快递员姓名电话
			public LinearLayout linearLayout_maijia_address;
			public LinearLayout linearLayout_sender_name_phone;
			// TextView pay_shipper_fee; // 商家费用
			// TextView fetch_buyer_fee;// 用户费用
			// TextView handover_fee; // 上缴费用
			// TextView distance;// 距离
			// TextView order_time;// 下单/预约
			// LinearLayout isonline;// 在线支付

		}

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// Toast.makeText(O2OSend_activity.this, "获取配送列表成功！",
				// Toast.LENGTH_LONG).show();
				ListView actualListView = mPullRefreshListView
						.getRefreshableView();
				registerForContextMenu(actualListView);
				O2OSend_adapter adapter = new O2OSend_adapter(
						DataManger.instance.getO2osendlist(),
						O2OSend_activity.this);
				actualListView.setAdapter(adapter);
				isRefresh = false;
				Intent mIntent = new Intent(
						O2O_activity.ACTION_REFRESH_SEND_REDIOBUTTON);
				sendBroadcast(mIntent);
				break;
			case 2:
				Toast.makeText(O2OSend_activity.this, "获取配送列表失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(O2OSend_activity.this, "签收成功！",
						Toast.LENGTH_SHORT).show();
				new GetDataTask().execute();
				O2O_activity.o2oactiviy.update("2");
				break;
			case 4:
				break;
			case 5:
				Toast.makeText(O2OSend_activity.this, "签收失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 6:
				Toast.makeText(O2OSend_activity.this, "异常件 失败！",
						Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(O2OSend_activity.this, "异常件！",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	// 点击签收
	protected void createMenu(View view, int state) {
		intstate = state;
		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(mContext).inflate(
				R.layout.o2o_sign_popwindow, null);
		if (state == 0) {
			contentView.findViewById(R.id.layout_sign).setVisibility(
					View.VISIBLE);
			contentView.findViewById(R.id.layout_error)
					.setVisibility(View.GONE);
		} else {
			contentView.findViewById(R.id.layout_error).setVisibility(
					View.VISIBLE);
			contentView.findViewById(R.id.layout_sign).setVisibility(View.GONE);
		}
		TextView num, price;
		num = (TextView) contentView.findViewById(R.id.num);
		num.setText("1件");
		price = (TextView) contentView.findViewById(R.id.price);
		List<Map<String, Object>> list = DataManger.instance.getO2osendlist();
		if (list != null) {
			price.setText("￥"
					+ list.get(intpositon).get("shipper_name").toString());
		}

		// // 设置按钮的点击事件
		// contentView.findViewById(R.id.ok).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// if (mPopupMenu != null && mPopupMenu.isShowing()) {
		// mPopupMenu.dismiss();
		// mPopupMenu = null;
		// if (intstate == 0) {
		// new GetDataTask().execute();
		// }
		//
		// }
		// }
		// });
		mPopupMenu = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		/*
		 * popupWindow.setTouchable(true);
		 * 
		 * popupWindow.setTouchInterceptor(new OnTouchListener() {
		 * 
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * 
		 * Log.i("mengdd", "onTouch : ");
		 * 
		 * return false; // 这里如果返回true的话，touch事件将被拦截 // 拦截后
		 * PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss } });
		 */
		mPopupMenu.setFocusable(true);
		mPopupMenu.setOutsideTouchable(true);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		ColorDrawable colorDrawable = new ColorDrawable(Color.argb(0, 0, 0, 0));
		mPopupMenu.setBackgroundDrawable(colorDrawable);
		mPopupMenu.showAtLocation(view, Gravity.CENTER, 0, 0);

	}

}
