package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import oracle.sql.ARRAY;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.article.entity.ArticleAndvideoREturnObject;
import com.heso.service.article.entity.ArticleCommentDTO;
import com.heso.service.expert.ExpertService;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.QaTestAndResult;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.order.consume.entity.VideoHistory;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询用户浏览记录
 * @author Administrator
 *
 */
public class Transaction_001154 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001154.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			//获取数据
 			//String amount = xmlBody.selectSingleNode("account").getText();
			String accountId = xmlBody.selectSingleNode("accountId").getText();
			 
 		//	String wardrobeId = xmlBody.selectSingleNode("wardrobeId").getText();
 			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			 
			
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			List<VideoHistory> list = new ConsumeOrder().checkReadHistory(accountId);
 			if(list!=null&&list.size()!=0){
	 
				for(VideoHistory vHistory : list){
					sb.append("<history>");
					sb.append("<id>" + vHistory.getId() + "</id>");
					sb.append("<account>" + vHistory.getAccount() + "</account>");
					sb.append("<time>" + vHistory.getTime() + "</time>");
					sb.append("<classId>" + vHistory.getClassId() + "</classId>");
					sb.append("<className>" + vHistory.getClassName() + "</className>");
					sb.append("<adminName>" + vHistory.getAdminName() + "</adminName>");
					sb.append("<adminDesc>" + vHistory.getAdminDesc() + "</adminDesc>");
					sb.append("<videoImage>" + vHistory.getVideoImage() + "</videoImage>");

					
					sb.append("</history>");
				}
			 
			}else {
				sb.append("<history></history>");
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
		StringBuffer ss = new StringBuffer();
		ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001090</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account></account><token></token></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001154</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>"); 
		sb.append("</head>");
		sb.append("<body>");
		
		sb.append("<accountId>account</accountId>");
		sb.append("<page>1</page>");
		sb.append("<pageSize>1</pageSize>");
		sb.append("<style>1</style>");
		sb.append("<scene>1</scene>");
		sb.append("<pinlei></pinlei>");
		sb.append("<productName></productName>");
		
		sb.append("<supplier>6</supplier>");
		sb.append("<paixu></paixu>");
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
