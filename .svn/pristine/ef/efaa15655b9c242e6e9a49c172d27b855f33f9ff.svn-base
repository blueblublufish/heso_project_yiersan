package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**用户设置用户ID和密码
 * @author xujun
 *
 */
public class Transaction_001923 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001923.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String userId= xmlBody.selectSingleNode("userId").getText();
			String password = xmlBody.selectSingleNode("password").getText();

			// 进行登陆交易
			UserServiceReturnObject usro = new YesUserService().setUserDatas(account,userId,password);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (usro.getCode().equals("000000")) {
				sb.append("<account>" + usro.getAccount() + "</account>");
				sb.append("<token>" + usro.getToken() + "</token>");
				//GlobalCache.getInstance().setValue(GlobalCache.USER_TOKEN, usro.getAccount(), usro.getToken());
			}
			String xmlBodyStr = super.buildResp(usro.getCode(), sb.toString());
			return xmlBodyStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
