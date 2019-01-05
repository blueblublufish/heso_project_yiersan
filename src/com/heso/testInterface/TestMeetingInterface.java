package com.heso.testInterface;
import java.io.BufferedInputStream;  
import java.io.BufferedReader;  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.net.URI;  
import java.net.URL;  
import java.net.URLConnection;  
import java.util.UUID;
  
import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpStatus;  
import org.apache.commons.httpclient.methods.PostMethod;  
  
/** 
 * ���Ե���һЩmeeting�������ӿ� 
 * @author Jack.Song 
 */  
public class TestMeetingInterface {  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
           
        String url = "http://120.79.59.188:8084/heso_project_yiersan/servlet/TransactionServlet";  
         //String url = "http://127.0.0.1:8087/heso_project_yiersan/servlet/TransactionServlet";  
        TestMeetingInterface tmi = new TestMeetingInterface();  
       /* String xmlString  = "<?xml version='1.0' encoding='utf-8'?><message><head>" +
        		"<type>001081</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><orderId>0000000000000215</orderId><desc>668545</desc><token>0</token><account></account><desc>��236tfyy</desc></body></message>";*/
       // String xmlString = "<?xml version='1.0' encoding='utf-8'?><message><head><type>001025</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><account>2222222</account><token>0</token><type>4</type><nature></nature><cloth></cloth><scene></scene><color></color><suit></suit><upload></upload><pattern></pattern><outline></outline><style></style><id></id></body></message>";
        String xmlString = "<?xml version='1.0' encoding='utf-8'?><message><head><type>001139</type><messageId>1</messageId><agentId>001</agentId><digest>MD5����ǩ��</digest></head><body><account>0000000000000909</account><token>0</token></body></message>";
        System.out.println("---");
        System.out.println(tmi.post(url,xmlString));
          
        /*//�жϵ�ǰϵͳ�Ƿ�֧��Java AWT Desktop��չ 
        if(java.awt.Desktop.isDesktopSupported()){ 
            try { 
                URI path = tmi.getClass().getResource("/listSummaryMeeting.xml").toURI(); 
                System.out.println(path); 
                //����һ��URIʵ�� 
//              java.net.URI uri = java.net.URI.create(path);  
                //��ȡ��ǰϵͳ������չ 
                java.awt.Desktop dp = java.awt.Desktop.getDesktop(); 
                //�ж�ϵͳ�����Ƿ�֧��Ҫִ�еĹ��� 
                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){ 
                    //��ȡϵͳĬ������������� 
                    dp.browse(path);     
                } 
            } catch (Exception e) { 
                e.printStackTrace(); 
            }              
        }*/  
    }  
      
      
  
    /**  
     * ����xml��������server��  
     * @param url xml�������ݵ�ַ  
     * @param xmlString ���͵�xml������  
     * @return null����ʧ�ܣ����򷵻���Ӧ����  
     */    
    public String post(String url,String xmlFileName){    
        //�ر�   
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");     
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");     
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");    
          
        //����httpclient���߶���   
        HttpClient client = new HttpClient();    
        //����post���󷽷�   
        PostMethod myPost = new PostMethod(url);    
        //��������ʱʱ��   
        client.setConnectionTimeout(300*1000);  
        String responseString = null;    
        try{    
            //��������ͷ������   
            myPost.setRequestHeader("Content-Type","text/xml");  
            myPost.setRequestHeader("charset","utf-8");  
              
            //���������壬��xml�ı����ݣ�ע������д�����ַ�ʽ��һ����ֱ�ӻ�ȡxml�����ַ�����һ���Ƕ�ȡxml�ļ���������ʽ   
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
      
    /** 
     * �ô�ͳ��URI��������� 
     * @param urlStr 
     */  
    public void testPost(String urlStr) {  
        try {  
            URL url = new URL(urlStr);  
            URLConnection con = url.openConnection();  
            con.setDoOutput(true);  
            con.setRequestProperty("Pragma:", "no-cache");  
            con.setRequestProperty("Cache-Control", "no-cache");  
            con.setRequestProperty("Content-Type", "text/xml");  
  
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
            String xmlInfo = getXmlInfo();  
            System.out.println("urlStr=" + urlStr);  
//            System.out.println("xmlInfo=" + xmlInfo);   
            out.write(new String(xmlInfo.getBytes("UTF-8")));  
            out.flush();  
            out.close();  
            BufferedReader br = new BufferedReader(new InputStreamReader(con  
                    .getInputStream()));  
            String line = "";  
            for (line = br.readLine(); line != null; line = br.readLine()) {  
                System.out.println(line);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    private String getXmlInfo() {  
        StringBuilder sb = new StringBuilder();  
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");  
        sb.append("<Message>");  
        sb.append(" <header>");  
        sb.append("     <action>readMeetingStatus</action>");  
        sb.append("     <service>meeting</service>");  
        sb.append("     <type>xml</type>");  
        sb.append("     <userName>admin</userName>");  
        sb.append("     <password>admin</password>");  
        sb.append("     <siteName>box</siteName>");  
        sb.append(" </header>");  
        sb.append(" <body>");  
        sb.append("     <confKey>43283344</confKey>");  
        sb.append(" </body>");  
        sb.append("</Message>");  
        return sb.toString();  
    }  
}  