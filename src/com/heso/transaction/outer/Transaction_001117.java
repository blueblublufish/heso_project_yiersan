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
 * 用户身材数据录入
 * 
 * @author xujun
 * 
 */
public class Transaction_001117 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001117.class);

	@Override 
	public String execute(Node xmlBody, String IPAddress) {
		try {
			//客户基本信息
 
			String name = xmlBody.selectSingleNode("name").getText();
			String phone = xmlBody.selectSingleNode("phone").getText();
	 
			//常穿尺码
			String chenyi = xmlBody.selectSingleNode("chenyi").getText();
			String xizhuang = xmlBody.selectSingleNode("xizhuang").getText();
			String xixku = xmlBody.selectSingleNode("xixku").getText();
			String xiema =xmlBody.selectSingleNode("xiema").getText();
			 
			//体型诊断
			String height = xmlBody.selectSingleNode("height").getText();//身高
			String weight = xmlBody.selectSingleNode("weight").getText();//体重
			String lingwei = xmlBody.selectSingleNode("lingwei").getText();//领围
			String xiongwei = xmlBody.selectSingleNode("xiongwei").getText();//胸围
			String zhongyaowei =xmlBody.selectSingleNode("zhongyaowei").getText();//中腰围
			String yaowei = xmlBody.selectSingleNode("yaowei").getText();//腰围
			String tuigenwei = xmlBody.selectSingleNode("tuigenwei").getText();//腿根围
			String tunwei = xmlBody.selectSingleNode("tunwei").getText();//臀围
			String tongdang = xmlBody.selectSingleNode("tongdang").getText();//通档
			String shangbiwei = xmlBody.selectSingleNode("shangbiwei").getText();//上臂围
			String shouwanwei = xmlBody.selectSingleNode("shouwanwei").getText();//手腕围
			String zongjiankuan = xmlBody.selectSingleNode("zongjiankuan").getText();//总肩宽
			String youxiuchang = xmlBody.selectSingleNode("youxiuchang").getText();//右袖长
			String qianjiankuan = xmlBody.selectSingleNode("qianjiankuan").getText();//前肩宽
			String zuoxiuchang = xmlBody.selectSingleNode("zuoxiuchang").getText();//左袖长
			String houyaojiechang = xmlBody.selectSingleNode("houyaojiechang").getText(); //后腰节长
			String houyichang = xmlBody.selectSingleNode("houyichang").getText();//后衣长
			String houyaogao = xmlBody.selectSingleNode("houyaogao").getText();//后腰高
			String qianyaojiechang = xmlBody.selectSingleNode("qianyaojiechang").getText();//前腰节长
			String qianyaogao = xmlBody.selectSingleNode("qianyaogao").getText();//前腰高
			String zuokuchang = xmlBody.selectSingleNode("zuokuchang").getText();//左裤长
			String youkuchang = xmlBody.selectSingleNode("youkuchang").getText();//右裤长
			String qunchang = xmlBody.selectSingleNode("qunchang").getText();//裙长
			String xiaotuikuchang = xmlBody.selectSingleNode("xiaotuikuchang").getText();//小腿裤长
			String xiaotuiwei = xmlBody.selectSingleNode("xiaotuiwei").getText();//小腿围
			String datuiwei = xmlBody.selectSingleNode("datuiwei").getText();//大腿围
			String jiaochangdu = xmlBody.selectSingleNode("jiaochangdu").getText();//脚长度
			String jiaokuan = xmlBody.selectSingleNode("jiaokuan").getText();//脚宽
			String bocu =xmlBody.selectSingleNode("bocu").getText();//脖粗
			String touwei = xmlBody.selectSingleNode("touwei").getText();//头围
			
			//特体信息
			String renti = xmlBody.selectSingleNode("renti").getText();//人体
			String zuojian = xmlBody.selectSingleNode("zuojian").getText();//左肩
			String youjian = xmlBody.selectSingleNode("youjian").getText();//右肩
			String duxing =xmlBody.selectSingleNode("duxing").getText();//肚型
			String shoubi = xmlBody.selectSingleNode("shoubi").getText();//手臂
			String tunxing = xmlBody.selectSingleNode("tunxing").getText();//臀型
			String beixing = xmlBody.selectSingleNode("beixing").getText();//背型
			String jianbu = xmlBody.selectSingleNode("jianbu").getText();//肩部
			String jingxing = xmlBody.selectSingleNode("jingxing").getText();//颈型
			String jianxiagu = xmlBody.selectSingleNode("jianxiagu").getText();//肩xiagu
			String xiongxing = xmlBody.selectSingleNode("xiongxing").getText();//胸型
			//20180606
			String chengyiteti = xmlBody.selectSingleNode("chengyiteti").getText();//成衣特体
			

			//上传正反侧
			String zheng = xmlBody.selectSingleNode("zheng").getText();
			String fan = xmlBody.selectSingleNode("fan").getText();
			String ce = xmlBody.selectSingleNode("ce").getText();
			String zhengTao = xmlBody.selectSingleNode("zhengTao").getText();
			String fanTao = xmlBody.selectSingleNode("fanTao").getText();
			String ceTao = xmlBody.selectSingleNode("ceTao").getText();
			String taoma = xmlBody.selectSingleNode("taoma").getText();
			//20180606
			String images = xmlBody.selectSingleNode("images").getText();//多图上传
			String xizhuangdingdan =  xmlBody.selectSingleNode("xizhuangdingdan").getText();//西装订单号
			String xizhuangjiajian =  xmlBody.selectSingleNode("xizhuangjiajian").getText();//西装加减
			String xikudingdan =  xmlBody.selectSingleNode("xikudingdan").getText();//西裤订单号
			String xikujiajian =  xmlBody.selectSingleNode("xikujiajian").getText();//西裤加减
			String xiqundingdan =  xmlBody.selectSingleNode("xiqundingdan").getText();//西裙订单号
			String xiqunjiajian =  xmlBody.selectSingleNode("xiqunjiajian").getText();//西裙加减
	 			
			
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
		StringBuffer ssBuffer  = new StringBuffer();
		ssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001117</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><name></name><phone>150146061416</phone><chenyi>1</chenyi><xizhuang>1</xizhuang><xixku>1</xixku><xiema>1</xiema><height>12</height><weight>23</weight><lingwei>34</lingwei><xiongwei>4</xiongwei><zhongyaowei>5</zhongyaowei><yaowei>6</yaowei><tuigenwei>7</tuigenwei><tunwei>8</tunwei><tongdang>9</tongdang><shangbiwei>10</shangbiwei><shouwanwei>11</shouwanwei><zongjiankuan>12</zongjiankuan><youxiuchang>13</youxiuchang><qianjiankuan>14</qianjiankuan><zuoxiuchang>15</zuoxiuchang><houyaojiechang>16</houyaojiechang><houyichang>17</houyichang><houyaogao>18</houyaogao><qianyaojiechang>19</qianyaojiechang><qianyaogao>20</qianyaogao><zuokuchang>21</zuokuchang><youkuchang>22</youkuchang><qunchang>23</qunchang><xiaotuikuchang></xiaotuikuchang><xiaotuiwei>24</xiaotuiwei><datuiwei>25</datuiwei><jiaochangdu>26</jiaochangdu><jiaokuan>27</jiaokuan><bocu>28</bocu><touwei>29</touwei><renti></renti><zuojian>10093,10095</zuojian><youjian>10068,10070</youjian><jianbu></jianbu><jianxiagu></jianxiagu><beixing>103731</beixing><xiongxing></xiongxing><tunxing>10084</tunxing><duxing>10079</duxing><shoubi>10081</shoubi><jingxing></jingxing><zheng>USER/0000000000001007/546cc8d6c78f76113d7c11f1ad7557d5.jpg</zheng><fan>USER/0000000000001007/5730278181d1f83bdaf5bf1adcc1e9a4.jpg</fan><ce>USER/0000000000001007/dd119c1fc90930b0c59f1fb22896832e.jpg</ce><zhengTao>USER/0000000000001007/c4de1957ccea7a78f0547eb99c0e1b89.jpg</zhengTao><fanTao>USER/0000000000001007/4f90686571fe7063db86074efa7ae800.jpg</fanTao><ceTao>USER/0000000000001007/df40b6a42e3d14abc7cbd3c1b6e85d28.jpg</ceTao><taoma>222</taoma></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001107</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
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
