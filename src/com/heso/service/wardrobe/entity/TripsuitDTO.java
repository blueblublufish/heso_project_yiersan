package com.heso.service.wardrobe.entity;

import java.util.List;

public class TripsuitDTO {
	private String Id;
	private String tripDate;
	private String scene;
	private String place;
	private String temperatuure;
	private String wardrobeId;
	private String productId;
	private String account;
	private String suit_flag;
	private String createTime;
	private String name ;
	private String image;
	private String style;
	public String getStyle() {
		return style;
	} 
	public void setStyle(String style) {
		this.style = style;
	}
	private String price;
	private String price2;
	private List<TripsuitDTO> wardrobeList;
	private List<TripsuitDTO> productList; 
	public List<TripsuitDTO> getWardrobeList() {
		return wardrobeList;
	}
	public void setWardrobeList(List<TripsuitDTO> wardrobeList) {
		this.wardrobeList = wardrobeList;
	}
	public List<TripsuitDTO> getProductList() {
		return productList;
	}
	public void setProductList(List<TripsuitDTO> productList) {
		this.productList = productList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPrice2() {
		return price2;
	}
	public void setPrice2(String price2) {
		this.price2 = price2;
	}
	public String getId() {
		return Id; 
	}
	public void setId(String id) {
		Id = id;
	}
	public String getTripDate() {
		return tripDate;
	}
	public void setTripDate(String tripDate) {
		this.tripDate = tripDate;
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getTemperatuure() {
		return temperatuure;
	}
	public void setTemperatuure(String temperatuure) {
		this.temperatuure = temperatuure;
	}
	public String getWardrobeId() {
		return wardrobeId;
	}
	public void setWardrobeId(String wardrobeId) {
		this.wardrobeId = wardrobeId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getSuit_flag() {
		return suit_flag;
	}
	public void setSuit_flag(String suit_flag) {
		this.suit_flag = suit_flag;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
}
