package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.funds.order.entity.FundsOrderReturnObject;
import com.heso.service.funds.order.recharge.OrderRecharge;
import com.heso.service.funds.order.recharge.entity.OrderRechargeItem;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass; 

/**
 * 审核向阿玛尼下单
 * 
 * @author xujun
 * 
 */
public class Transaction_001120 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001120.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 			String orderID = xmlBody.selectSingleNode("orderId").getText();
 			String account = xmlBody.selectSingleNode("account").getText();
		//	String orderId = xmlBody.selectSingleNode("orderId").getText();
			String checkStatus = xmlBody.selectSingleNode("checkStatus").getText();
			String remarks = xmlBody.selectSingleNode("remarks").getText();
			 
 			String name =xmlBody.selectSingleNode("name").getText();
			String phone= xmlBody.selectSingleNode("phone").getText(); 
			 	 
			
		   // String string = new YesUserService().addBiaoZhun(productId, orderID, pinlei, account);
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
		/*	if (account == null)
				code = "900010";
			else { 
				code = "000000";
				sb.append("<account>"+account+"</account>");
			}
*/
			UserServiceReturnObject usro = new YesUserService().getUserData(account);
			String userId = usro.getUdo().getUser_id();
			String ss = new YesUserService().updateOrderComsume(orderID, checkStatus, remarks, account, userId, phone); 
			sb.append("<returnDesc>"+ss+"</returnDesc>");
			String xmlBodyStr = super.buildResp(code, sb.toString());
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
		StringBuffer ssBuffer = new StringBuffer();
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001020</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><productId>F18ZS0019</productId><suitId>F18ZS0019</suitId><count>1</count><color></color><size></size><selected>1</selected><subordinate></subordinate><token>22f183c6ef5cd73cc1ee8346139d</token><productDetail><productId>D17O0004</productId><size>均码</size><color>黑色</color></productDetail><productDetail><productId>F18YS056</productId><size>均码</size><color>默认</color></productDetail></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001120</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<name>ceshi</name>");
		sb.append("<phone>18334673124</phone>");
		sb.append("<orderId>0000000000005300</orderId>");
		sb.append("<checkStatus>1</checkStatus>");
		sb.append("<account>0000000000000909</account>");
		
		sb.append("<remarks></remarks>");
	 
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
