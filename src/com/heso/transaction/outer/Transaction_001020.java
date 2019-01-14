package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CartItemObject;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 设置购物车
 * 
 * @author xujun
 * 
 */
public class Transaction_001020 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001020.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据 
			String account = xmlBody.selectSingleNode("account").getText();
			String productId = xmlBody.selectSingleNode("productId").getText();
			String count = xmlBody.selectSingleNode("count").getText();
			String size = xmlBody.selectSingleNode("size").getText();
			String selected = xmlBody.selectSingleNode("selected").getText();
			String suitId = xmlBody.selectSingleNode("suitId").getText();
			String subordinate = xmlBody.selectSingleNode("subordinate").getText();
			String color = xmlBody.selectSingleNode("color").getText();
			String colorType =xmlBody.selectSingleNode("colorType").getText();
		/*	String channelType = xmlBody.selectSingleNode("channelType").getText();
			String formatType = xmlBody.selectSingleNode("formatType").getText();
			String sizeType = xmlBody.selectSingleNode("sizeType").getText();
			String styleType = xmlBody.selectSingleNode("stylleType").getText();
			String clothType = xmlBody.selectSingleNode("clothType").getText();
			String seller = xmlBody.selectSingleNode("seller").getText();*/
			CartItemObject cio = new CartItemObject();
			cio.setAccount(account);
			cio.setProductId(productId);
			cio.setCount(count);
			cio.setSize(size);
			cio.setSelected(selected);
			cio.setSuitId(suitId);
			cio.setSubordinate(subordinate);
			cio.setColor(color);
			cio.setColorType(colorType);
		/*	cio.setChannelType(channelType);
			cio.setFormatType(formatType);
			cio.setSizeType(sizeType);
			cio.setStyleType(styleType);
			cio.setClothType(clothType);*/
			// 取出明细
			List<Node> listNodes = xmlBody.selectNodes("productDetail");
			if (listNodes != null) {
				ArrayList<CartItemObject> cioList = new ArrayList<CartItemObject>();
				for (Node node : listNodes) {
					CartItemObject cioDetail = new CartItemObject();
					cioDetail.setProductId(node.selectSingleNode("productId").getText());
					cioDetail.setSize(node.selectSingleNode("size").getText());
					cioDetail.setColor(node.selectSingleNode("color").getText());
					cio.setColorType(node.selectSingleNode("colorType").getText());
					cioList.add(cioDetail);
				}
				cio.setCioList(cioList);
			}
			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;

			CartServiceReturnObject csro = new CartService().setProduct(cio);

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
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
		StringBuffer sb = new StringBuffer();
		StringBuffer dd = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001020</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000913</account>");
		sb.append("<token>0</token>");
		sb.append("<productId>CS00002</productId>");
		sb.append("<count>10</count>");
		sb.append("<suitId>1</suitId>");
		sb.append("<size>S</size>");
		sb.append("<selected>1</selected>");
		sb.append("<subordinate>1</subordinate>");
		//sb.append("<productDetail></productDetail>");
		sb.append("</body>");
		sb.append("</message>");
		dd.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>008001</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><name>5678</name><tel>15014606141</tel><type>3</type><content></content></body></message>");
		try {
			new TransDataProcess().execute(dd.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
