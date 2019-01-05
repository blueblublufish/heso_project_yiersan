package com.heso.transaction.outer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
import com.heso.data.TransDataProcess;
import com.heso.service.mall.entity.ProductDTO;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.ConsumeProductObject;
import com.heso.service.yesUser.YesUserService;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.transaction.AbstractInterfaceClass;
 
/**
 * 线下订单生成接口
 * @author  www
 *
 */
public class Transaction_001080 extends AbstractInterfaceClass {

		private static final Log logger = LogFactory.getLog(Transaction_001080.class);
		
		@Override
		public String execute(Node xmlBody, String IPAddress) {
			try {
			
				//取出数据
				//注册新用户
				String phoneNum = xmlBody.selectSingleNode("mobile").getText();
				String address = xmlBody.selectSingleNode("address").getText();
				String sex = xmlBody.selectSingleNode("sex").getText();
				String userName = xmlBody.selectSingleNode("userName").getText();
				String regionId = xmlBody.selectSingleNode("regionId").getText();
				String paymentTerms = xmlBody.selectSingleNode("paymentTerms").getText();;
				//新增字段
				String channelType = xmlBody.selectSingleNode("channelType").getText();
				String formatType = xmlBody.selectSingleNode("formatType").getText();
				String sizeType = xmlBody.selectSingleNode("sizeType").getText();
				String styleType = xmlBody.selectSingleNode("styleType").getText();
				String clothType = xmlBody.selectSingleNode("clothType").getText();
				String seller = xmlBody.selectSingleNode("seller").getText();
				
				String account = "";
				if("1".equals(sex)&&"1".equals(channelType)){
					//男士量体数据
					InOrderMan inOrderMan = new InOrderMan();
					String heigth = xmlBody.selectSingleNode("heigth").getText();
					String weigth = xmlBody.selectSingleNode("weigth").getText();
					String lingwei = xmlBody.selectSingleNode("lingwei").getText();
					String xiongwei = xmlBody.selectSingleNode("xiongwei").getText();
					String zhongyaowei = xmlBody.selectSingleNode("zhongyaowei").getText();
					String yoawei = xmlBody.selectSingleNode("yaowei").getText();
					String tuigenwei = xmlBody.selectSingleNode("tuigenwei").getText();
					String tunwei = xmlBody.selectSingleNode("tunwei").getText();
					String tongdang = xmlBody.selectSingleNode("tongdang").getText();
					String shangbiwei = xmlBody.selectSingleNode("shangbiwei").getText();
					String shouwanwei = xmlBody.selectSingleNode("shouwanwei").getText();
					String zongjiankuan = xmlBody.selectSingleNode("zongjiankuan").getText();
					String youxiuchang = xmlBody.selectSingleNode("youxiuchang").getText();
					String zuoxiuchang = xmlBody.selectSingleNode("zuoxiuchang").getText();
					String houyaojiechang = xmlBody.selectSingleNode("houyaojiechang").getText();
					String houyichang = xmlBody.selectSingleNode("houyichang").getText();
					String houyaogao = xmlBody.selectSingleNode("houyaogao").getText();
					String qianyaojiechang = xmlBody.selectSingleNode("qianyaojiechang").getText();
					String qianyaogao = xmlBody.selectSingleNode("qianyaogao").getText();
					String zuokuchang = xmlBody.selectSingleNode("zuokuchang").getText();
					String youkuchang = xmlBody.selectSingleNode("youkuchang").getText();
					String qunchang = xmlBody.selectSingleNode("qunchang").getText();
					String xiaokutuichang = xmlBody.selectSingleNode("xiaokutuichang").getText();
					String xiaotuiwei = xmlBody.selectSingleNode("xiaotuiwei").getText();
					String datuiwei = xmlBody.selectSingleNode("datuiwei").getText();
					String jiaochangdu =xmlBody.selectSingleNode("jiaochangdu").getText();
					String jiaokuan =xmlBody.selectSingleNode("jiaokuan").getText();;
					String bocu = xmlBody.selectSingleNode("bocu").getText();
					String touwei = xmlBody.selectSingleNode("touwei").getText();
					String renti =xmlBody.selectSingleNode("renti").getText();
					String zuojian = xmlBody.selectSingleNode("zuojian").getText();
					String youjian =xmlBody.selectSingleNode("youjian").getText(); 
					String duxing = xmlBody.selectSingleNode("duxing").getText();
					String shoubi =xmlBody.selectSingleNode("shoubi").getText();
					String beixing = xmlBody.selectSingleNode("beixing").getText();
					String tunxing = xmlBody.selectSingleNode("tunxing").getText();
					String qianjiankuan = xmlBody.selectSingleNode("qianjiankuan").getText();
 					inOrderMan.setBeixing(beixing);
					inOrderMan.setBocu(bocu);
					inOrderMan.setDatuiwei(datuiwei);
					inOrderMan.setDuxing(duxing);
					inOrderMan.setHeigth(heigth);
					inOrderMan.setHouyaogao(houyaogao);
					inOrderMan.setHouyaojiechang(houyaojiechang);
					inOrderMan.setHouyichang(houyichang);
					inOrderMan.setJiaochangdu(jiaochangdu);
					inOrderMan.setJiaokuan(jiaokuan);
				 
					inOrderMan.setLingwei(lingwei);
					inOrderMan.setQianyaogao(qianyaogao);
					inOrderMan.setQianyaojiechang(qianyaojiechang);
					inOrderMan.setQunchang(qunchang);
					inOrderMan.setRenti(renti);
					inOrderMan.setTunxing(tunxing);
					inOrderMan.setShangbiwei(shangbiwei);
					inOrderMan.setShoubi(shoubi);
					inOrderMan.setShouwanwei(shouwanwei);
					inOrderMan.setTongdang(tongdang);
					inOrderMan.setTouwei(touwei);
					inOrderMan.setTuigenwei(tuigenwei);
					inOrderMan.setTunwei(tunwei);
					inOrderMan.setWeigth(weigth);
					inOrderMan.setXiaokutuichang(xiaokutuichang);
					inOrderMan.setXiaotuiwei(xiaotuiwei);
					inOrderMan.setXiongwei(xiongwei);
					inOrderMan.setYoawei(yoawei);
					inOrderMan.setYoujian(youjian);
					inOrderMan.setYoukuchang(youkuchang);
					inOrderMan.setYouxiuchang(youxiuchang);
					inOrderMan.setZhongyaowei(zhongyaowei);
					inOrderMan.setZongjiankuan(zongjiankuan);
					inOrderMan.setZuojian(zuojian);
					inOrderMan.setZuokuchang(zuokuchang);
					inOrderMan.setZuoxiuchang(zuoxiuchang);
					inOrderMan.setQianjiankuan(qianjiankuan);
					account = new YesUserService().autoRegisterByMan(phoneNum, userName, address, sex, regionId, inOrderMan);
					
				}else {
					account = new YesUserService().autoRegister(phoneNum,userName,address,sex,regionId);
				}
				
				
				//自动注册
				String reciveId = new YesUserService().findRecvIdbyAccount(account, regionId);
				
				String changduStyle = xmlBody.selectSingleNode("changduStyle").getText();
				String xikuwStyle = xmlBody.selectSingleNode("xikuWStyle").getText();
				String xizhuangwStyle = xmlBody.selectSingleNode("xizhuangWStyle").getText();
				String category = xmlBody.selectSingleNode("category").getText();
				String gongyi = xmlBody.selectSingleNode("gongyi").getText();
				String dianjian = xmlBody.selectSingleNode("dianjian").getText();
				String zuodianjian = xmlBody.selectSingleNode("zuodianjian").getText();
				String youdianjian = xmlBody.selectSingleNode("youdianjian").getText();
				String jijie = xmlBody.selectSingleNode("jijie").getText();
				String suoyanStyle = xmlBody.selectSingleNode("suoyanStyle").getText();
				String suoyanColor = xmlBody.selectSingleNode("suoyanColor").getText();
				String xizhuangkou = xmlBody.selectSingleNode("xizhuangkou").getText();
				String xikukou = xmlBody.selectSingleNode("xikukou").getText();
				String chenyikou = xmlBody.selectSingleNode("chenyikou").getText();
				String nvchima = xmlBody.selectSingleNode("nvchima").getText();
				String nvkuchima = xmlBody.selectSingleNode("nvkuchima").getText();
				String xiumokouColor = xmlBody.selectSingleNode("xiumokou").getText();
				String image = xmlBody.selectSingleNode("image").getText();
				
				String productName = xmlBody.selectSingleNode("productName").getText();
				String color = xmlBody.selectSingleNode("color").getText();
				String decs = xmlBody.selectSingleNode("desc").getText();
				String productId = xmlBody.selectSingleNode("productId").getText();
				String count = xmlBody.selectSingleNode("count").getText();
				String size = xmlBody.selectSingleNode("size").getText();
				String price = xmlBody.selectSingleNode("price").getText();
				String yuanchangId = xmlBody.selectSingleNode("yuanchangId").getText();
			
				
				List<Node> listPNodes = xmlBody.selectNodes("productItem");
				List<ProductsDTO> productsDTOs = new ArrayList<ProductsDTO>();
				if(listPNodes!=null){
					for(Node pnode : listPNodes){
						ProductsDTO productsDTO = new ProductsDTO();
						productsDTO.setProdctId(pnode.selectSingleNode("productId").getText());
						productsDTO.setColor(pnode.selectSingleNode("color").getText());
						productsDTO.setCount(pnode.selectSingleNode("count").getText());
						productsDTO.setSize(pnode.selectSingleNode("size").getText());						
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
								productDTOs.add(productDTO);
							}
							productsDTO.setProductDTOs(productDTOs);
						}
					}
					
				}
				
				 
				
				ConsumeOrder order = new ConsumeOrder();
				ArrayList<ConsumeOrderObject> coaoList = new ArrayList<ConsumeOrderObject>();

				//若是线上订单，查询商品，添加进入订单
				if("0".equals(channelType)){
					for(ProductsDTO productsDTO : productsDTOs){
						ConsumeOrderObject coao = new ConsumeOrderObject();
						List<ProductDTO> dtos = order.findProductById(productsDTO.getProdctId());
						if(dtos.size()==0||dtos==null){
						}else if(dtos.size()==1){
							//单品
							productName = dtos.get(0).getProName();
							price = dtos.get(0).getPrice();
							String pimage = dtos.get(0).getImage();
							coao.setAccount(account);
							coao.setColor(productsDTO.getColor());
							coao.setName(productName);
							coao.setProductId(productsDTO.getProdctId());
							coao.setRemark(decs);
							coao.setSize(productsDTO.getSize());
							coao.setType("1");
					 		coao.setImage(pimage);
					 		coao.setChannelType(channelType);
					 		Integer ordercount = Integer.parseInt(productsDTO.getCount());
					 		float orderamout = ordercount * new BigDecimal(price).floatValue();
							coao.setPrice(price);
							coao.setCount(productsDTO.getCount());
							coao.setAmount(orderamout+"");
							coao.setSuitId("");
							coao.setSubordinate("");
							coao.setYuanchangId(yuanchangId);
							coao.setCategory(category);
							coao.setXizhuangwStyle(xizhuangwStyle);
							coao.setChangduStyle(changduStyle);
							coao.setXikuwStyle(xikuwStyle);
							coao.setSeason(jijie);
							coao.setGongyi(gongyi);
							coao.setDianjian(dianjian);
							coao.setZuodianjian(zuodianjian);
							coao.setYoudianjian(youdianjian);
							coao.setSuoyanStyle(suoyanStyle);
							coao.setSuoyanColor(suoyanColor);
							coao.setXiumokouColor(xiumokouColor);
							coao.setXizhuangkou(xizhuangkou);
							coao.setXikukou(xikukou);
							coao.setChenyikou(chenyikou);
							coao.setNvchima(nvchima);
							coao.setSeller(seller);
							coao.setNvkuchima(nvkuchima);
						}else{
							//套装
							productName = dtos.get(0).getProName();
							price = dtos.get(0).getPrice();
							String pimage = dtos.get(0).getImage();
							coao.setAccount(account);
							coao.setColor(color);
							coao.setName(productName);
							coao.setProductId(productsDTO.getProdctId());
							coao.setRemark(decs);
							coao.setSize(productsDTO.getSize());
							coao.setType("2");
							coao.setYuanchangId(yuanchangId);
					 		coao.setImage(pimage);
					 		coao.setChannelType(channelType);
					 		Integer ordercount = Integer.parseInt(productsDTO.getCount());
					 		float orderamout = ordercount * new BigDecimal(price).floatValue();
							coao.setPrice(price);
							coao.setCount(productsDTO.getCount());
							coao.setAmount(orderamout+"");
							coao.setSuitId("");
							coao.setSubordinate("");
							coao.setSeller(seller);
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
	 									cpoList.add(cpo);
									}
								}	
							}
							coao.setCpoList(cpoList);
							//
							coao.setCategory(category);
							coao.setXizhuangwStyle(xizhuangwStyle);
							coao.setChangduStyle(changduStyle);
							coao.setXikuwStyle(xikuwStyle);
							coao.setSeason(jijie);
							coao.setGongyi(gongyi);
							coao.setDianjian(dianjian);
							coao.setZuodianjian(zuodianjian);
							coao.setYoudianjian(youdianjian);
							coao.setSuoyanStyle(suoyanStyle);
							coao.setSuoyanColor(suoyanColor);
							coao.setXiumokouColor(xiumokouColor);
							coao.setXizhuangkou(xizhuangkou);
							coao.setXikukou(xikukou);
							coao.setChenyikou(chenyikou);
							coao.setNvchima(nvchima);
							coao.setNvkuchima(nvkuchima);
 						}
						coaoList.add(coao);
					}
				
				}else{
					ConsumeOrderObject coao0 = new ConsumeOrderObject();
					coao0.setAccount(account);
					coao0.setColor(color);
					coao0.setName(productName);
					coao0.setProductId(productId);
					coao0.setRemark(decs);
					coao0.setSize(size);
					coao0.setType("1");
					//
					coao0.setCategory(category);
					coao0.setXizhuangwStyle(xizhuangwStyle);
					coao0.setChangduStyle(changduStyle);
					coao0.setXikuwStyle(xikuwStyle);
					coao0.setSeason(jijie);
					coao0.setGongyi(gongyi);
					coao0.setDianjian(dianjian);
					coao0.setZuodianjian(zuodianjian);
					coao0.setYoudianjian(youdianjian);
					coao0.setSuoyanStyle(suoyanStyle);
					coao0.setSuoyanColor(suoyanColor);
					coao0.setXiumokouColor(xiumokouColor);
					coao0.setXizhuangkou(xizhuangkou);
					coao0.setXikukou(xikukou);
					coao0.setChenyikou(chenyikou);
					coao0.setNvchima(nvchima);
					
					coao0.setYuanchangId(yuanchangId);
			 		coao0.setImage("");
			 		Integer ordercount = Integer.parseInt(count);
			 		float orderamout = ordercount * new BigDecimal(price).floatValue();
					coao0.setPrice(price);
					coao0.setCount(count);
					coao0.setAmount(orderamout+"");
					coao0.setSuitId("");
					coao0.setSubordinate("");
					coao0.setChannelType(channelType);
					coao0.setFormatType(formatType);
					coao0.setSeller(seller);
					coao0.setSizeType(sizeType);
					coao0.setClothType(clothType);
					coao0.setStyleType(styleType);
					coao0.setNvkuchima(nvkuchima);
					coao0.setImage(image);
					//coao0.setCpoList(cpoList);
					coaoList.add(coao0);
				}
				//建立订单对象
				/*ConsumeOrderObject coao0 = new ConsumeOrderObject();
				coao0.setAccount(account);
				coao0.setColor(color);
				coao0.setName(productName);
				coao0.setProductId(productId);
				coao0.setRemark(decs);
				coao0.setSize(size);
				coao0.setType("1");
				
		 		coao0.setImage("");
		 		Integer ordercount = Integer.parseInt(count);
		 		float orderamout = ordercount * new BigDecimal(price).floatValue();
				coao0.setPrice(price);
				coao0.setCount(count);
				coao0.setAmount(orderamout+"");
				coao0.setSuitId("");
				coao0.setSubordinate("");
				coao0.setChannelType(channelType);
				coao0.setFormatType(formatType);
				coao0.setSeller(seller);
				coao0.setSizeType(sizeType);
				coao0.setClothType(clothType);
				coao0.setStyleType(styleType);
				//coao0.setCpoList(cpoList);
				coaoList.add(coao0);*/
				
				
				// 建立订单对象
				ConsumeOrderReturnObject coaos = order.innerOrder(coaoList,account, productId, "", reciveId, paymentTerms, "");
				
				// 设置返回数据
				StringBuffer sb = new StringBuffer();
				//ConsumeOrderReturnObject coro = new ConsumeOrder().getInfo(account, coaos.getOrderId(), "0", "0", "0", "3", 0, 2);

				if (coaos.getCode().equals("000000")) {
					sb.append("<recCount>" + coaos.getReccount() + "</recCount>");
					//ssb.append("");
					/*for (ConsumeOrderObject coo : coro.getCooList()) {
						sb.append("<orderItem>");
						sb.append("<orderId>" + coo.getOrderId() + "</orderId>");
						sb.append("<account>" + coo.getAccount() + "</account>");
						sb.append("<createTime>" + coo.getCreateTime() + "</createTime>");
						sb.append("<productId>" + coo.getProductId() + "</productId>");
						sb.append("<type>" + coo.getType() + "</type>");
						sb.append("<name>" + coo.getName() + "</name>");
						sb.append("<image>" + coo.getImage() + "</image>");
						sb.append("<count>" + coo.getCount() + "</count>");
						sb.append("<price>" + coo.getPrice() + "</price>");
						sb.append("<amount>" + coo.getAmount() + "</amount>");
						sb.append("<color>" + coo.getColor() + "</color>");
						sb.append("<size>" + coo.getSize() + "</size>");
						sb.append("<payTime>" + coo.getPayTime() + "</payTime>");
						sb.append("<payStatus>" + coo.getPayStatus() + "</payStatus>");
						sb.append("<currency>" + coo.getCurrency() + "</currency>");
						sb.append("<innerCoin>" + coo.getInnerCoin() + "</innerCoin>");
						sb.append("<BonunPoint>" + coo.getBonusPoint() + "</BonunPoint>");
						sb.append("<sendStatus>" + coo.getSendStatus() + "</sendStatus>");
						sb.append("<returnStatus>" + coo.getReturnStatus() + "</returnStatus>");
						sb.append("<receiveId>" + coo.getReceiveId() + "</receiveId>");
						sb.append("<logistCom>"+coo.getLogistCom()+"</logistCom>");
						sb.append("<trackingNum>"+coo.getTrackingNum()+"</trackingNum>");
						sb.append("<showStatus>"+coo.getShowStatus()+"</showStatus>");
						sb.append("<suitId>"+coo.getSuitId()+"</suitId>");
						sb.append("</orderItem>");
					}*/
				}

				String xmlBodyStr = super.buildResp(coaos.getCode(), sb.toString());
				return xmlBodyStr;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.ERROR_RETURN;
		
		}
		public static void main(String[] args) {
			 
 
			StringBuffer ss = new StringBuffer();
			ss.append("<?xml version='1.0' encoding='UTF-8'?><message><head><type>001080</type><messageId>1</messageId><agentId>001</agentId><digest>MD5数字签名</digest></head><body><account></account><token></token><productId></productId><count></count><size></size><price></price><paymentTerms>2</paymentTerms><recommend></recommend><productName></productName><color></color><desc>11</desc><regionId>110228</regionId><mobile>18334673124</mobile><address>北京市县密云县/朝阳</address><sex></sex><userName>张丙未</userName><channelType>0</channelType><formatType></formatType><sizeType></sizeType><clothType></clothType><seller>2</seller><changduStyle></changduStyle><xikuWStyle></xikuWStyle><xizhuangWStyle></xizhuangWStyle><category></category><gongyi></gongyi><zuodianjian></zuodianjian><youdianjian></youdianjian><jijie></jijie><suoyanStyle></suoyanStyle><suoyanColor></suoyanColor><dianjian></dianjian><xizhuangkou></xizhuangkou><xikukou></xikukou><chenyikou></chenyikou><nvchima></nvchima><nvkuchima></nvkuchima><image></image><xiumokou></xiumokou><styleType></styleType><yuanchangId></yuanchangId><productItem><productId>D17O0008</productId><count>1</count><size>均码</size><color>大象灰</color></productItem><productItem><productId>F18YS093</productId><count>1</count><size>S</size><color>白色</color></productItem></body></message>");
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version='1.0' encoding='utf-8'?>");
			sb.append("<message>");
			sb.append("<head>");
			sb.append("<type>001080</type>");
			sb.append("<messageId>1</messageId>");
			sb.append("<agentId>001</agentId>");
			sb.append("<digest>MD5数字签名</digest>");
			sb.append("</head>");
			sb.append("<body>");
			sb.append("<account>in_0000000000000895</account>");
			sb.append("<token>0</token>");
			sb.append("<productId>16C0022</productId>");
			sb.append("<count>1</count>");
			sb.append("<size>S</size>");
			sb.append("<price>1000</price>");
			sb.append("<paymentTerms>1</paymentTerms>");
			sb.append("<recommend></recommend>");
			sb.append("<productName>11</productName>");
			sb.append("<color>green</color>");
			sb.append("<desc>这个666</desc>");
			//sb.append("<receiveId></receiveId>");
			sb.append("<regionId>44</regionId>");
			sb.append("<mobile>15014606141</mobile>");
			sb.append("<address>mark</address>");
			sb.append("<sex>1</sex>");
			sb.append("<userName>Hukkke</userName>");
			sb.append("<image>imiagndkngkdfls</image>");
			//
			/*sb.append("<heigth>11</heigth>");
			sb.append("<weigth>11</weigth>");
			sb.append("<lingwei>11</lingwei>");
			sb.append("<xiongwei>11</xiongwei>");
			sb.append("<zhongyaowei>11</zhongyaowei>");
			sb.append("<yaowei>11</yaowei>");
			sb.append("<tuigenwei>11</tuigenwei>");
			sb.append("<tunwei>1221</tunwei>");
			sb.append("<tongdang>11</tongdang>");
			sb.append("<shangbiwei>11</shangbiwei>");
			sb.append("<shouwanwei>11</shouwanwei>");
			sb.append("<zongjiankuan>11</zongjiankuan>");
			sb.append("<youxiuchang>11</youxiuchang>");
			sb.append("<zuoxiuchang>11</zuoxiuchang>");
			sb.append("<houyaojiechang>11</houyaojiechang>");
			sb.append("<houyichang>11</houyichang>");
			sb.append("<houyaogao>11</houyaogao>");
			sb.append("<qianyaojiechang>11</qianyaojiechang>");
			sb.append("<qianyaogao>11</qianyaogao>");
			sb.append("<zuokuchang>11</zuokuchang>");
			sb.append("<youkuchang>11</youkuchang>");
			sb.append("<qunchang>11</qunchang>");
			sb.append("<xiaokutuichang>11</xiaokutuichang>");
			sb.append("<xiaotuiwei>11</xiaotuiwei>");
			sb.append("<datuiwei>11</datuiwei>");
			sb.append("<jiaochangdu>11</jiaochangdu>");
			sb.append("<bocu>11</bocu>");
			sb.append("<touwei>11</touwei>");
			sb.append("<renti>11</renti>");
			sb.append("<zuojian>11</zuojian>");
			sb.append("<youjian>11</youjian>");
			sb.append("<duxing>11</duxing>");
			sb.append("<shoubi>11</shoubi>");
			sb.append("<beixing>11</beixing>");
			sb.append("<tunxing>11</tunxing>");
			sb.append("<qianjiankuan>11</qianjiankuan>");
			sb.append("<jiaokuan>11</jiaokuan>");
			*/
	//
			

			sb.append("<changduStyle>11</changduStyle>");
			sb.append("<xikuWStyle>11</xikuWStyle>");
			sb.append("<xizhuangWStyle>11</xizhuangWStyle>");
			sb.append("<category>11</category>");
			sb.append("<gongyi>11</gongyi>");
			sb.append("<zuodianjian>11</zuodianjian>");
			sb.append("<youdianjian>11</youdianjian>");
			sb.append("<youdianjian>11</youdianjian>");
			sb.append("<jijie>11</jijie>");
			sb.append("<suoyanStyle>11</suoyanStyle>");
			sb.append("<suoyanColor>11</suoyanColor>");
			sb.append("<dianjian>11</dianjian>");
			sb.append("<xizhuangkou>11</xizhuangkou>");
			sb.append("<xikukou>11</xikukou>");
			sb.append("<chenyikou>11</chenyikou>");
			sb.append("<nvkuchima>11</nvkuchima>");
			sb.append("<nvchima>11</nvchima>");
			sb.append("<xiumokou>11</xiumokou>");
			
			//
			
			sb.append("<channelType>0</channelType>");
			sb.append("<formatType>66</formatType>");
			sb.append("<sizeType>XI</sizeType>");
			sb.append("<styleType>cool</styleType>");
			sb.append("<clothType>nelong</clothType>");
			sb.append("<seller>4545454</seller>");
			
			sb.append("<productItem>");
			sb.append("<productId>CSF18ZX0015</productId>");
			sb.append("<count>1</count>");
			sb.append("<size>ss</size>");
			sb.append("<color>red</color>");
			sb.append("<productDetail>");
			sb.append("<productId>W17T0061</productId>");
			sb.append("<pcount>1</pcount>");
			sb.append("<size>qq</size>");
			sb.append("<color>gg</color>");
			sb.append("</productDetail>");
			sb.append("<productDetail>");
			sb.append("<productId>D17O0012</productId>");
			sb.append("<pcount>1</pcount>");
			sb.append("<size>qq</size>");
			sb.append("<color>gg</color>");
			sb.append("</productDetail>");
		
			sb.append("</productItem>");
	
			
			
		
			sb.append("</body>");
			sb.append("</message>");

			try {
				new TransDataProcess().execute(ss.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
