package com.heso.service.mall;

import java.awt.Menu;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oracle.net.aso.f;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.entity.CommonType;
import com.heso.db.DatabaseMgr;
import com.heso.service.mall.entity.KeywordType;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.MemberProduct;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.mall.entity.SizeAndColor;
import com.heso.service.mall.entity.SuitMatchArgsObject;

import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.utility.ErrorProcess;

import data.DataRow;
import data.DataTable;

public class MallService {
	private static final Log logger = LogFactory.getLog(MallService.class);

	public MallServiceReturnObject getProductList() {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		msro.setCode("000000");
		try {
			String sql = "select * from heso_product where status = 1 ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	}

	/**
	 * 查询服务列表
	 * @return
	 */
	public   MallServiceReturnObject getServiceList() {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		msro.setCode("000000");
 		try {
			
			String sqlString = "select hmp.PRICE,hmp.ZHUYISHIXIANG,hmp.TUPIANMIAOSHU,hmp.SERVICE_TYPE,hmp.FLAG,hmp.MAX_NUM,hmp.ID ,hmp.name,hmp.DESC,DESIGNERIDS ,IMAGE,SUPPLY_IMAGE from heso_member_product as hmp where   status='1' and end_time > SYSDATE() AND TYPE = '4' ";
			
			
			  
			ArrayList<Object> argsList = new ArrayList<Object>();
		
			
			//argsList.add(list);
			DataTable dt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
			if(dt.getRows().size()<=0){
				logger.error(">>>>>>>>>>查询服务列表失败");
				throw new Exception("001090");
			}
			ArrayList<MemberProduct> mpList = new ArrayList<MemberProduct>();
			
			for (int j = 0; j < dt.getRows().size(); j++) {
				DataRow dr = dt.getRows().get(j);
				MemberProduct mp = new MemberProduct();
				mp.setId(dr.getString("ID")); 
				mp.setDesc(dr.getString("DESC"));
				mp.setDesignerIds(dr.getString("DESIGNERIDS"));
				mp.setName(dr.getString("NAME"));
				mp.setImage(dr.getString("IMAGE"));
				mp.setServiceImage(dr.getString("SUPPLY_IMAGE"));
				mp.setServicetype(dr.getString("SERVICE_TYPE"));
				mp.setFlag(dr.getString("FLAG"));
				mp.setZhuyishixiang(dr.getString("ZHUYISHIXIANG"));
				mp.setTupianmiaoshu(dr.getString("TUPIANMIAOSHU"));
				mp.setMax_num(dr.getString("MAX_NUM"));
				mp.setPrice(dr.getString("PRICE"));
				mpList.add(mp);
			}
			msro.setMpList(mpList);
			logger.info(">>>>>>>>>>>>");
			logger.error("<<<<<<<<<<<<<");
			System.out.println(dt.getRows().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	} 

	public    MallServiceReturnObject getServiceListByDesignerId(String designerId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		msro.setCode("000000");
 		try {
			
			String sqlString = "select hmp.ZHUYISHIXIANG,hmp.TUPIANMIAOSHU,hmp.SERVICE_TYPE,hmp.FLAG,hmp.MAX_NUM,hmp.ID ,hmp.name,hmp.DESC,DESIGNERIDS ,IMAGE,SUPPLY_IMAGE from heso_member_product as hmp" +
					" where   status='1' and end_time > SYSDATE() AND TYPE = '2' AND DESIGNERIDS LIKE '%" +
					designerId +
					"%'";
			
			ArrayList<Object> argsList = new ArrayList<Object>();
		 
			
			//argsList.add(list);
			DataTable dt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
			if(dt.getRows().size()<=0){
				logger.error(">>>>>>>>>>查询服务列表失败");
				throw new Exception("001090");
			}
			ArrayList<MemberProduct> mpList = new ArrayList<MemberProduct>();
			
			for (int j = 0; j < dt.getRows().size(); j++) {
				DataRow dr = dt.getRows().get(j);
				MemberProduct mp = new MemberProduct();
				mp.setId(dr.getString("ID")); 
				mp.setDesc(dr.getString("DESC")); 
				mp.setDesignerIds(dr.getString("DESIGNERIDS"));
				mp.setName(dr.getString("NAME"));
				mp.setImage(dr.getString("IMAGE"));
				mp.setServiceImage(dr.getString("SUPPLY_IMAGE"));
				mp.setServicetype(dr.getString("SERVICE_TYPE"));
				mp.setFlag(dr.getString("FLAG"));
				mp.setZhuyishixiang(dr.getString("ZHUYISHIXIANG"));
				mp.setTupianmiaoshu(dr.getString("TUPIANMIAOSHU"));
				mp.setMax_num(dr.getString("MAX_NUM"));
				mpList.add(mp);
			}
			msro.setMpList(mpList);
			logger.info(">>>>>>>>>>>>");
			logger.error("<<<<<<<<<<<<<");
			System.out.println(dt.getRows().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	} 

	public   MallServiceReturnObject getDesignerDate(String designerId,String serviceId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		msro.setCode("000000");
		
 		try {
 			String sqlString = "SELECT MAX_NUM,FLAG,SERVICE_TYPE,SERVICE_DAY FROM heso_member_product WHERE ID = ?";
 			ArrayList<Object> argsList = new ArrayList<Object>();
 			argsList.add(serviceId);
 			
 			DataTable dtt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
 			String flag = dtt.getRows().get(0).getString("FLAG");
 			String serviceType = dtt.getRows().get(0).getString("SERVICE_TYPE");
 			String maxNum = dtt.getRows().get(0).getString("MAX_NUM");
 			String serviceDay = dtt.getRows().get(0).getString("SERVICE_DAY");
 			sqlString = "SELECT ad.DESIGNATION,ad.DESC ,ad.NAME,ad.IMAGE,ad.URL,ad.PLACE_N,ad.PLACE_CN FROM heso_admin as ad WHERE ad.ADMIN_ID = ?";
 			argsList.clear();
 			argsList.add(designerId);
 			DataTable dttt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
 			String designation = dttt.getRows().get(0).getString("DESIGNATION");
 			String desc = dttt.getRows().get(0).getString("DESC");
 			String name = dttt.getRows().get(0).getString("NAME");
 			String image = dttt.getRows().get(0).getString("IMAGE");
 			String url = dttt.getRows().get(0).getString("URL");
 			String place_n = dttt.getRows().get(0).getString("PLACE_N");
 			String place_cn = dttt.getRows().get(0).getString("PLACE_CN");
 			String day = "7";
 			if("1".equals(serviceType)){
 				day="3";
 			}
 			argsList.clear();
 			sqlString = "SELECT hdp.ID, hdp.BOOK_NUM,hdp.DESIGNERID,hdp.PLACE,hdp.DATE_PLACE,hdp.STATUS,ad.DESIGNATION,ad.DESC ,ad.NAME,ad.IMAGE,ad.URL,ad.PLACE_N,ad.PLACE_CN " +
 					"FROM heso_designer_place as hdp, heso_admin as ad " +
 					"WHERE datediff(DATE_PLACE,CURDATE())>" +
 					day +
 					" AND ad.ADMIN_ID=hdp.DESIGNERID AND hdp.DESIGNERID=" +
 					designerId +
 					" AND ad.ADMIN_ID=hdp.DESIGNERID AND hdp.SERVICEID = '-1'";
 			if("1".equals(flag)){
 				sqlString = "SELECT hdp.ID, hdp.BOOK_NUM,hdp.DESIGNERID,hdp.PLACE,hdp.DATE_PLACE,hdp.STATUS,ad.DESIGNATION,ad.DESC ,ad.NAME,ad.IMAGE,ad.URL,ad.PLACE_N,ad.PLACE_CN " +
 	 					"FROM heso_designer_place as hdp, heso_admin as ad " +
 	 					"WHERE datediff(DATE_PLACE,CURDATE())>" +
 	 					day +
 	 					" AND ad.ADMIN_ID=hdp.DESIGNERID AND hdp.DESIGNERID=" +
 	 					designerId +
 	 					" AND ad.ADMIN_ID=hdp.DESIGNERID" +
 	 					" AND hdp.SERVICEID = " + serviceId;
 			}

			 
		
			
			//argsList.add(designerId);
			DataTable dt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
			ArrayList<MemberProduct> mpList = new ArrayList<MemberProduct>();
			MemberProduct mProduct = new MemberProduct();
			mProduct.setDesignerDesc(desc);
			mProduct.setDesignation(designation);
			mProduct.setDesignerName(name);
			mProduct.setDesignerId(designerId);
			mProduct.setImage(image);
			mProduct.setUrl(url);
			mProduct.setPlace_cn(place_cn);
			mProduct.setPlace_n(place_n);
			mProduct.setFlag(flag);
			mProduct.setMaxNum(maxNum);
			if(dt.getRows().size()<=0){
				logger.error(">>>>>>>>>>查询设计师日程表失败");
				mpList.add(mProduct);
				msro.setMpList(mpList);
				return msro;
			}
			int x = dt.getRows().size();
			ArrayList<MemberProduct> mpList2 = new ArrayList<MemberProduct>();
			for (int j = 0; j < dt.getRows().size(); j++) {
				DataRow dr = dt.getRows().get(j);
				MemberProduct mp = new MemberProduct();
				mp.setId(dr.getString("ID"));
 				mp.setPlace(dr.getString("PLACE"));
 				String dateAfter = dateUpdate(dr.getString("DATE_PLACE"), serviceDay);
				mp.setDate_place(dateAfter);
				mp.setStatus(dr.getString("STATUS"));
				mp.setDesignation(dr.getString("DESIGNATION"));
				mp.setDesignerDesc(dr.getString("DESC"));
				mp.setDesignerId(dr.getString("DESIGNERID"));
				mp.setDesignerName(dr.getString("NAME"));
				mp.setUrl(dr.getString("URL"));
				mp.setImage(dr.getString("IMAGE"));
 				mp.setPlace_cn(dr.getString("PLACE_CN"));
 				mp.setPlace_n(dr.getString("PLACE_N"));
 				mp.setBookNum(dr.getString("BOOK_NUM"));
				mpList2.add(mp);
			}
			mProduct.setmList(mpList2);
			mpList.add(mProduct);
			msro.setMpList(mpList);
			System.out.println(dt.getRows().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	}

	
	/**
	 * 根据所选服务ID返回对应的设计师信息
	 * @param serviceId
	 * @return
	 */
	//todu
	public   MallServiceReturnObject getDesignerByServiceId(String serviceId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		msro.setCode("000000");
		String ids = "1,2,3";
		try {
			
			String sqlString = "SELECT DESIGNERIDS FROM heso_member_product WHERE ID = ?";
			
			
			 
			ArrayList<Object> argsList = new ArrayList<Object>();
		
			
			argsList.add(serviceId);
			DataTable dt = DatabaseMgr.getInstance().execSql(sqlString, argsList);
			if(dt.getRows().size()<=0){
				throw new Exception("001091");
			}
			String designerIds = dt.getRows().get(0).getString("DESIGNERIDS");
			
			String sql = "SELECT ad.URL,hsd.DESIGNERID,hsd.PRICE,hsd.UNIT,ad.name ,ad.desc,ad.DESIGNATION,ad.image FROM heso_service_designer as hsd,heso_admin AS ad " +
					"WHERE  ad.ADMIN_ID = hsd.DESIGNERID AND hsd.DESIGNERID in (" +
					designerIds +
					") AND hsd.SERVICEID = ?;";
			ArrayList<MemberProduct> mpList = new ArrayList<MemberProduct>();
			
			DataTable dt2 = DatabaseMgr.getInstance().execSql(sql, argsList);
			if(dt2.getRows().size()<=0){
				logger.error(">>>>>>>>>>>查询服务对应设计师列表失败");
				throw new Exception("001091");
			}
			for (int j = 0; j < dt2.getRows().size(); j++) {
				DataRow dr = dt2.getRows().get(j);
				MemberProduct mp = new MemberProduct();
				mp.setId(serviceId);
				mp.setUrl(dr.getString("URL"));
				mp.setDesignation(dr.getString("DESIGNATION"));
				mp.setDesignerDesc(dr.getString("DESC"));
				mp.setDesignerId(dr.getString("DESIGNERID"));
				mp.setDesignerName(dr.getString("NAME"));
				mp.setImage(dr.getString("IMAGE"));
				mp.setPrice(dr.getString("PRICE"));
				mp.setUnit(dr.getString("UNIT"));
				mpList.add(mp);
			}
			msro.setMpList(mpList);
			System.out.println(dt.getRows().size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	}

	/**
	 * 根据用户数据匹配套装中单品中的尺码，并返回单品信息
	 * 
	 * @param smao
	 * @return
	 */
	public MallServiceReturnObject matchGoodsSize(String account, String suitId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// 取得用户配置信息
			UserServiceReturnObject usro = new UserService().getUserProfiles(account);
			if (!usro.getCode().equals("000000"))
				throw new Exception(usro.getCode());

			String bust = usro.getUpo().getBust();
			String waist = usro.getUpo().getWaist();
			String hip = usro.getUpo().getHip();
			String shoeSize = usro.getUpo().getShoesize();

			// 获取套装明细
			String sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?)  and status='1' ORDER BY product_id ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.clear();
			argsList.add(suitId);
			DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
			for (int j = 0; j < dtGoods.getRows().size(); j++) {
				DataRow dr1 = dtGoods.getRows().get(j);
				ProductItemObject pioGoods = new ProductItemObject();
				String goodsId = dr1.getString("product_id");
				String size = "";
				/*// 匹配套装size
				sql = "select size from heso_product_size where product_id = ? ";
				argsList.clear();
				argsList.add(goodsId);
				sql += " and bust_begin <= ? and bust_end >= ? ";
				argsList.add(bust);
				argsList.add(bust);
				sql += " and waist_begin <= ? and waist_end >= ? ";
				argsList.add(waist);
				argsList.add(waist);
				sql += " and hip_begin <= ? and hip_end >= ? ";
				argsList.add(hip);
				argsList.add(hip);
				sql += " and yard_begin <= ? and yard_end >= ? ";
				argsList.add(shoeSize);
				argsList.add(shoeSize);
				DataTable dtGood = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if (dtGood.getRows().size() > 0)*/
					size = dr1.getString("size");
				pioGoods.setSize(size);
				pioGoods.setProductId(dr1.getString("product_id"));
				pioGoods.setName(dr1.getString("name"));
				pioGoods.setDesc(dr1.getString("desc"));
				pioGoods.setCategory(dr1.getString("category"));
				pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
				pioGoods.setSupplyName(dr1.getString("supply_name"));
				pioGoods.setColor(dr1.getString("color"));
				pioGoods.setPrice(dr1.getString("price"));
				pioGoods.setDiscountPrice(dr1.getString("discount_price"));
				pioGoods.setSuitPrice(dr1.getString("suit_price"));
				pioGoods.setStockStatus(dr1.getString("stock_status"));
				pioGoods.setStockCount(dr1.getString("stock_count"));
				pioGoods.setImgFront(dr1.getString("img_front"));
				pioGoods.setImgBehind(dr1.getString("img_behind"));
				pioGoodsList.add(pioGoods);
			}
			msro.setPioList(pioGoodsList);
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

	
	public  String dateUpdate(String date,String day){
		DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
		Date date2 = null;
		try {
			 date2= fmt.parse(date);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String date1 = fmt.format(date2);
		if("1".equals(day)){
			return date1;
		}
		Calendar c = Calendar.getInstance(); 
		 c.setTime(date2);  
	        int day2 = c.get(Calendar.DATE);  
	        int num = Integer.parseInt(day);
	        c.set(Calendar.DATE, day2 + num-1); 
	        String dayAfter = date1+" ~ "+fmt.format(c.getTime()); 
	        
		return dayAfter;
		
	}
	public MallServiceReturnObject matchTripsuit(SuitMatchArgsObject smao, String recStart, String recCount, String orderBy ,String category){
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
 		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "" ;
			if(!category.isEmpty()){
				 sql = "select * "
						    + " from heso_product where product_id in "
						    + " (select PRODUCT_SUIT_ID from heso_product_detail where PRODUCT_GOODS_ID in "
						    + " (select product_id from heso_product where category = ?)) and status='1'  and sex = '"+smao.getSex()+"'";
				argsList .add(category);

				// 产品名称
			}else if(!smao.getName().isEmpty()){
				sql = "select * from (select * from heso_product "+
				  "where product_id in "+ 
				  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail as b  "+ 
				  "INNER JOIN heso_product AS a on a.PRODUCT_ID=b.PRODUCT_GOODS_ID  "+
				  "where  a.`STATUS`='1'and a.name like '%"+smao.getName()+"%' and a.type = '1') "+
				  "UNION ( select * from  heso_product as a where   a.STATUS='1' and  a.name like  '%"+smao.getName()+"%')) product  where  product.STATUS='1' and product.sex = '"+smao.getSex()+"'" ;
//				sql = "select * from (select * from heso_product "+
//					  "where product_id in "+ 
//					  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail "+ 
//					  "where PRODUCT_GOODS_ID in "+
//					  "(select product_id from heso_product where name like '%"+smao.getName()+"%' and type = '1')) "+
//					  "UNION ( select * from  heso_product where name like  '%"+smao.getName()+"%')) product ";
			}else{
			    sql = "select * from heso_product where type = 2 and status='1'  and sex = '"+smao.getSex()+"'";
			}
			
			System.out.println(smao.getName());
			// 判断匹配条件
			// 场景
			if (!smao.getScene().isEmpty()) {
				sql += " and find_in_set(?, scene) ";
				argsList.add(smao.getScene());
			}
			// 风格
			if (!smao.getStyle().isEmpty()) {
				sql += " and find_in_set(?, style)  ";
				argsList.add(smao.getStyle());
			}
			// 年龄
			if (!smao.getAge().isEmpty()) {
				sql += " and find_in_set(?,age_type) ";
				argsList.add(smao.getAge());
			}
			// 体型
			if (!smao.getShape().isEmpty()) {
				sql += " and find_in_set(?, shape) ";
				argsList.add(smao.getShape());
			}
			// 身高
			if (!smao.getHeight().isEmpty()) {
				sql += " and height_begin <= ? and height_end >= ? ";
				argsList.add(smao.getHeight());
				argsList.add(smao.getHeight());
			}
			// 胸围
			if (!smao.getBust().isEmpty()) {
				sql += " and bust_begin <= ? and bust_end >= ? ";
				argsList.add(smao.getBust());
				argsList.add(smao.getBust());
			}
			// 腰围
			if (!smao.getWaist().isEmpty()) {
				sql += " and waist_begin <= ? and waist_end >= ? ";
				argsList.add(smao.getWaist());
				argsList.add(smao.getWaist());
			}
			// 臀围
			if (!smao.getHip().isEmpty()) {
				sql += " and hip_begin <= ? and hip_end >= ? ";
				argsList.add(smao.getHip());
				argsList.add(smao.getHip());
			}
			// 鞋长
			if (!smao.getYard().isEmpty()) {
				sql += " and yard_begin <= ? and yard_end >= ? ";
				argsList.add(smao.getYard());
				argsList.add(smao.getYard());
			}
			//季节
			if(!smao.getSeason().isEmpty()){
				String[] s= smao.getSeason().split(",");
				String sqll = " and (FIND_IN_SET('" +
						s[0] +
						"',SEASON)";
				for(int i = 1;i<s.length;i++){
					sqll = sqll+" or FIND_IN_SET('" +
							s[i] +
							"',SEASON)";
				}
				sqll =sqll+ ") ";
				sql  = sql + sqll;
			}
			sql+="order by create_time desc";
			//价格排序
			if (orderBy.equals("1")) {
				sql = sql.replace("order by create_time desc", "order by suit_price desc");
			} else if(orderBy.equals("2")) {
				sql = sql.replace("order by create_time desc", "order by suit_price asc");
			}
			System.out.println(sql);
			
			//计算总条数
			
			//String sql1 = sql.replace("*", "count(*)");
			String sql1="select count(*) from ("+sql+") C";
			DataTable dtCount = DatabaseMgr.getInstance().execSqlTrans(sql1, argsList, conn);
			String count = dtCount.getRows().get(0).getString(0);
			msro.setRecCount(count);
			System.out.println("-----测试------总条数："+count);
			
			
			
			//分页
			sql += " limit ?,? ";
			argsList.add(Integer.parseInt(recStart));
			argsList.add(Integer.parseInt(recCount));
			
			// 匹配套装
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setScene(dr.getString("scene"));
				pioSuit.setStyle(dr.getString("style"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setSoldCount(dr.getString("sold_count"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImages(dr.getString("images"));
				
				// 获取套装明细
				/*sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ? )  ORDER BY product_id ";
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
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));//rong 
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					String stockStatus = dr1.getString("STOCK_STATUS");
					pioGoods.setStockStatus(stockStatus);
					pioGoodsList.add(pioGoods);
					if(stockStatus.equals("0")){
						status = false;
					}
					
				}
				
				if(status){
					pioSuit.setStockStatus("1");
				}else{
					pioSuit.setStockStatus("0");
				}
				
				pioSuit.setGoodsList(pioGoodsList);*/
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
	
	public MallServiceReturnObject matchSuit1(SuitMatchArgsObject smao, String recStart, String recCount, String orderBy ,String category ) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "" ;
			if(!category.isEmpty()){
				 sql = "select * "
						    + " from heso_product where product_id in "
						    + " (select product_id from heso_product where category = ? and type= '1') and status='1' ";
				argsList .add(category);

				// 产品名称
			}else if(!smao.getName().isEmpty()){
				
				sql = "select * "
					    + " from heso_product where product_id in "
					    + " (select product_id from heso_product where name like '%"+smao.getName()+"%' and type = '1') and status='1' ";
//				sql = "select * from (select * from heso_product "+
//					  "where product_id in "+ 
//					  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail "+ 
//					  "where PRODUCT_GOODS_ID in "+
//					  "(select product_id from heso_product where name like '%"+smao.getName()+"%' and type = '1')) "+
//					  "UNION ( select * from  heso_product where name like  '%"+smao.getName()+"%')) product ";
			}else{
			    sql = "select * from heso_product where type = 1 and status='1'  ";
			}
			
			System.out.println(smao.getName());
			// 判断匹配条件
			// 场景
			if (!smao.getScene().isEmpty()) {
				sql += " and find_in_set(?, scene) ";
				argsList.add(smao.getScene());
			}
			// 风格
			if (!smao.getStyle().isEmpty()) {
				sql += " and find_in_set(?, style)  ";
				argsList.add(smao.getStyle());
			}
			// 年龄
			if (!smao.getAge().isEmpty()) {
				sql += " and find_in_set(?,age_type) ";
				argsList.add(smao.getAge());
			}
			// 体型
			if (!smao.getShape().isEmpty()) {
				sql += " and find_in_set(?, shape) ";
				argsList.add(smao.getShape());
			}
			// 身高
			if (!smao.getHeight().isEmpty()) {
				sql += " and height_begin <= ? and height_end >= ? ";
				argsList.add(smao.getHeight());
				argsList.add(smao.getHeight());
			}
			// 胸围
			if (!smao.getBust().isEmpty()) {
				sql += " and bust_begin <= ? and bust_end >= ? ";
				argsList.add(smao.getBust());
				argsList.add(smao.getBust());
			}
			//性别
			if(!smao.getSex().isEmpty()){
				sql += " and sex = ? ";
				argsList.add(smao.getSex());
			}
			//供应商
			if (!smao.getSupply().isEmpty()) {
				sql += " and supply_id = ? ";
				argsList.add(smao.getSupply());
				
			}
			// 腰围
			if (!smao.getWaist().isEmpty()) {
				sql += " and waist_begin <= ? and waist_end >= ? ";
				argsList.add(smao.getWaist());
				argsList.add(smao.getWaist());
			}
			// 臀围
			if (!smao.getHip().isEmpty()) {
				sql += " and hip_begin <= ? and hip_end >= ? ";
				argsList.add(smao.getHip());
				argsList.add(smao.getHip());
			}
			// 鞋长
			if (!smao.getYard().isEmpty()) {
				sql += " and yard_begin <= ? and yard_end >= ? ";
				argsList.add(smao.getYard());
				argsList.add(smao.getYard());
			}
			sql+="order by create_time desc";
			//价格排序
			if (orderBy.equals("1")) {
				sql = sql.replace("order by create_time desc", "order by suit_price desc");
			} else if(orderBy.equals("2")) {
				sql = sql.replace("order by create_time desc", "order by suit_price asc");
			}
			System.out.println(sql);
			
			//计算总条数
			
			//String sql1 = sql.replace("*", "count(*)");
			String sql1="select count(*) from ("+sql+") C";
			DataTable dtCount = DatabaseMgr.getInstance().execSqlTrans(sql1, argsList, conn);
			String count = dtCount.getRows().get(0).getString(0);
			msro.setRecCount(count);
			System.out.println("-----测试------总条数："+count);
			
			
			
			//分页
			sql += " limit ?,? ";
			argsList.add(Integer.parseInt(recStart));
			argsList.add(Integer.parseInt(recCount));
			
			// 匹配套装
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setScene(dr.getString("scene"));
				pioSuit.setStyle(dr.getString("style"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setSoldCount(dr.getString("sold_count"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImages(dr.getString("images"));
				
				// 获取套装明细
			/*	sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ? )  ORDER BY product_id ";
				argsList.clear();
				argsList.add(pioSuit.getProductId());
				DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);*/
				ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
				boolean status = true;
				/*
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
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));//rong 
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					String stockStatus = dr1.getString("STOCK_STATUS");
					pioGoods.setStockStatus(stockStatus);
					pioGoodsList.add(pioGoods);
					if(stockStatus.equals("0")){
						status = false;
					}
					
				}*/
				
				if(status){
					pioSuit.setStockStatus("1");
				}else{
					pioSuit.setStockStatus("0");
				}
				
				pioSuit.setGoodsList(pioGoodsList);
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

	
	/**
	 * 套装匹配2
	 * 
	 * @param smao
	 *            套装匹配参数对象
	 * @return
	 */
	public MallServiceReturnObject matchSuit2(SuitMatchArgsObject smao, String recStart, String recCount, String orderBy ,String category ) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "" ;
			if(!category.isEmpty()){
				 sql = "select * "
						    + " from heso_product where product_id in "
						    + " (select PRODUCT_SUIT_ID from heso_product_detail where PRODUCT_GOODS_ID in "
						    + " (select product_id from heso_product where category = ?)) and status='1' ";
				argsList .add(category);

				// 产品名称
			}else if(!smao.getName().isEmpty()){
				sql = "select * from (select * from heso_product "+
				  "where product_id in "+ 
				  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail as b  "+ 
				  "INNER JOIN heso_product AS a on a.PRODUCT_ID=b.PRODUCT_GOODS_ID  "+
				  "where  a.`STATUS`='1'and a.name like '%"+smao.getName()+"%' and a.type = '1') "+
				  "UNION ( select * from  heso_product as a where   a.STATUS='1' and  a.name like  '%"+smao.getName()+"%')) product  where  product.STATUS='1'";
//				sql = "select * from (select * from heso_product "+
//					  "where product_id in "+ 
//					  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail "+ 
//					  "where PRODUCT_GOODS_ID in "+
//					  "(select product_id from heso_product where name like '%"+smao.getName()+"%' and type = '1')) "+
//					  "UNION ( select * from  heso_product where name like  '%"+smao.getName()+"%')) product ";
			}else{
			    sql = "select * from heso_product where type = 2 and status='1'  ";
			}
			
			System.out.println(smao.getName());
			// 判断匹配条件
			// 场景
			if (!smao.getScene().isEmpty()) {
				sql += " and find_in_set(?, scene) ";
				argsList.add(smao.getScene());
			}
			// 风格
			if (!smao.getStyle().isEmpty()) {
				sql += " and find_in_set(?, style)  ";
				argsList.add(smao.getStyle());
			}
			// 年龄
			if (!smao.getAge().isEmpty()) {
				sql += " and find_in_set(?,age_type) ";
				argsList.add(smao.getAge());
			}
			// 体型
			if (!smao.getShape().isEmpty()) {
				sql += " and find_in_set(?, shape) ";
				argsList.add(smao.getShape());
			}
			// 身高
			if (!smao.getHeight().isEmpty()) {
				sql += " and height_begin <= ? and height_end >= ? ";
				argsList.add(smao.getHeight());
				argsList.add(smao.getHeight());
			}
			// 胸围
			if (!smao.getBust().isEmpty()) {
				sql += " and bust_begin <= ? and bust_end >= ? ";
				argsList.add(smao.getBust());
				argsList.add(smao.getBust());
			}
			// 腰围
			if (!smao.getWaist().isEmpty()) {
				sql += " and waist_begin <= ? and waist_end >= ? ";
				argsList.add(smao.getWaist());
				argsList.add(smao.getWaist());
			}
			// 臀围
			if (!smao.getHip().isEmpty()) {
				sql += " and hip_begin <= ? and hip_end >= ? ";
				argsList.add(smao.getHip());
				argsList.add(smao.getHip());
			}
			// 鞋长
			if (!smao.getYard().isEmpty()) {
				sql += " and yard_begin <= ? and yard_end >= ? ";
				argsList.add(smao.getYard());
				argsList.add(smao.getYard());
			}
			//性别
			if(!smao.getSex().isEmpty()){
				sql += " and sex = ? ";
				argsList.add(smao.getSex());
			}
			sql+="order by create_time desc";
			//价格排序
			if (orderBy.equals("1")) {
				sql = sql.replace("order by create_time desc", "order by suit_price desc");
			} else if(orderBy.equals("2")) {
				sql = sql.replace("order by create_time desc", "order by suit_price asc");
			}
			System.out.println(sql);
			
			//计算总条数
			
			//String sql1 = sql.replace("*", "count(*)");
			String sql1="select count(*) from ("+sql+") C";
			DataTable dtCount = DatabaseMgr.getInstance().execSqlTrans(sql1, argsList, conn);
			String count = dtCount.getRows().get(0).getString(0);
			msro.setRecCount(count);
			System.out.println("-----测试------总条数："+count);
			
			
			
			//分页
			sql += " limit ?,? ";
			argsList.add(Integer.parseInt(recStart));
			argsList.add(Integer.parseInt(recCount));
			
			// 匹配套装
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setScene(dr.getString("scene"));
				pioSuit.setStyle(dr.getString("style"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setSoldCount(dr.getString("sold_count"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImages(dr.getString("images"));
				
				// 获取套装明细
				sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ? )  ORDER BY product_id ";
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
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));//rong 
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					String stockStatus = dr1.getString("STOCK_STATUS");
					pioGoods.setStockStatus(stockStatus);
					pioGoodsList.add(pioGoods);
					if(stockStatus.equals("0")){
						status = false;
					}
					
				}
				
				if(status){
					pioSuit.setStockStatus("1");
				}else{
					pioSuit.setStockStatus("0");
				}
				
				pioSuit.setGoodsList(pioGoodsList);
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
	/**
	 * 套装匹配
	 * 
	 * @param smao
	 *            套装匹配参数对象
	 * @return
	 */
	public MallServiceReturnObject matchSuit(SuitMatchArgsObject smao, String recStart, String recCount, String orderBy ,String category ) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "" ;
			//衣服种类
			if(!category.isEmpty()){
				 sql = "select * "
						    + " from heso_product where product_id in "
						    + " (select PRODUCT_SUIT_ID from heso_product_detail where PRODUCT_GOODS_ID in "
						    + " (select product_id from heso_product where category = ?)) ";
				argsList .add(category);

			// 产品名称
			}else if(!smao.getName().isEmpty()){
				sql = "select * from heso_product "+
					  "where product_id in "+ 
					  "(select DISTINCT(PRODUCT_SUIT_ID) from heso_product_detail "+ 
					  "where PRODUCT_GOODS_ID in "+
					  "(select product_id from heso_product where name like '%"+smao.getName()+"%' and type = '1')) ";
			}else{
			    sql = "select * from heso_product where type = 2  ";
			} 
			// 判断匹配条件
			// 场景
			if (!smao.getScene().isEmpty()) {
				sql += " and find_in_set(?, scene) ";
				argsList.add(smao.getScene());
			}
			// 风格
			if (!smao.getStyle().isEmpty()) {
				sql += " and find_in_set(?, style)  ";
				argsList.add(smao.getStyle());
			}
			// 年龄
			if (!smao.getAge().isEmpty()) {
				sql += " and find_in_set(?,age_type) ";
				argsList.add(smao.getAge());
			}
			// 体型
			if (!smao.getShape().isEmpty()) {
				sql += " and find_in_set(?, shape) ";
				argsList.add(smao.getShape());
			}
			// 身高
			if (!smao.getHeight().isEmpty()) {
				sql += " and height_begin <= ? and height_end >= ? ";
				argsList.add(smao.getHeight());
				argsList.add(smao.getHeight());
			}
			// 胸围
			if (!smao.getBust().isEmpty()) {
				sql += " and bust_begin <= ? and bust_end >= ? ";
				argsList.add(smao.getBust());
				argsList.add(smao.getBust());
			}
			// 腰围
			if (!smao.getWaist().isEmpty()) {
				sql += " and waist_begin <= ? and waist_end >= ? ";
				argsList.add(smao.getWaist());
				argsList.add(smao.getWaist());
			}
			// 臀围
			if (!smao.getHip().isEmpty()) {
				sql += " and hip_begin <= ? and hip_end >= ? ";
				argsList.add(smao.getHip());
				argsList.add(smao.getHip());
			}
			// 鞋长
			if (!smao.getYard().isEmpty()) {
				sql += " and yard_begin <= ? and yard_end >= ? ";
				argsList.add(smao.getYard());
				argsList.add(smao.getYard());
			}
			//根据是否置顶并且置顶的最新时间排序
			sql += "and status = '1'  order by is_recommend desc,  is_recommend_time desc ";
			
			//计算总条数
			String sql1 = sql.replace("*", "count(*)");
			DataTable dtCount = DatabaseMgr.getInstance().execSqlTrans(sql1, argsList, conn);
			String count = dtCount.getRows().get(0).getString(0);
			msro.setRecCount(count);
			
			
			//价格排序
			if (orderBy.equals("1")) {

				sql += " ,suit_price desc ";
			} else if(orderBy.equals("2")) {
				sql += " ,suit_price asc ";
			}else{
				sql += " ,CREATE_TIME DESC  ";
			}
			
			//分页
			sql += " limit ?,? ";
			System.out.println("-----------------------------:"+sql);
			argsList.add(Integer.parseInt(recStart));
			argsList.add(Integer.parseInt(recCount));
			
			// 匹配套装
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setScene(dr.getString("scene"));
				pioSuit.setStyle(dr.getString("style"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setSoldCount(dr.getString("sold_count"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImages(dr.getString("images"));
			  	
				// 获取套装明细
				sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ? )  ORDER BY product_id ";
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
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));//rong 
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					String stockStatus = dr1.getString("STOCK_STATUS");
					pioGoods.setStockStatus(stockStatus);
					pioGoodsList.add(pioGoods);
					if(stockStatus.equals("0")){
						status = false;
					}
					
				}
				
				if(status){
					pioSuit.setStockStatus("1");
				}else{
					pioSuit.setStockStatus("0");
				}
				
				pioSuit.setGoodsList(pioGoodsList);
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
	
	
	
	
	public  ArrayList<KeywordType> getKeyWordTypes(String keyword){
		keyword = keyword.toUpperCase();
		ArrayList<KeywordType> kwList = new ArrayList<KeywordType>();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		String sql = "select * from heso_type where keyword =?";
		ArrayList<Object> argsList = new ArrayList<Object>();
		argsList.clear();
		argsList.add(keyword);
		DataTable dt1;
		try {
			dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
	
			for (int j = 0; j < dt1.getRows().size(); j++) {
				DataRow dr = dt1.getRows().get(j);
				KeywordType sto = new KeywordType();
				sto.setId(dr.getString("id"));
				sto.setName(dr.getString("name"));
				sto.setValue(dr.getString("value"));
				sto.setImage(dr.getString("image"));
				sto.setSex(dr.getString("sex"));
				kwList.add(sto);
			}	
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
		
		return kwList;
		
	}
	/**
	 * 获取商品明细信息(套装)
	 * 
	 * @param productId
	 *            产品编号
	 * @return
	 */
	public MallServiceReturnObject getSuitInfo(String productId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();

		try {
			// 获取套装列表
			String sql = "select * from heso_product where product_id = ? and type = 2 ";
			//sql += " and status = 1";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dtSuit.getRows().size() <=0 ){
				throw new Exception("101265");
			}
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setSex(dr.getString("sex"));
				pioSuit.setNot_suit_nature(dr.getString("NOT_SUIT_NATURE"));
				pioSuit.setBody_notsuit(dr.getString("BODY_NOTSUIT"));
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setCategory(dr.getString("category"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				pioSuit.setScene(dr.getString("scene"));
				pioSuit.setStyle(dr.getString("style"));
				pioSuit.setAgeType(dr.getString("age_type"));
				pioSuit.setShape(dr.getString("shape"));
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImages(dr.getString("images"));
				pioSuit.setImpression(dr.getString("IMPRESSION"));
				pioSuit.setBodySuit(dr.getString("BODY_SUIT"));
				pioSuit.setNotSuitSkin(dr.getString("NOT_SUIT_SKIN"));
				pioSuit.setFaceSuitColor(dr.getString("FACE_SUIT_COLOR"));
				
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
					pioGoods.setSize(dr1.getString("size"));
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setSoldCount(dr1.getString("sold_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setImpression(dr1.getString("IMPRESSION"));
					pioGoods.setBodySuit(dr1.getString("BODY_SUIT"));
					pioGoods.setNotSuitSkin(dr1.getString("NOT_SUIT_SKIN"));
					pioGoods.setFaceSuitColor(dr1.getString("FACE_SUIT_COLOR"));
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

	
	/**
	 * 获取商品明细信息(套装)
	 * 
	 * @param productId
	 *            产品编号 
	 * @return
	 */
	public MallServiceReturnObject getSuitInfoNew(String productId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();

		try {
			// 获取套装列表
			String sql = "select * from heso_product where product_id = ? and type = 2 ";
			//sql += " and status = 1";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			DataTable dtSuit = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dtSuit.getRows().size() <=0 ){
				throw new Exception("101265");
			}
			ArrayList<ProductItemObject> pioSuitList = new ArrayList<ProductItemObject>();
			for (int i = 0; i < dtSuit.getRows().size(); i++) {
				DataRow dr = dtSuit.getRows().get(i);
				ProductItemObject pioSuit = new ProductItemObject();
				pioSuit.setProductId(dr.getString("product_id"));
				pioSuit.setName(dr.getString("name"));
				pioSuit.setSex(dr.getString("sex"));
				String sex = dr.getString("sex");
				//nature不适合
				String natureIds = stringForSql(dr.getString("NOT_SUIT_NATURE"));
				if(!natureIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'NOT_SUIT_NATURE' AND id in (" +
							natureIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtNature = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtNature.getRows().size()>0){
						pioSuit.setNot_suit_nature(dtNature.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setNot_suit_nature("");
					}
				}else {
					pioSuit.setNot_suit_nature("");
				}
				
				//bodyNotSuit
				String bodyNotIds = stringForSql(dr.getString("BODY_NOTSUIT"));
				if(!bodyNotIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'BODY_NOTSUIT' AND id in (" +
							bodyNotIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtbodyNot = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtbodyNot.getRows().size()>0){
						pioSuit.setBody_notsuit(dtbodyNot.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setBody_notsuit("");
					}
				}else {
					pioSuit.setBody_notsuit("");
				}
				
				pioSuit.setDesc(dr.getString("desc"));
				pioSuit.setCategory(dr.getString("category"));
				pioSuit.setDesignName(dr.getString("designer_name"));
				//场景
				String sceneIds = stringForSql(dr.getString("scene"));
				if(!sceneIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'scene' AND id in (" +
							sceneIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable stScene = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(stScene.getRows().size()>0){
						pioSuit.setScene(stScene.getRows().get(0).getString("strname"));					
					}else {
						pioSuit.setScene("");
					}
				}else {
					pioSuit.setScene("");
				}
				
				//风格
				String styleIds = stringForSql(dr.getString("style"));
				if(!styleIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'style' AND id in (" +
							styleIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtStyle = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtStyle.getRows().size()>0){
						pioSuit.setStyle(dtStyle.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setStyle("");
					}
				}else {
					pioSuit.setStyle("");
				}
			
				
				pioSuit.setAgeType(dr.getString("age_type"));
				/*//身形
				String shapeIds = stringForSql(dr.getString("shape")); 	
				sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'shape' AND id in (" +
						shapeIds +
						") AND (SEX = '2' or SEX = '" +
						sex +
						"')";
				argsList.clear();
				DataTable shapeStyle = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(shapeStyle.getRows().size()>0){
					pioSuit.setShape(shapeStyle.getRows().get(0).getString("strname"));
				}else {*/
					pioSuit.setShape("");
//				}
				
				pioSuit.setPrice(dr.getString("price"));
				pioSuit.setDiscountPrice(dr.getString("discount_price"));
				pioSuit.setSuitPrice(dr.getString("suit_price"));
				pioSuit.setImgFront(dr.getString("img_front"));
				pioSuit.setImgBehind(dr.getString("img_behind"));
				pioSuit.setImages(dr.getString("images"));
				//印象
				String yinxiangIds = stringForSql(dr.getString("IMPRESSION"));
				if(!yinxiangIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'IMPRESSION' AND id in (" +
							yinxiangIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtYinxiang = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtYinxiang.getRows().size()>0){
						pioSuit.setImpression(dtYinxiang.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setImpression("");
					}
				}else {
					pioSuit.setImpression("");
				}
				
				//身形
				String bodyIds = stringForSql(dr.getString("BODY_SUIT"));
				if(!bodyIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'BODY_SUIT' AND id in (" +
							bodyIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtbody = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtbody.getRows().size()>0){
						pioSuit.setBodySuit(dtbody.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setBodySuit("");
					}
				}else {
					pioSuit.setBodySuit("");
				}
				
				//皮肤不适合
				String skinNotIds = stringForSql(dr.getString("NOT_SUIT_SKIN"));
				if(!skinNotIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'NOT_SUIT_SKIN' AND id in (" +
							skinNotIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtSkin = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtSkin.getRows().size()>0){
						pioSuit.setNotSuitSkin(dtSkin.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setNotSuitSkin("");
					}
				}else {
					pioSuit.setNotSuitSkin("");

				}
				
				//脸型颜色
				String facetIds = stringForSql(dr.getString("FACE_SUIT_COLOR"));
				if(!facetIds.equals("")){
					sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'FACE_SUIT_COLOR' AND id in (" +
							facetIds +
							") AND (SEX = '2' or SEX = '" +
							sex +
							"')";
					argsList.clear();
					DataTable dtFace = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					if(dtFace.getRows().size()>0){
						pioSuit.setFaceSuitColor(dtFace.getRows().get(0).getString("strname"));
					}else {
						pioSuit.setFaceSuitColor("");
					}
				}else {
					pioSuit.setFaceSuitColor("");
				}
				
 				
				// 获取套装明细
				sql = "select * from heso_product where product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ?) ORDER BY PRODUCT_ID";
				argsList.clear();
				argsList.add(pioSuit.getProductId());
				DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
 				int productZeroStock = 0;
 				for (int j = 0; j < dtGoods.getRows().size(); j++) {
					DataRow dr1 = dtGoods.getRows().get(j);
					ProductItemObject pioGoods = new ProductItemObject();
					String itemid = dr1.getString("product_id");
					pioGoods.setProductId(itemid);
					sql = "SELECT ID,PRODUCT_ID,SIZE,IMAGE,COLOR_ID,COLOR_TYPE,SIZE_STOCK,OUT_STOCK FROM heso_product_size WHERE PRODUCT_ID = ?";
					argsList.clear();
					argsList.add(itemid);
					DataTable dtItem = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					String itemStock = "0";
					int zeroStock = 0;
					List<SizeAndColor> sizeList = new ArrayList<>();
					for(int z = 0; z<dtItem.getRows().size();z++){
						DataRow drItem = dtItem.getRows().get(z);
						SizeAndColor sac = new SizeAndColor();
						String colorId = drItem.getString("COLOR_ID");
						String colorType = drItem.getString("COLOR_TYPE");
						String size = drItem.getString("SIZE");
						String image = drItem.getString("IMAGE");
						String sizeStock = drItem.getString("SIZE_STOCK");
						String outStock = drItem.getString("OUT_STOCK");
						String sizeId = drItem.getString("ID");
						
						sac.setColorId(colorId);
						sac.setColorType(colorType);
						sac.setId(sizeId);
						sac.setInStock(sizeStock);
						sac.setOutStock(outStock);
						sac.setProductId(itemid);
						sac.setSize(size);
						sac.setImage(image);
						sizeList.add(sac);
						if(outStock.equals("0")&&(sizeStock.equals("0")||sizeStock.equals("-1"))){
							zeroStock ++;
						}
					}
					sql = "select COLOR_TYPE,IMAGE,COLOR_ID from heso_product_size WHERE PRODUCT_ID = ? group by COLOR_ID";
					argsList.clear();
					argsList.add(itemid);
					DataTable dtItemColor = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					int s = dtItemColor.getRows().size();
					List<SizeAndColor> coList = new ArrayList<>();
					for(int x=0;x<dtItemColor.getRows().size();x++){
						DataRow dRow = dtItemColor.getRows().get(x);
 						SizeAndColor sac = new SizeAndColor();
						String colorId = dRow.getString("COLOR_ID");
						String colorType = dRow.getString("COLOR_TYPE");
 						String image = dRow.getString("IMAGE");
						sac.setColorId(colorId);
						sac.setColorType(colorType);
  						sac.setImage(image);
						coList.add(sac);
						
					}
					pioGoods.setColors(coList);
					pioGoods.setSizeColor(sizeList);
					if(zeroStock == dtItem.getRows().size()){
						itemStock = "1";
						productZeroStock ++;
					}
					pioGoods.setItemStock(itemStock);
					pioGoods.setName(dr1.getString("name"));
					pioGoods.setDesc(dr1.getString("desc"));
					pioGoods.setCategory(dr1.getString("category"));
					pioGoods.setMetarialDesc(dr1.getString("metarial_desc"));
					pioGoods.setSupplyName(dr1.getString("supply_name"));
					pioGoods.setColor(dr1.getString("color"));
					pioGoods.setPrice(dr1.getString("price"));
					pioGoods.setSize(dr1.getString("size"));
					pioGoods.setSuitPrice(dr1.getString("suit_price"));
					pioGoods.setDiscountPrice(dr1.getString("discount_price"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setStockCount(dr1.getString("stock_count"));
					pioGoods.setSoldCount(dr1.getString("sold_count"));
					pioGoods.setImgFront(dr1.getString("img_front"));
					pioGoods.setImgBehind(dr1.getString("img_behind"));
					pioGoods.setStockStatus(dr1.getString("stock_status"));
					pioGoods.setImpression(dr1.getString("IMPRESSION"));
					pioGoods.setBodySuit(dr1.getString("BODY_SUIT"));
					pioGoods.setNotSuitSkin(dr1.getString("NOT_SUIT_SKIN"));
					pioGoods.setFaceSuitColor(dr1.getString("FACE_SUIT_COLOR"));
					pioGoodsList.add(pioGoods);
					 
				}
				
				if(productZeroStock == dtGoods.getRows().size()){
					pioSuit.setStockStatus("1");
				}else {
					pioSuit.setStockStatus("0");
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
	
	/**
	 * 获取单品信息
	 * 
	 * @param productId
	 * @return
	 */
	public MallServiceReturnObject getGoodsInfo(String productId) {
		MallServiceReturnObject msro = new MallServiceReturnObject();
		try {
			// 获取套装列表
			String sql = "select * from heso_product where product_id = ? and type = 1 and status = 1 ";
	
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			ArrayList<ProductItemObject> pioGoodsList = new ArrayList<ProductItemObject>();
			if (dt.getRows().size() == 0)
				throw new Exception("101201");

			DataRow dr = dt.getRows().get(0);
			ProductItemObject pioGoods = new ProductItemObject();
			pioGoods.setProductId(dr.getString("product_id"));
			pioGoods.setName(dr.getString("name"));
			pioGoods.setDesc(dr.getString("desc"));
			pioGoods.setCategoryId(dr.getString("category"));
			sql = "SELECT ht.NAME FROM heso_type as ht WHERE ht.keyword = 'category' and ht.id = ?";
			argsList.clear();
			argsList.add(dr.getString("category"));
			DataTable dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
			
 			pioGoods.setCategory(dtt.getRows().get(0).getString("name"));
			pioGoods.setMetarialDesc(dr.getString("metarial_desc"));
			pioGoods.setSupplyName(dr.getString("supply_name"));
			pioGoods.setColor(dr.getString("color"));
			pioGoods.setPrice(dr.getString("price"));
			pioGoods.setSuitPrice(dr.getString("suit_price"));
			pioGoods.setDiscountPrice(dr.getString("discount_price"));
			pioGoods.setBrand(dr.getString("brand"));
			pioGoods.setWashingType(dr.getString("washing_type"));
			String image = dr.getString("IMAGES");
			ArrayList<String> imagesList = new ArrayList<>();
			//GOODS_DES
			String[] imastr =  image.split(",");
			for(int i = 0;i<imastr.length;i++){
				imagesList.add(imastr[i]);
			}
			imagesList.add(dr.getString("img_front"));
			imagesList.add(dr.getString("img_behind"));
			pioGoods.setStockStatus(dr.getString("stock_status"));
			pioGoods.setStockCount(dr.getString("stock_count"));
			pioGoods.setSoldCount(dr.getString("sold_count"));
			pioGoods.setImgFront(dr.getString("img_front"));
			pioGoods.setImgBehind(dr.getString("img_behind"));
			pioGoods.setStockStatus(dr.getString("STOCK_STATUS"));
			pioGoods.setImageList(imagesList);
			// 添加单品列表
			pioGoods.setGoodDes(dr.getString("GOODS_DES"));
			pioGoodsList.add(pioGoods);
			msro.setPioList(pioGoodsList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return msro;
	}

	/**
	 * @param args
	 */
	
	
	public  String stringForSql(String ids) {
		
		String [] str = ids.split(",");
		
		String result = "";
		
		for(int i = 0;i<str.length;i++){
			String id = str[i];
			if(!id.isEmpty()&&!id.equals("")){
				result= result + "'" +
						id
						+
						"',";
			}
		}
		result = result + "''";
		
		return result;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MallService ms = new MallService();

		// ms.getGoodsInfo("16FZQZDB00011");
		 
		//MallServiceReturnObject msro = getDesignerDate("5","49") ;
		//String date = dateUpdate("2018-02-24 00:00:00","2");
		//MallServiceReturnObject mallServiceReturnObject = getServiceList();
		//MallServiceReturnObject f =getDesignerDate("5","7");
	//	MallServiceReturnObject k = getServiceListByDesignerId("5");
		//ArrayList<KeywordType> alArrayList = getKeyWordTypes("CATEGORy");
		String sql = "";
		System.out.println(sql);
//		MallServiceReturnObject msro = ms.matchSuit(smao, "0", "10", "1");
//		System.out.println(msro.getCode());
//		System.out.println(msro.getPioList().size());
//		ProductItemObject pio = msro.getPioList().get(0);
//		System.out.println(pio.getProductId());
//		System.out.println(pio.getGoodsList().size());
//		for (ProductItemObject po : pio.getGoodsList()) {
//			System.out.println(po.getName());
//		}
//
//		msro = ms.matchGoodsSize("0000000000000002", "16SP0001");
//		for (ProductItemObject po : msro.getPioList()) {
//			System.out.println(po.getSize());
//		}
//
//	}
	}
}
