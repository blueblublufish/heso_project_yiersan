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
import com.heso.service.mall.entity.SizeAndColor;
import com.heso.service.mall.entity.SuitMatchArgsObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 获取套装信息
 * 
 * @author xujun
 * 
 */
public class Transaction_001011 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001011.class);

	@Override 
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String productId = xmlBody.selectSingleNode("productId").getText();

			MallServiceReturnObject msro = new MallService().getSuitInfoNew(productId);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (msro.getCode().equals("000000")) {
				ProductItemObject suit = msro.getPioList().get(0);
				sb.append("<productId>" + suit.getProductId() + "</productId>");
				sb.append("<name>" + suit.getName() + "</name>");
				sb.append("<desc>" + suit.getDesc() + "</desc>");
				sb.append("<category>" + suit.getCategory() + "</category>");
				sb.append("<designName>" + suit.getDesignName() + "</designName>");
				sb.append("<scene>" + suit.getScene() + "</scene>");
				sb.append("<style>" + suit.getStyle() + "</style>");
				sb.append("<shape>" + suit.getShape() + "</shape>");
				sb.append("<ageType>" + suit.getAgeType() + "</ageType>");
				sb.append("<price>" + suit.getPrice() + "</price>");
				sb.append("<suitPrice>" + suit.getSuitPrice() + "</suitPrice>");
				sb.append("<discountPrice>" + suit.getDiscountPrice() + "</discountPrice>");
				sb.append("<imgFront>"+suit.getImgFront()+"</imgFront>");
				sb.append("<imgBehind>"+suit.getImgBehind()+"</imgBehind>");
				sb.append("<images>"+suit.getImages()+"</images>");
				sb.append("<stockStatus>"+suit.getStockStatus()+"</stockStatus>");
				sb.append("<sex>"+suit.getSex()+"</sex>");
				sb.append("<impression>"+suit.getImpression()+"</impression>");
				sb.append("<bodySuit>"+suit.getBodySuit()+"</bodySuit>");
				sb.append("<notSuitSkin>"+suit.getNotSuitSkin()+"</notSuitSkin>");
				sb.append("<faceNotColor>"+suit.getFaceSuitColor()+"</faceNotColor>");
				sb.append("<not_suit_nature>"+suit.getNot_suit_nature()+"</not_suit_nature>");
				sb.append("<body_notsuit>"+suit.getBody_notsuit()+"</body_notsuit>");
				ArrayList<ProductItemObject> goodsList = suit.getGoodsList();
				for (int i = 0; i < goodsList.size(); i++) {
					ProductItemObject item = goodsList.get(i);
					sb.append("<suitItem>");
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
					sb.append("<size>"+item.getSize()+ "</size>");
 					sb.append("<stockCount>" + item.getStockCount() + "</stockCount>");
					sb.append("<soldCount>" + item.getSoldCount() + "</soldCount>");
					sb.append("<imgFront>" + item.getImgFront() + "</imgFront>");
					sb.append("<imgBehind>" + item.getImgBehind() + "</imgBehind>");
 					sb.append("<impression>"+item.getImpression()+"</impression>");
					sb.append("<bodySuit>"+item.getBodySuit()+"</bodySuit>");
					sb.append("<notSuitSkin>"+item.getNotSuitSkin()+"</notSuitSkin>");
					sb.append("<faceNotColor>"+item.getFaceSuitColor()+"</faceNotColor>");
					sb.append("<itemStock>"+item.getItemStock()+"</itemStock>");
					for(SizeAndColor sac:item.getSizeColor()){
						sb.append("<itemSize>"); 
						sb.append("<colorId>"+sac.getColorId()+"</colorId>");
						sb.append("<colorType>"+sac.getColorType()+"</colorType>");
						sb.append("<sizeId>"+sac.getId()+"</sizeId>");
						sb.append("<sizeStock>"+ sac.getInStock()+"</sizeStock>");
						sb.append("<outStock>"+sac.getOutStock()+"</outStock>");
						sb.append("<itemid>"+sac.getProductId()+"</itemid>");
						sb.append("<size>"+sac.getSize()+"</size>");
						sb.append("<image>"+sac.getImage()+"</image>");
						sb.append("</itemSize>");
					}
					for(SizeAndColor sacc : item.getColors()){
						sb.append("<itemColor>");
						sb.append("<colorId>"+sacc.getColorId()+"</colorId>");
						sb.append("<colorType>"+sacc.getColorType()+"</colorType>");
     					sb.append("<image>"+sacc.getImage()+"</image>");
						sb.append("</itemColor>");
					}
					sb.append("</suitItem>");
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
		sb.append("<type>001011</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<productId>F18ZS0075</productId>");
		
		sb.append("<account>jkfgjkfgjkf</account>");
		sb.append("<token>0</token>");
 	
 
		
		sb.append("</body>");
		sb.append("</message>");
		StringBuffer shhBuffer = new StringBuffer();
		shhBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001011</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><productId>F18SY0003</productId></body></message>");
		

		try {
			new TransDataProcess().execute(shhBuffer.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
