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
 * 新增派单表
 * 
 * 
 * @author xujun
 * 
 */
public class Transaction_001127 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001127.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
		 
 			String teacher_id = xmlBody.selectSingleNode("teacher_id").getText();
 			String teacher_name = xmlBody.selectSingleNode("teacher_name").getText();
 			String zhuliA_id = xmlBody.selectSingleNode("zhuliA_id").getText();
 			String zhuliA_name = xmlBody.selectSingleNode("zhuliA_name").getText();
 			String zhuliB_id = xmlBody.selectSingleNode("zhuliB_id").getText();
 			String zhuliB_name = xmlBody.selectSingleNode("zhuliB_name").getText();
 			String service_time = xmlBody.selectSingleNode("service_time").getText();
 			String sercive_place = xmlBody.selectSingleNode("sercive_place").getText();
 			String service_id = xmlBody.selectSingleNode("service_id").getText();
 			String service_name = xmlBody.selectSingleNode("service_name").getText();
 			String account = xmlBody.selectSingleNode("account").getText();
 			String account_name = xmlBody.selectSingleNode("account_name").getText();
 			String teacher_status = "0";
 			String zhuliA_status = "0";
  			String zhuliB_status = "0";
 			String account_quanyiid = xmlBody.selectSingleNode("account_quanyiid").getText();
 			String genjinren_id = xmlBody.selectSingleNode("genjinren_id").getText();
 			String genjinren_name = xmlBody.selectSingleNode("genjinren_name").getText();
 			 SendOrder sendOrder = new SendOrder();
 			 sendOrder.setTeacher_id(teacher_id);
 			 sendOrder.setTeacher_name(teacher_name);
 			 sendOrder.setZhuliA_id(zhuliA_id);
 			 sendOrder.setZhuliA_name(zhuliA_name);
 			 sendOrder.setZhuliB_id(zhuliB_id);
 			 sendOrder.setZhuliB_name(zhuliB_name);
 			 sendOrder.setService_time(service_time);
 			 sendOrder.setSercive_place(sercive_place);
 			 sendOrder.setService_id(service_id);
 			 sendOrder.setService_name(service_name);
 			 sendOrder.setAccount(account);
 			 sendOrder.setAccount_name(account_name);
 			 sendOrder.setTeacher_status(teacher_status);
 			 sendOrder.setZhuliA_status(zhuliA_status);
 			 sendOrder.setZhuliB_status(zhuliB_status);
 			 sendOrder.setAccount_quanyiid(account_quanyiid);
			 sendOrder.setGenjinren_id(genjinren_id);
			 sendOrder.setGenjinren_name(genjinren_name);
			
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
			 
			  code = new ConsumeOrder().addSendOrder(sendOrder);
			 
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
		StringBuffer ssBuffer = new StringBuffer();
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001127</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account_quanyiid>93</account_quanyiid><account>0000000000000909,0000000000000910</account><account_name>测试,姓名</account_name><service_id>5</service_id><service_name>发型定制与私教</service_name><teacher_id>2</teacher_id><zhuliA_id>3333359</zhuliA_id><zhuliB_id></zhuliB_id><service_time>2018-07-11</service_time><sercive_place>广东省 广州市 天河区 不知到</sercive_place><zhuliA_name>谢芷晴</zhuliA_name><zhuliB_name></zhuliB_name><teacher_name>SAKA</teacher_name><genjinren_id>2</genjinren_id><genjinren_name>Wayne Lv</genjinren_name></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001123</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<id>5</id>");
		sb.append("<type>1</type>");
		sb.append("<orderId>1</orderId>");
		sb.append("<checkStatus>1</checkStatus>");
		sb.append("<account>in_0000000000001223</account>");
		
		sb.append("<remarks>11</remarks>");
	 
 		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ssBuffer.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
