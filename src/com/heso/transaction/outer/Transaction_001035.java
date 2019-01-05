package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 *  更新支付状态
 * @author qinjianzhao
 *
 */
public class Transaction_001035 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001035.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String orderIds = arg0.selectSingleNode("orderIds").getText();
			String type = arg0.selectSingleNode("type").getText();
			String payTime = arg0.selectSingleNode("payTime").getText();
			//处理信息
			ConsumeOrderReturnObject coro = new ConsumeOrder().payFinish(type, orderIds, payTime);
			
			//返回信息
			StringBuffer data = new StringBuffer();
			String xmlStrBody = super.buildResp(coro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(); 
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001035</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<account>0000000000000909</account>");
		sb.append("<orderIds>0000000000004606</orderIds>");
		sb.append("<type>000000</type>");
		sb.append("<payTime>2018-2-27 04:54:00</payTime>");
		sb.append("<token>0</token>");
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
