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
import com.sun.org.apache.commons.beanutils.BeanUtils;
/**
 * 查询单品推荐
 * @author Administrator
 *
 */
public class Transaction_001156 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001156.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String account = xmlBody.selectSingleNode("account").getText();
		 

			String sex = xmlBody.selectSingleNode("sex").getText();

			 		 
 	 
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			
 			MallServiceReturnObject msro = new ExpertService().getTuijianDanpin(sex, account);
 			 
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<shangyi>");
				sb.append("<pinlei>1</pinlei>");
				sb.append("<pinleiName>上衣</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("1")){
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
						
				}
				sb.append("</shangyi>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<kuzhuang>");
				sb.append("<pinlei>2</pinlei>");
				sb.append("<pinleiName>裤装</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("2")){
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
						
				}
				sb.append("</kuzhuang>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<qunzhuang>");
				sb.append("<pinlei>3</pinlei>");
				sb.append("<pinleiName>裙装</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("3")){
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
						
				}
				sb.append("</qunzhuang>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<peishi>");
				sb.append("<pinlei>4</pinlei>");
				sb.append("<pinleiName>配饰</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("4")){
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
						
				}
				sb.append("</peishi>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<xielei>");
				sb.append("<pinlei>5</pinlei>");
				sb.append("<pinleiName>鞋类</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("5")){
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
						
				}
				sb.append("</xielei>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<wazi>");
				sb.append("<pinlei>6</pinlei>");
				sb.append("<pinleiName>袜子</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("6")){
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
						
				}
				sb.append("</wazi>");
			 
			}
			if(msro.getPioList()!=null&&msro.getPioList().size()!=0){
				sb.append("<teshufu>");
				sb.append("<pinlei>7</pinlei>");
				sb.append("<pinleiName>特殊服装</pinleiName>");
				for(ProductItemObject suit : msro.getPioList()){
					if(suit.getCategory().equals("7")){
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
						
				}
				sb.append("</teshufu>");
			 
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001150</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><sex></sex><page>1</page><pageSize>5</pageSize><style></style><scene></scene><productName>阳光</productName><pinlei></pinlei><supplier></supplier><paixu>0</paixu></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001156</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>"); 
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<sex>0</sex>");
		sb.append("<account>0000000000000922</account>");
		sb.append("<pageSize>5</pageSize>");
		sb.append("<style></style>");
		sb.append("<scene></scene>");
		sb.append("<pinlei></pinlei>");
		sb.append("<productName></productName>");
		
		sb.append("<supplier>6</supplier>");
		sb.append("<paixu>0</paixu>");
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
