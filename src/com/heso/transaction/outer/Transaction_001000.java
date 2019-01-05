package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * �¶���ע���ͽ�ң�����
 * @author qinjianzhao
 *
 */
public class Transaction_001000 extends AbstractInterfaceClass {

		private static final Log logger = LogFactory.getLog(Transaction_001000.class);
		
		@Override
		public String execute(Node xmlBody, String IPAddress) {
			try {
				//ȡ������ 
				String userId= xmlBody.selectSingleNode("userId").getText();
				String password = xmlBody.selectSingleNode("password").getText();
				String userType = xmlBody.selectSingleNode("userType").getText();
				String mobile = xmlBody.selectSingleNode("mobile").getText();
				String  mobileCode = xmlBody.selectSingleNode("mobileCode").getText();
				//String sex = xmlBody.selectSingleNode("sex").getText();
				//ע�����ͣ�0-ԭע��ҳ�棻1-������ȯʱ��ϵͳ�Զ�ע��
				String  registerType = xmlBody.selectSingleNode("registerType").getText();
				String openId  = xmlBody.selectSingleNode("openid").getText();
				String channel = xmlBody.selectSingleNode("channel").getText();
				String sex = xmlBody.selectSingleNode("sex").getText();
				//����ע�ύ��
				UserServiceReturnObject usro = new YesUserService().registerByUsername(userId, password, userType ,mobile,mobileCode,registerType,openId,channel,sex);
				
				//���÷�������
				StringBuffer sb = new StringBuffer();
				if (usro.getCode().equals("000000")) {
					UserDataObject udo = usro.getUdo();
					sb.append("<account>" + usro.getAccount() + "</account>");
					sb.append("<token>" + usro.getToken() + "</token>");
					sb.append("<userId>" + usro.getProductCount() + "</userId>");
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
		
		public static void main(String[] args) {
			StringBuffer ssb = new StringBuffer();
			ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001000</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><userId>1503306141</userId><password>c51cd8e64b0aeb778364765013df9ebe</password><userType>1</userType><mobile>15014606141</mobile><mobileCode>274378</mobileCode><registerType>0</registerType><openId></openId></body></message>");
			try {
				new TransDataProcess().execute(ssb.toString());
				System.out.println(">>>>>>>>>>>>>>gg");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
