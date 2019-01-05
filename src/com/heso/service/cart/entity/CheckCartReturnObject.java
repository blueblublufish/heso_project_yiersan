package com.heso.service.cart.entity;

import java.util.ArrayList;

public class CheckCartReturnObject {
	private String code;
	private String account;
	private String status;
	private ArrayList<CheckObject> checkList;
	public CheckCartReturnObject(){
		code = "000000";
	}
	public ArrayList<CheckObject> getCheckList() {
		return checkList;
	} 
	public void setCheckList(ArrayList<CheckObject> checkList) {
		this.checkList = checkList;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
