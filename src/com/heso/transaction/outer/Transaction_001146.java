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
 * 补充年龄职业测试
 * @author qinjianzhao
 *
 */
public class Transaction_001146 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001146.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String age = arg0.selectSingleNode("age").getText();
			String industry = arg0.selectSingleNode("industry").getText();
			
			String account = arg0.selectSingleNode("account").getText();
 			//验证token
		 
			StringBuffer data = new StringBuffer();
		 
			String code = new ConsumeOrder().updateQaTest(age, industry, account);
				 
			String xmlStrBody = super.buildResp(code, data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
