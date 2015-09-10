package com.expresstreasure.tils;

public class Urllist {
	// url列表
	public static final String HOST = "http://open.3gongli.com";
	// 测试版
	// public static final String HOST = "http://123.57.239.10:8080";
	// 本机
	// public static final String HOST = "http://119.40.9.37";
	public static final String login = HOST + "/mobile/login";
	public static final String scan_order = HOST + "/mobile/scan_order";
	public static final String o2o_waitsend = HOST
			+ "/mobile/query_courier_waybill";
	public static final String work_status = HOST + "/mobile/work_status";
	public static final String delivery_count = HOST + "/mobile/delivery_count";
	public static final String today_count = HOST + "/mobile/today_count";
	// public static final String handlewaybill =
	// "http://123.57.217.101/mobile/handle_waybill";
	public static final String add_new_order = HOST + "/mobile/save_order";
	public static final String scan_identify = HOST + "/mobile/scan_identify";
	public static final String handle_waybill = HOST + "/mobile/handle_waybill";
	public static final String query_courier_waybill = HOST
			+ "/mobile/query_courier_waybill";
	public static final String olsp_list = HOST + "/mobile/olsp_list";
	public static final String report_gps = HOST + "/mobile/report_gps";

	public static final String sender_check_version = HOST
			+ "/api/courier_version";
	public static final String sender_downloadnewversion = HOST
			+ "/api/download/courier_app?v=";

	public static final String uploadFile = HOST + "/mobile/upload_img";

	public static final String completeInfo = HOST + "/mobile/makeup_buyinfo";
}
