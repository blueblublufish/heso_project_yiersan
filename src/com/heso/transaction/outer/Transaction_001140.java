package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 用衣配卡权益支付订单
 * @author qinjianzhao
 *
 */
public class Transaction_001140 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001140.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String orderIds = arg0.selectSingleNode("orderIds").getText().trim();
			String account = arg0.selectSingleNode("account").getText();
			//验证token
			String token = arg0.selectSingleNode("token").getText();
			if(!tokenAuth(account, token))
				throw new Exception(super.ERROR_TOKEN);
			//处理信息
			List<String> Strs = new ArrayList<String>();
			StringBuffer data = new StringBuffer();
			String[] strList = orderIds.split(";");
			for(int i =0 ; i<strList.length ; i++){
				Strs.add(strList[i]);
			}
				ConsumeOrderReturnObject coro = new ConsumeOrder().payOrderByQuanyi(account, Strs);
				if("000000".equals(coro.getCode())){
					List<ConsumeOrderObject> list = coro.getCooList();
					for(int i =0 ; i<list.size()-1 ; i++){
						data.append("<orders>");
						data.append("<orderId>"+list.get(i).getOrderId()+"</orderId>");
						data.append("<amount>"+list.get(i).getAmount()+"</amount>");
						data.append("<discountPrice>"+list.get(i).getDiscountPrice()+"</discountPrice>");
						data.append("</orders>");
					}
					data.append("<paymentTerms>"+list.get(0).getPaymentTerms()+"</paymentTerms>");
					data.append("<waiOrder>"+coro.getOrderId()+"</waiOrder>");
					data.append("<total>"+list.get(list.size()-1).getAmount()+"</total>");
					data.append("<desc>"+coro.getDesc()+"</desc>");
				}
				
			String xmlStrBody = super.buildResp(coro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ddBuffer = new StringBuffer();
		ddBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001140</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><token></token><orderIds>0000000000005069</orderIds></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001140</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<designerId>3333341</designerId>");
		sb.append("<account>0000000000001944</account>");
		sb.append("<designerid></designerid>"); 
		sb.append("<orderIds>X1545878512208500</orderIds>");
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
			new TransDataProcess().execute(sb.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		}
		
	}
}
