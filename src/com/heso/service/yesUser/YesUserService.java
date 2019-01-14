package com.heso.service.yesUser;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import oracle.net.aso.a;
import oracle.net.aso.d;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.GlobalCache;
import com.heso.common.entity.CommonType;
import com.heso.data.entity.AmaniOrderdetail;
import com.heso.data.entity.CustomerInformation;
import com.heso.data.entity.Orderinformation;
import com.heso.db.DatabaseMgr;
import com.heso.service.order.consume.PackXmlToAmani;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.yesUser.entity.AccountNeed;
import com.heso.service.yesUser.entity.AccountRight;
import com.heso.service.yesUser.entity.InOrderMan;
import com.heso.utility.ErrorProcess;
import com.heso.utility.MD5Util;
import com.heso.utility.StringTools;
import com.heso.utility.ToolsUtil;
 

import data.DataRow;
import data.DataTable;

public class YesUserService extends UserService{
	private static final Log logger = LogFactory.getLog(YesUserService.class);
	
	public String findRecvIdbyAccount(String account,String regionalId) throws Exception{
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		List<Object> list = new ArrayList<Object>();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "select  * from heso_account_recvinfo where REGION_ID = ? and account = ? ";
		argsList.add(regionalId);
		argsList.add(account);
		DataTable dt;
		String recivedId = "";
		
		try {
			dt = dbm.execSqlTrans(sql, argsList, conn);
			if(dt.getRows().size() <=0 ){
				throw new Exception("101930");
			}
			recivedId =  dt.getRows().get(0).getString("ID");
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
		
		return recivedId;
		
	}
	
	/**
	 * ��ȡ��������
	 * 
	 * @param account
	 * @return
	 */
	public UserServiceReturnObject getUserData(String account) {
		// ��ʼ�����ض���
		UserServiceReturnObject usro = new UserServiceReturnObject();
		usro.setCode("000000");

		try {
			String sql = "select * from heso_account where account = ? ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() != 1)
				throw new Exception("100104");

			DataRow dr = dt.getRows().get(0);
			
			String userId = dr.getString("user_id");
			String userName = dr.getString("user_name");
			String address = dr.getString("address");
			String postcode = dr.getString("postcode");
			String fax = dr.getString("fax");
			String idNo = StringTools.getSecStr(dr.getString("id_no"), 4, 14);
			String tel = dr.getString("tel");
			String email = dr.getString("email");
			String mobile = dr.getString("mobile");
			String cardno = StringTools.getSecStr(dr.getString("card_no"), 4, 14);
			String sex = dr.getString("sex");
			String birthday = dr.getString("birthday");
			String regionId = dr.getString("region_id");
			
			//��ѯ�û��ȼ�
			sql = "SELECT * FROM heso_account_level WHERE ACCOUNT = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dto = DatabaseMgr.getInstance().execSql(sql, argsList);
			DataRow dro = dto.getRows().get(0);
			int level_id = dro.getInt("level_id");
//			sql = "SELECT * FROM heso_level WHERE LEVEL_ID = ?";
//			argsList.clear();
//			argsList.add(level_id);
//			dto = DatabaseMgr.getInstance().execSql(sql, argsList);
//			dro = dto.getRows().get(0);
			
			UserDataObject udo = new UserDataObject();
			udo.setUser_id(userId);
			udo.setUser_name(userName);
			udo.setAddress(address);
			udo.setPostcode(postcode);
			udo.setFax(fax);
			udo.setId_no(idNo);
			udo.setTel(tel);
			udo.setMobile(mobile);
			udo.setEmail(email);
			udo.setCard_no(cardno);
			udo.setSex(sex);
			udo.setBirthday(birthday);
			udo.setRegion_id(regionId);
			udo.setImage(dr.getString("image"));
			udo.setComment(dr.getString("comment"));
			udo.setLevel(String.valueOf(level_id));
			udo.setRegisterTime(dr.getString("register_time"));
			usro.setUdo(udo);
			
		} catch (Exception e) {
			// TODO: handle exception
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return usro;
	}

	
	public  String autoRegister2(String mobile,String name, String sex,String passward){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String account = "";
		try {
			
			
 
			// ����û��Ƿ����
			String sql = "select * from heso_account where  mobile =?";
			argsList.add(mobile);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				account = dt.getRows().get(0).getString("ACCOUNT");
				return account;
			}

			account = "in_"+dbm.getSequence("seq_account", "16");
			if (account.isEmpty())
				throw new Exception("000150");
			
			// д�û���Ϣ��
			sql = "insert into heso_account (account, user_id, login_password, user_type,mobile,USER_NAME,SEX) values (?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			argsList.add(passward);
			argsList.add(1);
			argsList.add(mobile);
			argsList.add(name);
			
			argsList.add(sex);
			int a = dbm.execNonSqlTrans(sql, argsList, conn);
			int d = 0;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			// e.printStackTrace();
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
		return account;
	}
	/**
	 * �Զ�ע��
	 * @param mobile
	 * @param name
	 * @param address
	 * @param sex
	 * @param regionId
	 * @return
	 */
	public static String autoRegister(String mobile,String name, String address,String sex,String regionId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String account = "";
		try {
			
			
			conn.setAutoCommit(false);

			// ����û��Ƿ����
			String sql = "select * from heso_account where  mobile =?";
			argsList.add(mobile);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				account = dt.getRows().get(0).getString("ACCOUNT");
				sql = "select  * from heso_account_recvinfo where REGION_ID = ? and account = ? ";
				argsList.clear();
				argsList.add(regionId);
				argsList.add(account);
				DataTable ddt = dbm.execSqlTrans(sql, argsList, conn);
				if(ddt.getRows().size() <=0 ){
					sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id) values (?,?,?,?,?,?) ";
					argsList.clear();
					argsList.add(account);
					argsList.add(name);
					argsList.add(address);
					argsList.add(mobile);
					argsList.add(mobile);
					System.out.println("regionid"+regionId);
					argsList.add(regionId);
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
				conn.commit();
				
				return account;
			}

			account = "in_"+dbm.getSequence("seq_account", "16");
			if (account.isEmpty())
				throw new Exception("000150");
			
			// д�û���Ϣ��
			sql = "insert into heso_account (account, user_id, login_password, user_type,mobile,USER_NAME,ADDRESS,SEX) values (?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			argsList.add("123456");
			argsList.add(1);
			argsList.add(mobile);
			argsList.add(name);
			argsList.add(address);
			argsList.add(sex);
			dbm.execNonSqlTrans(sql, argsList, conn);
  
			// д�û����˵�����
			sql = "insert into heso_account_profiles (account, birthday) values (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add("1900-01-01");
			dbm.execNonSqlTrans(sql, argsList, conn);

			// д���˻���3����
			sql = "insert into heso_currency (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_bonus_point (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_inner_coin (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û��ȼ���
			sql = "INSERT INTO heso_account_level (ACCOUNT,USER_ID) VALUES (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û�סַ��
			sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id) values (?,?,?,?,?,?) ";
			argsList.clear();
			argsList.add(account);
			argsList.add(name);
			argsList.add(address);
			argsList.add(mobile);
			argsList.add(mobile);
			System.out.println("regionid"+regionId);
			argsList.add(regionId);
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
		
		return account;
	}
	//��ֵ
	public   String addCurrency(String account,BigDecimal amount){
		String flag = "0";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "update heso_currency SET BALANCE = BALANCE +? ,TOTAL_RECHARGE = TOTAL_RECHARGE + ? WHERE ACCOUNT = ?";
		try {
			

			conn.setAutoCommit(false);

			String seqId = dbm.getSequence("seq_order", "16");
			
			String porderId = seqId;
			argsList.clear();
			argsList.add(amount);
			argsList.add(amount);
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			
			sql = "INSERT INTO heso_currency_detail (seq_id,account,trans_type,trans_amount,order_id) VALUES " +
					"(?,?,?,?,?)";
			argsList.clear();
			argsList.add(porderId);
			argsList.add(account);
			argsList.add("1");
			argsList.add(amount);
			argsList.add("");
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			flag = "1";
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
		
		
		return flag;
		
	}
	
	//�ۼ����
	public   String subCurrency(String account,BigDecimal amount,String orderId){
		String flag = "0";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "SELECT * FROM heso_currency WHERE ACCOUNT =?";
			argsList.add(account);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			BigDecimal balance = dt.getRows().get(0).getBigDecimal("BALANCE");
			if(balance.compareTo(amount)==-1){
				return flag;
			}
		    sql = "update heso_currency SET BALANCE = BALANCE -? ,TOTAL_DRAWING = TOTAL_DRAWING + ? WHERE ACCOUNT = ?";

			conn.setAutoCommit(false);

			String seqId = dbm.getSequence("seq_order", "16");
			
			String porderId = seqId;
			argsList.clear();
			argsList.add(amount);
			argsList.add(amount);
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			
			sql = "INSERT INTO heso_currency_detail (seq_id,account,trans_type,trans_amount,order_id) VALUES " +
					"(?,?,?,?,?)";
			argsList.clear();
			argsList.add(porderId);
			argsList.add(account);
			argsList.add("4");
			argsList.add(amount);
			argsList.add(orderId);
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			flag = "1";
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
		
		
		return flag;
		
	}
	
	//��ѯ���
	public  String checkCurrency(String account){
		String flag = "0";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "SELECT * FROM heso_currency WHERE ACCOUNT =?";
			argsList.add(account);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			BigDecimal balance = dt.getRows().get(0).getBigDecimal("BALANCE");
			
			flag = balance.toString();
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
		
		
		return flag;
		
	}
	
	public  String autoRegisterByMan(String mobile,String name, String address,String sex,String regionId,InOrderMan inOrderMan){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String account = "";
		try {
			
			
			conn.setAutoCommit(false);

			// ����û��Ƿ����
			String sql = "select * from heso_account where  mobile =?";
			argsList.add(mobile);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				account = dt.getRows().get(0).getString("ACCOUNT");
				sql = "delete from heso_account_profiles where account = ?";
				argsList.clear();
				argsList.add(account);
				int o = dbm.execNonSql(sql, argsList);
				
				sql = "insert into heso_account_profiles (account,sex, birthday,height,weight,bust,waist,hip,shoesize,shoulder,shank,thigh," +
						"head,lingwei,zhongyaowei,tuigenwei,tongdang,shouwanwei,youxiuchang,qianjiankuan,zuoxiuchang,houyaojiechang," +
						"houyichang,houyaogao,qianyaojiechang,qianyaogao,zuokuchang,youkuchang,qunchang,xiaotuikuchang,renti,zuojian," +
						"youjian,duxing,shoubi,tunxing,beixing,foot,neck,arm) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(account);
				argsList.add(Integer.parseInt(sex));
				argsList.add("1900-01-09");
				argsList.add(Integer.parseInt(inOrderMan.getHeigth()));
				argsList.add(Integer.parseInt(inOrderMan.getWeigth()));
				argsList.add(Integer.parseInt(inOrderMan.getXiongwei()));
				argsList.add(Integer.parseInt(inOrderMan.getYoawei()));
				argsList.add(Integer.parseInt(inOrderMan.getTunwei()));
				argsList.add(inOrderMan.getJiaochangdu());
				argsList.add(Integer.parseInt(inOrderMan.getZongjiankuan()));
				argsList.add(Integer.parseInt(inOrderMan.getXiaotuiwei()));
				argsList.add(Integer.parseInt(inOrderMan.getDatuiwei()));
				argsList.add(Integer.parseInt(inOrderMan.getTouwei()));
				argsList.add(inOrderMan.getLingwei());
				argsList.add(inOrderMan.getZhongyaowei());
				argsList.add(inOrderMan.getTuigenwei());
				argsList.add(inOrderMan.getTongdang());
				argsList.add(inOrderMan.getShouwanwei());
				argsList.add(inOrderMan.getYouxiuchang());
				argsList.add(inOrderMan.getQianjiankuan());
				argsList.add(inOrderMan.getZuoxiuchang());
				argsList.add(inOrderMan.getHouyaojiechang());
				argsList.add(inOrderMan.getHouyichang());
				argsList.add(inOrderMan.getHouyaogao());
				argsList.add(inOrderMan.getQianyaojiechang());
				argsList.add(inOrderMan.getQianyaogao());
				argsList.add(inOrderMan.getZuokuchang());
				argsList.add(inOrderMan.getYoukuchang());
				argsList.add(inOrderMan.getQunchang());
				argsList.add(inOrderMan.getXiaokutuichang());
				argsList.add(inOrderMan.getRenti());
				argsList.add(inOrderMan.getZuojian());
				argsList.add(inOrderMan.getYoujian());
				argsList.add(inOrderMan.getDuxing());
				argsList.add(inOrderMan.getShoubi());
				argsList.add(inOrderMan.getTunxing());
				argsList.add(inOrderMan.getBeixing());
				argsList.add(inOrderMan.getJiaokuan());
				argsList.add(inOrderMan.getBocu());
				argsList.add(inOrderMan.getShangbiwei());
				int p = dbm.execNonSqlTrans(sql, argsList, conn);
				
				
				sql = "select  * from heso_account_recvinfo where REGION_ID = ? and account = ? ";
				argsList.clear();
				argsList.add(regionId);
				argsList.add(account);
				DataTable ddt = dbm.execSqlTrans(sql, argsList, conn);
				if(ddt.getRows().size() <=0 ){
					sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id) values (?,?,?,?,?,?) ";
					argsList.clear();
					argsList.add(account);
					argsList.add(name);
					argsList.add(address);
					argsList.add(mobile);
					argsList.add(mobile);
					System.out.println("regionid"+regionId);
					argsList.add(regionId);
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
				conn.commit();
				return account;
			}

			account = "in_"+dbm.getSequence("seq_account", "16");
			if (account.isEmpty())
				throw new Exception("000150");
			
			// д�û���Ϣ��
			sql = "insert into heso_account (account, user_id, login_password, user_type,mobile,USER_NAME,ADDRESS,SEX) values (?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			argsList.add("e9af3de161c9038cd81014f31f8278fd");
			argsList.add(1);
			argsList.add(mobile);
			argsList.add(name);
			argsList.add(address);
			argsList.add(sex);
			dbm.execNonSqlTrans(sql, argsList, conn);
  
			// д�û����˵�����
			sql = "insert into heso_account_profiles (account,sex, birthday,height,weight,bust,waist,hip,shoesize,shoulder,shank,thigh," +
					"head,lingwei,zhongyaowei,tuigenwei,tongdang,shouwanwei,youxiuchang,qianjiankuan,zuoxiuchang,houyaojiechang," +
					"houyichang,houyaogao,qianyaojiechang,qianyaogao,zuokuchang,youkuchang,qunchang,xiaotuikuchang,renti,zuojian," +
					"youjian,duxing,shoubi,tunxing,beixing) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(Integer.parseInt(sex));
			argsList.add("1900-01-01");
			argsList.add(Integer.parseInt(inOrderMan.getHeigth()));
			argsList.add(Integer.parseInt(inOrderMan.getWeigth()));
			argsList.add(Integer.parseInt(inOrderMan.getXiongwei()));
			argsList.add(Integer.parseInt(inOrderMan.getYoawei()));
			argsList.add(Integer.parseInt(inOrderMan.getTunwei()));
			argsList.add(inOrderMan.getJiaochangdu());
			argsList.add(Integer.parseInt(inOrderMan.getZongjiankuan()));
			argsList.add(Integer.parseInt(inOrderMan.getXiaotuiwei()));
			argsList.add(Integer.parseInt(inOrderMan.getDatuiwei()));
			argsList.add(Integer.parseInt(inOrderMan.getTouwei()));
			argsList.add(inOrderMan.getLingwei());
			argsList.add(inOrderMan.getZhongyaowei());
			argsList.add(inOrderMan.getTuigenwei());
			argsList.add(inOrderMan.getTongdang());
			argsList.add(inOrderMan.getShouwanwei());
			argsList.add(inOrderMan.getYouxiuchang());
			argsList.add(inOrderMan.getQianjiankuan());
			argsList.add(inOrderMan.getZuoxiuchang());
			argsList.add(inOrderMan.getHouyaojiechang());
			argsList.add(inOrderMan.getHouyichang());
			argsList.add(inOrderMan.getHouyaogao());
			argsList.add(inOrderMan.getQianyaojiechang());
			argsList.add(inOrderMan.getQianyaogao());
			argsList.add(inOrderMan.getZuokuchang());
			argsList.add(inOrderMan.getYoukuchang());
			argsList.add(inOrderMan.getQunchang());
			argsList.add(inOrderMan.getXiaokutuichang());
			argsList.add(inOrderMan.getRenti());
			argsList.add(inOrderMan.getZuojian());
			argsList.add(inOrderMan.getYoujian());
			argsList.add(inOrderMan.getDuxing());
			argsList.add(inOrderMan.getShoubi());
			argsList.add(inOrderMan.getTunxing());
			argsList.add(inOrderMan.getBeixing());
			dbm.execNonSqlTrans(sql, argsList, conn);

			// д���˻���3����
			sql = "insert into heso_currency (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_bonus_point (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_inner_coin (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û��ȼ���
			sql = "INSERT INTO heso_account_level (ACCOUNT,USER_ID) VALUES (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û�סַ��
			sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id) values (?,?,?,?,?,?) ";
			argsList.clear();
			argsList.add(account);
			argsList.add(name);
			argsList.add(address);
			argsList.add(mobile);
			argsList.add(mobile);
			System.out.println("regionid"+regionId);
			argsList.add(regionId);
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
		
		return account;
	}
	
	//¼���׼����Ϣ
	public    String addBiaoZhun(String account , String chimaquyu,String baiozhunhao,String biaozhunhao_code,String orderId,String pinlei){
		
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String code = "000001";
		try {
			//�����ֻ������������
			String liangtiStr = "";
			String sql = "SELECT * FROM heso_account_profiles WHERE account = ? ";
			argsList.add(account);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			
			String zuojian = dt.getRows().get(0).getString("ZUOJIAN");//���Ҽ�ͬ
			String duxing = dt.getRows().get(0).getString("DUXING");//����
			String shoubi = dt.getRows().get(0).getString("SHOUBI");//�ֱ�
			String tunxing = dt.getRows().get(0).getString("TUNXING");//����
			String beixing = dt.getRows().get(0).getString("BEIXING");//����
			String tetiStr = zuojian+","+duxing+","+shoubi+","+tunxing+","+beixing;
			String  ddString  = "";
			//����¼�붩��
			sql = "UPDATE heso_order_consume set zuoyoujian = ?,duxing = ?,shoubi = ?,tunxing = ?,beixing = ? ,teti_code = ? ,CHICUNFENLEI=?,CHIMAQUYU=?  WHERE ORDER_ID = ?";
			argsList.clear();
			argsList.add(zuojian);
			argsList.add(duxing);
			argsList.add(shoubi);
			argsList.add(tunxing);
			argsList.add(beixing);
			argsList.add(tetiStr);
			argsList.add("10054");
			argsList.add(chimaquyu);
			argsList.add(orderId);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			  sql ="UPDATE heso_order_consume_detail SET BIAOZHUNSIZE = ?,BIAOZHUN_CODE = ? WHERE ORDER_ID = ? AND CLOTH_TYPE = ?";
			//����¼�붩��
			 argsList.clear();
			argsList.add(baiozhunhao);
			argsList.add(biaozhunhao_code);
			argsList.add(orderId);
			argsList.add(pinlei);
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
			
			
		
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
	//��ѯ�û������
	public    List<AccountNeed>  findAccountNeed(String date,String accountId,String adminId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String dateA = date+" 0:00:00";
		String dateB = date+" 23:59:59";
		String code = "000000";
		Connection conn = dbm.getConnection();
		List<AccountNeed> anList = new ArrayList<AccountNeed>();
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "SELECT * FROM heso_account_demand WHERE createTIme between ? and ? ";
			argsList.add(dateA);
			argsList.add(dateB);
			if(!"".equals(adminId)&&!adminId.isEmpty()){
				sql = sql +" and adminId = ?";
				argsList.add(adminId);
			}	
			if(!"".equals(accountId)&&!accountId.isEmpty()){
				sql = sql +" and accountId = ?";
				argsList.add(accountId);
			}
	 
		
		 
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				for(int i = 0 ; i<dt.getRows().size();i++){
					AccountNeed an = new AccountNeed();
					an.setAccountId(dt.getRows().get(i).getString("ADMINID"));
					an.setAccountName(dt.getRows().get(i).getString("ACCOUNTNAME"));
					an.setAdminId(dt.getRows().get(i).getString("ADMINID"));
					an.setCndesc(dt.getRows().get(i).getString("CNDESC"));
					an.setCreateTime(dt.getRows().get(i).getString("CREATETIME"));
					an.setId(dt.getRows().get(i).getInt("ID")+"");
					an.setPhone(dt.getRows().get(i).getString("PHONE"));
					an.setScene(dt.getRows().get(i).getString("SCENE"));
					an.setStyle(dt.getRows().get(i).getString("STYLE"));
					an.setZhiye(dt.getRows().get(i).getString("ZHIYE"));
					anList.add(an);
				}
				 
			}else {
				
				code = "000001";
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
		return anList;
	}
	//�����û������
	public  String addAccountNeed(String adminId,String accountId,String name,String phone,String zhiye,String desc,
			String sceneId,String styleId){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		String code = "000000";
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "INSERT INTO heso_account_demand (ADMINID,ACCOUNTID,STYLE,ACCOUNTNAME,PHONE,ZHIYE,CNDESC,SCENE) " +
					"VALUES (?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(adminId);
			argsList.add(accountId);
			argsList.add(styleId);
			argsList.add(name);
			argsList.add(phone);
			argsList.add(zhiye);
			argsList.add(desc);
			argsList.add(sceneId);
		 
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
			if(x<=0){
				code = "000001";
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
		
		return code;
	}
	//һ�������������ݼ��붩��
	public     String profileToOrderDetail(String productId,String orderID,String pinlei,String account,String sex){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		//String account = "";
		String code = "000001";
		try {
			//�����ֻ������������
			String liangtiStr = "";
			String sql = "SELECT * FROM heso_account_profiles WHERE account = ? ";
			argsList.add(account);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			//ȡ������ߴ� 
			if("3".equals(pinlei)||"6000".equals(pinlei)||"4000".equals(pinlei)||"95000".equals(pinlei)||"3000".equals(pinlei)||"11000".equals(pinlei)){
				String lingwei = dt.getRows().get(0).getString("LINGWEI");//��Χ
				String zhongyaowei = dt.getRows().get(0).getString("ZHONGYAOWEI");//����Χ
				String youxiuchang = dt.getRows().get(0).getString("YOUXIUCHANG");//���䳤
				String qianjiankuan= dt.getRows().get(0).getString("QIANJIANKUAN");//ǰ���
				String zuoxiuchang= dt.getRows().get(0).getString("ZUOXIUCHANG");//���䳤
				String houyaojiechang= dt.getRows().get(0).getString("HOUYAOJIECHANG");//�����ڳ�
				String houyichang= dt.getRows().get(0).getString("HOUYICHANG");//���³�
				String qianyaojiechang= dt.getRows().get(0).getString("QIANYAOJIECHANG");//ǰ���ڳ�
				String arm= dt.getRows().get(0).getString("ARM")+"";//�ϱ�
				String hip= dt.getRows().get(0).getString("HIP")+"";//��Χ
				String bust= dt.getRows().get(0).getString("BUST")+"";//��Χ
				String shoulder= dt.getRows().get(0).getString("SHOULDER")+"";//�ܼ��
				liangtiStr = "10101:"+lingwei+","
						+"10102:"+bust+","
						+"10105:"+zhongyaowei+","
						+"10108:"+hip+","
						+"10110:"+arm+","
						+"10111:"+shoulder+","
						+"10113:"+zuoxiuchang+","
						+"10114:"+youxiuchang+","
						+"10115:"+qianjiankuan +","
						+"10116:"+houyaojiechang+","
						+"10117:"+houyichang+","
						+"10172:"+qianyaojiechang; 
				
			}
			if("98000".endsWith(pinlei)||"2000".endsWith(pinlei)){
				String tuigenwei = dt.getRows().get(0).getString("TUIGENWEI");//�ȸ�Χ
				String tongdang = dt.getRows().get(0).getString("TONGDANG");//ͨ��
				String houyaogao = dt.getRows().get(0).getString("HOUYAOGAO");//������
				String qianyaogao = dt.getRows().get(0).getString("QIANYAOGAO");//ǰ����
				String zuokuchang = dt.getRows().get(0).getString("ZUOKUCHANG");//��㳤
				String youkuchang = dt.getRows().get(0).getString("YOUKUCHANG");//�ҿ㳤
				String hip= dt.getRows().get(0).getString(("HIP"))+"";//��Χ
				String zhongyaowei = dt.getRows().get(0).getString("ZHONGYAOWEI");//������
				String waist = dt.getRows().get(0).getString("WAIST")+"";//yaowei
				liangtiStr = "10120:"+waist+","
						+"10108:"+hip+","
						+"10122:"+tuigenwei+","
 						+"10123:"+tongdang+","
						+"10124:"+houyaogao+","
						+"10125:"+qianyaogao+","
						+"10126:"+zuokuchang+","
						+"10127:"+youkuchang ;
			}
			if("130000".equals(pinlei)){
				String hip= dt.getRows().get(0).getString("HIP")+"";//��Χ
				String waist = dt.getRows().get(0).getString("WAIST")+"";//yaowei
				String qunchang = dt.getRows().get(0).getString("QUNCHANG");//ȹ��s
				liangtiStr = "10120:"+waist+","
						+"10108:"+hip+","
						+"130225:"+qunchang;
			}
			//ȡ��������Ϣ
			String zuojian = dt.getRows().get(0).getString("ZUOJIAN");//���Ҽ�ͬ
			String duxing = dt.getRows().get(0).getString("DUXING");//����
			String shoubi = dt.getRows().get(0).getString("SHOUBI");//�ֱ�
			String tunxing = dt.getRows().get(0).getString("TUNXING");//����
			String beixing = dt.getRows().get(0).getString("BEIXING");//����
			String jianbu = dt.getRows().get(0).getString("JIANBU");//�粿
			String jianxiagu = dt.getRows().get(0).getString("JIANXIAGU");//���ι�
			String renti = dt.getRows().get(0).getString("RENTI");//����
			String jingxing = dt.getRows().get(0).getString("JINGXING");//����
			String xiongxing = dt.getRows().get(0).getString("XIONGXING");//����
			//��������
			String chengyiteti = dt.getRows().get(0).getString("CHENGYITETI");
			String manteti = "";
			if("1".equals(sex)){
				manteti = ","+renti +","+xiongxing+","+beixing;
			}
			String tetiStr = zuojian+","+duxing+","+shoubi+","+tunxing+ ","+jianbu+","+jianxiagu+","+jingxing+manteti;
			String  ddString  = "";
			//����¼�붩��
			sql = "UPDATE heso_order_consume set zuoyoujian = ?,duxing = ?,shoubi = ?,tunxing = ?,beixing = ? ,teti_code = ? ,CHICUNFENLEI=? ,CHENGYITETI=? WHERE ORDER_ID = ?";
			argsList.clear();
			argsList.add(zuojian);
			argsList.add(duxing);
			argsList.add(shoubi);
			argsList.add(tunxing);
			argsList.add(beixing);
			argsList.add(tetiStr);
			argsList.add("10052");
			argsList.add(chengyiteti);
			argsList.add(orderID);
			int x = dbm.execNonSqlTrans(sql, argsList, conn);
		
			
			//��������¼�붩������
			sql = "UPDATE heso_order_consume_detail set BODY_SIZE = ? WHERE ORDER_ID = ? AND CLOTH_TYPE = ?";
			argsList.clear();
			argsList.add(liangtiStr);
			argsList.add(orderID);
			argsList.add(pinlei);
			int y = dbm.execNonSqlTrans(sql, argsList, conn);
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
	
	//��ȡ�û�ʣ��Ȩ��
	public   List<AccountRight> getAccountQuanyiBy(String Id,String type){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		List<AccountRight> arList = new ArrayList<AccountRight>();
		String sql = "";
		//0Ϊ�����û�ID��ѯ
		if(type.equals("0")){
			sql = "SELECT haq.id,haq.accounts,haq.type,haq.phone,haq.quanyi,haq.endTime,haq.count,haq.teachera,haq.teacherb ,haq.zhuli,haq.status,haq.accountName ,hmp.name "
					+" FROM heso_account_quanyi AS haq ,heso_member_product AS hmp WHERE haq.ACCOUNTS LIKE '%" +
					Id +
					"%' AND haq.quanyi = hmp.id ";
		}else {
			//������ʦ��������ID��ѯ
			sql = "SELECT haq.id,haq.accounts,haq.type,haq.phone,haq.quanyi,haq.endTime,haq.count,haq.teachera,haq.teacherb ,haq.zhuli,haq.status,haq.accountName ,hmp.name "
					+" FROM heso_account_quanyi AS haq ,heso_member_product AS hmp WHERE (haq.teacherA = '" +
					 Id +
					"'  OR teacherb = '" +
					Id +
					"' OR zhuli = '" +
					Id +
					"') AND haq.quanyi = hmp.id ";
			argsList.clear();
			 
		}
		try {
		
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			for(int i = 0;i<dt.getRows().size();i++){
				DataRow dr = dt.getRows().get(i);
				AccountRight arRight = new AccountRight();
				arRight.setId(dr.getInt("id"));
				arRight.setAccount(dr.getString("accounts"));
				arRight.setType(dr.getString("type"));
				arRight.setPhone(dr.getString("phone"));
				arRight.setQuanyiId(dr.getString("quanyi"));
				arRight.setEndTime(dr.getDateTime("endTime"));
				arRight.setCount(dr.getInt("count"));
				arRight.setTeachera(dr.getString("teachera"));
				arRight.setTeacherb(dr.getString("teacherb"));
				arRight.setZhuli(dr.getString("zhuli"));
				arRight.setStatus(dr.getString("status"));
				arRight.setName(dr.getString("accountName"));
				arRight.setQuanyiName(dr.getString("name"));
				arList.add(arRight);
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
		
		
		return arList;
	}
	
	//��������¼��
	public  String updateProfile(InOrderMan inOrderMan,String phone){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String code = "000001";
		try {
			String sql = " UPDATE heso_account_profiles set height=?,weight=?,bust=?,waist=?,hip=?,shoesize=?,shoulder=?,shank=?,thigh=?, "+
						"head=?,lingwei=?,zhongyaowei=?,tuigenwei=?,tongdang=?,shouwanwei=?,youxiuchang=?,qianjiankuan=?,zuoxiuchang=?,houyaojiechang=?,"+
						"houyichang=?,houyaogao=?,qianyaojiechang=?,qianyaogao=?,zuokuchang=?,youkuchang=?,qunchang=?,xiaotuikuchang=?,renti=?,zuojian=?,"+
						"youjian=?,duxing=?,shoubi=?,tunxing=?,beixing=?,foot=?,neck=?,arm=?,zheng=?,fan=?,ce=?,zhengTao=?,fanTao=?,ceTao=?,taoma=?," +
						"changchenyi = ?,changxizhuang = ?,changxiku=?,changxiema = ?,jianbu=?,jianxiagu=?,jingxing=?,xiongxing=? ," +
						" CHENGYITETI=?,IMAGES=?,XIZHUANGDINGDAN=?,XIZHUANGJIAJIAN=?,XIKUDINGDAN=?,XIKUJIAJIAN=?,XIQUNDINGDAN=?,XIQUNJIAJIAN=?" +
						" WHERE  account = (SELECT account FROM heso_account where mobile = ?)";
			argsList.add(inOrderMan.getHeigth());
			argsList.add(inOrderMan.getWeigth());
			argsList.add(inOrderMan.getXiongwei());
			argsList.add(inOrderMan.getYoawei()); 
			argsList.add(inOrderMan.getTunwei());
			argsList.add(inOrderMan.getJiaochangdu());
			argsList.add(inOrderMan.getZongjiankuan());
			argsList.add(inOrderMan.getXiaotuiwei());
			argsList.add(inOrderMan.getDatuiwei());
			argsList.add(inOrderMan.getTouwei());
			argsList.add(inOrderMan.getLingwei());
			argsList.add(inOrderMan.getZhongyaowei());
			argsList.add(inOrderMan.getTuigenwei());
			argsList.add(inOrderMan.getTongdang());
			argsList.add(inOrderMan.getShouwanwei());
			argsList.add(inOrderMan.getYouxiuchang());
			argsList.add(inOrderMan.getQianjiankuan());
			argsList.add(inOrderMan.getZuoxiuchang());
			argsList.add(inOrderMan.getHouyaojiechang());
			argsList.add(inOrderMan.getHouyichang());
			argsList.add(inOrderMan.getHouyaogao());
			argsList.add(inOrderMan.getQianyaojiechang());
			argsList.add(inOrderMan.getQianyaogao());
			argsList.add(inOrderMan.getZuokuchang());
			argsList.add(inOrderMan.getYoukuchang());
			argsList.add(inOrderMan.getQunchang());
			argsList.add(inOrderMan.getXiaokutuichang());
			argsList.add(inOrderMan.getRenti());
			argsList.add(inOrderMan.getZuojian());
			argsList.add(inOrderMan.getYoujian());
			argsList.add(inOrderMan.getDuxing());
			argsList.add(inOrderMan.getShoubi());
			argsList.add(inOrderMan.getTunxing());
			argsList.add(inOrderMan.getBeixing());
			argsList.add(inOrderMan.getJiaokuan());
			argsList.add(inOrderMan.getBocu());
			argsList.add(inOrderMan.getShangbiwei());
			argsList.add(inOrderMan.getZheng());
			argsList.add(inOrderMan.getFan());
			argsList.add(inOrderMan.getCe());
			argsList.add(inOrderMan.getZhengTao());
			argsList.add(inOrderMan.getFanTao());
			argsList.add(inOrderMan.getCeTao());
			argsList.add(inOrderMan.getTaoma());
			argsList.add(inOrderMan.getChenyi());
			argsList.add(inOrderMan.getXizhuang());
			argsList.add(inOrderMan.getXixku());
			argsList.add(inOrderMan.getXiema());
			argsList.add(inOrderMan.getJianbu());
			argsList.add(inOrderMan.getJianxiagu());
			argsList.add(inOrderMan.getJingxing());
			argsList.add(inOrderMan.getXiongxing());
			argsList.add(inOrderMan.getChengyiteti());
			argsList.add(inOrderMan.getImages());
			argsList.add(inOrderMan.getXizhuangdingdan());
			argsList.add(inOrderMan.getXizhuangjiajian());
			argsList.add(inOrderMan.getXikudingdan());
			argsList.add(inOrderMan.getXikujiajian());
			argsList.add(inOrderMan.getXiqundingdan());
			argsList.add(inOrderMan.getXiqunjiajian());
			argsList.add(phone);
			
			if(dbm.execNonSqlTrans(sql, argsList, conn)>0){
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
	
	public  String autoRegister(String mobile,String name, String address,String sex,String regionId,InOrderMan inOrderMan){
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String account = ""; 
		try {
			
			
			conn.setAutoCommit(false);

			// ����û��Ƿ����
			String sql = "select * from heso_account where  mobile =?";
			argsList.add(mobile);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				account = dt.getRows().get(0).getString("ACCOUNT");
				sql = "delete from heso_account_profiles where account = ?";
				argsList.clear();
				argsList.add(account);
				int o = dbm.execNonSql(sql, argsList);
				
				sql = "insert into heso_account_profiles (account,sex, birthday,height,weight,bust,waist,hip,shoesize,shoulder,shank,thigh," +
						"head,lingwei,zhongyaowei,tuigenwei,tongdang,shouwanwei,youxiuchang,qianjiankuan,zuoxiuchang,houyaojiechang," +
						"houyichang,houyaogao,qianyaojiechang,qianyaogao,zuokuchang,youkuchang,qunchang,xiaotuikuchang,renti,zuojian," +
						"youjian,duxing,shoubi,tunxing,beixing,foot,neck,arm,impression,scene,sceneforman,suitcolor,notsuitcolor,bodystraight," +
						"body_size,body_pattern,body_notsuit,movement,INDUSTRY,IDENTITY,IMPRESSION2,WANTIP1,WANTIP2,BIRTHDAY2,BIRTHTYPE,COMPANYNAME) " +
						"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				argsList.clear();
				argsList.add(account);
				argsList.add(Integer.parseInt(sex));
				argsList.add(inOrderMan.getBirthday());
				argsList.add(Integer.parseInt(inOrderMan.getHeigth()));
				argsList.add(Integer.parseInt(inOrderMan.getWeigth()));
				argsList.add(Integer.parseInt(inOrderMan.getXiongwei()));
				argsList.add(Integer.parseInt(inOrderMan.getYoawei()));
				argsList.add(Integer.parseInt(inOrderMan.getTunwei()));
				argsList.add(inOrderMan.getJiaochangdu());
				argsList.add(Integer.parseInt(inOrderMan.getZongjiankuan()));
				argsList.add(Integer.parseInt(inOrderMan.getXiaotuiwei()));
				argsList.add(Integer.parseInt(inOrderMan.getDatuiwei()));
				argsList.add(Integer.parseInt(inOrderMan.getTouwei()));
				argsList.add(inOrderMan.getLingwei());
				argsList.add(inOrderMan.getZhongyaowei());
				argsList.add(inOrderMan.getTuigenwei());
				argsList.add(inOrderMan.getTongdang());
				argsList.add(inOrderMan.getShouwanwei());
				argsList.add(inOrderMan.getYouxiuchang());
				argsList.add(inOrderMan.getQianjiankuan());
				argsList.add(inOrderMan.getZuoxiuchang());
				argsList.add(inOrderMan.getHouyaojiechang());
				argsList.add(inOrderMan.getHouyichang());
				argsList.add(inOrderMan.getHouyaogao());
				argsList.add(inOrderMan.getQianyaojiechang());
				argsList.add(inOrderMan.getQianyaogao());
				argsList.add(inOrderMan.getZuokuchang());
				argsList.add(inOrderMan.getYoukuchang());
				argsList.add(inOrderMan.getQunchang());
				argsList.add(inOrderMan.getXiaokutuichang());
				argsList.add(inOrderMan.getRenti());
				argsList.add(inOrderMan.getZuojian());
				argsList.add(inOrderMan.getYoujian());
				argsList.add(inOrderMan.getDuxing());
				argsList.add(inOrderMan.getShoubi());
				argsList.add(inOrderMan.getTunxing());
				argsList.add(inOrderMan.getBeixing());
				argsList.add(inOrderMan.getJiaokuan());
				argsList.add(inOrderMan.getBocu());
				argsList.add(inOrderMan.getShangbiwei());
				argsList.add(inOrderMan.getIpStyle1());
				argsList.add(inOrderMan.getScene());
				argsList.add(inOrderMan.getSceneForMan());
				argsList.add(inOrderMan.getSuitColor());
				argsList.add(inOrderMan.getNotSuitColor());
				argsList.add(inOrderMan.getStraight());
				 
				argsList.add(inOrderMan.getSense());
				argsList.add(inOrderMan.getBodyStyle());
				argsList.add(inOrderMan.getBodynotsuit());
				argsList.add(inOrderMan.getMovement());
				argsList.add(inOrderMan.getCarree());
				argsList.add(inOrderMan.getIdentity());
				argsList.add(inOrderMan.getIpStyle2());
				argsList.add(inOrderMan.getWantIpstyle1());
				argsList.add(inOrderMan.getWantIpstyle2());
				argsList.add(inOrderMan.getBirthday2());
				argsList.add(inOrderMan.getBirthType());
				argsList.add(inOrderMan.getCompanyName());
		
				int p = dbm.execNonSqlTrans(sql, argsList, conn);
				
				sql = "UPDATE heso_account_recvinfo SET isdefault = '0' WHERE ACCOUNT = ?";
				argsList.clear();
				argsList.add(account);
				dbm.execNonSqlTrans(sql, argsList, conn);
				sql = "select  * from heso_account_recvinfo where REGION_ID = ? and account = ? ";
				argsList.clear();
				argsList.add(regionId);
				argsList.add(account);
				DataTable ddt = dbm.execSqlTrans(sql, argsList, conn);
				if(ddt.getRows().size() <=0 ){
					sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id,ISDEFAULT) values (?,?,?,?,?,?,?) ";
					argsList.clear();
					argsList.add(account);
					argsList.add(name);
					argsList.add(address);
					argsList.add(mobile);
					argsList.add(mobile);
					System.out.println("regionid"+regionId);
					argsList.add(regionId);
					argsList.add("1");
					dbm.execNonSqlTrans(sql, argsList, conn);
				}
				conn.commit();
				return account;
			}

			account = "in_"+dbm.getSequence("seq_account", "16");
			if (account.isEmpty())
				throw new Exception("000150");
			
			// д�û���Ϣ��
			sql = "insert into heso_account (account, user_id, login_password, user_type,mobile,USER_NAME,ADDRESS,SEX) values (?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			argsList.add("e9af3de161c9038cd81014f31f8278fd");
			argsList.add(1);
			argsList.add(mobile);
			argsList.add(name);
			argsList.add(address);
			argsList.add(sex);
			dbm.execNonSqlTrans(sql, argsList, conn);
  
			// д�û����˵�����
			sql = "insert into heso_account_profiles (account,sex, birthday,height,weight,bust,waist,hip,shoesize,shoulder,shank,thigh," +
					"head,lingwei,zhongyaowei,tuigenwei,tongdang,shouwanwei,youxiuchang,qianjiankuan,zuoxiuchang,houyaojiechang," +
					"houyichang,houyaogao,qianyaojiechang,qianyaogao,zuokuchang,youkuchang,qunchang,xiaotuikuchang,renti,zuojian," +
					"youjian,duxing,shoubi,tunxing,beixing,foot,neck,arm,impression,scene,sceneforman,suitcolor,notsuitcolor,bodystraight," +
					"body_size,body_pattern,body_notsuit,movement,INDUSTRY,IDENTITY,IMPRESSION2,WANTIP1,WANTIP2,BIRTHDAY2,BIRTHTYPE,COMPANYNAME) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(Integer.parseInt(sex));
			argsList.add(inOrderMan.getBirthday());
			argsList.add(Integer.parseInt(inOrderMan.getHeigth()));
			argsList.add(Integer.parseInt(inOrderMan.getWeigth()));
			argsList.add(Integer.parseInt(inOrderMan.getXiongwei()));
			argsList.add(Integer.parseInt(inOrderMan.getYoawei()));
			argsList.add(Integer.parseInt(inOrderMan.getTunwei()));
			argsList.add(inOrderMan.getJiaochangdu());
			argsList.add(Integer.parseInt(inOrderMan.getZongjiankuan()));
			argsList.add(Integer.parseInt(inOrderMan.getXiaotuiwei()));
			argsList.add(Integer.parseInt(inOrderMan.getDatuiwei()));
			argsList.add(Integer.parseInt(inOrderMan.getTouwei()));
			argsList.add(inOrderMan.getLingwei());
			argsList.add(inOrderMan.getZhongyaowei());
			argsList.add(inOrderMan.getTuigenwei());
			argsList.add(inOrderMan.getTongdang());
			argsList.add(inOrderMan.getShouwanwei());
			argsList.add(inOrderMan.getYouxiuchang());
			argsList.add(inOrderMan.getQianjiankuan());
			argsList.add(inOrderMan.getZuoxiuchang());
			argsList.add(inOrderMan.getHouyaojiechang());
			argsList.add(inOrderMan.getHouyichang());
			argsList.add(inOrderMan.getHouyaogao());
			argsList.add(inOrderMan.getQianyaojiechang());
			argsList.add(inOrderMan.getQianyaogao());
			argsList.add(inOrderMan.getZuokuchang());
			argsList.add(inOrderMan.getYoukuchang());
			argsList.add(inOrderMan.getQunchang());
			argsList.add(inOrderMan.getXiaokutuichang());
			argsList.add(inOrderMan.getRenti());
			argsList.add(inOrderMan.getZuojian());
			argsList.add(inOrderMan.getYoujian());
			argsList.add(inOrderMan.getDuxing());
			argsList.add(inOrderMan.getShoubi());
			argsList.add(inOrderMan.getTunxing());
			argsList.add(inOrderMan.getBeixing());
			argsList.add(inOrderMan.getJiaokuan());
			argsList.add(inOrderMan.getBocu());
			argsList.add(inOrderMan.getShangbiwei());
			argsList.add(inOrderMan.getIpStyle1());
			argsList.add(inOrderMan.getScene());
			argsList.add(inOrderMan.getSceneForMan());
			argsList.add(inOrderMan.getSuitColor());
			argsList.add(inOrderMan.getNotSuitColor());
			argsList.add(inOrderMan.getStraight());
			
			argsList.add(inOrderMan.getSense());
			argsList.add(inOrderMan.getBodyStyle());
			argsList.add(inOrderMan.getBodynotsuit());
			argsList.add(inOrderMan.getMovement());
			argsList.add(inOrderMan.getCarree());
			argsList.add(inOrderMan.getIdentity());
			argsList.add(inOrderMan.getIpStyle2());
			argsList.add(inOrderMan.getWantIpstyle1());
			argsList.add(inOrderMan.getWantIpstyle2());
			argsList.add(inOrderMan.getBirthday2());
			argsList.add(inOrderMan.getBirthType());
			argsList.add(inOrderMan.getCompanyName());
			
			
			dbm.execNonSqlTrans(sql, argsList, conn);

			// д���˻���3����
			sql = "insert into heso_currency (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_bonus_point (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_inner_coin (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û��ȼ���
			sql = "INSERT INTO heso_account_level (ACCOUNT,USER_ID) VALUES (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û�סַ��
			
			sql = "UPDATE heso_account_recvinfo SET isdefault = '0' WHERE ACCOUNT = ?";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			sql = "insert into heso_account_recvinfo(account, name, address, mobile, telephone,region_Id,ISDEFAULT) values (?,?,?,?,?,?,?) ";
			argsList.clear();
			argsList.add(account);
			argsList.add(name);
			argsList.add(address);
			argsList.add(mobile);
			argsList.add(mobile);
			System.out.println("regionid"+regionId);
			argsList.add(regionId);
			argsList.add("1");
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
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
		
		return account;
	}
	
	
	
	/**
	 * �û�ע�����Ƽ���
	 */
	public UserServiceReturnObject registerByUsername(String userName, String loginPassword, String userType ,String mobile ,String mobileCode ,String registerType,String openid,String channel,String sex) {
		// ��ʼ�����ض���
		UserServiceReturnObject usro = new UserServiceReturnObject();
		usro.setCode("000000");
	
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		
		try {
			
//			if(!ToolsUtil.authMobileCode(mobile, mobileCode)){
//				throw new Exception("100106");
//			}
//			
 			conn.setAutoCommit(false);

			// ����û��Ƿ����
			String sql = "select user_id from heso_account where  mobile =?";
			argsList.add(mobile);
			DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
			if (dt.getRows().size() > 0){
				usro.setCode("100101");//�˻����ֻ������ѱ�ע��
				return usro;
			}

			String account = dbm.getSequence("seq_account", "16");
			if (account.isEmpty())
				throw new Exception("000150");
			String userId2 = randomUserId();
			// д�û���Ϣ�� 
			sql = "insert into heso_account (account, user_name,sex, user_id, login_password, user_type,mobile,openid,channel) values (?,?,?,?,?,?,?,?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(userName);
			argsList.add(sex);
			argsList.add(userId2);
			argsList.add(loginPassword);
			argsList.add(userType);
			argsList.add(mobile);
			argsList.add(openid); 
			argsList.add(channel);
			dbm.execNonSqlTrans(sql, argsList, conn);
			usro.setAccount(account);
			usro.setProductCount(userId2);
			// д�û����˵�����
			sql = "insert into heso_account_profiles (account, birthday) values (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add("1900-01-01");
			dbm.execNonSqlTrans(sql, argsList, conn);

			// д���˻���3����
			sql = "insert into heso_currency (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_bonus_point (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);

			sql = "insert into heso_inner_coin (account) values (?)";
			argsList.clear();
			argsList.add(account);
			dbm.execNonSqlTrans(sql, argsList, conn);
			
			//�����û��ȼ���
			sql = "INSERT INTO heso_account_level (ACCOUNT,USER_ID) VALUES (?,?)";
			argsList.clear();
			argsList.add(account);
			argsList.add(randomUserId());
			dbm.execNonSqlTrans(sql, argsList, conn);
			conn.commit();
			
			//�����Ż�ȯ��ȡregisterType=1
		    if(registerType.equals("1")){
		    	
		    	sql = "select * from heso_coupon_gen where COUPON_TYPE=0 and now()>=START_TIME and NOW()<=END_TIME ORDER BY ADD_TIME LIMIT 1";
		    	argsList.clear();
		    	DataTable coupDt = dbm.getInstance().execSqlTrans(sql, argsList, conn);
		    	DataTable dtGoods = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				
		    	if (coupDt.getRows().size() > 0){//��ע��ʱ���������ȯ����ȡ
		    		
					String coupongen_id = coupDt.getRows().get(0).getString("COUPONGEN_ID");
					int coupongenNum = coupDt.getRows().get(0).getInt("COUPON_NUM")+1;
				    String prefix ="000000";
				    String det_id = coupongen_id + prefix.substring(0, 6-String.valueOf(coupongenNum).length())+String.valueOf(coupongenNum);
					
					sql = " INSERT INTO heso_coupon_det (COUPONGEN_ID, COUPONDET_ID, COUPON_STATE, IS_USE, ACCOUNT, USER_NAME, USE_TIME, GET_TIME, ORDER_ID) "
						+ " VALUES (?, ?, '1', '0', ?, NULL, NULL, now(), NULL);";
					
					argsList.clear();
					argsList.add(coupongen_id);
					argsList.add(det_id);
					argsList.add(account);
					dbm.execNonSqlTrans(sql, argsList, conn);
					
					//����������ȡ����Ϊ1
					sql = " update heso_coupon_gen set COUPON_NUM = ? where COUPONGEN_ID = ?";
					argsList.clear();
					argsList.add(coupongenNum);
					argsList.add(coupongen_id);
					dbm.execNonSqlTrans(sql, argsList, conn);
					
					//COUPON_STATE 1.��ʹ��2.�ѹ��� 3.δʹ��
					//COUPON_TYPE  1-��ͨ�Ż�ȯ 0-����ר���Ż�ȯ
					sql = " INSERT INTO heso_user_coupon (ACCOUNT, COUPONDET_ID, COUPON_STATE, COUPON_TYPE, GET_TIME, USE_TIME, ORDER_ID)" 
						+ " VALUES (?,?, '3', '0', now(), NULL, NULL)";
					argsList.clear();
					argsList.add(account);
					argsList.add(det_id);
					dbm.execNonSqlTrans(sql, argsList, conn);
					conn.commit();
				}
		    }
		    
		/*	//���ͽ��
			FundsServiceArgsObject fsao = new FundsServiceArgsObject();
			fsao.setAmount("5");
			fsao.setAccountId(account);
			FundsServiceReturnObject foro = new CoinRecharge().request(fsao);
			fsao.setOrderId(foro.getOrderId());
			new CoinRecharge().confirm(fsao, CommonType.CONFIRM_OK);
			//���ͻ���
			 fsao.setAmount("100");
			 foro = new PointRecharge().request(fsao);
			 fsao.setOrderId(foro.getOrderId());
			 new PointRecharge().confirm(fsao, CommonType.CONFIRM_OK);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return usro;
	}
	
	public   UserServiceReturnObject loginByOpenId(String openId, String loginIP) {
		// ��ʼ�����ض���
		UserServiceReturnObject usro = new UserServiceReturnObject();
		usro.setCode("000000");
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = null;
		 
			 
			 sql = "select * from heso_account where  openId = '" +
			 		openId +
			 		"'  ";
	 
		 
		 
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			System.out.println(dt.getRows().size());
			if (dt.getRows().size() != 1)
				throw new Exception("100104");

			// ȡ���˺�
			String account = dt.getRows().get(0).getString("account");
			String userId = dt.getRows().get(0).getString("user_id");
			// ����token
			Object o = new Object();
			String token = MD5Util.getMD5String(String.valueOf(o.hashCode()));

			
			UserDataObject udo = new UserDataObject();
			udo.setToken(token);
			udo.setUser_id(dt.getRows().get(0).getString("user_id"));
			
			// ���õ�¼token
			GlobalCache.getInstance().setValue(GlobalCache.USER_DATA, account, udo);
 

			// ���÷�������
			usro.setAccount(account);
			usro.setProductCount(userId);
			usro.setToken(token);
		} catch (Exception e) {
			// TODO: handle exception
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			// e.printStackTrace();
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
		return usro;
	}
	/**
	 * �û���¼����
	 * 
	 * @param loginString
	 * @param password
	 * @param loginIP
	 * @return
	 */
	public UserServiceReturnObject login(String loginString, String password, String userType,String mobile,String mobileCode, String loginIP,String openId) {
		// ��ʼ�����ض���
		UserServiceReturnObject usro = new UserServiceReturnObject();
		usro.setCode("000000");
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = null;
			if(mobileCode.equals("")||mobileCode==null){
				// ����û����������Ƿ�Ϊ��
				if (loginString.isEmpty() || password.isEmpty())
					throw new Exception("100111");
				 sql = "select * from heso_account where (user_id = ? or mobile = ?) and   login_password = ? and user_type= ? ";
				argsList.add(loginString);
				argsList.add(loginString);
				argsList.add(password);
				argsList.add(userType);
			}else{
				if(!ToolsUtil.authMobileCode(mobile, mobileCode)){
					throw new Exception("100106");
				}
				 sql = "select * from heso_account where  mobile = ?  and user_type= ? ";
					argsList.add(mobile);
					argsList.add(userType);
			}
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			System.out.println(dt.getRows().size());
			if (dt.getRows().size() != 1)
				throw new Exception("100104");

			// ȡ���˺�
			String account = dt.getRows().get(0).getString("account");
 			String userIdString = dt.getRows().get(0).getString("user_id");
 			String sex = dt.getRows().get(0).getString("sex");
			// ����token
			Object o = new Object();
			String token = MD5Util.getMD5String(String.valueOf(o.hashCode()));
			
			UserDataObject udo = new UserDataObject();
			udo.setToken(token);
			udo.setUser_id(dt.getRows().get(0).getString("user_id"));
			
			// ���õ�¼token
			GlobalCache.getInstance().setValue(GlobalCache.USER_DATA, account, udo);

			argsList.clear();
			// ���õ�¼��Ϣ������¼ʱ��, ��¼����, ��¼IP)
			sql = "update heso_account set last_login_time= ?, login_times=login_times+1, last_login_ip=? where account = ?";
			if(!openId.equals("")&&!openId.isEmpty()){
				sql = "update heso_account set openid = ?, last_login_time= ?, login_times=login_times+1, last_login_ip=? where account = ?";
				argsList.add(openId);
			}
			argsList.add(new Date());
			argsList.add(loginIP);
			argsList.add(account);
			DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);

			// ���÷�������
			usro.setAccount(account);
			usro.setProductCount(userIdString);
			usro.setToken(token);
			usro.setBalance(sex);
		} catch (Exception e) {
			// TODO: handle exception
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			// e.printStackTrace();
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
		return usro;
	}
	/**
	 * ע������������
	 * @param moblie
	 * @param authCode
	 * @param newPassword
	 * @param pswType
	 * @return
	 */
	public UserServiceReturnObject setUserDatas (String account, String login_password, String UserId){
		// ��ʼ�����ض���
				UserServiceReturnObject usro = new UserServiceReturnObject();
				usro.setCode("000000");
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
		
			String sql = "select * from heso_account where account = ? ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() != 1)
				throw new Exception("100104");
			DataRow dr = dt.getRows().get(0);
			// ��������
			//ԭ����Ϊ�գ��޸�����
			if(dr.getString("LOGIN_PASSWORD") == null && !login_password.isEmpty()){
				sql = "update heso_account set login_password =? where account = ?";
				argsList.clear();
				argsList.add(login_password);
				argsList.add(account);
				int row = DatabaseMgr.getInstance().execNonSql(sql, argsList);
				if (row == 0)
					throw new Exception("100105");
			}
			//����UserID
			if(!UserId.isEmpty()){
				sql = "update heso_account set USER_ID =? where account = ?";
				argsList.clear();
				argsList.add(UserId);
				argsList.add(account);
				int row = DatabaseMgr.getInstance().execNonSql(sql, argsList);
				if (row == 0)
					throw new Exception("100105");
			}
			//�޸��û��ȼ���
			if (!UserId.isEmpty()) {
				sql = "update heso_account_level set USER_ID =? where account = ?";
				argsList.clear();
				argsList.add(account);
				argsList.add(UserId);
				int row = DatabaseMgr.getInstance().execNonSql(sql, argsList);
				if (row == 0)
					throw new Exception("100105");
			}
		}catch(NumberFormatException e){
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
		} 
		catch (Exception e) {
			// TODO: handle exception
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return usro;
	}
	
	//���¶���
	 public    String updateOrderComsume (String orderId,String checkStatus,String remarks,String account,String name,String phone){ 
		 DatabaseMgr dbm = DatabaseMgr.getInstance();
		 Connection conn = dbm.getConnection();
		 String rescode = "";
		 String resdesc = "";
			try {
				
				String sql = "";
				List<Object> argsList = new ArrayList<Object>();
				if(!"1".equals(checkStatus)){
					sql = 	" UPDATE heso_order_consume SET check_status=?,remarks =?  WHERE  ORDER_ID = ?";
					argsList.clear();
					argsList.add(checkStatus);
					argsList.add(remarks);
					argsList.add(orderId);
					int o = dbm.execNonSqlTrans(sql, argsList, conn);
					return "103";
				}
				String strings = "";
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
				String chengyiteti = dtt.getRows().get(0).getString("CHENGYITETI");
				orderinformation.setClothingid(pinlei);
				String chicun = dtt.getRows().get(0).getString("chicunfenlei");
				orderinformation.setSizecategoryid(chicun);
				orderinformation.setAreaid(dtt.getRows().get(0).getString("chimaquyu"));
				orderinformation.setFabirc(mianliao);
				orderinformation.setCustormerbody(teti);
				if("10053".equals(chicun)){
					orderinformation.setCustormerbody(chengyiteti);
				}
				orderinformation.setClothingstyle(dtt.getRows().get(0).getString("changdu"));
				orderinformation.setRemark("");
				orderinformation.setOrderno(orderId);
				orderinformation.setAmount("1");
				//�������
				sql = "SELECT * FROM heso_account_profiles WHERE account =  ?";
				argsList.clear(); 
				argsList.add(account);
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
				String height = dt.getRows().get(0).getInt("height")+"";
				String sex = dt.getRows().get(0).getString("sex");
				String weight = dt.getRows().get(0).getString("weight"); 
				String heightunited = "10266";//��ߵ�λcm
				String weightunited = "10261";//���ص�λkg
				String email = "";
				String address = "";
			 
				String memos = "";//�ͻ���ע
				customerInformation.setHeight(height);
				customerInformation.setGender(sex);
				customerInformation.setWeight(weight);
				customerInformation.setHeightunited(heightunited);
				customerInformation.setWeightunited(weightunited);
				customerInformation.setEmail(email);
				customerInformation.setAddress(address);
				customerInformation.setTel("13600059771");
				customerInformation.setMemos(memos);
				customerInformation.setName(name);
			/*	//��ѯ�ջ��ˣ�д�붩��
				sql = "SELECT * FROM heso_account_recvinfo WHERE ACCOUNT = ? AND isdefault = '1'";
				argsList.clear();
				argsList.add(account);
				DataTable dti = dbm.execSqlTrans(sql, argsList, conn);
				String reciveName = dti.getRows().get(0).getString("name");
				String recivePhone = dti.getRows().get(0).getString("mobile");
				String reciveAdd = dti.getRows().get(0).getString("address");
				String regoin_id = dti.getRows().get(0).getString("region_id");*/
				//��ѯ�������飬��ɶ�����Ϣ
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
				String chengyiliangti = "";
				for(int i = 0 ;i<dttt.getRows().size(); i++){
					AmaniOrderdetail amaniOrderdetail = new AmaniOrderdetail();
					gongyi = dttt.getRows().get(i).getString("system_code");
					shenti = dttt.getRows().get(i).getString("body_size");
					detailpinlei = dttt.getRows().get(i).getString("cloth_type");
					style = dttt.getRows().get(i).getString("weidu");
					chengyiliangti = dttt.getRows().get(i).getString("CHENGYILIANGTI");
					amaniOrderdetail.setBodystyle(style);
					amaniOrderdetail.setCategoryid(detailpinlei);
					amaniOrderdetail.setPartsize(shenti);
					if("10053".equals(chicun)){
						amaniOrderdetail.setPartsize(chengyiliangti);
					}
					amaniOrderdetail.setSizespecheight(biaozhunhao);
					amaniOrderdetail.setOrdersprocess(gongyi);
					if("10054".equals(chicun)){
						biaozhunhao = dttt.getRows().get(i).getString("biaozhunsize");
						biaozhunhao_code = dttt.getRows().get(0).getString("biaozhun_code");
						amaniOrderdetail.setSizespecheight(biaozhunhao);
						amaniOrderdetail.setPartsize(biaozhunhao_code);
					}
					detaillist.add(amaniOrderdetail);
				}
				String xmlString = new PackXmlToAmani().packXml(orderinformation, customerInformation, detaillist); 
				System.out.println("xml:"+xmlString);
				System.out.println("xml:"+xmlString);
				String ss = new PackXmlToAmani().post("http://api.rcmtm.cn/api/order", xmlString);
				System.out.println(ss);
				String srt[] = ss.split(",");
				String d = srt[1];
				resdesc = d.substring(d.indexOf(":")+1,d.indexOf("}"));
 				
 				sql = 	" UPDATE heso_order_consume SET check_status=?,remarks =? ,RES_CODE = ?,RES_DESC=?  WHERE  ORDER_ID = ?";
				argsList.clear();
				argsList.add(checkStatus);
				argsList.add(remarks);
				if(ss.contains("101")){
					rescode = "101";
				}else {
					rescode = "102";
				}
				argsList.add(rescode);
				argsList.add(resdesc);
				/*argsList.add(reciveAdd);
				argsList.add(reciveName);
				argsList.add(recivePhone);
				argsList.add(regoin_id);*/
				argsList.add(orderId);
				int x = dbm.execNonSqlTrans(sql, argsList, conn);
				String dString ="";
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
			return rescode+","+resdesc;
		}
					 
	 
			

			
	
	//�����г̼������
	public  String checkTrip(String account,String tripId,String accountDesc){
		String flag = "0";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = dbm.getConnection();
		ArrayList<Object> argsList = new ArrayList<Object>();
		String sql = "";
		try {
			

		
			
			
			sql = "INSERT INTO heso_examine (CHECKTYPE,CHECKSTATUS,ACCOUNT,ACCOUNTDESC,TRIPID) VALUES" +
					"(?,?,?,?,?)";
			argsList.clear();
			argsList.add("1");
			argsList.add("1");
			argsList.add(account);
			argsList.add(accountDesc);
			argsList.add(tripId);
			dbm.execNonSqlTrans(sql, argsList, conn);
			flag = "1";
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
		
		
		return flag;
	}
	
	public static String randomUserId(){
          
        //���ַ�����ķ�ʽ���  
        String randomcode = "";   
        String model = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";  
        char[] m = model.toCharArray();  
          
        for (int j=0;j<6 ;j++ )  {  
            char c = m[(int)(Math.random()*52)];  
            randomcode = randomcode + c;  
        }  
		return "�û�"+randomcode;
	}
	public static void main(String[] args) {
		
		InOrderMan inOrderMan = new InOrderMan();
		inOrderMan.setBeixing("����");
		inOrderMan.setBocu("99");
		inOrderMan.setDatuiwei("44");
		inOrderMan.setDuxing("33");
		inOrderMan.setHeigth("121");
		inOrderMan.setHouyaogao("33");
		inOrderMan.setHouyaojiechang("33");
		inOrderMan.setHouyichang("33");
		inOrderMan.setJiaochangdu("33");
		inOrderMan.setJiaokuan("33");
		inOrderMan.setLingwei("33");
		inOrderMan.setQianyaogao("33");
	 
		inOrderMan.setQianyaojiechang("33");
		inOrderMan.setQunchang("33");
		inOrderMan.setRenti("33");
		inOrderMan.setTunxing("33");
		inOrderMan.setShangbiwei("33");
		inOrderMan.setShoubi("33");
		inOrderMan.setShouwanwei("33");
		inOrderMan.setTongdang("33");
		inOrderMan.setTouwei("123");
		inOrderMan.setTuigenwei("33");
		inOrderMan.setTunwei("00");
		inOrderMan.setWeigth("66");
		inOrderMan.setXiaokutuichang("33");
		inOrderMan.setXiaotuiwei("22");
		inOrderMan.setXiongwei("22");
		inOrderMan.setYoawei("33");
		inOrderMan.setYoujian("33");
		inOrderMan.setYoukuchang("33");
		inOrderMan.setYouxiuchang("33");
		inOrderMan.setZhongyaowei("33");
		inOrderMan.setZongjiankuan("11");
		inOrderMan.setZuojian("33");
		inOrderMan.setQianjiankuan("rrr");
		inOrderMan.setQianyaojiechang("yyyy");
		inOrderMan.setZuokuchang("33"); 
		inOrderMan.setZuoxiuchang("33");
		//autoRegisterByMan("15156615","dsds","sdsd sds ds","1","440106",inOrderMan);
		//String ppu  = updateProfile(inOrderMan, "150146061411");
		//String account = "0000000000000909";
	 	//profileToOrderDetail("D18TZ0040", "0000000000003831", "3", "0000000000000909");
		BigDecimal amount = new BigDecimal(12.0);
		String flag = "";
		String account = autoRegister("15914316031", "Sally Li", "�㶫����", "0", "440106");
		//List<AccountRight> arList = getAccountQuanyiBy("5", "1");
	//	UserServiceReturnObject cObject = loginByOpenId("123456","");
  		System.out.println("_____"+account );
		 // updateOrderComsume("0000000000003844", "1", "wu",  "0000000000000909", "dd","18826418804");
	//	addBiaoZhun("0000000000000909", "12121212121", "1212121332332", "0000000000003831", "2");
		/*String accountString = autoRegister("1882695573","kus","mark","0","416140");
		System.out.println("===="+accountString);*/
		// ������������
		/*String aString= autoRegister2("11211", "����", "1", "22222");
		System.out.println(aString);*/
					
	}
	
}
