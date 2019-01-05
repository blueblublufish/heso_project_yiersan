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
 * ���û���Ϣ¼��
 * 
 * @author xujun
 * 
 */
public class Transaction_001115 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001115.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			//�ͻ�������Ϣ
			String regionId = xmlBody.selectSingleNode("regionId").getText();

			String name = xmlBody.selectSingleNode("name").getText();
			String phone = xmlBody.selectSingleNode("phone").getText();
			String address = xmlBody.selectSingleNode("address").getText();
			//String custmerStyle = xmlBody.selectSingleNode("cusStyle").getText();//�ͻ�����
			String sex = xmlBody.selectSingleNode("sex").getText();
			
			//�����������
			String carree = xmlBody.selectSingleNode("carree").getText();//��ҵ
			String identity = xmlBody.selectSingleNode("identity").getText();//���
			String scene= xmlBody.selectSingleNode("scene").getText();//����ϯ����
			String sceneForMan = xmlBody.selectSingleNode("sceneForMan").getText();//���ڶ���
			String wantIpstyle1 = xmlBody.selectSingleNode("wantipstyle1").getText();//�봫�ݵ���ӡ��
			String wantIpstyle2 = xmlBody.selectSingleNode("wantipstyle2").getText();//�봫�ݵĸ�ӡ��
			//ɫ�����
			String suitColor = xmlBody.selectSingleNode("suitColor").getText();//�Ǻ�ɫ��
			String notSuitColor = xmlBody.selectSingleNode("notSuitColor").getText();//���Ǻ�ɫ��
			//������
			String straight =xmlBody.selectSingleNode("straight").getText(); //ֱ��
			//String movement = xmlBody.selectSingleNode("movement").getText();//����
			String sense = xmlBody.selectSingleNode("sense").getText();//����
			String ipStyle1 = xmlBody.selectSingleNode("ipStyle1").getText();//������ip
			String ipStyle2 = xmlBody.selectSingleNode("ipStyle2").getText();//������ip
			String bodyStyle = xmlBody.selectSingleNode("bodyStyle").getText();//����
			String bodynotsuit = xmlBody.selectSingleNode("bodynotsuit").getText();//�����ص�
			
 			
			//����
			String birthday  = xmlBody.selectSingleNode("birthday").getText();//����
			String birthday2  = xmlBody.selectSingleNode("birthday2").getText();//����������
			String birthType = xmlBody.selectSingleNode("birthType").getText();//��������
			String companyName = xmlBody.selectSingleNode("companyName").getText();//��˾����
			

		
			
			/*InOrderMan inOrderMan = new InOrderMan();
			inOrderMan.setScene(scene);
			inOrderMan.setSceneForMan(sceneForMan);
			inOrderMan.setWantIpstyle(wantIpstyle);
			inOrderMan.setSuitColor(suitColor);
			inOrderMan.setNotSuitColor(notSuitColor);
			inOrderMan.setStraight(straight);
			inOrderMan.setMovement(movement);
			inOrderMan.setSense(sense);
			inOrderMan.setIpStyle(ipStyle);
			inOrderMan.setBodynotsuit(bodynotsuit);
			inOrderMan.setBodyStyle(bodyStyle);
			inOrderMan.setIdentity(identity);
			inOrderMan.setCarree(carree);*/

			InOrderMan inOrderMan = new InOrderMan();
			inOrderMan.setIdentity(identity);
			inOrderMan.setCarree(carree);
			inOrderMan.setScene(scene);
			inOrderMan.setSceneForMan(sceneForMan);
			inOrderMan.setWantIpstyle1(wantIpstyle1);
			inOrderMan.setWantIpstyle2(wantIpstyle2);
			inOrderMan.setSuitColor(suitColor);
			inOrderMan.setNotSuitColor(notSuitColor);
			inOrderMan.setStraight(straight);
			inOrderMan.setMovement("0");
			inOrderMan.setSense(sense);
			inOrderMan.setIpStyle1(ipStyle1);
			inOrderMan.setIpStyle2(ipStyle2);
			inOrderMan.setBodynotsuit(bodynotsuit);
			inOrderMan.setBodyStyle(bodyStyle);
			inOrderMan.setChenyi("0");
			inOrderMan.setXizhuang("0");
			inOrderMan.setXixku("0");
			inOrderMan.setXiema("0");
			inOrderMan.setHeigth("0");
			inOrderMan.setWeigth("0");
			inOrderMan.setLingwei("0");
			inOrderMan.setXiongwei("0");
			inOrderMan.setZhongyaowei("0");
			inOrderMan.setYoawei("0");
			inOrderMan.setTuigenwei("0");
			inOrderMan.setTunwei("0");
			inOrderMan.setTongdang("0");
			inOrderMan.setShangbiwei("0");
			inOrderMan.setShangbiwei("0");
			inOrderMan.setShouwanwei("0");
			inOrderMan.setZongjiankuan("0");
			inOrderMan.setYouxiuchang("0");
			inOrderMan.setQianjiankuan("0");
			inOrderMan.setZuoxiuchang("0");
			inOrderMan.setHouyaojiechang("0");
			inOrderMan.setHouyichang("0");
			inOrderMan.setHouyaogao("0");
			inOrderMan.setQianyaojiechang("0");
			inOrderMan.setQianyaogao("0");
			inOrderMan.setZuokuchang("0");
			inOrderMan.setYoukuchang("0");
			inOrderMan.setQunchang("0");
			inOrderMan.setXiaotuiwei("0");
		    inOrderMan.setDatuiwei("0");
		    inOrderMan.setJiaochangdu("0");
		    inOrderMan.setJiaokuan("0");
		    inOrderMan.setBocu("0");
		    inOrderMan.setTouwei("0");
		    inOrderMan.setRenti("0");
		    inOrderMan.setZuojian("0");
		    inOrderMan.setYoujian("0");
		    inOrderMan.setDuxing("0");
		    inOrderMan.setShoubi("0");
		    inOrderMan.setTunxing("0");
		    inOrderMan.setBeixing("0");			
			inOrderMan.setBirthday(birthday);
			inOrderMan.setBirthday2(birthday2);
			inOrderMan.setBirthType(birthType);
			inOrderMan.setCompanyName(companyName);
 			
			
			
			
		    String string = new YesUserService().autoRegister(phone, name, address, sex, regionId, inOrderMan);
			
			
			
			
			
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// ���÷�������
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
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001115</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><name>����2��</name><phone>15014606141</phone><regionId>120103</regionId><address>222</address><sex>1</sex><carree>2</carree><identity>2</identity><scene>4</scene><sceneForMan>1</sceneForMan><wantipstyle1>3</wantipstyle1><wantipstyle2>2</wantipstyle2><suitColor>1,1</suitColor><notSuitColor>1</notSuitColor><sense>1</sense><straight>1</straight><bodyStyle>7</bodyStyle><bodynotsuit>1</bodynotsuit><ipStyle1>2</ipStyle1><ipStyle2>7</ipStyle2></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001115</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5����ǩ��</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<regionId>4401</regionId>");
		sb.append("<name>11</name>");
		sb.append("<phone>18828128291</phone>");
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
