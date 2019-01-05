package com.heso.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.entity.XmlContentObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.MD5Util;
import com.heso.utility.StringTools;
import com.heso.utility.ZipUtils;

/**
 * @author xujun
 * 
 */
public class TransDataProcess {
	private static final Log logger = LogFactory.getLog(TransDataProcess.class);

	/**
	 * xml数据解包
	 * 
	 * @param xmlContent
	 * @throws Exception
	 */
	private XmlContentObject xmlParse(String xmlContent) throws Exception {
		try {
			// String message = new String(byteOutput.toByteArray(),
			// StringTools.ENCODED);
			// String Message = ZipUtils.gunzip(xmlContent);
			// Message = new
			// String(DESFactory.decrypt(DESFactory.hexStr2ByteArr(Message),
			// SystemConfig.getValue("package_private_key")),
			// StringTools.ENCODED);
			System.out.println("==接收xml数据:" + xmlContent);

			// 取得包体md5密文
			//System.out.println(xmlContent);
			String body = xmlContent.substring(xmlContent.indexOf("<body>") + 6, xmlContent.indexOf("</body>"));
			// body = MD5Util.getMD5String(body);

			Document doc = DocumentHelper.parseText(xmlContent);
			Element rootElt = doc.getRootElement(); // 获取根节点
			rootElt.selectSingleNode("/head/digest");
			String digest = rootElt.selectSingleNode("//head/digest").getText();
			String agentId = rootElt.selectSingleNode("//head/agentId").getText();
			String transType = rootElt.selectSingleNode("//head/type").getText();
			String msgId = rootElt.selectSingleNode("//head/messageId").getText();
			Node nodeBody = rootElt.selectSingleNode("body");

			// 解析出包结构
			XmlContentObject xco = new XmlContentObject();
			xco.setTransType(transType);
			xco.setAgentId(agentId);
			xco.setMsgId(msgId);
			xco.setBody(nodeBody);
			return xco;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * xml数据打包
	 * 
	 * @param xmlBody
	 * @return
	 */
	private String xmlPack(String TransType, String xmlBody) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>" + TransType + "</type>");
		sb.append("<messageId></messageId>");
		sb.append("<agentId></agentId>");
		sb.append("<digest></digest>");
		sb.append("</head>");
		sb.append(xmlBody);
		sb.append("</message>");
		System.out.println("==返回xml数据:" + sb.toString());
		return sb.toString();
	}	 

	public String execute(String requestData) {
		try {
			System.out.println("==接收包:" + requestData);
			XmlContentObject xco = new TransDataProcess().xmlParse(requestData);
			String transType = xco.getTransType();
			AbstractInterfaceClass interfaceObj = (AbstractInterfaceClass) Class.forName(AbstractInterfaceClass.TRANSACTION_PATH + "Transaction_" + transType).newInstance();
			String xmlBody = interfaceObj.execute(xco.getBody(), "127.0.0.1");
			String xmlContent = new TransDataProcess().xmlPack(transType, xmlBody);
			System.out.println(">>>>>"+xmlBody);
			System.out.println("==返回包:" + xmlContent);
			return xmlContent;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ""; 
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// GlobalCache.Initialize();

		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();

		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");

		sb.append("<head>");
		sb.append("<type>000014</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");

		sb.append("<body>");
		// sb.append("<userId>xujun6</userId>");
		// sb.append("<password>" + MD5Util.getMD5String("123456") +
		// "</password>");
		// sb.append("<userType>1</userType>");
		sb.append("<account>0000000000000002</account>");
		sb.append("<token>0000000000000002</token>");
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
