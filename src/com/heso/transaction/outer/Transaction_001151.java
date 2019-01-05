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
import com.heso.service.mall.entity.RecommendProducts;
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
 * 查询推荐商品
 * @author Administrator
 *
 */
public class Transaction_001151 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001151.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			
 			MallServiceReturnObject msro = new ArticleService().getRcommenedProducts();
 			if(msro.getManreList()!=null){
 				for(RecommendProducts re: msro.getManreList()){
 					if(re.getList()!=null){
 						sb.append("<manproduct>");
 						sb.append("<sex>" + re.getSex() + "</sex>");
 						sb.append("<styleId>" + re.getStyleId() + "</styleId>");
 						sb.append("<styleName>" + re.getStyleName() + "</styleName>");
 						for(ProductItemObject suit : re.getList()){
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
 							sb.append("</product>");
 						}
 						sb.append("</manproduct>");
 					 
 					}else {
 						sb.append("<manproduct></manproduct>");
 					}
 				}
 			}
 			
 			if(msro.getWomenreList()!=null){
 				for(RecommendProducts re: msro.getWomenreList()){
 					if(re.getList()!=null){
 						sb.append("<womenproduct>");
 						sb.append("<sex>" + re.getSex() + "</sex>");
 						sb.append("<styleId>" + re.getStyleId() + "</styleId>");
 						sb.append("<styleName>" + re.getStyleName() + "</styleName>");
 						for(ProductItemObject suit : re.getList()){
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
 							sb.append("</product>");
 						}
 						sb.append("</womenproduct>");
 					 
 					}else {
 						sb.append("<womenproduct></womenproduct>");
 					}
 				}
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001090</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account></account><token></token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001151</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>"); 
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<videoId>6</videoId>");
		sb.append("<page>1</page>");
		sb.append("<pageSize>1</pageSize>");
		sb.append("<style>1</style>");
		sb.append("<scene>1</scene>");
		sb.append("<pinlei></pinlei>");
		sb.append("<productName></productName>");
		
		sb.append("<supplier>6</supplier>");
		sb.append("<paixu></paixu>");
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
