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
 * 查询退货订单
 * 
 * @author xujun
 * 
 */
public class Transaction_001029 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001029.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String orderId = xmlBody.selectSingleNode("orderId").getText();
			Integer page = Integer.parseInt(xmlBody.selectSingleNode("page").getText());
			Integer  pageSize = Integer.parseInt(xmlBody.selectSingleNode("pageSize").getText());

			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			RefundOrderReturnObject roro = new RefundOrder().getList(account, orderId, page, pageSize);
			if (roro.getCode().equals("000000")) {
				sb.append("<count>"+roro.getRooList().get(0).getCount()+"</count>");
				for (int i = 1;i<roro.getRooList().size();i++) {
					sb.append("<orderItem>");
					sb.append("<orderId>" + roro.getRooList().get(i).getOrderId() + "</orderId>");
					sb.append("<type>" + roro.getRooList().get(i).getType() + "</type>");
					sb.append("<createTime>" + roro.getRooList().get(i).getCreateTime() + "</createTime>");
					sb.append("<productId>" + roro.getRooList().get(i).getProductId() + "</productId>");
					sb.append("<name>" + roro.getRooList().get(i).getName() + "</name>");
					sb.append("<amount>" + roro.getRooList().get(i).getAmount() + "</amount>");
					sb.append("<image>" + roro.getRooList().get(i).getImages() + "</image>");
					sb.append("<orgOrderId>" + roro.getRooList().get(i).getOrgOrderId() + "</orgOrderId>");
					sb.append("<comment>" + roro.getRooList().get(i).getComment() + "</comment>");
					sb.append("<status>" + roro.getRooList().get(i).getStatus() + "</status>");
					sb.append("<refuseReason>"+roro.getRooList().get(i).getRefuseReason()+"</refuseReason>");
					sb.append("</orderItem>");
				}
			}

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
		for(int i = 0; i < 100; i++)
		{
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001029</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000002</account>");
		sb.append("<token>0</token>");
	sb.append("<orderId></orderId>");
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
}
