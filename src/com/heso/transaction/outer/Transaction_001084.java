package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.viewer.configure.MRUFileManager;
import org.dom4j.Element;
import org.dom4j.Node;

import com.heso.common.GlobalCache;
import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.service.expert.ExpertService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.sms.SmsService;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.StringTools;
import com.sun.faces.el.ELConstants;

/**
 * 获取专家作品库
 * 
 * @author www
 * 
 */
public class Transaction_001084 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001084.class);

	@Override  
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String designerId = xmlBody.selectSingleNode("designerId").getText();
			String sex = xmlBody.selectSingleNode("sex").getText(); 
			String style = xmlBody.selectSingleNode("style").getText();
			String scene = xmlBody.selectSingleNode("scene").getText();
			/*String account = xmlBody.selectSingleNode("account").getText();

			// 校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if (!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;*/
			MallServiceReturnObject msro =new ExpertService().getSuitInfo(designerId, style, sex,scene);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (msro.getCode().equals("000000")) {
				//ProductItemObject suit = msro.getPioList().get(0);
				if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
					
					for(ProductItemObject suit : msro.getPioList()){
						sb.append("<suit>");
						sb.append("<productId>" + suit.getProductId() + "</productId>");
						sb.append("<name>" + suit.getName() + "</name>");
						sb.append("<type>" + suit.getType() + "</type>");
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
						sb.append("<designerId>"+suit.getDesignerId()+"</designerId>");
						sb.append("<sex>"+suit.getSex()+"</sex>");
						sb.append("<stockStatus>"+suit.getStockStatus()+"</stockStatus>");
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
							sb.append("<stockStatus>" + item.getStockStatus() + "</stockStatus>");
							sb.append("<stockCount>" + item.getStockCount() + "</stockCount>");
							sb.append("<soldCount>" + item.getSoldCount() + "</soldCount>");
							sb.append("<imgFront>" + item.getImgFront() + "</imgFront>");
							sb.append("<imgBehind>" + item.getImgBehind() + "</imgBehind>");
							sb.append("<stockStatus>"+item.getStockStatus()+"</stockStatus>");
							sb.append("</suitItem>");
						}
						sb.append("</suit>");
					}
				 
				}else {
					sb.append("<suit></suit>");
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
		StringBuffer sb = new StringBuffer();
		
		StringBuffer ssBuffer = new StringBuffer();
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001084</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><designerId> </designerId><sex>0</sex><style>3</style><scene></scene></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001084</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId></designerId>");
		sb.append("<sex>0</sex>"); 
		sb.append("<style>2</style>");
		sb.append("<scene>1</scene>");
		sb.append("<account>jkfgjkfgjkf</account>");
		sb.append("<token>0</token>");
 	
 
		
		sb.append("</body>");
		sb.append("</message>");

		try {
			new TransDataProcess().execute(ssBuffer.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
