package com.heso.service.article.entity;

import java.sql.Date;
import java.util.List;

public class ArticleCommentDTO {
	private String articleId;//ÎÄÕÂID
	private String count;
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getAccount() {
		return account;
	} 
	public void setAccount(String account) {
		this.account = account;
	}
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getVideonName() {
		return videonName;
	}
	public void setVideonName(String videonName) {
		this.videonName = videonName;
	}
	public String getVideoSrc() {
		return videoSrc;
	}
	public void setVideoSrc(String videoSrc) {
		this.videoSrc = videoSrc;
	}
	public String getVideoLength() {
		return videoLength;
	}
	public void setVideoLength(String videoLength) {
		this.videoLength = videoLength;
	}
	public String getVideoImage() {
		return videoImage;
	}
	public void setVideoImage(String videoImage) {
		this.videoImage = videoImage;
	}
	private String comment;//ÄÚÈÝ
	private String account;
	private String goodCount;
	private Date createTime;
	private String readCount;
	private String accountName;
	private String price;
	private String commentId;
	private String isAddGood;
	
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public String getIsAddGood() {
		return isAddGood;
	}
	public void setIsAddGood(String isAddGood) {
		this.isAddGood = isAddGood;
	}
	public String getPrice() { 
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getAccountName() {
		return accountName;
	} 
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	} 
	private String videoId;//ÊÓÆµId

	 
	private String designerName ;
	private String videonName ;
	private String videoSrc ;
	private String like ;
	private String videoLength; 
	private String videoImage ;
	private String image ;
	private String dcreateTime ;
	private String adminId;
	private String isYugao;
	private String belongService;
	private String imgdesc;
	private String desc;
	private int read;
	private String adminDesc;
	private String baohan;
	private String productId;
	private String isBuy;
	private List<ArticleCommentDTO> dtolist;
	private String startime;
	
	public String getStartime() {
		return startime;
	}
	public void setStartime(String startime) {
		this.startime = startime;
	}
	public List<ArticleCommentDTO> getDtolist() {
		return dtolist;
	}
	public void setDtolist(List<ArticleCommentDTO> dtolist) {
		this.dtolist = dtolist;
	}
	public String getBaohan() {
		return baohan;
	}
	public void setBaohan(String baohan) {
		this.baohan = baohan;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getIsBuy() {
		return isBuy;
	}
	public void setIsBuy(String isBuy) {
		this.isBuy = isBuy;
	}
	public String getAdminDesc() {
		return adminDesc;
	}
	public void setAdminDesc(String adminDesc) {
		this.adminDesc = adminDesc;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	private String flag;
	
	public String getIsYugao() {
		return isYugao;
	}
	public void setIsYugao(String isYugao) {
		this.isYugao = isYugao;
	}
	public String getBelongService() {
		return belongService;
	}
	public void setBelongService(String belongService) {
		this.belongService = belongService;
	}
	public String getImgdesc() {
		return imgdesc;
	}
	public void setImgdesc(String imgdesc) {
		this.imgdesc = imgdesc;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public String getAdminId() {
		return adminId;
	}
	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}
	public String getDcreateTime() {
		return dcreateTime;
	}
	public void setDcreateTime(String dcreateTime) {
		this.dcreateTime = dcreateTime;
	}
	public String getvideoId() {
		return videoId;
	}
	public void setvideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getDesignerName() {
		return designerName;
	}
	public void setDesignerName(String designerName) {
		this.designerName = designerName;
	}
	public String getvideonName() {
		return videonName;
	}
	public void setvideonName(String videonName) {
		this.videonName = videonName;
	}
	public String getvideoSrc() {
		return videoSrc;
	}
	public void setvideoSrc(String videoSrc) {
		this.videoSrc = videoSrc;
	}
	public String getLike() {
		return like;
	}
	public void setLike(String like) {
		this.like = like;
	}
	public String getvideoLength() {
		return videoLength;
	}
	public void setvideoLength(String videoLength) {
		this.videoLength = videoLength;
	}
	public String getvideoImage() {
		return videoImage;
	}
	public void setvideoImage(String videoImage) {
		this.videoImage = videoImage;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getaccount() {
		return account;
	}
	public void setaccount(String account) {
		this.account = account;
	}
	public String getGoodCount() {
		return goodCount;
	}
	public void setGoodCount(String goodCount) {
		this.goodCount = goodCount;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getReadCount() {
		return readCount;
	}
	public void setReadCount(String readCount) {
		this.readCount = readCount;
	}
	 
}
 