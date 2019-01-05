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
 * �û��������¼��
 * 
 * @author xujun
 * 
 */
public class Transaction_001117 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001117.class);

	@Override 
	public String execute(Node xmlBody, String IPAddress) {
		try {
			//�ͻ�������Ϣ
 
			String name = xmlBody.selectSingleNode("name").getText();
			String phone = xmlBody.selectSingleNode("phone").getText();
	 
			//��������
			String chenyi = xmlBody.selectSingleNode("chenyi").getText();
			String xizhuang = xmlBody.selectSingleNode("xizhuang").getText();
			String xixku = xmlBody.selectSingleNode("xixku").getText();
			String xiema =xmlBody.selectSingleNode("xiema").getText();
			 
			//�������
			String height = xmlBody.selectSingleNode("height").getText();//���
			String weight = xmlBody.selectSingleNode("weight").getText();//����
			String lingwei = xmlBody.selectSingleNode("lingwei").getText();//��Χ
			String xiongwei = xmlBody.selectSingleNode("xiongwei").getText();//��Χ
			String zhongyaowei =xmlBody.selectSingleNode("zhongyaowei").getText();//����Χ
			String yaowei = xmlBody.selectSingleNode("yaowei").getText();//��Χ
			String tuigenwei = xmlBody.selectSingleNode("tuigenwei").getText();//�ȸ�Χ
			String tunwei = xmlBody.selectSingleNode("tunwei").getText();//��Χ
			String tongdang = xmlBody.selectSingleNode("tongdang").getText();//ͨ��
			String shangbiwei = xmlBody.selectSingleNode("shangbiwei").getText();//�ϱ�Χ
			String shouwanwei = xmlBody.selectSingleNode("shouwanwei").getText();//����Χ
			String zongjiankuan = xmlBody.selectSingleNode("zongjiankuan").getText();//�ܼ��
			String youxiuchang = xmlBody.selectSingleNode("youxiuchang").getText();//���䳤
			String qianjiankuan = xmlBody.selectSingleNode("qianjiankuan").getText();//ǰ���
			String zuoxiuchang = xmlBody.selectSingleNode("zuoxiuchang").getText();//���䳤
			String houyaojiechang = xmlBody.selectSingleNode("houyaojiechang").getText(); //�����ڳ�
			String houyichang = xmlBody.selectSingleNode("houyichang").getText();//���³�
			String houyaogao = xmlBody.selectSingleNode("houyaogao").getText();//������
			String qianyaojiechang = xmlBody.selectSingleNode("qianyaojiechang").getText();//ǰ���ڳ�
			String qianyaogao = xmlBody.selectSingleNode("qianyaogao").getText();//ǰ����
			String zuokuchang = xmlBody.selectSingleNode("zuokuchang").getText();//��㳤
			String youkuchang = xmlBody.selectSingleNode("youkuchang").getText();//�ҿ㳤
			String qunchang = xmlBody.selectSingleNode("qunchang").getText();//ȹ��
			String xiaotuikuchang = xmlBody.selectSingleNode("xiaotuikuchang").getText();//С�ȿ㳤
			String xiaotuiwei = xmlBody.selectSingleNode("xiaotuiwei").getText();//С��Χ
			String datuiwei = xmlBody.selectSingleNode("datuiwei").getText();//����Χ
			String jiaochangdu = xmlBody.selectSingleNode("jiaochangdu").getText();//�ų���
			String jiaokuan = xmlBody.selectSingleNode("jiaokuan").getText();//�ſ�
			String bocu =xmlBody.selectSingleNode("bocu").getText();//����
			String touwei = xmlBody.selectSingleNode("touwei").getText();//ͷΧ
			
			//������Ϣ
			String renti = xmlBody.selectSingleNode("renti").getText();//����
			String zuojian = xmlBody.selectSingleNode("zuojian").getText();//���
			String youjian = xmlBody.selectSingleNode("youjian").getText();//�Ҽ�
			String duxing =xmlBody.selectSingleNode("duxing").getText();//����
			String shoubi = xmlBody.selectSingleNode("shoubi").getText();//�ֱ�
			String tunxing = xmlBody.selectSingleNode("tunxing").getText();//����
			String beixing = xmlBody.selectSingleNode("beixing").getText();//����
			String jianbu = xmlBody.selectSingleNode("jianbu").getText();//�粿
			String jingxing = xmlBody.selectSingleNode("jingxing").getText();//����
			String jianxiagu = xmlBody.selectSingleNode("jianxiagu").getText();//��xiagu
			String xiongxing = xmlBody.selectSingleNode("xiongxing").getText();//����
			//20180606
			String chengyiteti = xmlBody.selectSingleNode("chengyiteti").getText();//��������
			

			//�ϴ�������
			String zheng = xmlBody.selectSingleNode("zheng").getText();
			String fan = xmlBody.selectSingleNode("fan").getText();
			String ce = xmlBody.selectSingleNode("ce").getText();
			String zhengTao = xmlBody.selectSingleNode("zhengTao").getText();
			String fanTao = xmlBody.selectSingleNode("fanTao").getText();
			String ceTao = xmlBody.selectSingleNode("ceTao").getText();
			String taoma = xmlBody.selectSingleNode("taoma").getText();
			//20180606
			String images = xmlBody.selectSingleNode("images").getText();//��ͼ�ϴ�
			String xizhuangdingdan =  xmlBody.selectSingleNode("xizhuangdingdan").getText();//��װ������
			String xizhuangjiajian =  xmlBody.selectSingleNode("xizhuangjiajian").getText();//��װ�Ӽ�
			String xikudingdan =  xmlBody.selectSingleNode("xikudingdan").getText();//���㶩����
			String xikujiajian =  xmlBody.selectSingleNode("xikujiajian").getText();//����Ӽ�
			String xiqundingdan =  xmlBody.selectSingleNode("xiqundingdan").getText();//��ȹ������
			String xiqunjiajian =  xmlBody.selectSingleNode("xiqunjiajian").getText();//��ȹ�Ӽ�
	 			
			
			InOrderMan inOrderMan = new InOrderMan();
			 
			inOrderMan.setChenyi(chenyi);
			inOrderMan.setXizhuang(xizhuang);
			inOrderMan.setXixku(xixku);
			inOrderMan.setXiema(xiema);
			inOrderMan.setHeigth(height);
			inOrderMan.setWeigth(weight);
			inOrderMan.setLingwei(lingwei);
			inOrderMan.setXiongwei(xiongwei);
			inOrderMan.setZhongyaowei(zhongyaowei);
			inOrderMan.setYoawei(yaowei);
			inOrderMan.setTuigenwei(tuigenwei);
			inOrderMan.setTunwei(tunwei);
			inOrderMan.setTongdang(tongdang);
			inOrderMan.setShangbiwei(shangbiwei);
			inOrderMan.setShangbiwei(shangbiwei);
			inOrderMan.setShouwanwei(shouwanwei);
			inOrderMan.setZongjiankuan(zongjiankuan);
			inOrderMan.setYouxiuchang(youxiuchang);
			inOrderMan.setQianjiankuan(qianjiankuan);
			inOrderMan.setZuoxiuchang(zuoxiuchang);
			inOrderMan.setHouyaojiechang(houyaojiechang);
			inOrderMan.setHouyichang(houyichang);
			inOrderMan.setHouyaogao(houyaogao);
			inOrderMan.setQianyaojiechang(qianyaojiechang);
			inOrderMan.setQianyaogao(qianyaogao);
			inOrderMan.setZuokuchang(zuokuchang);
			inOrderMan.setYoukuchang(youkuchang);
			inOrderMan.setQunchang(qunchang);
			inOrderMan.setXiaotuiwei(xiaotuiwei);
		    inOrderMan.setDatuiwei(datuiwei);
		    inOrderMan.setJiaochangdu(jiaochangdu);
		    inOrderMan.setJiaokuan(jiaokuan);
		    inOrderMan.setBocu(bocu);
		    inOrderMan.setTouwei(touwei);
		    inOrderMan.setRenti(renti); 
		    inOrderMan.setZuojian(zuojian);
		    inOrderMan.setYoujian(youjian);
		    inOrderMan.setDuxing(duxing);
		    inOrderMan.setShoubi(shoubi);
		    inOrderMan.setTunxing(tunxing);
		    inOrderMan.setBeixing(beixing);			
			inOrderMan.setZheng(zheng);
			inOrderMan.setFan(fan);
			inOrderMan.setCe(ce);
			inOrderMan.setZhengTao(zhengTao);
			inOrderMan.setFanTao(fanTao);
			inOrderMan.setCeTao(ceTao);
			inOrderMan.setTaoma(taoma);
			inOrderMan.setJianbu(jianbu);
			inOrderMan.setJingxing(jingxing);
			inOrderMan.setJianxiagu(jianxiagu);
			inOrderMan.setXiongxing(xiongxing);
			inOrderMan.setChengyiteti(chengyiteti);
			inOrderMan.setImages(images);
			inOrderMan.setXizhuangdingdan(xizhuangdingdan);
			inOrderMan.setXizhuangjiajian(xizhuangjiajian);
			inOrderMan.setXikudingdan(xikudingdan);
			inOrderMan.setXikujiajian(xikujiajian);
			inOrderMan.setXiqundingdan(xiqundingdan);
			inOrderMan.setXiqunjiajian(xiqunjiajian);
			
			
		    String code = new YesUserService().updateProfile(inOrderMan, phone);
		   // String string = new YesUserService().autoRegister(phone, name, address, sex, regionId, inOrderMan);
			
			
			
			
			
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			 
			
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
		StringBuffer ssBuffer  = new StringBuffer();
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001117</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><name></name><phone>150146061416</phone><chenyi>1</chenyi><xizhuang>1</xizhuang><xixku>1</xixku><xiema>1</xiema><height>12</height><weight>23</weight><lingwei>34</lingwei><xiongwei>4</xiongwei><zhongyaowei>5</zhongyaowei><yaowei>6</yaowei><tuigenwei>7</tuigenwei><tunwei>8</tunwei><tongdang>9</tongdang><shangbiwei>10</shangbiwei><shouwanwei>11</shouwanwei><zongjiankuan>12</zongjiankuan><youxiuchang>13</youxiuchang><qianjiankuan>14</qianjiankuan><zuoxiuchang>15</zuoxiuchang><houyaojiechang>16</houyaojiechang><houyichang>17</houyichang><houyaogao>18</houyaogao><qianyaojiechang>19</qianyaojiechang><qianyaogao>20</qianyaogao><zuokuchang>21</zuokuchang><youkuchang>22</youkuchang><qunchang>23</qunchang><xiaotuikuchang></xiaotuikuchang><xiaotuiwei>24</xiaotuiwei><datuiwei>25</datuiwei><jiaochangdu>26</jiaochangdu><jiaokuan>27</jiaokuan><bocu>28</bocu><touwei>29</touwei><renti></renti><zuojian>10093,10095</zuojian><youjian>10068,10070</youjian><jianbu></jianbu><jianxiagu></jianxiagu><beixing>103731</beixing><xiongxing></xiongxing><tunxing>10084</tunxing><duxing>10079</duxing><shoubi>10081</shoubi><jingxing></jingxing><zheng>USER/0000000000001007/546cc8d6c78f76113d7c11f1ad7557d5.jpg</zheng><fan>USER/0000000000001007/5730278181d1f83bdaf5bf1adcc1e9a4.jpg</fan><ce>USER/0000000000001007/dd119c1fc90930b0c59f1fb22896832e.jpg</ce><zhengTao>USER/0000000000001007/c4de1957ccea7a78f0547eb99c0e1b89.jpg</zhengTao><fanTao>USER/0000000000001007/4f90686571fe7063db86074efa7ae800.jpg</fanTao><ceTao>USER/0000000000001007/df40b6a42e3d14abc7cbd3c1b6e85d28.jpg</ceTao><taoma>222</taoma></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001107</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5����ǩ��</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<regionId>4401</regionId>");
		sb.append("<name>11</name>");
		sb.append("<phone>150146061411</phone>");
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
		
		sb.append("<height>15</height>");
		sb.append("<weight>15</weight>");
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
		sb.append("<zuoxiuchang>1</zuoxiuchang>");
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
		sb.append("<youjian>1</youjian>");
		sb.append("<duxing>1</duxing>");
		sb.append("<shoubi>1</shoubi>");
		sb.append("<tunxing>1</tunxing>");
		sb.append("<beixing>1</beixing>");
 		 
		sb.append("<cusStyle>11</cusStyle>");	
 		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ssBuffer.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
