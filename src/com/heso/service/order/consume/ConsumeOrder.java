package com.heso.service.order.consume;

import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.monitor.Monitor;
import javax.sql.rowset.serial.SerialArray;

import oracle.net.aso.b;
import oracle.net.aso.d;
import oracle.net.aso.e;
import oracle.net.aso.f;
import oracle.net.aso.l;
import oracle.net.aso.q;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.SessionActionMapping;
import org.omg.IOP.Codec;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

 
import antlr.debug.NewLineEvent;

import com.heso.data.entity.AmaniOrderdetail;
import com.heso.data.entity.CustomerInformation;
import com.heso.data.entity.Orderinformation;
import com.heso.db.DatabaseMgr;
import com.heso.service.cart.CartService;
import com.heso.service.expert.ExpertService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductDTO;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.mall.entity.ProductsDTO;
import com.heso.service.order.consume.entity.ClothPrice;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.service.order.consume.entity.ConsumeProductObject;
import com.heso.service.order.consume.entity.HesoType;
import com.heso.service.order.consume.entity.OrderTicheng;
import com.heso.service.order.consume.entity.QaTestAndResult;
import com.heso.service.order.consume.entity.QaTestQuestions;
import com.heso.service.order.consume.entity.SendOrder;
import com.heso.service.order.consume.entity.TichengDTO;
import com.heso.service.order.consume.entity.VideoHistory;
import com.heso.service.sms.SmsService;
import com.heso.service.wardrobe.WardrobeService;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.utility.ErrorProcess;
import com.mchange.v1.db.sql.DriverManagerDataSource.DmdsObjectFactory;
 
import data.DataRow;
import data.DataTable;

public class ConsumeOrder {
	private static final Log logger = LogFactory.getLog(ConsumeOrder.class);

	/**
	 * 生成订单
	 * 
	 * @param coao
	 * @param listObject
	 * @return
	 */
	public ConsumeOrderReturnObject genarate(ArrayList<ConsumeOrderObject> coaoList, String innerCoin, String receiveId,String paymentTerms ,String recommend,String couponDetId) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try { 
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			StringBuffer orderId = new StringBuffer();
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				String sql = "select * from heso_account where account = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
					throw new Exception("000150");

				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image,color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , payment_terms ,from_suit ,recommend, couponDet_Id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(coao.getAccount());
				argsList.add(coao.getProductId());
				argsList.add(coao.getType());
				argsList.add(coao.getName());
				argsList.add(coao.getImage());
				argsList.add(coao.getColor());
				argsList.add(coao.getSize());
				argsList.add(coao.getPrice());
				argsList.add(coao.getCount());
				argsList.add(coao.getAmount());
				argsList.add(coao.getCurrency());
				argsList.add(coao.getInnerCoin());
				argsList.add(coao.getBonusPoint());
				argsList.add(receiveId);
				argsList.add(paymentTerms);
				argsList.add(coao.getSuitId());
				argsList.add(recommend);
				argsList.add(couponDetId);//增加优惠券使用子表ID
				int rows = dbm.execNonSqlTrans(sql, argsList, conn);
				if (rows <= 0)
					throw new Exception("100150");

				ArrayList<ConsumeProductObject> copList = coao.getCpoList();
				for (ConsumeProductObject cpo : copList) {
				//	sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(seqId);
					argsList.add(cpo.getProductId());
					argsList.add(cpo.getName());
					argsList.add(cpo.getImage());
					argsList.add(cpo.getColor());
					argsList.add(cpo.getSize());
					argsList.add(cpo.getCount());
					argsList.add(cpo.getPrice());
					argsList.add(cpo.getAmount());
					argsList.add(cpo.getSuitPrice());
					argsList.add(cpo.getSuitPromotion());
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					rows = dbm.execNonSqlTrans(sql, argsList, conn);
					if (rows <= 0)
						throw new Exception("100151");
				}
				 
				ConsumeOrderObject coo = new ConsumeOrderObject(); 
				coo.setOrderId(seqId);
				coo.setPaymentTerms(paymentTerms);
				total  = total.add( new BigDecimal(coao.getAmount())); 
				
				if(!coao.getProductId().contains("SPE")){
					notSPtotal = notSPtotal.add(new BigDecimal(coao.getAmount()));
				}
				
				cooList.add(coo);
				orderId.append(seqId+";");
				
				
				
				
			}
			
			//创建支付订单
			String sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money ,CREATE_TIME) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderId.toString());
			argsList.add(paymentTerms);
			String waiOrder = dbm.getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(total);
			 if(dbm.execNonSqlTrans(sql, argsList, conn) <= 0 ){
				 throw new Exception("100151");
			 }
			 
			//更新用户优惠券表   modify by djh
			if(!couponDetId.equals("") && couponDetId!=""){
				
				if(couponDetId.contains(","))
					couponDetId = "'" + couponDetId.replace(",", "','") + "'";
				else
					couponDetId = "'"+couponDetId+"'";
				
				String sOrderId = orderId.toString().substring(0,orderId.toString().length());
				if(sOrderId.contains(";")){
					sOrderId = sOrderId.replace(";", ",");
				}
				
				sql = "UPDATE heso_user_coupon SET COUPON_STATE=1,USE_TIME=now(),ORDER_ID=? WHERE ACCOUNT=? AND COUPONDET_ID IN ("+couponDetId+")";
				argsList.clear();
				argsList.add(sOrderId);
				argsList.add(coaoList.get(0).getAccount());
				if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
					throw new Exception("100151");
				}
				
				//更新优惠券子表使用状态等。
				sql = "update heso_coupon_det set IS_USE=1,ACCOUNT=?,USE_TIME=NOW(),ORDER_ID=? where COUPONDET_ID in ("+couponDetId+")";
				argsList.clear();
				argsList.add(coaoList.get(0).getAccount());
				argsList.add(sOrderId);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
					throw new Exception("100151");
				}
			}
			 
			 conn.commit();
			 //添加地址
			 updateLoadAdd(receiveId, coaoList.get(0).getAccount(), orderIds);
			 
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));//原来总价
			cooList.add(coo1);

			ConsumeOrderObject coo2 = new ConsumeOrderObject();
			coo2.setAmount(String.valueOf(notSPtotal));//非特价商品总价
			cooList.add(coo2);
			 
			coro.setCooList(cooList);
			coro.setOrderId(waiOrder);
		} catch (Exception e) {
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return coro;
	}
	
	
	/**
	 * 生成订单不生成支付订单
	 * 
	 * @param coao
	 * @param listObject
	 * @return
	 */
	public ConsumeOrderReturnObject genarateNew(ArrayList<ConsumeOrderObject> coaoList, String innerCoin, String receiveId,String paymentTerms ,String recommend,String couponDetId) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try { 
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			StringBuffer orderId = new StringBuffer();
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				String sql = "select * from heso_account where account = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
					throw new Exception("000150");
				sql = "SELECT COST_PRICE,DISCOUNT_PRICE FROM heso_product WHERE PRODUCT_ID =?";
				argsList.clear();
				argsList.add(coao.getProductId());
				DataTable dtr = dbm.execSqlTrans(sql, argsList, conn);
				String costPrice = dtr.getRows().get(0).getString("COST_PRICE");
				String disPrice = dtr.getRows().get(0).getString("DISCOUNT_PRICE");
				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image,color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , payment_terms ,from_suit ,recommend, couponDet_Id,DISCOUNT_PRICE,price_cost,price_cost_sum) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(coao.getAccount());
				argsList.add(coao.getProductId());
				argsList.add(coao.getType());
				argsList.add(coao.getName());
				argsList.add(coao.getImage());
				argsList.add(coao.getColor());
				argsList.add(coao.getSize());
				argsList.add(coao.getPrice());
				argsList.add(coao.getCount());
				argsList.add(coao.getAmount());
				argsList.add(coao.getCurrency());
				argsList.add(coao.getInnerCoin());
				argsList.add(coao.getBonusPoint());
				argsList.add(receiveId);
				argsList.add(paymentTerms);
				argsList.add(coao.getSuitId());
				argsList.add(recommend);
				argsList.add(couponDetId);//增加优惠券使用子表ID
				argsList.add(disPrice);
				
				
				if(costPrice!=null&&!costPrice.equals("")&&!costPrice.equals("0")){
					argsList.add(costPrice);
					BigDecimal cost = new BigDecimal(costPrice);
					BigDecimal countD = new BigDecimal(coao.getCount());
					BigDecimal costSum = cost.multiply(countD);
					argsList.add(costSum);
				}else {
					argsList.add("");
					argsList.add("");
				}
				int rows = dbm.execNonSqlTrans(sql, argsList, conn);
				if (rows <= 0)
					throw new Exception("100150");

				ArrayList<ConsumeProductObject> copList = coao.getCpoList();
				for (ConsumeProductObject cpo : copList) {
				//	sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(seqId);
					argsList.add(cpo.getProductId());
					argsList.add(cpo.getName());
					argsList.add(cpo.getImage());
					argsList.add(cpo.getColor());
					argsList.add(cpo.getSize());
					argsList.add(cpo.getCount());
					argsList.add(cpo.getPrice());
					argsList.add(cpo.getAmount());
					argsList.add(cpo.getSuitPrice());
					argsList.add(cpo.getSuitPromotion());
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					rows = dbm.execNonSqlTrans(sql, argsList, conn);
					if (rows <= 0)
						throw new Exception("100151");
				}
				 
				ConsumeOrderObject coo = new ConsumeOrderObject(); 
				coo.setOrderId(seqId);
				coo.setPaymentTerms(paymentTerms);
				total  = total.add( new BigDecimal(coao.getAmount())); 
				
				if(!coao.getProductId().contains("SPE")){
					notSPtotal = notSPtotal.add(new BigDecimal(coao.getAmount()));
				}
				
				cooList.add(coo);
				orderId.append(seqId+";");
			}
			 
			 conn.commit();
			 //添加地址
			 updateLoadAdd(receiveId, coaoList.get(0).getAccount(), orderIds);
			 
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));//原来总价
			cooList.add(coo1);

			ConsumeOrderObject coo2 = new ConsumeOrderObject();
			coo2.setAmount(String.valueOf(notSPtotal));//非特价商品总价
			cooList.add(coo2);
			coro.setOrderId(orderId+"");
			coro.setCooList(cooList);
		 
		} catch (Exception e) {
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return coro;
	}
	
	
	
	/**
	 * 商家 添加备注
	 * @param orderId
	 * @param desc
	 * @return
	 */
	public  ConsumeOrderReturnObject addDesc(String orderId,String desc){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		List<Object> argsList = new ArrayList<Object>();
		try {
			conn = dbm.getConnection();
			String sql = "update heso_order_consume set REMARKS = ? where ORDER_ID = ?";
			argsList.add(desc);
			argsList.add(orderId);
			if(dbm.execNonSqlTrans(sql, argsList, conn) <= 0 ){
				throw new Exception("100151");
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
		return null;
	}
	
	
	public  ConsumeOrderReturnObject createServiceOrder(String serviceId,String account,String designerId,String addressId,String mobile){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		List<Object> argsList = new ArrayList<Object>();
		try {
			conn = dbm.getConnection();
			String sql = "insert into heso_order_consume (order_id, account, CURRENCY , INNER_COIN , BONUS_POINT , PRODUCT_ID , NAME , status , image,CHANGE_TYPE) "
					   + "values (?,?,?,?,?,?,?,?,?,?) ";
 		 
			if(dbm.execNonSqlTrans(sql, argsList, conn) <= 0 ){
				throw new Exception("100151");
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
		return coro;
	}
	
	/**
	 * 生成订单2内部
	 * 
	 * @param coao
	 * @param listObject
	 * @return
	 */
	public ConsumeOrderReturnObject genarate2(ArrayList<ConsumeOrderObject> coaoList, String innerCoin, String receiveId,String paymentTerms ,String recommend,String couponDetId) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			StringBuffer orderId = new StringBuffer();
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				String sql = "select * from heso_account where account = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
					throw new Exception("000150");

				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image," +
						"color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , " +
						"payment_terms ,from_suit ,recommend, couponDet_Id,channel_type,format_type," +
						"size_type,style_type,cloth_type,seller,remark,CATEGORY,XIZHUANG_WSTYLE,CHANGDUSTYLE," +
						"XIKU_WSTYLE,SEASON,GONGYI,DIANJIAN,ZUODIANJIAN,YOUDIANJIAN,SUOYAN_STYLE,SUOYAN_COLOR," +
						"XIUMOKOU_COLOR,XIZHUANGKOU,XIKUKOU,CHENYIKOU,NVCHIMA,NVKUCHIMA,YUANCHANGID) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(coao.getAccount());
				argsList.add(coao.getProductId());
				argsList.add(coao.getType());
				argsList.add(coao.getName());
				argsList.add(coao.getImage());
				argsList.add(coao.getColor());
				argsList.add(coao.getSize());
				argsList.add(coao.getPrice());
				argsList.add(coao.getCount());
				argsList.add(coao.getAmount());
				argsList.add(coao.getCurrency());
				argsList.add(coao.getInnerCoin());
				argsList.add(coao.getBonusPoint());
				argsList.add(receiveId);
				argsList.add(paymentTerms);//支付方式
				argsList.add(coao.getSuitId());
				argsList.add(recommend);//推荐人
				argsList.add(couponDetId);
				//20171122
				argsList.add(coao.getChannelType());
				argsList.add(coao.getFormatType());
				argsList.add(coao.getSizeType());
				argsList.add(coao.getStyleType());
				argsList.add(coao.getClothType());
				argsList.add(coao.getSeller());
				argsList.add(coao.getRemark());
				
				argsList.add(coao.getCategory());
				argsList.add(coao.getXizhuangwStyle());
				argsList.add(coao.getChangduStyle());
				argsList.add(coao.getXikuwStyle());
				argsList.add(coao.getSeason());
				argsList.add(coao.getGongyi());
				argsList.add(coao.getDianjian());
				argsList.add(coao.getZuodianjian());
				argsList.add(coao.getYoudianjian());
				argsList.add(coao.getSuoyanStyle());
				argsList.add(coao.getSuoyanColor());
				argsList.add(coao.getXiumokouColor());
				argsList.add(coao.getXizhuangkou());
				argsList.add(coao.getXikukou());
				argsList.add(coao.getChenyikou());
				argsList.add(coao.getNvchima());
				argsList.add(coao.getNvkuchima());
				argsList.add(coao.getYuanchangId());
				//
				int rows = dbm.execNonSql(sql, argsList);
				if (rows <= 0)
					throw new Exception("100150");
				coro.setOrderId(seqId);

				ArrayList<ConsumeProductObject> copList = coao.getCpoList();
				if(copList != null){
					for (ConsumeProductObject cpo : copList) {
						sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(seqId);
						argsList.add(cpo.getProductId());
						argsList.add(cpo.getName());
						argsList.add(cpo.getImage());
						argsList.add(cpo.getColor());
						argsList.add(cpo.getSize());
						argsList.add(cpo.getCount());
						argsList.add(cpo.getPrice());
						argsList.add(cpo.getAmount());
						argsList.add(cpo.getSuitPrice());
						argsList.add(cpo.getSuitPromotion());
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						//
						rows = dbm.execNonSql(sql, argsList);
						if (rows <= 0)
							throw new Exception("100151");
					}
				}
				
				
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(seqId);
				coo.setPaymentTerms(paymentTerms);
				total  = total.add( new BigDecimal(coao.getAmount())); 
				
				if(!coao.getProductId().contains("SPE")){
					notSPtotal = notSPtotal.add(new BigDecimal(coao.getAmount()));
				}
				
				cooList.add(coo);
				orderId.append(seqId+";");
				
			}
			
			//创建支付订单
			String sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money ,CREATE_TIME) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderId.toString());
			argsList.add(paymentTerms);
			String waiOrder = dbm.getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(total);
			//
			int y = dbm.execNonSql(sql, argsList) ;
			 if( y <= 0 ){
				 throw new Exception("100152");
			 }
			 
			//更新用户优惠券表   modify by djh
			if(!couponDetId.equals("") && couponDetId!=""){
				
				if(couponDetId.contains(","))
					couponDetId = "'" + couponDetId.replace(",", "','") + "'";
				else
					couponDetId = "'"+couponDetId+"'";
				
				String sOrderId = orderId.toString().substring(0,orderId.toString().length());
				if(sOrderId.contains(";")){
					sOrderId = sOrderId.replace(";", ",");
				}
				
				sql = "UPDATE heso_user_coupon SET COUPON_STATE=1,USE_TIME=now(),ORDER_ID=? WHERE ACCOUNT=? AND COUPONDET_ID IN ("+couponDetId+")";
				argsList.clear();
				argsList.add(sOrderId);
				argsList.add(coaoList.get(0).getAccount());
				//
				if(DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0 ){
					throw new Exception("100151");
				}
				
				//更新优惠券子表使用状态等。
				sql = "update heso_coupon_det set IS_USE=1,ACCOUNT=?,USE_TIME=NOW(),ORDER_ID=? where COUPONDET_ID in ("+couponDetId+")";
				argsList.clear();
				argsList.add(coaoList.get(0).getAccount());
				argsList.add(sOrderId);
				//
				if(DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0 ){
					throw new Exception("100151");
				}
			}
			 
			  conn.commit();
			 //添加地址
			 updateLoadAdd(receiveId, coaoList.get(0).getAccount(), orderIds);
			 
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));//原来总价
			cooList.add(coo1);

			ConsumeOrderObject coo2 = new ConsumeOrderObject();
			coo2.setAmount(String.valueOf(notSPtotal));//非特价商品总价
			cooList.add(coo2);
			
			coro.setCooList(cooList);
		} catch (Exception e) {
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return coro;
	}
	
	
	
	
	
	//查询secondType
	public List<HesoType> checkSecondType(String belongId,String sex){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		
		List<HesoType> list = new ArrayList<HesoType>();
		Connection conn = null; 
		try {
			conn = dbm.getConnection();
			String sql = "SELECT VALUE FROM heso_type WHERE keyword = 'CATEGORY' AND ID=?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(belongId);
			DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);
			
			String value = dtt.getRows().get(0).getString("VALUE");
			
			sql = "SELECT * FROM heso_category_type WHERE CATEGORY_TYPE = ? AND (sex = ? OR sex = '2')";
			
			argsList.clear();
			argsList.add(value);
			argsList.add(sex);
			
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() >0){
				for(int i=0;i<dt.getRows().size();i++){
					HesoType hesoType = new HesoType();
					DataRow dr = dt.getRows().get(i);
					String id = dr.getString("ID");
					String name = dr.getString("NAME");
 					String desc = dr.getString("DESC");
					String belong = dr.getString("BELONG");
					String categoryType = dr.getString("CATEGORY_TYPE");
					hesoType.setId(id);
 					hesoType.setName(name);
					hesoType.setCategoryType(categoryType);
					hesoType.setDesc(desc);
					hesoType.setSex(sex);
					hesoType.setBelong(belong);
				 
					list.add(hesoType);
					}
				}
			 
		
				 
		}catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	//查询type表
	public List<HesoType> checkType(String keyword){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		
		List<HesoType> list = new ArrayList<HesoType>();
		Connection conn = null; 
		try {
			conn = dbm.getConnection();
			 
	 
			String sql = "SELECT * FROM heso_type where keyword = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(keyword);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() >0){
				for(int i=0;i<dt.getRows().size();i++){
					HesoType hesoType = new HesoType();
					DataRow dr = dt.getRows().get(i);
					String id = dr.getString("ID");
					String name = dr.getString("NAME");
					String image = dr.getString("IMAGE");
					String value = dr.getString("VALUE");
					String sex = dr.getString("SEX");
					String label = dr.getString("LABEL");
					String effect = dr.getString("EFFECT");
					String crowd = dr.getString("CROWD");
					hesoType.setId(id);
					hesoType.setKeyword(keyword);
					hesoType.setName(name);
					hesoType.setImage(image);
					hesoType.setValue(value);
					hesoType.setSex(sex);
					hesoType.setLabel(label);
					hesoType.setEffect(effect);
					hesoType.setCrowd(crowd);
					list.add(hesoType);
					}
				}
			 
		
				 
		}catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}

	
	//查询测试结果
		public QaTestAndResult checkTestResult(String account){
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			// 初始化返回对象
			ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
			Connection conn = null;
			QaTestAndResult qaResult = new QaTestAndResult();
			try {
				conn = dbm.getConnection();
				conn.setAutoCommit(false); 
		 
				String sql = "SELECT * FROM heso_qa_result WHERE account = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(account);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() >0){
					String id = dt.getRows().get(0).getString("ID");
					String zhuStyle = dt.getRows().get(0).getString("ZHU_STYLE");
					//
					String zhuStyleId = dt.getRows().get(0).getString("ZHU_STYLE_ID");
					String zhuStyleName = dt.getRows().get(0).getString("ZHU_STYLE_NAME");
					String fuStyle = dt.getRows().get(0).getString("FU_STYLE");
					//
					String fuStyleId = dt.getRows().get(0).getString("FU_STYLE_ID");
					String fuStyleName = dt.getRows().get(0).getString("FU_STYLE_NAME");
					String styleDesc = dt.getRows().get(0).getString("STYLE_DESC");
					//
					String bodyPattern = dt.getRows().get(0).getString("BODY_PATTERN");
					String bodyPatternName = dt.getRows().get(0).getString("BODY_PATTERN_NAME");
					//
					String bodyNotSuit = dt.getRows().get(0).getString("BODY_NOTSUIT");
					String bodyNotSuitName = dt.getRows().get(0).getString("BODY_NOTSUIT_NAME");
					String sex = dt.getRows().get(0).getString("SEX");
					String skinNotSuit =  dt.getRows().get(0).getString("NOT_SUIT_SKIN");
					String skinNotSuitName =  dt.getRows().get(0).getString("NOT_SUIT_SKIN_NAME");
					String tedian =  dt.getRows().get(0).getString("STYLE_TEDIAN");
					String zhuiqiu =  dt.getRows().get(0).getString("STYLE_ZHUIQIU");
					String youshi =  dt.getRows().get(0).getString("STYLE_YOUSHI");
					String age = dt.getRows().get(0).getString("AGE");
					String industry = dt.getRows().get(0).getString("INDUSTRY");
					qaResult.setId(id);
					qaResult.setZhuStyle(zhuStyle);
					qaResult.setZhuStyleId(zhuStyleId);
					qaResult.setZhuStyleName(zhuStyleName); 
					qaResult.setFuStyle(fuStyle);
					qaResult.setFuStyleId(fuStyleId);
					qaResult.setFuStyleName(fuStyleName);
					qaResult.setStyleDesc(styleDesc);
					qaResult.setBodyPattern(bodyPattern);
					qaResult.setBodyPatternName(bodyPatternName);
					qaResult.setBodyNotSuit(bodyNotSuit);
					qaResult.setBodyNotSuitName(bodyNotSuitName);
					qaResult.setSkinNotSuit(skinNotSuit);
					qaResult.setSkinNotSuitName(skinNotSuitName);
					qaResult.setTedian(tedian);
					qaResult.setZhuiqiu(zhuiqiu);
					qaResult.setYoushi(youshi);
					qaResult.setAge(age);
					qaResult.setIndustry(industry);
					MallServiceReturnObject msro = new ExpertService().getTestSuit(sex,zhuStyleId, fuStyleId, bodyPattern, bodyNotSuit,skinNotSuit,age);
					MallServiceReturnObject msro2 = new ExpertService().getTestSuitTuozhan(sex,zhuStyleId, fuStyleId, bodyPattern, bodyNotSuit,skinNotSuit,age);
					qaResult.setMsro(msro);
					qaResult.setMsroTuozhan(msro2);
					}
				 
			 
					 
			}catch (Exception e) {
				// TODO: handle exception
			}
			return qaResult;
		}
	
	/**
	 * 测试
	 * @param color
	 * @param peishi
	 * @param miaoliao
	 * @param shengxing
	 * @param quedian
	 * @param skinNotSuit
	 * @param account
	 * @param fenshu
	 * @return
	 */
	public  String qaTest(String color,String peishi,String miaoliao,String shengxing,String quedian,String skinNotSuit,String account,String fenshu){
		
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
 		Connection conn = null;
		String styleZhu = "";
		String styleFu = "";
		String code = "000001";
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
 			 
	 
			// 获取用户账户信息
			String sql = "SELECT QA_OPTION  FROM heso_qa_test WHERE ID = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.clear();
			argsList.add(color);
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			String style1 = dt1.getRows().get(0).getString("QA_OPTION"); 
			sql = "SELECT QA_OPTION  FROM heso_qa_test WHERE ID in (" +
					peishi +
					");"; 
			argsList.clear();
			DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
			String style21 = dt2.getRows().get(0).getString("QA_OPTION"); 
		/*改为单选
		 * 	String style22 = ""; 
			if(dt2.getRows().size()>1){
				style22 = ","+ dt2.getRows().get(1).getString("QA_OPTION");
			}
			*/
			sql = "SELECT QA_OPTION  FROM heso_qa_test WHERE ID in (" +
					miaoliao +
					");";
			argsList.clear();
			DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
			String style31 = dt3.getRows().get(0).getString("QA_OPTION"); 
			/*改为单选
			 * String style32 = ""; 
			if(dt3.getRows().size()>1){
				style32 = ","+ dt3.getRows().get(1).getString("QA_OPTION");
			}*/
			
			String styleAll = style1+","+style31+","+style21;
			System.out.println(styleAll);
			List<String> list = Arrays.asList(styleAll.split(","));;
			Map<String,Integer> elementsCount=new HashMap<String,Integer>();
		    String tmp = "";
            int max_cnt = 0;  
            Pattern p ;
            Matcher m;  
            String regex = "";
            String max_str = "";  
            String tot_str = list.toString(); 
            for(String str : list) {  
                if (tmp.equals(str)) continue;            
                tmp = str;  
                regex = str;  
                p = Pattern.compile(regex);  
                m = p.matcher(tot_str);  
                int cnt = 0;  
                while(m.find()) {  
                    cnt++;  
                }  
                //System.out.println(str + ":" + cnt);  
                if (cnt > max_cnt) {  
                    max_cnt = cnt;  
                    max_str = str;  
                }  
            }    
			if(max_cnt==1){
				styleZhu = style21;
				styleFu = style31;
			}else {
				styleZhu = max_str;
				for(String str:list){
					if(!max_str.equals(str)){
						styleFu = str;
					}
				}
			}
            
			sql = "SELECT QANAME,SEX,KEY_WORD,KEY_WORD_BELONG, STYLE_TEDIAN,STYLE_ZHUIQIU,STYLE_YOUSHI FROM heso_qa_test WHERE ID = ?";
			argsList.clear();
			argsList.add(styleZhu);
			DataTable dt4 = dbm.execSqlTrans(sql, argsList, conn);
			String zhuStyle = dt4.getRows().get(0).getString("QANAME"); 
			String sex = dt4.getRows().get(0).getString("SEX"); 
			String zhukeyWord = dt4.getRows().get(0).getString("KEY_WORD"); 
			String zhukeywordBelong = dt4.getRows().get(0).getString("KEY_WORD_BELONG"); 
			String tedian = dt4.getRows().get(0).getString("STYLE_TEDIAN");
			String zhuiqiu = dt4.getRows().get(0).getString("STYLE_ZHUIQIU");
			String youshi =dt4.getRows().get(0).getString("STYLE_YOUSHI");
			
			
			sql = "SELECT DESCI,QA_OPTION,KEY_WORD,KEY_WORD_BELONG, STYLE_TEDIAN,STYLE_ZHUIQIU,STYLE_YOUSHI FROM heso_qa_test WHERE KEY_WORD = ? and KEY_WORD_BELONG = ?";
			String fuStyle =""; 
			//String sex = dt4.getRows().get(0).getString("SEX"); 
			String fukeyWord = ""; 
			String fukeywordBelong = ""; 
			
			argsList.clear();
			argsList.add(styleZhu);
			argsList.add(fenshu);
			DataTable dt5 = dbm.execSqlTrans(sql, argsList, conn);
			fuStyle = dt5.getRows().get(0).getString("DESCI"); 
			//String sex = dt4.getRows().get(0).getString("SEX"); 
			fukeyWord = "STYLE"; 
			fukeywordBelong = dt5.getRows().get(0).getString("QA_OPTION"); 
			if(zhukeywordBelong.equals(fukeywordBelong)){
				fuStyle = "无";
				fukeywordBelong = "无";
			}
				
			
			sql = "SELECT QANAME,SEX,KEY_WORD,KEY_WORD_BELONG FROM heso_qa_test WHERE ID in (" +
					shengxing +
					");";
			argsList.clear();
			DataTable dt6 = dbm.execSqlTrans(sql, argsList, conn);
			String bodyPatternName = "";
			String bodyPatternId = ",";
			String bodyNotSuitName = "";
			String bodyNotSuitId = ",";
			for(int i = 0;i<dt6.getRows().size();i++){
				bodyPatternName = dt6.getRows().get(i).getString("QANAME");
				bodyPatternId = bodyPatternId+ dt6.getRows().get(i).getString("KEY_WORD_BELONG") +",";
			}
			sql = "SELECT QANAME,SEX,KEY_WORD,KEY_WORD_BELONG FROM heso_qa_test WHERE ID in (" +
					quedian  +
					");";
			argsList.clear();
			DataTable dt7 = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0;i<dt7.getRows().size();i++){
				bodyNotSuitName = bodyNotSuitName+dt7.getRows().get(i).getString("QANAME")+",";
				bodyNotSuitId = bodyNotSuitId+ dt7.getRows().get(i).getString("KEY_WORD_BELONG") +",";
			}
			
			String skinNotSuitId = ",";
			String skinNotSuitName = "";
			sql = "SELECT QANAME,SEX,KEY_WORD,KEY_WORD_BELONG FROM heso_qa_test WHERE ID in (" +
					skinNotSuit  +
					");";
			argsList.clear();
			DataTable dt8 = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0;i<dt8.getRows().size();i++){
				skinNotSuitName = skinNotSuitName+dt8.getRows().get(i).getString("QANAME")+",";
				skinNotSuitId = skinNotSuitId+ dt8.getRows().get(i).getString("KEY_WORD_BELONG") +",";
			}
			
			// 取序列
		/*	String seqId = dbm.getSequence("seq_order", "16");
			orderIds.add(seqId);
			if (seqId.isEmpty())
				throw new Exception("000150");*/
			sql = "select id FROM heso_qa_result WHERE account = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dTable = dbm.execSqlTrans(sql, argsList, conn);
			if(dTable.getRows().size()!=0){
				sql = "UPDATE heso_qa_result SET ZHU_STYLE = ?,ZHU_STYLE_NAME = ?,ZHU_STYLE_ID = ?,FU_STYLE = ?,FU_STYLE_NAME = ?,FU_STYLE_ID = ?,STYLE_DESC = ?,BODY_PATTERN = ?,BODY_PATTERN_NAME = ?,BODY_NOTSUIT_NAME = ?,BODY_NOTSUIT = ?,ACCOUNT = ?,SEX=?,NOT_SUIT_SKIN = ?,NOT_SUIT_SKIN_NAME = ?,STYLE_TEDIAN = ?,STYLE_ZHUIQIU = ?,STYLE_YOUSHI = ? WHERE ACCOUNT = ?";
				argsList.clear();
				argsList.add(zhukeyWord);
				argsList.add(zhuStyle);
				argsList.add(zhukeywordBelong);
				argsList.add(fukeyWord);
				argsList.add(fuStyle);
				argsList.add(fukeywordBelong);
				argsList.add("");
				argsList.add(bodyPatternId);
				argsList.add(bodyPatternName);
				argsList.add(bodyNotSuitName);
				argsList.add(bodyNotSuitId);
				argsList.add(account);
				argsList.add(sex);
				argsList.add(skinNotSuitId);
				argsList.add(skinNotSuitName);
				argsList.add(tedian);
				argsList.add(zhuiqiu);
				argsList.add(youshi);
				argsList.add(account);
				int rows = dbm.execNonSql(sql, argsList);
				if(rows>0){
					return "000000";
					
				}
			}else {
				
				sql = "INSERT INTO heso_qa_result (ZHU_STYLE,ZHU_STYLE_NAME,ZHU_STYLE_ID,FU_STYLE,FU_STYLE_NAME,FU_STYLE_ID," +
						"STYLE_DESC,BODY_PATTERN,BODY_PATTERN_NAME,BODY_NOTSUIT_NAME,BODY_NOTSUIT,ACCOUNT,SEX,NOT_SUIT_SKIN," +
						"NOT_SUIT_SKIN_NAME,STYLE_TEDIAN,STYLE_ZHUIQIU,STYLE_YOUSHI) " +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(zhukeyWord);
				argsList.add(zhuStyle);
				argsList.add(zhukeywordBelong);
				argsList.add(fukeyWord);
				argsList.add(fuStyle);
				argsList.add(fukeywordBelong);
				argsList.add("");
				argsList.add(bodyPatternId);
				argsList.add(bodyPatternName);
				argsList.add(bodyNotSuitName);
				argsList.add(bodyNotSuitId);
				argsList.add(account);
				argsList.add(sex);
				argsList.add(skinNotSuitId);
				argsList.add(skinNotSuitName);
				argsList.add(tedian);
				argsList.add(zhuiqiu);
				argsList.add(youshi);
				int rows = dbm.execNonSql(sql, argsList);
				if(rows > 0 ){
					code = "000000";
				}
			}
			
			
			/*sql = "DELETE FROM heso_qa_result WHERE account = ?";
			argsList.clear();
			argsList.add(account);
			int row = dbm.execNonSql(sql, argsList);*/

		}catch (Exception e) {
			// TODO: handle exception
		}
	 
		
		return code;
	}
	
	
	
	
	//查询测试题
	public   List<List<QaTestQuestions>>  checkTestQuestion(){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		List<List<QaTestQuestions>> list = new ArrayList<List<QaTestQuestions>>();
		List<QaTestQuestions> qaTestQuestions1 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions6 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions15 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions24 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions30 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions36 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions43 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions52 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions60 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions74 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions89 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions95 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions109 = new ArrayList<QaTestQuestions>();
		List<QaTestQuestions> qaTestQuestions114 = new ArrayList<QaTestQuestions>();
		
		QaTestQuestions qaResult = new QaTestQuestions();
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false); 
	 
			String sql = "SELECT ID,QANAME,IMAGE,TYPE,BELONG,SEX,DESCI,KEY_WORD_BELONG from heso_qa_test WHERE type = '1'";
			List<Object> argsList = new ArrayList<Object>();
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() >0){
				for(int i = 0;i<dt.getRows().size();i++){
					QaTestQuestions qatest = new QaTestQuestions();
					DataRow dr = dt.getRows().get(i);
					String id = dr.getString("ID");
					String qanameString= dr.getString("QANAME");
					String image= dr.getString("IMAGE");
					String type= dr.getString("TYPE");
					String belong= dr.getString("BELONG");
					String desci= dr.getString("DESCI");
					String sex= dr.getString("SEX");
					String keyWordBelong= dr.getString("KEY_WORD_BELONG");
					if("1".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions1.add(qatest);
					}
					if("6".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions6.add(qatest);
					}
					if("15".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions15.add(qatest);
					}
					if("24".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions24.add(qatest);
					}
					if("30".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions30.add(qatest);
					}
					if("36".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions36.add(qatest);
					}
					if("43".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions43.add(qatest);
					}
					if("52".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions52.add(qatest);
					}
					if("60".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions60.add(qatest);
					}
					if("74".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions74.add(qatest);
					}
					if("89".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions89.add(qatest);
					}
					if("95".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions95.add(qatest);
					}
					if("109".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions109.add(qatest);
					}
					if("114".equals(belong)){
						qatest.setId(id);
						qatest.setQanameString(qanameString);
						qatest.setImage(image);
						qatest.setType(type);
						qatest.setBelong(belong);
						qatest.setDesci(desci);
						qatest.setSex(sex);
						qatest.setKeyWordBelong(keyWordBelong);
						qaTestQuestions114.add(qatest);
					}
				}
			}
			list.add(qaTestQuestions1);
			list.add(qaTestQuestions6);
			list.add(qaTestQuestions15);
			list.add(qaTestQuestions24);
			list.add(qaTestQuestions30);
			list.add(qaTestQuestions36);
			list.add(qaTestQuestions43);
			list.add(qaTestQuestions52);
			list.add(qaTestQuestions60);
			list.add(qaTestQuestions74);
			list.add(qaTestQuestions89);
			list.add(qaTestQuestions95);
			list.add(qaTestQuestions109);
			list.add(qaTestQuestions114);
				 
		}catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
	public ConsumeOrderReturnObject genarate3(ArrayList<ConsumeOrderObject> coaoList, String innerCoin, String receiveId,String paymentTerms ,String recommend,String couponDetId) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		// 初始化返回对象
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			StringBuffer orderId = new StringBuffer();
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				String sql = "select * from heso_account where account = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
					throw new Exception("000150");

				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name," +
						"price,count,amount, currency, inner_coin, bonus_point, receive_id , " +
						"payment_terms ,from_suit ,recommend, couponDet_Id,channel_type,format_type," +
						"size_type,style_type,cloth_type,seller,remark) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
			}
		  
		}catch (Exception e) {
			// TODO: handle exception
		}
		return coro;
	}
	/**
	 *  获取生成订单信息
	 */
	public ConsumeOrderReturnObject getInfo(String account, String receiveId ){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			
			String sql = " select amount , payment_terms , order_id from heso_order_consume  where account = ? and receive_id = ? pay_status = '0' and status = '0' ";
			List<Object>list= new ArrayList<Object>();
			list.add(account);
			list.add(receiveId);
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, list);
			
			if (dt.getRows().size() == 0)
				throw new Exception("100300");

			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();

			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr = dt.getRows().get(i);
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(dr.getString("order_id"));
				coo.setPaymentTerms(dr.getString("payment_terms"));
				coo.setAmount(dr.getString("amount"));
				cooList.add(coo);
			}
			coro.setCooList(cooList);
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return coro;
	}
	/**
	 * 获取订单列表
	 * 
	 * @param account
	 * @return
	 */
	public ConsumeOrderReturnObject getInfo(String account, String orderId, String payStatus, String sendStatus, String returnStatus, String showStatus, int recStart, int recCount) {
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			// 获取记录总数
			String sql = "select count(*) from heso_order_consume where account=? and CHANGE_TYPE = '0' ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			if (!orderId.isEmpty()) {
				sql += " and order_id=? ";
				argsList.add(orderId);
			}
			if (!payStatus.isEmpty()) {
				sql += " and pay_status=? ";
				argsList.add(payStatus);
			}
			if (!sendStatus.isEmpty()) {
				sql += " and send_status=? ";
				argsList.add(sendStatus);
			}
			if (!returnStatus.isEmpty()) {
				sql += " and return_status=? ";
				argsList.add(payStatus);
			}
			if (!showStatus.isEmpty()) {
				sql += " and show_status=? ";
				argsList.add(showStatus);
			}
			sql += " and status = '0' ";
			
			coro.setReccount(DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn).getRows().get(0).getString(0));
			if (coro.getReccount().equals("0"))
				throw new Exception("101300");
		 
			sql = "select * from heso_order_consume where account=? and CHANGE_TYPE = '0' ";
			argsList.clear();
			argsList.add(account);
			if (!orderId.isEmpty()) {
				sql += " and order_id=? ";
				argsList.add(orderId);
			}
			if (!payStatus.isEmpty()) {
				sql += " and pay_status=? ";
				argsList.add(payStatus);
			}
			if (!sendStatus.isEmpty()) {
				sql += " and send_status=? ";
				argsList.add(sendStatus);
			}
			if (!returnStatus.isEmpty()) {
				sql += " and return_status=? ";
				argsList.add(payStatus);
			}
			if (!showStatus.isEmpty()) {
				sql += " and show_status=? ";
				argsList.add(showStatus);
			}
			sql += " and status = '0' order by create_time desc limit ?, ?";
			argsList.add(recStart);
			argsList.add(recCount);

			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() == 0)
				throw new Exception("100300");

			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();

			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr = dt.getRows().get(i);
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(dr.getString("order_id"));
				coo.setAccount(dr.getString("account"));
				coo.setCreateTime(dr.getString("create_time"));
				coo.setProductId(dr.getString("product_id"));
				coo.setType(dr.getString("type"));
				coo.setName(dr.getString("name"));
				coo.setImage(dr.getString("image"));
				coo.setColor(dr.getString("color"));
				coo.setSize(dr.getString("size"));
				coo.setPrice(dr.getString("price"));
				coo.setCount(dr.getString("count"));
				coo.setAmount(dr.getString("amount"));
				coo.setCurrency(dr.getString("currency"));
				coo.setInnerCoin(dr.getString("inner_coin")); 
				coo.setBonusPoint(dr.getString("bonus_point"));
				coo.setPayTime(dr.getString("pay_time"));
				coo.setPayStatus(dr.getString("pay_status"));
				coo.setSendStatus(dr.getString("send_status"));
				coo.setReturnStatus(dr.getString("return_status"));
				coo.setReceiveId(dr.getString("receive_id"));
				coo.setLogistCom(dr.getString("logist_com"));
				coo.setTrackingNum(dr.getString("tracking_num"));
				coo.setShowStatus(dr.getString("show_status"));
				coo.setSuitId(dr.getString("from_suit"));
				coo.setPointType(dr.getString("POINT_TYPE"));
				coo.setSendTime(dr.getString("SEND_TIME"));
				coo.setReciveName(dr.getString("DELIVERY_NAME"));
				coo.setReciceAddress(dr.getString("DELIVERY_ADDRESS"));
				coo.setRecivePhone(dr.getString("DELIVERY_PHONE"));
				coo.setRemark(dr.getString("REMARK"));
				coo.setWuliugongsi(dr.getString("LOGIST_COM"));
				coo.setWuliuhao(dr.getString("TRACKING_NUM"));
				coo.setPaymentTerms(dr.getString("PAYMENT_TERMS"));
				cooList.add(coo);
			}
			coro.setCooList(cooList);
		} catch (Exception e) {
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
	
	public   String  updateQaTest (String age,String industry,String account){
 		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = "";
			ArrayList<Object> argsList = new ArrayList<Object>();

			sql = " UPDATE heso_qa_result SET AGE = ? ,INDUSTRY = ? WHERE ACCOUNT = ?";
			argsList.add(age);
			argsList.add(industry);
			argsList.add(account);
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
				throw new Exception("101123");
			
		} catch (Exception e) {
			// TODO: handle exception
 			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "000000";
	}

	/**
	 * 获取订单明细
	 * 
	 * @param orderId
	 * @return
	 */
	public   ConsumeOrderReturnObject getDetail(String orderId) {
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		String sql = "select * from heso_order_consume_detail where order_id=? ";
		ArrayList<Object> argsList = new ArrayList<Object>();
		argsList.add(orderId);

		try {
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() == 0)
				throw new Exception("100300");

			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr = dt.getRows().get(i);
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(dr.getString("order_id"));
				coo.setProductId(dr.getString("product_id"));
				coo.setName(dr.getString("name"));
				coo.setImage(dr.getString("image"));
				coo.setColor(dr.getString("color"));
				coo.setSize(dr.getString("size"));
				coo.setPrice(dr.getString("price"));
				coo.setCount(dr.getString("count"));
				coo.setAmount(dr.getString("amount"));
				cooList.add(coo);
			}
			coro.setCooList(cooList);
		} catch (Exception e) {
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return coro;
	}
	/**
	 * 确认收货
	 */
	public ConsumeOrderReturnObject confirmReceive (String orderId){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			//查询该订单是否已支付和已发货
			String sql = " select send_status from heso_order_consume where order_Id = ? and pay_status = '1' and send_status = '1' ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderId);
			if(DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn).getRows().size() <= 0)
				throw new Exception("101122");
			sql = " update heso_order_consume set send_status = '3' where order_Id = ? ";
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
				throw new Exception("101123");
			
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){
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
	 * 权益退款
	 */
	public     ConsumeOrderReturnObject refundByQuanyi(String account , String orderId ){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			
			String sql = " select receive_id, amount , order_id ,payment_terms , DISCOUNT_PRICE,rogion_id from heso_order_consume where account = ? and pay_status = '1' and order_id in(  ";
			List<Object> agesData = new ArrayList<Object>();
			agesData.add(account);
			
			
			sql += "? ) ";
			agesData.add(orderId);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			
			if(dt.getRows().size() <= 0)
				throw new Exception("101617");
			
			//判断订单是否存在不同的支付方式或存在不同的收货地址
			
			int reciveId0 =  dt.getRows().get(0).getInt("rogion_id");
 			//int payment0 = dt.getRows().get(0).getInt("payment_terms");
			BigDecimal refundMoney = dt.getRows().get(0).getBigDecimal("DISCOUNT_PRICE");
			
			sql = "SELECT * FROM heso_currency WHERE account = ? ";
			agesData.clear();
			agesData.add(account);
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			BigDecimal discount = dtt.getRows().get(0).getBigDecimal("DISCOUNT");
			//余额
			BigDecimal balance = dtt.getRows().get(0).getBigDecimal("BALANCE");
			//退款金额
			BigDecimal totalRefund = dtt.getRows().get(0).getBigDecimal("TOTAL_REFUND");
		 
			
			String teacher = "";
			ArrayList<ConsumeOrderObject>list = new ArrayList<ConsumeOrderObject>();
			
			Double total  = 0.0; 
			StringBuffer orderIdStr = new StringBuffer();
			
			
		 
			List<Object> argsList = new ArrayList<Object>();
		    
 			String desc = "";
		 
				
			sql = "INSERT INTO heso_currency_detail (SEQ_ID,ACCOUNT,TRANS_TIME,TRANS_TYPE,TRANS_AMOUNT,TRANS_BEFORE_BALANCE,TRANS_AFTER_BALANCE,ORDER_ID) " +
					"VALUES (?,?,?,?,?,?,?,?)";
			String currencyDetail = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.clear();
			argsList.add(currencyDetail);
			argsList.add(account);
			argsList.add(new Date());
			argsList.add("4");
			argsList.add(refundMoney);
			argsList.add(balance);
			argsList.add(balance.add(refundMoney));
			argsList.add(orderId);
			int row3 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			
			balance = balance.add(refundMoney);
			BigDecimal reDecimal = totalRefund.add(refundMoney);
			sql = "update heso_currency  SET balance = ?,TOTAL_REFUND = ? WHERE ACCOUNT = ? ";
			argsList.clear();
			argsList.add(balance);
			argsList.add(reDecimal);
			argsList.add(account);
			int row1 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			
			
			desc = "交易成功";
			 
			
		 
			//更新支付状态，支付时间
			StringBuffer buSql = new StringBuffer(" update heso_order_consume set pay_status = '2' , pay_time = SYSDATE(), status = '0' where  order_id in ( ? ");
			argsList.clear();
//				list.add(payTime);
			argsList.add(orderId);
			 
			buSql.append(" ) ");
			if(	DatabaseMgr.getInstance().execNonSqlTrans(buSql.toString(), argsList, conn)<= 0){
				throw new Exception("101268");
			}
			
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
		return coro;
	}
	/**
	 * 权益充值
	 * @param account
	 * @param orderId
	 * @return
	 */
	public   ConsumeOrderReturnObject addMoney(String account , String money ){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			
			String sql = "  ";
			List<Object> agesData = new ArrayList<Object>();
		 
		 
			
			sql = "SELECT * FROM heso_currency WHERE account = ? ";
			agesData.clear();
			agesData.add(account);
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			BigDecimal discount = dtt.getRows().get(0).getBigDecimal("DISCOUNT");
			//余额
			BigDecimal balance = dtt.getRows().get(0).getBigDecimal("BALANCE");
			//退款金额
			BigDecimal totalchongzhi = dtt.getRows().get(0).getBigDecimal("TOTAL_RECHARGE");
		 
			
			String teacher = "";
			ArrayList<ConsumeOrderObject>list = new ArrayList<ConsumeOrderObject>();
			
			Double total  = 0.0; 
			StringBuffer orderIdStr = new StringBuffer();
			
			
		 
			List<Object> argsList = new ArrayList<Object>();
		    
 			String desc = "";
 			BigDecimal chongzhi = new BigDecimal(money);
				
			sql = "INSERT INTO heso_currency_detail (SEQ_ID,ACCOUNT,TRANS_TIME,TRANS_TYPE,TRANS_AMOUNT,TRANS_BEFORE_BALANCE,TRANS_AFTER_BALANCE,ORDER_ID) " +
					"VALUES (?,?,?,?,?,?,?,?)";
			String currencyDetail = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.clear();
			argsList.add(currencyDetail);
			argsList.add(account);
			argsList.add(new Date());
			argsList.add("1");
			argsList.add(money);
			argsList.add(balance);
			argsList.add(balance.add(chongzhi));
			argsList.add("");
			int row3 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			
			balance = balance.add(chongzhi);
			BigDecimal reDecimal = totalchongzhi.add(chongzhi);
			sql = "update heso_currency  SET balance = ?,TOTAL_RECHARGE = ? WHERE ACCOUNT = ? ";
			argsList.clear();
			argsList.add(balance);
			argsList.add(reDecimal);
			argsList.add(account);
			int row1 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
			
			
			desc = "交易成功";
			  
			
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
		return coro;
	}
	
	/**
	 * 订单使用权益支付支付
	 */
	public     ConsumeOrderReturnObject payOrderByQuanyi(String account , List <String> orderIds ){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			
			String sql = " select receive_id,DISCOUNT_PRICE,COUNT, amount , order_id ,payment_terms , rogion_id from heso_order_consume where account = ? and pay_status = '0' and order_id in(  ";
			List<Object> agesData = new ArrayList<Object>();
			agesData.add(account);
			
			for(int i =1 ; i<orderIds.size();i++){
				sql += " ? , ";
				agesData.add(orderIds.get(i));
			}
			sql += "? ) ";
			agesData.add(orderIds.get(0));
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			
			if(dt.getRows().size() <= 0)
				throw new Exception("101617");
			
			//判断订单是否存在不同的支付方式或存在不同的收货地址
			
			int reciveId0 =  dt.getRows().get(0).getInt("rogion_id");
 			//int payment0 = dt.getRows().get(0).getInt("payment_terms");
			
			for(int i = 1 ;i <dt.getRows().size();i++){
				int reciveIdi = dt.getRows().get(i).getInt("rogion_id");
				int paymenti = dt.getRows().get(i).getInt("payment_terms");
				/*if(reciveId0 != reciveIdi || payment0 != paymenti){
						throw new Exception("101910");
				}*/
				if(reciveId0 != reciveIdi  ){
					throw new Exception("101910");
			}
			}
			sql = "SELECT * FROM heso_currency WHERE account = ? ";
			agesData.clear();
			agesData.add(account);
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			//BigDecimal discount = dtt.getRows().get(0).getBigDecimal("DISCOUNT");
			//余额
			BigDecimal balance = dtt.getRows().get(0).getBigDecimal("BALANCE");
			//总消费金额
			BigDecimal totalCounsume = dtt.getRows().get(0).getBigDecimal("TOTAL_CONSUME");
		 
			
			String teacher = "";
			ArrayList<ConsumeOrderObject>list = new ArrayList<ConsumeOrderObject>();
			
			Double total  = 0.0; 
			StringBuffer orderIdStr = new StringBuffer();
			for(int i =0;i<dt.getRows().size();i++){ 
			    String orderId = orderIds.get(i);
			   
			    orderIdStr.append(orderId+";");
				ConsumeOrderObject coo = new ConsumeOrderObject();
				BigDecimal amount = new BigDecimal(dt.getRows().get(i).getString("amount"));
				BigDecimal discountPrice = dt.getRows().get(i).getBigDecimal("DISCOUNT_PRICE");
				int count = dt.getRows().get(i).getInt("COUNT");
				 
				BigDecimal discountAmount = discountPrice.multiply(new BigDecimal(count));
				if(balance.compareTo(discountAmount)<0){
					coro.setCode("100000");
					String desc = "余额不足";
					return coro;
				}
				sql = "UPDATE heso_order_consume SET AMOUNT = ? ,PAYMENT_TERMS = ? WHERE order_id = ?";
				agesData.clear();
				agesData.add(discountAmount);
				agesData.add("3");
				agesData.add(orderId);
				int row = DatabaseMgr.getInstance().execNonSqlTrans(sql, agesData, conn) ;
				total = total + discountAmount.doubleValue() ;
				coo.setAmount(discountAmount+"");
				coo.setOrderId(orderId);
				coo.setPaymentTerms(dt.getRows().get(i).getString("payment_terms"));
				list.add(coo);
			} 
			
			//创建支付订单
			 sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money , create_time) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderIdStr.toString());
			argsList.add("3");
			String waiOrder = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(total);
			 if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
				 throw new Exception("100151");
			 }
			 
				//查询收货地址
				sql ="select REGION_ID from heso_account_recvinfo where id = ? ";
				agesData.clear();
				agesData.add(dt.getRows().get(0).getString("RECEIVE_ID"));
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
				coro = new ConsumeOrder().logisticsPay(dt1.getRows().get(0).getString("REGION_ID"),total,waiOrder);
				 //查询优惠价
				 //ConsumeOrderReturnObject coro1 = new ConsumeOrder().firstPay(String.valueOf(total), account ,waiOrder);//取消首单优惠 16/11/09
				 //查询优惠券
				 //ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(coro1.getReccount(), account, coro1.getDiscount(), orderIdStr.toString(), "", "2",waiOrder);//这里要传入原单号，不是外单号
			//ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(String.valueOf(total), account, "0", orderIdStr.toString(), "", "2",waiOrder);
			total = (new BigDecimal(total).add(new BigDecimal(coro.getReccount()))).doubleValue();
			
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));
			list.add(coo1);
			String desc = "";
			if(balance.compareTo(new BigDecimal(total))>=0){
				
				sql = "INSERT INTO heso_currency_detail (SEQ_ID,ACCOUNT,TRANS_TIME,TRANS_TYPE,TRANS_AMOUNT,TRANS_BEFORE_BALANCE,TRANS_AFTER_BALANCE,ORDER_ID) " +
						"VALUES (?,?,?,?,?,?,?,?)";
				String currencyDetail = DatabaseMgr.getInstance().getSequence("seq_order", "16");
				argsList.clear();
				argsList.add(currencyDetail);
				argsList.add(account);
				argsList.add(new Date());
				argsList.add("3");
				argsList.add(new BigDecimal(total));
				argsList.add(balance);
				argsList.add(balance.subtract(new BigDecimal(total)));
				argsList.add(waiOrder);
				int row3 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
				
				balance = balance.subtract(new BigDecimal(total));
				totalCounsume =totalCounsume.add(new BigDecimal(total));
				sql = "update heso_currency  SET balance = ?,total_consume = ? WHERE ACCOUNT = ? ";
				argsList.clear();
				argsList.add(balance);
				argsList.add(totalCounsume);
				argsList.add(account);
				int row1 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
				
				
				desc = "交易成功";
				//算入衣配卡派单
				
				sql = "SELECT * FROM heso_send_order WHERE ACCOUNT = '" +
						account +
						"' AND STATUS = '1' AND service_id in (52,59,65,71)";
				argsList.clear();
 				DataTable ddd = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				int ros = ddd.getRows().size();
				if(ddd.getRows().size()<=0){
 					desc = "没有派单表";
				}else {
					String paidanId = ddd.getRows().get(0).getString("ID");
					String paidanorderIds = ddd.getRows().get(0).getString("ORDERIDS");
					BigDecimal paidanAmount = ddd.getRows().get(0).getBigDecimal("AMOUNT");
					String serviceId = ddd.getRows().get(0).getString("service_id");
					String paidanstatus = ddd.getRows().get(0).getString("STATUS");
					sql = "SELECT jiesuanbiaozhun FROM heso_member_product WHERE ID  =  ?";
					argsList.clear();
					argsList.add(serviceId);
					DataTable dddd = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					BigDecimal jiesuanbiaozhun = dddd.getRows().get(0).getBigDecimal("jiesuanbiaozhun");
					paidanAmount = paidanAmount.add(new BigDecimal(total));
					if(paidanAmount.compareTo(jiesuanbiaozhun)>=0){
						paidanstatus = "4";
					}else {
						paidanstatus = "1";
					} 
					sql = "UPDATE heso_send_order SET ORDERIDS = ? ,amount = ? ,STATUS = ?WHERE ID = ?";
					argsList.clear();
					paidanorderIds = paidanorderIds+";"+orderIdStr;
					argsList.add(paidanorderIds);
					argsList.add(paidanAmount);
					argsList.add(paidanstatus);
					argsList.add(paidanId);
					int row2 = DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
					
				}
				
				
				sql = " select order_pay from heso_order_pay where wai_order  = ?  and is_pay=0";
				argsList.clear();
				argsList.add(waiOrder);
				DataTable dt3 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dt3.getRows().size() <= 0 )
					throw new Exception("101268");
				String [] orderIdss = dt3.getRows().get(0).getString("order_pay").split(";");
				/*for(int i = 0 ;  i  < orderIds.length; i++){
				
					List<WardrobeDTO> wardrobeDTOs = getWardrobeDTOsByOrderId(orderIds[i]);
					new WardrobeService().addMyWardrobe(wardrobeDTOs);
				}*/
				//更新支付状态，支付时间
				StringBuffer buSql = new StringBuffer(" update heso_order_consume set pay_status = '1' , pay_time = SYSDATE(), status = '0' where  order_id in ( ? ");
				argsList.clear();
//				list.add(payTime);
				argsList.add(orderIdss[0]);
				for(int i = 1 ;  i  < orderIdss.length; i++){
					buSql.append(", ? ");
					argsList.add(orderIdss[i]);
				}
				buSql.append(" ) ");
				if(	DatabaseMgr.getInstance().execNonSqlTrans(buSql.toString(), argsList, conn)<= 0){
					throw new Exception("101268");
				}
				
				sql = "update heso_order_pay set is_pay ='1' , pay_time = SYSDATE() where  wai_order = ? ";
				argsList.clear();
//				list.add(payTime);
				argsList.add(waiOrder);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
				 
				/*sql = "update heso_discount set status = '1' where  order_id = ? ";
				argsList.clear();
				argsList.add(waiOrder);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);*/
				for(String orderidString : orderIds){
					List<WardrobeDTO> wardrobeDTOs = getWardrobeDTOsByOrderId(orderidString);
					new WardrobeService().addMyWardrobe(wardrobeDTOs);
				}
			}else {
				coro.setCode("100000");
				desc = "余额不足";
			}
			coro.setDesc(desc);
			coro.setCooList(list);
			coro.setOrderId(waiOrder);
//			updateLoadAdd(String.valueOf(reciveId0), account, or);
			
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
		return coro;
	}
	
	
	/**
	 * 修改已支付
	 * @param account
	 * @param orderIds
	 * @return
	 */
	public     ConsumeOrderReturnObject payOrderIsOK(String account , String orderIds ){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {/*
			conn = DatabaseMgr.getInstance().getConnection();
			
			String sql = "  ";
			List<Object> agesData = new ArrayList<Object>();
			 
			
		 
			
			//判断订单是否存在不同的支付方式或存在不同的收货地址
			
			 
			 
			sql = "SELECT * FROM heso_currency WHERE account = ? ";
			agesData.clear();
			agesData.add(account);
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			//BigDecimal discount = dtt.getRows().get(0).getBigDecimal("DISCOUNT");
			//余额
			BigDecimal balance = dtt.getRows().get(0).getBigDecimal("BALANCE");
			//总消费金额
			BigDecimal totalCounsume = dtt.getRows().get(0).getBigDecimal("TOTAL_CONSUME");
		 
			
			String teacher = "";
			ArrayList<ConsumeOrderObject>list = new ArrayList<ConsumeOrderObject>();
			
			Double total  = 0.0; 
			StringBuffer orderIdStr = new StringBuffer();
			for(int i =0;i<dt.getRows().size();i++){ 
			    String orderId = orderIds.get(i);
			   
			    orderIdStr.append(orderId+";");
				ConsumeOrderObject coo = new ConsumeOrderObject();
				BigDecimal amount = new BigDecimal(dt.getRows().get(i).getString("amount"));
				BigDecimal discountPrice = dt.getRows().get(i).getBigDecimal("DISCOUNT_PRICE");
				int count = dt.getRows().get(i).getInt("COUNT");
				 
				BigDecimal discountAmount = discountPrice.multiply(new BigDecimal(count));
				if(balance.compareTo(discountAmount)<0){
					coro.setCode("100000");
					String desc = "余额不足";
					return coro;
				}
				sql = "UPDATE heso_order_consume SET AMOUNT = ? ,PAYMENT_TERMS = ? WHERE order_id = ?";
				agesData.clear();
				agesData.add(discountAmount);
				agesData.add("3");
				agesData.add(orderId);
				int row = DatabaseMgr.getInstance().execNonSqlTrans(sql, agesData, conn) ;
				total = total + discountAmount.doubleValue() ;
				coo.setAmount(discountAmount+"");
				coo.setOrderId(orderId);
				coo.setPaymentTerms(dt.getRows().get(i).getString("payment_terms"));
				list.add(coo);
			} 
			
			//创建支付订单
			 sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money , create_time) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderIdStr.toString());
			argsList.add("3");
			String waiOrder = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(total);
			 if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
				 throw new Exception("100151");
			 }
			 
				//查询收货地址
				sql ="select REGION_ID from heso_account_recvinfo where id = ? ";
				agesData.clear();
				agesData.add(dt.getRows().get(0).getString("RECEIVE_ID"));
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
				coro = new ConsumeOrder().logisticsPay(dt1.getRows().get(0).getString("REGION_ID"),total,waiOrder);
				 //查询优惠价
				 //ConsumeOrderReturnObject coro1 = new ConsumeOrder().firstPay(String.valueOf(total), account ,waiOrder);//取消首单优惠 16/11/09
				 //查询优惠券
				 //ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(coro1.getReccount(), account, coro1.getDiscount(), orderIdStr.toString(), "", "2",waiOrder);//这里要传入原单号，不是外单号
			//ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(String.valueOf(total), account, "0", orderIdStr.toString(), "", "2",waiOrder);
			total = (new BigDecimal(total).add(new BigDecimal(coro.getReccount()))).doubleValue();
			
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));
			list.add(coo1);
			String desc = "";
			if(balance.compareTo(new BigDecimal(total))>=0){
				
				 
				StringBuffer buSql = new StringBuffer(" update heso_order_consume set pay_status = '1' , pay_time = SYSDATE(), status = '0' where  order_id =? ");
				argsList.clear();
//				list.add(payTime);
				argsList.add(orderIdss[0]);
				
				if(	DatabaseMgr.getInstance().execNonSqlTrans(buSql.toString(), argsList, conn)<= 0){
					throw new Exception("101268");
				}
				
				sql = "update heso_order_pay set is_pay ='1' , pay_time = SYSDATE() where  wai_order = ? ";
				argsList.clear();
//				list.add(payTime);
				argsList.add(waiOrder);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
				 
				sql = "update heso_discount set status = '1' where  order_id = ? ";
				argsList.clear();
				argsList.add(waiOrder);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);
				for(String orderidString : orderIds){
					List<WardrobeDTO> wardrobeDTOs = getWardrobeDTOsByOrderId(orderidString);
					new WardrobeService().addMyWardrobe(wardrobeDTOs);
				}
			}else {
				coro.setCode("100000");
				desc = "余额不足";
			}
			coro.setDesc(desc);
			coro.setCooList(list);
			coro.setOrderId(waiOrder);
//			updateLoadAdd(String.valueOf(reciveId0), account, or);
			
		*/} catch (Exception e) {
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
		return coro;
	}
	/**
	 * 订单支付
	 */
	public ConsumeOrderReturnObject payOrder(String account , List <String> orderIds, String payType){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			
			String sql = " select receive_id, amount , order_id ,payment_terms , rogion_id from heso_order_consume where account = ? and pay_status = '0' and order_id in(  ";
			List<Object> agesData = new ArrayList<Object>();
			agesData.add(account);
			
			for(int i =1 ; i<orderIds.size();i++){
				sql += " ? , ";
				agesData.add(orderIds.get(i));
			}
			sql += "? ) ";
			agesData.add(orderIds.get(0));
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
			
			if(dt.getRows().size() <= 0)
				throw new Exception("101617");
			
			//判断订单是否存在不同的支付方式或存在不同的收货地址
			
			int reciveId0 =  dt.getRows().get(0).getInt("rogion_id");
			
			int payment0 = dt.getRows().get(0).getInt("payment_terms");
			
			for(int i = 1 ;i <dt.getRows().size();i++){
				int reciveIdi = dt.getRows().get(i).getInt("rogion_id");
				int paymenti = dt.getRows().get(i).getInt("payment_terms");
				if(reciveId0 != reciveIdi || payment0 != paymenti){
						throw new Exception("101910");
				}
			}
			
			ArrayList<ConsumeOrderObject>list = new ArrayList<ConsumeOrderObject>();
			Double total  = 0.0; 
			StringBuffer orderIdStr = new StringBuffer();
			for(int i =0;i<dt.getRows().size();i++){ 
					    String orderId = orderIds.get(i);
					   
					    orderIdStr.append(orderId+";");
						ConsumeOrderObject coo = new ConsumeOrderObject();
						total = total + new BigDecimal(dt.getRows().get(i).getString("amount")).doubleValue() ;
						coo.setAmount(dt.getRows().get(i).getString("amount"));
						coo.setOrderId(orderId);
						coo.setPaymentTerms(dt.getRows().get(i).getString("payment_terms"));
						list.add(coo);
					}
			
			//创建支付订单
			 sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money , create_time) values (? , ? , ? , ? , SYSDATE()) ";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(orderIdStr.toString());
			argsList.add(payType);
			String waiOrder = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(total);
			 if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0 ){
				 throw new Exception("100151");
			 }
			 
				//查询收货地址
				sql ="select REGION_ID from heso_account_recvinfo where id = ? ";
				agesData.clear();
				agesData.add(dt.getRows().get(0).getString("RECEIVE_ID"));
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, agesData, conn);
				 coro = new ConsumeOrder().logisticsPay(dt1.getRows().get(0).getString("REGION_ID"),total,waiOrder);
				 //查询优惠价
				 //ConsumeOrderReturnObject coro1 = new ConsumeOrder().firstPay(String.valueOf(total), account ,waiOrder);//取消首单优惠 16/11/09
				 //查询优惠券
				 //ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(coro1.getReccount(), account, coro1.getDiscount(), orderIdStr.toString(), "", "2",waiOrder);//这里要传入原单号，不是外单号
				 ConsumeOrderReturnObject cop = new ConsumeOrder().couponDiscount(String.valueOf(total), account, "0", orderIdStr.toString(), "", "2",waiOrder);
				total = (new BigDecimal(cop.getReccount()).add(new BigDecimal(coro.getReccount()))).doubleValue();
		
			ConsumeOrderObject coo1 = new ConsumeOrderObject();
			coo1.setAmount(String.valueOf(total));
			list.add(coo1);
			coro.setCooList(list);
			coro.setOrderId(waiOrder);
//			updateLoadAdd(String.valueOf(reciveId0), account, or);
			
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
		return coro;
	}
	
	
	public   ConsumeOrderReturnObject bookService(String accountId,String productId,String count ,String payType,String designerId,String reciveId,String remarks ,String desigmerServices){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		ArrayList<ConsumeOrderObject> cList = new ArrayList<ConsumeOrderObject>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			//根据地址ID查找详细地址
			String sql = "SELECT * FROM heso_account_recvinfo where ID = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(reciveId);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);	
			if (dt.getRows().size() == 0){
				logger.error(">>>>>>>>预约服务：查询详细地址失败");
				throw new Exception("001093");
			}
			String accountName = dt.getRows().get(0).getString("NAME");
			String address = dt.getRows().get(0).getString("ADDRESS");
			String mobile = dt.getRows().get(0).getString("MOBILE");
			
			String region_id = dt.getRows().get(0).getString("REGION_ID");
			//查找服务与对应设计师价格
			sql = "SELECT hmp.name ,hmp.MAX_NUM,hsd.price ,hmp.flag,hmp.image FROM heso_member_product as  hmp,heso_service_designer as hsd where hmp.ID = ? AND hmp.ID = hsd.SERVICEID AND hsd.DESIGNERID = ?";
			argsList.clear();
			argsList.add(productId);
			argsList.add(designerId);
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			if(dt1.getRows().size() == 0){
				logger.error(">>>>>>>>>预约服务：查询服务对应设计师价格失败");
				throw new Exception("100100");
			}
			String flag = dt1.getRows().get(0).getString("flag");
			String bookNum = "";
		 
			String serviceName = dt1.getRows().get(0).getString("NAME");
			String price = dt1.getRows().get(0).getString("price");
			String image = dt1.getRows().get(0).getString("image");
			String max_num =dt1.getRows().get(0).getString("MAX_NUM");
			//加入订单列表
			String seqId = dbm.getSequence("seq_order", "16");
			String porderId = seqId;
			String pstatus = "1";
			String paccount = "";
			if("1".equals(flag)){
				sql = "SELECT BOOK_NUM ,ORDERID ,ACCOUNT FROM heso_designer_place where ID= ?";
				argsList.clear();
				argsList.add(desigmerServices);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				bookNum = dt2.getRows().get(0).getString("BOOK_NUM");
				String orderId = dt2.getRows().get(0).getString("ORDERID");
				String account = dt2.getRows().get(0).getString("ACCOUNT");	
				pstatus = "0";
				if(Integer.parseInt(max_num)==Integer.parseInt(bookNum)+Integer.parseInt(count)){
					pstatus = "1";
				}else if(Integer.parseInt(max_num)<Integer.parseInt(bookNum)+Integer.parseInt(count)){
					coro.setCode("001093");
					throw new Exception("001093");
				}
				if(orderId!=null){
					porderId = orderId+","+porderId;
					paccount = account +","+accountId ;
				}
			}
			sql = "insert into heso_order_consume(order_id, account, product_Id,type,name," +
					"price,count,amount, receive_id , " +
					"payment_terms ,DELIVERY_ADDRESS,DELIVERY_NAME,DELIVERY_PHONE," +
					"seller,remark,image) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(accountId);
			argsList.add(productId);
			argsList.add("0"); 
			argsList.add(serviceName);
			argsList.add(price);
			double priceDouble = Double.valueOf(price);
			double amount = priceDouble * Integer.valueOf(count);
			argsList.add(count);
			argsList.add(amount);
			argsList.add(reciveId);
			argsList.add(payType);
			argsList.add(address);
			argsList.add(accountName);
			argsList.add(mobile);
			argsList.add(designerId);
			argsList.add(remarks);
			argsList.add(image);
			int rows = dbm.execNonSql(sql, argsList);
			if (rows <= 0){
				logger.error(">>>>>>>>>预约服务：新增订单失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}
			int num = Integer.valueOf(count);
 			//修改设计师日程表
			sql = "UPDATE heso_designer_place as hdp SET  orderId = ? ,STATUS= ? ,ACCOUNT= ?,SERVICEID=? ,BOOK_NUM = BOOK_NUM +" +
					count +
					" WHERE  ID in ("+
 			desigmerServices +")";
			argsList.clear();
			argsList.add(porderId);
			argsList.add(pstatus);
			argsList.add(paccount);
			argsList.add(productId);
			int dt2 = dbm.execNonSqlTrans(sql, argsList, conn);
			if(dt2 <=0){
				logger.error(">>>>>>>>预约服务：修改设计师日程表失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}  
			
			//创建支付订单表
			sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money ,CREATE_TIME) values (? , ? , ? , ? , SYSDATE()) ";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(payType);
			String waiOrder = dbm.getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(amount);
			//
			int y = dbm.execNonSql(sql, argsList) ;
			 if( y <= 0 ){
				 coro.setCode("001093");
				 throw new Exception("001093");
			 }
			 
			 conn.commit();
			 ConsumeOrderObject coo = new ConsumeOrderObject();
			 coo.setOrderId(waiOrder);
			 coo.setAmount(amount+"");
			 coo.setPaymentTerms(payType);
			cList.add(coo);
			coro.setCooList(cList);
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
		return coro;

	}
	public   ConsumeOrderReturnObject bookService3(String accountId,String productId,String count ,String payType,String designerId,String reciveId,String remarks ,String desigmerServices){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		ArrayList<ConsumeOrderObject> cList = new ArrayList<ConsumeOrderObject>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			//根据地址ID查找详细地址
			String sql = "SELECT * FROM heso_account_recvinfo where ID = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(reciveId);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);	
			if (dt.getRows().size() == 0){
				logger.error(">>>>>>>>预约服务：查询详细地址失败");
				throw new Exception("001093");
			}
			String accountName = dt.getRows().get(0).getString("NAME");
			String address = dt.getRows().get(0).getString("ADDRESS");
			String mobile = dt.getRows().get(0).getString("MOBILE");
			
			String region_id = dt.getRows().get(0).getString("REGION_ID");
			//查找服务与对应设计师价格
			sql = "SELECT hmp.name ,hmp.MAX_NUM,hmp.price  ,hmp.flag,hmp.image FROM heso_member_product as  hmp  where hmp.ID = ?  ";
			argsList.clear();
			argsList.add(productId);
		 
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			if(dt1.getRows().size() == 0){
 				throw new Exception("100100");
			}
			String flag = dt1.getRows().get(0).getString("flag");
			String bookNum = "";
		 
			String serviceName = dt1.getRows().get(0).getString("NAME");
			String price = dt1.getRows().get(0).getString("price");
			String image = dt1.getRows().get(0).getString("image");
			String max_num =dt1.getRows().get(0).getString("MAX_NUM");
			//加入订单列表
			String seqId = dbm.getSequence("seq_order", "16");
			String porderId = seqId;
			String pstatus = "1";
			String paccount = "";
			if("1".equals(flag)){
				sql = "SELECT BOOK_NUM ,ORDERID ,ACCOUNT FROM heso_designer_place where ID= ?";
				argsList.clear();
				argsList.add(desigmerServices);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				bookNum = dt2.getRows().get(0).getString("BOOK_NUM");
				String orderId = dt2.getRows().get(0).getString("ORDERID");
				String account = dt2.getRows().get(0).getString("ACCOUNT");	
				pstatus = "0";
				if(Integer.parseInt(max_num)==Integer.parseInt(bookNum)+Integer.parseInt(count)){
					pstatus = "1";
				}else if(Integer.parseInt(max_num)<Integer.parseInt(bookNum)+Integer.parseInt(count)){
					coro.setCode("001093");
					throw new Exception("001093");
				}
				if(orderId!=null){
					porderId = orderId+","+porderId;
					paccount = account +","+accountId ;
				}
			}
			sql = "insert into heso_order_consume(order_id, account, product_Id,type,name," +
					"price,count,amount, receive_id , " +
					"payment_terms ,DELIVERY_ADDRESS,DELIVERY_NAME,DELIVERY_PHONE," +
					"seller,remark,image) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(accountId);
			argsList.add(productId);
			argsList.add("0"); 
			argsList.add(serviceName);
			argsList.add(price);
			double priceDouble = Double.valueOf(price);
			double amount = priceDouble * Integer.valueOf(count);
			argsList.add(count);
			argsList.add(amount);
			argsList.add(reciveId);
			argsList.add(payType);
			argsList.add(address);
			argsList.add(accountName);
			argsList.add(mobile);
			argsList.add(designerId);
			argsList.add(remarks);
			argsList.add(image);
			int rows = dbm.execNonSql(sql, argsList);
			if (rows <= 0){
				logger.error(">>>>>>>>>预约服务：新增订单失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}
			int num = Integer.valueOf(count);
 			//修改设计师日程表
		/*	sql = "UPDATE heso_designer_place as hdp SET  orderId = ? ,STATUS= ? ,ACCOUNT= ?,SERVICEID=? ,BOOK_NUM = BOOK_NUM +" +
					count +
					" WHERE  ID in ("+
 			desigmerServices +")";
			argsList.clear();
			argsList.add(porderId);
			argsList.add(pstatus);
			argsList.add(paccount);
			argsList.add(productId);
			int dt2 = dbm.execNonSqlTrans(sql, argsList, conn);
			if(dt2 <=0){
				logger.error(">>>>>>>>预约服务：修改设计师日程表失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}  */
			
			//创建支付订单表
			sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money ,CREATE_TIME) values (? , ? , ? , ? , SYSDATE()) ";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(payType);
			String waiOrder = dbm.getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(amount);
			//
			int y = dbm.execNonSql(sql, argsList) ;
			 if( y <= 0 ){
				 coro.setCode("001093");
				 throw new Exception("001093");
			 }
			 
			 conn.commit();
			 ConsumeOrderObject coo = new ConsumeOrderObject();
			 coo.setOrderId(waiOrder);
			 coo.setAmount(amount+"");
			 coo.setPaymentTerms(payType);
			cList.add(coo);
			coro.setCooList(cList);
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
		return coro;

	}
	/*//查询派单表
	public List<SendOrder> checkSendOrder(String adminId){
		String sql = "SELECT * FROM heso_send_order WHERE TEACHER_ID = ? OR ZHULIA_ID = ? OR ZHULIB_ID = ? ";
		ArrayList<Object> argsList = new ArrayList<Object>();
 
		try {
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() == 0)
				throw new Exception("100300");

			List<SendOrder> sendOrders= new ArrayList<SendOrder>();
			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr = dt.getRows().get(i);
				SendOrder  sendOrder = new SendOrder();
				sendOrder.setId(dr.getString(""));

				sendOrder.setOrderId();
				sendOrder.set
				
			}
 		} catch (Exception e) {
 			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		
		return null;
		
	}*/
	
	//客户评分派单表
	public    String  updateSendOrder(String id,String fenshu,String jianyi) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		int pingfen = Integer.parseInt(fenshu);
		List<SendOrder> sendOrders = new ArrayList<SendOrder>();
		try {
			String  sql = "";
			sql ="UPDATE heso_send_order SET kehupingfen = ? ,kehujianyi = ?,status='2' WHERE ID = ?";
			argsList.clear();
			argsList.add(fenshu);
			argsList.add(jianyi);
			argsList.add(id);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			 
			 if(x>0){
				 code = "000000";
			 }
			 //查询服务派单提成人
				sql = "SELECT SERVICE_NAME,ACCOUNT,ACCOUNT_NAME,ID,TEACHER_ID,ZHULIA_ID,ZHULIB_ID,SERVICE_ID FROM heso_send_order WHERE ID = ?";
				argsList.clear();
				argsList.add(id);
				DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
 				String serviceId = dt1.getRows().get(0).getString("SERVICE_ID");
				String teacher = dt1.getRows().get(0).getString("TEACHER_ID");
				String zhuliA = dt1.getRows().get(0).getString("ZHULIA_ID");
				String zhuliB = dt1.getRows().get(0).getString("ZHULIB_ID");
				String serviceName = dt1.getRows().get(0).getString("SERVICE_NAME");
				String account = dt1.getRows().get(0).getString("ACCOUNT");
				String accountName = dt1.getRows().get(0).getString("ACCOUNT_NAME");
				//查出服务提成标准
				sql = "SELECT IN_TEACHERA,IN_TEACHERB,IN_ZHULI,OUT_TEACHERB,OUT_TEACHERA FROM heso_member_product where ID= ?";
				argsList.clear();
				argsList.add(serviceId);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal IN_TEACHERA = dt2.getRows().get(0).getBigDecimal("IN_TEACHERA");
				BigDecimal IN_TEACHERB = dt2.getRows().get(0).getBigDecimal("IN_TEACHERB");
				BigDecimal IN_ZHULI = dt2.getRows().get(0).getBigDecimal("IN_ZHULI");
				BigDecimal OUT_TEACHERB = dt2.getRows().get(0).getBigDecimal("OUT_TEACHERB");
				BigDecimal OUT_TEACHERA = dt2.getRows().get(0).getBigDecimal("OUT_TEACHERA");
 				//计算提成
				//技术老师
				if(!teacher.isEmpty()&&teacher!=null){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(teacher);
					DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt3.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0);
					if("WA".equals(type)&&OUT_TEACHERA.compareTo(new BigDecimal(-1))!=0){
						bili = OUT_TEACHERA;
					}else if("WB".equals(type)&&OUT_TEACHERB.compareTo(new BigDecimal(-1))!=0) {
						bili = OUT_TEACHERB;
					}else if("NA".equals(type)&&IN_TEACHERA.compareTo(new BigDecimal(-1))!=0){
						bili = IN_TEACHERA;
					}else if("NB".equals(type)&&IN_TEACHERB.compareTo(new BigDecimal(-1))!=0){
						bili = IN_TEACHERB;
					}
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(teacher);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(bili);
						int tid = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(teacher);
						argsList.add("技术老师");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(sumDecimal);
						 dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(teacher);
						argsList.add("技术老师");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(bili);
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				//助理A
				if(!zhuliA.isEmpty()&&zhuliA!=null){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(zhuliA);
					DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt3.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0);
					if("ZL".equals(type)&&IN_ZHULI.compareTo(new BigDecimal(-1))!=0){
						bili = IN_ZHULI;
					} 
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(zhuliA);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(bili);
						int tid = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(zhuliA);
						argsList.add("技术助理");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(sumDecimal);
						 dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(zhuliA);
						argsList.add("技术助理");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(bili);
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				//助理B
				 
				if(!zhuliB.isEmpty()&&zhuliB!=null){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(zhuliB);
					DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt3.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0);
					if("ZL".equals(type)){
						bili = IN_ZHULI;
					} 
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(zhuliB);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(bili);
						int tid = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(zhuliB);
						argsList.add("技术助理");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(sumDecimal);
						 dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(serviceName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("2");
						argsList.add(id);
						argsList.add("");
						argsList.add(zhuliB);
						argsList.add("技术助理");
						argsList.add(0);
						argsList.add(0);
						argsList.add(bili);
						argsList.add(0);
						argsList.add(bili);
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	//查询派单表
	public     List<SendOrder>  checkSendOrder(String account) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		
		List<SendOrder> sendOrders = new ArrayList<SendOrder>();
		try {
			String  sql = "SELECT heo.ID,heo.teacher_name,heo.zhulia_name,heo.zhulib_Name,heo.service_time,heo.COMPLETE_TIME,heo.account_name, " +
					"heo.status,heo.account_quanyiid,heo.SERVICE_NAME,heo.genjinren_name ,heo.fuwuzongjie,heo.jinyibu,heo.SERVICE_PLACE,hmp.image " +
					"FROM heso_send_order as heo,heso_member_product as hmp WHERE heo.service_id = hmp.id AND heo.account LIKE  '%" +
					account +
					"%'  ORDER BY service_time DESC";
			argsList.clear();
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0; i<dt1.getRows().size();i++){
				DataRow dr = dt1.getRows().get(i);
				SendOrder sendOrder = new SendOrder();
				sendOrder.setId(dr.getInt("id")+"");
				sendOrder.setTeacher_name(dr.getString("teacher_name")); 
				sendOrder.setZhuliA_name(dr.getString("zhulia_name"));
				sendOrder.setZhuliB_name(dr.getString("zhulib_name"));
 				sendOrder.setService_time(dr.getString("service_time"));
				sendOrder.setAccount_name(dr.getString("account_name"));
				sendOrder.setComplete_time(dr.getString("COMPLETE_TIME"));
				sendOrder.setAccount_quanyiid(dr.getString("account_quanyiid"));
				sendOrder.setService_name(dr.getString("SERVICE_NAME"));
				sendOrder.setGenjinren_name(dr.getString("genjinren_name"));
				sendOrder.setFuwuzongjie(dr.getString("fuwuzongjie"));
				sendOrder.setJinyibu(dr.getString("jinyibu"));
				sendOrder.setSercive_place(dr.getString("SERVICE_PLACE"));
				sendOrder.setStatus(dr.getString("status"));
				sendOrder.setImage(dr.getString("image"));
				sendOrders.add(sendOrder);
				
			}
			/*int y = dbm.execNonSqlTrans(sql, argsList, conn);
			
			sql = "SELECT COUNT  FROM heso_account_quanyi  WHERE ID = ?";
			argsList.clear();
		 
			int count = dt1.getRows().get(0).getInt("count");
			if(count != 0 ){
				count = count - 1 ;
			}
			if(count == 0){
				sql ="UPDATE heso_account_quanyi SET STATUS = '1',count = 0 WHERE ID = ?";
			}else {
				
				sql ="UPDATE heso_account_quanyi SET count = " +
						count +
						" WHERE ID = ?";
			}
			argsList.clear();
			argsList.add(sendOrder.getAccount_quanyiid());*/
			//int x = dbm.execNonSqlTrans(sql, argsList, conn);
			 
			 
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
		
		return sendOrders;
		
	}
	
	//查询派单包含标签订单
	public   List<SendOrder> checkBiaoqianOrder(String teacherId,String paidanId,String type){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		List<SendOrder> seList = new ArrayList<SendOrder>();
		try { 
			
			
 			String  sql = " ";
 			 
 			sql = "SELECT * FROM heso_biaoqian_order WHERE  TEACHER_ID = ? and TYPE = ? ";
 			
			argsList.clear();
			argsList.add(teacherId);
			argsList.add(type);
			if(!"".equals(paidanId)){
				sql = sql + "and BELONG = ?";
				argsList.add(paidanId);				
			}
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0;i<dt1.getRows().size();i++){
				SendOrder sendOrder = new SendOrder();
				DataRow dr = dt1.getRows().get(i);
				sendOrder.setId(dr.getString("ID"));
				sendOrder.setProductId(dr.getString("PRODUCT_ID"));
				sendOrder.setTeacher_id(dr.getString("TEACHER_ID"));
				sendOrder.setStatus(dr.getString("BSTATUS"));
				sendOrder.setBelong(dr.getString("BELONG"));
				seList.add(sendOrder);
			}
			
		 
		 
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
		
		return seList;
		
		
		 
	}
	//标签派单改变状态
	public    String changeBiaoqianOrderStatus(String biaoqianOrderId,String status){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try { 
			
			//添加标签派单表
 			String  sql = " ";
 			 
 			sql = "SELECT * FROM heso_biaoqian_order WHERE ID = ? ";
 			
			argsList.clear();
			argsList.add(biaoqianOrderId);
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			String productId = dt1.getRows().get(0).getString("PRODUCT_ID");
			String teacherId = dt1.getRows().get(0).getString("TEACHER_ID");
			String bStatus = dt1.getRows().get(0).getString("BSTATUS");
			if("2".equals(bStatus)){
				return "000000";
			}
			sql = "update heso_biaoqian_order set BSTATUS = ? WHERE ID = ? ";
			argsList.clear();
			argsList.add(status);
			argsList.add(biaoqianOrderId);
		
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			
 			if("2".equals(status)){
 				
 			
 				sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
				argsList.clear();
				argsList.add(teacherId);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal sumDecimal = BigDecimal.ZERO;
				if(dt.getRows().size()<=0){					
					sumDecimal = new BigDecimal(5);
				
				}else {
					BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
					sumDecimal = sumAmount.add(new BigDecimal(5));
				}
					
 				sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(productId);
				argsList.add("");
				argsList.add("");
				argsList.add("4");
				argsList.add(biaoqianOrderId);
				argsList.add("");
				argsList.add(teacherId);
				argsList.add("技术老师");
				argsList.add(0);
				argsList.add(0);
				argsList.add(5);
				argsList.add(0);
				argsList.add(sumDecimal);
				 dbm.execNonSqlTrans(sql, argsList, conn);
 				
 			}
			
			if(y>0){
				code = "000000";
			}
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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

	//新增衣橱标签审核派单表
	public  static String addYichuBiaoqianPaidan(List<String> productids,String account){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try { 
			//查询用户权益
			String sql = "SELECT ID,COUNT FROM heso_account_quanyi where  (QUANYI = '50' OR QUANYI = '56' OR QUANYI = '62' OR QUANYI = '68') AND STATUS= '0' AND COUNT != 0 AND ACCOUNTS LIKE '%" +
					account +
					"%'";
 			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
 			int count = dt.getRows().get(0).getInt("COUNT");
 			int quanyiId = dt.getRows().get(0).getInt("ID");
 			if(productids.size()>count){
 				code = "000002";
 				return code;
 			}
 			count = count - productids.size();
 			String status = "0";
 			if(count==0){
 				status = "1";
 			}
 			sql = "UPDATE heso_account_quanyi SET COUNT = ? , status= ? WHERE ID = ?";
 			argsList.clear();
 			argsList.add(count);
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
					"ZHULIA_STATUS,ZHULIB_STATUS,ACCOUNT_QUANYIID,GENJINREN_ID,GENJINREN_NAME,STATUS) " +
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
			argsList.add("");
			argsList.add("2");
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
 			
			//循环插入标签派单表
			sql = "INSERT heso_biaoqian_order   (PRODUCT_ID,PRODUCT_NAME,BSTATUS,BELONG,TEACHER_ID,TYPE) VALUES (?,?,?,?,?,?)";

			for(String productid : productids ){
				argsList.clear();
				argsList.add(productid);
				argsList.add("");
				argsList.add("0");
				argsList.add(id);
				argsList.add(teacherId);
				argsList.add("2");
				int x = dbm.execNonSqlTrans(sql, argsList, conn); 
			}
			if(y>0){
				code = "000000";
			}
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	//新增标签派单表
	public   String addBiaoqianPaidan(List<String> productids,String teacherId,String teacherName){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try { 
			
			//添加标签派单表
 			String  sql = "SELECT MAX(ID) as ID FROM heso_send_order ";
 			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
 			
 			int id = dt1.getRows().get(0).getInt("ID");
 			id = id + 2;
			sql = "INSERT INTO heso_send_order (ID,TEACHER_ID,TEACHER_NAME,ZHULIA_ID,ZHULIA_NAME,ZHULIB_ID,ZHULIB_NAME," +
					"SERVICE_TIME,SERVICE_PLACE,SERVICE_ID,SERVICE_NAME,ACCOUNT,ACCOUNT_NAME,TEACHER_STATUS," +
					"ZHULIA_STATUS,ZHULIB_STATUS,ACCOUNT_QUANYIID,GENJINREN_ID,GENJINREN_NAME,STATUS) " +
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
			argsList.add("84");
			argsList.add("标签派单");
			argsList.add("");
			argsList.add("");
			argsList.add("1");
			argsList.add("1");
			argsList.add("0");
			argsList.add("");
			argsList.add("");
			argsList.add("");
			argsList.add("2");
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
 			
			//循环插入标签派单表
			sql = "INSERT heso_biaoqian_order   (PRODUCT_ID,PRODUCT_NAME,BSTATUS,BELONG,TEACHER_ID,TYPE) VALUES (?,?,?,?,?,?)";

			for(String productid : productids ){
				argsList.clear();
				argsList.add(productid);
				argsList.add("");
				argsList.add("0");
				argsList.add(id);
				argsList.add(teacherId);
				argsList.add("1");
				int x = dbm.execNonSqlTrans(sql, argsList, conn); 
			}
			if(y>0){
				code = "000000";
			}
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	//订单详情增加样衣系列
		public String updateOrderDetail(String images,String jiajianma,String yangyi,String orderId,String productId){
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			Connection conn = dbm.getConnection();
			ArrayList<Object> argsList = new ArrayList<Object>();
			//String account = "";
			String code = "000001";
			try { 
				
				//根据手机查出身体数据
				String liangtiStr = "";
				String  sql = "UPDATE heso_order_consume_detail set YANGYI_IMAGES = ?, YANGYIHAO = ? ,JIAJIANMA= ? WHERE ORDER_ID = ? AND PRODUCT_ID = ?";
				argsList.clear();
				argsList.add(images);
				argsList.add(yangyi);
				argsList.add(jiajianma);
				argsList.add(orderId); 
				argsList.add(productId);
				int y = dbm.execNonSqlTrans(sql, argsList, conn);
				 
				sql = "UPDATE heso_order_consume set  CHICUNFENLEI=? WHERE ORDER_ID = ?";
				argsList.clear();
			 
				argsList.add("10053");
			 
				argsList.add(orderId);
				int x = dbm.execNonSqlTrans(sql, argsList, conn);
				if(x>0&&y>0){
					code = "000000";
				}
					
				
			} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
					e.printStackTrace();
				
					// TODO: handle exception
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
	
		
	public List<VideoHistory>  checkReadHistory(String accountID) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		List<VideoHistory> list = new ArrayList<VideoHistory>();
		try { 
			
			String liangtiStr = "";
			String  sql = "";
			sql = "SELECT hvh.id,hvh.time, hvh.class_id,hvh.class_name,hds.videoimage,ha.name as adminName,ha.desc as adminDesc " +
					"FROM heso_admin as ha,heso_video_history as hvh,heso_designer_class as hds " +
					"WHERE hvh.account = ? AND hvh.class_id = hds.id AND ha.admin_id = hds.DESIGNERID ORDER BY hvh.time desc";
			argsList.clear();
			argsList.add(accountID);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0;i<dt.getRows().size();i++){
				VideoHistory vHistory = new VideoHistory();
				vHistory.setAccount(accountID);
				vHistory.setClassId(dt.getRows().get(i).getString("class_id"));
				vHistory.setClassName(dt.getRows().get(i).getString("class_name"));
				vHistory.setId(dt.getRows().get(i).getString("id"));
				vHistory.setTime(dt.getRows().get(i).getString("time"));
				vHistory.setAdminName(dt.getRows().get(i).getString("adminName"));
				vHistory.setAdminDesc(dt.getRows().get(i).getString("adminDesc"));
				vHistory.setVideoImage(dt.getRows().get(i).getString("videoimage"));
				list.add(vHistory);
			}
				
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
		
		return list;
		
	}
	//增加阅读数，添加历史浏览记录
	public    String updateReadHistory(String classId,String account,String className){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try { 
			
			String liangtiStr = "";
			String  sql = "UPDATE heso_designer_class SET READ_COUNT = READ_COUNT + 1 WHERE ID = ?";
			argsList.clear();
			argsList.add(classId);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
			String now = dateFormat.format(new Date()); 
			
			sql = "UPDATE   heso_video_history SET time = ? where account = ? AND class_id = ?";
			argsList.clear();
			argsList.add(now);
			argsList.add(account);
			argsList.add(classId);
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			if(y<1){
				sql = "INSERT INTO heso_video_history (ACCOUNT,CLASS_ID,CLASS_NAME) VALUES (?,?,?)";
				argsList.clear();
				argsList.add(account);
				argsList.add(classId);
				argsList.add(className);
				y = dbm.execNonSqlTrans(sql, argsList, conn);

			}
			 
			if(x>0&&y>0){
				code = "000000";
			}
				
			 
				
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	 * 客户评分服饰订单
	 * @param orderId
	 * @param fenshu
	 * @param jianyi
	 * @return
	 */
	public    String  updateOrder(String orderId,String fenshu,String jianyi ) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		int pingfen = Integer.parseInt(fenshu);
 		try {
			String  sql = "";
			sql ="UPDATE heso_order_consume SET kehupingfen = ? ,kehujianyi = ?,show_status='2' WHERE order_id = ?";
			argsList.clear();
			argsList.add(fenshu);
			argsList.add(jianyi);
			argsList.add(orderId);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			 if(x>0){
				 code = "000000";
			 }
			 //查询服饰订单提成人
				sql = "SELECT KEHUPINGFEN, IS_DAIGOU,PRICE_COST,FANXIUCHENGBEN,PRICE,DELIVERY_NAME,NAME,ACCOUNT,AMOUNT,SIZE, SELLER,QUDAOJINGLI_ID,QUDAO_ID,TEACHER_ID FROM heso_order_consume WHERE order_id = ?";
				argsList.clear();
				argsList.add(orderId);
				DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
				int n = dt1.getRows().size();
				 	
				BigDecimal priceCost = dt1.getRows().get(0).getBigDecimal("PRICE_COST");
				BigDecimal price = dt1.getRows().get(0).getBigDecimal("PRICE");
				BigDecimal fanxiuchengben  = dt1.getRows().get(0).getBigDecimal("FANXIUCHENGBEN");
				BigDecimal cost = price.subtract(priceCost).subtract(fanxiuchengben);
				BigDecimal s = cost.divide(price,2, BigDecimal.ROUND_HALF_EVEN);
				String isDaigou = dt1.getRows().get(0).getString("IS_DAIGOU");
				boolean gg = false;
				if(s.compareTo(new BigDecimal(0.5))>=0&&isDaigou.equals("0")){
					gg = true;
				}
				String seller = dt1.getRows().get(0).getString("SELLER");
				String qudaojingli = dt1.getRows().get(0).getString("QUDAOJINGLI_ID");
				String teacher = dt1.getRows().get(0).getString("TEACHER_ID");
				String qudao = dt1.getRows().get(0).getString("QUDAO_ID");
				String size = dt1.getRows().get(0).getString("SIZE");
				String productName = dt1.getRows().get(0).getString("NAME");
				String accountName = dt1.getRows().get(0).getString("DELIVERY_NAME");
				String account = dt1.getRows().get(0).getString("ACCOUNT");
				BigDecimal amount = dt1.getRows().get(0).getBigDecimal("AMOUNT");
				//四个身份非空计算提成
				//渠道
				if(!qudao.isEmpty()&&qudao!=null){
					//查询提成人身份
				/*	sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(qudao);
					DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt2.getRows().get(0).getString("TYPE");*/
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0);
					if(size.equals("定制")){
						bili = new BigDecimal(0.38);
					}else if(!size.equals("定制")){
						 bili = new BigDecimal(0.21);
					} 
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(qudao);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(amount.multiply(bili));
						int id = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(qudao);
						argsList.add("渠道");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(sumDecimal);
						 dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(seller);
						argsList.add("客户跟进人");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(amount.multiply(bili));
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				//成交人
				if(!seller.isEmpty()&&seller!=null){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(seller);
					DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt2.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0);
					if(size.equals("定制")){
						if("WB".equals(type)){
							bili = new BigDecimal(0.05);
						}else {
							bili = new BigDecimal(0.03);					
						}
					}else if(!size.equals("定制")){
						 bili = new BigDecimal(0.02);
					} 
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(seller);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(amount.multiply(bili));
						int id = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(seller);
						argsList.add("客户跟进人");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(sumDecimal);
						 dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(seller);
						argsList.add("客户跟进人");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(amount.multiply(bili));
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				//服务老师
				if(!teacher.isEmpty()&&teacher!=null&&!teacher.equals(seller)){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(seller);
					DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt2.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0.02);
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					/*if(adminId.equals(seller)&&size.equals("定制")){
						if("WB".equals(type)){
							bili = new BigDecimal(0.05);
						}else {
							bili = new BigDecimal(0.03);					
						}
					}else if(adminId.equals(seller)&&!size.equals("定制")){
						 bili = new BigDecimal(0.02);
					} */
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(teacher);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(amount.multiply(bili));
						int id = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(teacher);
						argsList.add("技术老师");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(sumDecimal);
						dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(teacher);
						argsList.add("技术老师");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(amount.multiply(bili));
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
				}
				//渠道经理
				if(!qudaojingli.isEmpty()&&qudaojingli!=null&&!qudaojingli.equals(seller)){
					//查询提成人身份
					sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
					argsList.clear();
					argsList.add(seller);
					DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
					String type = dt2.getRows().get(0).getString("TYPE");
					//根据提成人身份计算提成
					BigDecimal bili = new BigDecimal(0.01);
					if(pingfen<=5){
						bili = new BigDecimal(0);
					}
					/*if(adminId.equals(seller)&&size.equals("定制")){
						if("WB".equals(type)){
							bili = new BigDecimal(0.05);
						}else {
							bili = new BigDecimal(0.03);					
						}
					}else if(adminId.equals(seller)&&!size.equals("定制")){
						 bili = new BigDecimal(0.02);
					} */
					sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
					argsList.clear();
					argsList.add(qudaojingli);
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					BigDecimal sumDecimal = BigDecimal.ZERO;
					if(dt.getRows().size()>0){
						BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
						sumDecimal = sumAmount.add(amount.multiply(bili));
						int id = dt.getRows().get(0).getInt("ID");
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(qudaojingli);
						argsList.add("渠道经理");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(sumDecimal);
						dbm.execNonSqlTrans(sql, argsList, conn);
						 
					}else {
						sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
								"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(productName);
						argsList.add(account);
						argsList.add(accountName);
						argsList.add("3");
						argsList.add("");
						argsList.add(orderId);
						argsList.add(qudaojingli);
						argsList.add("渠道经理");
						argsList.add(0);
						argsList.add(amount);
						argsList.add(0);
						argsList.add(amount.multiply(bili));
						argsList.add(amount.multiply(bili));
						dbm.execNonSqlTrans(sql, argsList, conn);
					}
					
				}
				 
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	 * 确认开始计算提成
	 * @param orderId
	 * @param fenshu
	 * @param jianyi
	 * @return
	 */
	
	public    String  extractCalculate(String orderId) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		//int pingfen = Integer.parseInt(fenshu);
 		try {
			String  sql = "";
			
			 //查询服饰订单提成人
			sql = "SELECT KEHUPINGFEN, IS_DAIGOU,PRICE_COST,FANXIUCHENGBEN,PRICE,DELIVERY_NAME,NAME,ACCOUNT,AMOUNT,SIZE, SELLER,QUDAOJINGLI_ID,QUDAO_ID,TEACHER_ID FROM heso_order_consume WHERE order_id = ?";
			argsList.clear();
			argsList.add(orderId);
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			int n = dt1.getRows().size();
			String fenshu = dt1.getRows().get(0).getString("KEHUPINGFEN");
			int pingfen = Integer.parseInt(fenshu);
			BigDecimal priceCost = dt1.getRows().get(0).getBigDecimal("PRICE_COST");
			BigDecimal price = dt1.getRows().get(0).getBigDecimal("PRICE");
			BigDecimal fanxiuchengben  = dt1.getRows().get(0).getBigDecimal("FANXIUCHENGBEN");
			BigDecimal cost = price.subtract(priceCost).subtract(fanxiuchengben);
			BigDecimal s = cost.divide(price,2, BigDecimal.ROUND_HALF_EVEN);
			String isDaigou = dt1.getRows().get(0).getString("IS_DAIGOU");
			boolean gg = false;
			if(s.compareTo(new BigDecimal(0.5))>=0&&isDaigou.equals("0")){
				gg = true;
			}
			String seller = dt1.getRows().get(0).getString("SELLER");
			String qudaojingli = dt1.getRows().get(0).getString("QUDAOJINGLI_ID");
			String teacher = dt1.getRows().get(0).getString("TEACHER_ID");
			String qudao = dt1.getRows().get(0).getString("QUDAO_ID");
			String size = dt1.getRows().get(0).getString("SIZE");
			String productName = dt1.getRows().get(0).getString("NAME");
			String accountName = dt1.getRows().get(0).getString("DELIVERY_NAME");
			String account = dt1.getRows().get(0).getString("ACCOUNT");
			BigDecimal amount = dt1.getRows().get(0).getBigDecimal("AMOUNT");
			//四个身份非空计算提成
			//渠道
			if(!qudao.isEmpty()&&qudao!=null){
				//查询提成人身份
			/*	sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
				argsList.clear();
				argsList.add(qudao);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String type = dt2.getRows().get(0).getString("TYPE");*/
				//根据提成人身份计算提成
				BigDecimal bili = new BigDecimal(0);
				if(size.equals("定制")){
					bili = new BigDecimal(0.38);
				}else if(!size.equals("定制")){
					 bili = new BigDecimal(0.21);
				} 
				if(pingfen<=5){
					bili = new BigDecimal(0);
				}
				sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
				argsList.clear();
				argsList.add(qudao);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal sumDecimal = BigDecimal.ZERO;
				if(dt.getRows().size()>0){
					BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
					sumDecimal = sumAmount.add(amount.multiply(bili));
					int id = dt.getRows().get(0).getInt("ID");
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(qudao);
					argsList.add("渠道");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(sumDecimal);
					 dbm.execNonSqlTrans(sql, argsList, conn);
					 
				}else {
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(seller);
					argsList.add("客户跟进人");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(amount.multiply(bili));
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
			}
			//成交人
			if(!seller.isEmpty()&&seller!=null){
				//查询提成人身份
				sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
				argsList.clear();
				argsList.add(seller);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String type = dt2.getRows().get(0).getString("TYPE");
				//根据提成人身份计算提成
				BigDecimal bili = new BigDecimal(0);
				if(size.equals("定制")){
					if("WB".equals(type)){
						bili = new BigDecimal(0.05);
					}else {
						bili = new BigDecimal(0.03);					
					}
				}else if(!size.equals("定制")){
					 bili = new BigDecimal(0.02);
				} 
				if(pingfen<=5){
					bili = new BigDecimal(0);
				}
				sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
				argsList.clear();
				argsList.add(seller);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal sumDecimal = BigDecimal.ZERO;
				if(dt.getRows().size()>0){
					BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
					sumDecimal = sumAmount.add(amount.multiply(bili));
					int id = dt.getRows().get(0).getInt("ID");
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(seller);
					argsList.add("客户跟进人");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(sumDecimal);
					 dbm.execNonSqlTrans(sql, argsList, conn);
					 
				}else {
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(seller);
					argsList.add("客户跟进人");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(amount.multiply(bili));
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
			}
			//服务老师
			if(!teacher.isEmpty()&&teacher!=null&&!teacher.equals(seller)){
				//查询提成人身份
				sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
				argsList.clear();
				argsList.add(seller);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String type = dt2.getRows().get(0).getString("TYPE");
				//根据提成人身份计算提成
				BigDecimal bili = new BigDecimal(0.02);
				if(pingfen<=5){
					bili = new BigDecimal(0);
				}
				/*if(adminId.equals(seller)&&size.equals("定制")){
					if("WB".equals(type)){
						bili = new BigDecimal(0.05);
					}else {
						bili = new BigDecimal(0.03);					
					}
				}else if(adminId.equals(seller)&&!size.equals("定制")){
					 bili = new BigDecimal(0.02);
				} */
				sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
				argsList.clear();
				argsList.add(teacher);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal sumDecimal = BigDecimal.ZERO;
				if(dt.getRows().size()>0){
					BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
					sumDecimal = sumAmount.add(amount.multiply(bili));
					int id = dt.getRows().get(0).getInt("ID");
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(teacher);
					argsList.add("技术老师");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(sumDecimal);
					dbm.execNonSqlTrans(sql, argsList, conn);
					 
				}else {
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(teacher);
					argsList.add("技术老师");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(amount.multiply(bili));
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
			}
			//渠道经理
			if(!qudaojingli.isEmpty()&&qudaojingli!=null&&!qudaojingli.equals(seller)){
				//查询提成人身份
				sql = "SELECT TYPE FROM heso_admin WHERE admin_id = ?";
				argsList.clear();
				argsList.add(seller);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String type = dt2.getRows().get(0).getString("TYPE");
				//根据提成人身份计算提成
				BigDecimal bili = new BigDecimal(0.01);
				if(pingfen<=5){
					bili = new BigDecimal(0);
				}
				/*if(adminId.equals(seller)&&size.equals("定制")){
					if("WB".equals(type)){
						bili = new BigDecimal(0.05);
					}else {
						bili = new BigDecimal(0.03);					
					}
				}else if(adminId.equals(seller)&&!size.equals("定制")){
					 bili = new BigDecimal(0.02);
				} */
				sql = "SELECT ID,SUM_AMOUNT FROM heso_admin_ticheng WHERE admin_id = ?  ORDER BY createtime desc";
				argsList.clear();
				argsList.add(qudaojingli);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				BigDecimal sumDecimal = BigDecimal.ZERO;
				if(dt.getRows().size()>0){
					BigDecimal sumAmount = dt.getRows().get(0).getBigDecimal("SUM_AMOUNT");
					sumDecimal = sumAmount.add(amount.multiply(bili));
					int id = dt.getRows().get(0).getInt("ID");
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(qudaojingli);
					argsList.add("渠道经理");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(sumDecimal);
					dbm.execNonSqlTrans(sql, argsList, conn);
					 
				}else {
					sql ="INSERT heso_admin_ticheng (PRODUCT_NAME,ACCOUNT,ACCOUNT_NAME,TYPE,PAIDAN_ID,ORDER_ID,ADMIN_ID,IDENTITY,XIAOSHOU_TICHENG,ORDER_AMOUNT,FUWU_TICHENG,FUSHI_TICHENG,SUM_AMOUNT) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(productName);
					argsList.add(account);
					argsList.add(accountName);
					argsList.add("3");
					argsList.add("");
					argsList.add(orderId);
					argsList.add(qudaojingli);
					argsList.add("渠道经理");
					argsList.add(0);
					argsList.add(amount);
					argsList.add(0);
					argsList.add(amount.multiply(bili));
					argsList.add(amount.multiply(bili));
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
				
			}
			sql ="UPDATE heso_order_consume SET show_status=4' WHERE order_id = ?";
			argsList.clear();

			argsList.add(orderId);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			 if(x>0){
				 code = "000000";
			 }
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	  * 计算个人提成
	  * @param adminId
	  * @param adminType
	  * @return
	  */
	public String calculaMoney(String adminId,String adminType ){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try { 
			
			//根据手机查出身体数据
			String liangtiStr = "";
			String  sql = "UPDATE heso_order_consume_detail set YANGYI_IMAGES = ?, YANGYIHAO = ? ,JIAJIANMA= ? WHERE ORDER_ID = ? AND PRODUCT_ID = ?";
			argsList.clear();
			 
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			 
			sql = "UPDATE heso_order_consume set  CHICUNFENLEI=? WHERE ORDER_ID = ?";
			argsList.clear();
		 
			argsList.add("10053");
		 
			 
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			if(x>0&&y>0){
				code = "000000";
			}
				
			 
				
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	
	//新增派单表
	public String addSendOrder(SendOrder sendOrder){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try {
			String  sql = "INSERT INTO heso_send_order (TEACHER_ID,TEACHER_NAME,ZHULIA_ID,ZHULIA_NAME,ZHULIB_ID,ZHULIB_NAME," +
					"SERVICE_TIME,SERVICE_PLACE,SERVICE_ID,SERVICE_NAME,ACCOUNT,ACCOUNT_NAME,TEACHER_STATUS," +
					"ZHULIA_STATUS,ZHULIB_STATUS,ACCOUNT_QUANYIID,GENJINREN_ID,GENJINREN_NAME) " +
					"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(sendOrder.getTeacher_id());
			argsList.add(sendOrder.getTeacher_name());
			argsList.add(sendOrder.getZhuliA_id());
			argsList.add(sendOrder.getZhuliA_name());
			argsList.add(sendOrder.getZhuliB_id());
			argsList.add(sendOrder.getZhuliB_name());
			
			argsList.add(sendOrder.getService_time());
			argsList.add(sendOrder.getSercive_place());
			argsList.add(sendOrder.getService_id());
			argsList.add(sendOrder.getService_name());
			argsList.add(sendOrder.getAccount());
			argsList.add(sendOrder.getAccount_name());
			argsList.add(sendOrder.getTeacher_status());
			argsList.add(sendOrder.getZhuliA_status());
			argsList.add(sendOrder.getZhuliB_status());
			argsList.add(sendOrder.getAccount_quanyiid());
			argsList.add(sendOrder.getGenjinren_id());
			argsList.add(sendOrder.getGenjinren_name());
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			
			sql = "SELECT COUNT  FROM heso_account_quanyi  WHERE ID = ?";
			argsList.clear();
			argsList.add(sendOrder.getAccount_quanyiid());
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			int count = dt1.getRows().get(0).getInt("count");
			if(count != 0 ){
				count = count - 1 ;
			}
			if(count == 0){
				sql ="UPDATE heso_account_quanyi SET STATUS = '1',count = 0 WHERE ID = ?";
			}else {
				
				sql ="UPDATE heso_account_quanyi SET count = " +
						count +
						" WHERE ID = ?";
			}
			argsList.clear();
			argsList.add(sendOrder.getAccount_quanyiid());
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			if(sendOrder.getTeacher_id()!=null&&!sendOrder.getTeacher_id().equals("")){
				sql = "SELECT mobile FROM heso_admin WHERE ADMIN_ID = ?";
				argsList.clear();
				argsList.add(sendOrder.getTeacher_id());
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String mobile = dt2.getRows().get(0).getString("mobile");
				new SmsService().sendMessageByaliyunByorderId("", mobile ,SmsService.SMS_TYPE_SENDORDER);		
			}
			if(sendOrder.getZhuliA_id()!=null&&!sendOrder.getZhuliA_id().equals("")){
				sql = "SELECT mobile FROM heso_admin WHERE ADMIN_ID = ?";
				argsList.clear();
				argsList.add(sendOrder.getZhuliA_id());
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String mobile = dt2.getRows().get(0).getString("mobile");
				new SmsService().sendMessageByaliyunByorderId("", mobile ,SmsService.SMS_TYPE_SENDORDER);		
			}
			if(sendOrder.getZhuliB_id()!=null&&!sendOrder.getZhuliB_id().equals("")){
				sql = "SELECT mobile FROM heso_admin WHERE ADMIN_ID = ?";
				argsList.clear();
				argsList.add(sendOrder.getZhuliB_id());
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				String mobile = dt2.getRows().get(0).getString("mobile");
				new SmsService().sendMessageByaliyunByorderId("", mobile ,SmsService.SMS_TYPE_SENDORDER);		
			}
			if(x>0&& y>0){
				code = "000000";
			}
				
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	
	//录入成衣量体
	public String addChengyiliangti(String liangti,String orderID,String pinlei){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try {
			
			//根据手机查出身体数据
			String liangtiStr = "";
			String  sql = "UPDATE heso_order_consume_detail set CHENGYILIANGTI = ? WHERE ORDER_ID = ? AND CLOTH_TYPE = ?";
			argsList.clear();
			argsList.add(liangti);
			argsList.add(orderID);
			argsList.add(pinlei);
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			sql = "UPDATE heso_order_consume set  CHICUNFENLEI=? WHERE ORDER_ID = ?";
			argsList.clear();
		 
			argsList.add("10053");
		 
			argsList.add(orderID);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			if(x>0&&y>0){
				code = "000000";
			}
				
			
			
		} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
				e.printStackTrace();
			
				// TODO: handle exception
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
	
	
	public   ConsumeOrderReturnObject bookService2(String accountId,String productId,String count ,String payType,String designerId,String reciveId,String remarks ,String desigmerServices,String type){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		ArrayList<ConsumeOrderObject> cList = new ArrayList<ConsumeOrderObject>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			conn.setAutoCommit(false);
			//根据地址ID查找详细地址
			String sql = "SELECT * from heso_account WHERE account  = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(accountId);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);	
			if (dt.getRows().size() == 0){
				logger.error(">>>>>>>>预约服务：查询详细地址失败");
				throw new Exception("001093");
			} 
			
			String accountName = dt.getRows().get(0).getString("USER_NAME");
			String address = "";
			String mobile = dt.getRows().get(0).getString("MOBILE");
			
			String region_id = dt.getRows().get(0).getString("REGION_ID");
			//查找服务价格
			sql = "SELECT hmp.ID,hmp.name ,hmp.BAOHAN,hmp.MAX_NUM, hmp.PRICE ,hmp.flag,hmp.image FROM heso_member_product as  hmp WHERE hmp.ID = ?";
			argsList.clear();
			argsList.add(productId);
			 
			DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
			if(dt1.getRows().size() == 0){
				logger.error(">>>>>>>>>预约服务：查询服务对应设计师价格失败");
				throw new Exception("100100");
			}
			String flag = dt1.getRows().get(0).getString("flag");
			String bookNum = "";
		 
			String serviceName = dt1.getRows().get(0).getString("NAME");
			String price = dt1.getRows().get(0).getString("price");
			String image = dt1.getRows().get(0).getString("image");
			String max_num =dt1.getRows().get(0).getString("MAX_NUM");
			//加入订单列表
			String seqId = dbm.getSequence("seq_order", "16");
			String porderId = seqId;
			String pstatus = "1";
			String paccount = "";
			/*if("1".equals(flag)){
				sql = "SELECT BOOK_NUM ,ORDERID ,ACCOUNT FROM heso_designer_place where ID= ?";
				argsList.clear();
				argsList.add(desigmerServices);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				bookNum = dt2.getRows().get(0).getString("BOOK_NUM");
				String orderId = dt2.getRows().get(0).getString("ORDERID");
				String account = dt2.getRows().get(0).getString("ACCOUNT");	
				pstatus = "0";
				if(Integer.parseInt(max_num)==Integer.parseInt(bookNum)+Integer.parseInt(count)){
					pstatus = "1";
				}else if(Integer.parseInt(max_num)<Integer.parseInt(bookNum)+Integer.parseInt(count)){
					coro.setCode("001093");
					throw new Exception("001093");
				}
				if(orderId!=null){
					porderId = orderId+","+porderId;
					paccount = account +","+accountId ;
				}
			}*/
			sql = "insert into heso_order_consume(order_id, account, product_Id,type,name," +
					"price,count,amount, receive_id , " +
					"payment_terms ,DELIVERY_ADDRESS,DELIVERY_NAME,DELIVERY_PHONE," +
					"seller,remark,image) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(accountId);
			argsList.add(productId);
			argsList.add(type); 
			argsList.add(serviceName);
			argsList.add(price);
			double priceDouble = Double.valueOf(price);
			double amount = priceDouble * Integer.valueOf(count);
			argsList.add(count);
			argsList.add(amount);
			argsList.add(reciveId);
			argsList.add(payType);
			argsList.add(address);
			argsList.add(accountName);
			argsList.add(mobile);
			argsList.add(designerId);
			argsList.add(remarks);
			argsList.add(image);
			int rows = dbm.execNonSql(sql, argsList);
			if (rows <= 0){
				logger.error(">>>>>>>>>预约服务：新增订单失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}
			int num = Integer.valueOf(count);
 			//修改设计师日程表
		/*	sql = "UPDATE heso_designer_place as hdp SET  orderId = ? ,STATUS= ? ,ACCOUNT= ?,SERVICEID=? ,BOOK_NUM = BOOK_NUM +" +
					count +
					" WHERE  ID in ("+
 			desigmerServices +")";
			argsList.clear();
			argsList.add(porderId);
			argsList.add(pstatus);
			argsList.add(paccount);
			argsList.add(productId);
			int dt2 = dbm.execNonSqlTrans(sql, argsList, conn);
			if(dt2 <=0){
				logger.error(">>>>>>>>预约服务：修改设计师日程表失败");
				coro.setCode("001093");
				throw new Exception("001093");
			}  */
			
			//创建支付订单表
			sql = "insert into heso_order_pay (order_pay , pay_type , wai_order , order_money ,CREATE_TIME) values (? , ? , ? , ? , SYSDATE()) ";
			argsList.clear();
			argsList.add(seqId);
			argsList.add(payType);
			String waiOrder = dbm.getSequence("seq_order", "16");
			argsList.add(waiOrder);
			argsList.add(amount);
			//
			int y = dbm.execNonSql(sql, argsList) ;
			 if( y <= 0 ){
				 coro.setCode("001093");
				 throw new Exception("001093");
			 }
			 
			/* sql = "INSERT INTO heso_account_quanyi (accounts,type,phone,quanyi,accountname) VALUES (?,?,?,?,?)";
			 argsList.clear();
			 argsList.add(accountId);
			 argsList.add("3");
			 argsList.add(mobile);
			 argsList.add(productId);
			 argsList.add(accountName);
			 int x = dbm.execNonSql(sql, argsList) ;
			 if( x <= 0 ){
				 coro.setCode("001093");
				 throw new Exception("001093");
			 }*/
			 
			 conn.commit();
			 ConsumeOrderObject coo = new ConsumeOrderObject();
			 coo.setOrderId(waiOrder);
			 coo.setAmount(amount+"");
			 coo.setPaymentTerms(payType);
			cList.add(coo);
			coro.setCooList(cList);
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
		return coro;

	}
	public   List<WardrobeDTO> getWardrobeDTOsByOrderId(String orderId){
		String sql = "SELECT hoc.ACCOUNT,hp.SEASON,hp.IMG_FRONT,hp.product_id,hp.TYPE,hp.SCENE,hp.STYLE,hp.COLOR,hp.CATEGORY,hp.NAME,hp.SHANGYI,hp.KUZHUANG," +
				"hp.QUNZHUANG,hp.PEISHI,hp.XIELEI,hp.WAZI,hp.TESHUFU FROM heso_order_consume AS hoc,heso_product AS hp where hoc.PRODUCT_ID = hp.PRODUCT_ID AND ORDER_ID = ?";
		Connection conn = null;
		List<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			List<Object> list = new ArrayList<Object>();
			list.add(orderId);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			WardrobeDTO wDto = new WardrobeDTO();
			if(dt.getRows().size()<=0){
				return new ArrayList<WardrobeDTO>();
			}
			String account = dt.getRows().get(0).getString("ACCOUNT");
			String image = dt.getRows().get(0).getString("IMG_FRONT");
			String type = dt.getRows().get(0).getString("TYPE");
			String scene = dt.getRows().get(0).getString("SCENE");
			String style = dt.getRows().get(0).getString("STYLE");
			String color = dt.getRows().get(0).getString("COLOR");
			String name = dt.getRows().get(0).getString("NAME");
			String season = dt.getRows().get(0).getString("SEASON");
			String productId = dt.getRows().get(0).getString("product_id");
			String category2 = "";
			String secondTypeName = "";
			String secondType = "";
			if("1".equals(type)){ 
				category2 = dt.getRows().get(0).getString("CATEGORY");
				sql = "SELECT * from heso_type WHERE keyword= 'category' and id='" +
						category2 +
						"'";
				List<Object> list1 = new ArrayList<Object>();
				DataTable dtt1 = DatabaseMgr.getInstance().execSqlTrans(sql, list1, conn);
				secondTypeName = dtt1.getRows().get(0).getString("VALUE");
				secondType = dt.getRows().get(0).getString(secondTypeName);
			}
			wDto.setAccount(account);
			wDto.setSeason(season);
			wDto.setSecondTypeName(secondTypeName);
			wDto.setSecondType(secondType);
			wDto.setType(category2);//
			wDto.setImage(image.replace(",", ""));//
			wDto.setScene(scene);//
			wDto.setStyle(style);//
			wDto.setColor(color);//
			wDto.setName(name);//
			wDto.setCharater("");//
			wDto.setCloth("");//
			wDto.setSuit(type);//
			wDto.setUplaod("");
			wDto.setPattern("");
			wDto.setOutline("");
			wDto.setIsGood("1");
			wardrobeDTOs.add(wDto);
			if("2".equals(type)){
				sql = "SELECT hp.IMG_FRONT,hp.SEASON,hp.TYPE,hp.SCENE,hp.STYLE,hp.COLOR,hp.CATEGORY,hp.NAME,hp.SHANGYI,hp.KUZHUANG," +
						"hp.QUNZHUANG,hp.PEISHI,hp.XIELEI,hp.WAZI,hp.TESHUFU  FROM heso_product AS hp WHERE PRODUCT_ID in " +
						"(SELECT ho.PRODUCT_ID FROM heso_order_consume_detail AS ho,heso_product_detail as hpd  " +
						"WHERE ho.PRODUCT_ID = hpd.PRODUCT_GOODS_ID AND ORDER_ID = ? AND hpd.PRODUCT_SUIT_ID = ?)";
				list.add(productId);
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
				for(int i =0;i<dtt.getRows().size();i++){
					WardrobeDTO wDto2 = new WardrobeDTO();
					String dimage = dtt.getRows().get(i).getString("IMG_FRONT");
					String dtype = dtt.getRows().get(i).getString("TYPE");
					String dscene = dtt.getRows().get(i).getString("SCENE");
					String dstyle = dtt.getRows().get(i).getString("STYLE");
					String dcolor = dtt.getRows().get(i).getString("COLOR");
					String category = dtt.getRows().get(i).getString("CATEGORY");
					String dname = dtt.getRows().get(i).getString("NAME");
					String dseason = dtt.getRows().get(i).getString("SEASON");
					sql = "SELECT * from heso_type WHERE keyword= 'category' and id='" +
							category +
							"'";
					List<Object> list2 = new ArrayList<Object>();
					DataTable dtt2 = DatabaseMgr.getInstance().execSqlTrans(sql, list2, conn);
					String secondTypeName2 = dtt2.getRows().get(0).getString("VALUE");
					String secondType2 = dtt.getRows().get(i).getString(secondTypeName2);
					wDto2.setSeason(dseason);
					wDto2.setSecondTypeName(secondTypeName2);
					wDto2.setSecondType(secondType2);
					wDto2.setAccount(account);
					wDto2.setType(category);//
					wDto2.setImage(dimage.replace(",", ""));//
					wDto2.setScene(dscene);//
					wDto2.setStyle(dstyle);//
					wDto2.setColor(dcolor);//
					wDto2.setName(dname);//
					wDto2.setCharater("");//
					wDto2.setCloth("");//
					wDto2.setSuit(dtype);//
					wDto2.setUplaod("");
					wDto2.setPattern("");
					wDto2.setOutline("");
					wDto2.setIsGood("1");
					wardrobeDTOs.add(wDto2);
							
					
				}

			}
			//new WardrobeService().addMyWardrobe(wardrobeDTOs);
			
			
		}catch (Exception e) {
			// TODO: handle exception
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
		
		
		return wardrobeDTOs;
		
	}
	/**
	 * 完成订单支付
	 * @param type
	 * @param orderIds
	 * @param account
	 * @return
	 */
	public ConsumeOrderReturnObject payFinish(String type , String orderId, String payTime){
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null;
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			if("000000".equals(type)){
				//查询订单是否存在
				String sql = " select order_pay from heso_order_pay where wai_order  = ?  and is_pay=0";
				List<Object> list = new ArrayList<Object>();
				list.add(orderId);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
				if(dt.getRows().size() <= 0 )
					throw new Exception("101268");
				String [] orderIds = dt.getRows().get(0).getString("order_pay").split(";");
				/*for(int i = 0 ;  i  < orderIds.length; i++){
				
					List<WardrobeDTO> wardrobeDTOs = getWardrobeDTOsByOrderId(orderIds[i]);
					new WardrobeService().addMyWardrobe(wardrobeDTOs);
				}*/
				//更新支付状态，支付时间
				StringBuffer buSql = new StringBuffer(" update heso_order_consume set pay_status = '1' , pay_time = SYSDATE(), status = '0' where  order_id in ( ? ");
				list.clear();
//				list.add(payTime);
				list.add(orderIds[0]);
				for(int i = 1 ;  i  < orderIds.length; i++){
					buSql.append(", ? ");
					list.add(orderIds[i]);
				}
				buSql.append(" ) ");
				if(	DatabaseMgr.getInstance().execNonSqlTrans(buSql.toString(), list, conn)<= 0){
					throw new Exception("101268");
				}
				
				sql = "update heso_order_pay set is_pay ='1' , pay_time = SYSDATE() where  wai_order = ? ";
				list.clear();
//				list.add(payTime);
				list.add(orderId);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
				 
				sql = "update heso_discount set status = '1' where  order_id = ? ";
				list.clear();
				list.add(orderId);
				DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
				
				String sql2 = "select TYPE ,ACCOUNT,DELIVERY_NAME,DELIVERY_PHONE ,PRODUCT_ID from heso_order_consume as hoc where ORDER_ID  = ?";
				for(int i = 0 ;  i  < orderIds.length; i++){
					String orderIdd = orderIds[i];
					list.clear();
					list.add(orderIdd);
					DataTable dtr = DatabaseMgr.getInstance().execSqlTrans(sql2, list, conn);
					String typee = dtr.getRows().get(0).getString("TYPE");
					String accountId = dtr.getRows().get(0).getString("ACCOUNT");
					String accountName = dtr.getRows().get(0).getString("DELIVERY_NAME");
					String mobile = dtr.getRows().get(0).getString("DELIVERY_PHONE");
					String productId = dtr.getRows().get(0).getString("PRODUCT_ID");
					if("0".equals(typee)){
						//new SmsService().sendMessageByaliyunByorderId(orderIdd, "13570241554" ,SmsService.SMS_TYPE_ORDERID);	
						//new SmsService().sendMessageByaliyunByorderId(orderIdd, "13600059771" ,SmsService.SMS_TYPE_ORDERID);	
//						new SmsService().sendMessageByaliyunByorderId(orderIdd, "18826418804" ,SmsService.SMS_TYPE_ORDERID);
					}else if("3".equals(typee)||"33".equals(typee.trim())) {
						 sql = "INSERT INTO heso_account_quanyi (accounts,type,phone,quanyi,accountname,status) VALUES (?,?,?,?,?,'1')";
						 list.clear();
						 list.add(accountId); 
						 list.add(typee);
						 list.add(mobile);
						 list.add(productId);
						 list.add(accountName);
						 int x = DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
					}else if("4".equals(typee)) {
						sql = "SELECT bonus_point, currency,inner_coin,baohan,count,is_paidan FROM heso_member_product WHERE ID = ?" ;
						list.clear();
						list.add(productId);
						DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
						String baohan = dtt.getRows().get(0).getString("baohan");
						BigDecimal discount = dtt.getRows().get(0).getBigDecimal("currency");
						String flag = dtt.getRows().get(0).getString("inner_coin");
						String amount = dtt.getRows().get(0).getString("bonus_point");
						
						if("0".equals(baohan)){
							sql = "INSERT INTO heso_account_quanyi (accounts,type,phone,quanyi,accountname,status) VALUES (?,?,?,?,?,'0')";
							 list.clear();
							 list.add(accountId);
							 list.add("4");
							 list.add(mobile);
							 list.add(productId);
							 list.add(accountName);
							 int x = DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
						}else {
							
							sql = "SELECT * FROM heso_member_product WHERE ID in (" +
									baohan +
									")";
							list.clear();
							DataTable dttt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
							int k = dttt.getRows().size();
							for (int j = 0;j<dttt.getRows().size();j++){
								sql = "INSERT INTO heso_account_quanyi (accounts,type,phone,quanyi,accountname,status,count) VALUES (?,?,?,?,?,?,?)";
								 list.clear();
								 list.add(accountId);
								 list.add("4");
								 list.add(mobile);
								 list.add(dttt.getRows().get(j).getString("id"));
								 list.add(accountName);
								 list.add(dttt.getRows().get(j).getString("is_paidan"));
								 list.add(dttt.getRows().get(j).getString("count"));
								 int x = DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
							}
							if("111".equals(flag)){
								sql = "UPDATE heso_currency set balance = balance + ? ,TOTAL_RECHARGE = TOTAL_RECHARGE + ? ,discount = ? where ACCOUNT = ?";
								list.clear();
								list.add(amount);
								list.add(amount);
								list.add(discount);
								list.add(accountId);
								int x = DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);

								
							}
						}
					} else {
						List<WardrobeDTO> wardrobeDTOs = getWardrobeDTOsByOrderId(orderIds[i]);
						new WardrobeService().addMyWardrobe(wardrobeDTOs);
						//new SmsService().sendMessageByaliyunByorderId(orderIdd, "18126828067" ,SmsService.SMS_TYPE_ORDERID);	
						//new SmsService().sendMessageByaliyunByorderId(orderIdd, "13600059771" ,SmsService.SMS_TYPE_ORDERID);	
//						new SmsService().sendMessageByaliyunByorderId(orderIdd, "18826418804" ,SmsService.SMS_TYPE_ORDERID);
					}
				}
				
				
				/*for(ConsumeOrderObject coo :coro.getCooList()){
					addWardrobeByProductId(coo.getProductId(), coo.getAccount());
					
				}*/
			}else{ 
				coro.setCode("101269");
			}
			conn.commit();
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
		return coro;
	}
	
	/**
	 * 
	 * @param waiorderId
	 * @return
	 */
	
	public  ConsumeOrderReturnObject getDetailsByWaiorder(String waiorderId){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		 ArrayList<ConsumeOrderObject> coolist = new ArrayList<ConsumeOrderObject>();
		 ConsumeOrderObject coo = new ConsumeOrderObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
 			  
				//查询订单是否存在
				String sql = " select order_pay,pay_type,is_pay,order_money,PAY_TIME,create_time from heso_order_pay where wai_order  = ? ";
				List<Object> list = new ArrayList<Object>();
				list.add(waiorderId);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
				if(dt.getRows().size() <= 0 )
					throw new Exception("101268");
				String payType = dt.getRows().get(0).getString("pay_type");
				String isPay = dt.getRows().get(0).getString("is_pay");
				String orderMoney = dt.getRows().get(0).getString("order_money");
				String payTime = dt.getRows().get(0).getString("pay_time");
				String orderIds = dt.getRows().get(0).getString("order_pay");
				String createTime = dt.getRows().get(0).getString("create_time");
				if(orderIds.endsWith(";")){
					orderIds = orderIds.substring(0,orderIds.length()-1);
				}
			    orderIds = orderIds.replace(";", ",");
			    String userName = "";
			    String sex = "";
			    sql = "SELECT hso.ORDER_ID,hso.PRODUCT_ID,hso.PAYMENT_TERMS, hso.TYPE,hso.AMOUNT, hso.PAY_STATUS ,ha.SEX as sex1,ha.USER_ID FROM heso_order_consume AS hso ,heso_account AS ha " +
			    		"WHERE  hso.ORDER_ID in (" +
			    		orderIds +
			    		") AND hso.ACCOUNT = ha.ACCOUNT;";
				list.clear();
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
				int x = dt1.getRows().size();
				userName = dt1.getRows().get(0).getString("USER_ID");
				sex = dt1.getRows().get(0).getString("sex1");
				coo.setPaymentTerms(payType);
				coo.setPayStatus(isPay);
				coo.setAmount(orderMoney);
				coo.setPayTime(payTime);
				coo.setOrderId(waiorderId);
				coo.setAccount(userName);
				coo.setCreateTime(createTime);
				coo.setSex(sex);
				ArrayList<ConsumeOrderObject> cpolist = new ArrayList<ConsumeOrderObject>();
				for(int i = 0; i < dt1.getRows().size();i++){
					ConsumeOrderObject cpo = new ConsumeOrderObject();
					DataRow dr = dt1.getRows().get(i);
 					String productId = dr.getString("PRODUCT_ID");
					String type = dr.getString("TYPE");
					String orderId = dr.getString("ORDER_ID");
					String productName = "";
					String psex = "2";
					String designerName = "";
					if(type.equals("0")){
						sql = "SELECT hmp.name ,hd.admin_name FROM heso_member_product AS hmp ,heso_designer_place AS hdp ,heso_admin AS hd	"
								+"WHERE hd.ADMIN_ID = hdp.DESIGNERID AND hmp.ID = hdp.SERVICEID AND hdp.ORDERID like '%" +
								orderId +
								"%'";
						list.clear();
						DataTable dt11 = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
						productName = dt11.getRows().get(0).getString("name");
						designerName = dt11.getRows().get(0).getString("admin_name");
					}else if(type.equals("1")||type.equals("2")){
						sql = "SELECT  hp.name,hp.sex  FROM heso_product AS hp WHERE hp.PRODUCT_ID=?";
						list.clear();
						list.add(productId);
						DataTable dt11 = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
						productName = dt11.getRows().get(0).getString("name");
						psex = dt11.getRows().get(0).getString("sex");
					}else if(type.equals("3")||type.equals("33")||type.equals("4")){
						sql = "SELECT hmp.NAME FROM heso_member_product AS hmp WHERE id = ?";
						list.clear();
						list.add(productId);
						DataTable dt11 = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
						productName = dt11.getRows().get(0).getString("name");
					}
					String payType1 = dr.getString("PAYMENT_TERMS");
					String amount = dr.getString("AMOUNT");
					String payStatus = dr.getString("PAY_STATUS");
				    cpo.setSeller(designerName);
					cpo.setOrderId(orderId);
					cpo.setPaymentTerms(payType1);
					cpo.setType(type);
					cpo.setAmount(amount);
					cpo.setPayStatus(payStatus);
					cpo.setSex(psex);
					cpo.setName(productName);
					cpo.setProductId(productId);
					cpolist.add(cpo);
					
				}
				coo.setCooList(cpolist);
				coolist.add(coo);
				coro.setCooList(coolist);
			 
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
		return coro;
	}
	
	/**
	 * 物流支付
	 * @param regionalId
	 * @return
	 */
	public ConsumeOrderReturnObject logisticsPay (String regionalId,Double amount,String orderId){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			String sql = " select PRICE from heso_deliver_fee where FINAL_PROVINCE like ";
			List<Object> list = new ArrayList<Object>();
			String regional = regionalId.substring(0,2);
			
			switch (Integer.parseInt(regional)) {
			case 65:
				sql+= " '%"+"新疆"+"%' ";
				break;
			case 54:
				sql+=" '%"+"西藏"+"%' ";
				break;
			case 71:
				sql+=" '%"+"台湾"+"%' ";
				break;
			case 81:
				sql+=" '%"+"香港"+"%' ";
				break;
			case 82:
				sql+=" '%"+"澳门"+"%' ";
				break;
			case 99:
				sql+=" '%"+"其他"+"%' ";
				break;
			default:
				if(amount<299){
					sql = "select PRICE from heso_deliver_fee where FINAL_PROVINCE = ( select REGION_NAME from heso_region where REGION_CODE = ? ) ";
					list.add(regional+="0000");
					break;
				}else{
					coro.setReccount("0");
					return coro;
				}
				
			}
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			if(orderId != null || !orderId.isEmpty()){
					sql = "update heso_order_pay set freight = ? where wai_order = ? ";
					list.clear();
					list.add(dt.getRows().get(0).getString("price"));
					list.add(orderId);
					if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
						throw new Exception("100151");
			}
			coro.setReccount(dt.getRows().get(0).getString("price"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){
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
	 * 首单优惠
	 * @param amount
	 * @param account
	 * @return
	 */
	public ConsumeOrderReturnObject firstPay(String amount,String account,String orderId){
		Connection conn = null;
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			String sql = "select order_id from heso_discount where account = ? and status = '1' and type= '1' ";
			List<Object> list = new ArrayList<Object>();
			list.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
			Double total = 0.0;
			Double dAmount = Double.parseDouble(amount);
			if(dt.getRows().size() > 0){
				coro.setReccount(dAmount.toString());
				return coro;
			}
			if(dAmount >= 300 && dAmount < 600){
				total =  new BigDecimal(dAmount - 8).doubleValue();
				coro.setDiscount("8");
			}else if(dAmount >= 600 && dAmount < 1000){
				total = new BigDecimal(dAmount - 18).doubleValue();
				coro.setDiscount("18");
			}else if(dAmount >= 1000 && dAmount < 1500 ){
				total = new BigDecimal( dAmount - 38).doubleValue();
				coro.setDiscount("38");
			}else if(dAmount >= 1500 && dAmount < 2000){
				total = new BigDecimal(dAmount - 58).doubleValue();
				coro.setDiscount("58");
			}else if(dAmount >= 2000 && dAmount < 3000){
				total = new BigDecimal(dAmount - 88).doubleValue();
				coro.setDiscount("88");
			}else if(dAmount >= 3000){
				total = new BigDecimal(dAmount - 138).doubleValue();
				coro.setDiscount("138");
			}else {
				total = dAmount;
				coro.setDiscount("0");
			}
			
			if(orderId !=null || !orderId.isEmpty()){
				sql = "update heso_order_pay set discount = ? where wai_order = ? ";
				list.clear();
				list.add(coro.getDiscount());
				list.add(orderId);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
					throw new Exception("100151");
				}
				
				sql = "insert into heso_discount (account , discount_money , type , order_id ) values (? , ? , ? , ?) ";
				list.clear();
				list.add(account);
				list.add(coro.getDiscount());
				list.add("1");//1-首单优惠,2-优惠券优惠
				list.add(orderId);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
					throw new Exception("100151");
				}
			}

			coro.setReccount(String.valueOf(total));
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return coro;
	}
	
	/**
	 * 重新更新支付表和折扣表(优惠券折扣使用的公共方法)
	 * @param coro 优惠券折扣实体
	 * @param orderId 订单号
	 * @param account 账号
	 * @param coupPrice 优惠金额
	 * @param conn 当前连接
	 * @throws Exception
	 */
	private void reUpdatePayAndDiscount(ConsumeOrderReturnObject coro,String orderId,String account,Double coupPrice,Connection conn) throws Exception{
		List<Object> list = new ArrayList<Object>();
		String sql = "";
		
		//重新更新支付表
		sql = "update heso_order_pay set discount = ? where wai_order = ? ";
		list.clear();
		list.add(coro.getDiscount());
		list.add(orderId);
		if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
			throw new Exception("100151");
		}
		
		sql = "insert into heso_discount (account , discount_money , type , order_id ) values (? , ? , ? , ?) ";
		list.clear();
		list.add(account);
		list.add(coupPrice);
		list.add("2");//1-首单优惠,2-优惠券优惠
		list.add(orderId);
		if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
			throw new Exception("100151");
		}
		
	}
	
	/**
	 * 根据外单号获取原订单号(优惠券折扣使用的公共方法)
	 * @param orderId 外单号
	 * @param conn  当前连接
	 * @return 返回原订单号，如 001','002
	 * @throws Exception
	 */
	private String getOriginalOrder(String orderId,Connection conn) throws Exception{
		List<Object> list = new ArrayList<Object>();
		String sql = "";
		
		sql = " SELECT * from heso_order_pay  where wai_order = ?";
		list.clear();
		list.add(orderId);
		DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
		if(dt.getRows().size()==0)
			throw new Exception("100151");
		
		String originallyOrderId = dt.getRows().get(0).getString("order_pay");//原订单，多个订单时以";"分割
		if(originallyOrderId.contains(";")){
			originallyOrderId = originallyOrderId.replace(";", "','");
		}
		
		return originallyOrderId;
	}
	
	/**
	 * 当前购物车提交商品只有1个,直接更新heso_order_consume
	 * @param dt 获取商品的结果集
	 * @param coupPrice 优惠金额
	 * @param account 账号
	 * @param conn 当前连接
	 * @throws Exception
	 */
	private void updateOrderConsume(DataTable dt,Double coupPrice,String account,Connection conn)throws Exception{
		List<Object> list = new ArrayList<Object>();
		String sql = "";
		
		String tempOrderId = dt.getRows().get(0).getString("ORDER_ID");//每个商品订单号
		
		//更新订单表优惠券总优惠金额
		sql = " update heso_order_consume set coupon_amount=? where order_id=? and account=?";
		list.clear();
		list.add(coupPrice);
		list.add(tempOrderId);
		list.add(account);
		
		if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
			throw new Exception("100151");
		}
	}
	
	public   ProductsDTO productDTOById(String productId) throws Exception{
		ProductsDTO dto = new ProductsDTO();
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		String sql = " select DISCOUNT_PRICE,SUIT_PRICE, name ,type , images , img_front , STOCK_COUNT," +
						  " case when type='1' then if(DISCOUNT_PRICE = 0,price,DISCOUNT_PRICE) when type = '2' then SUIT_PRICE end price ,color " +
				          " from heso_product where product_id = ? ";
		ArrayList<Object> argsList = new ArrayList<Object>();
		argsList.add(productId);
		DataTable dt;
		try { 
			dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() == 0){
				logger.error(">>>>>>>>>>>>>查找商品，找不到该商品："+ productId);
				throw new Exception("101120");
			}
			String price = dt.getRows().get(0).getString("DISCOUNT_PRICE");
			String type = dt.getRows().get(0).getString("type");
			String stock = dt.getRows().get(0).getString("STOCK_COUNT");
			String name = dt.getRows().get(0).getString("name");
			String image = dt.getRows().get(0).getString("img_front");
			String huiyuanPrice = dt.getRows().get(0).getString("SUIT_PRICE");
			 
			dto.setProdctId(productId);
			dto.setPrice(price);
			dto.setStock(stock);
			dto.setImage(image);
			dto.setType(type);
			dto.setProName(name);
			dto.setHuiyuanPrice(huiyuanPrice);
			 
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		

		
		return dto;
		
	}
	
	public   List<ProductDTO> findProductById(String productId) throws Exception{
		List<ProductDTO> listObjects = new ArrayList<ProductDTO>();
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		String sql = " select name ,type , images , img_front , STOCK_COUNT," +
						  " case when type='1' then if(DISCOUNT_PRICE = 0,price,DISCOUNT_PRICE) when type = '2' then SUIT_PRICE end price ,color " +
				          " from heso_product where product_id = ? ";
		ArrayList<Object> argsList = new ArrayList<Object>();
		argsList.add(productId);
		DataTable dt;
		try { 
			dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() == 0){
				logger.error(">>>>>>>>>>>>>查找商品，找不到该商品："+ productId);
				throw new Exception("101120");
			}
			String price = dt.getRows().get(0).getString("price");
			String type = dt.getRows().get(0).getString("type");
			String stock = dt.getRows().get(0).getString("STOCK_COUNT");
			String name = dt.getRows().get(0).getString("name");
			String image = dt.getRows().get(0).getString("img_front");
			
			
			ProductDTO productDTO = new ProductDTO();
			productDTO.setProdctId(productId);
			productDTO.setPrice(price);
			productDTO.setStock(stock);
			productDTO.setImage(image);
			productDTO.setType(type);
			productDTO.setProName(name);
			listObjects.add(productDTO);
			
			if("2".equals(type)){
				sql = "select * from heso_product where PRODUCT_ID in (select b.product_goods_id from heso_product a, heso_product_detail b where a.product_id = b.product_suit_id and b.product_suit_id =? ) ";
				argsList.clear();
				argsList.add(productId);
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				for (int i = 0; i < dt1.getRows().size(); i++) {
					 dr = dt1.getRows().get(i);
					 String pprice = dr.getString("price");
					 String ptype = dr.getString("type");
					 String pproductId = dr.getString("PRODUCT_ID");
					 String pstock = dr.getString("STOCK_COUNT");
					 String pname = dr.getString("name");
					 String pimage = dr.getString("img_front");
					 ProductDTO DTO = new ProductDTO();
					 DTO.setPrice(pprice);
					 DTO.setProdctId(pproductId);
					 DTO.setImage(pimage);
					 DTO.setStock(pstock);
					 DTO.setType(ptype);
					 DTO.setProName(pname);
					 listObjects.add(DTO);
				}
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
		

		
		return listObjects;
		
	}
	
	//查询提成表
	public   List<TichengDTO> checkTicheng(String adminId){
		
		List<ProductDTO> listObjects = new ArrayList<ProductDTO>();
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "SELECT hat.ID,hat.TYPE,hat.PAIDAN_ID,hat.ORDER_ID ,hat.ADMIN_ID,hat.IDENTITY,hat.XIAOSHOU_TICHENG,hat.FUWU_TICHENG,hat.FUSHI_TICHENG,hat.SUM_AMOUNT,hat.CREATETIME ,ha.NAME " +
				"FROM heso_admin_ticheng AS hat,heso_admin as ha WHERE  hat.ADMIN_ID = ha.ADMIN_ID " ;
		if(!"".equals(adminId)&&!adminId.isEmpty()){
			sql = sql + " and hat.ADMIN_ID = ?";
			argsList.add(adminId);
		}
		List<TichengDTO> list = new ArrayList<TichengDTO>();
		DataTable dt;
		try { 
			dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int x = dt.getRows().size();
			for(int i = 0;i<dt.getRows().size();i++){
				 String id = dt.getRows().get(i).getInt ("ID")+"";
				 String type = dt.getRows().get(i).getString("TYPE");
				 String paidanId = dt.getRows().get(i).getString("PAIDAN_ID");
				 String orderId = dt.getRows().get(i).getString("ORDER_ID");
				 String adminIdd = dt.getRows().get(i).getString("ADMIN_ID");
				 String identity = dt.getRows().get(i).getString("IDENTITY");
				 String xiaoshouTicheng = dt.getRows().get(i).getString("XIAOSHOU_TICHENG");
				 String fushiTicheng = dt.getRows().get(i).getString("FUSHI_TICHENG");
				 String createTime = dt.getRows().get(i).getString("CREATETIME");
				 String sumAmount = dt.getRows().get(i).getString("SUM_AMOUNT");
				 String fuwuTicheng = dt.getRows().get(i).getString("FUWU_TICHENG");
				 String adminName = dt.getRows().get(i).getString("NAME");
				 TichengDTO dto = new TichengDTO();
				 dto.setId(id);
				 dto.setType(type);
				 dto.setPaidanId(paidanId);
				 dto.setOrderId(orderId);
				 dto.setAdminId(adminIdd);
				 dto.setIdentity(identity);
				 dto.setXiaoshouTicheng(xiaoshouTicheng);
				 dto.setFushiTicheng(fushiTicheng);
				 dto.setCreateTime(createTime);
				 dto.setSumAmount(sumAmount);
				 dto.setFuwuTicheng(fuwuTicheng);
				 dto.setAdminName(adminName);
				 list.add(dto);
				 
				 
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		return list;
	}
	//查询服饰订单表
	public   List<OrderTicheng>  checkFushiOrder( ){
		List<ProductDTO> listObjects = new ArrayList<ProductDTO>();
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "SELECT hoc.order_id,hoc.account,hoc.create_time,hoc.product_id,hoc.type, hoc.name,hoc.image,hoc.AMOUNT,hoc.PAY_STATUS,hoc.SEND_STATUS," +
				"hoc.TRACKING_NUM,hoc.DELIVERY_NAME,hoc.PRICE_COST ,hoc.KEHUPINGFEN,hoc.KEHUJIANYI,hoc.SELLER_NAME,hoc.QUDAOJINGLI_NAME,hoc.QUDAO_NAME," +
				"hoc.TEACHER_NAME,hoc.QUDAOJINGLI_ID,hoc.QUDAO_ID,hoc.TEACHER_ID,hoc.SELLER FROM heso_order_consume as hoc WHERE type = '1' OR type='2'" ;
		 
		List<OrderTicheng> list = new ArrayList<OrderTicheng>();
	 
		try { 
			argsList.clear();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			sql = "SELECT order_id,admin_id,fushi_ticheng FROM heso_admin_ticheng  WHERE type='3'";
			DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int x = dt.getRows().size();
			for(int i = 0;i<dt.getRows().size();i++){
				OrderTicheng qt = new OrderTicheng();
				String orderId = dt.getRows().get(i).getString("order_id");
				String account = dt.getRows().get(i).getString("account");
				String createTime = dt.getRows().get(i).getString("create_time");
				String productId = dt.getRows().get(i).getString("product_id");
				String type = dt.getRows().get(i).getString("type");
				String productName = dt.getRows().get(i).getString("name");
				String iamge = dt.getRows().get(i).getString("image");
				String amount = dt.getRows().get(i).getString("AMOUNT");
				String payStatus = dt.getRows().get(i).getString("PAY_STATUS");
				String sendStatus = dt.getRows().get(i).getString("SEND_STATUS");
				String tracking_num = dt.getRows().get(i).getString("TRACKING_NUM");
				String accountName = dt.getRows().get(i).getString("DELIVERY_NAME");
				String price_cost = dt.getRows().get(i).getString("PRICE_COST");
				String kehupingfen = dt.getRows().get(i).getString("KEHUPINGFEN");
				String kehujianyi = dt.getRows().get(i).getString("KEHUJIANYI");
				String genjinrenName = dt.getRows().get(i).getString("SELLER_NAME");
				String qudaojingliName = dt.getRows().get(i).getString("QUDAOJINGLI_NAME");
				String qudaoName = dt.getRows().get(i).getString("QUDAO_NAME");
				String teacherName = dt.getRows().get(i).getString("TEACHER_NAME");
				String qudaojingliId = dt.getRows().get(i).getString("QUDAOJINGLI_ID");
				String qudaoId = dt.getRows().get(i).getString("QUDAO_ID");
				String teacherId = dt.getRows().get(i).getString("TEACHER_ID");
				String genjinrenId = dt.getRows().get(i).getString("SELLER");
				qt.setOrderId(orderId);
				qt.setAccount(account);
				qt.setCreateTime(createTime);
				qt.setProductId(productId);
				qt.setType(type);
				qt.setProductName(productName);
				qt.setIamge(iamge);
				qt.setAmount(amount);
				qt.setPayStatus(payStatus);
				qt.setSendStatus(sendStatus);
				qt.setTracking_num(tracking_num);
				qt.setAccountName(accountName);
				qt.setPrice_cost(price_cost);
				qt.setKehupingfen(kehupingfen);
				qt.setKehujianyi(kehujianyi);
				qt.setGenjinrenName(genjinrenName);
				qt.setQudaojingliName(qudaojingliName);
				qt.setQudaoName(qudaoName);
				qt.setTeacherName(teacherName);
				qt.setQudaojingliId(qudaojingliId);
				qt.setQudaoId(qudaoId);
				qt.setTeacherId(teacherId);
				qt.setGenjinrenId(genjinrenId);
				
				 for (int j = 0; j < dt2.getRows().size(); j++) {
					 String orderId2 = dt2.getRows().get(j).getString("order_id");
					 String fushiticheng = dt2.getRows().get(j).getString("fushi_ticheng");
					 String adminId2 = dt2.getRows().get(j).getString("admin_id");
					if(orderId.equals(orderId2)&&adminId2.equals(genjinrenId)){
						String genjinrenTicheng = fushiticheng;
						qt.setGenjinrenTicheng(genjinrenTicheng);
					}
					if(orderId.equals(orderId2)&&adminId2.equals(qudaoId)){
						String qudaoTicheng = fushiticheng;
						qt.setQudaoTicheng(qudaoTicheng);
					}
					if(orderId.equals(orderId2)&&adminId2.equals(qudaojingliId)){
						String qudaojingliTicheng = fushiticheng;
						qt.setQudaojingliTicheng(qudaojingliTicheng);
					}
					if(orderId.equals(orderId2)&&adminId2.equals(teacherId)){
						String teacherTicheng = fushiticheng;
						qt.setTeacherTicheng(teacherTicheng);
					} 
				}
				 list.add(qt);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		 
		
		
		return list;
	}
	
	//查询服务派单表
	public    List<SendOrder>  checkFuWuOrder( ){
		 
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "SELECT ID,TEACHER_ID,TEACHER_NAME,ZHULIA_ID,ZHULIA_NAME,ZHULIB_ID,ZHULIB_NAME,SERVICE_TIME,SERVICE_PLACE,SERVICE_ID,SERVICE_NAME ,ACCOUNT,ACCOUNT_NAME," +
				"GENJINREN_ID,GENJINREN_NAME,JINYIBU,STATUS,KEHUPINGFEN,KEHUJIANYI FROM heso_send_order " ;
		 
		List<SendOrder> list = new ArrayList<SendOrder>();
	 
		try { 
			argsList.clear();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			sql = "SELECT PAIDAN_ID,admin_id,FUWU_TICHENG FROM heso_admin_ticheng  WHERE type='2'";
			DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			int x = dt.getRows().size();
			for(int i = 0;i<dt.getRows().size();i++){
				 SendOrder so = new SendOrder();
				String id = dt.getRows().get(i).getString("ID");
				String teacherId = dt.getRows().get(i).getString("TEACHER_ID");
				String teacherName = dt.getRows().get(i).getString("TEACHER_NAME");
				String zhuliAId = dt.getRows().get(i).getString("ZHULIA_ID");
				String zhuliAName = dt.getRows().get(i).getString("ZHULIA_NAME");
				String zhuliBId = dt.getRows().get(i).getString("ZHULIB_ID");
				String zhuliBName = dt.getRows().get(i).getString("ZHULIB_NAME");
				String serviceTime = dt.getRows().get(i).getString("SERVICE_TIME");
				String servicePlace = dt.getRows().get(i).getString("SERVICE_PLACE");
				String serviceId = dt.getRows().get(i).getString("SERVICE_ID");
				String serviceName = dt.getRows().get(i).getString("SERVICE_NAME");
			 
				String account = dt.getRows().get(i).getString("ACCOUNT");
				String accountName = dt.getRows().get(i).getString("ACCOUNT_NAME");
				String genjinrenId = dt.getRows().get(i).getString("GENJINREN_ID");
				String genjinrenName = dt.getRows().get(i).getString("GENJINREN_NAME");
				String jinyibu = dt.getRows().get(i).getString("JINYIBU");
				String status = dt.getRows().get(i).getString("STATUS");
				String kehupingfen = dt.getRows().get(i).getString("KEHUPINGFEN");
				String kehujianyi = dt.getRows().get(i).getString("KEHUJIANYI");
				so.setId(id);
				so.setTeacher_id(teacherId);
				so.setTeacher_name(teacherName);
				so.setZhuliA_id(zhuliAId);
				so.setZhuliA_name(zhuliAName);
				so.setZhuliB_id(zhuliBId);
				so.setZhuliB_name(zhuliBName);
				so.setService_time(serviceTime);
				so.setSercive_place(servicePlace);
				so.setService_id(serviceId);
				so.setService_name(serviceName);
				so.setAccount(account);
				so.setAccount_name(accountName);
				so.setGenjinren_id(genjinrenId);
				so.setGenjinren_name(genjinrenName);
				so.setJinyibu(jinyibu);
				so.setStatus(status);
				so.setKehupingfen(kehupingfen);
				so.setKehujianyi(kehujianyi);
		 
				
				 for (int j = 0; j < dt2.getRows().size(); j++) {
					 String paidanId = dt2.getRows().get(j).getString("PAIDAN_ID");
					 String fuwuticheng = dt2.getRows().get(j).getString("FUWU_TICHENG");
					 String adminId2 = dt2.getRows().get(j).getString("admin_id");
					if(paidanId.equals(id)&&adminId2.equals(zhuliAId)){
						 
						so.setZhuliA_ticheng(fuwuticheng);
 					}
					if(paidanId.equals(id)&&adminId2.equals(zhuliBId)){
 						so.setZhuliB_ticheng(fuwuticheng);
					}
					if(paidanId.equals(id)&&adminId2.equals(teacherId)){
						so.setTeacher_ticheng(fuwuticheng);
					}
					 
				}
				 list.add(so);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		 
		
		
		return list;
	}
	
	
	public    ProductsDTO  productToProductsDTO(ProductsDTO dto ){
		 
		DataRow dr = null;
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 获取商品信息
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "" ;
		 
		
		List<ProductsDTO> itemsList = new ArrayList<ProductsDTO>();
		List<SendOrder> list = new ArrayList<SendOrder>();
		List<String> pinleiList = new ArrayList<String>();
		ProductsDTO dtto = new ProductsDTO();
		try { 
 			 
			//尺码查询
			sql = "SELECT * FROM heso_product_size WHERE PRODUCT_ID = ? AND size = ? AND color_id = ?";
			argsList.clear();
			argsList.add(dto.getProdctId());
			argsList.add(dto.getSize());
			argsList.add(dto.getColor());
			DataTable sizedt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			
			//工艺查询
			sql = "SELECT *FROM heso_product_gongyi WHERE PRODUCT_ID = ?";
			argsList.clear();
			argsList.add(dto.getProdctId());
			//工艺拼接
			DataTable gongyidt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			String clothType = "";
			String gongyi = "";
			String gongyi_code = "";
			for(int j=0;j<gongyidt.getRows().size();j++){
				DataRow drt = gongyidt.getRows().get(j);
				String chanese = drt.getString("CN_DESC");
				String gongyiCode = drt.getString("GONGYI");
				String flag = drt.getString("IS_ZHIDING");
				String code = drt.getString("SYS_CODE");
				if("1".equals(flag)){
					String zhiding = drt.getString("ZHIDING");
					code = code+":"+zhiding;
					gongyiCode = gongyiCode +" "+zhiding;
				}
				gongyi = gongyi + gongyiCode +" "+chanese +";";
				gongyi_code = gongyi_code + code +",";
				clothType = drt.getString("PINLEI");
			}
			pinleiList.add(clothType);
			//------组合尺码
			String chicunString = "0";
			//下单方式
			int x = sizedt.getRows().size();
			String xiadanfangshi = sizedt.getRows().get(0).getString("BIAOZHUNHAOXING");
			String biaozhunhao = "";
			String beizhu = sizedt.getRows().get(0).getString("CHIMA_DESC");
			//胸围
			String xiongwei = "0";
			//腰围
			String yaowei = "0";
			//臀围
			String tunwei= "0";
			//肩宽
			String jiankuan = "0";
			//袖肥
			String xiufei = "0";
			//后衣长
			String houyichang = "0";
			//袖长
			String xiuchang = "0";
			//领围
			String lingwei = "0";
			//袖口
			String xiukou = "0";
			//通档
			String tongdang = "0";
			//横档
			String hengdang = "0";
			//脚口
			String jiaokou = "0";
			//裙长
			String qunchang = "0";
			//下摆
			String xiabai = "0";
			String kuchang = "0";
			String xiwei = "";
			//女西服
			if("95000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//肩宽
					jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
					//袖肥
					xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
					//袖长
					xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
				}
				
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "35012:"+xiongwei+",35013:"+yaowei+",35014:"+tunwei+",35015:"+jiankuan+",35016:"+xiufei+",35017:"+houyichang+",35018:"+xiuchang+",35019:"+xiuchang;
			}
			//女西裤
			if("98000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//脚口
					jiaokou = sizedt.getRows().get(0).getString("FOOT_WIDE");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//横档
					hengdang = sizedt.getRows().get(0).getString("HENGDANG");
					//通档
					tongdang = sizedt.getRows().get(0).getString("TONGDANG");
					//裤长
					kuchang = sizedt.getRows().get(0).getString("PANTS");
					 
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "35244:"+jiaokou+",35241:"+yaowei+",35242:"+tunwei+",35246:"+hengdang+",35245:"+tongdang+",35247:"+kuchang+",35248:"+kuchang ;
			}
			//女衬衣
			if("11000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//袖口
					xiukou= sizedt.getRows().get(0).getString("XIUKOU");
					//肩宽
					jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
					//袖肥
					xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
					//袖长
					xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
					//领围
					lingwei = sizedt.getRows().get(0).getString("LINGWEI");
					
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "35255:"+xiongwei+",35258:"+jiankuan+",35259:"+xiufei+",35262:"+houyichang+",35263:"+xiuchang+",35264:"+xiuchang +",35260:"+xiukou+",35261:"+xiukou+",35254:"+lingwei;
			}
			//女大衣
			if("103000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//肩宽
					jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
					//袖肥
					xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
					//袖长
					xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
					
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "35678:"+xiongwei+",35679:"+yaowei+",35680:"+tunwei+",35681:"+jiankuan+",35682:"+xiufei+",35683:"+houyichang+",35684:"+xiuchang+",35685:"+xiuchang;
			}
			//女马甲
			if("222000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "661057:"+yaowei+",661058:"+houyichang;
			}
			//女西裙
			if("130000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//裙长
					qunchang = sizedt.getRows().get(0).getString("SKIRT");
					//下摆
					xiabai= sizedt.getRows().get(0).getString("XIABAI");
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "130032:"+yaowei+",130033:"+tunwei+",130034:"+qunchang+",130047:"+xiabai;
			}
			//男西装
			if("3".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//肩宽
					jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
					//袖肥
					xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
					//袖长
					xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
				}
				
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33001:"+xiongwei+",33002:"+yaowei+",33003:"+tunwei+",33004:"+jiankuan+",33005:"+xiufei+",33006:"+houyichang+",33007:"+xiuchang+",33007:"+xiuchang;
			
			}
			//男西裤
			if("2000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//脚口
					jiaokou = sizedt.getRows().get(0).getString("FOOT_WIDE");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//横档
					hengdang = sizedt.getRows().get(0).getString("HENGDANG");
					//通档
					tongdang = sizedt.getRows().get(0).getString("TONGDANG");
					//裤长
					kuchang = sizedt.getRows().get(0).getString("PANTS");
					//膝围
					xiwei = sizedt.getRows().get(0).getString("XIWEI");
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33011:"+jiaokou+",33008:"+yaowei+",33009:"+tunwei+",33015:"+hengdang+",33012:"+tongdang+",33016:"+kuchang+",33079:"+kuchang ;
				if(!"".equals(xiwei)&&xiwei!=null&&!"0".equals(xiwei)){
					chicunString = chicunString + ",33010:" +xiwei;
				}
			}
			//男马甲
			if("4000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
				}
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33018:"+xiongwei+",33017:"+yaowei+",33019:"+houyichang ;
			
			}
			//夹克
			if("213000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){

						
				}
				
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33001:"+xiongwei+",33002:"+yaowei+",33003:"+tunwei+",33004:"+jiankuan+",33005:"+xiufei+",33006:"+houyichang+",33007:"+xiuchang+",33007:"+xiuchang;
			}
			//男大衣
			if("6000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					 
				}
				
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33001:"+xiongwei+",33002:"+yaowei+",33003:"+tunwei+",33004:"+jiankuan+",33005:"+xiufei+",33006:"+houyichang+",33007:"+xiuchang+",33007:"+xiuchang;
			
			}
			//男衬衫
			if("3000".equals(clothType)){
				if(!"206051".equals(xiadanfangshi)){
					//胸围
					xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
					//腰围
					yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
					//臀围
					tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
					//肩宽
					jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
					//袖肥
					xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
					//后衣长
					houyichang = sizedt.getRows().get(0).getString("CLOTHES");
					//袖长
					xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
					//袖口
					xiukou= sizedt.getRows().get(0).getString("XIUKOU");
					lingwei = sizedt.getRows().get(0).getString("LINGWEI");

				}
				
				biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
				chicunString = "33021:"+xiongwei+",33022:"+yaowei+",33023:"+tunwei+",33025:"+xiufei+",33028:"+houyichang+",33029:"+xiuchang+",33081:"+xiuchang+",33026:"+xiukou+",33080:"+xiukou;
				if(jiankuan!=null&&!"".equals(jiankuan)&&!"0".equals(jiankuan)){
					chicunString = chicunString + ",33024:"+jiankuan;
				}
			
			}
			//商品查询
			sql  = "SELECT * FROM heso_product WHERE PRODUCT_ID = ?";
			argsList.clear();
			argsList.add(dto.getProdctId());
			DataTable productdt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
 			String pprice = productdt.getRows().get(0).getString("DISCOUNT_PRICE");
 			String huiyuanprice = productdt.getRows().get(0).getString("SUIT_PRICE");
			String ptype = productdt.getRows().get(0).getString("type");
			String pproductId = productdt.getRows().get(0).getString("PRODUCT_ID");
			String pstock = productdt.getRows().get(0).getString("STOCK_COUNT");
			String pname = productdt.getRows().get(0).getString("name");
			String pimage = productdt.getRows().get(0).getString("img_front");
			dtto.setPrice(pprice);
			dtto.setProdctId(pproductId);
			dtto.setImage(pimage);
			dtto.setStock(pstock);
			dtto.setType(ptype);
			dtto.setProName(pname);
			dtto.setChangdu("");
			dtto.setWeidu("");
			dtto.setGongyi(gongyi_code);
			dtto.setXiadangfangshi(xiadanfangshi);
			biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
			dtto.setChimaquyu(biaozhunhao);
			dtto.setRemark(sizedt.getRows().get(0).getString("CHIMA_DESC"));
			if("206051".equals(xiadanfangshi)){
				sql = "SELECT hdt.desc FROM heso_dingzhi_type as hdt where hdt.name = ? AND hdt.value = ? AND hdt.belong = ?";
				argsList.clear();
				argsList.add(biaozhunhao);
				argsList.add("206051");
				argsList.add(clothType);
				DataTable biaozhundt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				 int n = biaozhundt.getRows().size();
				if(biaozhundt.getRows().size()!=0){
					dtto.setChima(biaozhundt.getRows().get(0).getString("desc"));
				}
			}else {				
				dtto.setChima(chicunString);
			}
			dtto.setGongyiCn(gongyi);
			dtto.setPinlei(clothType);
			dtto.setColor(dto.getColor());
			dtto.setSize(dto.getSize());
			dtto.setCount(dto.getCount());
			dtto.setHuiyuanPrice(huiyuanprice);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
		 
		
		
		return dtto;
	}
	
	
	//查询服务派单表
		public    List<SendOrder>  zhuangdingdan(String productId,List<ProductsDTO> itemIdList ){
			 
			DataRow dr = null;
			Connection conn = DatabaseMgr.getInstance().getConnection();
			// 获取商品信息
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "SELECT PRODUCT_GOODS_ID FROM heso_product_detail WHERE product_suit_id =  ?" ;
			argsList.add(productId);
			
			List<ProductsDTO> itemsList = new ArrayList<ProductsDTO>();
			List<SendOrder> list = new ArrayList<SendOrder>();
			List<String> pinleiList = new ArrayList<String>();
			try { 
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				for(int i = 0 ;i<dt.getRows().size();i++){
					String goodId = dt.getRows().get(i).getString("PRODUCT_GOODS_ID");
					for(ProductsDTO dto : itemIdList){
						if(dto.getProdctId().equals(goodId)){
							ProductsDTO dtto = new ProductsDTO();
							//尺码查询
							sql = "SELECT * FROM heso_product_size WHERE PRODUCT_ID = ? AND size = ? AND color_id = ?";
							argsList.clear();
							argsList.add(dto.getProdctId());
							argsList.add(dto.getSize());
							argsList.add(dto.getColor());
							DataTable sizedt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
							
							//工艺查询
							sql = "SELECT *FROM heso_product_gongyi WHERE PRODUCT_ID = ?";
							argsList.clear();
							argsList.add(dto.getProdctId());
							//工艺拼接
							DataTable gongyidt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
							String clothType = "";
							String gongyi = "";
							String gongyi_code = "";
							for(int j=0;j<gongyidt.getRows().size();j++){
								DataRow drt = gongyidt.getRows().get(j);
								String chanese = drt.getString("CN_DESC");
								String gongyiCode = drt.getString("GONGYI");
								String flag = drt.getString("IS_ZHIDING");
								String code = drt.getString("SYS_CODE");
								if("1".equals(flag)){
									String zhiding = drt.getString("ZHIDING");
									code = code+":"+zhiding;
									gongyiCode = gongyiCode +" "+zhiding;
								}
								gongyi = gongyi + gongyiCode +" "+chanese +";";
								gongyi_code = gongyi_code + code +",";
								clothType = dr.getString("PINLEI");
							}
							pinleiList.add(clothType);
							//------组合尺码
							String chicunString = "0";
							//下单方式
							String xiadanfangshi = sizedt.getRows().get(0).getString("BIAOZHUNHAOXING");
							String biaozhunhao = "";
							String beizhu = sizedt.getRows().get(0).getString("CHIMA_DESC");
							//胸围
							String xiongwei = "0";
							//腰围
							String yaowei = "0";
							//臀围
							String tunwei= "0";
							//肩宽
							String jiankuan = "0";
							//袖肥
							String xiufei = "0";
							//后衣长
							String houyichang = "0";
							//袖长
							String xiuchang = "0";
							//领围
							String lingwei = "0";
							//袖口
							String xiukou = "0";
							//通档
							String tongdang = "0";
							//横档
							String hengdang = "0";
							//脚口
							String jiaokou = "0";
							//裙长
							String qunchang = "0";
							//下摆
							String xiabai = "0";
							String kuchang = "0";
							//女西服
							if("95000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//胸围
									xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
									//腰围
									yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
									//臀围
									tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
									//肩宽
									jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
									//袖肥
									xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
									//后衣长
									houyichang = sizedt.getRows().get(0).getString("CLOTHES");
									//袖长
									xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
								}
								
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "35012:"+xiongwei+",35013:"+yaowei+",35014:"+tunwei+",35015:"+jiankuan+",35016:"+xiufei+",35017:"+houyichang+",35018:"+xiuchang+",35019:"+xiuchang;
							}
							//女西裤
							if("98000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//脚口
									jiaokou = sizedt.getRows().get(0).getString("FOOT_WIDE");
									//腰围
									yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
									//臀围
									tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
									//横档
									hengdang = sizedt.getRows().get(0).getString("HENGDANG");
									//通档
									tongdang = sizedt.getRows().get(0).getString("TONGDANG");
									//裤长
									kuchang = sizedt.getRows().get(0).getString("PANTS");
									 
								}
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "35244:"+jiaokou+",35013:"+yaowei+",35014:"+tunwei+",35246:"+hengdang+",35245:"+tongdang+",35247:"+kuchang+",35248:"+kuchang ;
							}
							//女衬衣
							if("11000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//胸围
									xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
									//袖口
									xiukou= sizedt.getRows().get(0).getString("SLEEVE_WIDE");
									//肩宽
									jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
									//袖肥
									xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
									//后衣长
									houyichang = sizedt.getRows().get(0).getString("CLOTHES");
									//袖长
									xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
									//领围
									lingwei = sizedt.getRows().get(0).getString("LINGWEI");
									
								}
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "35255:"+xiongwei+",35258:"+jiankuan+",35259:"+xiufei+",35262:"+houyichang+",35263:"+xiuchang+",35264:"+xiuchang +",35260:"+xiukou+",35261:"+xiukou+",35254:"+lingwei;
							}
							//女大衣
							if("11000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//胸围
									xiongwei = sizedt.getRows().get(0).getString("BUST_BEGIN");
									//腰围
									yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
									//臀围
									tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
									//肩宽
									jiankuan = sizedt.getRows().get(0).getString("SHOULDER");
									//袖肥
									xiufei = sizedt.getRows().get(0).getString("SLEEVE_WIDE");
									//后衣长
									houyichang = sizedt.getRows().get(0).getString("CLOTHES");
									//袖长
									xiuchang = sizedt.getRows().get(0).getString("SLEEVE");
									
								}
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "35678:"+xiongwei+",35679:"+yaowei+",35680:"+tunwei+",35681:"+jiankuan+",35682:"+xiufei+",35683:"+houyichang+",35684:"+xiuchang+",35685:"+xiuchang;
							}
							//女马甲
							if("11000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//腰围
									yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
									//后衣长
									houyichang = sizedt.getRows().get(0).getString("CLOTHES");
								}
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "661057:"+yaowei+",661058:"+houyichang;
							}
							//女西裙
							if("11000".equals(clothType)){
								if(!"206051".equals(xiadanfangshi)){
									//腰围
									yaowei = sizedt.getRows().get(0).getString("WAIST_BEGIN");
									//臀围
									tunwei= sizedt.getRows().get(0).getString("HIP_BEGIN");
									//裙长
									qunchang = sizedt.getRows().get(0).getString("SKIRT");
									//下摆
									xiabai= sizedt.getRows().get(0).getString("XIABAI");
								}
								biaozhunhao = sizedt.getRows().get(0).getString("CHIMALEIXING");
								chicunString = "130032:"+yaowei+",130033:"+tunwei+",130034:"+qunchang+",130047:"+xiabai;
							}
							
							//商品查询
							sql  = "SELECT * FROM heso_product WHERE PRODUCT_ID = ?";
							argsList.clear();
							argsList.add(dto.getProdctId());
							DataTable productdt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
							String productName = productdt.getRows().get(0).getString("");
							String pprice = productdt.getRows().get(0).getString("price");
							String ptype = productdt.getRows().get(0).getString("type");
							String pproductId = productdt.getRows().get(0).getString("PRODUCT_ID");
							String pstock = productdt.getRows().get(0).getString("STOCK_COUNT");
							String pname = productdt.getRows().get(0).getString("name");
							String pimage = productdt.getRows().get(0).getString("img_front");
							dtto.setPrice(pprice);
							dtto.setProdctId(pproductId);
							dtto.setImage(pimage);
							dtto.setStock(pstock);
							dtto.setType(ptype);
							dtto.setProName(pname);
							dtto.setChangdu("");
							dtto.setWeidu("");
							dtto.setGongyi(gongyi_code);
							dtto.setXiadangfangshi(xiadanfangshi);
							dtto.setChimaquyu(biaozhunhao);
							dtto.setChima(chicunString);
							dtto.setGongyiCn(gongyi);
							dtto.setPinlei(clothType);
							dtto.setColor(dto.getColor());
							dtto.setSize(dto.getSize());
							dtto.setCount(dto.getCount());
						}
					}
					
				}
				
					  
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
			 
			
			
			return list;
		}
		
	
	
	public      ClothPrice findPirceByMianliao(String mianliao,String pinlei,String cloth,String length){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		double price = 0;
		double price_cost = 0;
		double price_cost_sum = 0;
		String sql = "";
		ClothPrice clothPrice = new ClothPrice();
		ArrayList<Object> argsList = new ArrayList<Object>();
		sql = "select hc.cloth_number, hcp.price_cost,hcp.price_cost_sum,hcp.price FROM	heso_cloth as hc,heso_cloth_price as hcp " +
				"where hc.cloth_number = ? and hcp.cloh_id = hc.id and hcp.category = ? and hcp.cloth = ? ";
		try {
			if(pinlei.equals("2")){
				pinlei = "1";
				argsList.add(mianliao);
				argsList.add("4000");
				argsList.add("0");
				DataTable dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
				price = dtt.getRows().get(0).getDouble("price");
				price_cost = dtt.getRows().get(0).getDouble("price_cost");
				price_cost_sum = dtt.getRows().get(0).getDouble("price_cost_sum");
			}
			if(pinlei.equals("1")){
				argsList.clear();
				argsList.add(mianliao);
				argsList.add("1");
				argsList.add(cloth);
				DataTable dtt1 = DatabaseMgr.getInstance().execSql(sql, argsList);
				if(dtt1.getRows().size()!=0){
 					price = price + dtt1.getRows().get(0).getDouble("price");
					price_cost = price_cost + dtt1.getRows().get(0).getDouble("price_cost");
					price_cost_sum = price_cost_sum + dtt1.getRows().get(0).getDouble("price_cost_sum");
					clothPrice.setCloth(mianliao);
					clothPrice.setPrice(price+"");
					clothPrice.setPrice_cost(price_cost+"");
					clothPrice.setPrice_cost_sum(price_cost_sum+"");
					return clothPrice;
					
				}
				argsList.clear();
				argsList.add(mianliao);
				argsList.add("3");
				argsList.add(cloth);
				DataTable dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
				price = price + dtt.getRows().get(0).getDouble("price");
				price_cost = price_cost + dtt.getRows().get(0).getDouble("price_cost");
				price_cost_sum = price_cost_sum + dtt.getRows().get(0).getDouble("price_cost_sum");
				argsList.clear();
				argsList.add(mianliao);
			    argsList.add("2000");
				argsList.add("0");
			    dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
				price = price + dtt.getRows().get(0).getDouble("price");
				price_cost = price_cost + dtt1.getRows().get(0).getDouble("price_cost");
				price_cost_sum = price_cost_sum +  dtt1.getRows().get(0).getDouble("price_cost_sum") ;
				clothPrice.setCloth(mianliao);
				clothPrice.setPrice(price+"");
				clothPrice.setPrice_cost(price_cost+"");
				clothPrice.setPrice_cost_sum(price_cost_sum+"");
				return clothPrice;
			}
			if(pinlei.equals("7")){
				argsList.clear();
				argsList.add(mianliao);
				argsList.add("7");
				argsList.add(cloth);
				DataTable dtt1 = DatabaseMgr.getInstance().execSql(sql, argsList);
				if(dtt1.getRows().size()!=0){
 					price = price +  dtt1.getRows().get(0).getDouble("price") ;
 					price_cost = price_cost + dtt1.getRows().get(0).getDouble("price_cost");
 					price_cost_sum = price_cost_sum +  dtt1.getRows().get(0).getDouble("price_cost_sum") ;
					clothPrice.setCloth(mianliao);
					clothPrice.setPrice(price+"");
					clothPrice.setPrice_cost(price_cost+"");
					clothPrice.setPrice_cost_sum(price_cost_sum+"");
					return clothPrice;
					
				}
				argsList.clear();
				argsList.add(mianliao);
				argsList.add("95000");
				argsList.add("0");
				DataTable dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
				price = price + dtt.getRows().get(0).getDouble("price");
				price_cost = price_cost + dtt.getRows().get(0).getDouble("price_cost");
				price_cost_sum = price_cost_sum + dtt.getRows().get(0).getDouble("price_cost_sum");
				argsList.clear();
				argsList.add(mianliao);
			    argsList.add("98000");
				argsList.add("0");
			    dtt = DatabaseMgr.getInstance().execSql(sql, argsList);
				price = price + dtt.getRows().get(0).getDouble("price");
				price_cost = price_cost + dtt.getRows().get(0).getDouble("price_cost");
				price_cost_sum = price_cost_sum + dtt.getRows().get(0).getDouble("price_cost_sum");
				clothPrice.setCloth(mianliao);
				clothPrice.setPrice(price+"");
				clothPrice.setPrice_cost(price_cost+"");
				clothPrice.setPrice_cost_sum(price_cost_sum+"");
				return clothPrice;
			}
			argsList.clear();
			argsList.add(mianliao);
			argsList.add(pinlei);
			argsList.add(cloth);
		
			if(pinlei.equals("103000")||pinlei.equals("6000")){
				sql = sql + " and hcp.length = ?";
				argsList.add(length);
			}
		

		
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() == 0)
				throw new Exception("100300");
			double pprice = dt.getRows().get(0).getDouble("price");
			double pprice_cost = dt.getRows().get(0).getDouble("price_cost");
			double pprice_cost_sum = dt.getRows().get(0).getDouble("price_cost_sum");
			double ppprice  = pprice+price;
			double ppprice_cost = pprice_cost + price_cost;
			double ppprice_cost_sum = pprice_cost_sum + price_cost_sum;
			clothPrice.setCloth(dt.getRows().get(0).getString("cloth_number"));
			clothPrice.setPrice(ppprice+"");
			clothPrice.setPrice_cost(ppprice_cost+"");
			clothPrice.setPrice_cost_sum(ppprice_cost_sum+"");
		
			
		} catch (Exception e) {
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		} 
	
	
		return clothPrice;
	}
	
	 public  String updateOrderComsume (String orderId,String checkStatus,String remarks,String chimaquyu,String chicunfenlei){
			try {
				DatabaseMgr dbm = DatabaseMgr.getInstance();
				Connection conn = dbm.getConnection();
				
				String sql = " UPDATE heso_order_consume SET check_status=?,remarks =? ,CHICUNFENLEI=?,CHIMAQUYU=? WHERE  ORDER_ID = ?";
				List<Object> argsList = new ArrayList<Object>();
				argsList.add(checkStatus);
				argsList.add(remarks);
				argsList.add(orderId);
				argsList.add(chimaquyu);
				argsList.add(chicunfenlei);
				if(dbm.execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101122");
			}
			 catch (Exception e) {
					// TODO: handle exception
					//coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
					logger.error(e.getMessage());
					e.printStackTrace();
				}finally{
					
				}
				return "2";
			}
	 
	 
	  
	//下单至阿玛尼
	
	public    String orderToAmani(String orderId,String checkStatus,String remarks,String chimaquyu,String chicunfenlei,String account,String name,String phone){ 
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		 
		try {
			
		
			String sql = " UPDATE heso_order_consume SET check_status=?,remarks =? ,CHICUNFENLEI=?,CHIMAQUYU=? WHERE  ORDER_ID = ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.add(checkStatus);
			argsList.add(remarks);
			argsList.add(orderId);
			argsList.add(chimaquyu); 
			argsList.add(chicunfenlei);
		/*	没有更新数据*/
		 if(dbm.execNonSqlTrans(sql, argsList, conn) <= 0)
				throw new Exception("101122");
			Orderinformation orderinformation = new Orderinformation();
			CustomerInformation customerInformation = new CustomerInformation();
			List<AmaniOrderdetail> detaillist = new ArrayList<AmaniOrderdetail>();
			sql = "SELECT * FROM heso_order_consume WHERE ORDER_ID = ?";
			argsList.clear();
			argsList.add(orderId);
			DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);

			String pinlei = dtt.getRows().get(0).getString("pinlei");
			String mianliao = dtt.getRows().get(0).getString("cloth_type");
			String teti = dtt.getRows().get(0).getString("teti_code");
			orderinformation.setClothingid(pinlei);
			orderinformation.setSizecategoryid(chicunfenlei);
			orderinformation.setAreaid(chimaquyu);
			orderinformation.setFabirc(mianliao);
			orderinformation.setCustormerbody(teti);
			orderinformation.setClothingstyle("20100");
			orderinformation.setRemark("");
			orderinformation.setOrderno("00000000898989");
			orderinformation.setAmount("1");
			//身材数据
			sql = "SELECT * FROM heso_account_profiles WHERE account =  ?";
			argsList.clear();
			argsList.add(account);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			String height = dt.getRows().get(0).getInt("height")+"";
			String sex = dt.getRows().get(0).getString("sex");
			String weight = dt.getRows().get(0).getString("weight");
			String heightunited = "10266";//身高单位cm
			String weightunited = "10261";//体重单位kg
			String email = "";
			String address = "";
		 
			String memos = "";//客户备注
			customerInformation.setHeight(height);
			customerInformation.setGender(sex);
			customerInformation.setWeight(weight);
			customerInformation.setHeightunited(heightunited);
			customerInformation.setWeightunited(weightunited);
			customerInformation.setEmail(email);
			customerInformation.setAddress(address);
			customerInformation.setTel(phone);
			customerInformation.setMemos(memos);
			
			//查询订单详情，组成订单信息
			sql = "SELECT * FROM heso_order_consume_detail WHERE ORDER_ID = ?";
			argsList.clear();
			argsList.add(orderId);
			DataTable dttt = dbm.execSqlTrans(sql, argsList, conn);
			String biaozhunhao = ""; 
			String biaozhunhao_code = "";
			String gongyi = "";
			String shenti = "";
			String style = "10284";
			String  detailpinlei = "";
			for(int i = 0 ;i<dttt.getRows().size(); i++){
				AmaniOrderdetail amaniOrderdetail = new AmaniOrderdetail();
				gongyi = dttt.getRows().get(i).getString("system_code");
				shenti = dttt.getRows().get(i).getString("body_size");
				detailpinlei = dttt.getRows().get(i).getString("cloth_type");
				amaniOrderdetail.setBodystyle(style);
				amaniOrderdetail.setCategoryid(detailpinlei);
				amaniOrderdetail.setPartsize(shenti);
				amaniOrderdetail.setSizespecheight(biaozhunhao);
				amaniOrderdetail.setOrdersprocess(gongyi);
				if("10054".equals(chicunfenlei)){
					biaozhunhao = dttt.getRows().get(i).getString("biaozhunsize");
					biaozhunhao_code = dtt.getRows().get(0).getString("biaozhun_code");
					amaniOrderdetail.setSizespecheight(biaozhunhao);
					amaniOrderdetail.setPartsize(biaozhunhao_code);
				}
				detaillist.add(amaniOrderdetail);
			}
			String xmlString = new PackXmlToAmani().packXml(orderinformation, customerInformation, detaillist); 
			String ss = new PackXmlToAmani().post("http://api.rcmtm.cn/api/order/submit", xmlString);
			System.out.println(ss);
			String ssString = "";
		} catch (Exception e) {
			// TODO: handle exception
			//coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "2";
	}
	
	
	public  ConsumeOrderReturnObject innerOrder2(ArrayList<ConsumeOrderObject> coaoList,String account,String innerCoin,String receiveId,
			String paymentTerms,String recommend,String quanma){

	    WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String couponDetId = "";
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null; 
		try {
			conn = dbm.getConnection();
			String ri = receiveId;
			String sql = "SELECT * FROM heso_account_recvinfo WHERE id= ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.clear();
			argsList.add(receiveId);
			DataTable dti = dbm.execSqlTrans(sql, argsList, conn);
			String reciveName = dti.getRows().get(0).getString("name");
			String recivePhone = dti.getRows().get(0).getString("mobile");
			String reciveAdd = dti.getRows().get(0).getString("address");
			String regoin_id = dti.getRows().get(0).getString("region_id");
			//conn.setAutoCommit(false);
			StringBuffer orderId = new StringBuffer();
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				sql = "select * from heso_account where account = ?";
				argsList.clear();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
					throw new Exception("000150");
				sql = "SELECT COST_PRICE,SUIT_PRICE FROM heso_product WHERE PRODUCT_ID =?";
				argsList.clear();
				argsList.add(coao.getProductId());
				DataTable dtr = dbm.execSqlTrans(sql, argsList, conn);
				String costPrice = dtr.getRows().get(0).getString("COST_PRICE");
				String disPrice = dtr.getRows().get(0).getString("SUIT_PRICE");
				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image," +
						"color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , " +
						"payment_terms ,from_suit ,recommend, couponDet_Id,channel_type,format_type," +
						"size_type,style_type,cloth_type,seller,remark,price_cost,price_cost_sum,pinlei," +
						"category,DELIVERY_ADDRESS,DELIVERY_NAME,DELIVERY_PHONE,ROGION_ID,CHANGDU," +
						"SELLER_NAME,QUDAOJINGLI_ID,QUDAOJINGLI_NAME,QUDAO_ID,QUDAO_NAME,TEACHER_ID,TEACHER_NAME,DISCOUNT_PRICE) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(coao.getAccount());
				argsList.add(coao.getProductId());
				argsList.add(coao.getType());
				argsList.add(coao.getName());
				argsList.add(coao.getImage());
				argsList.add(coao.getColor());
				argsList.add(coao.getSize());
				argsList.add(coao.getPrice());
				argsList.add(coao.getCount());
				argsList.add(coao.getAmount());
				argsList.add(coao.getCurrency());
				argsList.add(coao.getInnerCoin());
				argsList.add(coao.getBonusPoint());
				argsList.add(receiveId);
				argsList.add(paymentTerms);//支付方式
				argsList.add(coao.getSuitId());
				argsList.add(recommend);//推荐人
				argsList.add(couponDetId);
				//20171122
				argsList.add(coao.getChannelType());
				argsList.add(coao.getFormatType());
				argsList.add(coao.getSizeType());
				argsList.add(coao.getStyleType()); 
				argsList.add(coao.getClothType());
				argsList.add(coao.getSeller());
				argsList.add(coao.getRemark());
				if(costPrice!=null&&!costPrice.equals("")&&!costPrice.equals("0")){
					argsList.add(costPrice);
					BigDecimal cost = new BigDecimal(costPrice);
					BigDecimal countD = new BigDecimal(coao.getCount());
					BigDecimal costSum = cost.multiply(countD);
					argsList.add(costSum);
				}else {
					argsList.add("");
					argsList.add("");
				}
				argsList.add(coao.getPinlei());
				argsList.add(coao.getCategory());
				argsList.add(reciveAdd);
				argsList.add(reciveName);
				argsList.add(recivePhone);
				argsList.add(regoin_id);
				
				argsList.add(coao.getChangduStyle());
				argsList.add(coao.getSellerName());
				argsList.add(coao.getQudaojingliId());
				argsList.add(coao.getQudaojingli());
				argsList.add(coao.getQudaoId());
				argsList.add(coao.getQudao());
				argsList.add(coao.getTeacherId());
				argsList.add(coao.getTeacher());
				argsList.add(disPrice);
				/*if(costPrice!=null){
					argsList.add(costPrice);
				}else {
					argsList.add("");
				}*/
				//
				int rows = dbm.execNonSql(sql, argsList);
				if (rows <= 0)
					throw new Exception("100150");
				coro.setOrderId(seqId);

				ArrayList<ConsumeProductObject> copList = coao.getCpoList();
				if(copList != null){
					for (ConsumeProductObject cpo : copList) {
						
						sql = "SELECT * FROM heso_product_gongyi WHERE PRODUCT_ID = ?";
						argsList.clear();
						argsList.add(cpo.getProductId());
						DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn);
						String clothType = "";
						String gongyi = "";
						String gongyi_code = "";
						for(int i=0;i<dtt.getRows().size();i++){
							DataRow dr = dtt.getRows().get(i);
							String chanese = dr.getString("CN_DESC");
							String gongyiCode = dr.getString("GONGYI");
							String flag = dr.getString("IS_ZHIDING");
							String code = dr.getString("SYS_CODE");
							if("1".equals(flag)){
								String zhiding = dr.getString("ZHIDING");
								code = code+":"+zhiding;
								gongyiCode = gongyiCode +" "+zhiding;
							}
							gongyi = gongyi + gongyiCode +" "+chanese +";";
							gongyi_code = gongyi_code + code +",";
							clothType = dr.getString("PINLEI");
						}
						if("3".equals(clothType)&&"1".equals(quanma)){
							gongyi = gongyi +";000A 全麻衬";
							gongyi_code = gongyi_code + ",432";
							
						}
						sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(seqId);
						argsList.add(cpo.getProductId());
						argsList.add(cpo.getName());
						argsList.add(cpo.getImage());
						argsList.add(cpo.getColor());
						argsList.add(cpo.getSize());
						argsList.add(cpo.getCount());
						argsList.add(cpo.getPrice());
						argsList.add(cpo.getAmount());
						argsList.add(cpo.getSuitPrice());
						argsList.add(cpo.getSuitPromotion());
						argsList.add("0");
						argsList.add(gongyi_code);
						argsList.add(gongyi);
						argsList.add("0");
						argsList.add(clothType);
						argsList.add("0");
						argsList.add("0");
						argsList.add(cpo.getWeidu());
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						//
						rows = dbm.execNonSql(sql, argsList);
						if (rows <= 0)
							throw new Exception("100151");
					}
				}else {
					sql = "SELECT * FROM heso_product_gongyi WHERE PRODUCT_ID = ?";
					argsList.clear();
					argsList.add(coao.getProductId());
					DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList,conn);
					String clothType = "";
					String gongyi = "";
					String gongyi_code = "";
					for(int i=0;i<dtt.getRows().size();i++){
						DataRow dr = dtt.getRows().get(i);
						String chanese = dr.getString("CN_DESC");
						String gongyiCode = dr.getString("GONGYI");
						String flag = dr.getString("IS_ZHIDING");
						String code = dr.getString("SYS_CODE");
						if("1".equals(flag)){
							String zhiding = dr.getString("ZHIDING");
							code = code+":"+zhiding;
							gongyiCode = gongyiCode +" "+zhiding;
						}
						gongyi = gongyi + gongyiCode +" "+chanese +";";
						gongyi_code = gongyi_code + code +",";
						clothType = dr.getString("PINLEI");
					}
					sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//	sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(seqId);
					argsList.add(coao.getProductId());
					argsList.add(coao.getName());
					argsList.add(coao.getImage());
					argsList.add(coao.getColor());
					argsList.add(coao.getSize());
					argsList.add(coao.getCount());
					argsList.add(coao.getPrice());
					argsList.add(coao.getAmount());
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add(gongyi_code);
					argsList.add(gongyi);
					argsList.add("0");
					argsList.add(clothType);
					argsList.add("0");
					argsList.add("0");
					argsList.add(coao.getWeidu());
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");

					argsList.add("0");
					//
					rows = dbm.execNonSql(sql, argsList);
				}
				
				
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(seqId);
				coo.setPaymentTerms(paymentTerms);
				total  = total.add( new BigDecimal(coao.getAmount())); 
				
				if(!coao.getProductId().contains("SPE")){
					notSPtotal = notSPtotal.add(new BigDecimal(coao.getAmount()));
				}
				
				cooList.add(coo);
				orderId.append(seqId+";");
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
		return  coro;
		
	}
	
	

	public  ConsumeOrderReturnObject innerOrder4(ArrayList<ConsumeOrderObject> coaoList,String account,String innerCoin,String receiveId,
			String paymentTerms,String recommend){

	    WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String couponDetId = "";
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = null; 
		String orderId = "";
		try {
			conn = dbm.getConnection();
			String ri = receiveId;
			String sql = "SELECT * FROM heso_account_recvinfo WHERE id= ?";
			List<Object> argsList = new ArrayList<Object>();
			argsList.clear();
			argsList.add(receiveId);
			DataTable dti = dbm.execSqlTrans(sql, argsList, conn);
			String reciveName = dti.getRows().get(0).getString("name");
			String recivePhone = dti.getRows().get(0).getString("mobile");
			String reciveAdd = dti.getRows().get(0).getString("address");
			String regoin_id = dti.getRows().get(0).getString("region_id");
			//conn.setAutoCommit(false);
			List<String> orderIds = new ArrayList<String>();
			ArrayList<ConsumeOrderObject> cooList = new ArrayList<ConsumeOrderObject>();
			BigDecimal total = new BigDecimal(0) ;
			BigDecimal notSPtotal = new BigDecimal(0) ;//非特价商品总额，不在优惠券优惠范围
			for (ConsumeOrderObject coao : coaoList) {
				// 获取用户账户信息
				sql = "select * from heso_account where account = ?";
				argsList.clear();
				argsList.add(coao.getAccount());
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				if (dt.getRows().size() == 0)
					throw new Exception("100100");

				// 取序列
				String seqId = dbm.getSequence("seq_order", "16");
				orderIds.add(seqId);
				if (seqId.isEmpty())
						throw new Exception("000150");
				sql = "SELECT COST_PRICE FROM heso_product WHERE PRODUCT_ID =?";
				argsList.clear();
				argsList.add(coao.getProductId());
				DataTable dtr = dbm.execSqlTrans(sql, argsList, conn);
				String costPrice = dtr.getRows().get(0).getString("COST_PRICE");
				
				sql = "insert into heso_order_consume(order_id, account, product_Id,type,name,image," +
						"color,size,price,count,amount, currency, inner_coin, bonus_point, receive_id , " +
						"payment_terms ,from_suit ,recommend, couponDet_Id,channel_type,format_type," +
						"size_type,style_type,cloth_type,seller,remark,price_cost,price_cost_sum,pinlei," +
						"category,DELIVERY_ADDRESS,DELIVERY_NAME,DELIVERY_PHONE,ROGION_ID,CHANGDU," +
						"SELLER_NAME,QUDAOJINGLI_ID,QUDAOJINGLI_NAME,QUDAO_ID,QUDAO_NAME,TEACHER_ID," +
						"TEACHER_NAME,CHICUNFENLEI,CHIMAQUYU,TETI_CODE,CHENGYITETI,DISCOUNT_PRICE) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(coao.getAccount());
				argsList.add(coao.getProductId());
				argsList.add(coao.getType());
				argsList.add(coao.getName());
				argsList.add(coao.getImage());
				argsList.add(coao.getColor());
				argsList.add(coao.getSize());
				argsList.add(coao.getPrice());
				argsList.add(coao.getCount());
				argsList.add(coao.getAmount());
				argsList.add(coao.getCurrency());
				argsList.add(coao.getInnerCoin());
				argsList.add(coao.getBonusPoint());
				argsList.add(receiveId);
				argsList.add(paymentTerms);//支付方式
				argsList.add(coao.getSuitId());
				argsList.add(recommend);//推荐人
				argsList.add(couponDetId);
				//20171122
				argsList.add(coao.getChannelType());
				argsList.add(coao.getFormatType());
				argsList.add(coao.getSizeType());
				argsList.add(coao.getStyleType()); 
				argsList.add(coao.getClothType());
				argsList.add(coao.getSeller());
				argsList.add(coao.getRemark());
				if(costPrice!=null&&!costPrice.equals("")&&!costPrice.equals("0")){
					argsList.add(costPrice);
					BigDecimal cost = new BigDecimal(costPrice);
					BigDecimal countD = new BigDecimal(coao.getCount());
					BigDecimal costSum = cost.multiply(countD);
					argsList.add(costSum);
				}else {
					argsList.add("");
					argsList.add("");
				}
				argsList.add(coao.getPinlei());
				argsList.add(coao.getCategory());
				argsList.add(reciveAdd);
				argsList.add(reciveName);
				argsList.add(recivePhone);
				argsList.add(regoin_id);
				
				argsList.add("20100");
				argsList.add(coao.getSellerName());
				argsList.add(coao.getQudaojingliId());
				argsList.add(coao.getQudaojingli());
				argsList.add(coao.getQudaoId());
				argsList.add(coao.getQudao());
				argsList.add(coao.getTeacherId());
				argsList.add(coao.getTeacher());
				String chicunfenlei = "";
				if("206051".equals(coao.getXiadanfangshi())){
					chicunfenlei = "10054";
				}else {
					chicunfenlei = coao.getXiadanfangshi(); 
				} 
				argsList.add(chicunfenlei);
				argsList.add(coao.getXiadanfangshi());
				argsList.add("10087,10088,10090,10091,10092,205867,205875,205881");
				argsList.add("10085,10086,223142,223149,10097,223156,223159,223163,10368");
				argsList.add(coao.getDiscountPrice());
				/*if(costPrice!=null){
					argsList.add(costPrice);
				}else {
					argsList.add("");
				}*/
				//
				int rows = dbm.execNonSql(sql, argsList);
				if (rows <= 0)
					throw new Exception("100150");
				coro.setOrderId(seqId);

				ArrayList<ConsumeProductObject> copList = coao.getCpoList();
				if(copList != null){
					for (ConsumeProductObject cpo : copList) {
						 
						
						sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						argsList.clear();
						argsList.add(seqId);
						argsList.add(cpo.getProductId());
						argsList.add(cpo.getName());
						argsList.add(cpo.getImage());
						argsList.add(cpo.getColor());
						argsList.add(cpo.getSize());
						argsList.add(cpo.getCount());
						argsList.add(cpo.getPrice());
						argsList.add(cpo.getAmount());
						argsList.add(cpo.getSuitPrice());
						argsList.add(cpo.getSuitPromotion());
						argsList.add("0");
						argsList.add(cpo.getGongyi());
						argsList.add(cpo.getGongyiCn());
						argsList.add("0");
						argsList.add(cpo.getPinlei());
						argsList.add(cpo.getBiaozhunhaochicun());
						if(chicunfenlei!=null&&"10054".equals(chicunfenlei)){
							argsList.add(cpo.getLiangti());
						}else {							
							argsList.add("0");
						}
						argsList.add("10284");
						if(chicunfenlei!=null&&"10053".equals(chicunfenlei)){
							argsList.add(cpo.getLiangti());
						}else {							
							argsList.add("0");
						}
						argsList.add("0");
						argsList.add("0");
						argsList.add("0");
						argsList.add(cpo.getDetailRemark());
						//
						rows = dbm.execNonSql(sql, argsList);
						if (rows <= 0)
							throw new Exception("100151");
					}
				}else {
					sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//	sql = "insert into heso_order_consume_detail values(?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.clear();
					argsList.add(seqId);
					argsList.add(coao.getProductId());
					argsList.add(coao.getName());
					argsList.add(coao.getImage());
					argsList.add(coao.getColor());
					argsList.add(coao.getSize());
					argsList.add(coao.getCount());
					argsList.add(coao.getPrice());
					argsList.add(coao.getAmount());
					argsList.add(coao.getDiscountPrice());
					argsList.add("0");
					argsList.add("0");
					argsList.add(coao.getGongyi());
					argsList.add(coao.getGongyiCn());
					argsList.add("0");
					argsList.add(coao.getPinlei());
					argsList.add(coao.getBiaozhunhaochicun());
					if(chicunfenlei!=null&&"10054".equals(chicunfenlei) ){
						argsList.add(coao.getLiangti());
					}else {							
						argsList.add("0");
					}
					argsList.add("10284");
					if(chicunfenlei!=null&&"10053".equals(chicunfenlei)){
						argsList.add(coao.getLiangti());
					}else {							
						argsList.add("0");
					}
					argsList.add("0");
					argsList.add("0");
					argsList.add("0");
					argsList.add(coao.getDetailRemark());
					//
					rows = dbm.execNonSql(sql, argsList);
				}
				
				
				ConsumeOrderObject coo = new ConsumeOrderObject();
				coo.setOrderId(seqId);
				coo.setPaymentTerms(paymentTerms);
				total  = total.add( new BigDecimal(coao.getAmount())); 
				
				if(!coao.getProductId().contains("SPE")){
					notSPtotal = notSPtotal.add(new BigDecimal(coao.getAmount()));
				}
				
				cooList.add(coo);
				orderId = orderId + seqId+";";
			}
			coro.setCooList(cooList);
			coro.setOrderId(orderId);
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
		return  coro;
		
	}
	//根据订单编号和品类修改订单详情工艺
	public   String updateGongyiToOrderdetail(String orderId,String prodductId,String gongyi_desc,String system_code){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		String code = "000000";
		try {
			//查询该订单是否已支付和已发货
			String sql = " ";
			List<Object> argsList = new ArrayList<Object>();
			
			sql = " UPDATE heso_order_consume_detail SET GONGYI_DESC = ?,SYSTEM_CODE= ? WHERE ORDER_ID = ? AND PRODUCT_ID = ?";
			argsList.add(gongyi_desc);
			argsList.add(system_code);
			argsList.add(orderId);
			argsList.add(prodductId);
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0){
				code = "000001";
			}
				
			 
		} catch (Exception e) {
			// TODO: handle exception
 			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){
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
	
	
	public   ConsumeOrderReturnObject innerOrder(ArrayList<ConsumeOrderObject> coaoList,String account,String productId,String innerCoin,String receiveId,
			String paymentTerms,String recommend){
		
		//double amount = 0.00;
		//int count = 0;
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String couponDetId = "";
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = dbm.getConnection();
		// 建立订单对象
		ConsumeOrder order = new ConsumeOrder();
	
	
		
		/*double amount = 0.00;
		int count = 0;*/
		try {
			//新建用户详细信息表数据
			
			
			String ri = receiveId;			
			//conn.setAutoCommit(false);
			// 生成订单
			coro = order.genarate2(coaoList, innerCoin, receiveId,paymentTerms,recommend,couponDetId);
			
			String total = coro.getCooList().get(coro.getCooList().size()-2).getAmount();
			String notSPtotal = coro.getCooList().get(coro.getCooList().size()-1).getAmount();//非特价商品总价
			//首单优惠
			//ConsumeOrderReturnObject coro2 = order.firstPay(total, account,coro.getOrderId());//取消首单优惠 16/11/09
			//优惠券优惠
			//ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, coro2.getDiscount(), coro.getOrderId(), couponDetId, "1","");//从购物车支付时，不需要外单号
			ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, "0", coro.getOrderId(), couponDetId, "1","");
			//物流计费
			//ConsumeOrderReturnObject coro1 = order.logisticsPay(ri, Double.parseDouble(total),coro.getOrderId());
			
			Double pay = Double.parseDouble(total)-Double.parseDouble(cop.getDiscount());
			//Double logistics = Double.parseDouble(coro1.getReccount());
			coro.getCooList().get(coro.getCooList().size()-1).setAmount(String.valueOf(pay));
			//coro.getCooList().get(coro.getCooList().size()-1).setAmount(String.valueOf(pay+logistics));
		/*	String total = coro.getCooList().get(coro.getCooList().size()-2).getAmount();
			String notSPtotal = coro.getCooList().get(coro.getCooList().size()-1).getAmount();//非特价商品总价
		*/	//首单优惠 
			//ConsumeOrderReturnObject coro2 = order.firstPay(total, account,coro.getOrderId());//取消首单优惠 16/11/09
			//优惠券优惠
			//ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, coro2.getDiscount(), coro.getOrderId(), couponDetId, "1","");//从购物车支付时，不需要外单号
			
			//ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, "0", coro.getOrderId(), couponDetId, "1","");
			//物流计费
			//ConsumeOrderReturnObject coro1 = order.logisticsPay(ri, Double.parseDouble(total),coro.getOrderId());
			
			/*Double pay = Double.parseDouble(total)-Double.parseDouble(cop.getDiscount());
			Double logistics = Double.parseDouble(coro1.getReccount());
			
			coro.getCooList().get(coro.getCooList().size()-1).setAmount(String.valueOf(pay+logistics));
			*/
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return coro;
	}
	public   ConsumeOrderReturnObject innerOrder3(ArrayList<ConsumeOrderObject> coaoList,String account,String productId,String innerCoin,String receiveId,
			String paymentTerms,String recommend){
		
		//double amount = 0.00;
		//int count = 0;
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String couponDetId = "";
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		Connection conn = dbm.getConnection();
		// 建立订单对象
		ConsumeOrder order = new ConsumeOrder();
	
	
		
		/*double amount = 0.00;
		int count = 0;*/
		try {
			//新建用户详细信息表数据
			
			
			String ri = receiveId;			
		
			//conn.setAutoCommit(false);
			
			
			
			
			
			// 生成订单
			coro = order.genarate2(coaoList, innerCoin, receiveId,paymentTerms,recommend,couponDetId);
			
			String total = coro.getCooList().get(coro.getCooList().size()-2).getAmount();
			String notSPtotal = coro.getCooList().get(coro.getCooList().size()-1).getAmount();//非特价商品总价
			//首单优惠
			//ConsumeOrderReturnObject coro2 = order.firstPay(total, account,coro.getOrderId());//取消首单优惠 16/11/09
			//优惠券优惠
			//ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, coro2.getDiscount(), coro.getOrderId(), couponDetId, "1","");//从购物车支付时，不需要外单号
			ConsumeOrderReturnObject cop = order.couponDiscount(notSPtotal, account, "0", coro.getOrderId(), couponDetId, "1","");
			//物流计费
			//ConsumeOrderReturnObject coro1 = order.logisticsPay(ri, Double.parseDouble(total),coro.getOrderId());
			
			Double pay = Double.parseDouble(total)-Double.parseDouble(cop.getDiscount());
			//Double logistics = Double.parseDouble(coro1.getReccount());
			coro.getCooList().get(coro.getCooList().size()-1).setAmount(String.valueOf(pay));
			
		} catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return coro;
	}
	
	/**
	 * 查询使用优惠券优惠价格
	 * @param amount 非特价商品总价
	 * @param account
	 * @param orderId 为外单号，heso_order_pay的wai_orderId
	 * @param couponDetId
	 * @return
	 */
	public ConsumeOrderReturnObject couponDiscount(String amount,String account,String firstPayAmount,String orderId,String couponDetId,String type,String waiorderId){
		Connection conn = null;
		String sql = "";
		
		double total = 0;
		if(type.equals("1"))//购物车支付页面
			total = Double.parseDouble(amount)-Double.parseDouble(firstPayAmount);//该订单(非特价商品总额)减去首单优惠之后的金额
		else if(type.equals("2")) //订单支付页面
			total = Double.parseDouble(amount);//已扣除首单优惠
		
		List<Object> list = new ArrayList<Object>();
		conn = DatabaseMgr.getInstance().getConnection();
		ConsumeOrderReturnObject coro = new ConsumeOrderReturnObject();
		DecimalFormat df = new DecimalFormat("#.00");
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			if(type.equals("1")){//购物车里提交
				if(!couponDetId.trim().equals("")){
					if(couponDetId.contains(","))
						couponDetId = "'" + couponDetId.replace(",", "','") + "'";
					else
						couponDetId = "'" + couponDetId + "'";
					
					//检测是否有现金券是否和折扣券、满减券混合使用，新人券和普通优惠券均为现金券
					sql = " SELECT CASE WHEN COUPON_TYPE in (0,1) THEN 0 ELSE 1 END AS c_type FROM heso_coupon_gen where COUPONGEN_ID in ("
						+" select COUPONGEN_ID from heso_coupon_det where COUPONDET_ID IN ("+couponDetId+"))"
						+" GROUP BY CASE WHEN COUPON_TYPE in (0,1) THEN 0 ELSE 1 END;";
					list.clear();
					DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
					if(dt.getRows().size()>=2)
						throw new Exception("100151");
					
					if(dt.getRows().get(0).getInt("c_type")==0){//新人现金券和普通优惠现金券,这两种券可以同时使用
						
						sql = " SELECT SUM(COUPON_PRICE) PRICE FROM heso_coupon_gen where COUPONGEN_ID in ("
							+  " select COUPONGEN_ID from heso_coupon_det where COUPONDET_ID IN ("+couponDetId+")) ";
						list.clear();
						dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
						if(dt.getRows().size()==0)
							throw new Exception("100151");
						
						Double coupPrice = dt.getRows().get(0).getDouble("PRICE");
						
						if(coupPrice>total*0.3){//不超过非折扣商品总额的30%
							coupPrice = Double.parseDouble(df.format(total*0.3));
						}
						total = total - coupPrice;
						coro.setDiscount(String.valueOf(Double.parseDouble(firstPayAmount)+coupPrice));
						
						// 重新更新支付表和折扣表
						reUpdatePayAndDiscount(coro, orderId, account, coupPrice, conn);
						
						//-------------------更新每个订单表的优惠券优惠金额
						String originallyOrderId = getOriginalOrder(orderId, conn);
						
						sql = " SELECT * from heso_order_consume  where ORDER_ID in ('"+originallyOrderId+"') AND PRODUCT_ID NOT LIKE '%SPE%' ORDER BY AMOUNT ASC";//一定保证最小金额先分配优惠券金额,且排除掉特价商品
						list.clear();
						dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
						
						//当前提交订单中，商品数超过2时，进行优惠券金额分配：
						if(dt.getRows().size()>1){
							double avgCoupPrice = Double.parseDouble(df.format(coupPrice/dt.getRows().size()));//计算平均优惠券金额
							double lastCoupPrice = coupPrice;//剩余金额
							
							for(int i=0;i<dt.getRows().size();i++){
								String tempOrderId = dt.getRows().get(i).getString("ORDER_ID");//每个商品订单号
								double maxAmount = Double.parseDouble(df.format( dt.getRows().get(i).getDouble("AMOUNT")*0.3 ));//该商品最大优惠金额，四舍五入保留2位小数
								
								//更新订单表优惠券总优惠金额
								sql = " update heso_order_consume set coupon_amount=? where order_id=? and account=?";
								list.clear();
								
								if(maxAmount>avgCoupPrice){//商品最大优惠金额大于平均优惠金额，则该商品的优惠金额为平均优惠金额,否则优惠金额为该商品最大优惠金额。
									
									if(i!=dt.getRows().size()-1){//判断是否为最后一个商品，是则把剩余的优惠金额归入该商品中
										list.add(avgCoupPrice);
										lastCoupPrice = lastCoupPrice - avgCoupPrice;
									}else
										list.add(lastCoupPrice);
									
								}else{
									if(i!=dt.getRows().size()-1){
										list.add(maxAmount);
										lastCoupPrice = lastCoupPrice - maxAmount;
									}else
										list.add(lastCoupPrice);
								}
								
								list.add(tempOrderId);
								list.add(account);
								if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
									throw new Exception("100151");
								}
							}
							
						}else if(dt.getRows().size()==1){//当前购物车提交商品只有1个,直接更新
							updateOrderConsume(dt, coupPrice, account, conn);
						}
					}else{	//折扣券或是满减券
						
						sql = " SELECT COUPON_TYPE FROM heso_coupon_gen where COUPONGEN_ID in ("
						   +  " select COUPONGEN_ID from heso_coupon_det where COUPONDET_ID IN ("+couponDetId+")) ";
						list.clear();
						dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
						if(dt.getRows().size()==0||dt.getRows().size()>=2)  //找不到该优惠子券号或是折扣券和满减券同时使用、多张优惠券一起使用
							throw new Exception("100151");
						
						Double coupPrice = null;
						sql = " SELECT * FROM heso_coupon_gen where COUPONGEN_ID in ("
						   +  " select COUPONGEN_ID from heso_coupon_det where COUPONDET_ID IN ("+couponDetId+")) ";
						list.clear();
						dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
						
						int min_consumption = dt.getRows().get(0).getInt("MIN_CONSUMPTION");
						double discount_rate = dt.getRows().get(0).getDouble("DISCOUNT_RATE");
						double tmp_coupPrice = dt.getRows().get(0).getDouble("COUPON_PRICE");
						
						if(dt.getRows().get(0).getInt("COUPON_TYPE")==2){	//折扣券
							
							if(total<min_consumption){//该订单非折扣商品总金额不满足最低消费金额时，不打折
								coupPrice = 0.00;
							}else{	//折扣金额为 该订单非折扣商品总金额*（1-折扣率）,四舍五入保留2位
								coupPrice = Double.parseDouble(df.format(total*(1-discount_rate)));
							}
							
							total = total - coupPrice;
							coro.setDiscount(String.valueOf(Double.parseDouble(firstPayAmount)+coupPrice));
							
							// 重新更新支付表和折扣表
							reUpdatePayAndDiscount(coro, orderId, account, coupPrice, conn);
							
							//更新折扣券对应的子券实际折扣金额,此处的couponDetId一定是一张券
							sql = " update heso_coupon_det set DISCOUNT_PRICE=? where COUPONDET_ID IN ("+couponDetId+") ";
							list.clear();
							list.add(coupPrice);
							if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
								throw new Exception("100151");
							}
							
							//-------------------更新每个订单表的优惠券优惠金额
							String originallyOrderId = getOriginalOrder(orderId, conn);
							
							sql = " SELECT * from heso_order_consume  where ORDER_ID in ('"+originallyOrderId+"') AND PRODUCT_ID NOT LIKE '%SPE%' ORDER BY AMOUNT ASC";//一定保证最小金额先分配优惠券金额,且排除掉特价商品
							list.clear();
							dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
							
							//当前提交订单中，商品数超过2时，进行优惠券金额分配：
							if(dt.getRows().size()>1){
								double lastCoupPrice = coupPrice;//剩余金额
								double avgCoupPrice = 0.00;//每个商品折扣金额
								
								for(int i=0;i<dt.getRows().size();i++){
									String tempOrderId = dt.getRows().get(i).getString("ORDER_ID");//每个商品订单号
									
									//更新订单表优惠券总优惠金额
									sql = " update heso_order_consume set coupon_amount=? where order_id=? and account=?";
									list.clear();
									
									if(i!=dt.getRows().size()-1){//判断是否为最后一个商品，是则把剩余的优惠金额归入该商品中
										avgCoupPrice = Double.parseDouble(df.format(dt.getRows().get(i).getDouble("AMOUNT")*(1-discount_rate)));
										lastCoupPrice = lastCoupPrice - avgCoupPrice;
										list.add(avgCoupPrice);
									}else{
										list.add(lastCoupPrice);
									}
									
									list.add(tempOrderId);
									list.add(account);
									if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
										throw new Exception("100151");
									}
								}
								
							}else if(dt.getRows().size()==1){//当前购物车提交商品只有1个,直接更新
								updateOrderConsume(dt, coupPrice, account, conn);
							}
							
						}else if(dt.getRows().get(0).getInt("COUPON_TYPE")==3){	//满减券
							
							if(total<min_consumption){//该订单非折扣商品总金额不满足最低消费金额时，不满减
								coupPrice = 0.00;
							}else{	
								coupPrice = tmp_coupPrice;
							}
							
							total = total - coupPrice;
							coro.setDiscount(String.valueOf(Double.parseDouble(firstPayAmount)+coupPrice));
							
							// 重新更新支付表和折扣表
							reUpdatePayAndDiscount(coro, orderId, account, coupPrice, conn);
							//-------------------更新每个订单表的优惠券优惠金额
							String originallyOrderId = getOriginalOrder(orderId, conn);
							
							sql = " SELECT * from heso_order_consume  where ORDER_ID in ('"+originallyOrderId+"') AND PRODUCT_ID NOT LIKE '%SPE%' ORDER BY AMOUNT ASC";//一定保证最小金额先分配优惠券金额,且排除掉特价商品
							list.clear();
							dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
							
							//当前提交订单中，商品数超过2时，进行优惠券金额分配：
							if(dt.getRows().size()>1){
								double lastCoupPrice = coupPrice;//剩余金额
								double avgCoupPrice = 0.00;//每个商品单独优惠金额
								
								for(int i=0;i<dt.getRows().size();i++){
									String tempOrderId = dt.getRows().get(i).getString("ORDER_ID");//每个商品订单号
									
									//更新订单表优惠券总优惠金额
									sql = " update heso_order_consume set coupon_amount=? where order_id=? and account=?";
									list.clear();
									
									if(coupPrice==0){//不满足最低消费金额
										list.add(coupPrice);
									}else {
										if(i!=dt.getRows().size()-1){//判断是否为最后一个商品，是则把剩余的优惠金额归入该商品中
											avgCoupPrice = Double.parseDouble(df.format(dt.getRows().get(i).getDouble("AMOUNT")/(total+coupPrice)*coupPrice)); //此处total已经减去了coupPrice,所以要加上才是非折扣商品总价
											lastCoupPrice = lastCoupPrice - avgCoupPrice;
											list.add(avgCoupPrice);
										}else{
											list.add(lastCoupPrice);
										}
									}
									
									list.add(tempOrderId);
									list.add(account);
									if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) <= 0 ){
										throw new Exception("100151");
									}
								}
								
							}else if(dt.getRows().size()==1){//当前购物车提交商品只有1个,直接更新
								updateOrderConsume(dt, coupPrice, account, conn);
							}
						}
						
					}
					
				}else{//没有选择优惠券时：
					coro.setDiscount(String.valueOf(Double.parseDouble(firstPayAmount)));
				}
				
				coro.setReccount(String.valueOf(total));//非折扣商品金额-优惠券金额-首单
				
			}else if(type.equals("2")){//支付页面提交,因为页面不提交优惠券子表ID，需从数据库查询
				
				if(orderId.contains(";")){
					orderId = "'" + orderId.replace(";", "','") + "'";
				}else
					orderId = "'" + orderId + "'";
				
				sql = "SELECT * from heso_order_consume where ACCOUNT=? and ORDER_ID in (" + orderId +")";
				list.clear();
				list.add(account);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list,conn);
				if(dt.getRows().size()==0)
					throw new Exception("100151");
				
				//获取优惠券优惠金额,这里无需考虑特价商品的优惠金额，因为在购物车的时候已经分配好优惠券金额
				double coupPrice = 0;
				for(int i=0;i<dt.getRows().size();i++){
					coupPrice = coupPrice + dt.getRows().get(i).getDouble("COUPON_AMOUNT");
				}
				
				if(firstPayAmount.equals("0")||firstPayAmount.equals("")||firstPayAmount==null)
					coro.setDiscount(String.valueOf(coupPrice));
				else
					coro.setDiscount(String.valueOf(Double.parseDouble(firstPayAmount)+coupPrice));
				
				//重新更新支付表
				reUpdatePayAndDiscount(coro, waiorderId, account, coupPrice, conn);
				
				coro.setReccount(String.valueOf(total-coupPrice));//已减去首单优惠商品金额-优惠券金额
				
			}
			
			conn.commit();
			
		}catch (Exception e) {
			// TODO: handle exception
			coro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){}
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return coro;
	}
	
	/**
	 * 支付将订单商品添加进衣橱
	 * @param productId
	 * @param account
	 */
	public void addWardrobeByProductId(String productId ,String account){
		
		List<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
		Connection conn = null;
		ArrayList<String> list = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		String suitFlag = "1";
		ArrayList<Object> arglist = new ArrayList<Object>();
 		try{
			conn = DatabaseMgr.getInstance().getConnection();
			String sql = "SELECT PRODUCT_ID  FROM heso_order_consume WHERE ORDER_ID = ?";
			arglist.add(productId);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, arglist, conn);
			if(dt.getRows().size()>0){
				String suitProductId = dt.getRows().get(0).getString("PRODUCT_ID");
				list.add(suitProductId);
			}
			sql = "SELECT PRODUCT_ID   FROM heso_order_consume_detail WHERE ORDER_ID = ?";
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, arglist, conn);
			if(dt1.getRows().size()>0){
				for(int x= 0;x<dt1.getRows().size();x++){
					String detailProductId = dt1.getRows().get(x).getString("PRODUCT_ID");
					list.add(detailProductId);
				}
			}
			for(String id : list){
				WardrobeDTO wardrobeDTO = new WardrobeDTO();
				sql = "SELECT IMG_FRONT,TYPE,SCENE,STYLE,COLOR,CATEGORY FROM heso_product WHERE PRODUCT_ID = ?";
				arglist.clear();
				arglist.add(id);
				DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql, arglist, conn);
				wardrobeDTO.setImage(dt2.getRows().get(0).getString("IMG_FRONT"));
				wardrobeDTO.setColor(dt2.getRows().get(0).getString("COLOR"));
				wardrobeDTO.setSuit(dt2.getRows().get(0).getString("TYPE"));
				wardrobeDTO.setScene(dt2.getRows().get(0).getString("SCENE"));
				wardrobeDTO.setStyle(dt2.getRows().get(0).getString("STYLE"));
				wardrobeDTO.setType(dt2.getRows().get(0).getString("CATEGORY"));
				wardrobeDTO.setAccount(account);
				wardrobeDTO.setCharater("");
				wardrobeDTO.setCloth("");
				wardrobeDTO.setOutline("");
				wardrobeDTO.setPattern("");
				wardrobeDTO.setUplaod(account);
				wardrobeDTOs.add(wardrobeDTO);
			}
			new WardrobeService().addMyWardrobe(wardrobeDTOs);
				
		}catch (Exception e) {
			
			logger.error(e.getMessage());
			e.printStackTrace();
		}finally{
			if(conn != null){}
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * 把收货地址加入到订单表
	 * @param regionalId
	 * @param account
	 */
	public void updateLoadAdd(String regionalId ,String account ,List<String> orderIds){
		Connection conn = null;
		List<Object> list = new ArrayList<Object>();
		try {
			conn = DatabaseMgr.getInstance().getConnection();
			conn.setAutoCommit(false);
			
				StringBuffer sb = new StringBuffer("select  name , address , mobile , region_id from heso_account_recvinfo where id = ? and account = ? ");
				list.add(regionalId);
				list.add(account);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sb.toString(), list, conn);
				if(dt.getRows().size() <=0 ){
					 throw new Exception("101930");
				}
				sb.delete(0, sb.length());
					sb.append("update heso_order_consume set delivery_address = ? , delivery_name = ? , rogion_id = ?, delivery_phone = ? " +
							         "where account = ? and pay_status = '0' and order_id in( ? ");
					list.clear();
					list.add(dt.getRows().get(0).getString("address"));
					list.add(dt.getRows().get(0).getString("name"));
					list.add(dt.getRows().get(0).getString("region_id"));
					list.add(dt.getRows().get(0).getString("mobile"));
					list.add(account);
					list.add(orderIds.get(0));
					for(int i = 1 ;i<orderIds.size();i++){
						sb.append(", ?");
						list.add(orderIds.get(i));
					}
					sb.append(")");
					if(DatabaseMgr.getInstance().execNonSqlTrans(sb.toString(), list, conn) <= 0 ){
						throw new Exception("101930");
					}
					
			
		
			conn.commit();
		} catch (Exception e) {
			// TODO: handle exception
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
	}
	

	
	
	
	public static void main(String[] args) {
		/*ConsumeOrderReturnObject object = getDetail("0000000000000217");
		for(ConsumeOrderObject obj: object.getCooList()){
			
			System.out.println("===="+obj.getOrderId());
		}*/
		/*ConsumeOrderReturnObject object2 = innerOrder("in_0000000000000895", "shoojf", "1", "228", "1", "1", "衣服", "22", "2", "破的", "1", "11", 
				"0", "banshi", "dadada", "魔幻", "布", "545444646");
		System.out.println("====");
	*/
		try {
			//bookService("qqqqq", "11", "2", "2", "1111111", "2", "wu", "1,2");
			//addDesc("0000000000002949", "我是商家备注");
			String orderIds = "0000000000003252;0000000000003252;";
			String orderIds1 = "0000000000003279";
			//String string = subString("1", "1,1,2,3,4");
			/*ConsumeOrderReturnObject  wConsumeOrderReturnObject= getDetailsByWaiorder(orderIds1);
			if(orderIds.endsWith(";")){
				orderIds = orderIds.substring(0,orderIds.length()-1);
			}
			orderIds = orderIds.replace(";", ",");*/
 			
   			System.out.println(  "--"  );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}

