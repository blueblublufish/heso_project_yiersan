package com.heso.service.sms;

import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.heso.utility.HttpUtil;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class SmsService {
	private static final Log logger = LogFactory.getLog(SmsService.class);
	private static Properties prop = new Properties();

	/**
	 * sms初始化
	 * 
	 */
	public static void Initialize() {
//		try {
//			prop.load(SmsService.class.getResourceAsStream("/sms.properties"));
//		} catch (IOException e) {
//			logger.info("加载配置文件错误" + e.getMessage());
//			e.printStackTrace();
//		}
	}

	/**
	 * 获取URL串
	 * 
	 * @param url
	 * @return
	 */
	public URLConnection getURLConnection(String url) {
		URLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

//	public String getBalance() {
//		try {
//			
//			StringBuffer sb = new StringBuffer();
//			sb.append("Id=");
//			sb.append(SmsService.SMS_ID);
//			sb.append("&Name=");
//			sb.append(SmsService.SMS_NAME);
//			sb.append("&Psw=");
//			sb.append(SmsService.SMA_PWD);
//
//			StringBuffer ss = HttpUtil.submitPost(SmsService.SMS_URL, sb.toString());
//			return ss.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.info("发送短信异常:" + e.getMessage());
//		}
//		return "";
//
//	}

	/**
	 * 发送短信
	 * 
	 * @param message
	 * @param phone
	 * @return
	 * @throws ClientException 
	 * @throws ServerException 
	 */
//	public String sendMessage(String message, String phone) {
//		try {
//			StringBuffer sb = new StringBuffer();
//			sb.append("Message=");
//			sb.append(message);
//			sb.append("&Phone=");
//			sb.append(phone);
//			sb.append("&Id=");
//			sb.append(SmsService.SMS_ID);
//			sb.append("&Name=");
//			sb.append(SmsService.SMS_NAME);
//			sb.append("&Psw=");
//			sb.append(SmsService.SMA_PWD);
//			sb.append("&Timestamp=0&");
//
//			StringBuffer ss = HttpUtil.submitPost(SmsService.SMS_URL, sb.toString());
//			return ss.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.info("发送短信异常:" + e.getMessage());
//		}
//		logger.info("发送接收手机号码====" + phone);
//		logger.info("发送消息内容====" + message);
//		return "";
//	}
	
	

	public  String sendMessageByaliyunByorderId(String message,String phone,String smsType) throws ServerException, ClientException{
		
	    //设置超时时间-可自行调整
	    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
	    System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	    //初始化ascClient需要的几个参数
	    final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
	    final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
	    //替换成你的AK
	    final String accessKeyId = "LTAIx5WyyzjjUOao";//你的accessKeyId,参考本文档步骤2
	    final String accessKeySecret = "wxKUvEROqTXVu7gO71hgxMwMbp6OGj";//你的accessKeySecret，参考本文档步骤2
	    //初始化ascClient,暂时不支持多region（请勿修改）
	    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
	    accessKeySecret);
	    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	    IAcsClient acsClient = new DefaultAcsClient(profile);
	     //组装请求对象
	     SendSmsRequest request = new SendSmsRequest();
	     //使用post提交
	     request.setMethod(MethodType.POST);
	     //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
	     request.setPhoneNumbers(phone);
	     //必填:短信签名-可在短信控制台中找到
	     request.setSignName("红兰衣配");
	     //必填:短信模板-可在短信控制台中找到
	     request.setTemplateCode(smsType);
	     //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
	     //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
	     request.setTemplateParam("{\"orderId\":\"" +
		     		message +
		     		"\"}");
	     
	     String ooString = "{\"name\":\"" +
	     		message +
	     		"\"}";
	     //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
	     request.setSmsUpExtendCode("90997");
	     //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
	     request.setOutId("");
	    //请求失败这里会抛ClientException异常
	    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
	    if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    	logger.info(sendSmsResponse.getMessage());
	    	System.out.println("OOOOOOKKKKKK");
	    //请求成功
	    }else {
	    	logger.error(sendSmsResponse.getMessage());
			System.out.println(sendSmsResponse.getMessage());
		}
		return "";
	}
	
	public  String sendMessageByaliyun(String message,String phone,String smsType) throws ServerException, ClientException{
		
	    //设置超时时间-可自行调整
	    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
	    System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	    //初始化ascClient需要的几个参数
	    final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
	    final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
	    //替换成你的AK
	    final String accessKeyId = "LTAIx5WyyzjjUOao";//你的accessKeyId,参考本文档步骤2
	    final String accessKeySecret = "wxKUvEROqTXVu7gO71hgxMwMbp6OGj";//你的accessKeySecret，参考本文档步骤2
	    //初始化ascClient,暂时不支持多region（请勿修改）
	    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
	    accessKeySecret);
	    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	    IAcsClient acsClient = new DefaultAcsClient(profile);
	     //组装请求对象
	     SendSmsRequest request = new SendSmsRequest();
	     //使用post提交
	     request.setMethod(MethodType.POST);
	     //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
	     request.setPhoneNumbers(phone);
	     //必填:短信签名-可在短信控制台中找到
	     request.setSignName("红兰衣配");
	     //必填:短信模板-可在短信控制台中找到
	     request.setTemplateCode(smsType);
	     //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
	     //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
	     request.setTemplateParam("{\"code\":" +
	     		message +
	     		"}");
	     //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
	     //request.setSmsUpExtendCode("90997");
	     //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
	     request.setOutId("");
	    //请求失败这里会抛ClientException异常
	    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
	    if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    	logger.info(sendSmsResponse.getMessage());
	    	System.out.println("OOOOOOKKKKKK");
	    //请求成功
	    }else {
	    	logger.error(sendSmsResponse.getMessage());
			System.out.println(sendSmsResponse.getMessage());
		}
		return "";
	}
	
	public String sendMessage(String message,String phone,String smsType){
		try {
			
			TaobaoClient client = new DefaultTaobaoClient(SMS_URL, SMS_APPKEY, SMS_SECRET);
			AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
			req.setSmsType("normal");
			req.setSmsFreeSignName("红兰衣配");
			req.setSmsParam("{name:'"+message+"'}");
			req.setRecNum(phone);
			req.setSmsTemplateCode(smsType);
			AlibabaAliqinFcSmsNumSendResponse response = client.execute(req);
			System.out.println( response.getBody());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
//			logger.info("发送短信异常:" + e.getMessage());
		}
		logger.info("发送接收手机号码====" + phone);
//		logger.info("发送消息内容====" + message);
		return "";
	}
	
	public static final String SMS_URL = "http://gw.api.taobao.com/router/rest";
	public static final String SMS_APPKEY = "23376409";
	public static final String SMS_SECRET = "7851ffbc3f4e86675a4f39d7895f5ce9";
	public static final String SMS_FREE_SIGN_NAME = "红兰衣配";
//	public static final String SMS_URL = "http://124.172.234.157:8180/Service.asmx/SendMessage";
//	public static final String SMS_ID = "1421";
//	public static final String SMS_NAME = "衣二衫";
//	public static final String SMA_PWD = "123456";
	public static final String SMS_TYPE_AUTH_CODE = "SMS_117285433";
	public static final String SMS_TYPE_SERVICER_NUM = "SMS_10315063";
	public static final String SMS_TYPE_ORDERID = "SMS_123673791";
	public static final String SMS_TYPE_SENDORDER = "SMS_136861657";
	public static void main(String[] args) throws ServerException {
		//SmsService.Initialize();
		//String a = new SmsService().getBalance();
		try {
			String a = new SmsService().sendMessageByaliyunByorderId("0000000000003118","17620718804","SMS_123673791");
			System.out.println(a);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.err.println(a);
	
	}
}
