package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.lf5.viewer.configure.MRUFileManager;
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
 * 获取服务对应设计师列表
 * @author Administrator
 *
 */
public class Transaction_001091 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001091.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String serviceId = xmlBody.selectSingleNode("serviceId").getText();
			
		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
		 
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			
			MallServiceReturnObject msro = new MallService().getDesignerByServiceId(serviceId);
			
			
 			 
			
			//处理数据
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			if(msro.getCode().equals("000000")){
				if(msro.getMpList()!=null&&msro.getMpList().size()!=0){	
					for(MemberProduct mp:msro.getMpList()){
						sb.append("<designerItem>");
						sb.append("<serviceId>"+mp.getId()+"</serviceId>");
						sb.append("<designerId>"+mp.getDesignerId()+"</designerId>");
						sb.append("<designerDesc>"+mp.getDesignerDesc()+"</designerDesc>");
						sb.append("<designerName>"+mp.getDesignerName()+"</designerName>");
						sb.append("<designerImage>"+mp.getImage()+"</designerImage>");
						sb.append("<designation>"+mp.getDesignation()+"</designation>");
						sb.append("<url>"+mp.getUrl()+"</url>");
						sb.append("<price>"+mp.getPrice()+"</price>");
						sb.append("<unit>"+mp.getUnit()+"</unit>");
						sb.append("</designerItem>");	
					}
				}else {
					sb.append("<designerItem></designerItem>");
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
		sb.append("<type>001091</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<serviceId>1</serviceId>");
		sb.append("<account>0000000000000016</account>");
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
