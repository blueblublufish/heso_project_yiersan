package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**查询用户资料
 * @author xujun
 *
 */
public class Transaction_001155 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001155.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();

			//校验token
			String token = xmlBody.selectSingleNode("token").getText();
			if(!super.tokenAuth(account, token))
				return super.ERROR_TOKEN;
			
			// 查询资料
			UserServiceReturnObject usro = new YesUserService().getUserData(account);

			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (usro.getCode().equals("000000")) {
				UserDataObject udo = usro.getUdo();
				sb.append("<userId>" + udo.getUser_id() + "</userId>");
				sb.append("<userName>" + udo.getUser_name() + "</userName>");
				sb.append("<mobile>" + udo.getMobile() + "</mobile>");
				sb.append("<idType>" + udo.getId_type() + "</idType>");
				sb.append("<idNo>" + udo.getId_no() + "</idNo>");
				sb.append("<address>" + udo.getAddress() + "</address>");
				sb.append("<email>" + udo.getEmail() + "</email>");
				sb.append("<fax>" + udo.getFax()+ "</fax>");
				sb.append("<tel>" + udo.getTel()+ "</tel>");
				sb.append("<postcode>" + udo.getPostcode()+ "</postcode>");
				sb.append("<sex>" + udo.getSex()+ "</sex>");
				sb.append("<birthday>" + udo.getBirthday()+ "</birthday>");
				sb.append("<regionId>" + udo.getRegion_id()+ "</regionId>");
				sb.append("<image>"+udo.getImage()+"</image>");
				sb.append("<comment>"+udo.getComment()+"</comment>");
				sb.append("<level>"+udo.getLevel()+"</level>");
				sb.append("<registerTime>"+udo.getRegisterTime()+"</registerTime>");
				sb.append("<cardNo>"+udo.getCard_no()+"</cardNo>");
			}
			String xmlBodyStr = super.buildResp(usro.getCode(), sb.toString());
			return xmlBodyStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001090</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account></account><token></token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001155</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>"); 
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<account>0000000000000909</account>");
		sb.append("<page>1</page>");
		sb.append("<pageSize>1</pageSize>");
		sb.append("<style>1</style>");
		sb.append("<scene>1</scene>");
		sb.append("<pinlei></pinlei>");
		sb.append("<productName></productName>");
		
		sb.append("<supplier>6</supplier>");
		sb.append("<paixu></paixu>");
		sb.append("<token>0</token>");	
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
