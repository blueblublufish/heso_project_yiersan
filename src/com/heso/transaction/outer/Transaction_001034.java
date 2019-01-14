package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 支付订单
 * @author qinjianzhao
 *
 */
public class Transaction_001034 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001034.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String orderIds = arg0.selectSingleNode("orderIds").getText().trim();
			String account = arg0.selectSingleNode("account").getText();
			String payType = arg0.selectSingleNode("payType").getText();
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
				ConsumeOrderReturnObject coro = new ConsumeOrder().payOrder(account, Strs,payType);
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
				}
				
			String xmlStrBody = super.buildResp(coro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
