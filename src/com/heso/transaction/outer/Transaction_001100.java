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
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 查询类型列表
 * 
 * @author xujun
 * 
 */
public class Transaction_001100 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001100.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String keyword = xmlBody.selectSingleNode("keyword").getText();

			String code = "000000";
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			ArrayList<KeywordType> stoList = new MallService().getKeyWordTypes(keyword);
			if (stoList == null)
				code = "900010";
			else {
				for (KeywordType ro : stoList) {
					sb.append("<typeItem>");
					sb.append("<id>" + ro.getId() + "</id>");
					sb.append("<name>" + ro.getName() + "</name>");
					sb.append("<image>" + ro.getImage() + "</image>");
					sb.append("<value>"+ro.getValue()+"</value>");
					sb.append("<sex>"+ro.getSex()+"</sex>");
					sb.append("</typeItem>");
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
		sb.append("<type>001100</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<keyword>CATEGORY</keyword>");
 		sb.append("<account>0000000000000019</account>");
		sb.append("<token>0</token>");	
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
