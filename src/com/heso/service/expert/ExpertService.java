package com.heso.service.expert;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.db.DatabaseMgr;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.utility.ErrorProcess;

import data.DataRow;
import data.DataTable;
/**
 * 专家作品
 * @author win10
 *
 */
public class ExpertService {
	
	private static final Log logger = LogFactory.getLog(ExpertService.class);
	 
	
	 public  static List<String> getStyleName(String ids,Map<String, List<String>> map){
			List<String> list = new ArrayList<>();
			String wstyle1 ="";
			String wstyle2 ="";
			String[] sourceStrArray = ids.split(",");
	 		for (int i = 0; i < sourceStrArray.length; i++) {
	 			 String styleId = sourceStrArray[i].trim();
	 			 for(String key:map.keySet()){
	 				 if(styleId.equals(key)){
	 					if(wstyle1.equals("")||wstyle1.isEmpty()){
	 						wstyle1 = map.get(key).get(0);			
	 						wstyle2 = map.get(key).get(1);		
	 					}else {
	 						wstyle1 = wstyle1 + " | " +map.get(key).get(0);	
	 						wstyle2 = wstyle2 + " " +map.get(key).get(1);		
	 					}
	 					
	 				 }
	 			 }
	 		}
	 		list.add(wstyle1);
			list.add(wstyle2);
			return list;
		}
	 
	 
	 //一键衣配 
		public     MallServiceReturnObject getTestSuit(String sex ,String zhuStyle,String fuStyle,String bodyPattern,String bodyNotSuit,String skinNotSuit,String age) {
			MallServiceReturnObject msro = new MallServiceReturnObject();
			Connection conn = DatabaseMgr.getInstance().getConnection();
			Map<String, List<String>> map = new HashMap<>();
			ArrayList<Object> argsList = new ArrayList<Object>();
			try {
				
				String  sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int d = dtt.getRows().size();
				for(int i = 0;i<dtt.getRows().size();i++){
					List<String> list = new ArrayList<>();
					String value = dtt.getRows().get(i).getString("VALUE");
					String id = dtt.getRows().get(i).getString("ID");
	 				String label = dtt.getRows().get(i).getString("LABEL");
					String name = dtt.getRows().get(i).getString("NAME"); 
					if("1".equals(sex)){
						list.add(value.trim());					
					}else {
						list.add(name.trim());
					}
					list.add(label.trim());
					map.put(id, list);
 
				}
				String [] st = bodyNotSuit.split(",");
				String bodyNotSuitSql = "";
				for(int i = 0;i<st.length;i++){
				 if(!st[i].isEmpty()&&!st[i].equals("")){
					 bodyNotSuitSql = bodyNotSuitSql + " BODY_NOTSUIT NOT LIKE '%," +
					 		st[i] +
					 		",%' AND ";
				 }
				}
				/*
				 * 去除辅风格匹配
				 * String styleSql = "(STYLE LIKE '%," +
						zhuStyle +
						",%' OR STYLE LIKE '%," +
						fuStyle +
						",%') and ";*/
				
				String styleSql = "(STYLE LIKE '%," +
						zhuStyle +
						",%' ) and ";
				
				String bodyPatternSql = " ";
				
				String [] stt = bodyPattern.split(",");
				if(stt.length==2){
					 bodyPatternSql = bodyPatternSql + " BODY_PATTERN LIKE '%," +
						 		stt[1] +
						 		",%' AND ";
				}else {
					bodyPatternSql = bodyPatternSql + " (BODY_PATTERN LIKE '%," +
					 		stt[1] + 
					 		",%' OR BODY_PATTERN LIKE '%," +
					 		stt[2] +
					 		",%' ) AND"; 
				}
				String ageSql = " AGE_TYPE LIKE '%" +
						age +
						"%' AND ";
				String skinNotSuitSql = " NOT_SUIT_SKIN NOT LIKE '%" +
						skinNotSuit +
						"%' and ";
				// 获取套装列表 
				 sql = "select * from heso_product where " +
						styleSql+ 
						ageSql+
				 		bodyNotSuitSql +
				 		skinNotSuitSql +
						"  ( type = 2 or (type = '1' AND SUPPLY_ID !='1' AND SUPPLY_ID !='6' AND SUPPLY_ID !='7' AND SUPPLY_ID !='3' AND SUPPLY_ID !='2' AND SUPPLY_ID !='4' AND SUPPLY_ID !='5' AND SUPPLY_ID !='' OR PRODUCT_ID LIKE '%MArmani%' ) )and status = '1' and sex = '" +
						sex +
						"'";
				argsList.clear();
  				//argsList.add(style);
 				ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
				DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int sn = dtSuit.getRows().size();
				if(dtSuit.getRows().size() <=0 ){
					msro.setPioList(pioSuitList);
					return msro;
				}
				for (int i = 0; i < dtSuit.getRows().size(); i++) {
					DataRow dr = dtSuit.getRows().get(i);
					ProductItemObject pioSuit = new ProductItemObject();
					pioSuit.setProductId(dr.getString("product_id"));
					pioSuit.setName(dr.getString("name"));
					pioSuit.setDesc(dr.getString("desc"));
					pioSuit.setCategory(dr.getString("category"));
					pioSuit.setDesignName(dr.getString("designer_name"));
					pioSuit.setScene(dr.getString("scene"));
					pioSuit.setType(dr.getString("type"));
					String sstyle = dr.getString("style");
					Date date = dr.getTimestamp("CREATE_TIME");
					Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
				
					String supplypId = dr.getString("supply_id");
					if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
						continue;
					}
					List<String> list = getStyleName(sstyle, map);
					pioSuit.setStyle(list.get(0)); 
					pioSuit.setAgeType(dr.getString("age_type"));
					pioSuit.setShape(dr.getString("shape"));
					pioSuit.setPrice(dr.getString("price"));
					pioSuit.setDiscountPrice(dr.getString("discount_price"));
					pioSuit.setSuitPrice(dr.getString("suit_price"));
					pioSuit.setImgFront(dr.getString("img_front"));
					pioSuit.setImgBehind(dr.getString("img_behind"));
					pioSuit.setImages(dr.getString("images"));
					pioSuit.setDesignerId(dr.getString("DESIGNER"));
					pioSuit.setSex(dr.getString("sex"));
					// 获取套装明细
					sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
					argsList.clear();
					argsList.add(pioSuit.getProductId());
					DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
					boolean status = true;
					for (int j = 0; j < dtGoods.getRows().size(); j++) {
						DataRow dr1 = dtGoods.getRows().get(j);
						ProductItemObject pioGoods = new ProductItemObject();
						pioGoods.setProductId(dr1.getString("product_id"));
						pioGoods.setName(dr1.getString("name"));
						pioGoods.setDesc(dr1.getString("desc"));
						pioGoods.setCategory(dr1.getString("category"));
						pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
						pioGoods.setSupplyName(dr1.getString("supply_name"));
						pioGoods.setColor(dr1.getString("color"));
						pioGoods.setPrice(dr1.getString("price"));
						pioGoods.setSuitPrice(dr1.getString("suit_price"));
						pioGoods.setDiscountPrice(dr1.getString("discount_price"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoods.setStockCount(dr1.getString("stock_count"));
						pioGoods.setSoldCount(dr1.getString("sold_count"));
						pioGoods.setImgFront(dr1.getString("img_front"));
						pioGoods.setImgBehind(dr1.getString("img_behind"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoodsList.add(pioGoods);
						if(dr1.getString("stock_status").equals("0")){
							status = false;
						}
					}
					if(status){
						pioSuit.setStockStatus("1");
					}
					// 添加到单品列表
					pioSuit.setGoodsList(pioGoodsList);
					// 添加套装列表
					pioSuitList.add(pioSuit);
				}
				sql = "";
				
				msro.setPioList(pioSuitList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
				logger.error(e.getMessage());
				e.printStackTrace();
			}finally{
				if(conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return msro;
		}
	 /**
	  * 获取单品推荐
	  */
		public     MallServiceReturnObject getTuijianDanpin(String sex ,String account) {
			MallServiceReturnObject msro = new MallServiceReturnObject();
			Connection conn = DatabaseMgr.getInstance().getConnection();
			Map<String, List<String>> map = new HashMap<>();
			ArrayList<Object> argsList = new ArrayList<Object>();
			try {
				
				String  sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int d = dtt.getRows().size();
				for(int i = 0;i<dtt.getRows().size();i++){
					List<String> list = new ArrayList<>();
					String value = dtt.getRows().get(i).getString("VALUE");
					String id = dtt.getRows().get(i).getString("ID");
	 				String label = dtt.getRows().get(i).getString("LABEL");
					String name = dtt.getRows().get(i).getString("NAME"); 
					if("1".equals(sex)){
						list.add(value.trim());					
					}else {
						list.add(name.trim());
					}
					list.add(label.trim());
					map.put(id, list);
 
				}
				List<List<String>>  lists = new WardrobeService().wardrobeReport("0000000000000909", "0");

				 sql = "select * from heso_product where sex = '" +
				 		sex +
				 		"' and (";
				 List list1 = lists.get(0);
				 sql = sql + list1.get(1) + " in (" +
					 		list1.get(2) +
					 		")";
				 
				 for(int i = 1;i<lists.size();i++){
					 List<String> list = lists.get(i);
					 if(list.get(4).equals("0")){
						 sql = sql + " or " + list.get(1) + " in (" +
						 		list.get(2) +
						 		")";
					 }
					 
				 }
				sql = sql +")";
				  
				argsList.clear();
  				//argsList.add(style);
 				ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
				DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				System.out.println("查询单品推荐===="+sql);
				int sn = dtSuit.getRows().size();
				if(dtSuit.getRows().size() <=0 ){
					msro.setPioList(pioSuitList);
					return msro;
				}
				for (int i = 0; i < dtSuit.getRows().size(); i++) {
					DataRow dr = dtSuit.getRows().get(i);
					ProductItemObject pioSuit = new ProductItemObject();
					pioSuit.setProductId(dr.getString("product_id"));
					pioSuit.setName(dr.getString("name"));
					pioSuit.setDesc(dr.getString("desc"));
					pioSuit.setCategory(dr.getString("category"));
					pioSuit.setDesignName(dr.getString("designer_name"));
					pioSuit.setScene(dr.getString("scene"));
					pioSuit.setType(dr.getString("type"));
					String sstyle = dr.getString("style");
					Date date = dr.getTimestamp("CREATE_TIME");
					Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
				
					String supplypId = dr.getString("supply_id");
					if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
						continue;
					}
					List<String> list = getStyleName(sstyle, map);
					pioSuit.setStyle(list.get(0)); 
					pioSuit.setAgeType(dr.getString("age_type"));
					pioSuit.setShape(dr.getString("shape"));
					pioSuit.setPrice(dr.getString("price"));
					pioSuit.setDiscountPrice(dr.getString("discount_price"));
					pioSuit.setSuitPrice(dr.getString("suit_price"));
					pioSuit.setImgFront(dr.getString("img_front"));
					pioSuit.setImgBehind(dr.getString("img_behind"));
					pioSuit.setImages(dr.getString("images"));
					pioSuit.setDesignerId(dr.getString("DESIGNER"));
					pioSuit.setSex(dr.getString("sex"));
					 
				 
					// 添加套装列表
					pioSuitList.add(pioSuit);
				}
				sql = "";
				
				msro.setPioList(pioSuitList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
				logger.error(e.getMessage());
				e.printStackTrace();
			}finally{
				if(conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return msro;
		}
	 
		//搜索商品
				public     ArrayList<ProductItemObject> getSearchtSuitByProductId(String productId) {
 					Connection conn = DatabaseMgr.getInstance().getConnection();
					Map<String, List<String>> map = new HashMap<>();
					ArrayList<Object> argsList = new ArrayList<Object>(); 
					ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
					try { 
						
						String  sql = "";
						sql = "select * from heso_product where " + 
								"  ( type = 2 or (type = '1' AND SUPPLY_ID !='6' AND SUPPLY_ID !='1' AND SUPPLY_ID !='3'  AND SUPPLY_ID !='7' AND SUPPLY_ID !='2' AND SUPPLY_ID !='4' AND SUPPLY_ID !='5' AND SUPPLY_ID !=''  OR PRODUCT_ID LIKE '%MArmani%' ) )and status = '1' ";
						argsList.clear();
						DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
						
						int sn = dtSuit.getRows().size();
						if(dtSuit.getRows().size() <=0 ){
 							return pioSuitList;
						}
						
						sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
						DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
						int d = dtt.getRows().size();
						for(int i = 0;i<dtt.getRows().size();i++){
							List<String> list = new ArrayList<>();
							String value = dtt.getRows().get(i).getString("VALUE");
							String id = dtt.getRows().get(i).getString("ID");
			 				String label = dtt.getRows().get(i).getString("LABEL");
							String name = dtt.getRows().get(i).getString("NAME"); 
							if("1".equals("")){
								list.add(value.trim());					
							}else {
								list.add(name.trim()); 
							}
							list.add(label.trim());
							map.put(id, list);
		 
						}
						
						
		 				
						 
						// 获取套装列表 
					
		  				//argsList.add(style);
						 
		 				
						for (int i = 0; i < dtSuit.getRows().size(); i++) {
							DataRow dr = dtSuit.getRows().get(i);
							ProductItemObject pioSuit = new ProductItemObject();
							pioSuit.setProductId(dr.getString("product_id"));
							pioSuit.setName(dr.getString("name"));
							pioSuit.setDesc(dr.getString("desc"));
							pioSuit.setCategory(dr.getString("category"));
							pioSuit.setDesignName(dr.getString("designer_name"));
							pioSuit.setScene(dr.getString("scene"));
							pioSuit.setType(dr.getString("type"));
							String sstyle = dr.getString("style");
							Date date = dr.getTimestamp("CREATE_TIME");
							Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
							String supplypId = dr.getString("supply_id");
							if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
								continue;
							}
							
							List<String> list = getStyleName(sstyle, map);
							pioSuit.setStyle(list.get(0)); 
							pioSuit.setAgeType(dr.getString("age_type"));
							pioSuit.setShape(dr.getString("shape"));
							pioSuit.setPrice(dr.getString("price"));
							pioSuit.setDiscountPrice(dr.getString("discount_price"));
							pioSuit.setSuitPrice(dr.getString("suit_price"));
							pioSuit.setImgFront(dr.getString("img_front"));
							pioSuit.setImgBehind(dr.getString("img_behind"));
							pioSuit.setImages(dr.getString("images"));
							pioSuit.setDesignerId(dr.getString("DESIGNER"));
							pioSuit.setSex(dr.getString("sex"));
							// 获取套装明细
							sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
							argsList.clear();
							argsList.add(pioSuit.getProductId());
							DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
							ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
							boolean status = true;
							for (int j = 0; j < dtGoods.getRows().size(); j++) {
								DataRow dr1 = dtGoods.getRows().get(j);
								ProductItemObject pioGoods = new ProductItemObject();
								pioGoods.setProductId(dr1.getString("product_id"));
								pioGoods.setName(dr1.getString("name"));
								pioGoods.setDesc(dr1.getString("desc"));
								pioGoods.setCategory(dr1.getString("category"));
								pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
								pioGoods.setSupplyName(dr1.getString("supply_name"));
								pioGoods.setColor(dr1.getString("color"));
								pioGoods.setPrice(dr1.getString("price"));
								pioGoods.setSuitPrice(dr1.getString("suit_price"));
								pioGoods.setDiscountPrice(dr1.getString("discount_price"));
								pioGoods.setStockStatus(dr1.getString("stock_status"));
								pioGoods.setStockCount(dr1.getString("stock_count"));
								pioGoods.setSoldCount(dr1.getString("sold_count"));
								pioGoods.setImgFront(dr1.getString("img_front"));
								pioGoods.setImgBehind(dr1.getString("img_behind"));
								pioGoods.setStockStatus(dr1.getString("stock_status"));
								pioGoodsList.add(pioGoods);
								if(dr1.getString("stock_status").equals("0")){
									status = false;
								}
							}
							if(status){
								pioSuit.setStockStatus("1");
							}
							// 添加到单品列表
							pioSuit.setGoodsList(pioGoodsList);
							// 添加套装列表
							pioSuitList.add(pioSuit);
						}
						sql = "";
						
 					} catch (Exception e) {
						// TODO Auto-generated catch block
 						logger.error(e.getMessage());
						e.printStackTrace();
					}finally{
						if(conn!=null){
							try {
								conn.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					return pioSuitList;
				}
		 //搜索商品
		public     MallServiceReturnObject getSearchtSuit(String sex ,String style,String scene,String productName,String pinlei,String supplier,Integer page,Integer pageSize,String paixu) {
			MallServiceReturnObject msro = new MallServiceReturnObject();
			Connection conn = DatabaseMgr.getInstance().getConnection();
			Map<String, List<String>> map = new HashMap<>();
			ArrayList<Object> argsList = new ArrayList<Object>(); 
			try { 
				
				String  sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int d = dtt.getRows().size();
				for(int i = 0;i<dtt.getRows().size();i++){
					List<String> list = new ArrayList<>();
					String value = dtt.getRows().get(i).getString("VALUE");
					String id = dtt.getRows().get(i).getString("ID");
	 				String label = dtt.getRows().get(i).getString("LABEL");
					String name = dtt.getRows().get(i).getString("NAME"); 
					if("1".equals(sex)){
						list.add(value.trim());					
					}else {
						list.add(name.trim()); 
					}
					list.add(label.trim());
					map.put(id, list);
 
				}
				String styleSql ="";
				if(!style.trim().equals("")&&!style.isEmpty()){
					styleSql = "(STYLE LIKE '%," +
							style +
							",%' ) and ";
				}
				String sceneSql = "";
				if(!scene.trim().equals("")&&!scene.isEmpty()){
					sceneSql = "(scene like '%," +
							scene+
							",%' ) and ";
				}
				String productNameSql = "";
				if(!productName.trim().equals("")&&!productName.isEmpty()){
					productNameSql = "(name like '%" +
							productName +
							"%' ) and ";
				}
				String sexSql = "";
				if(!sex.trim().equals("")&&!sex.isEmpty()){
					sexSql = "sex = '" +
							sex +
							"' and ";
				}
				String supplierSql = "";
				if(!supplier.trim().equals("")&&!supplier.isEmpty()){
					supplierSql = " SUPPLY_ID = '" +
							supplier +
							"' and ";
				}
				String pinleiSql = "";
				if(!pinlei.trim().equals("")&&!pinlei.isEmpty()){
					pinleiSql = " CATEGORY = '" +
							pinlei +
							"' and ";
				}
				String paixuSql = "";
				if(paixu.equals("1")){
					paixuSql = " order by DISCOUNT_PRICE ASC ";
				}else if(paixu.equals("0")) {
					paixuSql = " order by DISCOUNT_PRICE DESC ";
				}else {
					paixuSql = " order by create_time desc ";
				}
				
 				String productIdSql = "OR PRODUCT_ID in  (SELECT product_suit_id FROM heso_product_detail where product_goods_id = '" +
 						productName +
 						"') OR PRODUCT_ID = '" +
 						productName +
 						"'";
				 
				// 获取套装列表 
				 sql = "select * from heso_product where " +
						styleSql+ 
						sceneSql +
				 		productNameSql +
				 		sexSql +
				 		pinleiSql +
				 		supplierSql +
						"  ( type = 2 or (type = '1' AND SUPPLY_ID !='6' AND SUPPLY_ID !='1' AND SUPPLY_ID !='3'  AND SUPPLY_ID !='7' AND SUPPLY_ID !='2' AND SUPPLY_ID !='4' AND SUPPLY_ID !='5' AND SUPPLY_ID !=''  OR PRODUCT_ID LIKE '%MArmani%' ) ) " +
						productIdSql +
						"and status = '1' ";
				argsList.clear();
  				//argsList.add(style);
				sql += " " +
						paixuSql +
						" limit ?,? ";
				argsList.add((page-1)*pageSize);
				argsList.add(pageSize);
 				ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
				DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int sn = dtSuit.getRows().size();
				if(dtSuit.getRows().size() <=0 ){
					msro.setPioList(pioSuitList);
					return msro;
				}
				for (int i = 0; i < dtSuit.getRows().size(); i++) {
					DataRow dr = dtSuit.getRows().get(i);
					ProductItemObject pioSuit = new ProductItemObject();
					pioSuit.setProductId(dr.getString("product_id"));
					pioSuit.setName(dr.getString("name"));
					pioSuit.setDesc(dr.getString("desc"));
					pioSuit.setCategory(dr.getString("category"));
					pioSuit.setDesignName(dr.getString("designer_name"));
					pioSuit.setScene(dr.getString("scene"));
					pioSuit.setType(dr.getString("type"));
					String sstyle = dr.getString("style");
					Date date = dr.getTimestamp("CREATE_TIME");
					Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
					String supplypId = dr.getString("supply_id");
					if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
						continue;
					}
					
					List<String> list = getStyleName(sstyle, map);
					pioSuit.setStyle(list.get(0)); 
					pioSuit.setAgeType(dr.getString("age_type"));
					pioSuit.setShape(dr.getString("shape"));
					pioSuit.setPrice(dr.getString("price"));
					pioSuit.setDiscountPrice(dr.getString("discount_price"));
					pioSuit.setSuitPrice(dr.getString("suit_price"));
					pioSuit.setImgFront(dr.getString("img_front"));
					pioSuit.setImgBehind(dr.getString("img_behind"));
					pioSuit.setImages(dr.getString("images"));
					pioSuit.setDesignerId(dr.getString("DESIGNER"));
					pioSuit.setSex(dr.getString("sex"));
					// 获取套装明细
					sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
					argsList.clear();
					argsList.add(pioSuit.getProductId());
					DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
					boolean status = true;
					for (int j = 0; j < dtGoods.getRows().size(); j++) {
						DataRow dr1 = dtGoods.getRows().get(j);
						ProductItemObject pioGoods = new ProductItemObject();
						pioGoods.setProductId(dr1.getString("product_id"));
						pioGoods.setName(dr1.getString("name"));
						pioGoods.setDesc(dr1.getString("desc"));
						pioGoods.setCategory(dr1.getString("category"));
						pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
						pioGoods.setSupplyName(dr1.getString("supply_name"));
						pioGoods.setColor(dr1.getString("color"));
						pioGoods.setPrice(dr1.getString("price"));
						pioGoods.setSuitPrice(dr1.getString("suit_price"));
						pioGoods.setDiscountPrice(dr1.getString("discount_price"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoods.setStockCount(dr1.getString("stock_count"));
						pioGoods.setSoldCount(dr1.getString("sold_count"));
						pioGoods.setImgFront(dr1.getString("img_front"));
						pioGoods.setImgBehind(dr1.getString("img_behind"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoodsList.add(pioGoods);
						if(dr1.getString("stock_status").equals("0")){
							status = false;
						}
					}
					if(status){
						pioSuit.setStockStatus("1");
					}
					// 添加到单品列表
					pioSuit.setGoodsList(pioGoodsList);
					// 添加套装列表
					pioSuitList.add(pioSuit);
				}
				sql = "";
				
				msro.setPioList(pioSuitList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
				logger.error(e.getMessage());
				e.printStackTrace();
			}finally{
				if(conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return msro;
		}
		
		public     MallServiceReturnObject getTestSuitTuozhan(String sex ,String zhuStyle,String fuStyle,String bodyPattern,String bodyNotSuit,String skinNotSuit,String age) {
			MallServiceReturnObject msro = new MallServiceReturnObject();
			Connection conn = DatabaseMgr.getInstance().getConnection();
			Map<String, List<String>> map = new HashMap<>();
			ArrayList<Object> argsList = new ArrayList<Object>();
			try {
				
				String  sql = "SELECT ID, VALUE,LABEL,NAME FROM  heso_type WHERE KEYWORD = 'style' ";
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int d = dtt.getRows().size();
				for(int i = 0;i<dtt.getRows().size();i++){
					List<String> list = new ArrayList<>();
					String value = dtt.getRows().get(i).getString("VALUE");
					String id = dtt.getRows().get(i).getString("ID");
	 				String label = dtt.getRows().get(i).getString("LABEL");
					String name = dtt.getRows().get(i).getString("NAME");
					if("1".equals(sex)){
						list.add(value.trim());					
					}else {
						list.add(name.trim());
					}
					list.add(label.trim());
					map.put(id, list);
 
				}
				String [] st = bodyNotSuit.split(",");
				String bodyNotSuitSql = "";
				for(int i = 0;i<st.length;i++){
				 if(!st[i].isEmpty()&&!st[i].equals("")){
					 bodyNotSuitSql = bodyNotSuitSql + " BODY_NOTSUIT NOT LIKE '%," +
					 		st[i] +
					 		",%' AND ";
				 }
				}
				
				/*
				 * 去除辅风格匹配
				 * String styleSql = "(STYLE LIKE '%," +
						zhuStyle +
						",%' OR STYLE LIKE '%," +
						fuStyle +
						",%') and ";*/
				String [] sttt = fuStyle.split(",");
				String styleSql = "";
				if(sttt.length==1){
					 styleSql = styleSql + " STYLE  LIKE '%," +
						 		sttt[0] +
						 		",%' AND ";
				}else {
					styleSql =  "( STYLE  LIKE '%," +
					 		sttt[0] +
					 		",%' OR  STYLE  LIKE '%," +
					 		sttt[1] +
					 		",%' ) AND ";
				}
			 
				
				String bodyPatternSql = " ";
				
				String [] stt = bodyPattern.split(",");
				if(stt.length==2){
					 bodyPatternSql = bodyPatternSql + " BODY_PATTERN LIKE '%," +
						 		stt[1] +
						 		",%' AND ";
				}else {
					bodyPatternSql = bodyPatternSql + " (BODY_PATTERN LIKE '%," +
					 		stt[1] + 
					 		",%' OR BODY_PATTERN LIKE '%," +
					 		stt[2] +
					 		",%' ) AND"; 
				}
				String ageSql = " AGE_TYPE LIKE '%" +
						age +
						"%' AND";
				String skinNotSuitSql = " NOT_SUIT_SKIN NOT LIKE '%" +
						skinNotSuit +
						"%' and ";
				// 获取套装列表 
				 sql = "select * from heso_product where " +
						styleSql+ 
						bodyPatternSql+
						ageSql+
				 		bodyNotSuitSql +
				 		skinNotSuitSql +
						"  ( type = 2 or (type = '1' AND SUPPLY_ID !='1' AND SUPPLY_ID !='6' AND SUPPLY_ID !='7' AND SUPPLY_ID !='3' AND SUPPLY_ID !='2' AND SUPPLY_ID !='4' AND SUPPLY_ID !='5' AND SUPPLY_ID !=''  OR PRODUCT_ID LIKE '%MArmani%' ) )and status = '1' and sex = '" +
						sex +
						"'";
				argsList.clear();
  				//argsList.add(style);
 				ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
				DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int sn = dtSuit.getRows().size();
				if(dtSuit.getRows().size() <=0 ){
					msro.setPioList(pioSuitList);
					return msro;
				}
				for (int i = 0; i < dtSuit.getRows().size(); i++) {
					DataRow dr = dtSuit.getRows().get(i);
					ProductItemObject pioSuit = new ProductItemObject();
					pioSuit.setProductId(dr.getString("product_id"));
					pioSuit.setName(dr.getString("name"));
					pioSuit.setDesc(dr.getString("desc"));
					pioSuit.setCategory(dr.getString("category"));
					pioSuit.setDesignName(dr.getString("designer_name"));
					pioSuit.setScene(dr.getString("scene"));
					pioSuit.setType(dr.getString("type"));
					String sstyle = dr.getString("style");
					
					Date date = dr.getTimestamp("CREATE_TIME");
					Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
				
					String supplypId = dr.getString("supply_id");
					if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
						continue;
					}
					List<String> list = getStyleName(sstyle, map);
					pioSuit.setStyle(list.get(0)); 
					pioSuit.setAgeType(dr.getString("age_type"));
					pioSuit.setShape(dr.getString("shape"));
					pioSuit.setPrice(dr.getString("price"));
					pioSuit.setDiscountPrice(dr.getString("discount_price"));
					pioSuit.setSuitPrice(dr.getString("suit_price"));
					pioSuit.setImgFront(dr.getString("img_front"));
					pioSuit.setImgBehind(dr.getString("img_behind"));
					pioSuit.setImages(dr.getString("images"));
					pioSuit.setDesignerId(dr.getString("DESIGNER"));
					pioSuit.setSex(dr.getString("sex"));
					// 获取套装明细
					sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
					argsList.clear();
					argsList.add(pioSuit.getProductId());
					DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
					boolean status = true;
					for (int j = 0; j < dtGoods.getRows().size(); j++) {
						DataRow dr1 = dtGoods.getRows().get(j);
						ProductItemObject pioGoods = new ProductItemObject();
						pioGoods.setProductId(dr1.getString("product_id"));
						pioGoods.setName(dr1.getString("name"));
						pioGoods.setDesc(dr1.getString("desc"));
						pioGoods.setCategory(dr1.getString("category"));
						pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
						pioGoods.setSupplyName(dr1.getString("supply_name"));
						pioGoods.setColor(dr1.getString("color"));
						pioGoods.setPrice(dr1.getString("price"));
						pioGoods.setSuitPrice(dr1.getString("suit_price"));
						pioGoods.setDiscountPrice(dr1.getString("discount_price"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoods.setStockCount(dr1.getString("stock_count"));
						pioGoods.setSoldCount(dr1.getString("sold_count"));
						pioGoods.setImgFront(dr1.getString("img_front"));
						pioGoods.setImgBehind(dr1.getString("img_behind"));
						pioGoods.setStockStatus(dr1.getString("stock_status"));
						pioGoodsList.add(pioGoods);
						if(dr1.getString("stock_status").equals("0")){
							status = false;
						}
					}
					if(status){
						pioSuit.setStockStatus("1");
					}
					// 添加到单品列表
					pioSuit.setGoodsList(pioGoodsList);
					// 添加套装列表
					pioSuitList.add(pioSuit);
				}
				sql = "";
				
				msro.setPioList(pioSuitList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
				logger.error(e.getMessage());
				e.printStackTrace();
			}finally{
				if(conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return msro;
		}
	 
	//获取专家作品库
	public   MallServiceReturnObject getSuitInfo(String desingerId,String style,String sex,String scene) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		Map<String, List<String>> map = new HashMap<>();
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			
			String  sql = "SELECT ID, VALUE,LABEL ,NAME FROM heso_type WHERE KEYWORD = 'style' ";
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int d = dtt.getRows().size();
			for(int i = 0;i<dtt.getRows().size();i++){
				List<String> list = new ArrayList<>();
				String value = dtt.getRows().get(i).getString("VALUE");
				String id = dtt.getRows().get(i).getString("ID");
 				String label = dtt.getRows().get(i).getString("LABEL");
				String name = dtt.getRows().get(i).getString("NAME");
				if("1".equals(sex)){
					list.add(value.trim());					
				}else {
					list.add(name.trim());
				}
				list.add(label.trim());
				map.put(id, list);
			}
			String str = style+",";
			String string = "STYLE like " +
					"'%"+style.trim()+"%' ";
			if("1".equals(style)){
				 string = "(STYLE like " +
							"'%"+str.trim()+"%' OR STYLE = '" +
							style +
							"')";
			}
			// 获取套装列表 
			 sql = "select * from heso_product where  " +
			 		string+
					" and SCENE like " +
					"'%"+scene.trim()+"%'"+ 
					" and SEX = ? and  ( type = 2 or (type = '1' AND SUPPLY_ID !='1' AND SUPPLY_ID !='6' AND SUPPLY_ID !='3'  AND SUPPLY_ID !='7' AND SUPPLY_ID !='2' AND SUPPLY_ID !='4' AND SUPPLY_ID !='5' AND SUPPLY_ID !=''  OR PRODUCT_ID LIKE '%MArmani%' ) )and status = '1' ";
			 argsList.clear();
			//argsList.add(style);
			argsList.add(sex);
			if("".equals(desingerId)){
				sql = sql + "and DESIGNER=? ";
				argsList.add(desingerId);
			}
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dtSuit.getRows().size() <=0 ){
				msro.setPioList(pioSuitList);
				throw new Exception("101206");
			}
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setType(dr.getString("type"));
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setCategory(dr.getString("category"));

				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setScene(dr.getString("scene")); 
				String sstyle = dr.getString("style");
				Date date = dr.getTimestamp("CREATE_TIME");
				Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse("2018-9-24");
			
				String supplypId = dr.getString("supply_id");
				if(supplypId!=null&&supplypId.equals("8")&&date.getTime()<date2.getTime()){
					continue;
				}
				List<String> list = getStyleName(sstyle, map);
				pioSuit.setStyle(list.get(0)); 
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImages(dr.getString("images"));
				pioSuit.setDesignerId(dr.getString("DESIGNER"));
				pioSuit.setSex(dr.getString("sex"));
				// 获取套装明细
				sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
				argsList.clear();
				argsList.add(pioSuit.getProductId());
				DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
				boolean status = true;
				for (int j = 0; j < dtGoods.getRows().size(); j++) {
					DataRow dr1 = dtGoods.getRows().get(j);
					ProductItemObject pioGoods = new ProductItemObject();
					pioGoods.setProductId(dr1.getString("product_id"));
					pioGoods.setName(dr1.getString("name"));
					pioGoods.setDesc(dr1.getString("desc"));
					pioGoods.setCategory(dr1.getString("category"));
					pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
					pioGoods.setSupplyName(dr1.getString("supply_name"));
					pioGoods.setColor(dr1.getString("color"));
					pioGoods.setPrice(dr1.getString("price"));
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setSoldCount(dr1.getString("sold_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoodsList.add(pioGoods);
					if(dr1.getString("stock_status").equals("0")){
						status = false;
					}
				}
				if(status){
					pioSuit.setStockStatus("1");
				}
				// 添加到单品列表
				pioSuit.setGoodsList(pioGoodsList);
				// 添加套装列表
				pioSuitList.add(pioSuit);
			}
			msro.setPioList(pioSuitList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return msro;
	}
	public void getExpertDesign(String designer,String style,String sex){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try {
			conn = dbm.getConnection();
			String sql = " SELECT * FROM heso_product WHERE DESIGNER= ? and STYLE= ? and SEX =?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(designer);
			argsList.add(style);
			argsList.add(sex);
			
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
 			if(dt.getRows().size() <= 0 )
				throw new Exception("101268");
 			//把所有数据存到DTO
 			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr1 = dt.getRows().get(i);
				dr.getString("");//获取字段名
 			}
		} catch (Exception e) { 
			// TODO: handle exception
		}finally{
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) {
		//MallServiceReturnObject object = getSuitInfo("5", "2", "1","2");
		String string = ",8,";
		String [] stt = string.split(",");
		String bodyPatternSql = "";
 		if(stt.length==2){
			 bodyPatternSql = bodyPatternSql + " BODY_PATTERN LIKE '%," +
				 		stt[1] +
				 		",%' AND ";
		}else {
			bodyPatternSql = bodyPatternSql + " (BODY_PATTERN LIKE '%," +
			 		stt[1] + 
			 		",%' OR BODY_PATTERN LIKE '%," +
			 		stt[2] +
			 		",%' ) AND";
		}
 		String sql = "";
 		List<List<String>>  lists = new WardrobeService().wardrobeReport("0000000000000909", "0");

		 sql = "select * from heso_product where sex = '" +
		 		0 +
		 		"' and ";
		 List list1 = lists.get(0);
		 sql = sql + list1.get(1) + " in (" +
			 		list1.get(2) +
			 		")";
		 
		 for(int i = 1;i<lists.size();i++){
			 List<String> list = lists.get(i);
			 if(list.get(4).equals("0")){
				 sql = sql + " or " + list.get(1) + " in (" +
				 		list.get(2) +
				 		")";
			 }
			 
		 }
		//MallServiceReturnObject msro = getTestSuit("1","3", "4", "4,5", ",17,10,","");
		System.out.println(sql);
		
	}
}
