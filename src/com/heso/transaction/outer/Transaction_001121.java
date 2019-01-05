package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import oracle.net.aso.i;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.cart.entity.CheckCartReturnObject;
import com.heso.service.cart.entity.CheckObject;
import com.heso.service.funds.order.entity.FundsOrderReturnObject;
import com.heso.service.funds.order.recharge.OrderRecharge;
import com.heso.service.funds.order.recharge.entity.OrderRechargeItem;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.ReportDTO;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 衣橱诊断报告
 * 
 * @author xujun
 * 
 */
public class Transaction_001121 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001121.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String token =xmlBody.selectSingleNode("token").getText();
			UserServiceReturnObject usro = new YesUserService().getUserData(account);

			String sex = usro.getUdo().getSex();
			List<List<String>>  lists = new WardrobeService().wardrobeReport(account, sex);
			List<ReportDTO> dtos = new WardrobeService().wardrobeReport2(account, sex);
			List<ReportDTO> dtos2 = new WardrobeService().wardrobeReport3(account, sex);
			List<ReportDTO> dtos4 = new WardrobeService().wardrobeReport4(account, sex);
  			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			sb.append("<shangxiazhuang>");
			sb.append("<shangyiName>" +dtos4.get(0).getName()+"</shangyiName>");
			sb.append("<shangyiCount>" +dtos4.get(0).getCount()+"</shangyiCount>");
			sb.append("<xiazhuangName>" +dtos4.get(1).getName()+"</xiazhuangName>"); 
			sb.append("<xiazhuangCount>" +dtos4.get(1).getCount()+"</xiazhuangCount>");
			sb.append("</shangxiazhuang>");
			//二级单品的统计
			sb.append("<secondDanpins>" );
			for(List<String> list : lists){
				sb.append("<secondDanpin>");
				sb.append("<secondName>"+list.get(0)+"</secondName>");
				sb.append("<pinlei>"+list.get(1)+"</pinlei>");
				sb.append("<secondType>"+list.get(2)+"</secondType>");
				sb.append("<count>"+list.get(3)+"</count>");
				sb.append("<countFlag>"+list.get(4)+"</countFlag>");
				sb.append("</secondDanpin>");
			}
			sb.append("</secondDanpins>" );
			
			//品类统计
			sb.append("<danpins>" );
			sb.append("<danpinCount>"+dtos4.get(2).getCount()+"</danpinCount>");
			for(ReportDTO dto : dtos){
				sb.append("<danpin>");
				sb.append("<name>"+dto.getName()+"</name>");
				sb.append("<pinlei>"+dto.getType()+"</pinlei>");
				sb.append("<pinleiType>"+dto.getNumber()+"</pinleiType>");
				sb.append("<count>"+dto.getCount()+"</count>");
 				sb.append("</danpin>");
			}
			sb.append("</danpins>" );
			//套装统计
			sb.append("<taozhuangs>" );
			sb.append("<taozhuangCount>"+dtos4.get(3).getCount()+"</taozhuangCount>");
			for(int i= 0; i<4;i++){
				sb.append("<scene>");
				sb.append("<name>"+dtos2.get(i).getName()+"</name>");
				sb.append("<pinlei>"+dtos2.get(i).getType()+"</pinlei>");
				sb.append("<pinleiType>"+dtos2.get(i).getNumber()+"</pinleiType>");
				sb.append("<count>"+dtos2.get(i).getCount()+"</count>");
				sb.append("<countFlag>"+dtos2.get(i).getIsEnough()+"</countFlag>");
 				sb.append("</scene>");
			}
			for(int i= 4; i<dtos2.size();i++){
				sb.append("<style>");
				sb.append("<name>"+dtos2.get(i).getName()+"</name>");
				sb.append("<pinlei>"+dtos2.get(i).getType()+"</pinlei>");
				sb.append("<pinleiType>"+dtos2.get(i).getNumber()+"</pinleiType>");
				sb.append("<count>"+dtos2.get(i).getCount()+"</count>");
				sb.append("<countFlag>"+dtos2.get(i).getIsEnough()+"</countFlag>");
 				sb.append("</style>");
			}
			sb.append("</taozhuangs>" );
 
			String xmlBodyStr = super.buildResp(code, sb.toString());
			return xmlBodyStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001121</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
  		sb.append("<account>0000000000000910</account>");
 		sb.append("<sex>0</sex>");
		sb.append("<token>1111111111</token>");	
		sb.append("<sex>0</sex>");	
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
