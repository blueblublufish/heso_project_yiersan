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
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 进行服务预约
 * @author Administrator
 *
 */
public class Transaction_001093 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001093.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String type = xmlBody.selectSingleNode("type").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			String designerId = xmlBody.selectSingleNode("designerId").getText();
			String productId = xmlBody.selectSingleNode("productId").getText();
			String count = xmlBody.selectSingleNode("count").getText();
			String payType = xmlBody.selectSingleNode("payType").getText();
			String reciveId = xmlBody.selectSingleNode("reciveId").getText();
			String remark = xmlBody.selectSingleNode("remark").getText();
			String desigmerServices = xmlBody.selectSingleNode("desigmerServices").getText();
		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
			ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
			if("3".equals(type.trim())||"4".equals(type.trim())||"33".equals(type.trim())){
				coro = new ConsumeOrder().bookService2(account, productId, count, payType, designerId, reciveId, remark, desigmerServices,type);				

			}else {
				coro = new ConsumeOrder().bookService(account, productId, count, payType, designerId, reciveId, remark, desigmerServices);				
				
			}
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
		if(coro.getCode().equals("000000")){
			if(coro.getCooList()!=null&&coro.getCooList().size()!=0 ){
				for(ConsumeOrderObject coo:coro.getCooList()){
					sb.append("<payItem>");
					sb.append("<waiOrder>"+coo.getOrderId()+"</waiOrder>");
					sb.append("<amount>"+coo.getAmount()+"</amount>");
					sb.append("<payType>"+coo.getPaymentTerms()+"</payType>");
					sb.append("</payItem>");
				}	
			}
			
		}
		
			String xmlBodyStr = super.buildResp(coro.getCode(), sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ddBuffer = new StringBuffer();
		ddBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001093</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><token>23f0b3f75e67c4625e66035a961761</token><designerId></designerId><productId>114</productId><count>1</count><payType>1</payType><reciveId></reciveId><remark></remark><desigmerServices></desigmerServices><type>33</type></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001093</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId>3333341</designerId>");
		sb.append("<account>0000000000001749</account>");
		sb.append("<designerid></designerid>"); 
		sb.append("<count>1</count>");
		sb.append("<productId>9</productId>");
		sb.append("<reciveId>671</reciveId>");
		sb.append("<remark>234</remark>");
		sb.append("<payType>1</payType>");
		sb.append("<desigmerServices>296</desigmerServices>");
		sb.append("<token>f943cad4c318af7528885f860c0a7f</token>");	
		sb.append("<type>0</type>");	
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ddBuffer.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
	}
}
