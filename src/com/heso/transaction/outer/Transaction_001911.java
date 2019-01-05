package com.heso.transaction.outer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.message.entity.MessageObject;
import com.heso.service.message.entity.MessageServiceReturnObject;
import com.heso.service.sms.YRSSms;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 用户信息管理：注册、登录、更新
 * @author Administrator
 *
 */
public class Transaction_001911 extends AbstractInterfaceClass{
	private static final Log logger = LogFactory.getLog(Transaction_000920.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String account = xmlBody.selectSingleNode("account").getText();
			Integer page = Integer.parseInt(xmlBody.selectSingleNode("page").getText());
			Integer pageSize = Integer.parseInt(xmlBody.selectSingleNode("pageSize").getText());
			String status = xmlBody.selectSingleNode("status").getText();
			//验证token
			String token = xmlBody.selectSingleNode("token").getText();
			if(!tokenAuth(account, token))
				throw new Exception(super.ERROR_TOKEN);
			//处理信息
			MessageServiceReturnObject msro = new YRSSms().readMessage(account, page, pageSize, status);
			//返回信息
			
			StringBuffer data = new StringBuffer();
			if("000000".equals(msro.getCode())){
				List<MessageObject> list  = msro.getMessages();
				data.append("<messages>");
				data.append("<count>"+list.get(0).getCount()+"</count>");
				for(int i = 1 ; i < list.size() ; i++){
					data.append("<listing>");
					data.append("<accountSource>"+list.get(i).getAccountSource()+"</accountSource>");
					data.append("<accountTarget>"+list.get(i).getAccountTarget()+"</accountTarget>");
					data.append("<createTime>"+list.get(i).getCreateTime()+"</createTime>");
					data.append("<message>"+list.get(i).getMessage()+"</message>");
					data.append("<userId>"+list.get(i).getSourceUserId()+"</userId>");
					data.append("<image>"+list.get(i).getImage()+"</image>");
					data.append("<status>"+list.get(i).getStatus()+"</status>");
					data.append("<messageType>"+list.get(i).getMessageType()+"</messageType>");
					data.append("</listing>");
				}
				data.append("</messages>");	
			}
			String xmlStrBody = super.buildResp(msro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return super.ERROR_RETURN;
	}
}
