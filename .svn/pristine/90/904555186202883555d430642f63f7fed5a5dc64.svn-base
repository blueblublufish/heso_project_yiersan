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
import com.heso.service.order.refund.RefundOrder;
import com.heso.service.order.refund.entity.RefundOrderObject;
import com.heso.service.order.refund.entity.RefundOrderReturnObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 生成退货订单
 * 
 * @author xujun
 * 
 */
public class Transaction_001028 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001028.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			RefundOrderObject roo = new RefundOrderObject();
			
			String account = xmlBody.selectSingleNode("account").getText();
			roo.setAccount(account);
			roo.setOrderId(xmlBody.selectSingleNode("orderId").getText());
			roo.setProductId(xmlBody.selectSingleNode("productId").getText());
			roo.setType(xmlBody.selectSingleNode("type").getText());
			roo.setComment(xmlBody.selectSingleNode("comment").getText());
			roo.setImages(xmlBody.selectSingleNode("Images").getText());
			roo.setCount(xmlBody.selectSingleNode("count").getText());
			roo.setBankName(xmlBody.selectSingleNode("bankName").getText());
			roo.setBankUserName(xmlBody.selectSingleNode("bankUserName").getText());
			roo.setBankCareNo(xmlBody.selectSingleNode("bankCareNo").getText());
			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			RefundOrderReturnObject csro = new RefundOrder().generate(roo);

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			String xmlBodyStr = super.buildResp(csro.getCode(), sb.toString());
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
		sb.append("<type>001021</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000002</account>");
		sb.append("<token>0</token>");
		sb.append("<productIds>");
		sb.append("<productId>16SP0001</productId>");
		sb.append("</productIds>");
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
