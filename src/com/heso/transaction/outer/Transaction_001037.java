package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * ��ȡ�����۸�
 * @author qinjianzhao
 *
 */
public class Transaction_001037 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001037.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//��ȡ��Ϣ
			String reregionalId = arg0.selectSingleNode("regionalId").getText();
			String amount = arg0.selectSingleNode("amount").getText();
			String orderId = "";
			//������Ϣ
			ConsumeOrderReturnObject coro = new ConsumeOrder().logisticsPay(reregionalId,Double.parseDouble(amount),orderId);
			//������Ϣ
			StringBuffer sb = new StringBuffer();
			if("000000".equals(coro.getCode())){
				sb.append("<price>"+coro.getReccount()+"</price>");
			}
			String xmlStrBody = super.buildResp(coro.getCode(), sb.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return super.ERROR_RETURN;
	}

}
