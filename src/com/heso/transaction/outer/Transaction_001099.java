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
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 *	根据外订单号获取详情
 * @author Administrator
 *
 */
public class Transaction_001099 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001099.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String waiorderId = xmlBody.selectSingleNode("waiOrderId").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			ConsumeOrderReturnObject coro = new ConsumeOrder().getDetailsByWaiorder(waiorderId);
 			if(coro.getCode().equals("000000")){
				for(ConsumeOrderObject coo:coro.getCooList()){
					sb.append("<userName>"+coo.getAccount()+"</userName>");
					sb.append("<sex>"+coo.getSex()+"</sex>");
					sb.append("<payType>"+coo.getPaymentTerms()+"</payType>");   
					sb.append("<payStatus>"+coo.getPayStatus()+"</payStatus>");
					sb.append("<amount>"+coo.getAmount()+"</amount>");
					sb.append("<payTime>"+coo.getPayTime()+"</payTime>");
					sb.append("<waiOrdrId>"+waiorderId+"</waiOrdrId>");
					sb.append("<createTime>"+coo.getCreateTime()+"</createTime>");
					for(ConsumeOrderObject cooo:coo.getCooList()){
						sb.append("<orderItem>");
						sb.append("<orderId>"+cooo.getOrderId()+"</orderId>");
						sb.append("<orderPayType>"+cooo.getPaymentTerms()+"</orderPayType>");
						sb.append("<type>"+cooo.getType()+"</type>");
						sb.append("<orderAmount>"+cooo.getAmount()+"</orderAmount>");
						sb.append("<orderPayStatus>"+cooo.getPayStatus()+"</orderPayStatus>");
						sb.append("<proSex>"+cooo.getSex()+"</proSex>");
						sb.append("<proName>"+cooo.getName()+"</proName>");
						sb.append("<productId>"+cooo.getProductId()+"</productId>");
						sb.append("<designerName>"+cooo.getSeller()+"</designerName>");
						sb.append("</orderItem>");
					}
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001099</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><waiOrderId>0000000000005376</waiOrderId><account>0000000000000909</account><token>c8e1356a4f7368d31eee219d3793c819</token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001099</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<waiOrderId>0000000000005290</waiOrderId>");
 		sb.append("<account>0000000000001944</account>");
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
