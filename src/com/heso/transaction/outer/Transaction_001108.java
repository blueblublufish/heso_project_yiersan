package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.cart.CartService;
import com.heso.service.funds.order.entity.FundsOrderReturnObject;
import com.heso.service.funds.order.recharge.OrderRecharge;
import com.heso.service.funds.order.recharge.entity.OrderRechargeItem;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 加入审核购物车
 * 
 * @author xujun
 * 
 */
public class Transaction_001108 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001108.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			// 取出数据
			String account = xmlBody.selectSingleNode("account").getText();
			String token =xmlBody.selectSingleNode("token").getText();
 			String price = xmlBody.selectSingleNode("price").getText();
			String wardrobeId = "";
			String image = "";
			String type = "";
			String name = "";
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
					image = pnode.selectSingleNode("image").getText();
					type = pnode.selectSingleNode("type").getText();
					wardrobeDTO.setColor(pnode.selectSingleNode("color").getText());
					wardrobeDTO.setSuit(pnode.selectSingleNode("suit").getText());
					wardrobeDTO.setUplaod(pnode.selectSingleNode("upload").getText());
					wardrobeDTO.setPattern(pnode.selectSingleNode("pattern").getText());
					wardrobeDTO.setOutline(pnode.selectSingleNode("outline").getText());
					wardrobeDTO.setStyle(pnode.selectSingleNode("style").getText());
					wardrobeDTO.setIsGood(pnode.selectSingleNode("isGood").getText());
					wardrobeDTO.setName(pnode.selectSingleNode("name").getText());
					name = pnode.selectSingleNode("name").getText();
					wardrobeDTO.setSecondType(pnode.selectSingleNode("secondType").getText());
					wardrobeDTO.setSecondTypeName(pnode.selectSingleNode("secondTypeName").getText());
					wardrobeDTO.setSeason(pnode.selectSingleNode("season").getText());
					wardrobeDTOs.add(wardrobeDTO);
				} 
			}
			WardrobeServiceReturnObject wsro = new WardrobeService().addMyWardrobe(wardrobeDTOs);
			for(WardrobeDTO dto:wsro.getWardrobeList()){
				wardrobeId = dto.getId();
				
			}
			String check = new CartService().setCheckCart(account, "", wardrobeId, image, type,name,price);
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
			if (check == null)
				code = "900010";
			else {
				code = "000000";
				sb.append("<checkId>"+check+"</checkId>");
			}

			String xmlBodyStr = super.buildResp(code, sb.toString());
			return xmlBodyStr;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001108</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<account>0000000000000964</account>");
 		sb.append("<token>miammia</token>");
		sb.append("<checkId></checkId>");	
		sb.append("<price>21</price>");
		sb.append("<wardrobeItem>");
		sb.append("<type>1</type>");	
		sb.append("<nature>1</nature>");
		sb.append("<cloth>2</cloth>");
		sb.append("<scene>1</scene>");
		sb.append("<image>image/iamge006</image>");
	 
		sb.append("<color>1</color>");
		sb.append("<suit>1</suit>");
		sb.append("<upload>1</upload>");
		sb.append("<pattern>1</pattern>");
		sb.append("<outline>1</outline>");
		
		sb.append("<style>1</style>");
		
		sb.append("<isGood>1</isGood>");
		sb.append("<name>1</name>");
		sb.append("<secondType>1</secondType>");
		
		sb.append("<name>2</name>");
		sb.append("<secondTypeName>SHANGYI</secondTypeName>");
		sb.append("<season>1</season>");
		sb.append("</wardrobeItem>");
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
