package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.order.refund.RefundOrder;
import com.heso.service.order.refund.entity.RefundOrderObject;
import com.heso.service.order.refund.entity.RefundOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 申请退货
 * 
 * @author xujun
 * 
 */
public class Transaction_001031 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001031.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String orderId = xmlBody.selectSingleNode("orderId").getText();
//			String bankName = xmlBody.selectSingleNode("bankName").getText();
//			String bankUserName = xmlBody.selectSingleNode("bankUserName").getText();
//			String bankCareNo = xmlBody.selectSingleNode("bankCareNo").getText();


			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
//			RefundOrderReturnObject roro = new RefundOrder().execute(orderId,bankName,bankUserName,bankCareNo);
			RefundOrderReturnObject roro = new RefundOrder().execute(orderId);
			String xmlBodyStr = super.buildResp(roro.getCode(), sb.toString());
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
		sb.append("<type>001031</type>");
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
