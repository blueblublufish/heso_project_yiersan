package com.heso.service.yesUser.entity;

import java.util.Date;

public class AccountRight {
	private String account;
	private int Id;
	private String phone;
	private String quanyiId;
	private String quanyiName;
	private String status;
	private int count;
	private String teachera;
	private String teacherb;
	private String Name;
	private String zhuli;
	private Date endTime;
	private String type;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {	
		this.account = account;
	}
 
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getQuanyiId() {
		return quanyiId;
	}
	public void setQuanyiId(String quanyiId) {
		this.quanyiId = quanyiId;
	}
	public String getQuanyiName() {
		return quanyiName;
	}
	public void setQuanyiName(String quanyiName) {
		this.quanyiName = quanyiName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getTeachera() {
		return teachera;
	}
	public void setTeachera(String teachera) {
		this.teachera = teachera;
	}
	public String getTeacherb() {
		return teacherb;
	}
	public void setTeacherb(String teacherb) {
		this.teacherb = teacherb;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getZhuli() {
		return zhuli;
	}
	public void setZhuli(String zhuli) {
		this.zhuli = zhuli;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
