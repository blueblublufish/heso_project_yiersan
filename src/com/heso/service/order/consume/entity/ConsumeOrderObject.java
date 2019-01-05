package com.heso.service.order.consume.entity;

import java.util.ArrayList;

public class ConsumeOrderObject {
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId; 
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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getInnerCoin() {
		return innerCoin;
	}

	public void setInnerCoin(String innerCoin) {
		this.innerCoin = innerCoin;
	}

	public String getBonusPoint() {
		return bonusPoint;
	}

	public void setBonusPoint(String bonusPoint) {
		this.bonusPoint = bonusPoint;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(String receiveId) {
		this.receiveId = receiveId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<ConsumeProductObject> getCpoList() {
		return cpoList;
	}

	public void setCpoList(ArrayList<ConsumeProductObject> cpoList) {
		this.cpoList = cpoList;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	
	public String getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(String discountPrice) {
		this.discountPrice = discountPrice;
	}
	
	public String getSuitId() {
		return suitId;
	}

	public void setSuitId(String suitId) {
		this.suitId = suitId;
	}
	
	public String getLogistCom() {
		return logistCom;
	}

	public void setLogistCom(String logistCom) {
		this.logistCom = logistCom;
	}

	public String getTrackingNum() {
		return trackingNum;
	}

	public void setTrackingNum(String trackingNum) {
		this.trackingNum = trackingNum;
	}

	String account;
	String orderId;
	String createTime;
	String productId;
	String type;
	String name;
	String image;
	String price;
	String price_cost;
	String price_cost_sum;	
	String biaozhunhaochicun;
	String liangti;
	
	
	public String getBiaozhunhaochicun() {
		return biaozhunhaochicun;
	}

	public void setBiaozhunhaochicun(String biaozhunhaochicun) {
		this.biaozhunhaochicun = biaozhunhaochicun;
	}

	public String getLiangti() {
		return liangti;
	}

	public void setLiangti(String liangti) {
		this.liangti = liangti;
	}

	public String getPrice_cost() {
		return price_cost;
	}

	public void setPrice_cost(String price_cost) {
		this.price_cost = price_cost;
	}

	public String getPrice_cost_sum() {
		return price_cost_sum;
	}

	public void setPrice_cost_sum(String price_cost_sum) {
		this.price_cost_sum = price_cost_sum;
	}

	String count;
	String amount;
	String currency;
	String innerCoin;
	String bonusPoint;
	String payTime;
	String payStatus;
	String sendStatus;
	String returnStatus;
	String receiveId;
	String color;
	String size;
	String paymentTerms;
	String discountPrice;
	String suitId;
	String logistCom;
	String trackingNum;
	String subordinate;
	String showStatus;
	String yuanchangId;
	public String getYuanchangId() {
		return yuanchangId;
	}

	public void setYuanchangId(String yuanchangId) {
		this.yuanchangId = yuanchangId;
	}

	String seller;//经手人
	String channelType;//下单渠道，0为原线上，1为线下
	String formatType;//版式
	String sizeType;//衣码
	String clothType;//布料
	public String getPinlei() {
		return pinlei;
	} 
	public void setPinlei(String pinlei) {
		this.pinlei = pinlei;
	}

	String pinlei;//阿玛尼品类
	String styleType;//风格
	String remark;//客户备注
	String detailRemark;
	String sex;//
	String category;
	String xizhuangwStyle;
	String changduStyle;
	String weidu;
	String xikuwStyle;
	String season;
	String gongyi;
	String dianjian;
	String zuodianjian;
	String youdianjian ;
	String suoyanStyle;
	String suoyanColor;
	String xiumokouColor;
	String xizhuangkou;
	String xikukou;
	String chenyikou;
	String nvchima;
	String nvkuchima;
	String sellerName;//客户跟进人
	String qudaojingliId;
	String qudaojingli;
	String teacher;
	String teacherId;
	String qudao;
	String qudaoId;
	String gongyiCn;
	String chima;
	String biaozhunhaoxing;
	String xiadanfangshi;
	String recivePhone;
	String reciceAddress;
	String wuliugongsi;
	String wuliuhao;
	String reciveName;	

	
	public String getRecivePhone() {
		return recivePhone;
	}

	public void setRecivePhone(String recivePhone) {
		this.recivePhone = recivePhone;
	}

	public String getReciceAddress() { 
		return reciceAddress;
	}

	public void setReciceAddress(String reciceAddress) {
		this.reciceAddress = reciceAddress;
	}

	public String getWuliugongsi() {
		return wuliugongsi;
	}

	public void setWuliugongsi(String wuliugongsi) {
		this.wuliugongsi = wuliugongsi;
	}

	public String getWuliuhao() {
		return wuliuhao;
	}

	public void setWuliuhao(String wuliuhao) {
		this.wuliuhao = wuliuhao;
	}

	public String getReciveName() {
		return reciveName;
	}

	public void setReciveName(String reciveName) {
		this.reciveName = reciveName;
	}

	public String getDetailRemark() {
		return detailRemark;
	}

	public void setDetailRemark(String detailRemark) {
		this.detailRemark = detailRemark;
	}

	public String getXiadanfangshi() {
		return xiadanfangshi;
	}

	public void setXiadanfangshi(String xiadanfangshi) {
		this.xiadanfangshi = xiadanfangshi;
	}

	public String getGongyiCn() {
		return gongyiCn;
	}

	public void setGongyiCn(String gongyiCn) {
		this.gongyiCn = gongyiCn;
	}

	public String getChima() {
		return chima;
	}

	public void setChima(String chima) {
		this.chima = chima;
	}

	public String getBiaozhunhaoxing() {
		return biaozhunhaoxing;
	}

	public void setBiaozhunhaoxing(String biaozhunhaoxing) {
		this.biaozhunhaoxing = biaozhunhaoxing;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getQudaojingliId() {
		return qudaojingliId;
	}

	public void setQudaojingliId(String qudaojingliId) {
		this.qudaojingliId = qudaojingliId;
	}

	public String getQudaojingli() {
		return qudaojingli;
	}

	public void setQudaojingli(String qudaojingli) {
		this.qudaojingli = qudaojingli;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public String getQudao() {
		return qudao;
	}

	public void setQudao(String qudao) {
		this.qudao = qudao;
	}

	public String getQudaoId() {
		return qudaoId;
	}

	public void setQudaoId(String qudaoId) {
		this.qudaoId = qudaoId;
	}

	public String getWeidu() {
		return weidu;
	}

	public void setWeidu(String weidu) {
		this.weidu = weidu;
	}

	public String getNvkuchima() {
		return nvkuchima;
	}

	public void setNvkuchima(String nvkuchima) {
		this.nvkuchima = nvkuchima;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getXizhuangwStyle() {
		return xizhuangwStyle;
	}

	public void setXizhuangwStyle(String xizhuangwStyle) {
		this.xizhuangwStyle = xizhuangwStyle;
	}

	public String getChangduStyle() {
		return changduStyle;
	}

	public void setChangduStyle(String changduStyle) {
		this.changduStyle = changduStyle;
	}

	public String getXikuwStyle() {
		return xikuwStyle;
	}

	public void setXikuwStyle(String xikuwStyle) {
		this.xikuwStyle = xikuwStyle;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getGongyi() {
		return gongyi;
	}

	public void setGongyi(String gongyi) {
		this.gongyi = gongyi;
	}

	public String getDianjian() {
		return dianjian;
	}

	public void setDianjian(String dianjian) {
		this.dianjian = dianjian;
	}

	public String getZuodianjian() {
		return zuodianjian;
	}

	public void setZuodianjian(String zuodianjian) {
		this.zuodianjian = zuodianjian;
	}

	public String getYoudianjian() {
		return youdianjian;
	}

	public void setYoudianjian(String youdianjian) {
		this.youdianjian = youdianjian;
	}

	public String getSuoyanStyle() {
		return suoyanStyle;
	}

	public void setSuoyanStyle(String suoyanStyle) {
		this.suoyanStyle = suoyanStyle;
	}

	public String getSuoyanColor() {
		return suoyanColor;
	}

	public void setSuoyanColor(String suoyanColor) {
		this.suoyanColor = suoyanColor;
	}

	public String getXiumokouColor() {
		return xiumokouColor;
	}

	public void setXiumokouColor(String xiumokouColor) {
		this.xiumokouColor = xiumokouColor;
	}

	public String getXizhuangkou() {
		return xizhuangkou;
	}

	public void setXizhuangkou(String xizhuangkou) {
		this.xizhuangkou = xizhuangkou;
	}

	public String getXikukou() {
		return xikukou;
	}

	public void setXikukou(String xikukou) {
		this.xikukou = xikukou;
	}

	public String getChenyikou() {
		return chenyikou;
	}

	public void setChenyikou(String chenyikou) {
		this.chenyikou = chenyikou;
	}

	public String getNvchima() {
		return nvchima;
	}

	public void setNvchima(String nvchima) {
		this.nvchima = nvchima;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getRemark() {
		return remark;
	}
  
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	String remarks;//商家备注
	
	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public String getSizeType() {
		return sizeType;
	}

	public void setSizeType(String sizeType) {
		this.sizeType = sizeType;
	}

	public String getClothType() {
		return clothType;
	}

	public void setClothType(String clothType) {
		this.clothType = clothType;
	}

	public String getStyleType() {
		return styleType;
	}

	public void setStyleType(String styleType) {
		this.styleType = styleType;
	}

	public String getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
	}

	public String getSubordinate() {
		return subordinate;
	}

	public void setSubordinate(String subordinate) {
		this.subordinate = subordinate;
	}

	ArrayList<ConsumeProductObject> cpoList;
	ArrayList<ConsumeOrderObject> cooList;
	public ArrayList<ConsumeOrderObject> getCooList() {
		return cooList;
	}

	public void setCooList(ArrayList<ConsumeOrderObject> cooList) {
		this.cooList = cooList;
	}
  
	private String pointType;
	private String sendTime;
	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

}
