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
 * ���ı�ǩ����״̬��Ϊ2ʱ�������
 * @author qinjianzhao
 *
 */
public class Transaction_001142 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001142.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//��ȡ��Ϣ
			String biaoqianOrderId = arg0.selectSingleNode("biaoqianOrderId").getText().trim();
			String status = arg0.selectSingleNode("status").getText();
 			 
			 
			//������Ϣ
		 
			StringBuffer data = new StringBuffer();
		 
		 
			String code = new ConsumeOrder().changeBiaoqianOrderStatus(biaoqianOrderId, status) ;
				 
			String xmlStrBody = super.buildResp(code, data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
