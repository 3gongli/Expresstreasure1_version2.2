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
import android.view.View.OnClickListener;
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
import com.expresstreasure.tils.SharePrefUtil;
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
												dt.getShipper_name());
										map.put("buyer_name",
												dt.getBuyer_name() + "/");
										map.put("buyer_phone",
												dt.getBuyer_phone());
										map.put("buyer_address",
												dt.getBuyer_address());
										map.put("goods_num",
												dt.getCargo_price());
										map.put("total_num", dt.getTotal_num());
										map.put("remarks", dt.getRemarks());
										map.put("goods_type",
												dt.getGoods_type());
										map.put("create_time",
												dt.getCreate_time());
										map.put("id", dt.getId());

										map.put("sp_x", dt.getSp_x());
										map.put("sp_y", dt.getSp_y());
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
				holder.complete_info = (TextView) convertView
						.findViewById(R.id.complete_info);
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
			holder.complete_info.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharePrefUtil sp = new SharePrefUtil();
					sp.saveString(O2OSendover_activity.this, "sp_x",
							(String) data.get(position).get("sp_x"));
					sp.saveString(O2OSendover_activity.this, "sp_y",
							(String) data.get(position).get("sp_y"));
					Intent intent = new Intent(O2OSendover_activity.this,
							SendOver_order_detail.class);
					Bundle bundle = new Bundle();
					bundle.putInt("position", position);
					bundle.putString("id", (String) data.get(position)
							.get("id"));
					bundle.putString("shipper_name", (String) data
							.get(position).get("shipper_name"));
					bundle.putString("shipper_address",
							(String) data.get(position).get("shipper_address"));
					bundle.putString("shipper_phone",
							(String) data.get(position).get("shipper_phone"));
					bundle.putString("goods_type", (String) data.get(position)
							.get("goods_type"));
					bundle.putString("remarks", (String) data.get(position)
							.get("remarks"));
					// bundle.putString("sp_x",
					// (String) data.get(position).get("sp_x"));
					// bundle.putString("sp_y",
					// (String) data.get(position).get("sp_y"));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
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
			public TextView complete_info;// 信息补录
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
