package com.heso.service.cart.entity;

import java.math.BigDecimal;

public class CheckObject {
	private String iamge;
	private String warbrobe;
	private String checkId;
	private String id;
	private String account;
	private String type;
	private String name ;
	private String wardrobeOrTripId;
	private String recordId;
	private BigDecimal price;
	private BigDecimal amount;
	public BigDecimal getPrice() {
		return price; 
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getWardrobeOrTripId() {
		return wardrobeOrTripId;
	}
	public void setWardrobeOrTripId(String wardrobeOrTripId) {
		this.wardrobeOrTripId = wardrobeOrTripId;
	}
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIamge() {
		return iamge;
	}
	public void setIamge(String iamge) {
		this.iamge = iamge;
	}
	public String getWarbrobe() {
		return warbrobe;
	}
	public void setWarbrobe(String warbrobe) {
		this.warbrobe = warbrobe;
	}
	public String getCheckId() {
		return checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
