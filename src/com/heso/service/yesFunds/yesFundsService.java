package com.heso.service.yesFunds;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.entity.CommonType;
import com.heso.db.DatabaseMgr;
import com.heso.service.funds.FundsService;
import com.heso.service.funds.entity.FundsServiceArgsObject;
import com.heso.service.funds.entity.FundsServiceReturnObject;
import com.heso.service.funds.order.consumption.OrderConsumption;
import com.heso.service.message.MessageService;
import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.sms.SmsService;
import com.heso.service.yesFunds.entity.yesFundsServiceReturnObject;
import com.heso.utility.ErrorProcess;
import com.heso.utility.ToolsUtil;

import data.DataTable;

public class yesFundsService extends FundsService {
	private static final Log logger = LogFactory.getLog(yesFundsService.class);
//	/**
//	 *  送金币
//	 * @param account
//	 * @param orderId
//	 * @return
//	 */
//	public yesFundsServiceReturnObject orderShowRecharge (){
//		yesFundsServiceReturnObject fsro = new yesFundsServiceReturnObject();
//		Connection conn = DatabaseMgr.getInstance().getConnection();
//		try {
//			//获取已收货订单信息
//			String sql = " select account,order_id,amount "
//					   + " from heso_order_consume where SYSDATE() >= ADDDATE(SEND_TIME,12) and PAY_STATUS = '1' ";
//			List<Object> list = new ArrayList<Object>();
//			DataTable orderShow = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
//			if(orderShow.getRows().size() <=0){
//				throw new Exception("101262");
//			}
//			
//			//获取金币赠送状态
//			sql = " select order_id from heso_inner_coin_detail where account = ? and order_id = ? ";
//			
//			if(DatabaseMgr.getInstance().execSqlTrans(sql, list,conn).getRows().size() >=1){
//				throw new Exception("101263");
//			}
//			//赠送金币
//				FundsServiceArgsObject fsao = new FundsServiceArgsObject();
//				fsao.setAccountId(account);
//				fsao.setOrderId(orderId);
//				BigDecimal orderShowCoin = new BigDecimal(orderShow.getRows().get(0).getDouble("amount")*0.3);
//				fsao.setAmount(String.valueOf(orderShowCoin));
//				FundsServiceReturnObject orderShowfsro = super.recharge(fsao, CommonType.FUNDS_COIN);
//				//查询该用户是否有推荐人
//				sql = " select recommend , add_status from heso_account where account = ? and recommend is not null and add_status = '0' ";
//				list.remove(1);
//				DataTable recommend = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
//				BigDecimal recommendCoin = null;
//				if("000000".equals(orderShowfsro.getCode()) && recommend.getRows().size()==1){
//					//赠送当前帐号金币
//					fsao.setAccountId(account);
//					fsao.setOrderId("add:"+recommend.getRows().get(0).getString("recommend"));
//				    recommendCoin = new BigDecimal(orderShow.getRows().get(0).getDouble("amount")*0.15);
//					fsao.setAmount(String.valueOf(recommendCoin));
//					FundsServiceReturnObject accounForo = super.recharge(fsao, CommonType.FUNDS_COIN);
//					//赠送推荐帐号金币
//					fsao.setAccountId(recommend.getRows().get(0).getString("recommend"));
//					fsao.setOrderId("add:"+account);
//					FundsServiceReturnObject recommendForo = super.recharge(fsao, CommonType.FUNDS_COIN);
//					sql = " update heso_account set add_status = '1' where account = ? ";
//					if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn) == 0){
//						throw new Exception("101264");
//					}
//				}
//				fsao.setAmount("orderShowCoin:"+orderShowCoin+",accountCoin:"+recommendCoin+",recommendCoin:"+recommendCoin);
//				fsro.setFsao(fsao);
//			
//		} catch (Exception e) {
//			// TODO: handle exception
//			fsro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}finally{
//			if(conn!=null){
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		return fsro;
//		
//	}
	/**
	 * 兑换积分-改
	 * @param account
	 * @param productId
	 * @param receiveId
	 * @param mobile
	 * @param authCode
	 * @return
	 */
	public yesFundsServiceReturnObject change(String account,String productId ,String receiveId,String mobile ,String authCode ){
		yesFundsServiceReturnObject fsro = new yesFundsServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
					//验证手机号
					if(!ToolsUtil.authMobileCode(mobile, authCode)){
						throw new Exception("100106");
					}
			// 取得会员尊享
						String sql = "select type , CURRENCY ,INNER_COIN,BONUS_POINT,name,image from heso_member_product where id = ? and status='1' and end_time > SYSDATE() ";
						ArrayList<Object> argsList = new ArrayList<Object>();
						argsList.add(productId);
						
						DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
						if (dt.getRows().size() == 0)
							throw new Exception("101299");
						
						FundsServiceArgsObject fsao = new FundsServiceArgsObject();
						fsao.setAccountId(account);
						fsao.setGoodsId(productId);
						fsao.setGoodsName(dt.getRows().get(0).getString("name"));
						fsao.setCurrency(dt.getRows().get(0).getString("currency"));
						fsao.setCoin(dt.getRows().get(0).getString("inner_coin"));
						fsao.setPoint(dt.getRows().get(0).getString("bonus_point"));
						fsao.setImage(dt.getRows().get(0).getString("image"));
						//新建服务订单
						FundsServiceReturnObject foro  = new OrderConsumption().request(fsao);
						//解冻金额
						if(!"000000".equals(foro.getCode())){
							new OrderConsumption().confirm(fsao, CommonType.CONFIRM_FAIL);
							throw new Exception(foro.getCode());
						}
						fsao.setOrderId(foro.getOrderId());
					
//						//查询订单信息
//						sql = " select product_id,CURRENCY , INNER_COIN , BONUS_POINT  from heso_order_consume where order_id =? and account = ?  and status = '1' ";
//						List <Object> list = new ArrayList<Object>();
//						list.add(foro.getOrderId());
//						list.add(account);
//						if(DatabaseMgr.getInstance().execSqlTrans(sql, list, conn).getRows().size() == 0 ){
//							throw new Exception("101302");
//						}
						if(dt.getRows().get(0).getString("type").equals("2")){
								sql = "select hsc.code ,hmp.name from heso_service_code hsc left outer join heso_member_product hmp on (hsc.mp_id = hmp.id ) "
								    	+ "where hmp.id = ? and hsc.account is null ";
								argsList.clear();
								argsList.add(productId);
								DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
								if(dt1.getRows().size() <= 0){
									new OrderConsumption().confirm(fsao, CommonType.CONFIRM_FAIL);
									throw new Exception("101304");
								}
								String seleverNum = dt1.getRows().get(0).getString("code");
								
								sql = " update heso_service_code set account = ? , order_id = ? where code = ?  ";
								argsList.clear();
								argsList.add(account);
								argsList.add(foro.getOrderId());
								argsList.add(seleverNum);
								if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn)<=0){
									new OrderConsumption().confirm(fsao, CommonType.CONFIRM_FAIL);
									throw new Exception("101305");
								}
								//发送消息给客户
								new SmsService().sendMessage(seleverNum,mobile,SmsService.SMS_TYPE_SERVICER_NUM);
								//发送站内信息
								new MessageService().sendMessage("系统消息", account , "【红兰衣配】尊敬的用户，您兑换的"+dt1.getRows().get(0).getString("name")+"服务码为: "+seleverNum);
								fsro.setSeleverNum(seleverNum);
						}else if(dt.getRows().get(0).getString("type").equals("1") && !receiveId.isEmpty() ){
							sql = " update heso_order_consume set receive_id = ? where order_id = ? and account = ? ";
							argsList.clear();
							argsList.add(receiveId);
							argsList.add(foro.getOrderId());
							argsList.add(account);
							if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <=0 ){
								//解冻金额
								new OrderConsumption().confirm(fsao, CommonType.CONFIRM_FAIL);
								throw new Exception("101307");
							}
							List <String> orderIds = new ArrayList<String>();
							orderIds.add(foro.getOrderId());
							new ConsumeOrder().updateLoadAdd(receiveId, account, orderIds);
							new MessageService().sendMessage("系统消息", account , "【红兰衣配】尊敬的用户，您已成功兑换商品：订单号为："+fsao.getOrderId());
						}else {
							throw new Exception("101303");
						}
						fsro.setType(dt.getRows().get(0).getString("type"));
						
						//确定订单
						if(!new OrderConsumption().confirm(fsao, CommonType.CONFIRM_OK)){
							throw new Exception("101303");
						}
		} catch (Exception e) {
			// TODO: handle exception
			fsro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return fsro;
	}
//	/**
//	 * 积分金币兑换
//	 *
//	 * @return
//	 */
//	public yesFundsServiceReturnObject change(String account ,String productId) {
//		yesFundsServiceReturnObject fsro = new yesFundsServiceReturnObject();
//		try {
//			
//			// 取得会员尊享
//			String sql = "select type , CURRENCY ,INNER_COIN,BONUS_POINT,name,image from heso_member_product where id = ? and status='1' and end_time > SYSDATE() ";
//			ArrayList<Object> argsList = new ArrayList<Object>();
//			argsList.add(productId);
//			
//			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
//			if (dt.getRows().size() == 0)
//				throw new Exception("101299");
//			
//			FundsServiceArgsObject fsao = new FundsServiceArgsObject();
//			fsao.setAccountId(account);
//			fsao.setGoodsId(productId);
//			fsao.setGoodsName(dt.getRows().get(0).getString("name"));
//			fsao.setCurrency(dt.getRows().get(0).getString("currency"));
//			fsao.setCoin(dt.getRows().get(0).getString("inner_coin"));
//			fsao.setPoint(dt.getRows().get(0).getString("bonus_point"));
//			fsao.setImage(dt.getRows().get(0).getString("image"));
//			FundsServiceReturnObject foro  = new OrderConsumption().request(fsao);
//			if(!"000000".equals(foro.getCode())){
//				throw new Exception(foro.getCode());
//			}
//			fsao.setOrderId(foro.getOrderId());
//			fsro.setFsao(fsao);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			fsro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
//		return fsro;
//	}
	/**
	 * 确定兑换商品
	 * @param mobile
	 * @param mobileToken
	 * @param account
	 * @param orderId
	 * @return
	 */
//	public yesFundsServiceReturnObject confirmChange(String mobile ,String authCode,String account , String orderId){
//		yesFundsServiceReturnObject fsro = new yesFundsServiceReturnObject();
//		Connection conn = DatabaseMgr.getInstance().getConnection();
//		try {
//			//验证手机验证码
//			if(!ToolsUtil.authMobileCode(mobile, authCode)){
//				throw new Exception("101301");
//			}
//			//查询订单信息
//			String sql = " select product_id,CURRENCY , INNER_COIN , BONUS_POINT  from heso_order_consume where order_id =? and account = ?  and status = '1' ";
//			List <Object> list = new ArrayList<Object>();
//			list.add(orderId);
//			list.add(account);
//			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
//			if(dt.getRows().size() == 0 ){
//				throw new Exception("101302");
//			}
//			//确定订单
//			FundsServiceArgsObject fsao = new FundsServiceArgsObject();
//			fsao.setAccountId(account);
//			fsao.setOrderId(orderId);
//			fsao.setCurrency(dt.getRows().get(0).getString("currency"));
//			fsao.setPoint(dt.getRows().get(0).getString("BONUS_POINT"));
//			fsao.setCoin(dt.getRows().get(0).getString(" INNER_COIN "));
//			if(!new OrderConsumption().confirm(fsao, CommonType.CONFIRM_OK)){
//				throw new Exception("101303");
//			}
//			sql = "select hsc.code ,hmp.name from heso_service_code hsc left outer join heso_member_product hmp on (hsc.mp_id = hmp.id ) "
//				+ "where hmp.id = ? and hsc.account is null ";
//			list.clear();
//			list.add(dt.getRows().get(0).getString("product_id"));
//			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
//			if(dt1.getRows().size() <= 0){
//				throw new Exception("101304");
//			}
//			String seleverNum = dt1.getRows().get(0).getString("code");
//			if(seleverNum == null){
//				throw new Exception("101305");
//			}
//			//发送消息给客户
//			new SmsService().sendMessage(seleverNum , mobile, SmsService.SMS_TYPE_SERVICER_NUM);
//			//发送站内信息
//			new MessageService().sendMessage("系统消息", account, "【红兰衣配】尊敬的用户，您兑换的"+dt1.getRows().get(0).getString("name")+"服务码为: "+seleverNum);
//			sql = " update heso_service_code set account = ? where code = ? ";
//			list.clear();
//			list.add(account);
//			list.add(seleverNum);
//			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn)<=0){
//				throw new Exception("101305");
//			}
//			fsro.setSeleverNum(seleverNum);
//		} catch (Exception e) {
//			// TODO: handle exception
//			fsro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}finally{
//			if(conn!=null){
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
//		return fsro;
//	}
}
