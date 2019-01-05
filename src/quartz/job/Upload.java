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
	 * �Զ��ջ� ���������죬�Զ������ջ�״̬
	 * @author qinjianzhao
	 *
	 */
	public static void goods (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			// ��ѯ�ѵ���
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
				logger.info("������Ϊ��"+dt.getRows().get(i).getString("order_id"));
			}
			sql+="?) ";
			argsList.add(dt.getRows().get(0).getString("order_id"));
			logger.info("������Ϊ��"+dt.getRows().get(0).getString("order_id"));
			//���µ���
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
	 * �Զ��������12�췵��
	 */
	public static void coin(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//��ѯ�����ͻ��ֵ��û�
		try {
			String sql = "select order_id , account , amount, RECOMMEND , return_status , type from heso_order_consume "+
					"where SYSDATE() >= ADDDATE(SEND_TIME,12) and CHANGE_TYPE ='0' and recommend <> ' ' and RECOMMEND_TYPE='0' and send_status = '3' " +
					"and pay_status = '1' and status = '0'  ";
			List<Object> list = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);

			if(dt.getRows().size()<=0){
				throw new Exception("101506");
			}
			//��ѯ�˻�����
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
					//���͸����Ƽ��˽��
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					double bd = new BigDecimal(amount).multiply(new BigDecimal(0.005)).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					if(bd != 0){
						fsao.setAmount(String.valueOf(bd));
						fsao.setOrderId(orderId);
						fsao.setType("3");
						//��������
						fsro = new CoinRecharge().request(fsao);
						fsao.setOrderId(fsro.getOrderId());
						logger.info("���Ƽ����˺�Ϊ��"+dt.getRows().get(i).getString("account"));
						if(!new CoinRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
							logger.info("���Ƽ�������ʧ�ܣ�"+orderId);
						}
					}
					//���͸��Ƽ��˽��
					fsao.setAccountId(dt.getRows().get(i).getString("recommend"));
					bd = new BigDecimal(amount).multiply(new BigDecimal(0.015)).setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
					if(bd != 0){
						fsao.setAmount(String.valueOf(bd));
						fsao.setOrderId(orderId);
						fsao.setType("2");
						//��������
						fsro = new CoinRecharge().request(fsao);
						fsao.setOrderId(fsro.getOrderId());
						logger.info("���Ƽ����˺�Ϊ��"+dt.getRows().get(i).getString("recommend"));
						if(!new CoinRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
							logger.info("�Ƽ�������ʧ�ܣ�"+orderId);
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
	 * �Զ����ػ��� 12���
	 */
	public static void point (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//��ѯ�����ͻ��ֵ��û�
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

			//��ѯ�˻�����
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
					//�������ͻ���
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					BigDecimal bd = new BigDecimal(amount).divide(new BigDecimal(3),BigDecimal.ROUND_HALF_UP);

					fsao.setAmount(String.valueOf(bd));
					fsao.setOrderId(orderId);
					FundsServiceReturnObject fsro = new PointRecharge().request(fsao);
					fsao.setOrderId(fsro.getOrderId());
					logger.info("�ͻ����ʺ�Ϊ��"+dt.getRows().get(i).getString("account"));
					if(!new PointRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
						logger.info("��������ʧ�ܣ�"+orderId);
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
	 * �Զ����ض��� 2Сʱδ֧��������
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
				logger.info(">>>>>>>>>>>>û�г�ʱ����");
			}else {
				sql = " update heso_order_consume set status = '1' where order_id in ( ";
				for(int i =0;i<dt.getRows().size();i++){
					sql+="?,";
					list.add(dt.getRows().get(i).getString("order_id"));
					map.put(dt.getRows().get(i).getString("order_id"), dt.getRows().get(i).getString("account"));
					String orderId = dt.getRows().get(i).getString("order_id");
					String count = dt.getRows().get(i).getString("count");
					String account = dt.getRows().get(i).getString("account");
					//�ճ̷���
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
					//�ճ̷��ؽ���
					logger.info("���ض�����"+dt.getRows().get(i).getString("order_id"));
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
	 * �Զ�ɾ������1����δ֧����ɾ��
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
	 * �Զ�ͳ�ƻ�Ա�����Ѷ� 12������ڻ�Ա�ȼ��жϣ�
	 */
	public static void point2 (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		
		//��ѯ������ɺ�12��δ�˻����û��������Ѷ�
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
				System.out.println("��ѯ������Ҫ�������Ѽ�¼������");
				throw new Exception("101506");
			}else{
				for(int i =0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					double	 amount = dt.getRows().get(i).getDouble("amount");
					//���������Ѷ��¼
					String sqlu="update heso_account_level set consumption =consumption+ ? where account = ?";
					list.clear();	
					list.add(amount);
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sqlu, list);
					conn.commit();
					//����Ҫһ�������Щ�Ǹ��¹��ȼ����Ѷ��
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
	 * �Զ�ͳ�ƻ�Ա�����Ѷ� 12������ڻ�Ա�ȼ��жϣ�
	 */
	public static void point1 (){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		//��ѯ�������Ѷ�δ�˻����û�
		
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

			//��ѯ�˻��������û������¼������Ѷ�
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
			//���»�Ա���Ѽ�¼
				String	sqld = "update heso_account_level set consumption = ? where account = ? ";
				sqlSb.delete(0, sqlSb.length());
				//sqlSb.append( "update heso_account_level set POINT_TYPE ='1'  where order_id = ? ");
				list.clear();
				list.add(orderId);
				list.add(orderId);
				System.out.println(sqlSb);
				if(DatabaseMgr.getInstance().execNonSqlTrans(sqld, list, conn)>0 && amount > 0){
					FundsServiceArgsObject fsao = new FundsServiceArgsObject();
					//�������ͻ���
					fsao.setAccountId(dt.getRows().get(i).getString("account"));
					BigDecimal bd = new BigDecimal(amount).divide(new BigDecimal(3),BigDecimal.ROUND_HALF_UP);

					fsao.setAmount(String.valueOf(bd));
					fsao.setOrderId(orderId);
					FundsServiceReturnObject fsro = new PointRecharge().request(fsao);
					fsao.setOrderId(fsro.getOrderId());
					logger.info("�ͻ����ʺ�Ϊ��"+dt.getRows().get(i).getString("account"));
					if(!new PointRecharge().confirm(fsao, CommonType.CONFIRM_OK)){
						logger.info("��������ʧ�ܣ�"+orderId);
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
	 * ͳ�Ƶȼ�
	 */
	public  static void statisticsLevel()
	{
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			//�õ��û�
			String sql = "SELECT * FROM heso_account_level";
			String sqla=null;
			List <Object> list = new ArrayList<Object>();
			List <Object> lista = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//��ѯ�ȼ�
			sql = "SELECT * FROM heso_level";
			DataTable dtlevel = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//ͳ�Ƶȼ�
			sql = "UPDATE heso_account_level SET LEVEL_ID = ? WHERE ACCOUNT = ? ";
			// sqla="UPDATE heso_account SET RANK = ?  WHERE ACCOUNT = ?";
			
			
			for(int i = 0;i<dt.getRows().size();i++){
				String account = dt.getRows().get(i).getString("account");
				Float money = dt.getRows().get(i).getFloat("consumption");
				Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
				list.clear();
				if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-1).getFloat("consumption")) {
					//��ʯ
					if(LEVEL_ID==4){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(4));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
					
					new MessageService().sendMessage("ϵͳ��Ϣ", account, "���������䡿�𾴵��û�����Ļ�Ա�ȼ�����Ϊ��ʯ");
				}else if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-2).getFloat("consumption")) {
					//����
					if(LEVEL_ID==3){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(3));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
				
					new MessageService().sendMessage("ϵͳ��Ϣ", account, "���������䡿�𾴵��û�����Ļ�Ա�ȼ�����Ϊ����");

				}else if (money>=dtlevel.getRows().get(dtlevel.getRows().size()-3).getFloat("consumption")) {
					//����
					if(LEVEL_ID==2){
						continue;
					}
					list.clear();
					list.add(Float.valueOf(2));
					list.add(account);
					DatabaseMgr.getInstance().execNonSql(sql, list);
					conn.commit();
					new MessageService().sendMessage("ϵͳ��Ϣ", account, "���������䡿�𾴵��û�����Ļ�Ա�ȼ�����Ϊ����");

				}/*else {
					//��ͨ
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
			//�����û����ȼ�
			/* for(int i = 0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
					String Rank=Integer.valueOf(LEVEL_ID.intValue()).toString();//����̫��
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
	 * �����û���Rank
	 */
	public  static void statisticsRank(){
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try{
			String sqla="UPDATE heso_account SET RANK = ?  WHERE ACCOUNT = ?";
			List <Object> list = new ArrayList<Object>();
			
			//��ѯ�ȼ�
			String sql = "SELECT * FROM heso_account_level";
			DataTable dt= DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			//�����û����ȼ�
			 for(int i = 0;i<dt.getRows().size();i++){
					String account = dt.getRows().get(i).getString("account");
					Float LEVEL_ID = dt.getRows().get(i).getFloat("LEVEL_ID");
					String Rank=Integer.valueOf(LEVEL_ID.intValue()).toString();//����̫��
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
	 * �Զ��ж��û��Ż�ȯ��δʹ�õ��Ż�ȯ�Ƿ���ڣ�����Ϊ����
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
				throw new Exception("100504");	//ϵͳ��ʱδ�����û����ѹ��ڵ��Ż�ȯ
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