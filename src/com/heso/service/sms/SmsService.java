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
	 * sms��ʼ��
	 * 
	 */
	public static void Initialize() {
//		try {
//			prop.load(SmsService.class.getResourceAsStream("/sms.properties"));
//		} catch (IOException e) {
//			logger.info("���������ļ�����" + e.getMessage());
//			e.printStackTrace();
//		}
	}

	/**
	 * ��ȡURL��
	 * 
	 * @param url
	 * @return
	 */
	public URLConnection getURLConnection(String url) {
		URLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

			// ����POST�������������������
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
//			logger.info("���Ͷ����쳣:" + e.getMessage());
//		}
//		return "";
//
//	}

	/**
	 * ���Ͷ���
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
//			logger.info("���Ͷ����쳣:" + e.getMessage());
//		}
//		logger.info("���ͽ����ֻ�����====" + phone);
//		logger.info("������Ϣ����====" + message);
//		return "";
//	}
	
	

	public  String sendMessageByaliyunByorderId(String message,String phone,String smsType) throws ServerException, ClientException{
		
	    //���ó�ʱʱ��-�����е���
	    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
	    System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	    //��ʼ��ascClient��Ҫ�ļ�������
	    final String product = "Dysmsapi";//����API��Ʒ���ƣ����Ų�Ʒ���̶��������޸ģ�
	    final String domain = "dysmsapi.aliyuncs.com";//����API��Ʒ�������ӿڵ�ַ�̶��������޸ģ�
	    //�滻�����AK
	    final String accessKeyId = "LTAIx5WyyzjjUOao";//���accessKeyId,�ο����ĵ�����2
	    final String accessKeySecret = "wxKUvEROqTXVu7gO71hgxMwMbp6OGj";//���accessKeySecret���ο����ĵ�����2
	    //��ʼ��ascClient,��ʱ��֧�ֶ�region�������޸ģ�
	    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
	    accessKeySecret);
	    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	    IAcsClient acsClient = new DefaultAcsClient(profile);
	     //��װ�������
	     SendSmsRequest request = new SendSmsRequest();
	     //ʹ��post�ύ
	     request.setMethod(MethodType.POST);
	     //����:�������ֻ��š�֧���Զ��ŷָ�����ʽ�����������ã���������Ϊ1000���ֻ�����,������������ڵ������ü�ʱ�������ӳ�,��֤�����͵Ķ����Ƽ�ʹ�õ������õķ�ʽ
	     request.setPhoneNumbers(phone);
	     //����:����ǩ��-���ڶ��ſ���̨���ҵ�
	     request.setSignName("��������");
	     //����:����ģ��-���ڶ��ſ���̨���ҵ�
	     request.setTemplateCode(smsType);
	     //��ѡ:ģ���еı����滻JSON��,��ģ������Ϊ"�װ���${name},������֤��Ϊ${code}"ʱ,�˴���ֵΪ
	     //������ʾ:���JSON����Ҫ�����з�,����ձ�׼��JSONЭ��Ի��з���Ҫ��,������������а���\r\n�������JSON����Ҫ��ʾ��\\r\\n,����ᵼ��JSON�ڷ���˽���ʧ��
	     request.setTemplateParam("{\"orderId\":\"" +
		     		message +
		     		"\"}");
	     
	     String ooString = "{\"name\":\"" +
	     		message +
	     		"\"}";
	     //��ѡ-���ж�����չ��(��չ���ֶο�����7λ�����£������������û�����Դ��ֶ�)
	     request.setSmsUpExtendCode("90997");
	     //��ѡ:outIdΪ�ṩ��ҵ����չ�ֶ�,�����ڶ��Ż�ִ��Ϣ�н���ֵ���ظ�������
	     request.setOutId("");
	    //����ʧ���������ClientException�쳣
	    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
	    if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    	logger.info(sendSmsResponse.getMessage());
	    	System.out.println("OOOOOOKKKKKK");
	    //����ɹ�
	    }else {
	    	logger.error(sendSmsResponse.getMessage());
			System.out.println(sendSmsResponse.getMessage());
		}
		return "";
	}
	
	public  String sendMessageByaliyun(String message,String phone,String smsType) throws ServerException, ClientException{
		
	    //���ó�ʱʱ��-�����е���
	    System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
	    System.setProperty("sun.net.client.defaultReadTimeout", "10000");
	    //��ʼ��ascClient��Ҫ�ļ�������
	    final String product = "Dysmsapi";//����API��Ʒ���ƣ����Ų�Ʒ���̶��������޸ģ�
	    final String domain = "dysmsapi.aliyuncs.com";//����API��Ʒ�������ӿڵ�ַ�̶��������޸ģ�
	    //�滻�����AK
	    final String accessKeyId = "LTAIx5WyyzjjUOao";//���accessKeyId,�ο����ĵ�����2
	    final String accessKeySecret = "wxKUvEROqTXVu7gO71hgxMwMbp6OGj";//���accessKeySecret���ο����ĵ�����2
	    //��ʼ��ascClient,��ʱ��֧�ֶ�region�������޸ģ�
	    IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
	    accessKeySecret);
	    DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
	    IAcsClient acsClient = new DefaultAcsClient(profile);
	     //��װ�������
	     SendSmsRequest request = new SendSmsRequest();
	     //ʹ��post�ύ
	     request.setMethod(MethodType.POST);
	     //����:�������ֻ��š�֧���Զ��ŷָ�����ʽ�����������ã���������Ϊ1000���ֻ�����,������������ڵ������ü�ʱ�������ӳ�,��֤�����͵Ķ����Ƽ�ʹ�õ������õķ�ʽ
	     request.setPhoneNumbers(phone);
	     //����:����ǩ��-���ڶ��ſ���̨���ҵ�
	     request.setSignName("��������");
	     //����:����ģ��-���ڶ��ſ���̨���ҵ�
	     request.setTemplateCode(smsType);
	     //��ѡ:ģ���еı����滻JSON��,��ģ������Ϊ"�װ���${name},������֤��Ϊ${code}"ʱ,�˴���ֵΪ
	     //������ʾ:���JSON����Ҫ�����з�,����ձ�׼��JSONЭ��Ի��з���Ҫ��,������������а���\r\n�������JSON����Ҫ��ʾ��\\r\\n,����ᵼ��JSON�ڷ���˽���ʧ��
	     request.setTemplateParam("{\"code\":" +
	     		message +
	     		"}");
	     //��ѡ-���ж�����չ��(��չ���ֶο�����7λ�����£������������û�����Դ��ֶ�)
	     //request.setSmsUpExtendCode("90997");
	     //��ѡ:outIdΪ�ṩ��ҵ����չ�ֶ�,�����ڶ��Ż�ִ��Ϣ�н���ֵ���ظ�������
	     request.setOutId("");
	    //����ʧ���������ClientException�쳣
	    SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
	    if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
	    	logger.info(sendSmsResponse.getMessage());
	    	System.out.println("OOOOOOKKKKKK");
	    //����ɹ�
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
			req.setSmsFreeSignName("��������");
			req.setSmsParam("{name:'"+message+"'}");
			req.setRecNum(phone);
			req.setSmsTemplateCode(smsType);
			AlibabaAliqinFcSmsNumSendResponse response = client.execute(req);
			System.out.println( response.getBody());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
//			logger.info("���Ͷ����쳣:" + e.getMessage());
		}
		logger.info("���ͽ����ֻ�����====" + phone);
//		logger.info("������Ϣ����====" + message);
		return "";
	}
	
	public static final String SMS_URL = "http://gw.api.taobao.com/router/rest";
	public static final String SMS_APPKEY = "23376409";
	public static final String SMS_SECRET = "7851ffbc3f4e86675a4f39d7895f5ce9";
	public static final String SMS_FREE_SIGN_NAME = "��������";
//	public static final String SMS_URL = "http://124.172.234.157:8180/Service.asmx/SendMessage";
//	public static final String SMS_ID = "1421";
//	public static final String SMS_NAME = "�¶���";
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
