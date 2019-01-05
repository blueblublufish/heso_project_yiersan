package com.heso.service.mall.entity;

import java.util.List;

public class ProductsDTO {
	private String prodctId;
	private String stock;
	private String type;
	private String price;
	private String size;
	private String count;
	private String color;
	private String image;
	private String changdu;
	private String weidu;
	private String proName;
	private String gongyi;
	private String chima;
	private String chimaquyu; 
	private String gongyiCn;
	private String xiadangfangshi;
	private String pinlei;
	private String huiyuanPrice;
	private String remark;
	
	
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getHuiyuanPrice() {
		return huiyuanPrice;
	}
	public void setHuiyuanPrice(String huiyuanPrice) {
		this.huiyuanPrice = huiyuanPrice;
	}
	public String getPinlei() {
		return pinlei;
	}
	public void setPinlei(String pinlei) {
		this.pinlei = pinlei;
	}
	public String getGongyiCn() {
		return gongyiCn;
	}
	public void setGongyiCn(String gongyiCn) {
		this.gongyiCn = gongyiCn;
	}
	public String getXiadangfangshi() {
		return xiadangfangshi;
	}
	public void setXiadangfangshi(String xiadangfangshi) {
		this.xiadangfangshi = xiadangfangshi;
	}
	public String getGongyi() {
		return gongyi;
	}
	public void setGongyi(String gongyi) {
		this.gongyi = gongyi;
	}
	public String getChima() {
		return chima;
	}
	public void setChima(String chima) {
		this.chima = chima;
	}
	public String getChimaquyu() {
		return chimaquyu;
	}
	public void setChimaquyu(String chimaquyu) {
		this.chimaquyu = chimaquyu;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getChangdu() {
		return changdu;
	}
	public void setChangdu(String changdu) {
		this.changdu = changdu;
	}
	public String getWeidu() {
		return weidu;
	}
	public void setWeidu(String weidu) {
		this.weidu = weidu;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	private List<ProductDTO> productDTOs;
	private List<ProductsDTO> productsDTOs;
	
	public List<ProductsDTO> getProductsDTOs() {
		return productsDTOs;
	}
	public void setProductsDTOs(List<ProductsDTO> productsDTOs) {
		this.productsDTOs = productsDTOs;
	}
	public String getProdctId() {
		return prodctId;
	}
	public void setProdctId(String prodctId) {
		this.prodctId = prodctId;
	}
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getColor() {
		return color;
	} 
	public void setColor(String color) {
		this.color = color;
	}
	public List<ProductDTO> getProductDTOs() {
		return productDTOs;
	}
	public void setProductDTOs(List<ProductDTO> productDTOs) {
		this.productDTOs = productDTOs;
	}
	

}
