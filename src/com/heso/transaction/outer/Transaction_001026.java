package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 查询订单套装明细
 * 
 * @author xujun
 * 
 */
public class Transaction_001026 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001026.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String orderId = xmlBody.selectSingleNode("orderId").getText();

			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			ConsumeOrderReturnObject coro = new ConsumeOrder().getDetail(orderId);
			if (coro.getCode().equals("000000")) {
				for (ConsumeOrderObject coo : coro.getCooList()) {
					sb.append("<orderDetail>");
					sb.append("<orderId>" + coo.getOrderId() + "</orderId>");
					sb.append("<productId>" + coo.getProductId() + "</productId>");
					sb.append("<name>" + coo.getName() + "</name>");
					sb.append("<image>" + coo.getImage() + "</image>");
					sb.append("<count>" + coo.getCount() + "</count>");
					sb.append("<price>" + coo.getPrice() + "</price>");
					sb.append("<amount>" + coo.getAmount() + "</amount>");
					sb.append("<color>" + coo.getColor() + "</color>");
					sb.append("<size>" + coo.getSize() + "</size>");
					sb.append("</orderDetail>");
				}
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
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001026</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000002</account>");
		sb.append("<token>0</token>");
		sb.append("<orderId>0000000000024422</orderId>");
		sb.append("</body>");
		sb.append("</message>");

		try {
			new TransDataProcess().execute(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
