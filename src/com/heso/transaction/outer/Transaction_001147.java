package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.comment.CommentService;
import com.heso.service.comment.entity.CommentServiceObject;
import com.heso.service.comment.entity.CommentServiceReturnObject;
import com.heso.service.ordershow.OrderShowService;
import com.heso.service.ordershow.entity.OrderShowObject;
import com.heso.service.ordershow.entity.OrderShowServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 评论点赞
 * 
 * @author xujun
 * 
 */
public class Transaction_001147 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001147.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			CommentServiceObject cso = new CommentServiceObject();
			String id= xmlBody.selectSingleNode("id").getText();
			String ip = xmlBody.selectSingleNode("ip").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			String videoId = xmlBody.selectSingleNode("videoId").getText();
 			CommentServiceReturnObject csro = new ArticleService().addGoodComment(id,ip,account,videoId);
			StringBuffer sb = new StringBuffer();
			String xmlBodyStr = super.buildResp(csro.getCode(), sb.toString());
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
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001147</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<id>373</id>");
		sb.append("<ip>1</ip>");
		sb.append("<account>0000000000000909</account>");
		sb.append("<videoId>17</videoId>");
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
