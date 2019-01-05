package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CheckCartReturnObject;
import com.heso.service.cart.entity.CheckObject;
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
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 查询审核购物车
 * 
 * @author xujun
 * 
 */
public class Transaction_001109 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001109.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try { 
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String token =xmlBody.selectSingleNode("token").getText();
 			 
			
			CheckCartReturnObject ccro = new CartService().checkCart(account);
 			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if(ccro.getCheckList().size()!=0&&ccro.getCheckList()!=null){
				for(CheckObject cObject : ccro.getCheckList()){
					sb.append("<checkDetail>");
					sb.append("<id>"+cObject.getId()+"</id>");
					sb.append("<image>"+cObject.getIamge()+"</image>");
					sb.append("<type>"+cObject.getType()+"</type>");
					sb.append("<wordrobe>"+cObject.getWarbrobe()+"</wordrobe>");
					sb.append("<checkId>"+ cObject.getCheckId()+"</checkId>");
					sb.append("<account>"+cObject.getAccount()+"</account>");
					sb.append("<name>"+cObject.getName()+"</name>");
					sb.append("</checkDetail>");
				}
			}
			

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
		sb.append("<type>001109</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<checkId>ddsdsd</checkId>");
 		sb.append("<account>0000000000000964</account>");
		sb.append("<token>1111111111</token>");	
		 	
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
