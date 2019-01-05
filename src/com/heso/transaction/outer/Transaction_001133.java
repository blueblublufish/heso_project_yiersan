package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 根据ID查询视频套餐
 * @author Administrator
 *
 */
public class Transaction_001133 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001133.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String videoId = xmlBody.selectSingleNode("videoId").getText();
			String account = xmlBody.selectSingleNode("account").getText();
  		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
			ArticleAndvideoREturnObject aavro = new ArticleService().getVideoById(videoId, account );
			if(aavro.getCode().equals("000000")){
				if(aavro.getAcDtoList()!=null&&aavro.getAcDtoList().size()!=0){
					for(ArticleCommentDTO dto:aavro.getAcDtoList()){
						sb.append("<videoItems>");
						 
						sb.append("<isBuy>"+dto.getIsBuy()+"</isBuy>");
						sb.append("<itemName>"+dto.getVideonName()+"</itemName>");
						sb.append("<image>"+dto.getVideoImage()+"</image>");
						sb.append("<itemDesc>"+dto.getDesc()+"</itemDesc>");
						sb.append("<tupianmianshu>"+dto.getImgdesc()+"</tupianmianshu>");
						sb.append("<startime>"+dto.getStartime()+"</startime>");
						sb.append("<price>"+dto.getPrice()+"</price>");
						sb.append("<supplyImage>"+dto.getImage()+"</supplyImage>");
						sb.append("<productId>"+dto.getProductId()+"</productId>");
						for(ArticleCommentDTO aDto : dto.getDtolist()){
							sb.append("<videoItem>");
							sb.append("<designerName>"+aDto.getDesignerName()+"</designerName>");
							sb.append("<id>"+aDto.getvideoId()+"</id>");
							sb.append("<videonName>"+aDto.getvideonName()+"</videonName>");
							sb.append("<videoSrc>"+aDto.getvideoSrc()+"</videoSrc>");
							sb.append("<like>"+aDto.getLike()+"</like>"); 
							sb.append("<videoLength>"+aDto.getvideoLength()+"</videoLength>");
							sb.append("<videoImage>"+aDto.getvideoImage()+"</videoImage>");
							sb.append("<image>"+aDto.getImage()+"</image>");
							sb.append("<createTime>"+aDto.getDcreateTime()+"</createTime>");
							sb.append("<designerId>"+aDto.getAdminId()+"</designerId>");
							sb.append("<count>"+aDto.getCount()+"</count>");
							sb.append("<admindesc>"+aDto.getAdminDesc()+"</admindesc>");
							sb.append("<imgdesc>"+aDto.getImgdesc()+"</imgdesc>");
							sb.append("<desc>"+aDto.getDesc()+"</desc>");
							sb.append("<flag>"+aDto.getFlag()+"</flag>");
							sb.append("<yugao>"+aDto.getIsYugao()+"</yugao>");
							sb.append("<belongService>"+aDto.getBelongService()+"</belongService>");
							sb.append("<yuedu>"+aDto.getRead()+"</yuedu>");
							sb.append("</videoItem>");
						}
						sb.append("</videoItems>");
					}
					}else {
						sb.append("<videoItem></videoItem>");
					} 
			}
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001133</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<videoId>40</videoId>");
		sb.append("<account>0000000000000909</account>");
		sb.append("<productId></productId>");
		sb.append("<token>0</token>");	
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(sb.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
