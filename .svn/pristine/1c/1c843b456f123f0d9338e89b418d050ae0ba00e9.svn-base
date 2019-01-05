package com.heso.transaction.outer;

import org.dom4j.Node;

import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 确认收货
 * @author qinjianzhao
 *
 */
public class Transaction_001032 extends AbstractInterfaceClass {

	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String account = arg0.selectSingleNode("account").getText();
			String orderId = arg0.selectSingleNode("orderId").getText();
			//验证token
			String token = arg0.selectSingleNode("token").getText();
			if(!tokenAuth(account, token))
				throw new Exception(super.ERROR_TOKEN);
			//处理信息
			ConsumeOrderReturnObject coro = new ConsumeOrder().confirmReceive(orderId);
			StringBuffer data = new StringBuffer();
			String xmlStrBody = super.buildResp(coro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
