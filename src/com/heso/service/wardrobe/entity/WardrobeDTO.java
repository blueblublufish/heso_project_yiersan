package com.heso.service.wardrobe.entity;

import java.util.ArrayList;

public class WardrobeDTO {
	private String id; //ID
	private String account;//用户ID
	private String image;//图片
	private String type;//类型
	private String cloth;//面料
	private String scene;//场景
	private String style;//风格 
	private String color;//颜色
	private String suit;//套装
	private String uplaod;//上传者
	private String pattern;//款式
	private String outline;//轮廓
	private String charater;//性格
	private String createTime; 
	private String isGood;//是否商品
	private String name;//物品ID
	private String secondType;
	private String secondTypeName;
	private String season;
	private String sex;
	
	private String secondTypeId;
	private String seasonName;
	private String styleName;
	private String sceneName;
	private String categoryName;
	
	
	
	
	public String getSecondTypeId() {
		return secondTypeId;
	}
	public void setSecondTypeId(String secondTypeId) {
		this.secondTypeId = secondTypeId;
	}
	public String getSeasonName() {
		return seasonName;
	}
	public void setSeasonName(String seasonName) {
		this.seasonName = seasonName;
	}
	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getSecondType() {
		return secondType;
	}
	public void setSecondType(String secondType) {
		this.secondType = secondType;
	}
	public String getSecondTypeName() {
		return secondTypeName;
	}
	public void setSecondTypeName(String secondTypeName) {
		this.secondTypeName = secondTypeName;
	}
	private String typeName;
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	private String count;
	private String label;
	private String impression; 
	private ArrayList<WardrobeDTO> wList;
	public ArrayList<WardrobeDTO> getwList() {
		return wList;
	}
	public void setwList(ArrayList<WardrobeDTO> wList) {
		this.wList = wList;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getImpression() {
		return impression;
	}
	public void setImpression(String impression) {
		this.impression = impression;
	}
	public String getCount() { 
		return count;
	} 
	public void setCount(String count) {
		this.count = count;
	}
	public String getName() {
		return name; 
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIsGood() {
		return isGood;
	}
	public void setIsGood(String isGood) {
		this.isGood = isGood;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCloth() {
		return cloth;
	}
	public void setCloth(String cloth) {
		this.cloth = cloth;
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getSuit() {
		return suit;
	}
	public void setSuit(String suit) {
		this.suit = suit;
	}
	public String getUplaod() {
		return uplaod;
	}
	public void setUplaod(String uplaod) {
		this.uplaod = uplaod;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getOutline() {
		return outline;
	}
	public void setOutline(String outline) {
		this.outline = outline;
	}
	public String getCharater() {
		return charater;
	}
	public void setCharater(String charater) {
		this.charater = charater;
	}
	
}
