package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 生成行程套装
 * @author Administrator
 *
 */
public class Transaction_001088 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001088.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			List<Node> listPNodes = xmlBody.selectNodes("tripItem");
			List<TripsuitDTO> dtos = new ArrayList<TripsuitDTO>();
			
			if(listPNodes!=null){ 
				for(Node pnode : listPNodes){
					TripsuitDTO dto = new TripsuitDTO();
					dto.setAccount(account);
					dto.setTripDate(pnode.selectSingleNode("tripDate").getText());
					dto.setPlace(pnode.selectSingleNode("place").getText());
					dto.setScene(pnode.selectSingleNode("scene").getText());
					dto.setTemperatuure(pnode.selectSingleNode("temperature").getText());
					dto.setStyle(pnode.selectSingleNode("style").getText());
				dtos.add(dto);
				}
			}
 		 
			//String orderId = xmlBody.selectSingleNode("orderId").getText();
			//String desc = xmlBody.selectSingleNode("desc").getText();
			//判断token
			if(!tokenAuth(account, token)){
				throw new Exception(super.ERROR_TOKEN);
			}
			WardrobeServiceReturnObject wsro = new WardrobeService().addTripsuit(dtos, account);
			//处理数据
			//ConsumeOrderReturnObject coro = new ConsumeOrder().addDesc(orderId, desc);
			 
			//返回数据
			StringBuffer sb = new StringBuffer();
			
		
			String xmlBodyStr = super.buildResp("000000", sb.toString());
			return xmlBodyStr;
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer sssBuffer = new StringBuffer();
		sssBuffer.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001088</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000910</account><token>354f85124c6eabf82c843c97d5858185</token><tripItem><tripDate>2018-11-14</tripDate><place>北京市 北京市</place><temperature>5</temperature><scene>1</scene><style></style></tripItem><tripItem><tripDate>2018-11-14</tripDate><place>北京市 北京市</place><temperature>5</temperature><scene>2</scene><style></style></tripItem><tripItem><tripDate>2018-11-14</tripDate><place>北京市 北京市</place><temperature>5</temperature><scene>3</scene><style></style></tripItem><tripItem><tripDate>2018-11-14</tripDate><place>北京市 北京市</place><temperature>5</temperature><scene>4</scene><style></style></tripItem></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001088</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
 		sb.append("<account>0000000000000964</account>");
		sb.append("<token>f4b920114f388ff4ac2ee681548f233</token>");	
		
		sb.append("<tripItem>");
		sb.append("<tripDate>2018-03-13</tripDate>");
		sb.append("<scene>3</scene>");
		sb.append("<place>beijing</place>");
		sb.append("<style>2</style>");
		sb.append("<temperature>22</temperature>");
		sb.append("</tripItem>");
		/*sb.append("<tripItem>");
		sb.append("<tripDate>2017-12-12</tripDate>");
		sb.append("<scene>3</scene>");
		sb.append("<place>beijing</place>");
		sb.append("<style>4</style>");
		sb.append("<temperature>22</temperature>");
		sb.append("</tripItem>");*/
		
		sb.append("</body>");
		sb.append("</message>");
		try {
			new TransDataProcess().execute(sssBuffer.toString());
			System.out.println(">>>>>>>>>>>>>>gg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
