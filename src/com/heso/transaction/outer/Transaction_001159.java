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
 * 查询测试题
 * @author Administrator
 *
 */
public class Transaction_001159 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001159.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			 
			//返回数据 
			StringBuffer sb = new StringBuffer();
			List<List<QaTestQuestions>> list = new ConsumeOrder().checkTestQuestion();
			List<HesoType> ageList = new ConsumeOrder().checkType("AGE");
			List<HesoType> hangyeList = new ConsumeOrder().checkType("INDUSTRY");
			sb.append("<testQuestion>");
			for(List<QaTestQuestions> qaList:list){
				sb.append("<qa>");
				for(QaTestQuestions qatest:qaList){
					sb.append("<qanwser>");
					sb.append("<id>"+ qatest.getId() +"</id>");
					sb.append("<qaname>"+ qatest.getQanameString() +"</qaname>");
					sb.append("<image>"+ qatest.getImage() +"</image>");
					sb.append("<type>"+ qatest.getType() +"</type>");
					sb.append("<belong>"+ qatest.getBelong() +"</belong>");
					sb.append("<desci>"+ qatest.getDesci() +"</desci>");
					sb.append("<sex>"+ qatest.getSex() +"</sex>");
					sb.append("<keyWordBelong>"+ qatest.getKeyWordBelong()+"</keyWordBelong>");
					sb.append("</qanwser>");
				}
				sb.append("</qa>");
			}
			for(HesoType age : ageList){
				sb.append("<ageTest>");
				sb.append("<keyword>"+ age.getKeyword()+"</keyword>");
				sb.append("<typeId>"+ age.getId()+"</typeId>");
				sb.append("<typeName>"+ age.getName()+"</typeName>");
				sb.append("<sex>"+ age.getSex()+"</sex>");
				sb.append("</ageTest>");
			}
			for(HesoType hangye : hangyeList){
				sb.append("<industryTest>"); 
				sb.append("<keyword>"+ hangye.getKeyword()+"</keyword>");
				sb.append("<typeId>"+ hangye.getId()+"</typeId>");
				sb.append("<typeName>"+ hangye.getName()+"</typeName>");
				sb.append("<sex>"+ hangye.getSex()+"</sex>");
				sb.append("</industryTest>");
				
			}
			
			sb.append("</testQuestion>");
			
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
		sb.append("<type>001159</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		 
		sb.append("<account>0000000000001374</account>");
		 
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
