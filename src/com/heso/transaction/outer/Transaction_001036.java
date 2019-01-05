package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.yesFunds.yesFundsService;
import com.heso.service.yesFunds.entity.yesFundsServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * �һ����ֽ�� ͣ����
 * @author qinjianzhao
 *
 */
public class Transaction_001036 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001036.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//��ȡ��Ϣ
			String account = arg0.selectSingleNode("account").getText();
			String productId = arg0.selectSingleNode("productId").getText();
			//��֤token
			String token = arg0.selectSingleNode("token").getText();
			if(!super.tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			//������Ϣ
//			yesFundsServiceReturnObject fsro = new yesFundsService().change(account, productId);
//			//������Ϣ
//			StringBuffer sb = new StringBuffer();
//			if("000000".equals(fsro.getCode())){
//				sb.append("<orderId>"+fsro.getFsao().getOrderId()+"</orderId>");
//			}
//			String xmlStrBody = super.buildResp(fsro.getCode(), sb.toString());
//			
//			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ERROR_RETURN;
	}

}