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
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 查询二级标签
 * @author Administrator
 *
 */
public class Transaction_001161 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001161.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{   
			String belongId = xmlBody.selectSingleNode("belongId").getText();
			String account = xmlBody.selectSingleNode("account").getText();
			//返回数据 
			StringBuffer sb = new StringBuffer();
 			/*List<HesoType> shangyiList = new ConsumeOrder().checkSecondType("SHANGYI");
 			List<HesoType> kuzhuangList = new ConsumeOrder().checkSecondType("KUZHUANG");
 			List<HesoType> qunzhuangList = new ConsumeOrder().checkSecondType("QUNZHUANG");
 			List<HesoType> peishiList = new ConsumeOrder().checkSecondType("PEISHI");
 			List<HesoType> xieleiList = new ConsumeOrder().checkSecondType("XIELEI");
 			List<HesoType> waziList = new ConsumeOrder().checkSecondType("WAZI");
 			List<HesoType> teshufuList = new ConsumeOrder().checkSecondType("TESHUFU");
 			UserServiceReturnObject usro = new YesUserService().getUserData(account);
 			List<HesoType> zuheList = new ConsumeOrder().checkSecondType("ZHUHE");*/
			
			UserServiceReturnObject usro = new YesUserService().getUserData(account);
			String sex = usro.getUdo().getSex();
 			List<HesoType> list = new ConsumeOrder().checkSecondType(belongId,sex);
			for(HesoType type2 : list){
				sb.append("<typeList>");
				sb.append("<category>"+ type2.getCategoryType()+"</category>");
				sb.append("<id>"+ type2.getId()+"</id>");
				sb.append("<name>"+ type2.getName()+"</name>");
				sb.append("<sex>"+ type2.getSex()+"</sex>");
				sb.append("<belong>"+ type2.getBelong()+"</belong>");
				sb.append("<desc>"+ type2.getDesc()+"</desc>");
				WardrobeServiceReturnObject wsro = new WardrobeService().getSecondWardRobe(belongId, account, sex, type2.getCategoryType(), type2.getId(), type2.getName());
				if(wsro.getWardrobeList()!=null&&wsro.getWardrobeList().size()!=0){
					for(WardrobeDTO wardrobeDTO : wsro.getWardrobeList()){
						sb.append("<wardrobeItem>");
						
						sb.append("<id>"+wardrobeDTO.getId()+"</id>");
						sb.append("<account>"+wardrobeDTO.getAccount()+"</account>");
						sb.append("<image>"+wardrobeDTO.getImage()+"</image>");
						sb.append("<type>"+wardrobeDTO.getType()+"</type>");
						sb.append("<cloth>"+wardrobeDTO.getCloth()+"</cloth>");
						sb.append("<scene>"+wardrobeDTO.getScene()+"</scene>");
						sb.append("<style>"+wardrobeDTO.getStyle()+"</style>");
						sb.append("<color>"+wardrobeDTO.getColor()+"</color>");
						sb.append("<suit>"+wardrobeDTO.getSuit()+"</suit>");
						sb.append("<uplaod>"+wardrobeDTO.getUplaod()+"</uplaod>");
						sb.append("<pattern>"+wardrobeDTO.getPattern()+"</pattern>");
						sb.append("<outline>"+wardrobeDTO.getOutline()+"</outline>");
						sb.append("<nature>"+wardrobeDTO.getCharater()+"</nature>");
						sb.append("<createTime>"+wardrobeDTO.getCreateTime()+"</createTime>");
						sb.append("<isGood>"+wardrobeDTO.getIsGood()+"</isGood>");	
						sb.append("<name>"+wardrobeDTO.getName()+"</name>");
						sb.append("<label>"+wardrobeDTO.getLabel()+"</label>");
						sb.append("<impression>"+wardrobeDTO.getImpression()+"</impression>");
						sb.append("<secondType>"+wardrobeDTO.getSecondType()+"</secondType>");
						sb.append("<secondTypeName>"+wardrobeDTO.getSecondTypeName()+"</secondTypeName>");
						sb.append("</wardrobeItem>");	
					} 
				}else {
					sb.append("<wardrobeItem></wardrobeItem>");
				}
				sb.append("</typeList>");
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
		sb.append("<type>001161</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		  
		sb.append("<belongId>2</belongId>");
		sb.append("<account>0000000000000909</account>");
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
