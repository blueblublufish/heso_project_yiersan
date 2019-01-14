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
 * 查询测试结果
 * @author Administrator
 *
 */
public class Transaction_001139 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001139.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			QaTestAndResult qa = new ConsumeOrder().checkTestResult(account);
 			MallServiceReturnObject msro = qa.getMsro();
 			MallServiceReturnObject msro2 = qa.getMsroTuozhan();
			sb.append("<testResult>");
			sb.append("<id>"+ qa.getId()+"</id>");
			sb.append("<zhustyle>"+ qa.getZhuStyle() +"</zhustyle>");
			sb.append("<zhustyleid>"+ qa.getZhuStyleId() +"</zhustyleid>");
			sb.append("<zhustyleName>"+ qa.getZhuStyleName()		 +"</zhustyleName>");
			sb.append("<fustyle>"+ qa.getFuStyle() +"</fustyle>");
			sb.append("<fustyleid>"+ qa.getFuStyleId() +"</fustyleid>");
			sb.append("<fustyleName>"+ qa.getFuStyleName() +"</fustyleName>");
			sb.append("<styledesc>"+ qa.getStyleDesc() +"</styledesc>");
			sb.append("<bodypattern>"+ qa.getBodyPattern() +"</bodypattern>");
			sb.append("<bodypatternName>"+ qa.getBodyPatternName() +"</bodypatternName>");
			sb.append("<bodynotsuit>"+ qa.getBodyNotSuit() +"</bodynotsuit>");	 
			sb.append("<bodynotsuitName>"+ qa.getBodyNotSuitName() +"</bodynotsuitName>");
			sb.append("<skinnotsuit>"+ qa.getSkinNotSuit() +"</skinnotsuit>");
			sb.append("<skinnotsuitName>"+ qa.getSkinNotSuitName() +"</skinnotsuitName>");
			sb.append("<tedian>"+ qa.getTedian() +"</tedian>");
			sb.append("<zhuiqiu>"+ qa.getZhuiqiu() +"</zhuiqiu>");
			sb.append("<youshi>"+ qa.getYoushi() +"</youshi>");
			sb.append("<age>"+ qa.getAge() +"</age>");
			sb.append("<industry>"+ qa.getIndustry() +"</industry>");
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				
				for(ProductItemObject suit : msro.getPioList()){
					sb.append("<zhusuit>");
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
					sb.append("</zhusuit>");
				}
			 
			}else {
				sb.append("<zhusuit></zhusuit>");
			}
			if(msro2.getPioList()!=null&&msro2.getPioList().size()!=0){
				
				for(ProductItemObject suit : msro2.getPioList()){
					sb.append("<tuozhansuit>");
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
					sb.append("</tuozhansuit>");
				}
			 
			}else {
				sb.append("<tuozhansuit></tuozhansuit>");
			}
			sb.append("</testResult>");
			
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001139</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000001958</account><token>123456</token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001139</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		 
		sb.append("<account>0000000000001374</account>");
		 
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
