package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.mall.entity.SuitMatchArgsObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 匹配单品尺寸
 * 
 * @author xujun
 * 
 */
public class Transaction_001013 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001013.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String suitId = xmlBody.selectSingleNode("suitId").getText();

			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			// 匹配套装
			MallServiceReturnObject msro = new MallService().matchGoodsSize(account, suitId);

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (msro.getCode().equals("000000")) {
				ArrayList<ProductItemObject> goodsList = msro.getPioList();
				for (ProductItemObject item : goodsList) {
					sb.append("<goodsItem>");
					sb.append("<productId>" + item.getProductId() + "</productId>");
					sb.append("<name>" + item.getName() + "</name>");
					sb.append("<desc>" + item.getDesc() + "</desc>");
					sb.append("<category>" + item.getCategory() + "</category>");
					sb.append("<metarialDesc>" + item.getMetarialDesc() + "</metarialDesc>");
					sb.append("<supplyName>" + item.getSupplyName() + "</supplyName>");
					sb.append("<color>" + item.getColor() + "</color>");
					sb.append("<price>" + item.getPrice() + "</price>");
					sb.append("<suitPrice>" + item.getSuitPrice() + "</suitPrice>");
					sb.append("<discountPrice>" + item.getDiscountPrice() + "</discountPrice>");
					sb.append("<size>" + item.getSize() + "</size>");
					sb.append("<stockStatus>" + item.getStockStatus() + "</stockStatus>");
					sb.append("<stockCount>" + item.getStockCount() + "</stockCount>");
					sb.append("<soldCount>" + item.getSoldCount() + "</soldCount>");
					sb.append("<imgFront>" + item.getImgFront() + "</imgFront>");
					sb.append("<imgBehind>" + item.getImgBehind() + "</imgBehind>");
					sb.append("<pid>"+item.getPid()+"</pid>");
					sb.append("</goodsItem>");
				}
			}

			String xmlBodyStr = super.buildResp(msro.getCode(), sb.toString());
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
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001013</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account></account>");
		sb.append("<token>16SP0001</token>");
		sb.append("<suitId>F18WY0001</suitId>");
		sb.append("</body>");
		sb.append("</message>");

		try {
			new TransDataProcess().execute(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
