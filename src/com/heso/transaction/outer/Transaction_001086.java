package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.taglib.tiles.GetTag;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
import com.sun.org.apache.commons.beanutils.WrapDynaBean;
/**
 * 查询衣橱
 * @author Administrator
 *
 */
public class Transaction_001086 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001086.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			
			String type = xmlBody.selectSingleNode("type").getText();
			String nature = xmlBody.selectSingleNode("nature").getText();
			String cloth = xmlBody.selectSingleNode("cloth").getText();
			String scene = xmlBody.selectSingleNode("scene").getText();
			String suit = xmlBody.selectSingleNode("suit").getText();
			String upload = xmlBody.selectSingleNode("upload").getText();
			String pattern = xmlBody.selectSingleNode("pattern").getText();
			String outline = xmlBody.selectSingleNode("outline").getText();
			String style = xmlBody.selectSingleNode("style").getText();
			String id = xmlBody.selectSingleNode("id").getText();
			String isGood = xmlBody.selectSingleNode("isGood").getText(); 
			String season = xmlBody.selectSingleNode("season").getText();
			//String isGood = xmlBody.selectSingleNode("isGood").getText();
		    WardrobeServiceReturnObject wsro =  null;
		    if(suit.equals("1")){
		    	wsro = new WardrobeService().getMyWardRobe2(account, type, scene, style, suit, pattern, outline, nature,id,isGood,season); 
		    }else {
		    	wsro = new WardrobeService().getMyWardRobe(account, type, scene, style, suit, pattern, outline, nature,id,isGood,season);
				
			}
		    
			 
				//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			//new ArticleService().addComment(articleId, comment, userName, account);
			//处理数据
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			
			//返回数据
			StringBuffer sb = new StringBuffer();
			if(wsro.getCode().equals("000000")){ 
				if(wsro.getWardrobeList()!=null&&wsro.getWardrobeList().size()!=0){
					for(WardrobeDTO wardrobeDTO : wsro.getWardrobeList()){
						sb.append("<wardrobeItem>");
						/*if(wardrobeDTO.getwList()!=null&&wardrobeDTO.getwList().size()!=0){
							for(WardrobeDTO wwdto:wardrobeDTO.getwList()){
								sb.append("<secondType>");
								sb.append("<id>"+wwdto.getId()+"</id>"); 
								sb.append("<account>"+wwdto.getAccount()+"</account>");
								sb.append("<image>"+wwdto.getImage()+"</image>");
								sb.append("<type>"+wwdto.getType()+"</type>");
								sb.append("<cloth>"+wwdto.getCloth()+"</cloth>");
								sb.append("<scene>"+wwdto.getScene()+"</scene>");
								sb.append("<style>"+wwdto.getStyle()+"</style>");
								sb.append("<color>"+wwdto.getColor()+"</color>");
								sb.append("<suit>"+wwdto.getSuit()+"</suit>");
								sb.append("<uplaod>"+wwdto.getUplaod()+"</uplaod>");
								sb.append("<pattern>"+wwdto.getPattern()+"</pattern>");
								sb.append("<outline>"+wwdto.getOutline()+"</outline>");
								sb.append("<nature>"+wwdto.getCharater()+"</nature>");
								sb.append("<createTime>"+wwdto.getCreateTime()+"</createTime>");
								sb.append("<isGood>"+wwdto.getIsGood()+"</isGood>");
								sb.append("<name>"+wwdto.getName()+"</name>");
								sb.append("<label>"+wwdto.getLabel()+"</label>");
								sb.append("<impression>"+wwdto.getImpression()+"</impression>");
								sb.append("</secondType>");

							}
						}*/
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
			}
		
			String xmlBodyStr = super.buildResp(wsro.getCode(), sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001086</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000919</account><token>3f458e513228a57f5612128e6cb50</token><type>2</type><nature></nature><scene></scene><suit>1</suit><upload></upload><pattern></pattern><outline></outline><style></style><cloth></cloth><id></id><isGood></isGood><season></season></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001086</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000909</account>");
		sb.append("<token>0</token>");
		  

		sb.append("<type></type>");	
		sb.append("<nature></nature>"); 
		sb.append("<cloth></cloth>");
		sb.append("<scene>4</scene>");
		sb.append("<color></color>");
		sb.append("<suit>1</suit>");  
		sb.append("<upload></upload>");
		sb.append("<pattern></pattern>");
		sb.append("<outline></outline>");
		sb.append("<style></style>");	
		sb.append("<id></id>");
		sb.append("<season></season>");
		sb.append("<isGood></isGood>");
	 
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(ssb.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
