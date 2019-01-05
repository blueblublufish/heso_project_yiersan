package com.heso.transaction.outer;

import oracle.net.aso.d;

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
 * 根据设计师ID查询设计师详情与排班表
 * @author Administrator
 *
 */
public class Transaction_001092 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001092.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String designerId = xmlBody.selectSingleNode("designerId").getText();
			String serviceID = xmlBody.selectSingleNode("serviceId").getText();
		
		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
		 
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			
			MallServiceReturnObject msro = new MallService().getDesignerDate(designerId,serviceID);
			
		 
		
			//返回数据 
			StringBuffer sb = new StringBuffer();
			if(msro.getCode().equals("000000")){
				if(msro.getMpList()!=null&&msro.getMpList().size()!=0){
					
					for(MemberProduct mp:msro.getMpList()){
						sb.append("<designer_service>");
						sb.append("<designerId>"+mp.getDesignerId()+"</designerId>");
						sb.append("<designerDesc>"+mp.getDesignerDesc()+"</designerDesc>");
						sb.append("<designation>"+mp.getDesignation()+"</designation>");
						sb.append("<designerName>"+mp.getDesignerName()+"</designerName>");
						sb.append("<designerImage>"+mp.getImage()+"</designerImage>");
						sb.append("<url>"+mp.getUrl()+"</url>");
						sb.append("<place_n>"+mp.getPlace_n()+"</place_n>");
						sb.append("<place_cn>"+mp.getPlace_cn()+"</place_cn>");
						sb.append("<flag>"+mp.getFlag()+"</flag>");
						sb.append("<max_num>"+mp.getMaxNum()+"</max_num>");
						if(mp.getmList()!=null&&mp.getmList().size()!=0){
							
							for(MemberProduct mmp:mp.getmList()){
								sb.append("<date_place_Item>");
								sb.append("<date_place_id>"+mmp.getId()+"</date_place_id>");
								sb.append("<place>"+mmp.getPlace()+"</place>");
								sb.append("<date_place>"+mmp.getDate_place()+"</date_place>");
								sb.append("<status>"+mmp.getStatus()+"</status>");
								sb.append("<bookNum>"+mmp.getBookNum()+"</bookNum>");
								sb.append("</date_place_Item>");
							}
						}
						
						sb.append("</designer_service>");
						
					}
				}else {
					sb.append("<date_place_Item></date_place_Item>");
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
		sb.append("<type>001092</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId>3333341</designerId>");
		sb.append("<serviceId>5</serviceId>");
		sb.append("<account></account>");
		sb.append("<flag>1</flag>");
		sb.append("<token></token>");	
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
