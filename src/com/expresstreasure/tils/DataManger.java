package com.expresstreasure.tils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.expresstreasure.activity.MainActivity;

public class DataManger {
	public static DataManger instance = new DataManger();
	private MainActivity mainActivity;
	private String str_scan = "";// 扫描结果
	private String cid = ""; // 用户id
	private String name;// 用户名称
	private String realname;// 用户名称
	private String phone;
	private String start;
	private boolean istake = true; // 是否取件，否为签收
	private boolean iswork = false;// 开工，收工是否成功
	private String pay_shipper_fee;// 商家费用
	private double Longitude;// 经度
	private double Latitude;// 纬度
	private String enterprise_id = "";
	// es
	private List<DataManger> olsp_list;
	private String olsp_id; // 电商id
	private String olsp_name;// 电商名称
	private String olsp_phone;// 电商手机
	private String olsp_tel;// 电话
	private ArrayList<String> arr_olsp_list; // 商家列表

	// o2o
	private String page = "0";// 页面
	private String create_time;// 创建时间
	private String fetch_buyer_fee; // 客户费用
	private String cargo_price;// 配送的商家价格
	private String shipper_name;// 发货商家名称
	private String shipper_phone;// 发货商家电话
	private String shipper_address;// 发货商家地址
	private String buyer_name;// 收货人名称
	private String buyer_phone;// 收货商家电话
	private String buyer_address;// 收货商家地址
	private String distance;// 距离
	private String pay_type;// 支付类型
	private String shipper_origin_id;// 当前o2o订单 id
	private String is_booking;// 是否实物
	private String booking_fetch_time;// 预定取货时间
	// private String remarks;// 配送备注
	private String waybill_status;// 运单状态
	private String id;// 订单号
	private String status = "0";// 订单号
	private String handover_fee;// 上缴费用
	private int totalCount; // o2o 当前页条数
	private List<Map<String, Object>> o2osendlist;
	private List<DataManger> O2OSend_list;

	// 统计
	private String total_shipper_fees;// 付给商家总费用
	private String total_buyer_fees; // 收取客户总费用
	private String total_pay_fees;// 上缴费用
	private String total_waybill;// 总单数
	private String average_time;// 平均配送时间
	private String total_goods_fee;

	public String getTotal_goods_fee() {
		return total_goods_fee;
	}

	public void setTotal_goods_fee(String total_goods_fee) {
		this.total_goods_fee = total_goods_fee;
	}

	// 每天统计电商
	private String today_es_take = "0";// 电商取件配送
	private String today_es_sign = "0"; // 客户签收
	private String today_es_error = "0";// 异常件
	// 每天统计o2o
	private String today_o2o_take = "0";// 待配送
	private String today_o2o_seding = "0";// 配送中
	private String today_o2o_over = "0";// 配送中

	private String remarks = ""; // 备注

	private String goods_type;
	private String goods_num;
	private String total_num;

	public String getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(String goods_type) {
		this.goods_type = goods_type;
	}

	public String getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(String goods_num) {
		this.goods_num = goods_num;
	}

	public String getTotal_num() {
		return total_num;
	}

	public void setTotal_num(String total_num) {
		this.total_num = total_num;
	}

	public String getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public String getShipper_origin_id() {
		return shipper_origin_id;
	}

	public void setShipper_origin_id(String shipper_origin_id) {
		this.shipper_origin_id = shipper_origin_id;
	}

	public String getPay_shipper_fee() {
		return pay_shipper_fee;
	}

	public void setPay_shipper_fee(String pay_shipper_fee) {
		this.pay_shipper_fee = pay_shipper_fee;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public ArrayList<String> getArr_olsp_list() {
		return arr_olsp_list;
	}

	public void setArr_olsp_list(ArrayList<String> arr_olsp_list) {
		this.arr_olsp_list = arr_olsp_list;
	}

	public List<DataManger> getOlsp_list() {
		return olsp_list;
	}

	public void setOlsp_list(List<DataManger> olsp_list) {
		this.olsp_list = olsp_list;
	}

	public String getOlsp_id() {
		return olsp_id;
	}

	public void setOlsp_id(String olsp_id) {
		this.olsp_id = olsp_id;
	}

	public String getOlsp_name() {
		return olsp_name;
	}

	public void setOlsp_name(String olsp_name) {
		this.olsp_name = olsp_name;
	}

	public String getOlsp_phone() {
		return olsp_phone;
	}

	public void setOlsp_phone(String olsp_phone) {
		this.olsp_phone = olsp_phone;
	}

	public String getOlsp_tel() {
		return olsp_tel;
	}

	public void setOlsp_tel(String olsp_tel) {
		this.olsp_tel = olsp_tel;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getToday_es_take() {
		return today_es_take;
	}

	public void setToday_es_take(String today_es_take) {
		this.today_es_take = today_es_take;
	}

	public String getToday_es_sign() {
		return today_es_sign;
	}

	public void setToday_es_sign(String today_es_sign) {
		this.today_es_sign = today_es_sign;
	}

	public String getToday_es_error() {
		return today_es_error;
	}

	public void setToday_es_error(String today_es_error) {
		this.today_es_error = today_es_error;
	}

	public String getToday_o2o_take() {
		return today_o2o_take;
	}

	public void setToday_o2o_take(String today_o2o_take) {
		this.today_o2o_take = today_o2o_take;
	}

	public String getToday_o2o_seding() {
		return today_o2o_seding;
	}

	public void setToday_o2o_seding(String today_o2o_seding) {
		this.today_o2o_seding = today_o2o_seding;
	}

	public String getToday_o2o_over() {
		return today_o2o_over;
	}

	public void setToday_o2o_over(String today_o2o_over) {
		this.today_o2o_over = today_o2o_over;
	}

	public String getBuyer_address() {
		return buyer_address;
	}

	public void setBuyer_address(String buyer_address) {
		this.buyer_address = buyer_address;
	}

	public List<Map<String, Object>> getO2osendlist() {
		return o2osendlist;
	}

	public void setO2osendlist(List<Map<String, Object>> o2osendlist) {
		this.o2osendlist = o2osendlist;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getHandover_fee() {
		return handover_fee;
	}

	public void setHandover_fee(String handover_fee) {
		this.handover_fee = handover_fee;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getTotal_shipper_fees() {
		return total_shipper_fees;
	}

	public void setTotal_shipper_fees(String total_shipper_fees) {
		this.total_shipper_fees = total_shipper_fees;
	}

	public String getTotal_buyer_fees() {
		return total_buyer_fees;
	}

	public void setTotal_buyer_fees(String total_buyer_fees) {
		this.total_buyer_fees = total_buyer_fees;
	}

	public String getTotal_pay_fees() {
		return total_pay_fees;
	}

	public void setTotal_pay_fees(String total_pay_fees) {
		this.total_pay_fees = total_pay_fees;
	}

	public String getTotal_waybill() {
		return total_waybill;
	}

	public void setTotal_waybill(String total_waybill) {
		this.total_waybill = total_waybill;
	}

	public String getAverage_time() {
		return average_time;
	}

	public void setAverage_time(String average_time) {
		this.average_time = average_time;
	}

	public String getFetch_buyer_fee() {
		return fetch_buyer_fee;
	}

	public void setFetch_buyer_fee(String fetch_buyer_fee) {
		this.fetch_buyer_fee = fetch_buyer_fee;
	}

	public String getCargo_price() {
		return cargo_price;
	}

	public void setCargo_price(String cargo_price) {
		this.cargo_price = cargo_price;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getShipper_name() {
		return shipper_name;
	}

	public void setShipper_name(String shipper_name) {
		this.shipper_name = shipper_name;
	}

	public String getShipper_phone() {
		return shipper_phone;
	}

	public void setShipper_phone(String shipper_phone) {
		this.shipper_phone = shipper_phone;
	}

	public String getShipper_address() {
		return shipper_address;
	}

	public void setShipper_address(String shipper_address) {
		this.shipper_address = shipper_address;
	}

	public String getBuyer_name() {
		return buyer_name;
	}

	public void setBuyer_name(String buyer_name) {
		this.buyer_name = buyer_name;
	}

	public String getBuyer_phone() {
		return buyer_phone;
	}

	public void setBuyer_phone(String buyer_phone) {
		this.buyer_phone = buyer_phone;
	}

	public String getIs_booking() {
		return is_booking;
	}

	public void setIs_booking(String is_booking) {
		this.is_booking = is_booking;
	}

	public String getBooking_fetch_time() {
		return booking_fetch_time;
	}

	public void setBooking_fetch_time(String booking_fetch_time) {
		this.booking_fetch_time = booking_fetch_time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWaybill_status() {
		return waybill_status;
	}

	public void setWaybill_status(String waybill_status) {
		this.waybill_status = waybill_status;
	}

	public List<DataManger> getO2OSend_list() {
		return O2OSend_list;
	}

	public void setO2OSend_list(List<DataManger> o2oSend_list) {
		O2OSend_list = o2oSend_list;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getCid() {
		return cid;
	}

	public boolean getIswork() {
		return iswork;
	}

	public void setIswork(boolean iswork) {
		this.iswork = iswork;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public boolean getIstake() {
		return istake;
	}

	public void setIstake(boolean istake) {
		this.istake = istake;
	}

	public String getStr_scan() {
		return str_scan;
	}

	public void setStr_scan(String str_scan) {
		this.str_scan = str_scan;
	}

	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

}
