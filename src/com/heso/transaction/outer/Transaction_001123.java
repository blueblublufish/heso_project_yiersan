package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.AccountRight;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass; 
/**
 * 查询用户权益
 * 
 * @author xujun
 * 
 */
public class Transaction_001123 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001123.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 			String ID = xmlBody.selectSingleNode("id").getText();
 			String type = xmlBody.selectSingleNode("type").getText();
	 
			 
 			   
			 	 
			
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
			 
			 List<AccountRight> arList = new YesUserService().getAccountQuanyiBy(ID, type);
			 for(AccountRight ar:arList){
				 sb.append("<accountRight>");
				 sb.append("<accounts>"+ar.getAccount()+"</accounts>");
				 sb.append("<id>"+ar.getId()+"</id>");
				 sb.append("<phone>"+ar.getPhone()+"</phone>");
				 sb.append("<quanyiId>"+ar.getQuanyiId()+"</quanyiId>");
				 sb.append("<quanyiName>"+ar.getQuanyiName()+"</quanyiName>");
				 sb.append("<status>"+ar.getStatus()+"</status>");
				 sb.append("<count>"+ar.getCount()+"</count>");
				 sb.append("<teachera>"+ar.getTeachera()+"</teachera>");
				 sb.append("<teacherb>"+ar.getTeacherb()+"</teacherb>");
				 sb.append("<name>"+ar.getName()+"</name>");
				 sb.append("<zhuli>"+ar.getZhuli()+"</zhuli>");
				 sb.append("<endTime>"+ar.getEndTime()+"</endTime>");
				 sb.append("<type>"+ar.getType()+"</type>");
				 sb.append("</accountRight>");
				 
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
		sb.append("<type>001123</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
 		sb.append("<id>0000000000001393</id>");
		sb.append("<type>0</type>");
		sb.append("<orderId>1</orderId>");
		sb.append("<checkStatus>1</checkStatus>");
		sb.append("<account>in_0000000000001223</account>");
		
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
