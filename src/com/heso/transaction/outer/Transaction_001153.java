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
 * ��ѯ��Ƶ����Ʒ
 * @author Administrator
 *
 */
public class Transaction_001153 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001153.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//��ȡ����
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String videoId = xmlBody.selectSingleNode("videoId").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//�ж�token
			 
			
			//�������� 
			StringBuffer sb = new StringBuffer();
 			
 			MallServiceReturnObject msro = new ArticleService().getVideoProducts(videoId);
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001090</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><account></account><token></token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001151</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5����ǩ��</digest>"); 
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