package com.heso.service.article.entity;

import java.util.ArrayList;

public class ArticleAndvideoREturnObject {
	private String code;
	private ArrayList<ArticleCommentDTO> acDtoList;
	public ArticleAndvideoREturnObject(){
		code = "000000";
	}  
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ArrayList<ArticleCommentDTO> getAcDtoList() {
		return acDtoList;
	}
	public void setAcDtoList(ArrayList<ArticleCommentDTO> acDtoList) {
		this.acDtoList = acDtoList;
	}
	
	private String flag;
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
