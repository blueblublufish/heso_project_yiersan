package com.heso.service.order.consume.entity;

import java.util.ArrayList;

public class ConsumeOrderReturnObject {
	
	public ConsumeOrderReturnObject() {
		code = "000000";
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public ArrayList<ConsumeOrderObject> getCooList() {
		return cooList;
	}
	public void setCooList(ArrayList<ConsumeOrderObject> cooList) {
		this.cooList = cooList;
	}

	public String getReccount() {
		return reccount;
	}
	public void setReccount(String reccount) {
		this.reccount = reccount;
	}
	
	
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}


	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}


	private String reccount;
	private String desc;
	private String code;
	private String orderId;
	private String discount;
	ArrayList<ConsumeOrderObject> cooList;

}
