package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JList.DropLocation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.tiles.GetTag;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.sun.org.apache.commons.beanutils.WrapDynaBean;
/**
 * 查询衣橱danpin
 * @author Administrator
 *
 */
public class Transaction_001164 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001164.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String id = xmlBody.selectSingleNode("id").getText();
			StringBuffer sb = new StringBuffer();
		    WardrobeDTO  wardrobeDTO   = new WardrobeService().findWardrobeDTO(id);
		    if(wardrobeDTO!=null){
		    	sb.append("<wardrobeItem>");
			 
				sb.append("<id>"+wardrobeDTO.getId()+"</id>");
				sb.append("<account>"+wardrobeDTO.getAccount()+"</account>");
				sb.append("<image>"+wardrobeDTO.getImage()+"</image>");
				sb.append("<type>"+wardrobeDTO.getType()+"</type>");
				sb.append("<typeName>"+wardrobeDTO.getCategoryName()+"</typeName>"); 
 				sb.append("<scene>"+wardrobeDTO.getScene()+"</scene>");
 				sb.append("<sceneName>"+wardrobeDTO.getSceneName()+"</sceneName>");
				sb.append("<style>"+wardrobeDTO.getStyle()+"</style>");
				sb.append("<styleName>"+wardrobeDTO.getStyleName()+"</styleName>");
				sb.append("<color>"+wardrobeDTO.getColor()+"</color>");
				sb.append("<suit>"+wardrobeDTO.getSuit()+"</suit>");
				sb.append("<uplaod>"+wardrobeDTO.getUplaod()+"</uplaod>");
 				sb.append("<isGood>"+wardrobeDTO.getIsGood()+"</isGood>");	
				sb.append("<name>"+wardrobeDTO.getName()+"</name>");
				sb.append("<label>"+wardrobeDTO.getLabel()+"</label>");
 				sb.append("<secondType>"+wardrobeDTO.getSecondType()+"</secondType>");
				sb.append("<secondTypeName>"+wardrobeDTO.getSecondTypeName()+"</secondTypeName>");
				
				sb.append("<secondTypeId>"+wardrobeDTO.getSecondTypeId()+"</secondTypeId>");
				sb.append("<season>"+wardrobeDTO.getSeason()+"</season>");
				sb.append("<seasonName>"+wardrobeDTO.getSeasonName()+"</seasonName>");
				
				
				sb.append("</wardrobeItem>");	
		    	
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
		StringBuffer ssb = new StringBuffer();
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001164</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><id>5750</id></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001164</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<id>3045</id>");
		sb.append("<token>0</token>");
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ssb.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
