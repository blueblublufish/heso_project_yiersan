package com.heso.transaction.outer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node; 

import com.heso.service.account.YiErSanAccountService;
import com.heso.service.message.entity.MessageObject;
import com.heso.service.message.entity.MessageServiceReturnObject;
import com.heso.service.sms.YRSSms;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**用户openId登录
 * @author xujun
 *
 */
public class Transaction_001924 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001924.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try {

			// 取出数据
			 
			String openId = xmlBody.selectSingleNode("openId").getText();
			UserServiceReturnObject usro = new UserServiceReturnObject();
			
			usro = new YesUserService().loginByOpenId(openId, IPAddress);

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

}
