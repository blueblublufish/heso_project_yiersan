package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.data.entity.AmaniOrderdetail;
import com.heso.service.funds.order.entity.FundsOrderReturnObject;
import com.heso.service.funds.order.recharge.OrderRecharge;
import com.heso.service.funds.order.recharge.entity.OrderRechargeItem;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.AccountNeed;
import com.heso.service.yesUser.entity.AccountRight;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass;
 

/**
 * 用户查询派单表
 * 
 * @author xujun
 * 
 */
public class Transaction_001128 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001128.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 		 
 			String account = xmlBody.selectSingleNode("account").getText();
			 
 			 
			 	 
			
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
			 List<SendOrder> list = new ConsumeOrder().checkSendOrder(account);
			 for ( SendOrder sendOrder : list) {
				 sb.append("<sendOrder>");
				 sb.append("<Id>"+sendOrder.getId()+"</Id>");
				 sb.append("<teacher_name>"+sendOrder.getTeacher_name()+"</teacher_name>");
				 sb.append("<zhulia_name>"+sendOrder.getZhuliA_name()+"</zhulia_name>");
				 sb.append("<zhulib_name>"+sendOrder.getZhuliB_name()+"</zhulib_name>");
				 sb.append("<service_time>"+sendOrder.getService_time()+"</service_time>");
				 sb.append("<account_name>"+sendOrder.getAccount_name()+"</account_name>");
				 sb.append("<COMPLETE_TIME>"+sendOrder.getComplete_time()+"</COMPLETE_TIME>");
				 sb.append("<account_quanyiid>"+sendOrder.getAccount_quanyiid()+"</account_quanyiid>");
				 sb.append("<SERVICE_NAME>"+sendOrder.getService_name()+"</SERVICE_NAME>");
				 sb.append("<genjinren_name>"+sendOrder.getGenjinren_name()+"</genjinren_name>"); 
				 sb.append("<jinyibu>"+sendOrder.getJinyibu()+"</jinyibu>");
				 sb.append("<SERVICE_PLACE>"+sendOrder.getSercive_place()+"</SERVICE_PLACE>");
				 sb.append("<status>"+sendOrder.getStatus()+"</status>");
				 sb.append("<image>"+sendOrder.getImage()+"</image>");
				 sb.append("<fuwuzongjie>"+sendOrder.getFuwuzongjie()+"</fuwuzongjie>");
				 
				 sb.append("</sendOrder>");
			}
			 
		 
			//sb.append("<returnDesc>"+ss+"</returnDesc>");
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
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001128</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<id>5</id>");
		sb.append("<type>1</type>");
		sb.append("<orderId>1</orderId>");
		sb.append("<checkStatus>1</checkStatus>");
		sb.append("<account>0000000000000909</account>");
		
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
