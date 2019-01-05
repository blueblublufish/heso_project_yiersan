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
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询标签订单
 * @author Administrator
 *
 */
public class Transaction_001143 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001143.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String teacherId = xmlBody.selectSingleNode("teacherId").getText();
			String paidanId = xmlBody.selectSingleNode("paidanId").getText();
			String type = xmlBody.selectSingleNode("type").getText();

 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			List<SendOrder> sendOrders= new ConsumeOrder().checkBiaoqianOrder(teacherId, paidanId,type);
 			for(SendOrder sendOrder : sendOrders){
 				sb.append("<sendOrder>");
 			 
 				sb.append("<id>"+ sendOrder.getId()+"</id>");
 				sb.append("<productId>"+ sendOrder.getProductId() +"</productId>");
 				sb.append("<teacherId>"+ sendOrder.getTeacher_id() +"</teacherId>");
 				sb.append("<status>"+ sendOrder.getStatus() +"</status>");
 				sb.append("<belong>"+ sendOrder.getBelong() +"</belong>");
 				sb.append("</sendOrder>");
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001118</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><orderId>0000000000004753</orderId><account>0000000000000948</account><pinlei>3000</pinlei><producId>D18TZ0261</producId><sex>1</sex></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001139</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		 
		sb.append("<account>0000000000000964</account>");
		 
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
