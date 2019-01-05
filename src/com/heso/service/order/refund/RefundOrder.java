package com.heso.service.order.refund;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.entity.CommonType;
import com.heso.db.DatabaseMgr;
import com.heso.service.funds.FundsService;
import com.heso.service.funds.entity.FundsServiceArgsObject;
import com.heso.service.funds.entity.FundsServiceReturnObject;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.refund.entity.RefundOrderObject;
import com.heso.service.order.refund.entity.RefundOrderReturnObject;
import com.heso.service.ordershow.OrderShowService;
import com.heso.utility.ErrorProcess;

import data.DataRow;
import data.DataTable;

public class RefundOrder {
	private static final Log logger = LogFactory.getLog(RefundOrder.class);

	/**
	 * 生成退货/退款/换货单
	 * 
	 * @param roo
	 * @return
	 */
	public RefundOrderReturnObject generate(RefundOrderObject roo) {
		RefundOrderReturnObject roro = new RefundOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			// 取序列
			String seqId = DatabaseMgr.getInstance().getSequence("seq_order", "16");
			if (seqId.isEmpty())
				throw new Exception("100150");

			// 检索订单表中可退货记录（已支付，已发货，未退货）
			String sql = "select * from heso_order_consume where order_id = ? and pay_status = 1 and send_status <> 0 ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(roo.getOrderId());
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			
			DataRow dr = dt.getRows().get(0);
			String account = dr.getString("account");
			String productType = dr.getString("type");
			String count = dt.getRows().get(0).getString("return_status");
			
			//检索套装的件数
			sql = "select count(*) count from heso_order_consume_detail where order_id = ? ";
			dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			
			if(productType.equals("1")){
				if(Integer.parseInt(count) >= 1){
					throw new Exception("101301");
				}
			}else{
				if(Integer.parseInt(count) >= dt.getRows().get(0).getInt("count")){
					throw new Exception("101301");
				}
			}
		
			if(Integer.parseInt(roo.getCount()) > dr.getInt("count")){
				throw new Exception("101304");
			}
			// 退单品或者套装（整套）
			if (roo.getProductId().equals(dr.getString("product_id")) && dr.getString("return_status").equals("0")) {
				sql = "insert into heso_order_refund(order_id, type, account, org_order_id, product_id, name, product_image, count, amount, comment, images,bank_name , bank_user_name , bank_care_no) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(roo.getType());
				argsList.add(account);
				argsList.add(roo.getOrderId());
				argsList.add(roo.getProductId());
				argsList.add(dr.getString("name"));
				argsList.add(dr.getString("image"));
				argsList.add(roo.getCount());
//				if(roo.getType().equals("3")){
					argsList.add("0");
//				}else{
//					argsList.add(dr.getString("amount"));
//				}
				argsList.add(roo.getComment());
				argsList.add(roo.getImages());
				argsList.add(roo.getBankName());
				argsList.add(roo.getBankUserName());
				argsList.add(roo.getBankCareNo());
				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101302");

				// 更新退货状态
				sql = "update heso_order_consume set return_status = 0 where order_id= ? ";
				argsList.clear();
				argsList.add(roo.getOrderId());
				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101303");

				// 如果是套装还需要更新明细表
				if (productType.equals("2")) {
					sql = "update heso_order_consume_detail set return_status = 4 where order_id=? ";
					argsList.clear();
					argsList.add(roo.getOrderId());
					if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
						throw new Exception("101303");
				}
			} else {// 退套装中的单品
				sql = "select * from heso_order_consume_detail where order_id=? and product_id=? and return_status <> 4 ";
				argsList.clear();
				argsList.add(roo.getOrderId());
				argsList.add(roo.getProductId());
				dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				// 订单主记录中找不到
				if (dt.getRows().size() <= 0)
					throw new Exception("101301");

				dr = dt.getRows().get(0);
				// 统计优惠价格
//				sql = "select sum(suit_promotion) from heso_order_consume_detail where order_id=? ";
//				argsList.clear();
//				argsList.add(roo.getOrderId());
//				float suitPromotion = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn).getRows().get(0).getFloat(0);

				// 生成订单
				sql = "insert into heso_order_refund(order_id, type, account, org_order_id, product_id, name, amount, comment, images,count,bank_name , bank_user_name , bank_care_no,product_image) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
				argsList.clear();
				argsList.add(seqId);
				argsList.add(roo.getType());
				argsList.add(account);
				argsList.add(roo.getOrderId());
				argsList.add(roo.getProductId());
				argsList.add(dr.getString("name"));
//				if(roo.getType().equals("3")){
					argsList.add("0");
//				}else{
//					argsList.add(dr.getFloat("amount") + suitPromotion);
//				}
				argsList.add(roo.getComment());
				argsList.add(roo.getImages());
				argsList.add(roo.getCount());
				argsList.add(roo.getBankName());
				argsList.add(roo.getBankUserName());
				argsList.add(roo.getBankCareNo());
				argsList.add(dr.getString("image"));
				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101302");

				// 更新套装主订单
//				sql = "update heso_order_consume set return_status = '4' where order_id= ? ";
//				argsList.clear();
//				argsList.add(roo.getOrderId());
//				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
//					throw new Exception("101303");

				// 更新明细
				sql = "update heso_order_consume_detail set return_status = 4 where order_id= ? and product_id =?";
				argsList.clear();
				argsList.add(roo.getOrderId());
				argsList.add(roo.getProductId());
				if (DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
					throw new Exception("101303");
			}
			conn.commit();
		} catch (Exception e) {
			roro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return roro;
	}

	/**
	 * 查询退货记录
	 * 
	 * @param account
	 * @return
	 */
	public RefundOrderReturnObject getList(String account, String orderId ,Integer page ,Integer pageSize) {
		RefundOrderReturnObject roro = new RefundOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			//查询总条数
			String sql = " select count(*) total  from heso_order_refund where account = ? ";
			
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			if (!orderId.isEmpty()) {
				sql += " and order_id = ?";
				argsList.add(orderId);
			}
			DataTable total = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(total.getRows().size() <= 0 ){
				throw new Exception("101304");
			}
			RefundOrderObject totalRoo = new RefundOrderObject();
			totalRoo.setCount(String.valueOf(total.getRows().get(0).getObject("total")));
		    sql = "select * from heso_order_refund where account=? ";
			
			if (!orderId.isEmpty()) {
				sql += " and order_id = ?";
			}
			sql += " order by create_time desc limit ?,? ";
			argsList.add((page-1)*pageSize);
			argsList.add(pageSize);
			
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() <= 0)
				throw new Exception("101304");
			ArrayList<RefundOrderObject> rooList = new ArrayList<RefundOrderObject>();
			rooList.add(totalRoo);
			for (int i = 0; i < dt.getRows().size(); i++) {
				RefundOrderObject roo = new RefundOrderObject();
				DataRow dr = dt.getRows().get(i);
				roo.setOrderId(dr.getString("order_id"));
				roo.setAccount(dr.getString("account"));
				roo.setType(dr.getString("type"));
				roo.setOrgOrderId(dr.getString("org_order_id"));
				roo.setProductId(dr.getString("product_id"));
				roo.setName(dr.getString("name"));
				roo.setAmount(dr.getString("amount"));
				roo.setCreateTime(dr.getString("create_time"));
				roo.setComment(dr.getString("comment"));
				roo.setImages(dr.getString("product_image"));
				roo.setStatus(dr.getString("status"));
				roo.setRefuseReason(dr.getString("refuse_reason"));
				rooList.add(roo);
			}
			roro.setRooList(rooList);
		} catch (Exception e) {
			roro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return roro;
	}

	/**
	 * 取消退货申请
	 * 
	 * @param orderId
	 * @param productId
	 * @return
	 */
	public RefundOrderReturnObject cancel(RefundOrderObject roo) {
		RefundOrderReturnObject roro = new RefundOrderReturnObject();
		try {
			String sql = "update heso_order_refund set status = 4 where account=? and order_id = ? and product_id=?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(roo.getAccount());
			argsList.add(roo.getOrderId());
			argsList.add(roo.getProductId());
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("101305");
		} catch (Exception e) {
			roro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return roro;
	}

	/**
	 * 执行退货换货，退款操作
	 * 
	 * @param orderId
	 * @return
	 */
	public RefundOrderReturnObject execute(String orderId) {
		RefundOrderReturnObject roro = new RefundOrderReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// 查询退货订单
			String sql = "select * from heso_order_refund where order_id=? and status = 1";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(orderId);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() <= 0)
				throw new Exception("101305");

			//更新退货单状态（3）
//			sql = "update heso_order_refund set status = 3 ,bank_name = ? ,bank_user_name = ?,bank_care_no = ? where order_id = ? ";
			sql = "update heso_order_refund set status = 3 where order_id = ? ";
			argsList.clear();
//			argsList.add(bankName);
//			argsList.add(bankUserName);
//			argsList.add(bankCareNo);
			argsList.add(orderId);
			
			// 获取退货类型
			DataRow dr = dt.getRows().get(0);
			String refundType = dr.getString("type");
			String account = dr.getString("account");
			String amount = dr.getString("amount");
			String orgOrderId = dr.getString("org_order_id");
			FundsServiceArgsObject faso = new FundsServiceArgsObject();
			FundsServiceReturnObject fsro = null;
			Integer type = Integer.parseInt(refundType);
			switch (type) {
			case 1: // 退款(未收货)
				faso.setAccountId(account);
				faso.setAmount(amount);
				faso.setOrderId(orderId);
				/*fsro = new FundsService().refund(faso, CommonType.FUNDS_CURRENCY);*/
				if (!fsro.getCode().equals("000000"))
					throw new Exception(fsro.getCode());
				break;
			case 2: // 退款(已收货)
				faso.setAccountId(account);
				faso.setAmount(amount);
				faso.setOrderId(orderId);
				/*fsro = new FundsService().refund(faso, CommonType.FUNDS_CURRENCY);*/
				if (!fsro.getCode().equals("000000"))
					throw new Exception(fsro.getCode());
				break;
			case 3://换货
				//查询原订单
				
				
			}
			
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <=0)
				throw new Exception("101306");
			
		} catch (Exception e) {
			roro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return roro;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RefundOrderObject roo = new RefundOrderObject();
		roo.setAccount("0000000000000002");
		roo.setOrderId("0000000000024480");
		roo.setProductId("16SP00004");
		roo.setType("1");
		roo.setComment("我要去无条件的换货(单品)");
		roo.setImages("");
		new RefundOrder().generate(roo);
		
		// new RefundOrder().cancel(roo);
		// new RefundOrder().getList("0000000000000002", orderId);
//		new RefundOrder().execute("0000000000024457");

	}

}
