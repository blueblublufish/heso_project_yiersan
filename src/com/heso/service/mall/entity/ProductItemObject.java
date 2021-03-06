package com.heso.service.mall.entity;

import java.util.ArrayList;
import java.util.List;

public class ProductItemObject {
	String productId;
	String itemStock;
	
	public String getItemStock() {
		return itemStock;
	}
	public void setItemStock(String itemStock) {
		this.itemStock = itemStock;
	}
 
	List<SizeAndColor> sizeColor;
	List<SizeAndColor> colors;
	
	public List<SizeAndColor> getColors() {
		return colors;
	}
	public void setColors(List<SizeAndColor> colors) {
		this.colors = colors;
	}
	public List<SizeAndColor> getSizeColor() {
		return sizeColor;
	}
	public void setSizeColor(List<SizeAndColor> sizeColor) {
		this.sizeColor = sizeColor;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	} 
	public String getMetarialDesc() {
		return metarialDesc;
	}
	public void setMetarialDesc(String metarialDesc) {
		this.metarialDesc = metarialDesc;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDesignName() {
		return designName;
	}
	public void setDesignName(String designName) {
		this.designName = designName;
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
	public String getShape() {
		return shape;
	}
	public void setShape(String shape) {
		this.shape = shape;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDiscountPrice() {
		return discountPrice;
	}
	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}
	public String getSuitPrice() {
		return suitPrice;
	}
	public void setSuitPrice(String suitPrice) {
		this.suitPrice = suitPrice;
	}
	public String getSupplyId() {
		return supplyId;
	}  
	public void setSupplyId(String supplyId) {
		this.supplyId = supplyId;
	}
	public String getSupplyName() {
		return supplyName;
	} 
	public void setSupplyName(String supplyName) {
		this.supplyName = supplyName;
	}
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	public String getStockStatus() {
		return stockStatus;
	}
	public void setStockStatus(String stockStatus) {
		this.stockStatus = stockStatus;
	}
	public String getStockCount() {
		return stockCount;
	}
	public void setStockCount(String stockCount) {
		this.stockCount = stockCount;
	}
	public String getSoldCount() {
		return soldCount;
	}
	public void setSoldCount(String soldCount) {
		this.soldCount = soldCount;
	}
	public String getAgeType() {
		return ageType;
	}
	public void setAgeType(String ageType) {
		this.ageType = ageType;
	}
	public String getAgeBegin() {
		return ageBegin;
	}
	public void setAgeBegin(String ageBegin) {
		this.ageBegin = ageBegin;
	}
	public String getAgeEnd() {
		return ageEnd;
	}
	public void setAgeEnd(String ageEnd) {
		this.ageEnd = ageEnd;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getBustBegin() {
		return bustBegin;
	}
	public void setBustBegin(String bustBegin) {
		this.bustBegin = bustBegin;
	}
	public String getBustEnd() {
		return bustEnd;
	}
	public void setBustEnd(String bustEnd) {
		this.bustEnd = bustEnd;
	}
	public String getWaistBegin() {
		return waistBegin;
	}
	public void setWaistBegin(String waistBegin) {
		this.waistBegin = waistBegin;
	}
	public String getWaistEnd() {
		return waistEnd;
	}
	public void setWaistEnd(String waistEnd) {
		this.waistEnd = waistEnd;
	}
	public String getHipBegin() {
		return hipBegin;
	}
	public void setHipBegin(String hipBegin) {
		this.hipBegin = hipBegin;
	}
	public String getHipEnd() {
		return hipEnd;
	}
	public void setHipEnd(String hipEnd) {
		this.hipEnd = hipEnd;
	}
	public String getYardBegin() {
		return yardBegin;
	}
	public void setYardBegin(String yardBegin) {
		this.yardBegin = yardBegin;
	}
	public String getYardEnd() {
		return yardEnd;
	}
	public void setYardEnd(String yardEnd) {
		this.yardEnd = yardEnd;
	}
	public String getImgFront() {
		return imgFront;
	}
	public void setImgFront(String imgFront) {
		this.imgFront = imgFront;
	}
	public String getImgBehind() {
		return imgBehind;
	}
	public void setImgBehind(String imgBehind) {
		this.imgBehind = imgBehind;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<ProductItemObject> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(ArrayList<ProductItemObject> goodsList) {
		this.goodsList = goodsList;
	}

	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}

	String type;
	String name;
	String category;
	String desc;
	String metarialDesc;
	String color;
	String designName;
	String scene;
	String style;
	String shape;
	String endTime;
	String price;
	String discountPrice;
	String suitPrice;
	String supplyId;
	String supplyName;
	String regionId;
	String stockStatus;
	String stockCount;
	String soldCount;
	String ageType;
	String ageBegin;
	String ageEnd;
	String size;
	String bustBegin;
	String bustEnd;
	String waistBegin;
	String waistEnd;
	String hipBegin;
	String hipEnd;
	String yardBegin;
	String yardEnd;
	String imgFront;
	String imgBehind;
	String images;
	String CreateTime;
	String status;
	String pid;
	String brand;
	String washingType;
	String designerId;
	String sex;
	String impression;
	String bodySuit;
	String notSuitSkin;
	String faceSuitColor;
	String categoryId;
	String not_suit_nature;
	String body_notsuit;
	
	
	
	
	public String getNot_suit_nature() {
		return not_suit_nature;
	}
	public void setNot_suit_nature(String not_suit_nature) {
		this.not_suit_nature = not_suit_nature;
	}
	public String getBody_notsuit() {
		return body_notsuit;
	}
	public void setBody_notsuit(String body_notsuit) {
		this.body_notsuit = body_notsuit;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getImpression() {
		return impression;
	}
	public void setImpression(String impression) {
		this.impression = impression;
	}
	public String getBodySuit() {
		return bodySuit;
	}
	public void setBodySuit(String bodySuit) {
		this.bodySuit = bodySuit;
	}
	public String getNotSuitSkin() {
		return notSuitSkin;
	}
	public void setNotSuitSkin(String notSuitSkin) {
		this.notSuitSkin = notSuitSkin;
	}
	public String getFaceSuitColor() {
		return faceSuitColor;
	}
	public void setFaceSuitColor(String faceSuitColor) {
		this.faceSuitColor = faceSuitColor;
	}
	public String getDesignerId() {
		return designerId;
	}
	public void setDesignerId(String designerId) {
		this.designerId = designerId;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getWashingType() {
		return washingType;
	}
	public void setWashingType(String washingType) {
		this.washingType = washingType;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

	ArrayList<ProductItemObject> goodsList;
	
	ArrayList<String> imageList;
	public ArrayList<String> getImageList() {
		return imageList;
	}
	public void setImageList(ArrayList<String> imageList) {
		this.imageList = imageList;
	}
	private String goodDes;
	public String getGoodDes() {
		return goodDes;
	}
	public void setGoodDes(String goodDes) {
		this.goodDes = goodDes;
	}
	

}
