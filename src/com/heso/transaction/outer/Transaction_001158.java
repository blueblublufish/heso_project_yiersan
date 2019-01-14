package com.heso.transaction.outer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.data.TransDataProcess;
import com.heso.data.entity.AmaniOrderdetail;

import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ClothPrice;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.ConsumeProductObject;
import com.heso.transaction.AbstractInterfaceClass;
 

/**
 * 订单保存
 * 
 * @author xujun
 * 
 */
public class Transaction_001158 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001158.class);

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
			String remark = xmlBody.selectSingleNode("remark").getText();
		 
 		 
 			 
			//品类
			String suitType = xmlBody.selectSingleNode("suitStyle").getText();
			 
			String account = xmlBody.selectSingleNode("account").getText();
			
			String reciveId = xmlBody.selectSingleNode("reciveId").getText();
 			
			List<Node> listPNodes = xmlBody.selectNodes("productItem");
		 
			
			ConsumeOrder order = new ConsumeOrder();
			List<ProductsDTO> productsDTOs = new ArrayList<ProductsDTO>();
			if(listPNodes!=null){
				for(Node pnode : listPNodes){
					//套装内品类list
					List<String> pinleiList = new ArrayList<String>();
					//套装内面料list
					List<String> mianliaoList = new ArrayList<String>();
					List<String> xiadanfangshiList = new ArrayList<String>();
					ProductsDTO productsDTO = new ProductsDTO();
					productsDTO.setProdctId(pnode.selectSingleNode("productId").getText());
					productsDTO.setColor(pnode.selectSingleNode("color").getText());
					productsDTO.setCount(pnode.selectSingleNode("count").getText());
					productsDTO.setSize(pnode.selectSingleNode("size").getText());	
					List<Node> listNodes = pnode.selectNodes("productDetail");
					List<ProductsDTO> productDTOs = new ArrayList<ProductsDTO>();
					
					String dapinlei = "000000";
					String damianliao = "";
					String daxiadanfangshi = "";
 					if (listNodes != null && listNodes.size()!=0) {
						for (int i = 0; i <listNodes.size();i++) {
							ProductsDTO productDTO = new ProductsDTO();
							Node node = listNodes.get(i);
							productDTO.setProdctId(node.selectSingleNode("productId").getText());
							productDTO.setSize(node.selectSingleNode("size").getText());
							productDTO.setCount(node.selectSingleNode("pcount").getText());
							productDTO.setColor(node.selectSingleNode("color").getText());
							
							ProductsDTO smallOrderDto = order.productToProductsDTO(productDTO);
							smallOrderDto.setType("1");
							productDTOs.add(smallOrderDto);
							//填充套装内品类/面料list
							mianliaoList.add(smallOrderDto.getColor());
							pinleiList.add(smallOrderDto.getPinlei());
							xiadanfangshiList.add(smallOrderDto.getXiadangfangshi());
							if(i == listNodes.size()-1){
								damianliao = getmianliao(mianliaoList);
								dapinlei = getpinlei(pinleiList);
								daxiadanfangshi = getxiadanfangshi(xiadanfangshiList);
								if("000".equals(damianliao)||"000000".equals(dapinlei)||"0000".equals(daxiadanfangshi)){
									productsDTOs.addAll(productDTOs);
								}else {
									
									productsDTO = order.productDTOById(productsDTO.getProdctId());
									productsDTO.setPinlei(dapinlei);
									productsDTO.setColor(damianliao);
									productsDTO.setType("2");
 									productsDTO.setCount(pnode.selectSingleNode("count").getText());
									productsDTO.setSize(pnode.selectSingleNode("size").getText());	
									productsDTO.setXiadangfangshi(smallOrderDto.getXiadangfangshi());
									//productsDTOs.add(e);
									productsDTO.setProductsDTOs(productDTOs);
									productsDTOs.add(productsDTO);
								}
							}
						}
					}else {
						ProductsDTO bigOrderDto = order.productToProductsDTO(productsDTO);
						productsDTOs.add(bigOrderDto);
					}
				}
			}
			
			
 			ArrayList<ConsumeOrderObject> coaoList = new ArrayList<ConsumeOrderObject>();
			String productName = "";
			String price = "";
			for(ProductsDTO productsDTO : productsDTOs){
				ConsumeOrderObject coao = new ConsumeOrderObject();
		 
					//套装
					productName = productsDTO.getProName();
					price = productsDTO.getPrice();
					String pimage = productsDTO.getImage();
					coao.setAccount(account);
					coao.setColor(productsDTO.getColor());
					coao.setPinlei(productsDTO.getPinlei());
					coao.setName(productName);
					coao.setProductId(productsDTO.getProdctId());
					coao.setRemark(remark);
					coao.setSize(productsDTO.getSize());
					coao.setType(productsDTO.getType());
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
					coao.setGongyiCn(productsDTO.getGongyiCn());
					coao.setXiadanfangshi(productsDTO.getXiadangfangshi());
					if(productsDTO.getProductsDTOs()!=null &&productsDTO.getProductsDTOs().size()!=0){
						ArrayList<ConsumeProductObject> cpoList = new ArrayList<ConsumeProductObject>();

						for(ProductsDTO dto : productsDTO.getProductsDTOs()){
							 
							ConsumeProductObject cpo = new ConsumeProductObject();
							cpo.setProductId(dto.getProdctId());
							cpo.setName(dto.getProName());
							cpo.setColor(dto.getColor());
							cpo.setCount(dto.getCount());
							cpo.setPrice(dto.getPrice());
							cpo.setSuitPrice(dto.getHuiyuanPrice());
							cpo.setSize(dto.getSize());
							cpo.setImage(dto.getImage());
							Integer countInt = Integer.parseInt(dto.getCount());
							float amountt = countInt * new BigDecimal(dto.getPrice()).floatValue();
							cpo.setAmount(amountt+"");
							cpo.setSuitPrice(dto.getPrice());
							cpo.setSuitPromotion("0");
							cpo.setSubordinate("0");
							cpo.setWeidu(dto.getWeidu());
							cpo.setGongyi(dto.getGongyi());
							cpo.setPinlei(dto.getPinlei());
							cpo.setLiangti(dto.getChima());
							cpo.setBiaozhunhaochicun(dto.getChimaquyu());
							cpo.setGongyiCn(dto.getGongyiCn());
							cpo.setDetailRemark(dto.getRemark());
							cpoList.add(cpo);
							
						}
						coao.setCpoList(cpoList);
					}
					//
					coao.setCategory(suitType);
					coao.setXizhuangwStyle("");
					coao.setChangduStyle(productsDTO.getChangdu());
					coao.setXikuwStyle("");
					coao.setSeason("");
					coao.setGongyi(productsDTO.getGongyi());
					coao.setClothType(productsDTO.getColor());
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
					coao.setPrice_cost(productsDTO.getHuiyuanPrice());
					coao.setPrice_cost_sum("");
					coao.setSellerName(sellerName);
					coao.setQudao(qudao);
					coao.setQudaoId(qudaoId);
					coao.setQudaojingli(qudaojingli);
					coao.setQudaojingliId(qudaojingliId);
					coao.setTeacher(teacher);
					coao.setTeacherId(teacherId);
					coao.setDiscountPrice(productsDTO.getHuiyuanPrice());
					coao.setLiangti(productsDTO.getChima());
					coao.setBiaozhunhaochicun(productsDTO.getChimaquyu());
					coao.setDetailRemark(productsDTO.getRemark());
					
					coaoList.add(coao);
					}
			
			System.out.println("");
		 	ConsumeOrderReturnObject coaos = order.innerOrder4(coaoList,account, "", reciveId, "", "");

		
			
			
			
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

	
	  String getmianliao(List<String> mianliaoList){
		for(String str1 : mianliaoList){
			for(String str2 : mianliaoList){
				if(!str1.equals(str2)){
					return "000";
				}
			}
		}
		return mianliaoList.get(0);
	}
	
	  
	  String getxiadanfangshi(List<String> xiadanfangshiList){
		  for(String str1 : xiadanfangshiList){
				for(String str2 : xiadanfangshiList){
					if(!str1.equals(str2)){
						return "0000";
					}
				}
			}
			return xiadanfangshiList.get(0);
		  
	  }
	 String getpinlei(List<String> pinleiList){
		Collections.sort(pinleiList);
		List<String> nvliangjian = new ArrayList<String>();
		nvliangjian.add("95000");
		nvliangjian.add("98000");
		Collections.sort(nvliangjian);
		List<String> nvsanjian = new ArrayList<String>();
		nvsanjian.add("95000");
		nvsanjian.add("98000");
		nvsanjian.add("222000");
		Collections.sort(nvsanjian);
		List<String> nanliangjian = new ArrayList<String>();
		nanliangjian.add("3");
		nanliangjian.add("2000");
		Collections.sort(nanliangjian);
		List<String> nansanjian = new ArrayList<String>();
		nansanjian.add("4000");
		nansanjian.add("2000");
		nansanjian.add("3");
		Collections.sort(nansanjian);
		if(pinleiList.equals(nvliangjian)){
			return "7";
		}
		if(pinleiList.equals(nvsanjian)){
			return "";
		}
		if(pinleiList.equals(nanliangjian)){
			return "1";
		}
		if(pinleiList.equals(nansanjian)){
			return "2";
		}

		return "000000";
	}
	/**
	 * @param args
	 */
 
	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		StringBuffer ssb = new StringBuffer();
		ssb.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001158</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><seller>2</seller><sellerName></sellerName><qudao></qudao><qudaoId></qudaoId><qudaojingli></qudaojingli><qudaojingliId></qudaojingliId><teacher></teacher><teacherId></teacherId><mobile>15014606141</mobile><mianliao></mianliao><quanma>0</quanma><suitStyle></suitStyle><account>0000000000000909</account><reciveId>718</reciveId><remark>22</remark><productItem><productId>FHUNDA1810220005</productId><count>1</count><size>定制</size><color>定制</color><changdu></changdu><productDetail><productId>FArmani18110005</productId><pcount>1</pcount><weidu></weidu><size>XS</size><color>DBQ558A</color></productDetail><productDetail><productId>FArmani18110016</productId><pcount>1</pcount><weidu></weidu><size>S</size><color>DAV2447</color></productDetail></productItem></body></message>");
		sb.append("<?xml version='1.0' encoding='utf-8'?>");
		sb.append("<message>");
		sb.append("<head>");
		sb.append("<type>001158</type>");
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
		sb.append("<account>0000000000000909</account>");
		sb.append("<token>0</token>");
		sb.append("<seller>12312313</seller>");
		sb.append("<sellerName>12312313</sellerName>");
		sb.append("<qudaojingliId>12312313</qudaojingliId>");
		sb.append("<qudaojingli>12312313</qudaojingli>");
		sb.append("<remark>我是备注</remark>");
		sb.append("<teacher>12312313</teacher>");
		sb.append("<qudao>12312313</qudao>");
		sb.append("<qudaoId>12312313</qudaoId>");
		sb.append("<teacherId>12312313</teacherId>");
		sb.append("<paymentTerms>1</paymentTerms>");
		sb.append("<recommend></recommend>");
		sb.append("<productName>11</productName>");
 
		sb.append("<pinlei>这个666</pinlei>");
		//sb.append("<receiveId></receiveId>");
		sb.append("<regionId>44</regionId>");
		sb.append("<mobile>15014606141</mobile>");
		sb.append("<address>mark</address>");
		sb.append("<sex>1</sex>");
		sb.append("<userName>Hukkke</userName>");
		sb.append("<image>imiagndkngkdfls</image>");
		sb.append("<productItem>");
		sb.append("<productId>MArmani1812040010</productId>");
		sb.append("<count>1</count>");
		sb.append("<changdu>changdu</changdu>");
		sb.append("<color>DAV2650</color>");
		sb.append("<size>S</size>");
		sb.append("<price>1000</price>");
		
		sb.append("<productDetail>");
		sb.append("<productId>MArmani18120010</productId>");
		sb.append("<pcount>1</pcount>");
		sb.append("<size>50</size>");
		sb.append("<weidu>weidu21</weidu>");
		sb.append("<color>DBQ674A</color>");
		sb.append("</productDetail>");
		sb.append("<productDetail>");
		sb.append("<weidu>weidu2</weidu>");
		sb.append("<productId>MArmani18120020</productId>");
		sb.append("<pcount>1</pcount>");
		sb.append("<size>浪漫风尺寸</size>");
		sb.append("<color>DAV2353</color>");
		sb.append("</productDetail>");
	
		sb.append("</productItem>");
 		sb.append("</body>");
		sb.append("</message>");
		try {
			//new TransDataProcess().execute(ssb.toString());
			List<String> mianliaoList = new ArrayList<String>();
			mianliaoList.add("454545");
			mianliaoList.add("45455");
			
			//String pinleiString = getpinlei(pinleiList);
			new TransDataProcess().execute(ssb.toString());
 			System.out.println(">>>>>>>>>>>>>>" );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
