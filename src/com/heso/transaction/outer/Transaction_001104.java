package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.WebRowSet;

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
/**
 * 新增衣橱物品
 * @author Administrator 
 *
 */
public class Transaction_001104 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001104.class);
	@Override
	public String execute(Node xmlBody, String IPAddress) {
		// TODO Auto-generated method stub
		try{  
			//获取数据
			String account = xmlBody.selectSingleNode("account").getText();
			//String amount = xmlBody.selectSingleNode("account").getText();
			String token = xmlBody.selectSingleNode("token").getText();
			
			List<Node> listPNodes = xmlBody.selectNodes("wardrobeItem");
			List<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
			if(listPNodes!=null){
				for(Node pnode : listPNodes){
					WardrobeDTO wardrobeDTO = new WardrobeDTO();
 					wardrobeDTO.setAccount(account);
					wardrobeDTO.setType(pnode.selectSingleNode("type").getText());
					wardrobeDTO.setCharater(pnode.selectSingleNode("nature").getText());
					wardrobeDTO.setCloth(pnode.selectSingleNode("cloth").getText());
					wardrobeDTO.setScene(pnode.selectSingleNode("scene").getText());
					wardrobeDTO.setImage(pnode.selectSingleNode("image").getText());
					wardrobeDTO.setColor(pnode.selectSingleNode("color").getText());
					wardrobeDTO.setSuit(pnode.selectSingleNode("suit").getText());
					wardrobeDTO.setUplaod(pnode.selectSingleNode("upload").getText());
					wardrobeDTO.setPattern(pnode.selectSingleNode("pattern").getText());
					wardrobeDTO.setOutline(pnode.selectSingleNode("outline").getText());
					wardrobeDTO.setStyle(pnode.selectSingleNode("style").getText());
					wardrobeDTO.setIsGood(pnode.selectSingleNode("isGood").getText());
					wardrobeDTO.setName(pnode.selectSingleNode("name").getText());
					wardrobeDTO.setSecondType(pnode.selectSingleNode("secondType").getText());
					wardrobeDTO.setSecondTypeName(pnode.selectSingleNode("secondTypeName").getText());
					wardrobeDTO.setSeason(pnode.selectSingleNode("season").getText());
					wardrobeDTO.setId(pnode.selectSingleNode("id").getText());
					wardrobeDTOs.add(wardrobeDTO);
				} 
			}
			WardrobeServiceReturnObject wsro = new WardrobeService().updateMyWardrobe(wardrobeDTOs);
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
			if("000000".equals(wsro.getCode())){
				if(wsro.getWardrobeList()!=null && wsro.getWardrobeList().size()!=0){
					for(WardrobeDTO dto:wsro.getWardrobeList()){
						sb.append("<worbItem>");
						sb.append("<id>"+dto.getId()+"</id>");
						sb.append("</worbItem>");
					}
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
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001104</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account>0000000000000909</account><token>84b41e3d736fd85ee2bc63832c8cfb</token><wardrobeItem><type>1</type><nature></nature><cloth></cloth><color></color><suit>1</suit><scene>6</scene><id>21</id><name>测试</name><upload>0000000000000909</upload><pattern></pattern><style></style><outline></outline><isGood>0</isGood><season></season><secondTypeName>SHANGYI</secondTypeName><secondType>10</secondType><image>INDEX/YICU_909/20180208/ec6e98621c80ebbfaa28ae6c2bb393c7.jpg</image></wardrobeItem></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001104</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>");
		sb.append("<account>0000000000000909</account>");
		sb.append("<token>0</token>");
		sb.append("<wardrobeItem><type>1</type><nature></nature><cloth></cloth><color></color><suit>2</suit><scene>0</scene><secondType>1</secondType><secondTypeName>SHANGYI</secondTypeName><season>1</season><name>322</name><upload>0000000000000909</upload><pattern></pattern><style></style><id></id><outline></outline><isGood>0</isGood><image>INDEX/YICU_909/20180202/0bd6b3bacfc78372436b6cdef0252546.jpg</image></wardrobeItem>");
		/*sb.append("<wardrobeItem>");
		sb.append("<type>1</type>");
		sb.append("<nature>HHFFF</nature>");
		sb.append("<cloth>5</cloth>");
		sb.append("<scene>5</scene>");
		sb.append("<image>5</image>");
		sb.append("<color>5</color>");
		sb.append("<suit>5</suit>");
		sb.append("<upload>5</upload>");
		sb.append("<pattern>5</pattern>");
		sb.append("<outline>5</outline>");
		sb.append("<style>5</style>");
		sb.append("<isGood>1</isGood>");
		sb.append("<name>1</name>");
		sb.append("</wardrobeItem>");
		
		sb.append("<wardrobeItem>");
		sb.append("<type>1</type>");
		sb.append("<nature>HH</nature>");
		sb.append("<cloth>5</cloth>");
		sb.append("<scene>5</scene>");
		sb.append("<image>5</image>");
		sb.append("<color>5</color>");
		sb.append("<suit>5</suit>");
		sb.append("<upload>5</upload>");
		sb.append("<pattern>5</pattern>");
		sb.append("<outline>5</outline>");
		sb.append("<style>5</style>");
		sb.append("<isGood>0</isGood>");
		sb.append("<name>1</name>");
		sb.append("</wardrobeItem>");*/
		
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
