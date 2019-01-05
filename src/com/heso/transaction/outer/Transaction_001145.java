package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import oracle.sql.ARRAY;

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
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.QaTestAndResult;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.CheckRecord;
import com.heso.service.wardrobe.entity.CheckRecordDetail;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询审核列表
 * @author Administrator
 *
 */
public class Transaction_001145 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001145.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			String orderId = xmlBody.selectSingleNode("orderId").getText();
 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			List<CheckRecord> list= new WardrobeService().checkYichuBiaoqianOrder(account, orderId);
 		 
 			for(CheckRecord cRecord : list){
 				sb.append("<checkRecord>");
 	 			 
 				sb.append("<id>"+ cRecord.getId()+"</id>");
 				sb.append("<name>"+ cRecord.getName() +"</name>");
 				sb.append("<status>"+ cRecord.getStatus() +"</status>");
 				sb.append("<account>"+ cRecord.getAccount() +"</account>");
 				sb.append("<recordType>"+ cRecord.getRecordType() +"</recordType>");
 				sb.append("<image>"+ cRecord.getIamge() +"</image>");
 				sb.append("<count>"+ cRecord.getCount() +"</count>");
 				sb.append("<price>"+ cRecord.getPrice() +"</price>");
 				sb.append("<amount>"+ cRecord.getAmount() +"</amount>");
 				sb.append("<creatime>"+ cRecord.getCreatime() +"</creatime>");
 				List<CheckRecordDetail> crdList = cRecord.getCrdList();
 				for(CheckRecordDetail crd:crdList){
 					sb.append("<checkRecordDetail>");
 				 
 					sb.append("<id>"+ crd.getId()+"</id>");
 					sb.append("<recordId>"+ crd.getRecordId() +"</recordId>");
 					sb.append("<wardrobeOrderId>"+ crd.getWardrobeOrderId() +"</wardrobeOrderId>");
 					sb.append("<image>"+ crd.getImage() +"</image>");
 					sb.append("<name>"+ crd.getName() +"</name>");
 					sb.append("<creatime>"+ crd.getCreatime() +"</creatime>");
 					sb.append("<recordType>"+ crd.getRecordType() +"</recordType>");
 					sb.append("<account>"+ crd.getAccount() +"</account>");
 					sb.append("<price>"+ crd.getPrice() +"</price>");

 					sb.append("</checkRecordDetail>");
 					
 					
 				}
 				sb.append("</checkRecord>");
 				
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001140</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><token>daad6f3f5be7e5a5838ccbcb8cab3f94</token><orderIds>0000000000004690</orderIds></body></message>");
 		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001145</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		 
		sb.append("<account>0000000000000964</account>");
		sb.append("<orderId></orderId>");
		sb.append("<token>0</token>");	
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ss.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
