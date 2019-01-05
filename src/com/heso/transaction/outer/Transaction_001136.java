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
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询服饰订单含提成
 * @author Administrator
 *
 */
public class Transaction_001136 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001136.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String designerId = xmlBody.selectSingleNode("designerId").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
			List<SendOrder> list = new ConsumeOrder().checkFuWuOrder();
			
			 
			for (SendOrder qTicheng : list) {
				sb.append("<fuwuOrder>");
				sb.append("<id>"+qTicheng.getId()+"</id>");
				sb.append("<teacherId>"+qTicheng.getTeacher_id()+"</teacherId>");
				sb.append("<teacherName>"+qTicheng.getTeacher_name()+"</teacherName>");
				sb.append("<zhuliAId>"+qTicheng.getZhuliA_id()+"</zhuliAId>"); 
				sb.append("<zhuliAName>"+qTicheng.getZhuliA_name()+"</zhuliAName>");
				sb.append("<zhuliBId>"+qTicheng.getZhuliB_id()+"</zhuliBId>");
				sb.append("<zhuliBName>"+qTicheng.getZhuliB_name()+"</zhuliBName>");
				sb.append("<serviceTime>"+qTicheng.getService_time()+"</serviceTime>");
				sb.append("<servicePlace>"+qTicheng.getSercive_place()+"</servicePlace>");
				sb.append("<serviceId>"+qTicheng.getService_id()+"</serviceId>");
				sb.append("<serviceName>"+qTicheng.getService_name()+"</serviceName>");
				sb.append("<accountName>"+qTicheng.getAccount_name()+"</accountName>");
				sb.append("<account>"+qTicheng.getAccount()+"</account>");
 				sb.append("<genjinrenId>"+qTicheng.getGenjinren_id()+"</genjinrenId>");
				sb.append("<genjinrenName>"+qTicheng.getGenjinren_name()+"</genjinrenName>");
				sb.append("<jinyibu>"+qTicheng.getJinyibu()+"</jinyibu>");
				sb.append("<status>"+qTicheng.getStatus()+"</status>");
				sb.append("<kehupingfen>"+qTicheng.getKehupingfen()+"</kehupingfen>");
				sb.append("<kehujianyi>"+qTicheng.getKehujianyi()+"</kehujianyi>");
				 
				sb.append("<zhuliATicheng>"+qTicheng.getZhuliA_ticheng()+"</zhuliATicheng>");
				sb.append("<zhuliBTicheng>"+qTicheng.getZhuliB_ticheng()+"</zhuliBTicheng>");
				sb.append("<teacherTicheng>"+qTicheng.getTeacher_ticheng()+"</teacherTicheng>");
				 
				sb.append("</fuwuOrder>");
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
		sb.append("<type>001136</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId></designerId>");
		sb.append("<account>0000000000000016</account>");
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
