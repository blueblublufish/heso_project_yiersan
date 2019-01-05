package com.heso.service.order.consume;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 

import com.heso.data.entity.AmaniOrderdetail;
import com.heso.data.entity.CustomerInformation;
import com.heso.data.entity.Orderinformation;
import com.heso.service.order.consume.ConsumeOrder;

public class PackXmlToAmani {
	private static final Log logger = LogFactory.getLog(PackXmlToAmani.class);
	
	public  static String post(String url,String xmlFileName){    
        //关闭   
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");     
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");     
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");    
          
        //创建httpclient工具对象   
        HttpClient client = new HttpClient();    
        //创建post请求方法   
        PostMethod myPost = new PostMethod(url);    
        //设置请求超时时间   
        client.setConnectionTimeout(300*1000);   
        String responseString = null;    
        try{    
            //设置请求头部类型   
            myPost.setRequestHeader("Content-Type","application/xml");  
            myPost.setRequestHeader("charset","utf-8"); 
            myPost.setRequestHeader("user","SAX8"); 
            myPost.setRequestHeader("pwd","17ef02940ada86e5e42fe51522e115e1"); 
            myPost.setRequestHeader("accept","application/json"); 
            myPost.setRequestHeader("lan","zh"); 
              
            //设置请求体，即xml文本内容，注：这里写了两种方式，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式   
           myPost.setRequestBody(xmlFileName);   
              
          /*  InputStream body=this.getClass().getResourceAsStream("/"+xmlFileName);  
            myPost.setRequestBody(body); */ 
//            myPost.setRequestEntity(new StringRequestEntity(xmlString,"text/xml","utf-8"));     
            int statusCode = client.executeMethod(myPost);    
            if(statusCode == HttpStatus.SC_OK){    
                BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());    
                byte[] bytes = new byte[1024];    
                ByteArrayOutputStream bos = new ByteArrayOutputStream();    
                int count = 0;    
                while((count = bis.read(bytes))!= -1){    
                    bos.write(bytes, 0, count);    
                }    
                byte[] strByte = bos.toByteArray();    
                responseString = new String(strByte,0,strByte.length,"utf-8");    
                System.out.println(responseString);
                bos.close();    
                bis.close();    
            }    
        }catch (Exception e) {    
            e.printStackTrace();    
        }    
        myPost.releaseConnection();    
        return responseString;    
    }     
	public static String packXml(Orderinformation orderinformation,CustomerInformation customerInformation,List<AmaniOrderdetail> detaillist){
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>");
		sb.append("<orderinformation id=\"\" includesuit=\"0A01\" appendclothingid=\"\">");
		sb.append("<clothingid>"+orderinformation.getClothingid()+"</clothingid>");
		sb.append("<sizecategoryid>"+orderinformation.getSizecategoryid()+"</sizecategoryid>");
		sb.append("<areaid>"+orderinformation.getAreaid()+"</areaid>");
		sb.append("<fabric>"+orderinformation.getFabirc()+"</fabric>");
		sb.append("<amount>"+orderinformation.getAmount()+"</amount>");
		sb.append("<clothingstyle>"+orderinformation.getClothingstyle()+"</clothingstyle>");
		sb.append("<custormerbody>"+orderinformation.getCustormerbody()+"</custormerbody>");
		sb.append("<orderno>"+orderinformation.getOrderno()+"</orderno>");
		sb.append("<semifinished>2</semifinished>");
		sb.append("<remark>"+orderinformation.getRemark()+"</remark>");
		
		
		//顾客信息开始
		sb.append("<customerInformation genderid=\""+customerInformation.getGender()+"\">");
		sb.append("<name>"+customerInformation.getName()+"</name>");
		sb.append("<height>"+customerInformation.getHeight()+"</height>");
		sb.append("<heightunitid>"+customerInformation.getHeightunited()+"</heightunitid>");
		sb.append("<weight>"+customerInformation.getWeight()+"</weight>");
		sb.append("<weightunitid>"+customerInformation.getWeightunited()+"</weightunitid>");
		sb.append("<email>"+customerInformation.getEmail()+"</email>");
		sb.append("<address>"+customerInformation.getAddress()+"</address>");
		sb.append("<tel>"+customerInformation.getTel()+"</tel>");
		sb.append("<memos>"+customerInformation.getMemos()+"</memos>");
		sb.append("</customerInformation>");
		//顾客信息结束
		//订单明细开始
		sb.append("<orderdetails>");
		for(AmaniOrderdetail detail:detaillist){
			sb.append("<orderdetail>");
			sb.append("<sizespecheight>"+detail.getSizespecheight()+"</sizespecheight>");
			sb.append("<categoryid>"+detail.getCategoryid()+"</categoryid>");
			sb.append("<bodystyle>"+detail.getBodystyle()+"</bodystyle>");
			sb.append("<partsize>"+detail.getPartsize()+"</partsize>");
			sb.append("<ordersprocess>"+detail.getOrdersprocess()+"</ordersprocess>");
			sb.append("</orderdetail>");
		}
		sb.append("</orderdetails>");
		//订单明细结束
		sb.append("</orderinformation>");
		System.out.println("==返回xml数据:" + sb.toString());
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		AmaniOrderdetail amaniOrderdetail = new AmaniOrderdetail();
		amaniOrderdetail.setBodystyle("10285");
		amaniOrderdetail.setCategoryid("3");
		amaniOrderdetail.setOrdersprocess("37,51,88,178,194,220,214,433,1146:FLL624-135,30083,1889,1381,1385,1378,1380,1965,1379,1927");
		amaniOrderdetail.setPartsize("10101:41,10102:93,10105:87,10110:32,10111:45.5,10113:61,10114:61,10115:41,10116:37,10117:70,10172:39,10108:94");
		amaniOrderdetail.setSizespecheight("");
		List<AmaniOrderdetail> list = new ArrayList<>();
		list.add(amaniOrderdetail);
		
		Orderinformation orderinformation = new Orderinformation();
		orderinformation.setAmount("1");//
		orderinformation.setAppendclothingid("");
		orderinformation.setFabirc("DBN326A");//
		orderinformation.setClothingid("3");//
		orderinformation.setCustormerbody("10087,10088,10090,10091,10092");//
		orderinformation.setOrderno("XXX982339180");//
		orderinformation.setIncludesuit("0A011");//
		orderinformation.setSizecategoryid("10052");//
		orderinformation.setClothingstyle("20100");
		orderinformation.setRemark("");
		
		
		CustomerInformation customerInformation = new CustomerInformation();
		customerInformation.setAddress("青岛");
		customerInformation.setGender("10040");
		customerInformation.setName("alipy");
		customerInformation.setHeight("185");
		customerInformation.setHeightunited("10266");
		customerInformation.setWeight("75");
		customerInformation.setWeightunited("10261");
		customerInformation.setEmail("alice.shao@kutesmart.com");
		customerInformation.setTel("18661855675");
		customerInformation.setMemos("");
		
		String xml = packXml(orderinformation, customerInformation, list);
		System.out.println(xml);
		String url = "http://api.rcmtm.cn/api/order/submit";
		String backxml = post(url, xml);
	}
}
