package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpUtils;


import oracle.sql.ARRAY;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.payment.alipay.util.httpClient.HttpResponse;
import com.heso.service.article.ArticleService;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.HesoType;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.QaTestAndResult;
import com.heso.service.order.consume.entity.QaTestQuestions;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询天气
 * @author Administrator
 *
 */
public class Transaction_001162 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001162.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			String belongId = xmlBody.selectSingleNode("belongId").getText();
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			/*List<HesoType> shangyiList = new ConsumeOrder().checkSecondType("SHANGYI");
 			List<HesoType> kuzhuangList = new ConsumeOrder().checkSecondType("KUZHUANG");
 			List<HesoType> qunzhuangList = new ConsumeOrder().checkSecondType("QUNZHUANG");
 			List<HesoType> peishiList = new ConsumeOrder().checkSecondType("PEISHI");
 			List<HesoType> xieleiList = new ConsumeOrder().checkSecondType("XIELEI");
 			List<HesoType> waziList = new ConsumeOrder().checkSecondType("WAZI");
 			List<HesoType> teshufuList = new ConsumeOrder().checkSecondType("TESHUFU");
 			
 			List<HesoType> zuheList = new ConsumeOrder().checkSecondType("ZHUHE");*/
 			 
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	

	
	void checkWeather(){
		  String host = "http://saweather.market.alicloudapi.com";
		    String path = "/day15";
		    String method = "GET";
		    String appcode = "你自己的AppCode";
		    Map<String, String> headers = new HashMap<String, String>();
		    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		    headers.put("Authorization", "APPCODE " + appcode);
		    Map<String, String> querys = new HashMap<String, String>();
		    querys.put("area", "丽江");
		    querys.put("areaid", "101230506");


		    try {
		    	/**
		    	* 重要提示如下:
		    	* HttpUtils请从
		    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
		    	* 下载
		    	*
		    	* 相应的依赖请参照
		    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
		    	*/
		    	/*HttpResponse response =
		    			
		    			HttpUtils.doGet(host, path, method, headers, querys);*/
		    	//System.out.println(response.toString());
		    	//获取response的body
		    	//System.out.println(EntityUtils.toString(response.getEntity()));
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	
	
	
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001090</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account></account><token></token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001161</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		 
		sb.append("<belongId>2</belongId>");
		 
		sb.append("<token>0</token>");	
		sb.append("</body>");
		sb.append("</message>");
		try {
			 
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
