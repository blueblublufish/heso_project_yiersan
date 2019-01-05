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
 * 增加阅读数及更新历史记录
 * 
 * 
 * @author xujun
 * 
 */
public class Transaction_001137 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001137.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 			String account = xmlBody.selectSingleNode("account").getText();
 			String classId = xmlBody.selectSingleNode("classId").getText();
 			String className = xmlBody.selectSingleNode("className").getText();
			 
 			 
			 	  
			
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
			 
			  code = new ConsumeOrder().updateReadHistory(classId, account, className) ;
			 
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
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001127</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account_quanyiid>4</account_quanyiid><account>0000000000000914,0000000000000915</account><account_name>王国安、素婷</account_name><service_id>8</service_id><service_name>演讲与口才训练</service_name><teacher_id>2</teacher_id><zhuliA_id>11</zhuliA_id><zhuliB_id></zhuliB_id><service_time>2018-06-14</service_time><sercive_place>广西壮族自治区 防城港市 市辖区 测试</sercive_place><zhuliA_name>曾伟杰</zhuliA_name><zhuliB_name></zhuliB_name><teacher_name>吕维聪</teacher_name><genjinren_id>2</genjinren_id><genjinren_name>Wayne Lv</genjinren_name></body></message>");
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
