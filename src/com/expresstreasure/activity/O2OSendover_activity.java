package com.expresstreasure.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class O2OSendover_activity extends ListActivity {

	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;
	private PullToRefreshListView mPullRefreshListView;
	Context mContext = O2OSendover_activity.this;
	public static String TotalNum = "";
	public boolean isRefresh = false;
	public static final String ACTION_REFRESH_LISTVIEW = "action_refresh_listview";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestinAgent.setLocalDebug(true);
		setContentView(R.layout.activity_o2o_list);
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
								String status = "5";// 状态(0：待配送； 1: 取件配送；5: 配送完成
													// e:
													// 异常件)
								if (DataManger.instance.getPage().equals(0)) {
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
										map.put("goods_num", dt.getGoods_num());
										map.put("total_num", dt.getTotal_num());
										map.put("remarks", dt.getRemarks());
										map.put("goods_type",
												dt.getGoods_type());
										map.put("create_time",
												dt.getCreate_time());
										// map.put("cargo_price",
										// dt.getCargo_price());
										// map.put("fetch_buyer_fee",
										// dt.getFetch_buyer_fee());
										// map.put("pay_shipper_fee",
										// dt.getPay_shipper_fee());
										// map.put("distance",
										// dt.getDistance());
										// map.put("waybill_status",
										// dt.getWaybill_status());
										li.add(map);
									}
									TotalNum = String
											.valueOf(DataManger.instance
													.getTotalCount());
									DataManger.instance.setO2osendlist(li);
								} else {
									Message msg = new Message();
									msg.what = 2;
									handler.sendMessage(msg);
									DataManger.instance.setO2osendlist(null);
									isRefresh = false;
								}
							} catch (JSONException e) {
								isRefresh = false;
								e.printStackTrace();
								return;
							}
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
						}
					} catch (IOException e) {
						isRefresh = false;
						e.printStackTrace();
					}
				}
			}).start();
			return DataManger.instance.getO2osendlist();
		}

		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {

			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	public class O2OSendover_adapter extends BaseAdapter {

		private LayoutInflater mInflater = null;
		private List<Map<String, Object>> data;

		private O2OSendover_adapter(List<Map<String, Object>> list,
				Context context) {
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
			return data.get(position);
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
			// TODO Auto-generated method stub
			ViewHolder holder;
			// 如果缓存convertView为空，则需要创建View
			if (convertView == null) {
				holder = new ViewHolder();
				// 根据自定义的Item布局加载布局
				convertView = mInflater.inflate(
						R.layout.activity_o2osendover_list_item, null);
				holder.shipper_address = (TextView) convertView
						.findViewById(R.id.shipper_address);
				holder.shipper_name_phone = (TextView) convertView
						.findViewById(R.id.shipper_name_phone);
				holder.goods_num = (TextView) convertView
						.findViewById(R.id.goods_num);
				holder.total_num = (TextView) convertView
						.findViewById(R.id.total_num);
				holder.remarks = (TextView) convertView
						.findViewById(R.id.remarks);
				holder.goods_type = (TextView) convertView
						.findViewById(R.id.goods_type);
				holder.create_time = (TextView) convertView
						.findViewById(R.id.create_time);
				holder.sender_name_phone = (TextView) convertView
						.findViewById(R.id.sender_name_phone);
				holder.maijia_address = (TextView) convertView
						.findViewById(R.id.maijia_address);
				holder.linearLayout_sender_name_phone = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_sender_name_phone);
				holder.linearLayout_maijia_address = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_maijia_address);
				// holder.cargo_price = (TextView) convertView
				// .findViewById(R.id.cargo_price);
				// holder.pay_shipper_fee = (TextView) convertView
				// .findViewById(R.id.pay_shipper_fee);
				// holder.fetch_buyer_fee = (TextView) convertView
				// .findViewById(R.id.fetch_buyer_fee);
				// holder.waybill_status = (TextView) convertView
				// .findViewById(R.id.waybill_status);
				// holder.distance = (TextView) convertView
				// .findViewById(R.id.distance);
				// holder.order_time = (TextView) convertView
				// .findViewById(R.id.order_time);
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
			// String buyer_name_phone = (String) data.get(position).get(
			// "buyer_name")
			// + (String) data.get(position).get("buyer_phone");
			// holder.buyer_name_phone.setText(buyer_name_phone);
			// holder.buyer_address.setText((String) data.get(position).get(
			// "buyer_address"));
			holder.remarks.setText((String) data.get(position).get("remarks"));
			holder.goods_type.setText((String) data.get(position).get(
					"goods_type"));
			holder.create_time.setText((String) data.get(position).get(
					"create_time"));
			holder.goods_num.setText((String) data.get(position).get(
					"goods_num"));
			holder.total_num.setText((String) data.get(position).get(
					"total_num"));
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
			// holder.cargo_price.setText((String) data.get(position).get(
			// "cargo_price"));
			// holder.pay_shipper_fee.setText("应收客户￥"
			// + (String) data.get(position).get("pay_shipper_fee"));
			// holder.fetch_buyer_fee.setText("实收客户￥"
			// + (String) data.get(position).get("fetch_buyer_fee"));
			// holder.waybill_status.setText((String) data.get(position).get(
			// "waybill_status"));
			// if
			// (data.get(position).get("waybill_status").toString().equals("5"))
			// {
			// holder.waybill_status.setText("配送完成");
			// } else if (data.get(position).get("waybill_status").toString()
			// .equals("e")) {
			// holder.waybill_status.setText("异常件！");
			// }
			// switch (data.get(position).get("waybill_status").toString()) {
			// case "0":
			// holder.waybill_status.setText("待配送");
			// break;
			// case "1":
			// holder.waybill_status.setText("取件配送");
			// break;
			// case "5":
			// holder.waybill_status.setText("配送完成");
			// break;
			// case "e":
			// holder.waybill_status.setText("异常件！");
			// break;
			//
			// default:
			// break;
			// }
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
			return convertView;
		}

		// ViewHolder静态类
		class ViewHolder {
			public TextView num; // 快递单号
			public TextView numname;// 快递名称
			public TextView shipper_address; // 发货商家地址
			public TextView shipper_name_phone; // 发货商家名称和电话
			public TextView goods_num; // 物品数量
			public TextView total_num; // 总单量
			public TextView remarks; // 备注
			public TextView goods_type; // 物品类型
			public TextView create_time; // 下单时间
			public TextView maijia_address; // 买家地址
			public TextView sender_name_phone; // 快递员姓名电话
			public LinearLayout linearLayout_maijia_address;
			public LinearLayout linearLayout_sender_name_phone;
			// public TextView cargo_price; // 费用
			// public TextView pay_shipper_fee; // 商家费用
			// public TextView fetch_buyer_fee;// 用户费用
			// public TextView handover_fee;
			// public TextView waybill_status; // 上缴费用
			// public TextView distance;// 距离
			// public TextView order_time;// 下单/预约时间

		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(O2OSendover_activity.this, "刷新配送完成列表成功",
						Toast.LENGTH_LONG).show();
				ListView actualListView = mPullRefreshListView
						.getRefreshableView();
				registerForContextMenu(actualListView);
				O2OSendover_adapter adapter = new O2OSendover_adapter(
						DataManger.instance.getO2osendlist(),
						O2OSendover_activity.this);
				actualListView.setAdapter(adapter);
				// DataManger.instance.setO2osendlist(null);
				isRefresh = false;
				Intent mIntent = new Intent(
						O2O_activity.ACTION_REFRESH_SENDOVER_REDIOBUTTON);
				sendBroadcast(mIntent);
				break;
			case 2:
				Toast.makeText(O2OSendover_activity.this, "获取配送列表失败！",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_REFRESH_LISTVIEW)) {
				new GetDataTask().execute();
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_REFRESH_LISTVIEW);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}
}
