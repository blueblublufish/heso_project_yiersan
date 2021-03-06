package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 获取服务列表
 * @author Administrator
 *
 */
public class Transaction_001090 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001090.class);
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
			
			MallServiceReturnObject msro = new MallService().getServiceList();
			
			
 			 
			
			//处理数据
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			if(msro.getCode().equals("000000")){
				if(msro.getMpList()!=null&&msro.getMpList().size()!=0){
					for(MemberProduct mp:msro.getMpList()){
						sb.append("<serviceItem>");
						sb.append("<serviceId>"+mp.getId()+"</serviceId>");
						sb.append("<desc>"+mp.getDesc()+"</desc>");
						sb.append("<designerIds>"+mp.getDesignerIds()+"</designerIds>");
						sb.append("<serviceName>"+mp.getName()+"</serviceName>");
						sb.append("<image>"+mp.getImage()+"</image>"); 
						sb.append("<serviceImage>"+mp.getServiceImage()+"</serviceImage>");
						sb.append("<serviceType>"+mp.getServicetype()+"</serviceType>");
						sb.append("<max_num>"+mp.getMax_num()+"</max_num>");
						sb.append("<tupianmiaoshu>"+mp.getTupianmiaoshu()+"</tupianmiaoshu>");
						sb.append("<zhuyishixiang>"+mp.getZhuyishixiang()+"</zhuyishixiang>");
						sb.append("<flag>"+mp.getFlag()+"</flag>");
						sb.append("<price>"+mp.getPrice()+"</price>");
						sb.append("</serviceItem>");
						
					}
				}else {
					sb.append("<serviceItem></serviceItem>");

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
		sb.append("<type>001090</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<productId>16CC00002</productId>");
		sb.append("<account>0000000000000909</account>");
		sb.append("<token>369b92bff271ff81fb7460695f40b9be</token>");	
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
