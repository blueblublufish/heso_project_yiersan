package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.common.entity.CommonType;
import com.heso.service.funds.order.consumption.OrderConsumption;
import com.heso.service.yesFunds.yesFundsService;
import com.heso.service.yesFunds.entity.yesFundsServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.heso.utility.ToolsUtil;
/**
 * 兑换积分-改（含确定订单）
 * @author Administrator
 *
 */
public class Transaction_001039 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001038.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String productId = arg0.selectSingleNode("productId").getText();
			String receiveId = arg0.selectSingleNode("receiveId").getText();
			String mobile = arg0.selectSingleNode("mobile").getText();
			String authCode = arg0.selectSingleNode("authCode").getText();
			String account = arg0.selectSingleNode("account").getText();
			//验证token
			String token = arg0.selectSingleNode("token").getText();
			if(!super.tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
		
			//处理信息
			yesFundsServiceReturnObject fsro = new yesFundsService().change(account, productId, receiveId, mobile, authCode);
			StringBuffer sb = new StringBuffer();
			if("000000".equals(fsro.getCode())){
				if(fsro.getType().equals("2")){
					sb.append("<NUM>服务号码为："+fsro.getSeleverNum()+"</NUM>");
				}else{
					sb.append("<NUM>兑换成功</NUM>");
				}
				
			}
			String xmlStrBody = super.buildResp(fsro.getCode(), sb.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
}
