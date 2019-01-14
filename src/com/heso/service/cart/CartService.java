package com.heso.service.cart;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
 
import javax.xml.crypto.Data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;
 
import com.heso.common.entity.CommonType;
import com.heso.db.DatabaseMgr;
import com.heso.service.cart.entity.CartItemObject;
import com.heso.service.cart.entity.CartServiceReturnObject;
import com.heso.service.cart.entity.CheckCartReturnObject;
import com.heso.service.cart.entity.CheckObject;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.ConsumeProductObject;
import com.heso.service.sms.SmsService;
import com.heso.utility.ErrorProcess;
import com.heso.utility.JarUtil;
 

import data.DataRow;
import data.DataTable;

public class CartService {
	private static final Log logger = LogFactory.getLog(CartService.class);

	
	
	
	public   String submitCheckCart(String account){
		
		CheckCartReturnObject ccro = new CheckCartReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
 		String sql = "";
 		String checkId = "";
 		ArrayList<Object> argsList = new ArrayList<Object>();
	 
		sql = "SELECT service_match FROM heso_account_level WHERE ACCOUNT = ?";
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);

			argsList.add(account);
		/*	DataTable dt = dbm.execSqlTrans(sql, argsList, conn);;
			int matchTime = dt.getRows().get(0).getInt("service_match");*/
			sql = "SELECT COUNT(ID) FROM heso_check_cart_deatil WHERE ACCOUNT = ?;";
			DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);
  			int leftTime = dtt.getRows().get(0).getInt("COUNT(ID)");
			sql = "SELECT * FROM heso_check_cart_deatil WHERE ACCOUNT = ?";
			DataTable dtt2 = dbm.execSqlTrans(sql, argsList, conn);
			BigDecimal price = new BigDecimal(0);
			List<CheckObject> colist = new ArrayList<CheckObject>();
			for(int i = 0 ;i<dtt2.getRows().size();i++){
				DataRow dr = dtt2.getRows().get(i);
				CheckObject co = new CheckObject();
				co.setAccount(dr.getString("account"));
				co.setIamge(dr.getString("image"));
				co.setName(dr.getString("name"));
				co.setType(dr.getString("type"));
				co.setWardrobeOrTripId(dr.getString("warbrobeid"));
				price = dr.getBigDecimal("price");
				co.setPrice(dr.getBigDecimal("price"));
				colist.add(co);
			}

		/*	if(matchTime<leftTime){
				//返回余量不足
				return "数量不足";
			}
			int n = matchTime - leftTime;*/
			//更新会员权益表在支付后扣减权益
		/*	sql = "UPDATE heso_account_level SET service_match = ? WHERE ACCOUNT = ?";
			argsList.clear();
			argsList.add(n);
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);*/
			BigDecimal amount = price.multiply(new BigDecimal(leftTime));
			
			//加入审核记录表
			sql = "INSERT heso_check_record (ORDER_ID,ID,STATUS,NAME,ACCOUNT,RECORD_TYPE,IMAGE,COUNT,AMOUNT) VALUES (?,?,?,?,?,?,?,?,?)";
			checkId = dbm.getSequence("seq_order", "16");
			String seqId = dbm.getSequence("seq_order", "16");
			argsList.clear();
			argsList.add(seqId);
			argsList.add(checkId);
			argsList.add("1");
			argsList.add("套装录入");
			argsList.add(account);
			argsList.add("1");//1为套装录入
			argsList.add("");
			argsList.add(leftTime);
			argsList.add(amount);
			dbm.execNonSqlTrans(sql, argsList, conn);
			//加入审核记录详情表
			for(CheckObject co : colist){
				sql = "INSERT heso_check_record_detail (RECORD_ID,WARDROBEORTRIPID,IMAGE,NAME,RECORD_TYPE,ACCOUNT,PRICE) VALUES (?,?,?,?,?,?,?);";
				argsList.clear();
				argsList.add(checkId);
				argsList.add(co.getWardrobeOrTripId());
				argsList.add(co.getIamge());
				argsList.add(co.getName());
				argsList.add("1");//1为套装录入
				argsList.add(account);
				argsList.add(co.getPrice());
				dbm.execNonSqlTrans(sql, argsList, conn);
			}
			
			//加入订单记录

			sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image,color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , payment_terms ,from_suit ,recommend, couponDet_Id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(account);
			argsList.add("109");
			argsList.add("4");
			argsList.add("服饰在线搭配审核");
			argsList.add("");
			argsList.add("");
			argsList.add("");
			argsList.add(price);
			argsList.add(leftTime);
			argsList.add(amount);
			argsList.add(amount);
			argsList.add("0");
			argsList.add("0");
			argsList.add("0");
			argsList.add("1");
			argsList.add("0");
			argsList.add(checkId);
			argsList.add("");//增加优惠券使用子表ID
			int rows = dbm.execNonSqlTrans(sql, argsList, conn);
			
			//删除审核购物车
			sql = "DELETE FROM heso_check_cart_deatil where account = ?" ;
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			sql = "DELETE FROM heso_check_cart where account = ?" ;
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			
		} catch (Exception e) {
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
		 
		
		
		return checkId;
	}
	
	/**
	 * 删除查询购物车
	 * @param detailId
	 * @return
	 */
	public static CheckCartReturnObject delCheckDetail(String detailId){
		
		CheckCartReturnObject ccro = new CheckCartReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
 		String sql = "";
 		ArrayList<Object> argsList = new ArrayList<Object>();
		// 删除原始主记录
		sql = "DELETE FROM heso_check_cart_deatil WHERE id= ?";
		try {
			conn = dbm.getConnection();
			argsList.add(detailId);
			DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
		} catch (Exception e) {
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
		return ccro;
		
	}
	/**
	 * 查询购物车
	 * @param checkId
	 * @return
	 */
	public static  CheckCartReturnObject checkCart(String account){
		CheckCartReturnObject ccro = new CheckCartReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
 		String sql = "SELECT * FROM heso_check_cart_deatil WHERE ACCOUNT =?";
 		ArrayList<Object> argsList = new ArrayList<Object>();
 		ArrayList<CheckObject> checkList = new ArrayList<CheckObject>();
 		
		argsList.add(account);
 		try {
			conn = dbm.getConnection();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);;
			if(dt.getRows().size()>0){
				for(int i = 0 ; i < dt.getRows().size() ; i++){
					DataRow dr = dt.getRows().get(i);
					CheckObject co = new CheckObject();
					co.setAccount(dr.getString("ACCOUNT"));
					co.setCheckId(dr.getString("CHECK_ID"));
					co.setIamge(dr.getString("IMAGE"));
					co.setId(dr.getString("ID"));
					co.setWarbrobe(dr.getString("WARBROBEID"));
					co.setType(dr.getString("TYPE"));
					co.setName(dr.getString("NAME"));
					checkList.add(co);
					
				}
			}
			 
		ccro.setCheckList(checkList);
		} catch (Exception e) {
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
		return ccro;
	}
	
	
	
	
	
	public  String payOver(String waiOrderId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		String code = "";
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
 		try {
			conn = DatabaseMgr.getInstance().getConnection();
			
			
			
			String sql = " select order_pay from heso_order_pay where wai_order  = ?  and is_pay=0";
			List<Object> agesData = new ArrayList<Object>();
			List<Object> argsList = new ArrayList<Object>();
			 agesData.add(waiOrderId);
			 DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			if(dt.getRows().size()<=0){
				return "查不到该支付订单";
			}
			String orderId = dt.getRows().get(0).getString("order_pay");
			orderId = orderId.replace(";", "");
			//查询订单
			sql = "SELECT * from heso_order_consume WHERE recommend = ?";
			agesData.clear();
			agesData.add(waiOrderId);
 			DataTable dt2 = dbm.execSqlTrans(sql, agesData, conn);
 			String account = dt2.getRows().get(0).getString("ACCOUNT");
 			String orderconsumeId = dt2.getRows().get(0).getString("order_id");
 			int orderCount = dt2.getRows().get(0).getInt("count");
			//查询用户权益
			  sql = "SELECT ID,COUNT FROM heso_account_quanyi where  (QUANYI = '50' OR QUANYI = '56' OR QUANYI = '62' OR QUANYI = '68') AND STATUS= '0' AND COUNT != 0 AND ACCOUNTS LIKE '%" +
					account +
					"%'";
			  agesData.clear();
 			DataTable dt3 = dbm.execSqlTrans(sql, agesData, conn);
 			int quanyiCount = dt3.getRows().get(0).getInt("COUNT");
 			int quanyiId = dt3.getRows().get(0).getInt("ID");
 			if(orderCount>quanyiCount){
 				code = "000002";
 				return code;
 			}
 			quanyiCount = quanyiCount - orderCount;
 			String status = "0";
 			if(quanyiCount==0){
 				status = "1";
 			}
 			
 			sql = "UPDATE heso_account_quanyi SET COUNT = ? , status= ? WHERE ID = ?";
 			argsList.clear();
 			argsList.add(quanyiCount);
 			argsList.add(status);
 			argsList.add(quanyiId);
			int q = dbm.execNonSqlTrans(sql, argsList, conn);
			//查询绑定老师信息
			sql = "SELECT ha2.name,ha2.mobile,ha2.admin_id FROM heso_account AS ha,heso_admin AS ha2 WHERE ha.account=? and ha.teacherid = ha2.admin_id";
			argsList.clear();
			argsList.add(account);
 			DataTable dt12 = dbm.execSqlTrans(sql, argsList, conn);
 			String teacherId = dt12.getRows().get(0).getString("admin_id");
 			String teacherName = dt12.getRows().get(0).getString("name");
 			String mobile = dt12.getRows().get(0).getString("mobile");
		    //new SmsService().sendMessageByaliyunByorderId(orderIdd, "18826418804" ,SmsService.SMS_TYPE_ORDERID);
			new SmsService().sendMessageByaliyunByorderId("", mobile ,SmsService.SMS_TYPE_SENDORDER);		
			
 			//添加标签派单表
 			sql = "SELECT MAX(ID) as ID FROM heso_send_order ";
 			argsList.clear();
 			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
 			
 			int id = dt1.getRows().get(0).getInt("ID");
 			id = id + 2;
			sql = "INSERT INTO heso_send_order (ID,TEACHER_ID,TEACHER_NAME,ZHULIA_ID,ZHULIA_NAME,ZHULIB_ID,ZHULIB_NAME," +
					"SERVICE_TIME,SERVICE_PLACE,SERVICE_ID,SERVICE_NAME,ACCOUNT,ACCOUNT_NAME,TEACHER_STATUS," +
					"ZHULIA_STATUS,ZHULIB_STATUS,ACCOUNT_QUANYIID,GENJINREN_ID,ORDERIDS,STATUS) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(id);
			argsList.add(teacherId);
			argsList.add(teacherName);
			argsList.add("");
			argsList.add("");
			argsList.add("");
			argsList.add("");
			
			argsList.add("2018-12-31");
			argsList.add("");
			argsList.add("68");
			argsList.add("衣橱审核标签派单");
			argsList.add(account);
			argsList.add("");
			argsList.add("1");
			argsList.add("1");
			argsList.add("0");
			argsList.add("");
			argsList.add("");
			argsList.add(orderconsumeId);
			argsList.add("2");
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			
			//更新订单状态
			sql = " update heso_order_consume set pay_status = '1' , pay_time = SYSDATE(), status = '0'  where  order_id = ?";
			
			argsList.clear();
 			argsList.add(orderconsumeId);
			int d = dbm.execNonSqlTrans(sql, argsList, conn);
			
			
			//更新审核订单支付状态
			sql  = "UPDATE heso_check_record SET STATUS = '1' WHERE Id= ?";
			agesData.clear();
			agesData.add(orderId);
			if (DatabaseMgr.getInstance().execNonSqlTrans(sql, agesData, conn) <= 0)
				throw new Exception("101122");
			
			//更新支付订单支付状态
			sql = "update heso_order_pay set is_pay ='1' , pay_time = SYSDATE() where  wai_order = ? ";
			agesData.clear();
			agesData.add(waiOrderId);
			if (DatabaseMgr.getInstance().execNonSqlTrans(sql, agesData, conn) <= 0)
				throw new Exception("101122");
			
			
			 
			
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return code;
	}
	/**
	 * 审核提交支付
	 * 支付类型/审核类型/account/orderId
	 * @param account
	 * @param orderId
	 * @return
	 */
	
	public  String payOrder(String account ,String orderId ,String payType,String checkType){

		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		String waiOrder = "";
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			String sql = " SELECT ORDER_ID,STATUS,COUNT,amount,RECORD_TYPE FROM heso_check_record WHERE ID = ?";
			List<Object> agesData = new ArrayList<Object>();
			 agesData.add(orderId);
			 DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			BigDecimal amount = dt.getRows().get(0).getBigDecimal("AMOUNT");
			String recordType = dt.getRows().get(0).getString("RECORD_TYPE");
			String orderCusonm = dt.getRows().get(0).getString("ORDER_ID");
		 
			
			//创建支付订单
			 sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money , create_time) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderCusonm);
			argsList.add(payType);
			waiOrder = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(amount);
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
				 throw new Exception("100151");
			 }
			sql = "UPDATE heso_order_consume SET recommend = ? WHERE recommend = ?";
			argsList.clear();
			argsList.add(waiOrder);
			argsList.add(orderId);
			DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			 
			 
			
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
	 
	
		
		return waiOrder;
	}
	
	public     String setCheckCart(String account,String checkId,String woberobeId,String image,String type,String name,String price){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
 		String sql = "SELECT * FROM heso_check_cart where account=? ";
		ArrayList<Object> argsList = new ArrayList<Object>();
		argsList.add(account);
		BigDecimal priceBigDecimal = new BigDecimal(price);
		try {
			conn = dbm.getConnection();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);;
			if(dt.getRows().size()<=0){
				checkId = dbm.getSequence("seq_order", "16");
				sql = "INSERT INTO heso_check_cart (ID,STATUS,ACCOUNT) VALUES (?,?,?)";
				argsList.clear();
				argsList.add(checkId);
				argsList.add("0");
				argsList.add(account);
				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101121");
			}else {
				checkId = dt.getRows().get(0).getString("ID");
			}
			sql = "INSERT INTO heso_check_cart_deatil (WARBROBEID,CHECK_ID,ACCOUNT,IMAGE,TYPE,NAME,PRICE) VALUES (?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(woberobeId);
			argsList.add(checkId);
			argsList.add(account);
			argsList.add(image);
			argsList.add(type);
			argsList.add(name);
			argsList.add(priceBigDecimal);
			if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
				throw new Exception("101121");
		
		} catch (Exception e) {
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
		return checkId;
	}
	
	
	/**
	 * 添加购物车
	 * 
	 * @param account
	 * @param ctType
	 * @param productId
	 * @param count 
	 * @param ct
	 * @return 
	 */
	public CartServiceReturnObject setProduct(CartItemObject cio) {
		// 初始化返回对象
		CartServiceReturnObject csro = new CartServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		DataRow dr = null;
		try { 
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			boolean flag = true;
			//flag = checkStock(cio.getProductId(), cio.getCount(), cio.getSize());
			if(flag){
				// 获取商品信息
				String sql = " select name ,type , images , img_front , " +
								  " case when type='1' then if(DISCOUNT_PRICE = 0,price,DISCOUNT_PRICE) when type = '2' then SUIT_PRICE end price ,color " +
						          " from heso_product where product_id = ? ";
				ArrayList<Object> argsList = new ArrayList<Object>();
				argsList.add(cio.getProductId());
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("101120");

				// 产品名称
				String name = dt.getRows().get(0).getString("name");
				// 产品类型
				String type = dt.getRows().get(0).getString("type");
				// 产品图片
				String image = ""; 
				if(type.equals("2")){
					 image = dt.getRows().get(0).getString("img_front");
				}else{
					 image = dt.getRows().get(0).getString("img_front");
				}
				
				// 取得单价和金额

				String price = dt.getRows().get(0).getString("price");
				float amount = 0;
				if (!cio.getCount().isEmpty())
					amount = Integer.parseInt(cio.getCount()) * new BigDecimal(price).floatValue();
				// 颜色
				String color = dt.getRows().get(0).getString("color");

				sql = "select * from heso_cart where account = ? and product_id = ? ";
				argsList = new ArrayList<Object>();
				argsList.add(cio.getAccount());
				argsList.add(cio.getProductId());
				dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				String subordinate = null ;
				Integer count = 0 ;
				
				//判断购物车里是否有相同的商品，尺寸或颜色相同
				boolean status = false ;
				if(!cio.getSize().isEmpty()||cio.getCioList().size() != 0){
					List <CartItemObject> list = cio.getCioList();
					for(int i =0 ; i<dt.getRows().size() ; i++){
									//判断购物车单品相同的
			
						if(type.equals(CommonType.PRODUCT_TYPE_GOODS.getNumber().toString())){									
							subordinate =  dt.getRows().get(i).getString("SUBORDINATE");
									count = dt.getRows().get(i).getInt("count");
									//判断该商品颜色或尺寸是否相同
									if(!dt.getRows().get(i).getString("size").equals(cio.getSize()) || !dt.getRows().get(i).getString("color").equals(cio.getColor())){
										status = true;
									}else{
										status = false;
									}
									//相同就更新原有信息
									if(!status){
										count += 1;
										amount = amount = count * new BigDecimal(price).floatValue();
										break;
									}
								}else {
									//判断购物车套装相同的
									subordinate =  dt.getRows().get(i).getString("SUBORDINATE");
									count = dt.getRows().get(i).getInt("count");
									sql = "select * from heso_cart_detail where account = ? and product_suit_id = ? and subordinate = ? group by product_id ";
									argsList.clear();
									argsList.add(cio.getAccount());
									argsList.add(cio.getProductId());
									argsList.add(subordinate);
									DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
								
									for(int index = 0 ; index < dt1.getRows().size() ; index++){
										for(int j = 0 ; j < list.size();j++){
											if(dt1.getRows().get(index).getString("product_id").equals(list.get(j).getProductId())){
												sql = " select color from heso_product where product_id = ?  ";
												argsList.clear();
												argsList.add(list.get(j).getProductId());
											/*	DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
												String col = dt2.getRows().get(0).getString("color");*/
												if(!dt1.getRows().get(index).getString("size").equals(list.get(j).getSize()) || !dt1.getRows().get(index).getString("color").equals(list.get(j).getColor()) ){
													subordinate = dbm.getSequence("seq_subordinate", "16");
													status = true;
													break;
												}else{
													status = false ; 
												}
											  }
										}
										if(status){
											break;
										}
								}
								if(!status){
									count += 1;
									amount = amount = count * new BigDecimal(price).floatValue();
									break;
								}
							
							}
							
						}
											
											
				}else{
					subordinate =cio.getSubordinate();
					if(!cio.getCount().isEmpty()){
						count = Integer.parseInt(cio.getCount());
					}
				}
				
				if(subordinate == null || status ){
					subordinate = dbm.getSequence("seq_subordinate", "16");
				}
				
				// 判断购物车中已经是否存在
				if (dt.getRows().size() == 0 || status ) {
					// 添加购物车
					if (type.equals(CommonType.PRODUCT_TYPE_GOODS.getNumber().toString())) {// 添加单品
						sql = "insert into heso_cart (account, product_id, name, type, image, size, color, count,COLOR_TYPE, price, amount,suit_id, subordinate ) values(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
						argsList.clear();
						argsList.add(cio.getAccount());
						argsList.add(cio.getProductId());
						argsList.add(name);
						argsList.add(type);
						argsList.add(image);
						if (cio.getSize().isEmpty()) 
							throw new Exception("101126");
						argsList.add(cio.getSize());
						argsList.add(cio.getColor());
						argsList.add(cio.getCount());
						argsList.add(cio.getColorType());
						argsList.add(price);
						argsList.add(amount);
						argsList.add(cio.getSuitId());
						argsList.add(subordinate);
						if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
							throw new Exception("101121");
					} else {// 添加套装
						sql = "insert into heso_cart (account, product_id, name, type, image, count, price, amount,suit_id,subordinate) values(?,?,?,?,?,?,?,?,?,?) ";
						
						argsList.clear();
						argsList.add(cio.getAccount());
						argsList.add(cio.getProductId());
						argsList.add(name);
						argsList.add(type);
						argsList.add(image);
						argsList.add(cio.getCount());
						argsList.add(price);
						argsList.add(amount);
						argsList.add(cio.getSuitId()); 
						argsList.add(subordinate);
						if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
							throw new Exception("101121");
					
						
						// 查询套装中单品mark
						sql = "select * from heso_product where PRODUCT_ID in (select b.product_goods_id from heso_product a, heso_product_detail b where a.product_id = b.product_suit_id and b.product_suit_id =? ) ";
						argsList.clear();
						argsList.add(cio.getProductId());
						DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
						for (int i = 0; i < dt1.getRows().size(); i++) {
							dr = dt1.getRows().get(i);
							sql = "insert into heso_cart_detail (account, product_suit_id, product_id, type, name, image, count, price, amount, color, COLOR_TYPE,size, suit_price, suit_promotion ,subordinate) value (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
							argsList.clear();
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(dr.getString("product_id"));
							argsList.add(dr.getString("type"));
							argsList.add(dr.getString("name"));
							argsList.add(dr.getString("img_front"));
							argsList.add(cio.getCount());
							String goodsPrice = dr.getString("discount_price");
							String suitPrice = dr.getString("suit_price");
							float goodsAmount = Integer.parseInt(cio.getCount()) * new BigDecimal(goodsPrice).floatValue();
							float suitAmount = Integer.parseInt(cio.getCount()) * new BigDecimal(suitPrice).floatValue();
							argsList.add(goodsPrice);
							argsList.add(suitAmount);
							String size = "";
							String detailcolor = "";
							String detailColorType = "";
							// 更新尺码
							if (cio.getCioList() != null) {
								for (CartItemObject co : cio.getCioList()) {
									if (co.getProductId().equals(dr.getString("product_id"))) {
										size = co.getSize();
										detailcolor = co.getColor();
										detailColorType = co.getColorType();
										break;
									}
								}
							}
							// 如果套装单品尺码为空返回无法添加购物车
							argsList.add(detailcolor);
							argsList.add(detailColorType);
							System.out.println(">>>>>>>>>>>>>>>>>>>>>>"+size);
							if (size.equals("")){
								throw new Exception("101126");
							}
							argsList.add(size);
							argsList.add(suitPrice);
							argsList.add(goodsAmount - suitAmount);
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");
						}
					}
				} else {
					// 更新购物车
					if (type.equals(CommonType.PRODUCT_TYPE_GOODS.getNumber().toString())) {
						// 修改数量
						if (!cio.getCount().isEmpty()) {
							sql = "update heso_cart set  count = ? , amount= ? where account = ? and product_id = ? and subordinate = ? ";
							argsList.clear();
							argsList.add(count);
							argsList.add(amount);
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");
						}
	//					// 修改尺码
	//					if (!cio.getSize().isEmpty()) {
	//						sql = "update heso_cart set size=? where account = ? and product_id = ?";
	//						argsList.clear();
	//						argsList.add(cio.getSize());
	//						argsList.add(cio.getAccount());
	//						argsList.add(cio.getProductId());
	//						if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
	//							throw new Exception("101122");
	//					}
						// 修改选中状态
						if (!cio.getSelected().isEmpty()) {
							sql = "update heso_cart set selected=? where account = ? and product_id = ? and subordinate = ? ";
							argsList.clear();
							argsList.add(cio.getSelected());
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");
						}
					} else {
						// 更新套装记录
						// 修改数量
						if (!cio.getCount().isEmpty()) {
							sql = "update heso_cart set  count = ? , amount= ? where account = ? and product_id = ? and subordinate = ? ";
							argsList.clear();
							argsList.add(count);
							argsList.add(amount);
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");
							
							//更新单品记录
							sql = "update heso_cart_detail set count=?, amount = count * suit_price , suit_promotion = count * price - count*suit_price  where account = ? and product_suit_id = ? and subordinate = ? ";
							argsList.clear();
							argsList.add(count);
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");					
						}
	
						// 修改选中状态
						if (!cio.getSelected().isEmpty()) {
							sql = "update heso_cart set selected=? where account = ? and product_id = ? and subordinate = ? ";
							argsList.clear();
							argsList.add(cio.getSelected());
							argsList.add(cio.getAccount());
							argsList.add(cio.getProductId());
							argsList.add(subordinate);
							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
								throw new Exception("101122");
						}
	
						// 更新单品记录
	//					if (cio.getCioList() != null) {
	//						sql = "select * from heso_cart_detail where account = ? and product_suit_id = ?";
	//						argsList.clear();
	//						argsList.add(cio.getAccount());
	//						argsList.add(cio.getProductId());
	//						DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
	//						for (int i = 0; i < dt2.getRows().size(); i++) {
	//							dr = dt2.getRows().get(i);
	//							sql = "update heso_cart_detail set size=? where account =? and product_id=? and product_suit_id=?";
	//							argsList.clear();
	//							String size = "";
	//							// 更新尺码
	//							for (CartItemObject co : cio.getCioList()) {
	//								if (co.getProductId().equals(dr.getString("product_id"))) {
	//									size = co.getSize();
	//									break;
	//								}
	//							}
	//							argsList.add(size);
	//							argsList.add(cio.getAccount());
	//							argsList.add(dr.getString("product_id"));
	//							argsList.add(cio.getProductId());
	//							if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
	//								throw new Exception("101125");
	//						}
	//					}
					}
				}
				// 提交事务
				conn.commit();

				// 计算购物车内货物数量
				sql = "select sum(count) count from heso_cart where account = ?";
				argsList.clear();
				argsList.add(cio.getAccount());
				dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				String productCount = dt.getRows().get(0).getString("count");
				csro.setCount(productCount);
			}else {
				csro.setCode("101131");
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return csro;
	}

	/**
	 * 购物车移除货品
	 * 
	 * @param account
	 * @param productIdList
	 * @param crt
	 * @return
	 */
	public CartServiceReturnObject removeProduct(String account, ArrayList<String> productIdList) {
		// 初始化返回对象
		CartServiceReturnObject csro = new CartServiceReturnObject();
		csro.setCount("0");

		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			ArrayList<Object> argsList = new ArrayList<Object>();
			for (int index = 0; index < productIdList.size(); index++) {
				String [] productId = productIdList.get(index).split(":");
				String sql = "select * from heso_cart where account = ? and product_id = ? and subordinate = ? ";
				argsList.clear();
				argsList.add(account);
				argsList.add(productId[0]);
				argsList.add(productId[1]);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100126");

				// 删除原始主记录
				sql = "delete from heso_cart where account = ? and product_id = ? and subordinate = ? ";
				argsList.clear();
				argsList.add(account);
				argsList.add(productId[0]);
				argsList.add(productId[1]);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);

				// 删除明细记录
				sql = "delete from heso_cart_detail where account = ? and product_suit_id = ? and subordinate = ? ";
				argsList.clear();
				argsList.add(account);
				argsList.add(productId[0]);
				argsList.add(productId[1]);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			}
			conn.commit();

			// 计算购物车内货物数量
			String sql = "select sum(count) count from heso_cart where account = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			String productCount = dt.getRows().get(0).getString("count");
			if (productCount != null)
				csro.setCount(productCount);
		} catch (Exception e) {
			// TODO: handle exception
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return csro;
	}

	/**
	 * 获取购物车信息
	 * 
	 * @param account
	 * @return
	 */
	public CartServiceReturnObject getInfo(String account) {
		// 初始化返回对象
		CartServiceReturnObject csro = new CartServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = "select * from heso_cart where account=? order by create_time desc";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int totalcount = 0;
			ArrayList<CartItemObject> cioList = new ArrayList<CartItemObject>();
			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr = dt.getRows().get(i);
				CartItemObject cio = new CartItemObject();
				cio.setProductId(dr.getString("product_id"));
				cio.setType(dr.getString("type"));
				cio.setName(dr.getString("name"));
				cio.setImage(dr.getString("image"));
				cio.setCount(dr.getString("count"));
				cio.setPrice(dr.getString("price"));
				cio.setAmount(dr.getString("amount"));
				cio.setColor(dr.getString("color"));
				cio.setSize(dr.getString("size"));
				cio.setSelected(dr.getString("selected"));
				cio.setSubordinate(dr.getString("subordinate"));
				cio.setSuitId(dr.getString("suit_id"));
				ArrayList<CartItemObject> cioDetailList = new ArrayList<CartItemObject>();
				if (cio.getType().equals(CommonType.PRODUCT_TYPE_SUIT.getNumber().toString())) {
					sql = "select * from heso_cart_detail where account = ? and product_suit_id = ?";
					argsList.clear();
					argsList.add(account);
					argsList.add(cio.getProductId());
					DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					for (int j = 0; j < dt1.getRows().size(); j++) {
						DataRow dr1 = dt1.getRows().get(j);
						CartItemObject ciodetail = new CartItemObject();
						ciodetail.setProductId(dr1.getString("product_id"));
						ciodetail.setType(dr1.getString("type"));
						ciodetail.setName(dr1.getString("name"));
						ciodetail.setImage(dr1.getString("image"));
						ciodetail.setCount(dr1.getString("count"));
						ciodetail.setPrice(dr1.getString("price"));
						ciodetail.setAmount(dr1.getString("amount"));
						ciodetail.setColor(dr1.getString("color"));
						ciodetail.setSize(dr1.getString("size"));
						ciodetail.setSubordinate(dr1.getString("subordinate"));
						cioDetailList.add(ciodetail);
					}
				}
				cio.setCioList(cioDetailList);
				cioList.add(cio);
			}
			csro.setCioList(cioList);
		} catch (Exception e) {
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return csro;
	}

	/**
	 * 查询购物车套装明细
	 * 
	 * @param account
	 * @param productId
	 * @return
	 */
	public CartServiceReturnObject getInfoDetail(String account, String productId ,String subordinate) {
		// 初始化返回对象
		CartServiceReturnObject csro = new CartServiceReturnObject();
		try {
			ArrayList<CartItemObject> cioList = new ArrayList<CartItemObject>();
			CartItemObject cio = new CartItemObject();
			ArrayList<CartItemObject> cioDetailList = new ArrayList<CartItemObject>();
			String sql = "select * from heso_cart_detail where account = ? and product_suit_id = ? and  subordinate = ? ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			argsList.add(productId);
			argsList.add(subordinate);
			DataTable dt1 = DatabaseMgr.getInstance().execSql(sql, argsList);
			for (int j = 0; j < dt1.getRows().size(); j++) {
				DataRow dr1 = dt1.getRows().get(j);
				CartItemObject ciodetail = new CartItemObject();
				ciodetail.setProductId(dr1.getString("product_id"));
				ciodetail.setType(dr1.getString("type"));
				ciodetail.setName(dr1.getString("name"));
				ciodetail.setImage(dr1.getString("image"));
				ciodetail.setCount(dr1.getString("count"));
				ciodetail.setPrice(dr1.getString("price"));
				ciodetail.setAmount(dr1.getString("amount"));
				ciodetail.setColor(dr1.getString("color"));
				ciodetail.setSize(dr1.getString("size"));
				ciodetail.setSuitPrice(dr1.getString("suit_price"));
				ciodetail.setSuitPromotion(dr1.getString("suit_promotion"));
				ciodetail.setSubordinate(dr1.getString("subordinate"));
				cioDetailList.add(ciodetail);
			}
			cio.setCioList(cioDetailList);
			cioList.add(cio);
			csro.setCioList(cioList);
			csro.setAccount(account);
		} catch (Exception e) {
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return csro;
	}

	/**
	 * 购物车结算
	 * 
	 * @param account
	 * @param productId
	 * @return
	 */
	public ConsumeOrderReturnObject settle(String account, String innerCoin, String receiveId,String paymentTerms,String recommend ,String couponDetId ,String remarks) {
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// 建立订单对象
			ConsumeOrder order = new ConsumeOrder();
			// 取得购物车选中商品
			String sql = "select * from heso_cart where account = ? and selected = ?  ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			argsList.add(CommonType.CART_SELECT_PRODUCT.getNumber());
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList , conn);
			if (dt.getRows().size() == 0)
				throw new Exception("101130");
			
			for(int i = 0 ; i < dt.getRows().size() ; i++){
				argsList.clear();
				argsList.add(dt.getRows().get(i).getString("product_id"));
				
				if(dt.getRows().get(i).getString("type").equals("1")){
					sql = " select product_id from heso_product where  product_id = ? and  ( status = '0') ";
				}else if(dt.getRows().get(i).getString("type").equals("2")){
					sql = " select product_id from heso_product where  product_id in (select PRODUCT_GOODS_ID from heso_product_detail where product_suit_id = ? ) and ( status = '0') ";
				} 
				DataTable stockStatus = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(stockStatus.getRows().size()>0){
					throw new Exception("101131");
				}
				
			}
			
			double amount = 0.00;
			int count = 0;
			ArrayList<ConsumeOrderObject> coaoList = new ArrayList<ConsumeOrderObject>();
			// 建立订单数据
			for (int index = 0; index < dt.getRows().size(); index++) {
				DataRow dr = dt.getRows().get(index);
				// 建立主数据
				ConsumeOrderObject coao = new ConsumeOrderObject();
				coao.setAccount(account);
				coao.setProductId(dr.getString("product_id"));
				coao.setType(dr.getString("type"));
				coao.setName(dr.getString("name"));
				coao.setImage(dr.getString("image"));
				coao.setPrice(dr.getString("price"));
				coao.setCount(dr.getString("count"));
				coao.setAmount(dr.getString("amount"));
				coao.setSuitId(dr.getString("suit_id"));
				coao.setColor(dr.getString("color"));
				coao.setRemarks(remarks);
				coao.setSubordinate(dr.getString("subordinate"));
				if(dr.getString("type").equals("1")){
					coao.setSize(dr.getString("size"));
				}
				// coao.setCurrency(dr.getString("currency"));
				// coao.setInnerCoin(dr.getString("inner_coin"));
				// coao.setBonusPoint(dr.getString("bonus_point"));

				ArrayList<ConsumeProductObject> cpoList = new ArrayList<ConsumeProductObject>();
				sql = "select * from heso_cart_detail where account=? and  product_suit_id=? and subordinate = ? ";
				argsList.clear();
				argsList.add(account);
				argsList.add(coao.getProductId());
				argsList.add(dr.getString("subordinate"));
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn);
				for (int i = 0; i < dt1.getRows().size(); i++) {
					DataRow dr1 = dt1.getRows().get(i);
					ConsumeProductObject cpo = new ConsumeProductObject();
					cpo.setProductId(dr1.getString("product_id"));
					cpo.setName(dr1.getString("name"));
					cpo.setImage(dr1.getString("image"));
					cpo.setColor(dr1.getString("color"));
					cpo.setCount(dr1.getString("count"));
					cpo.setSize(dr1.getString("size"));
					cpo.setPrice(dr1.getString("price"));
					cpo.setAmount(dr1.getString("amount"));
					cpo.setSuitPrice(dr1.getString("suit_price"));
					cpo.setSuitPromotion(dr1.getString("suit_promotion"));
					cpo.setSubordinate(dr1.getString("subordinate"));
					cpoList.add(cpo);
				}
				coao.setCpoList(cpoList);
				coaoList.add(coao);
			}
			sql ="select REGION_ID from heso_account_recvinfo where id = ? ";
			argsList.clear();
			argsList.add(receiveId);
			DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dt2.getRows().size()<=0){
				throw new Exception("101130");
			}
			String ri = dt2.getRows().get(0).getString("REGION_ID");
			// 生成订单
			coro = order.genarate(coaoList, innerCoin, receiveId,paymentTerms,recommend,couponDetId);
			
			String total = coro.getCooList().get(coro.getCooList().size()-2).getAmount();
			String notSPtotal = coro.getCooList().get(coro.getCooList().size()-1).getAmount();//非特价商品总价
			//首单优惠
			//ConsumeOrderReturnObject coro2 = order.firstPay(total, account,coro.getOrderId());//取消首单优惠 16/11/09
			//优惠券优惠
			//ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, coro2.getDiscount(), coro.getOrderId(), couponDetId, "1","");//从购物车支付时，不需要外单号
			ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, "0", coro.getOrderId(), couponDetId, "1","");
			//物流计费
			ConsumeOrderReturnObject coro1 = order.logisticsPay(ri, Double.parseDouble(total),coro.getOrderId());
			
			Double pay = Double.parseDouble(total)-Double.parseDouble(cop.getDiscount());
			Double logistics = Double.parseDouble(coro1.getReccount());
			
			coro.getCooList().get(coro.getCooList().size()-1).setAmount(String.valueOf(pay+logistics));
		
			
			// 移除购物车商品
			if (coro.getCode().equals("000000")) {
				ArrayList<String> strList = new ArrayList<String>();
				for (int i = 0; i < coaoList.size(); i++) {
					String productId = coaoList.get(i).getProductId();
					String subordinate = coaoList.get(i).getSubordinate();
					String ps = productId+":"+subordinate;
					strList.add(ps);
				} 
				removeProduct(account, strList);
		
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return coro;
	}

	
	
	/**
	 * 购物车结算New
	 * 
	 * 
	 * @param account
	 * @param productId
	 * @return
	 */
	public ConsumeOrderReturnObject settleNew(String account, String innerCoin, String reciveId,String paymentTerms,String recommend ,String couponDetId ,String remark) {
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		ConsumeOrderReturnObject coroDingzhi = new ConsumeOrderReturnObject();
		ConsumeOrderReturnObject coroo = new ConsumeOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// 建立订单对象
			ConsumeOrder order = new ConsumeOrder();
			// 取得购物车选中商品
			String sql = "select * from heso_cart where account = ? and selected = ?  ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			argsList.add(CommonType.CART_SELECT_PRODUCT.getNumber());
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList , conn);
			if (dt.getRows().size() == 0)
				throw new Exception("101130");
			List<ProductsDTO> productsDTOs = new ArrayList<ProductsDTO>();
			ArrayList<ConsumeOrderObject> coaooList = new ArrayList<ConsumeOrderObject>();
			ArrayList<ConsumeOrderObject> coaooListDingzhi = new ArrayList<ConsumeOrderObject>();
			ArrayList<ConsumeOrderObject> coaoList = new ArrayList<ConsumeOrderObject>();
			for(int i = 0 ; i < dt.getRows().size() ; i++){
				String colorType = dt.getRows().get(i).getString("COLOR_TYPE");
				if(colorType==null||colorType.equals("null")||colorType.equals("")){
					DataRow dr = dt.getRows().get(i);
					// 建立主数据
					ConsumeOrderObject coao = new ConsumeOrderObject();
					coao.setAccount(account);
					coao.setProductId(dr.getString("product_id"));
					coao.setType(dr.getString("type"));
					coao.setName(dr.getString("name"));
					coao.setImage(dr.getString("image"));
					coao.setPrice(dr.getString("price"));
					coao.setCount(dr.getString("count"));
					coao.setAmount(dr.getString("amount"));
					coao.setSuitId(dr.getString("suit_id"));
					coao.setColor(dr.getString("color"));
					coao.setRemarks(remark);
					coao.setSubordinate(dr.getString("subordinate"));
					if(dr.getString("type").equals("1")){
						coao.setSize(dr.getString("size"));
					}
					// coao.setCurrency(dr.getString("currency"));
					// coao.setInnerCoin(dr.getString("inner_coin"));
					// coao.setBonusPoint(dr.getString("bonus_point"));

					ArrayList<ConsumeProductObject> cpoList = new ArrayList<ConsumeProductObject>();
					sql = "select * from heso_cart_detail where account=? and  product_suit_id=? and subordinate = ? ";
					argsList.clear();
					argsList.add(account);
					argsList.add(coao.getProductId());
					argsList.add(dr.getString("subordinate"));
					DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn);
					for (int ij = 0; ij < dt1.getRows().size(); ij++) {
						DataRow dr1 = dt1.getRows().get(ij);
						ConsumeProductObject cpo = new ConsumeProductObject();
						cpo.setProductId(dr1.getString("product_id"));
						cpo.setName(dr1.getString("name"));
						cpo.setImage(dr1.getString("image"));
						cpo.setColor(dr1.getString("color"));
						cpo.setCount(dr1.getString("count"));
						cpo.setSize(dr1.getString("size"));
						cpo.setPrice(dr1.getString("price"));
						cpo.setAmount(dr1.getString("amount"));
						cpo.setSuitPrice(dr1.getString("suit_price"));
						cpo.setSuitPromotion(dr1.getString("suit_promotion"));
						cpo.setSubordinate(dr1.getString("subordinate"));
						cpoList.add(cpo);
					}
					coao.setCpoList(cpoList);
					coaooListDingzhi.add(coao);
					continue;
				}
				
				
				String productId = dt.getRows().get(i).getString("product_id");
				String size = dt.getRows().get(i).getString("SIZE");
				String count = dt.getRows().get(i).getString("COUNT");
				String cartId = dt.getRows().get(i).getString("ID");
				String subordinate = dt.getRows().get(i).getString("subordinate");
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setProductId(productId);
				coo.setSubordinate(subordinate);
				coaooList.add(coo);
				sql = "select * from heso_cart_detail where account=? and  product_suit_id=? and subordinate = ? ";
				argsList.clear();
				argsList.add(account);
				argsList.add(productId);
				argsList.add(subordinate);
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn);
				

				//套装内品类list
				List<String> pinleiList = new ArrayList<String>();
				//套装内面料list
				List<String> mianliaoList = new ArrayList<String>();
				List<String> xiadanfangshiList = new ArrayList<String>();
				ProductsDTO productsDTO = new ProductsDTO();
				productsDTO.setProdctId(productId);
				productsDTO.setColor(colorType);
				productsDTO.setCount(count);
				productsDTO.setSize(size);	
				 
				List<ProductsDTO> productDTOs = new ArrayList<ProductsDTO>();
				
				String dapinlei = "000000";
				String damianliao = "";
				String daxiadanfangshi = "";
				if (dt1.getRows().size()>0) {
					for (int j = 0; j <dt1.getRows().size();j++) {
						ProductsDTO productDTO = new ProductsDTO();
						DataRow drow = dt1.getRows().get(j);
						productDTO.setProdctId(drow.getString("PRODUCT_ID"));
						productDTO.setSize(drow.getString("SIZE"));
						productDTO.setCount(drow.getString("COUNT"));
						productDTO.setColor(drow.getString("COLOR_TYPE"));
						
						ProductsDTO smallOrderDto = order.productToProductsDTO(productDTO);
						smallOrderDto.setType("1");
						productDTOs.add(smallOrderDto);
						//填充套装内品类/面料list
						mianliaoList.add(smallOrderDto.getColor());
						pinleiList.add(smallOrderDto.getPinlei());
						xiadanfangshiList.add(smallOrderDto.getXiadangfangshi());
						if(j == dt1.getRows().size()-1){
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
								productsDTO.setCount(drow.getString("COUNT"));
								productsDTO.setSize(drow.getString("COLOR_TYPE"));	
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
			coroDingzhi =  order.genarateNew(coaooListDingzhi, innerCoin, reciveId,paymentTerms,recommend,couponDetId);
			
			String seller = "";//客户跟进人
			String sellerName = "";//客户跟进人
			String qudaojingliId = "";
			String qudaojingli = "";
			String teacher = "";
			String teacherId = "";
			String qudao = "";
			String qudaoId =""; 
			String mobile ="";
			
			
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
					coao.setCategory("");
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
			coro= order.innerOrder4(coaoList,account, "", reciveId, paymentTerms, "");
			
			// 移除购物车商品
			if (coro.getCode().equals("000000")) {
				ArrayList<String> strList = new ArrayList<String>();
				for (int i = 0; i < coaooList.size(); i++) {
					String productId = coaooList.get(i).getProductId();
					String subordinate = coaooList.get(i).getSubordinate();
					String ps = productId+":"+subordinate;
					strList.add(ps);
				}
				for (int i = 0; i < coaooListDingzhi.size(); i++) {
					String productId = coaooList.get(i).getProductId();
					String subordinate = coaooList.get(i).getSubordinate();
					String ps = productId+":"+subordinate;
					strList.add(ps);
				}
				//removeProduct(account, strList);
			}
			List<String> Strs = new ArrayList<String>();
			String orderIds = coro.getOrderId()+coroDingzhi.getOrderId();
 			String[] strList = orderIds.split(";");
			for(int i =0 ; i<strList.length ; i++){
				if(!strList[i].isEmpty()){
					Strs.add(strList[i]);					
				}
			}
			coroo = new ConsumeOrder().payOrder(account, Strs,paymentTerms);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return coroo;
	}
	
	/**
	 * 购物车物品全选/全不选
	 * 
	 * @param account
	 * @param selected
	 * @return
	 */
	public CartServiceReturnObject setAllSelected(String account, String selected) {
		CartServiceReturnObject csro = new CartServiceReturnObject();
		try {
			String sql = "update heso_cart set selected=? where account = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(selected);
			argsList.add(account);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("101130");

		} catch (Exception e) {
			// TODO: handle exception
			csro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return csro;
	}

	
	
	/**
	 * 扣减库存
	 * @param productId
	 * @param count
	 * @param size
	 * @return
	 */
	public   boolean checkStock(String productId,String count,String size,String color){
		try {
			Connection conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			//查询商品剩余库存
			String sql = "SELECT SIZE_STOCK ,OUT_STOCK FROM heso_product_size WHERE PRODUCT_ID =? AND SIZE = ? AND color_id = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			argsList.add(size);
			argsList.add(color);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int sizeStock = dt.getRows().get(0).getInt("SIZE_STOCK");//剩余库存\
			String out_stock = dt.getRows().get(0).getString("OUT_STOCK");
			int countNum = Integer.valueOf(count);
			if(out_stock.equals("0")){
				if(sizeStock<countNum){
					logger.error(">>>>>>>>>>>>>>>>>>>>>库存不足");
					return false;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
 			logger.error(e.getMessage());
			e.printStackTrace();
		}
	 
		return true;
	}
	
	
	
	
	
	/**
	 * 扣减库存
	 * @param productId
	 * @param count
	 * @param size
	 * @return
	 */
	public   boolean subStock(String productId,String count,String size){
		try {
			Connection conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			//查询商品剩余库存
			String sql = "SELECT SIZE_STOCK ,OUT_STOCK FROM heso_product_size WHERE PRODUCT_ID =? AND SIZE = ? ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			argsList.add(size);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int sizeStock = dt.getRows().get(0).getInt("SIZE_STOCK");//剩余库存\
			String out_stock = dt.getRows().get(0).getString("OUT_STOCK");
			int countNum = Integer.valueOf(count);
			if(out_stock.equals("0")){
				
				if(sizeStock<countNum){
					logger.error(">>>>>>>>>>>>>>>>>>>>>库存不足");
					return false;
					
				}
			}
			//开始扣减库存 
			sql = "UPDATE heso_product_size SET SIZE_STOCK = ? WHERE PRODUCT_ID =? AND SIZE = ? ";
			argsList.clear();
			argsList.add(sizeStock-countNum>0?sizeStock-countNum:0);
			argsList.add(productId);
			argsList.add(size);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0){
				logger.error(">>>>>>>>>>>>>>>>>>>>>库存不足");
				return false;			
			}
			sql = "UPDATE heso_product SET  STOCK_COUNT = STOCK_COUNT -" +
					count+ " WHERE PRODUCT_ID =? AND SIZE = ? ";
			argsList.clear();
			argsList.add(productId);
			argsList.add(size);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0){
				throw new Exception("101130");				
			}
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception
 			logger.error(e.getMessage());
			e.printStackTrace();
		}
	 
		return true;
	}
	
	
	
	/**
	 * 返回库存
	 * @param productId
	 * @param count
	 * @param size
	 * @return
	 */
	public    int addStock(String productId,String count,String size){
		try {
			Connection conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			//查询商品剩余库存
			String sql = "SELECT SIZE_STOCK FROM heso_product_size WHERE PRODUCT_ID =? AND SIZE = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(productId);
			argsList.add(size);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int sizeStock = dt.getRows().get(0).getInt("SIZE_STOCK");//剩余库存
			int countNum = Integer.valueOf(count);
			/*if(sizeStock<countNum){
				logger.error(">>>>>>>>>>>>>>>>>>>>>库存不足");
				throw new Exception("10113");
				}*/
			//开始返回库存
			sql = "UPDATE heso_product_size SET SIZE_STOCK = ? WHERE PRODUCT_ID =? AND SIZE = ? ";
			argsList.clear();
			argsList.add(sizeStock+countNum);
			argsList.add(productId);
			argsList.add(size);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0){
				throw new Exception("101130");				
			}
			sql = "UPDATE heso_product SET  STOCK_COUNT = STOCK_COUNT +" +
					count
					+
					" WHERE PRODUCT_ID =? AND SIZE = ? ";
			argsList.clear();
			argsList.add(productId);
			argsList.add(size);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0){
				throw new Exception("101130");				
			}
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception
 			logger.error(e.getMessage());
			e.printStackTrace();
		}
	 
		return 1;
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
		// // TODO Auto-generated method stub
		/*String account = "0000000000000024";
		// 添加购物车
		CartService cs = new CartService();
		CartServiceReturnObject csro = null;
		CartItemObject cio = new CartItemObject();
		cio.setAccount("0000000000000024");
		//cio.setProductId("16FZQZDB00011");
		cio.setProductId("16C0164");
		cio.setCount("1");
		cio.setColor("黄色");
		cio.setSize("SSS");
		cio.setSelected("1");
		csro = cs.setProduct(cio);
		csro = cs.setAllSelected(account, "1");
		System.out.println(csro.getCode());
		System.out.println("商品总数:" + csro.getCount());

		// cs.settle(account);
		csro = cs.getInfo(account);
		System.out.println(csro.getCioList().size());
		CartService cartService=new CartService();
		 
		/*boolean i = subStock(productId, count, size);
		System.out.println(">>>>>>>>>>GG>>>>>>>>>>>>>"+i);*/
		// csro = cs.setProduct(account, "16FZQZDB00011", "2", "1");
		// System.out.println(csro.getCode());
		// System.out.println("商品总数:" + csro.getCount());

		// // 移除购物车
		// ArrayList<String> strList = new ArrayList<String>();
		// strList.add("testsuit001");
		// usro = us.removeCartProduct(account, strList,
		// CommonType.CART_USER_REMOVE);
		// System.out.println(usro.getCode());
		// System.out.println(usro.getProductCount());

		// // 查询购物车
		// csro = cs.getInfo(account);
		// System.out.println(csro.getCode());
		// System.out.println("商品总数:" + csro.getCount());
		// ArrayList<CartItemObject> cioList = csro.getCioList();
		// for (int i = 0; i < cioList.size(); i++) {
		// CartItemObject cio = cioList.get(i);
		// System.out.println(cio.getProductId());
		// System.out.println(cio.getName());
		// System.out.println(cio.getPrice());
		// } 
		List<String> Strs = new ArrayList<String>();
		String orderIds = "fdfd;fdfdf;";
			String[] strList = orderIds.split(";");
		for(int i =0 ; i<strList.length ; i++){
			if(strList[i].isEmpty()){
				Strs.add(strList[i]);					
			}
		}
 		  System.out.println("----");
	}

}
