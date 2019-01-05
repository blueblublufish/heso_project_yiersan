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
 * ������������
 * 
 * 
 * @author xujun
 * 
 */
public class Transaction_001138 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001138.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 		  
 			String qcolor = xmlBody.selectSingleNode("qcolor").getText();
 			String qpeishi = xmlBody.selectSingleNode("qpeishi").getText();
 			String qmianliao = xmlBody.selectSingleNode("qmianliao").getText();
 			String qshenxing = xmlBody.selectSingleNode("qshenxing").getText();
 			String qquedian = xmlBody.selectSingleNode("qquedian").getText();
 			String account = xmlBody.selectSingleNode("account").getText();
			String skinNotSuit = xmlBody.selectSingleNode("skinNotSuit").getText();
 			String fenshu = xmlBody.selectSingleNode("mianrong").getText();
			
			 
		   // String string = new YesUserService().addBiaoZhun(productId, orderID, pinlei, account);
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// ���÷�������
			StringBuffer sb = new StringBuffer();
		/*	if (account == null)
				code = "900010"; 
			else { 
				code = "000000";
				sb.append("<account>"+account+"</account>");
			}
*/ 
			 
			  code = new ConsumeOrder().qaTest(qcolor, qpeishi, qmianliao, qshenxing, qquedian, skinNotSuit,account,fenshu);
			 
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
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001138</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><account>0000000000000909</account><qcolor>40</qcolor><qpeishi>46</qpeishi><qmianliao>77</qmianliao><qshenxing>55</qshenxing><qquedian>64</qquedian><skinNotSuit>93</skinNotSuit><mianrong>223</mianrong></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001038</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5����ǩ��</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<id>5</id>");
		sb.append("<type>1</type>");
		sb.append("<orderId>1</orderId>");
		sb.append("<checkStatus>1</checkStatus>");
		sb.append("<account>0000000000000964</account>");
		
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
