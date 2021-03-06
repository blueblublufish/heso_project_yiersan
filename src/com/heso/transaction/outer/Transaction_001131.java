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
 * 查询专家课程
 * @author Administrator
 *
 */
public class Transaction_001131 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001131.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{    
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String classId = xmlBody.selectSingleNode("classId").getText();
			String type = xmlBody.selectSingleNode("type").getText();
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
			ArticleAndvideoREturnObject aavro = new ArticleService().getFreeVideo(classId,type);
			if(aavro.getCode().equals("000000")){
				if(aavro.getAcDtoList()!=null&&aavro.getAcDtoList().size()!=0){
					for(ArticleCommentDTO dto:aavro.getAcDtoList()){
						sb.append("<videoItem>");
						sb.append("<designerName>"+dto.getDesignerName()+"</designerName>");
						sb.append("<id>"+dto.getvideoId()+"</id>");
						sb.append("<videonName>"+dto.getvideonName()+"</videonName>");
						sb.append("<videoSrc>"+dto.getvideoSrc()+"</videoSrc>");
						sb.append("<like>"+dto.getLike()+"</like>"); 
						sb.append("<videoLength>"+dto.getvideoLength()+"</videoLength>");
						sb.append("<videoImage>"+dto.getvideoImage()+"</videoImage>");
						sb.append("<image>"+dto.getImage()+"</image>");
						sb.append("<createTime>"+dto.getDcreateTime()+"</createTime>");
						sb.append("<designerId>"+dto.getAdminId()+"</designerId>");
						sb.append("<count>"+dto.getCount()+"</count>");
						sb.append("<admindesc>"+dto.getAdminDesc()+"</admindesc>");
						sb.append("<imgdesc>"+dto.getImgdesc()+"</imgdesc>");
						sb.append("<desc>"+dto.getDesc()+"</desc>");
						sb.append("<flag>"+dto.getFlag()+"</flag>");
						sb.append("<yugao>"+dto.getIsYugao()+"</yugao>");
						sb.append("<belongService>"+dto.getBelongService()+"</belongService>");
						sb.append("<yuedu>"+dto.getRead()+"</yuedu>");
						sb.append("</videoItem>");
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
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001123</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001131</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<classId></classId>");
		sb.append("<type>0</type>");
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
