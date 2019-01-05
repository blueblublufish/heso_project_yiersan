package com.heso.transaction.outer;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 获取行程套装
 * @author Administrator
 *
 */
public class Transaction_001089 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001089.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
		 
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			WardrobeServiceReturnObject wsro = new WardrobeService().getTripsuit(account);
			
			
			//处理数据
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			if(wsro.getCode().equals("000000")){
				if(wsro.getTripsuitList()!=null&&wsro.getTripsuitList().size()!=0){
					
					for(TripsuitDTO dto : wsro.getTripsuitList()){
						sb.append("<tripSuit>");
						sb.append("<tripSuitId>"+dto.getId()+"</tripSuitId>");
						sb.append("<tripDate>"+dto.getTripDate()+"</tripDate>");
						sb.append("<temperature>"+dto.getTemperatuure()+"</temperature>");
						sb.append("<createTime>"+dto.getCreateTime()+"</createTime>");
						sb.append("<scene>"+dto.getScene()+"</scene>");
						sb.append("<suitFlag>"+dto.getSuit_flag()+"</suitFlag>");
						sb.append("<wardrobeId>"+dto.getWardrobeId()+"</wardrobeId>");
						sb.append("<productId>"+dto.getProductId()+"</productId>");
						sb.append("<account>"+dto.getAccount()+"</account>");
						sb.append("<place>"+dto.getPlace()+"</place>");
						sb.append("<style>"+dto.getStyle()+"</style>");
						sb.append("<productList>");
						if(dto.getProductList()!=null&&dto.getProductList().size()!=0){
							List<TripsuitDTO> list = dto.getProductList();
							Collections.shuffle(list);
							for(TripsuitDTO td :list){
								sb.append("<productDetail>");
								sb.append("<productDetailId>"+td.getProductId()+"</productDetailId>");
								sb.append("<image>"+td.getImage()+"</image>");
								sb.append("<name>"+td.getName()+"</name>");
								sb.append("<price>"+td.getPrice()+"</price>");
								sb.append("<discount_price>"+td.getPrice2()+"</discount_price>");
								sb.append("</productDetail>");
							} 
						}  
						sb.append("</productList>");
						sb.append("<warbrobeList>");
						if(dto.getWardrobeList()!=null&&dto.getWardrobeList().size()!=0){
							List<TripsuitDTO> list2 = dto.getWardrobeList();
							Collections.shuffle(list2);
							for(TripsuitDTO td :list2){
								sb.append("<warbrobeDetail>");
								sb.append("<warbrobeDetailId>"+td.getWardrobeId()+"</warbrobeDetailId>");
								sb.append("<image>"+td.getImage()+"</image>");
								sb.append("<name>"+td.getName()+"</name>");
								sb.append("</warbrobeDetail>");
							}
						}
						sb.append("</warbrobeList>");
						sb.append("</tripSuit>");
						
					} 
				}else {
					sb.append("<tripSuit></tripSuit>");
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
		sb.append("<type>001089</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
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
