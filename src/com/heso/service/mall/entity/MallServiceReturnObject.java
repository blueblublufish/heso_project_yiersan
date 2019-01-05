package com.heso.service.mall.entity;

import java.util.ArrayList;
import java.util.List;

public class MallServiceReturnObject {
	public MallServiceReturnObject(){
		code = "000000";
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ArrayList<ProductItemObject> getPioList() {
		return pioList;
	}

	public void setPioList(ArrayList<ProductItemObject> pioList) {
		this.pioList = pioList;
	}

	public String getRecCount() {
		return recCount;
	}
	public void setRecCount(String recCount) {
		this.recCount = recCount;
	}
	ArrayList<MemberProduct> mpList;
	public ArrayList<MemberProduct> getMpList() {
		return mpList;
	}
	public void setMpList(ArrayList<MemberProduct> mpList) {
		this.mpList = mpList;
	}

	String code;
	String recCount;
	ArrayList<ProductItemObject> pioList;
	List<RecommendProducts> manreList;
	List<RecommendProducts> womenreList;
	public List<RecommendProducts> getManreList() {
		return manreList;
	}
	public void setManreList(List<RecommendProducts> manreList) {
		this.manreList = manreList;
	}
	public List<RecommendProducts> getWomenreList() {
		return womenreList;
	}
	public void setWomenreList(List<RecommendProducts> womenreList) {
		this.womenreList = womenreList;
	}
	
}
