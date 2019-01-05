package com.heso.transaction.outer;

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
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 根据是否单品是否商品查询衣橱类型数量
 * @author Administrator
 *
 */
public class Transaction_001098 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001098.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String isSuit = xmlBody.selectSingleNode("isSuit").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			String isGood = xmlBody.selectSingleNode("isGood").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String season = xmlBody.selectSingleNode("season").getText();
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			
			 
			//返回数据
			StringBuffer sb = new StringBuffer();
	  
			WardrobeServiceReturnObject waro = new WardrobeService().getTypeAndCount(account,isSuit,isGood,season);
			if(waro.getCode().equals("000000")){
				for(WardrobeDTO dto:waro.getWardrobeList()){
					sb.append("<item>");
					sb.append("<typeName>"+dto.getName()+"</typeName>");
					sb.append("<count>"+dto.getCount()+"</count>");
					sb.append("<type>"+dto.getType()+"</type>");   
					sb.append("<image>"+dto.getImage()+"</image>");
					sb.append("<sex>"+dto.getSex()+"</sex>");
 					sb.append("</item>");
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
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>"); 
		sb.append("<head>");
		sb.append("<type>001098</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<isSuit>1</isSuit>");
		sb.append("<isGood> </isGood>");
		sb.append("<season>1,2</season>");
		sb.append("<account>0000000000000909</account>");
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
