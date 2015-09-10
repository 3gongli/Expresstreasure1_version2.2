package com.expresstreasure.tils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Jsontool {

	/**
	 * 
	 * 开工/收工
	 * 
	 **/
	public void Startwork(JSONObject json, String type) throws JSONException {

	}

	/**
	 * 
	 * o2o配送 列表
	 * 
	 **/
	public void o2olist(JSONObject json) throws JSONException {

		List<DataManger> li = new ArrayList<DataManger>();
		JSONArray o2oarray = json.getJSONArray("obj");
		DataManger.instance.setTotalCount(Integer.parseInt(json
				.getString("totalCount")));// 总条数
		DataManger.instance.setPage(json.getString("page"));// 页数
		for (int i = 0; i < o2oarray.length(); i++) {
			JSONObject jsobj = o2oarray.getJSONObject(i);
			// Log.i("------------->", jsobj.toString());
			DataManger dt = new DataManger();
			dt.setCreate_time(jsobj.getString("create_time"));// 下单时间
			// dt.setPay_shipper_fee(jsobj.getString("pay_shipper_fee"));// 商家费用
			// dt.setFetch_buyer_fee(jsobj.getString("fetch_buyer_fee"));// 客户费用
			dt.setCargo_price(jsobj.getString("cargo_price"));// 价格
			dt.setShipper_name(jsobj.getString("shipper_name"));// 发货商家名称
			dt.setShipper_phone(jsobj.getString("shipper_phone"));// 发货商家电话
			dt.setShipper_address(jsobj.getString("shipper_address"));// 发货商家地址
			dt.setBuyer_name(jsobj.optString("buyer_real_name"));// 收货人名称
			dt.setBuyer_phone(jsobj.optString("buyer_phone"));// 收货人电话
			dt.setBuyer_address(jsobj.optString("buyer_address"));// 收货人地址
			// dt.setIs_booking(jsobj.getString("is_booking"));// 是否实物
			dt.setBooking_fetch_time(jsobj.getString("booking_fetch_time"));// 预定取货时间
			dt.setRemarks(jsobj.getString("remarks"));// 备注
			dt.setWaybill_status(jsobj.getString("waybill_status"));// 运单状态
			dt.setId(jsobj.getString("id"));// 订单号
			dt.setGoods_num(jsobj.optString("cargo_num"));
			dt.setGoods_type(jsobj.optString("cargo_type"));
			dt.setTotal_num(jsobj.optString("waybill_num"));
			dt.setSp_x(jsobj.optString("shipper_x"));
			dt.setSp_y(jsobj.optString("shipper_y"));

			// dt.setIs_topay(jsobj.getString("is_topay"));// 是否到付
			// dt.setHandover_fee(jsobj.getString("handover_fee"));// 上缴费用
			// dt.setDistance(jsobj.getString("distance"));
			// dt.setPay_type(jsobj.getString("pay_type")); //支付类型
			// dt.setShipper_origin_id(jsobj.getString("shipper_origin_id"));
			// //订单id
			li.add(dt);
		}
		DataManger.instance.setO2OSend_list(li);

	}

	/**
	 * 
	 * 每天电商统计
	 * 
	 **/
	public void day_count(JSONObject json, String str_state)
			throws JSONException {
		JSONArray es_array = json.getJSONArray("obj");

		int state = Integer.valueOf(str_state);
		for (int i = 0; i < es_array.length(); i++) {
			JSONObject data = es_array.getJSONObject(i);
			switch (Integer.valueOf(data.getString("status"))) {
			// case -1:
			// // 异常
			// DataManger.instance.setToday_es_error(isnull(data.getString("num")));
			// break;
			case 0:
				// 待配送
				DataManger.instance.setToday_o2o_take(isnull(data
						.getString("num")));
				break;
			case 1:
				if (state == 1)// 1为电商，2为o2o
				{
					// 取件配送
					// DataManger.instance.setToday_es_take(isnull(data.getString("num")));
				} else {
					// 配送中
					DataManger.instance.setToday_o2o_seding(isnull(data
							.getString("num")));
				}
				break;

			case 5:
				// 完成
				if (state == 1)// 1为电商，2为o2o
				{
					// 电商 签收
					// DataManger.instance.setToday_es_sign(isnull(data.getString("num")));
				} else {
					// o2o完成
					DataManger.instance.setToday_o2o_over(isnull(data
							.getString("num")));
				}

				break;

			default:
				break;
			}

		}

	}

	public static String isnull(String strnull) {
		if (strnull.equals(null) || strnull.equals("")) {
			return "0";
		}
		return strnull;
	}

	/**
	 * 
	 * 统计
	 * 
	 **/
	public void all_count(JSONObject json) throws JSONException {
		JSONObject data = json.getJSONObject("obj");
		DataManger.instance.setTotal_waybill(data.getString("total_waybill"));
		DataManger.instance.setTotal_shipper_fees(data
				.getString("total_shipper_fees"));
		DataManger.instance.setTotal_buyer_fees(data
				.getString("total_buyer_fees"));
		DataManger.instance.setTotal_pay_fees(data.getString("total_pay_fees"));
		DataManger.instance.setTotal_goods_fees(data.getString("total_price"));
		// DataManger.instance.setAverage_time(data.getString("average_time"));
	}
}
