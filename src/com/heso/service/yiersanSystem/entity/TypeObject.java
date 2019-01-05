package com.heso.service.yiersanSystem.entity;

import java.io.Serializable;

public class TypeObject implements Serializable {
	
	private String id; 
	private String des;
	private String requirement;
	private String name ; 
	
	
	@Override
	public String toString() {
		return "typeObject [id=" + id + ", des=" + des + ", requirement=" + requirement + ", name=" + name + "]";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getRequirement() {
		return requirement;
	}
	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}
	
	
	
	
}
