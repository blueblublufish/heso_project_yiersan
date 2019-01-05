package com.heso.transaction.outer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.service.funds.order.entity.FundsOrderReturnObject;
import com.heso.service.funds.order.recharge.OrderRecharge;
import com.heso.service.funds.order.recharge.entity.OrderRechargeItem;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.mall.entity.ProductDTO;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ClothPrice;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.ConsumeProductObject;
import com.heso.service.region.RegionService;
import com.heso.service.region.entity.RegionObject;
import com.heso.service.region.entity.RegionServiceReturnObject;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass;
 

/**
 * 订单保存
 * 
 * @author xujun
 * 
 */
public class Transaction_001116 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001116.class);

	@Override
	public String execute(Node xmlBody, String IPAddress) {
		try {
			
			
			//
			String seller = xmlBody.selectSingleNode("seller").getText();//客户跟进人
			String sellerName = xmlBody.selectSingleNode("sellerName").getText();//客户跟进人
			String qudaojingliId = xmlBody.selectSingleNode("qudaojingliId").getText();
			String qudaojingli = xmlBody.selectSingleNode("qudaojingli").getText();
			String teacher = xmlBody.selectSingleNode("teacher").getText();
			String teacherId = xmlBody.selectSingleNode("teacherId").getText();
			String qudao = xmlBody.selectSingleNode("qudao").getText();
			String qudaoId = xmlBody.selectSingleNode("qudaoId").getText();
			 
			String mobile = xmlBody.selectSingleNode("mobile").getText();
		//	String productId = xmlBody.selectSingleNode("productId").getText();
			String mianliao = xmlBody.selectSingleNode("mianliao").getText();		
 			String quanma = xmlBody.selectSingleNode("quanma").getText();
			//品类
			String suitType = xmlBody.selectSingleNode("suitStyle").getText();
			//版型风格
			//String banxingfengge = xmlBody.selectSingleNode("banxingStyle").getText();
			//性别
			//String sex = xmlBody.selectSingleNode("sex").getText();
			
			//姓名
			//String name = xmlBody.selectSingleNode("name").getText();
			//account
			String account = xmlBody.selectSingleNode("account").getText();
			
			String reciveId = xmlBody.selectSingleNode("reciveId").getText();
 			
			List<Node> listPNodes = xmlBody.selectNodes("productItem");
			String length = "";
			String weidu = "";
			List<ProductsDTO> productsDTOs = new ArrayList<ProductsDTO>();
			if(listPNodes!=null){
				for(Node pnode : listPNodes){
					ProductsDTO productsDTO = new ProductsDTO();
					productsDTO.setProdctId(pnode.selectSingleNode("productId").getText());
					productsDTO.setColor(pnode.selectSingleNode("color").getText());
					productsDTO.setCount(pnode.selectSingleNode("count").getText());
					productsDTO.setSize(pnode.selectSingleNode("size").getText());	
					productsDTO.setChangdu(pnode.selectSingleNode("changdu").getText());	//new
					length = pnode.selectSingleNode("changdu").getText();
					productsDTOs.add(productsDTO);
					List<Node> listNodes = pnode.selectNodes("productDetail");
					List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
					if (listNodes != null) {
						for (Node node : listNodes) {
							ProductDTO productDTO = new ProductDTO();
							productDTO.setProdctId(node.selectSingleNode("productId").getText());
							productDTO.setSize(node.selectSingleNode("size").getText());
							productDTO.setCount(node.selectSingleNode("pcount").getText());
							productDTO.setColor(node.selectSingleNode("color").getText());
							productDTO.setWeidu(node.selectSingleNode("weidu").getText());
							weidu = node.selectSingleNode("weidu").getText();
							productDTOs.add(productDTO);
						}
						productsDTO.setProductDTOs(productDTOs);
					}
				}
				
			}
			
			
			ConsumeOrder order = new ConsumeOrder();
			ArrayList<ConsumeOrderObject> coaoList = new ArrayList<ConsumeOrderObject>();
			String productName = "";
			String price = "";
			
			//获取面料价格
			ClothPrice clothPrice = order.findPirceByMianliao(mianliao, suitType, quanma,length);
			
		 
				for(ProductsDTO productsDTO : productsDTOs){
					ConsumeOrderObject coao = new ConsumeOrderObject();
					List<ProductDTO> dtos = order.findProductById(productsDTO.getProdctId());
					if(dtos.size()==0||dtos==null){
					}else if(dtos.size()==1){
						//单品
						productName = dtos.get(0).getProName();
						price = clothPrice.getPrice();
						String pimage = dtos.get(0).getImage();
						coao.setAccount(account);
						coao.setColor("");
						coao.setName(productName);
						coao.setProductId(productsDTO.getProdctId());
						coao.setRemark("");
						coao.setSize(productsDTO.getSize());
						coao.setType("1");
				 		coao.setImage(pimage);
				 		coao.setChannelType("1");
				 		Integer ordercount = Integer.parseInt(productsDTO.getCount());
				 		float orderamout = ordercount * new BigDecimal(price).floatValue();
						coao.setPrice(price);
						coao.setCount(productsDTO.getCount());
						coao.setAmount(orderamout+"");
						coao.setSuitId("");
						coao.setSubordinate("");
						coao.setYuanchangId("");
						coao.setCategory(suitType);
						coao.setXizhuangwStyle("");
						coao.setChangduStyle(productsDTO.getChangdu());
						coao.setXikuwStyle("");
						coao.setSeason("");
						coao.setGongyi("");
						coao.setDianjian("");
						coao.setZuodianjian("");
						coao.setYoudianjian("");
						coao.setSuoyanStyle("");
						coao.setSuoyanColor("");
						coao.setXiumokouColor("");
						coao.setXizhuangkou("");
						coao.setXikukou("");
						coao.setChenyikou("");
						coao.setNvchima("");
						coao.setNvkuchima("");
						coao.setSeller(seller);
						coao.setClothType(mianliao);
						coao.setPrice_cost(clothPrice.getPrice_cost());
						coao.setPrice_cost_sum(clothPrice.getPrice_cost_sum());
						coao.setPinlei(suitType);
						coao.setWeidu(weidu);
						coao.setSellerName(sellerName);
						coao.setQudao(qudao);
						coao.setQudaoId(qudaoId);
						coao.setQudaojingli(qudaojingli);
						coao.setQudaojingliId(qudaojingliId);
						coao.setTeacher(teacher);
						coao.setTeacherId(teacherId);
						//如果是套西单品，检测工艺表，生成单品
						
					}else{
						//套装
						productName = dtos.get(0).getProName();
						price = clothPrice.getPrice();
						String pimage = dtos.get(0).getImage();
						coao.setAccount(account);
						coao.setColor("");
						coao.setPinlei(suitType);
						coao.setName(productName);
						coao.setProductId(productsDTO.getProdctId());
						coao.setRemark("");
						coao.setSize(productsDTO.getSize());
						coao.setType("2");
						coao.setYuanchangId("");
				 		coao.setImage(pimage);
				 		coao.setChannelType("1");
				 		Integer ordercount = Integer.parseInt(productsDTO.getCount());
				 		float orderamout = ordercount * new BigDecimal(price).floatValue();
						coao.setPrice(price);
						coao.setCount(productsDTO.getCount());
						coao.setAmount(orderamout+"");
						coao.setSuitId("");
						coao.setSubordinate("");
						ArrayList<ConsumeProductObject> cpoList = new ArrayList<ConsumeProductObject>();
						productName = dtos.get(0).getProName();
						dtos.remove(0);
						for(ProductDTO dto : dtos){
							for(ProductDTO dto2 : productsDTO.getProductDTOs()){
								if(dto.getProdctId().equals(dto2.getProdctId())){
									ConsumeProductObject cpo = new ConsumeProductObject();
									cpo.setProductId(dto.getProdctId());
									cpo.setName(dto.getProName());
									cpo.setColor(dto2.getColor());
									cpo.setCount(dto2.getCount());
									cpo.setPrice(dto.getPrice());
									cpo.setSize(dto2.getSize());
									cpo.setImage(dto.getImage());
									Integer countInt = Integer.parseInt(dto2.getCount());
 									float amountt = countInt * new BigDecimal(dto.getPrice()).floatValue();
 									cpo.setAmount(amountt+"");
 									cpo.setSuitPrice(dto.getPrice());
 									cpo.setSuitPromotion("0");
 									cpo.setSubordinate("0");
 									cpo.setWeidu(dto2.getWeidu());
 									cpoList.add(cpo);
 									
								} 
							}	
						}
						coao.setCpoList(cpoList);
						//
						coao.setCategory(suitType);
						coao.setXizhuangwStyle("");
						coao.setChangduStyle(productsDTO.getChangdu());
						coao.setXikuwStyle("");
						coao.setSeason("");
						coao.setGongyi("");
						coao.setClothType(mianliao);
						coao.setDianjian("");
						coao.setZuodianjian("");
						coao.setYoudianjian("");
						coao.setSuoyanStyle("");
						coao.setSuoyanColor("");
						coao.setSeller(seller);
						coao.setXiumokouColor("");
						coao.setXizhuangkou("");
						coao.setXikukou("");
						coao.setChenyikou("");
						coao.setNvchima("");
						coao.setNvkuchima("");
						coao.setPrice_cost(clothPrice.getPrice_cost());
						coao.setPrice_cost_sum(clothPrice.getPrice_cost_sum());
						coao.setSellerName(sellerName);
						coao.setQudao(qudao);
						coao.setQudaoId(qudaoId);
						coao.setQudaojingli(qudaojingli);
						coao.setQudaojingliId(qudaojingliId);
						coao.setTeacher(teacher);
						coao.setTeacherId(teacherId);
						}
					coaoList.add(coao);
				}
 			 	ConsumeOrderReturnObject coaos = order.innerOrder2(coaoList,account, "", reciveId, "", "",quanma);

			
			
			
			
 		//	String sex = xmlBody.selectSingleNode("sex").getText();
			String code = "000000";
			
			//String account = new YesUserService().autoRegister2(mobile, name, sex, pws);
			// 设置返回数据
			StringBuffer sb = new StringBuffer();
		/*	if (account == null)
				code = "900010";
			else {
				code = "000000";
				sb.append("<account>"+account+"</account>");
			}
*/
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
		StringBuffer ssb = new StringBuffer();
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001116</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><seller>3</seller><sellerName></sellerName><qudao>朱红兰</qudao><qudaoId>5</qudaoId><qudaojingli>朱红兰</qudaojingli><qudaojingliId>5</qudaojingliId><teacher>周怡</teacher><teacherId>3</teacherId><mobile>13809207655</mobile><mianliao>DBN303A</mianliao><quanma>0</quanma><suitStyle>7</suitStyle><account>0000000000001492</account><reciveId>705</reciveId><productItem><productId>F18ZS0075</productId><count>1</count><size>定制</size><color>定制</color><changdu>20100</changdu><productDetail><productId>D18TZ0264</productId><pcount>1</pcount><size>定制</size><weidu>10284</weidu><color>Array</color></productDetail><productDetail><productId>D18TZ0271</productId><pcount>1</pcount><size>定制</size><weidu>10284</weidu><color>Array</color></productDetail></productItem></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001116</type>");
		sb.append("<messageId>1</messageId>");
		sb.append("<agentId>001</agentId>");
		sb.append("<digest>MD5数字签名</digest>");
		sb.append("</head>");
		sb.append("<body>"); 
		sb.append("<mobile>15014606141</mobile>");
		sb.append("<name>11</name>");
		sb.append("<mianliao>DAV329A</mianliao>");
		sb.append("<gongyi>1212</gongyi>");
		sb.append("<quanma>1</quanma>");
		sb.append("<suitStyle>1</suitStyle>");
		sb.append("<banxingStyle>1</banxingStyle>");
		sb.append("<reciveId>474</reciveId>");
		sb.append("<account>in_0000000000001224</account>");
		sb.append("<token>0</token>");
		sb.append("<seller>12312313</seller>");
		sb.append("<paymentTerms>1</paymentTerms>");
		sb.append("<recommend></recommend>");
		sb.append("<productName>11</productName>");
		sb.append("<color>green</color>");
		sb.append("<desc>这个666</desc>");
		sb.append("<pinlei>这个666</pinlei>");
		//sb.append("<receiveId></receiveId>");
		sb.append("<regionId>44</regionId>");
		sb.append("<mobile>15014606141</mobile>");
		sb.append("<address>mark</address>");
		sb.append("<sex>1</sex>");
		sb.append("<userName>Hukkke</userName>");
		sb.append("<image>imiagndkngkdfls</image>");
		sb.append("<productItem>");
		sb.append("<productId>M18ZX0018</productId>");
		sb.append("<count>1</count>");
		sb.append("<changdu>changdu</changdu>");
		sb.append("<color>red</color>");
		sb.append("<size>S</size>");
		sb.append("<price>1000</price>");
		sb.append("<productDetail>");
		sb.append("<productId>D18TZ0040</productId>");
		sb.append("<pcount>1</pcount>");
		sb.append("<size>ss</size>");
		sb.append("<weidu>weidu21</weidu>");
		sb.append("<color>red</color>");
		sb.append("</productDetail>");
		sb.append("<productDetail>");
		sb.append("<weidu>weidu2</weidu>");
		sb.append("<productId>D18TZ0041</productId>");
		sb.append("<pcount>1</pcount>");
		sb.append("<size>qq</size>");
		sb.append("<color>gg</color>");
		sb.append("</productDetail>");
	
		sb.append("</productItem>");
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
