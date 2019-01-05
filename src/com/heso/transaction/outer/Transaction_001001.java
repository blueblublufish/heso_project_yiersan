package com.heso.transaction.outer;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;
/**发送验证码
 * @author xujun
 *
 */
public class Transaction_001001 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001001.class);
	
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			//取出数据
			String mobile= xmlBody.selectSingleNode("mobile").getText();

			String authCode = StringTools.randomNumberString(6);
			//new SmsService().sendMessage(authCode, mobile ,SmsService.SMS_TYPE_AUTH_CODE);
			new SmsService().sendMessageByaliyun(authCode, mobile ,SmsService.SMS_TYPE_AUTH_CODE);
 			//设置缓存
			GlobalCache gc = GlobalCache.getInstance();
			gc.setValue(GlobalCache.USER_MOBILE_AUTHCODE, mobile, authCode);
			System.out.println("手机验证码:" +authCode );
			 
			//设置返回数据
			StringBuffer sb = new StringBuffer();
			//打桩
			String xmlBodyStr = super.buildResp("000000", sb.toString());
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
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001001</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<mobile>17620718804</mobile>");
		
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
