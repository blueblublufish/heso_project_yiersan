package com.heso.service.account;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.common.GlobalCache;
import com.heso.db.DatabaseMgr;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserDataObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.utility.ErrorProcess;
import com.heso.utility.MD5Util;
import com.heso.utility.StringTools;
import com.heso.utility.ToolsUtil;

import data.DataRow;
import data.DataTable;

public class YiErSanAccountService extends UserService {
	private static final Log logger = LogFactory.getLog(YiErSanAccountService.class);
	/**
	 * 获取完善资料
	 * @param account
	 * @return
	 */
	public UserServiceReturnObject getUserData(String account){
		// 初始化返回对象
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
					String userName = StringTools.getSecStr(dr.getString("user_name"),1, 2);
					String address = dr.getString("address");
					String postcode = dr.getString("postcode");
					String fax = dr.getString("fax");
					String idNo = dr.getString("id_no");
					String tel = dr.getString("tel");
					String email = dr.getString("email");
					String mobile = dr.getString("mobile").substring(0,dr.getString("mobile").length()-(dr.getString("mobile").substring(3)).length())+"****"+dr.getString("mobile").substring(7);//手机号码中间4位*号代替
					String cardno = StringTools.getSecStr(dr.getString("card_no"), 4, 14);
					String sex = dr.getString("sex");
					String birthday = dr.getString("birthday");
					String regionId = dr.getString("region_id");

					UserDataObject udo = new UserDataObject();
					udo.setUser_id(userId);
					udo.setUser_name(dr.getString("user_name"));
					udo.setAddress(address);
					udo.setPostcode(postcode);
					udo.setFax(fax);
					udo.setId_no(idNo);
					udo.setTel(tel);
					udo.setEmail(email);
					udo.setCard_no(cardno);
					udo.setMobile(mobile);
					udo.setSex(sex);
					udo.setBirthday(birthday);
					udo.setRegion_id(regionId);
					udo.setImage(dr.getString("image"));
					udo.setComment(dr.getString("comment"));
					udo.setLevel(dr.getString("level"));
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
	
	public UserServiceReturnObject login(String loginString,String mobile, String password, String userType, String loginIP,String authCode){
		// 初始化返回对象
				UserServiceReturnObject usro = new UserServiceReturnObject();
				usro.setCode("000000");
				Connection conn = DatabaseMgr.getInstance().getConnection();
				try{
					String sql = "";
					ArrayList<Object> argsList = new ArrayList<Object>();
					DataTable dt = null;
					if( !loginString.isEmpty() && !password.isEmpty() ){
						sql = "select * from heso_account where (user_id = ? or mobile = ?) and login_password = ? and user_type= ? ";
						argsList.add(loginString);
						argsList.add(loginString);
						argsList.add(password);
						argsList.add(userType);
					}else if( loginString.isEmpty() && password.isEmpty() && !mobile.isEmpty() ){
						if (ToolsUtil.authMobileCode(usro.getUdo().getMobile(), authCode)) {
							sql = "select * from heso_account where mobile = ? and user_type= ? ";
							argsList.add(mobile);
							argsList.add(userType);
						}else{
							usro.setCode("100106");
							return usro;
						}
					}
					dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					System.out.println(dt.getRows().get(0).getString("id_status"));
					if(dt.getRows().get(0).getString("status").equals("0"))
						throw new Exception("100104");
					System.out.println(dt.getRows().size());
					if (dt.getRows().size() != 1)
						throw new Exception("100104");
					// 取得账号
					String account = dt.getRows().get(0).getString("account");

					// 生成token
					Object o = new Object();
					String token = MD5Util.getMD5String(String.valueOf(o.hashCode()));

					
					UserDataObject udo = new UserDataObject();
					udo.setToken(token);
					udo.setUser_id(dt.getRows().get(0).getString("user_id"));
					
					// 设置登录token
					GlobalCache.getInstance().setValue(GlobalCache.USER_DATA, account, udo);

					// 设置登录信息（最后登录时间, 登录次数, 登录IP)
					sql = "update heso_account set last_login_time= ?, login_times=login_times+1, last_login_ip=? where account = ?";
					argsList.clear();
					argsList.add(new Date());
					argsList.add(loginIP);
					argsList.add(account);
					DatabaseMgr.getInstance().execNonSqlTrans(sql, argsList, conn);

					// 设置返回数据
					usro.setAccount(account);
					usro.setToken(token);
					
				}catch (Exception e) {
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
}
