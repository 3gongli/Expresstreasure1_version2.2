package com.expresstreasure.tils;

public class EsOrderData {
	private String id = "0"; //内部编号
	private String orderId = "0";  //快递单号
	private String companyName = ""; //电商名称
	private String sp_id = "0";	//电商编号
	private String is_topay = "0";
	private String fee = "0"; // 到付费用
	private String fetch_buyer_fee="0";
	private String status = "0"; // 快递单状态
	private String remarks = ""; // 备注
	private String creat_time = "";

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getSpId() {
		return sp_id;
	}

	public void setSpId(String sp_id) {
		this.sp_id = sp_id;
	}
	
	
	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}
	
	public void setBuyerFee(String fetch_buyer_fee)
	{
		this.fetch_buyer_fee =fetch_buyer_fee;
	}
	
	public String getBuyerFee()
	{
		return fetch_buyer_fee;
	}
	
	public String getIsToPay() {
		return is_topay;
	}

	public void setIsToPay(String is_topay) {
		this.is_topay = is_topay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public void setCreatTime(String creat_time)
	{
		this.creat_time =creat_time;
	}
	
	public String getCreatTime()
	{
		return creat_time;
	}
	
	
}