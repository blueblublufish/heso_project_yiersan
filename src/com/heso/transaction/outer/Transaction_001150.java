package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import oracle.sql.ARRAY;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;
import com.heso.service.expert.ExpertService;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.QaTestAndResult;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询商品
 * @author Administrator
 *
 */
public class Transaction_001150 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001150.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String page = xmlBody.selectSingleNode("page").getText();
			String pageSize = xmlBody.selectSingleNode("pageSize").getText();

			String sex = xmlBody.selectSingleNode("sex").getText();
			
			String style = xmlBody.selectSingleNode("style").getText();
			String scene = xmlBody.selectSingleNode("scene").getText();
			String productName = xmlBody.selectSingleNode("productName").getText();
			String pinlei = xmlBody.selectSingleNode("pinlei").getText();
			String supplier = xmlBody.selectSingleNode("supplier").getText();
			String paixu = xmlBody.selectSingleNode("paixu").getText();			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			
 			MallServiceReturnObject msro = new ExpertService().getSearchtSuit(sex, style, scene, productName, pinlei, supplier, Integer.valueOf(page),Integer.valueOf(pageSize), paixu);
 			 
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				
				for(ProductItemObject suit : msro.getPioList()){
					sb.append("<product>");
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
					sb.append("<type>"+ suit.getType() +"</type>");
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
					sb.append("</product>");
				}
			 
			}else {
				sb.append("<product></product>");
			}
			
			 
			
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001115</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><name>废弃</name><phone>12345636612</phone><regionId>440701</regionId><address>市区</address><sex>0</sex><birthType>1</birthType><birthday></birthday><birthday2></birthday2><companyName>22</companyName><carree>1</carree><identity>2</identity><scene>1</scene><sceneForMan>2</sceneForMan><wantipstyle1>1</wantipstyle1><wantipstyle2>1</wantipstyle2><suitColor>1</suitColor><notSuitColor>3</notSuitColor><sense>2</sense><straight>1</straight><bodyStyle>2</bodyStyle><bodynotsuit>6</bodynotsuit><ipStyle1>1</ipStyle1><ipStyle2>1</ipStyle2></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001150</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>"); 
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<sex>1</sex>");
		sb.append("<page>1</page>");
		sb.append("<pageSize>5</pageSize>");
		sb.append("<style></style>");
		sb.append("<scene></scene>");
		sb.append("<pinlei></pinlei>");
		sb.append("<productName>D17O0003</productName>");
		
		sb.append("<supplier>6</supplier>");
		sb.append("<paixu>0</paixu>");
		sb.append("<token>0</token>");	
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ss.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
