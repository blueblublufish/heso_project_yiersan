package com.heso.transaction.outer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;

import com.heso.transaction.AbstractInterfaceClass;
/**
 * 根据视频Id查询视频评论
 * @author Administrator
 *
 */
public class Transaction_001097 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001097.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String videoId = xmlBody.selectSingleNode("videoId").getText();
			String account = xmlBody.selectSingleNode("account").getText();
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			ArticleAndvideoREturnObject aavro = new ArticleService().getCommentByvideoId(videoId,account);
			if(aavro.getCode().equals("000000")){
				if(aavro.getAcDtoList()!=null&&aavro.getAcDtoList().size()!=0){
					sb.append("<flag>"+aavro.getFlag()+"</flag>");
					for(ArticleCommentDTO dto:aavro.getAcDtoList()){
						sb.append("<commentItem>");
						sb.append("<account>"+dto.getaccount()+"</account>");
						sb.append("<userName>"+dto.getAccountName()+"</userName>");
						sb.append("<comment>"+dto.getComment()+"</comment>");
						sb.append("<createTime>"+dto.getDcreateTime()+"</createTime>");
						sb.append("<image>"+dto.getImage()+"</image>");
						sb.append("<commentId>"+dto.getCommentId()+"</commentId>");
						sb.append("<goodCount>"+dto.getGoodCount()+"</goodCount>");
						sb.append("<isAddGood>"+dto.getIsAddGood()+"</isAddGood>");
						sb.append("</commentItem>");
					}
				}else {
					sb.append("<commentItem></commentItem>");
				}
			}  
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>"); 
		sb.append("<head>");
		sb.append("<type>001097</type>");
		sb.append("<messageId>1</messageId>"); 
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<isSuit>1</isSuit>");
		sb.append("<videoId>17</videoId>");
		sb.append("<season>1,2</season>");
		sb.append("<account></account>");
		sb.append("<token>0</token>");	
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
