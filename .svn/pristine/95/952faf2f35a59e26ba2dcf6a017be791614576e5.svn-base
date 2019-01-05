package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 首单优惠
 * @author Administrator
 *
 */
public class Transaction_001910 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001910.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			String amount = xmlBody.selectSingleNode("amount").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String orderId = "";
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			//处理数据
			ConsumeOrderReturnObject coro = new ConsumeOrder().firstPay(amount, account , orderId);
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			sb.append("<amount>"+coro.getReccount()+"</amount>");
			sb.append("<discount>"+coro.getDiscount()+"</discount>");
			String xmlBodyStr = super.buildResp(coro.getCode(), sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
