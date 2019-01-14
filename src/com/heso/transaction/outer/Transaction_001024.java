package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 购物车结算
 * 
 * @author xujun
 * 
 */
public class Transaction_001024 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001024.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String innerCoin = xmlBody.selectSingleNode("innerCoin").getText();
			String receiveId =  xmlBody.selectSingleNode("receiveId").getText();
			String paymentTerms = xmlBody.selectSingleNode("paymentTerms").getText();
			String recommend = xmlBody.selectSingleNode("recommend").getText();
			String remarks = xmlBody.selectSingleNode("remark").getText();
			//增加优惠券优惠
			String couponDetId = xmlBody.selectSingleNode("couponDetId").getText();
			if(couponDetId.equals("none"))
				couponDetId = "";
			
			ConsumeOrderReturnObject coro = new CartService().settleNew(account, innerCoin, receiveId , paymentTerms,recommend,couponDetId,remarks);
			
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if("000000".equals(coro.getCode())){
				List<ConsumeOrderObject> list = coro.getCooList();
				for(int i = 0 ;i<list.size()-1;i++){
					sb.append("<orders>");
					sb.append("<amount>"+list.get(i).getAmount()+"</amount>");
					sb.append("<orderId>"+list.get(i).getOrderId()+"</orderId>");
					sb.append("</orders>");
				} 
				sb.append("<paymentTerms>"+list.get(0).getPaymentTerms()+"</paymentTerms>");
				sb.append("<waiOrder>"+coro.getOrderId()+"</waiOrder>");
				sb.append("<total>"+list.get(list.size()-1).getAmount()+"</total>");
			}
			String xmlBodyStr = super.buildResp(coro.getCode(), sb.toString());
			return xmlBodyStr;
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001024</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><innerCoin>0</innerCoin><receiveId>728</receiveId><paymentTerms>2</paymentTerms><recommend></recommend><remark></remark><couponDetId></couponDetId><account>0000000000001958</account><token>123456</token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001024</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<account>in_0000000000000946</account>");
		sb.append("<innerCoin>0</innerCoin>");
		sb.append("<receiveId>245</receiveId>");
		sb.append("<couponDetId>none</couponDetId>");
		sb.append("<paymentTerms>1</paymentTerms>");
		sb.append("<recommend></recommend>");
		sb.append("<token>0</token>");
		sb.append("</body>");
		sb.append("</message>");

		try {
			new TransDataProcess().execute(ssb.toString());
		} catch (Exception e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
