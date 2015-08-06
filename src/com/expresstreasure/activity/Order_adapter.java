package com.expresstreasure.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.expresstreasure.R;
import com.expresstreasure.tils.EsOrderData;

public class Order_adapter extends BaseAdapter {
	private List<EsOrderData> data;
	private LayoutInflater mInflater;
	private int type; // 1表示待配送列表，5表示签收列表，-2表示异常列表
	private Context mContext;

	public Order_adapter(List<EsOrderData> list, Context context, int type) {
		this.data = new ArrayList<EsOrderData>();
		this.data.addAll(list);
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.type = type;
	}

	@Override
	public int getCount() {
		// How many items are in the data set represented by this Adapter.
		// 在此适配器中所代表的数据集中的条目数
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// Get the data item associated with the specified position in the data
		// set.
		// 获取数据集中与指定索引对应的数据项
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// Get the row id associated with the specified position in the list.
		// 获取在列表中与指定索引对应的行id
		return position;
	}

	// Get a View that displays the data at the specified position in the data
	// set.
	// 获取一个在数据集中指定索引的视图来显示数据
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// 如果缓存convertView为空，则需要创建View
		if (convertView == null) {
			holder = new ViewHolder();
			// 根据自定义的Item布局加载布局
			convertView = mInflater.inflate(R.layout.activity_sign_list_item,
					null);
			holder.tvParceid = (TextView) convertView.findViewById(R.id.num);
			holder.tvEnterpriseName = (TextView) convertView
					.findViewById(R.id.numname);
			// Log.i("---->", "执行到了这里......");
			holder.tvRemarks = (TextView) convertView
					.findViewById(R.id.remarks);
			if (this.type == 1) {
				holder.btnTake = (Button) convertView
						.findViewById(R.id.btn_take);
				holder.btnTake.setVisibility(View.VISIBLE);
			}// 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (String.valueOf(data.get(position).getStatus()).equals("-2")) {
			holder.tvParceid.setTextColor(Color.RED);
		}
		holder.tvParceid.setText(data.get(position).getOrderId());
		holder.tvEnterpriseName.setText(data.get(position).getCompanyName());
		holder.tvRemarks.setText("备注：" + data.get(position).getRemarks());
		// if (this.type == 1) {
		//
		// holder.btnTake.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// Intent intent = new Intent(
		// Es_activity.ACTION_SHOW_SIGN_WINDOWS);
		// Es_activity.mSignOrderData = data.get(position);
		// mContext.sendBroadcast(intent);
		// }
		// });
		// }
		return convertView;
	}

	public class ViewHolder {
		TextView tvParceid;
		TextView tvEnterpriseName;
		TextView tvRemarks;
		Button btnTake;
	}

}