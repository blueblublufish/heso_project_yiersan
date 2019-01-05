package com.heso.service.wardrobe.entity;

import java.util.List;

public class CheckRecord {
	
	private String Id;
	private String name;
	private String status;
	private String account;
	private String recordType;
	private String iamge;
	private String count;
	private String price;
	private String amount;
	private String creatime;
	
	private List<CheckRecordDetail> crdList;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getIamge() {
		return iamge;
	}

	public void setIamge(String iamge) {
		this.iamge = iamge;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCreatime() {
		return creatime;
	}

	public void setCreatime(String creatime) {
		this.creatime = creatime;
	}

	public List<CheckRecordDetail> getCrdList() {
		return crdList;
	}

	public void setCrdList(List<CheckRecordDetail> crdList) {
		this.crdList = crdList;
	}
	

}
