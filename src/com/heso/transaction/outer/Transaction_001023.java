package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CartItemObject;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 查询购物车明细
 * 
 * @author xujun
 * 
 */
public class Transaction_001023 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001023.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String productId = xmlBody.selectSingleNode("productId").getText();
			String subordinate = xmlBody.selectSingleNode("subordinate").getText();
			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			// 设置返回数据
			StringBuffer sb = new StringBuffer();

			CartServiceReturnObject csro = new CartService().getInfoDetail(account, productId,subordinate);
			if (csro.getCode().equals("000000")) {
				ArrayList<CartItemObject> cioList = csro.getCioList();
				CartItemObject cio = cioList.get(0);
				for (CartItemObject ciodetail : cio.getCioList()) {
					sb.append("<productDetail>");
					sb.append("<productId>" + ciodetail.getProductId() + "</productId>");
					sb.append("<type>" + ciodetail.getType() + "</type>");
					sb.append("<name>" + ciodetail.getName() + "</name>");
					sb.append("<image>" + ciodetail.getImage() + "</image>");
					sb.append("<count>" + ciodetail.getCount() + "</count>");
					sb.append("<price>" + ciodetail.getPrice() + "</price>");
					sb.append("<amount>" + ciodetail.getAmount() + "</amount>");
					sb.append("<color>" + ciodetail.getColor() + "</color>");
					sb.append("<size>" + ciodetail.getSize() + "</size>");
					sb.append("<amount>" + ciodetail.getAmount() + "</amount>");
					sb.append("<suitPrice>"+ciodetail.getSuitPrice()+"</suitPrice>");
					sb.append("<subordinate>"+ciodetail.getSubordinate()+"</subordinate>");
					sb.append("</productDetail>");
				}
			}
			String xmlBodyStr = super.buildResp(csro.getCode(), sb.toString());
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
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001023</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000002</account>");
		sb.append("<productId>16SP0001</productId>");
		sb.append("<token>0</token>");
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
