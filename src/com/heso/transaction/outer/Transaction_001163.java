package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 充值
 * @author qinjianzhao
 *
 */
public class Transaction_001163 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001163.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String money = arg0.selectSingleNode("money").getText().trim();
			String account = arg0.selectSingleNode("account").getText();
			//验证token
			String token = arg0.selectSingleNode("token").getText();
			if(!tokenAuth(account, token))
				throw new Exception(super.ERROR_TOKEN);
		 
			StringBuffer data = new StringBuffer();
			 
				ConsumeOrderReturnObject coro = new ConsumeOrder().addMoney(account, money);
				if("000000".equals(coro.getCode())){}
				
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
		StringBuffer ddBuffer = new StringBuffer();
		ddBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001093</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><token>23f0b3f75e67c4625e66035a961761</token><designerId></designerId><productId>114</productId><count>1</count><payType>1</payType><reciveId></reciveId><remark></remark><desigmerServices></desigmerServices><type>33</type></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001163</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId>3333341</designerId>");
		sb.append("<account>0000000000001944</account>");
		sb.append("<designerid></designerid>"); 
		sb.append("<money>24000</money>");
		sb.append("<productId>9</productId>");
		sb.append("<reciveId>671</reciveId>");
		sb.append("<remark>234</remark>");
		sb.append("<payType>1</payType>");
		sb.append("<desigmerServices>296</desigmerServices>");
		sb.append("<token>f943cad4c318af7528885f860c0a7f</token>");	
		sb.append("<type>0</type>");	
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
