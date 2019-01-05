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
 * �����³���˱�ǩ�ɵ�
 * @author qinjianzhao
 *
 */
public class Transaction_001144 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001144.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//��ȡ��Ϣ
			String productIds = arg0.selectSingleNode("productIds").getText().trim();
			String account = arg0.selectSingleNode("account").getText();
 			//��֤token
			 
			//������Ϣ
			List<String> Strs = new ArrayList<String>();
			StringBuffer data = new StringBuffer();
			String[] strList = productIds.split(";");
			for(int i =0 ; i<strList.length ; i++){
				Strs.add(strList[i]);
			}
			String code = new ConsumeOrder().addYichuBiaoqianPaidan(Strs, account) ;
				 
			String xmlStrBody = super.buildResp(code, data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}