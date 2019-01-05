package com.heso.service.mall.entity;

import java.util.List;

public class RecommendProducts {
	private String sex;
	private String styleId;
	private String styleName;
	List<ProductItemObject> list;
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getStyleId() {
		return styleId;
	}
	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}
	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	public List<ProductItemObject> getList() {
		return list;
	}
	public void setList(List<ProductItemObject> list) {
		this.list = list;
	}
	
	
}
