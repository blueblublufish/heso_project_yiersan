package com.heso.service.mall.entity;

import java.util.List;

/**
 * 服务DTO
 * @author win10
 *
 */
public class MemberProduct {
	private String id;
	private String name;//服务名
	private String desc;//服务描述
	private String designerIds;//提供该服务的设计师们
	private String unit;//单位
	private String place_n; 
	private String place_cn;
	private String zhuyishixiang;
	private String tupianmiaoshu;
	private String max_num;
	private String servicetype;
	private String flag;
	public String getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(String maxNum) {
		this.maxNum = maxNum;
	}
	private String bookNum;
	private String maxNum;
	 
	private List<MemberProduct> mList;
	public List<MemberProduct> getmList() {
		return mList;
	}
	public void setmList(List<MemberProduct> mList) {
		this.mList = mList;
	}
	public String getBookNum() {
		return bookNum;
	}
	public void setBookNum(String bookNum) {
		this.bookNum = bookNum;
	}
	public String getZhuyishixiang() {
		return zhuyishixiang;
	}
	public void setZhuyishixiang(String zhuyishixiang) {
		this.zhuyishixiang = zhuyishixiang;
	}
	public String getTupianmiaoshu() {
		return tupianmiaoshu;
	}
	public void setTupianmiaoshu(String tupianmiaoshu) {
		this.tupianmiaoshu = tupianmiaoshu;
	}
	public String getMax_num() {
		return max_num;
	}
	public void setMax_num(String max_num) {
		this.max_num = max_num;
	}
	public String getServicetype() {
		return servicetype;
	}
	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getPlace_n() {
		return place_n;
	}
	public void setPlace_n(String place_n) {
		this.place_n = place_n;
	}
	public String getPlace_cn() {
		return place_cn;
	}
	public void setPlace_cn(String place_cn) {
		this.place_cn = place_cn;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	private String designerId;//设计师ID
	private String price;//设计师价格
	private String designerName;//设计师名字
	private String designerDesc;//设计师描述
	private String designation;//设计师称号
	private String image;//设计师头像
	private String phone;//设计师联系方式
	private String url;//地址
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	private String place;//设计师行程地点
	private String status;//设计师当天预约状态
	private String account;//用户Id
	public String getServiceImage() { 
		return serviceImage;
	}
	public void setServiceImage(String serviceImage) {
		this.serviceImage = serviceImage;
	}
	private String date_place;//所处地点日期
	
	private String serviceImage;//服务大图
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDate_place() {
		return date_place;
	}
	public void setDate_place(String date_place) {
		this.date_place = date_place;
	}
	public String getDesignerId() {
		return designerId;
	}
	public void setDesignerId(String designerId) {
		this.designerId = designerId;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getDesignerName() {
		return designerName;
	}
	public void setDesignerName(String designerName) {
		this.designerName = designerName;
	}
	public String getDesignerDesc() {
		return designerDesc;
	}
	public void setDesignerDesc(String designerDesc) {
		this.designerDesc = designerDesc;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesignerIds() {
		return designerIds;
	}
	public void setDesignerIds(String designerIds) {
		this.designerIds = designerIds;
	}
	
}	
