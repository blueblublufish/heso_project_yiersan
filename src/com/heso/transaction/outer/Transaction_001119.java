package com.heso.transaction.outer;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
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
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass;
 

/**
 * 录入标准号
 * 
 * @author xujun
 * 
 */
public class Transaction_001119 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001119.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			String biaozhunhao = xmlBody.selectSingleNode("biaozhunhao").getText();
			String orderID = xmlBody.selectSingleNode("orderId").getText();
			String pinlei = xmlBody.selectSingleNode("pinlei").getText();
			String biaozhunhao_code = xmlBody.selectSingleNode("biaozhunhao_code").getText();
			String chimaquyu  = xmlBody.selectSingleNode("chimaquyu").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			
		    String string = new YesUserService().addBiaoZhun(account,chimaquyu,biaozhunhao, biaozhunhao_code, orderID, pinlei);
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
		/*	if (account == null)
				code = "900010"; 
			else {
				code = "000000";
				sb.append("<account>"+account+"</account>");
			}
*/
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
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001119</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><orderId>0000000000004498</orderId><pinlei>130000</pinlei><producId>D18TZ0290</producId><account>0000000000000918</account><chimaquyu>10206</chimaquyu><sex>0</sex><biaozhunhao>155/90A</biaozhunhao><biaozhunhao_code>130032:0,130047:-8,130033:0,130034:1</biaozhunhao_code></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001115</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<regionId>4401</regionId>");
		sb.append("<name>11</name>");
		sb.append("<phone>18828128281</phone>");
		sb.append("<address>1</address>");
		sb.append("<custmerStyle>1</custmerStyle>");
		sb.append("<sex>1</sex>");
		
		sb.append("<scene>11</scene>");
		sb.append("<sceneForMan>11</sceneForMan>");
		sb.append("<wantipstyle>11</wantipstyle>");
		
		sb.append("<suitColor>11</suitColor>");
		sb.append("<notSuitColor>11</notSuitColor>");
		
		sb.append("<straight>1</straight>");
		sb.append("<movement>1</movement>");
		sb.append("<sense>11</sense>");
		sb.append("<ipStyle>1</ipStyle>");
		sb.append("<bodyStyle>11</bodyStyle>");
		sb.append("<bodynotsuit>1</bodynotsuit>");
		
		sb.append("<chenyi>11</chenyi>");
		sb.append("<xizhuang>1</xizhuang>");
		sb.append("<xixku>1</xixku>");
		sb.append("<xiema>1</xiema>");
		
		sb.append("<height>1</height>");
		sb.append("<weight>1</weight>");
		sb.append("<lingwei>1</lingwei>");
		sb.append("<xiongwei>1</xiongwei>");
		sb.append("<zhongyaowei>1</zhongyaowei>");
		sb.append("<yaowei>1</yaowei>");
		sb.append("<tuigenwei>1</tuigenwei>");
		sb.append("<tunwei>1</tunwei>");
		sb.append("<tongdang>1</tongdang>");
		sb.append("<shangbiwei>1</shangbiwei>");
		sb.append("<shouwanwei>1</shouwanwei>");
		sb.append("<zongjiankuan>1</zongjiankuan>");
		sb.append("<youxiuchang>1</youxiuchang>");
		sb.append("<qianjiankuan>1</qianjiankuan>");
		sb.append("<identity>1</identity>");
		sb.append("<houyaojiechang>1</houyaojiechang>");
		sb.append("<houyichang>1</houyichang>");
		sb.append("<houyaogao>1</houyaogao>");
		sb.append("<qianyaojiechang>1</qianyaojiechang>");
		sb.append("<qianyaogao>1</qianyaogao>");
		sb.append("<zuokuchang>1</zuokuchang>");
		sb.append("<youkuchang>1</youkuchang>");
		sb.append("<qunchang>1</qunchang>");
		sb.append("<xiaotuikuchang>1</xiaotuikuchang>");
		sb.append("<xiaotuiwei>1</xiaotuiwei>");
		sb.append("<datuiwei>1</datuiwei>");
		sb.append("<jiaochangdu>1</jiaochangdu>");
		sb.append("<jiaokuan>1</jiaokuan>");
		sb.append("<bocu>1</bocu>");
		sb.append("<touwei>1</touwei>");
		sb.append("<renti>1</renti>");
		sb.append("<zuojian>1</zuojian>");
		sb.append("<carree>1</carree>");
		sb.append("<duxing>1</duxing>");
		sb.append("<shoubi>1</shoubi>");
		sb.append("<tunxing>1</tunxing>");
		sb.append("<beixing>1</beixing>");
 		 
		sb.append("<cusStyle>11</cusStyle>");	
 		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ss.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
