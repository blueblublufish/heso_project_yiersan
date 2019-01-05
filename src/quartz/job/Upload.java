package quartz.job;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.entity.CommonType;
import com.heso.db.DatabaseMgr;
import com.heso.service.funds.entity.FundsServiceArgsObject;
import com.heso.service.funds.entity.FundsServiceReturnObject;
import com.heso.service.funds.order.recharge.CoinRecharge;
import com.heso.service.funds.order.recharge.PointRecharge;
import com.heso.service.message.MessageService;
import com.heso.service.sms.SmsService;

import data.DataRow;
import data.DataTable;

public class Upload {
	private static final Log logger = LogFactory.getLog(Upload.class);

	/**
	 * 自动收货 发货后七天，自动更新收货状态
	 * @author qinjianzhao
	 *
	 */
	public static void goods (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// 查询已到期
			String sql = " select account , order_id , SEND_TIME from heso_order_consume "
					+ " where SYSDATE() >= ADDDATE(SEND_TIME,7) and send_status = '1' and pay_status = '1' and status = '0' ";
			List<Object> argsList = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			if(dt.getRows().size() <=0)
				throw new Exception("101206");

			sql = " update heso_order_consume set send_status = '3' where order_id in ( ";
			for(int i=1;i< dt.getRows().size();i++){
				sql+="?, ";
				argsList.add(dt.getRows().get(i).getString("order_id"));
				logger.info("订单号为："+dt.getRows().get(i).getString("order_id"));
			}
			sql+="?) ";
			argsList.add(dt.getRows().get(0).getString("order_id"));
			logger.info("订单号为："+dt.getRows().get(0).getString("order_id"));
			//更新到期
			if(DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn) <= 0)
				throw new Exception("101207");
		} catch (Exception e) {
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
	}

	/**
	 * 自动返还金币12天返还
	 */
	public static void coin(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//查询可以送积分的用户
		try {
			String sql = "select order_id , account , amount, RECOMMEND , return_status , type from heso_order_consume "+
					"where SYSDATE() >= ADDDATE(SEND_TIME,12) and CHANGE_TYPE ='0' and recommend <> ' ' and RECOMMEND_TYPE='0' and send_status = '3' " +
					"and pay_status = '1' and status = '0'  ";
			List<Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);

			if(dt.getRows().size()<=0){
				throw new Exception("101506");
			}
			//查询退货订单
			StringBuffer sqlSb = new StringBuffer("select sum(amount) amount ,org_order_id from heso_order_refund where org_order_id in ( ? " );
			list.add(dt.getRows().get(0).getString("order_id"));
			for(int index =1 ; index<dt.getRows().size() ; index++){
				sqlSb.append(",? ");
				list.add(dt.getRows().get(index).getString("order_id"));
			}
			sqlSb.append(" ) and type <> '3' and status  in ('0' , '1') group by org_order_id ");
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sqlSb.toString(), list, conn);


			for(int i =0;i<dt.getRows().size();i++){
				String orderId = dt.getRows().get(i).getString("order_id");
				double amount = dt.getRows().get(i).getDouble("amount");
				if(dt1.getRows().size()>0){
					for(int z = 0 ;z <dt1.getRows().size();z++){
						if(orderId.equals(dt1.getRows().get(z).getString("org_order_id"))){
							amount -= dt1.getRows().get(z).getDouble("amount");
							break;
						}
					}
				}

				sqlSb.delete(0, sqlSb.length());
				sqlSb.append(" update heso_order_consume set RECOMMEND_TYPE='1' where order_id = ? ");
				list.clear();
				list.add(orderId);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sqlSb.toString(), list, conn) > 0 && amount > 0){
					FundsServiceArgsObject fsao = new FundsServiceArgsObject();
					FundsServiceReturnObject fsro = new FundsServiceReturnObject();
					//赠送给被推荐人金币
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					double bd = new BigDecimal(amount).multiply(new BigDecimal(0.005)).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					if(bd != 0){
						fsao.setAmount(String.valueOf(bd));
						fsao.setOrderId(orderId);
						fsao.setType("3");
						//创建订单
						fsro = new CoinRecharge().request(fsao);
						fsao.setOrderId(fsro.getOrderId());
						logger.info("被推荐人账号为："+dt.getRows().get(i).getString("account"));
						if(!new CoinRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
							logger.info("被推荐人赠送失败："+orderId);
						}
					}
					//赠送给推荐人金币
					fsao.setAccountId(dt.getRows().get(i).getString("recommend"));
					bd = new BigDecimal(amount).multiply(new BigDecimal(0.015)).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					if(bd != 0){
						fsao.setAmount(String.valueOf(bd));
						fsao.setOrderId(orderId);
						fsao.setType("2");
						//创建订单
						fsro = new CoinRecharge().request(fsao);
						fsao.setOrderId(fsro.getOrderId());
						logger.info("被推荐人账号为："+dt.getRows().get(i).getString("recommend"));
						if(!new CoinRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
							logger.info("推荐人赠送失败："+orderId);
						}
					}
				}	
			}




		} catch (Exception e) {
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
	}

	/**
	 * 自动返回积分 12天后
	 */
	public static void point (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//查询可以送积分的用户
		try {
			String sql = "select order_id , account , amount from heso_order_consume "+
					"where SYSDATE() >= ADDDATE(SEND_TIME,12) and CHANGE_TYPE ='0' and POINT_TYPE = '0'  and send_status = '3' " +
					"and pay_status = '1'  and status = '0' ";
			List<Object> list = new ArrayList<Object>();
			System.out.println(sql);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			if(dt.getRows().size()<=0){
				throw new Exception("101506");
			}

			//查询退货订单
			StringBuffer sqlSb = new StringBuffer("select sum(amount) amount ,org_order_id from heso_order_refund where org_order_id in ( ? " );
			list.add(dt.getRows().get(0).getString("order_id"));

			for(int index =1 ; index<dt.getRows().size() ; index++){
				sqlSb.append(",? ");
				list.add(dt.getRows().get(index).getString("order_id"));
			}
			sqlSb.append(" ) and type <> '3' and status  in ('0' , '1') group by org_order_id ");
			System.out.println(sqlSb);
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sqlSb.toString(), list, conn);


			for(int i =0;i<dt.getRows().size();i++){
				String orderId = dt.getRows().get(i).getString("order_id");
				double	amount = dt.getRows().get(i).getDouble("amount");

				if(dt1.getRows().size()>0){
					for(int z = 0 ;z <dt1.getRows().size();z++){
						if(orderId.equals(dt1.getRows().get(z).getString("org_order_id"))){
							amount -= dt1.getRows().get(z).getDouble("amount");
							break;
						}
					}
				}
		
				sqlSb.delete(0, sqlSb.length());
				sqlSb.append( "update heso_order_consume set POINT_TYPE ='1'  where order_id = ? ");
				list.clear();
				list.add(orderId);
				System.out.println(sqlSb);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sqlSb.toString(), list, conn)>0 && amount > 0){
					FundsServiceArgsObject fsao = new FundsServiceArgsObject();
					//购买人送积分
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					BigDecimal bd = new BigDecimal(amount).divide(new BigDecimal(3),BigDecimal.ROUND_HALF_UP);

					fsao.setAmount(String.valueOf(bd));
					fsao.setOrderId(orderId);
					FundsServiceReturnObject fsro = new PointRecharge().request(fsao);
					fsao.setOrderId(fsro.getOrderId());
					logger.info("送积分帐号为："+dt.getRows().get(i).getString("account"));
					if(!new PointRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
						logger.info("积分赠送失败："+orderId);
					}
				}
			}

		} catch (Exception e) {
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
	}
	/**
	 * 自动隐藏订单 2小时未支付的隐藏
	 */
	public static void payType(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = "select count,order_id ,account from heso_order_consume where SYSDATE() >= ADDTIME(CREATE_TIME, '2:0:0' ) and pay_status ='0' and status = '0' ";
			List <Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			Map<String,String> map = new HashMap<String, String>();
			List <Object> list2 = new ArrayList<Object>();
			List <Object> list3 = new ArrayList<Object>();
			if(dt.getRows().size() <= 0){
				logger.info(">>>>>>>>>>>>没有超时订单");
			}else {
				sql = " update heso_order_consume set status = '1' where order_id in ( ";
				for(int i =0;i<dt.getRows().size();i++){
					sql+="?,";
					list.add(dt.getRows().get(i).getString("order_id"));
					map.put(dt.getRows().get(i).getString("order_id"), dt.getRows().get(i).getString("account"));
					String orderId = dt.getRows().get(i).getString("order_id");
					String count = dt.getRows().get(i).getString("count");
					String account = dt.getRows().get(i).getString("account");
					//日程返回
					String sql2 = "SELECT id,book_num,account ,orderid FROM heso_designer_place WHERE ORDERID LIKE '%" +
							orderId +
							"%'"; 
					DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql2, list2, conn);
					for(int j = 0;j<dtt.getRows().size();j++){
						String orderids = dtt.getRows().get(j).getString("orderid");
						String bookNum = dtt.getRows().get(j).getString("book_num");
						String accounts = dtt.getRows().get(j).getString("account");
						String id = dtt.getRows().get(j).getString("id");
						String newOrderIds = subString(orderId, orderids);
						String newaccounts = subString(account, accounts);
						sql2 = "UPDATE heso_designer_place as hdp SET STATUS = '0', orderId = ? ,ACCOUNT= ? ,BOOK_NUM = BOOK_NUM -" +
								count +
								" WHERE  ID = '"+
			 			id +"'";
						list3.clear();
						list3.add(newOrderIds);
						list3.add(newaccounts);
						if(DatabaseMgr.getInstance().execNonSqlTrans(sql2, list3, conn)<=0){
						 
						}
						
						
					}
					//日程返回结束
					logger.info("隐藏订单："+dt.getRows().get(i).getString("order_id"));
				}
				sql+="0)";
				if(DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn)<=0){
					throw new Exception("101510");
				}
			}
		} catch (Exception e) {
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
	}

	/**
	 * 自动删除订单1天内未支付的删除
	 * @param args
	 */
	public static void delpayType(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = " select order_id from heso_order_consume where SYSDATE() >= ADDTIME(CREATE_TIME,24) and pay_status ='0' and status = '1' ";
			List<Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			if(dt.getRows().size() <= 0){
				throw new Exception("101510");
			}

			conn.setAutoCommit(false);

			StringBuffer sql1 = new StringBuffer("delete from heso_order_consume where order_id in( ? ");
			list.add(dt.getRows().get(0).getString("order_id"));
			for(int i = 1;i<dt.getRows().size();i++){
				sql1.append(",? ");
				list.add(dt.getRows().get(i).getString("order_id"));
			}
			sql1.append(") ");

			if( DatabaseMgr.getInstance().execNonSqlTrans(sql1.toString(), list, conn) <= 0 )
				throw new Exception("101511");

			String sql2 = sql1.toString().replace("heso_order_consume", "heso_order_consume_detail");

			DatabaseMgr.getInstance().execNonSqlTrans(sql2, list, conn);

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
	
	/**
	 * 自动统计会员的消费额 12天后（用于会员等级判断）
	 */
	public static void point2 (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		
		//查询订单完成后12天未退货的用户增加消费额
		try {	
			conn.setAutoCommit(false);
			String sqly="SELECT b.account account ,b.ORG_ORDER_ID order_id,a.SEND_TIME send_time, (SUM(a.amount)-SUM(b.amount)) amount " +
							"from heso_order_consume a inner join heso_order_refund b" +
							" where a.ORDER_ID=b.ORG_ORDER_ID and   SYSDATE() >= ADDDATE(a.SEND_TIME,12) and a.upamount=0 GROUP BY b.account " +
							"union all " +
							"SELECT account,order_id ,send_time,sum(amount) amount " +
							"from heso_order_consume " +
							"where order_id NOT in (SELECT org_order_id from heso_order_refund) and   SYSDATE() >= ADDDATE(SEND_TIME,12)  and upamount=0  GROUP BY account" ;
			List<Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sqly, list, conn);
			if(dt.getRows().size()<=0){
				System.out.println("查询不到需要更新消费记录的数据");
				throw new Exception("101506");
			}else{
				for(int i =0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					double	 amount = dt.getRows().get(i).getDouble("amount");
					//更新下消费额记录
					String sqlu="update heso_account_level set consumption =consumption+ ? where account = ?";
					list.clear();	
					list.add(amount);
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sqlu, list);
					conn.commit();
					//还需要一个标记哪些是更新过等级消费额的
					String sqlua="update heso_order_consume set upamount=1 where account =?";
					list.clear();
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sqlua, list);
					conn.commit();
				}
			
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		finally{
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
	/**
	 * 自动统计会员的消费额 12天后（用于会员等级判断）
	 */
	public static void point1 (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//查询增加消费额未退货的用户
		
		try {
			String sql = "select order_id , account , amount from heso_order_consume "+
					"where SYSDATE() >= ADDDATE(SEND_TIME,12) and CHANGE_TYPE ='0' and POINT_TYPE = '0'  and send_status = '3' " +
					"and pay_status = '1'  and status = '0' ";
			List<Object> list = new ArrayList<Object>();
			System.out.println(sql);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			if(dt.getRows().size()<=0){
				throw new Exception("101506");
			}

			//查询退货订单的用户，重新计算消费额
			StringBuffer sqlSb = new StringBuffer("select sum(amount) amount ,org_order_id from heso_order_refund where org_order_id in ( ? " );
			list.add(dt.getRows().get(0).getString("order_id"));

			for(int index =1 ; index<dt.getRows().size() ; index++){
				sqlSb.append(",? ");
				list.add(dt.getRows().get(index).getString("order_id"));
			}
			sqlSb.append(" ) and type <> '3' and status  in ('0' , '1') group by org_order_id ");
			System.out.println(sqlSb);
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sqlSb.toString(), list, conn);


			for(int i =0;i<dt.getRows().size();i++){
				String orderId = dt.getRows().get(i).getString("order_id");
				double	amount = dt.getRows().get(i).getDouble("amount");

				if(dt1.getRows().size()>0){
					for(int z = 0 ;z <dt1.getRows().size();z++){
						if(orderId.equals(dt1.getRows().get(z).getString("org_order_id"))){
							amount -= dt1.getRows().get(z).getDouble("amount");
							break;
						}
					}
				}
			//更新会员消费记录
				String	sqld = "update heso_account_level set consumption = ? where account = ? ";
				sqlSb.delete(0, sqlSb.length());
				//sqlSb.append( "update heso_account_level set POINT_TYPE ='1'  where order_id = ? ");
				list.clear();
				list.add(orderId);
				list.add(orderId);
				System.out.println(sqlSb);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sqld, list, conn)>0 && amount > 0){
					FundsServiceArgsObject fsao = new FundsServiceArgsObject();
					//购买人送积分
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					BigDecimal bd = new BigDecimal(amount).divide(new BigDecimal(3),BigDecimal.ROUND_HALF_UP);

					fsao.setAmount(String.valueOf(bd));
					fsao.setOrderId(orderId);
					FundsServiceReturnObject fsro = new PointRecharge().request(fsao);
					fsao.setOrderId(fsro.getOrderId());
					logger.info("送积分帐号为："+dt.getRows().get(i).getString("account"));
					if(!new PointRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
						logger.info("积分赠送失败："+orderId);
					}
				}
			}

		} catch (Exception e) {
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
	}
	/**
	 * 统计等级
	 */
	public  static void statisticsLevel()
	{
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			//得到用户
			String sql = "SELECT * FROM heso_account_level";
			String sqla=null;
			List <Object> list = new ArrayList<Object>();
			List <Object> lista = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//查询等级
			sql = "SELECT * FROM heso_level";
			DataTable dtlevel = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//统计等级
			sql = "UPDATE heso_account_level SET LEVEL_ID = ? WHERE ACCOUNT = ? ";
			// sqla="UPDATE heso_account SET RANK = ?  WHERE ACCOUNT = ?";
			
			
			for(int i = 0;i<dt.getRows().size();i++){
				String account = dt.getRows().get(i).getString("account");
				Float money = dt.getRows().get(i).getFloat("consumption");
				Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
				list.clear();
				if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-1).getFloat("consumption")) {
					//钻石
					if(LEVEL_ID==4){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(4));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
					
					new MessageService().sendMessage("系统消息", account, "【红兰衣配】尊敬的用户，你的会员等级更新为钻石");
				}else if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-2).getFloat("consumption")) {
					//金牌
					if(LEVEL_ID==3){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(3));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
				
					new MessageService().sendMessage("系统消息", account, "【红兰衣配】尊敬的用户，你的会员等级更新为金牌");

				}else if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-3).getFloat("consumption")) {
					//白银
					if(LEVEL_ID==2){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(2));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
					new MessageService().sendMessage("系统消息", account, "【红兰衣配】尊敬的用户，你的会员等级更新为白银");

				}/*else {
					//普通
					if(LEVEL_ID==1){
						continue;
					}
					list.clear();
					//list.add(dtlevel.getRows().get(dtlevel.getRows().size()-1).getFloat("level_id"));
					list.add(Float.valueOf(1));
					list.add(account);
				}
				DatabaseMgr.getInstance().execNonSqlTrans(sql, list, conn);
				conn.commit();
				//DatabaseMgr.getInstance().execNonSqlTrans(sqla, lista, conn);
				DatabaseMgr.getInstance().execNonSql(sqla, lista);
				conn.commit();*/
			}
			//更新用户表等级
			/* for(int i = 0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
					String Rank=Integer.valueOf(LEVEL_ID.intValue()).toString();//长度太长
					lista.clear();
					lista.add(Rank);
					lista.add(account);
					DatabaseMgr.getInstance().execNonSql(sqla, lista);
					conn.commit();
					}*/
		
		} catch (Exception e) {
			e.printStackTrace();
			}
		finally{
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
	
	/**
	 * 更新用户表Rank
	 */
	public  static void statisticsRank(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try{
			String sqla="UPDATE heso_account SET RANK = ?  WHERE ACCOUNT = ?";
			List <Object> list = new ArrayList<Object>();
			
			//查询等级
			String sql = "SELECT * FROM heso_account_level";
			DataTable dt= DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//更新用户表等级
			 for(int i = 0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
					String Rank=Integer.valueOf(LEVEL_ID.intValue()).toString();//长度太长
					list.clear();
					list.add(Rank);
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sqla, list);
					//conn.commit();
					}
		} catch (Exception e) {
			e.printStackTrace();
			}
		finally{
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
	
	/**
	 * 自动判断用户优惠券表未使用的优惠券是否过期，更新为过期
	 * @param args
	 */
	public static void userCoupon(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = " SELECT u.* FROM heso_user_coupon u , ( "
						+ " 	SELECT a.*,b.START_TIME,b.END_TIME " 
						+ " 	FROM heso_coupon_det a,heso_coupon_gen b "
						+ " 	where a.COUPONGEN_ID=b.COUPONGEN_ID ) cou "
						+ " WHERE u.COUPONDET_ID=cou.COUPONDET_ID " 
						+ " AND u.COUPON_STATE=3 "
						+" and cou.END_TIME<NOW()";
			
			List<Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			
			if(dt.getRows().size() <= 0){
				throw new Exception("100504");	//系统暂时未发现用户有已过期的优惠券
			}
			
			conn.setAutoCommit(false);
			
			for(int z = 0 ;z <dt.getRows().size();z++){
				list.clear();
				sql = "UPDATE heso_user_coupon SET COUPON_STATE=2 WHERE COUPONDET_ID = ? AND ACCOUNT = ? AND COUPON_STATE=3 ";
				list.add(dt.getRows().get(z).getString("COUPONDET_ID"));
				list.add(dt.getRows().get(z).getString("ACCOUNT"));
				
				if( DatabaseMgr.getInstance().execNonSqlTrans(sql.toString(), list, conn) <= 0 )
					throw new Exception("100505");
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
	
	static String subString (String str1,String longstr){
		
		List<String> list = Arrays.asList(longstr.split(","));  
		ArrayList <String> newList = new ArrayList<>(list);
		
		
		for(String string:newList){
			if(str1.equals(string)){
				newList.remove(string);
				break;
			}	
		}
		String str = StringUtils.join(newList.toArray(), ",");  
		
		
		return str;
	}
	
	public static void main(String[] args) {
		Upload upload=new Upload();
		payType();
		System.out.println("----");
		//upload.statisticsLevel();
	//	 statisticsRank();
		//	upload.point2();
		//		 Upload.point();
		//Upload.coin();
		//Upload.statisticsLevel();
	}
}
