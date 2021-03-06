package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import com.heso.service.mall.entity.SizeAndColor;
import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;

/**
 * 获取单品信息 
 * 
 * @author xujun 
 *  
 */
public class Transaction_001010 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001010.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			
			String productId = xmlBody.selectSingleNode("productId").getText();

			MallServiceReturnObject msro = new MallService().getGoodsInfo(productId);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (msro.getCode().equals("000000")) {
				ProductItemObject item = msro.getPioList().get(0);
				sb.append("<productId>" + item.getProductId() + "</productId>");
				sb.append("<name>" + item.getName() + "</name>");
				sb.append("<desc>" + item.getDesc() + "</desc>");
				sb.append("<category>" + item.getCategory() + "</category>");
				sb.append("<categoryId>" + item.getCategoryId() + "</categoryId>");
				sb.append("<metarialDesc>" + item.getMetarialDesc() + "</metarialDesc>");
				sb.append("<supplyName>" + item.getSupplyName() + "</supplyName>");
				sb.append("<color>" + item.getColor() + "</color>");
				sb.append("<price>" + item.getPrice() + "</price>");
				sb.append("<suitPrice>" + item.getSuitPrice() + "</suitPrice>");
				sb.append("<discountPrice>" + item.getDiscountPrice() + "</discountPrice>");
				sb.append("<size>"+item.getSize()+ "</size>");
				sb.append("<stockStatus>" + item.getStockStatus() + "</stockStatus>");
				sb.append("<stockCount>" + item.getStockCount() + "</stockCount>");
				sb.append("<soldCount>" + item.getSoldCount() + "</soldCount>");
				sb.append("<imgFront>" + item.getImgFront() + "</imgFront>");
				sb.append("<imgBehind>" + item.getImgBehind() + "</imgBehind>");
				sb.append("<pid>"+item.getPid()+"</pid>");
				sb.append("<brand>"+item.getBrand()+"</brand>");
				sb.append("<washingType>"+item.getWashingType()+"</washingType>");
				sb.append("<stockStatus>"+item.getStockStatus()+"</stockStatus>");
				sb.append("<goodsDes>"+"<![CDATA["+item.getGoodDes()+"]]>"+"</goodsDes>");
				sb.append("<imageList>");
				for(String image : item.getImageList()){
					sb.append("<image>"+image+"</image>");
				}
				sb.append("</imageList>");
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
		sb.append("<type>001010</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
 		sb.append("<productId>D17O0002</productId>");
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
