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
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询服饰订单含提成
 * @author Administrator
 *
 */
public class Transaction_001135 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001135.class);
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
			List<OrderTicheng> list = new ConsumeOrder().checkFushiOrder();
			
		 
			for (OrderTicheng qTicheng : list) {
				sb.append("<fushiOrder>");
				sb.append("<orderId>"+qTicheng.getOrderId()+"</orderId>");
				sb.append("<account>"+qTicheng.getAccount()+"</account>");
				sb.append("<createTime>"+qTicheng.getCreateTime()+"</createTime>");
				sb.append("<productId>"+qTicheng.getProductId()+"</productId>"); 
				sb.append("<type>"+qTicheng.getType()+"</type>");
				sb.append("<productName>"+qTicheng.getProductName()+"</productName>");
				sb.append("<image>"+qTicheng.getIamge()+"</image>");
				sb.append("<amount>"+qTicheng.getAmount()+"</amount>");
				sb.append("<payStatus>"+qTicheng.getPayStatus()+"</payStatus>");
				sb.append("<sendStatus>"+qTicheng.getSendStatus()+"</sendStatus>");
				sb.append("<tracking_num>"+qTicheng.getTracking_num()+"</tracking_num>");
				sb.append("<accountName>"+qTicheng.getAccountName()+"</accountName>");
				sb.append("<price_cost>"+qTicheng.getPrice_cost()+"</price_cost>");
				sb.append("<kehupingfen>"+qTicheng.getKehupingfen()+"</kehupingfen>");
				sb.append("<kehujianyi>"+qTicheng.getKehujianyi()+"</kehujianyi>");
				sb.append("<genjinrenName>"+qTicheng.getGenjinrenName()+"</genjinrenName>");
				sb.append("<qudaojingliName>"+qTicheng.getQudaojingliName()+"</qudaojingliName>");
				sb.append("<qudaoName>"+qTicheng.getQudaoName()+"</qudaoName>");
				sb.append("<teacherName>"+qTicheng.getTeacherName()+"</teacherName>");
				sb.append("<qudaojingliId>"+qTicheng.getQudaojingliId()+"</qudaojingliId>");
				sb.append("<qudaoId>"+qTicheng.getQudaoId()+"</qudaoId>");
				sb.append("<teacherId>"+qTicheng.getTeacherId()+"</teacherId>");
				sb.append("<genjinrenId>"+qTicheng.getGenjinrenId()+"</genjinrenId>");
				sb.append("<genjinrenTicheng>"+qTicheng.getGenjinrenTicheng()+"</genjinrenTicheng>");
				sb.append("<qudaoTicheng>"+qTicheng.getQudaoTicheng()+"</qudaoTicheng>");
				sb.append("<qudaojingliTicheng>"+qTicheng.getQudaojingliTicheng()+"</qudaojingliTicheng>");
				sb.append("<teacherTicheng>"+qTicheng.getTeacherTicheng()+"</teacherTicheng>");
				sb.append("</fushiOrder>");
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
		sb.append("<type>001135</type>");
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
