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
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass; 

/**
 * 一键导入身材数据
 * 
 * @author xujun 
 *  
 */ 
public class Transaction_001118 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001118.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			String productId = xmlBody.selectSingleNode("producId").getText();
			String orderID = xmlBody.selectSingleNode("orderId").getText();
			String pinlei = xmlBody.selectSingleNode("pinlei").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			String sex = xmlBody.selectSingleNode("sex").getText();
			
			
		    String string = new YesUserService().profileToOrderDetail(productId, orderID, pinlei, account,sex);
			
			
			
			
			
			
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
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001118</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><orderId>0000000000004753</orderId><account>0000000000000948</account><pinlei>3000</pinlei><producId>D18TZ0261</producId><sex>1</sex></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001118</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<account>0000000000000918</account>");
		sb.append("<producId>W17T0099</producId>");
		sb.append("<pinlei>95000</pinlei>");
		sb.append("<orderId>0000000000004943</orderId>");
		sb.append("<sex>0</sex>");
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
