package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * ��Ƶ����
 * @author Administrator
 *
 */
public class Transaction_001095 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001095.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{ 
			//��ȡ���� 
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String videoId = xmlBody.selectSingleNode("videoId").getText();
			String comment = xmlBody.selectSingleNode("comment").getText();
			String userName = xmlBody.selectSingleNode("userName").getText();
			 
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//�ж�token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			new ArticleService().addCommentByvideoId(videoId, comment, userName, account);
			//��������
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			
			//��������
			StringBuffer sb = new StringBuffer();
			
		
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001095</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5����ǩ��</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<videoId>0</videoId>");
		sb.append("<userName>HH</userName>");
		sb.append("<comment>«έţ��</comment>");
		sb.append("<account>22d2dd</account>");
		sb.append("<token>0</token>");	
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
