package com.heso.transaction.outer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node; 

import com.heso.data.TransDataProcess;
import com.heso.service.account.YiErSanAccountService;
import com.heso.service.message.entity.MessageObject;
import com.heso.service.message.entity.MessageServiceReturnObject;
import com.heso.service.sms.YRSSms;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**用户登录
 * @author xujun
 *
 */
public class Transaction_001922 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001922.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try {

			// 取出数据
			String userId= xmlBody.selectSingleNode("userId").getText();
			String password = xmlBody.selectSingleNode("password").getText();
			String userType = xmlBody.selectSingleNode("userType").getText();
			String mobile = xmlBody.selectSingleNode("mobile").getText();
			String  mobileCode = xmlBody.selectSingleNode("mobileCode").getText();
			String openId = xmlBody.selectSingleNode("openId").getText();
			UserServiceReturnObject usro = new UserServiceReturnObject();
		 
				
				// 进行登陆交易
			usro = new YesUserService().login(userId, password, userType,mobile,mobileCode, IPAddress,openId);
		 

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (usro.getCode().equals("000000")) {
				sb.append("<account>" + usro.getAccount() + "</account>");
				sb.append("<token>" + usro.getToken() + "</token>");
				sb.append("<userId>" + usro.getProductCount() + "</userId>");
			}
			String xmlBodyStr = super.buildResp(usro.getCode(), sb.toString());
			return xmlBodyStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001922</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<userId>黄启团</userId>");
		sb.append("<phone>18613008888</phone>");
		sb.append("<password>0000000000004770</password>");
		sb.append("<userType>1</userType>");
		sb.append("<mobile>18613008888</mobile>");
		sb.append("<mobileCode>354248</mobileCode>");
		sb.append("<openId>in_0000000000001227</openId>");
		sb.append("<remarks>11</remarks>");
	 
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
