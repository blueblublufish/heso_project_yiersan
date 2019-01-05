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
 * 查询用户需求
 * 
 * @author xujun
 * 
 */
public class Transaction_001125 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001125.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
 			String date = xmlBody.selectSingleNode("date").getText();
 			String adminId = xmlBody.selectSingleNode("adminId").getText();
 			String accountId = xmlBody.selectSingleNode("accountid").getText();
			 
 			 
			 	 
			
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
			 
			 List<AccountNeed> arList = new YesUserService().findAccountNeed(date, accountId, adminId);
			 for(AccountNeed an:arList){
				 sb.append("<accountNeed>");
				 sb.append("<Id>"+an.getId()+"</Id>");
				 sb.append("<adminId>"+an.getAdminId()+"</adminId>");
				 sb.append("<phone>"+an.getPhone()+"</phone>");
				 sb.append("<accountId>"+an.getAccountId()+"</accountId>");
				 sb.append("<style>"+an.getStyle()+"</style>");
				 sb.append("<zhiye>"+an.getZhiye()+"</zhiye>");
				 sb.append("<cndesc>"+an.getCndesc()+"</cndesc>");
				 sb.append("<scene>"+an.getScene()+"</scene>");
				 sb.append("<createTime>"+an.getCreateTime()+"</createTime>");
				 sb.append("<accountName>"+an.getAccountName()+"</accountName>");
				 sb.append("</accountNeed>");
				 
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
 		sb.append("<id>5</id>");
		sb.append("<type>1</type>");
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
