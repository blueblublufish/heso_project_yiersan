package com.heso.service.wardrobe;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.crypto.AEADBadTagException;
import javax.print.attribute.HashAttributeSet;

import oracle.net.aso.a;
import oracle.net.aso.e;
import oracle.net.aso.q;
import oracle.net.aso.s;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.Lookahead;

import com.heso.db.DatabaseMgr;
import com.heso.service.article.ArticleService;
import com.heso.service.mall.MallService;
import com.heso.service.mall.entity.MallServiceReturnObject;
import com.heso.service.mall.entity.ProductItemObject;
import com.heso.service.mall.entity.SuitMatchArgsObject;
import com.heso.service.system.SystemType;
import com.heso.service.user.UserService;
import com.heso.service.user.entity.UserProfilesObject;
import com.heso.service.user.entity.UserServiceReturnObject;
import com.heso.service.wardrobe.entity.CheckRecord;
import com.heso.service.wardrobe.entity.CheckRecordDetail;
import com.heso.service.wardrobe.entity.ReportDTO;
import com.heso.service.wardrobe.entity.TripsuitDTO;
import com.heso.service.wardrobe.entity.WardrobeDTO;
import com.heso.service.wardrobe.entity.WardrobeServiceReturnObject;
import com.heso.utility.ErrorProcess;
 
import com.sun.org.apache.commons.beanutils.WrapDynaBean;

import data.DataRow;
import data.DataTable;

/**
 * 衣橱
 * 
 * @author win10
 * 
 */
public class WardrobeService {
	private static final Log logger = LogFactory.getLog(WardrobeService.class);

	public WardrobeServiceReturnObject getTypeAndCount(String account,
			String isSuit, String isProduct,String season) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		ArrayList<WardrobeDTO> wardrobeList = new ArrayList<WardrobeDTO>();
		try {
			conn = dbm.getConnection();
			
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "";
			if ("1".equals(isSuit.trim())) {
				sql = "SELECT ht.SEX,  ht.IMAGE, ht. NAME ,ht.ID FROM heso_type as ht WHERE KEYWORD = 'CATEGORY'";
				DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);
				for (int i = 0; i < dtt.getRows().size(); i++) {
					String type = dtt.getRows().get(i).getString("ID");
					String image = dtt.getRows().get(i).getString("image");
					String typeName = dtt.getRows().get(i).getString("name");
					String sex = dtt.getRows().get(i).getString("sex");
					WardrobeDTO dto = new WardrobeDTO();
					dto.setType(type);
					dto.setImage(image);
					dto.setName(typeName);
					dto.setCount("0");
					dto.setSex(sex);
					wardrobeList.add(dto);
				}
				argsList.clear(); 
				argsList.add(account);
				argsList.add(isSuit);

				sql = "SELECT huw.TYPE, COUNT(huw.ID), ht.IMAGE, ht. NAME FROM heso_user_wardrobe AS huw, heso_type AS ht"
						+ " WHERE type IN (SELECT ID FROM heso_type WHERE KEYWORD = 'CATEGORY')"
						+ " AND huw.ACCOUNT = ? AND huw.SUIT = ? " ;
				if(!isProduct.isEmpty()&&!isProduct.trim().equals("")){
					sql = sql + " AND huw.IS_GOOD = ? ";
					argsList.add(isProduct);
				}
				if (!season.isEmpty() && season != null) {
					String[] s = season.split(",");
					String sqll = " and (FIND_IN_SET('" + s[0] + "',SEASON)";
					for (int i = 1; i < s.length; i++) {
						sqll = sqll + " or FIND_IN_SET( '" + s[i] + "',SEASON)";
					}
					sqll = sqll + ") ";
					sql = sql + sqll;
				}
				
				sql = sql +" AND huw.TYPE = ht.ID AND ht.KEYWORD = 'CATEGORY' GROUP BY huw.TYPE";
				DataTable dt = dbm.execSqlTrans(sql, argsList, conn);

				int x = dt.getRows().size();
				if (x <= 0) {
					logger.error(">>>>>>>>>>>>查询类型数量失败");
					wsro.setWardrobeList(wardrobeList);
					return wsro;
				}
				for (WardrobeDTO dto : wardrobeList) {
					for (int i = 0; i < dt.getRows().size(); i++) {
						String type = dt.getRows().get(i).getString("type");
						if (dto.getType().equals(type)) {
							String count = dt.getRows().get(i)
									.getString("COUNT(huw.ID)");
							dto.setCount(count);
						}
					}
				}
			} else {
				sql = "SELECT * FROM heso_type WHERE KEYWORD = 'scene'"; 
				DataTable dtt = dbm.execSqlTrans(sql, argsList, conn);
				for (int i = 0; i < dtt.getRows().size(); i++) {
					String type = dtt.getRows().get(i).getString("ID");
					String image = dtt.getRows().get(i).getString("image");
					String typeName = dtt.getRows().get(i).getString("name");
					String sex = dtt.getRows().get(i).getString("sex");
					WardrobeDTO dto = new WardrobeDTO();
					dto.setType(type);
					dto.setImage(image);
					dto.setName(typeName);
					dto.setCount("0");
					dto.setSex(sex);
					argsList.clear();
					argsList.add(account);
					argsList.add(isSuit);
					
					sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  ACCOUNT = ? AND SUIT = ? " ;
					if(!isProduct.isEmpty()||!isProduct.equals("")){
						sql = sql + " AND IS_GOOD = ? ";
						argsList.add(isProduct);
					}
					if (!season.isEmpty() && season != null) {
						String[] s = season.split(",");
						String sqll = " and (FIND_IN_SET('" + s[0] + "',SEASON)";
						for (int j = 1; j < s.length; j++) {
							sqll = sqll + " or FIND_IN_SET( '" + s[j] + "',SEASON)";
						}
						sqll = sqll + ") ";
						sql = sql + sqll;
					}
					sql = sql +	" AND  SCENE LIKE '%"
							+ type + "%'";
					DataTable dt = dbm.execSqlTrans(sql, argsList, conn);
					if (dt.getRows().size() > 0) {
						String count = dt.getRows().get(0)
								.getString("COUNT(ID)");
						dto.setCount(count);
					}
					wardrobeList.add(dto);
				}
			}
			wsro.setWardrobeList(wardrobeList);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}

	//衣橱诊断报告 单品数量统计
	public   List<ReportDTO>  wardrobeReport2(String account ,String sex) {
 		List<ReportDTO> dtos = new ArrayList<ReportDTO>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
  		try {
			conn = dbm.getConnection();
		 
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "";
		 
			int count = 0 ;
			
			//单品统计
			//上衣数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '1'";
			List<String> list = new ArrayList<String>();
			argsList.clear();
			argsList.add(account);
			DataTable dt  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto1 = new ReportDTO();
			dto1.setName("上衣");
			dto1.setType("shangyi");
			dto1.setCount(count+"");
			dto1.setNumber("1");
			dtos.add(dto1);
			//裤装数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '2'";
 			argsList.clear();
			argsList.add(account);
			DataTable dt2  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt2.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto2 = new ReportDTO();
			dto2.setName("裤装");
			dto2.setType("kuzhuang");
			dto2.setCount(count+"");
			dto2.setNumber("2");
			dtos.add(dto2);
			if(sex.equals("0")){
				//裙装数量
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '3'";
				argsList.clear();
				argsList.add(account);
				DataTable dt3  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt3.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto3 = new ReportDTO();
				dto3.setName("裙装");
				dto3.setType("qunzhuang");
				dto3.setCount(count+"");
				dto3.setNumber("3");
				dtos.add(dto3);
			}
			//配饰数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '4'";
			argsList.clear();
			argsList.add(account);
			DataTable dt4  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt4.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto4 = new ReportDTO();
			dto4.setName("配饰");
			dto4.setType("peishi");
			dto4.setCount(count+"");
			dto4.setNumber("4");
			dtos.add(dto4);
			//鞋类数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '5'";
			argsList.clear();
			argsList.add(account);
			DataTable dt5  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt5.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto5 = new ReportDTO();
			dto5.setName("鞋类");
			dto5.setType("xielei");
			dto5.setCount(count+"");
			dto5.setNumber("5");
			dtos.add(dto5);
			//袜子数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '6'";
			argsList.clear();
			argsList.add(account);
			DataTable dt6  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt6.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto6 = new ReportDTO();
			dto6.setName("袜子");
			dto6.setType("wazi");
			dto6.setCount(count+"");
			dto6.setNumber("6");
			dtos.add(dto6);
			//特殊数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '7'";
			argsList.clear();
			argsList.add(account);
			DataTable dt7  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt7.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto7 = new ReportDTO();
			dto7.setName("特殊类服装");
			dto7.setType("teshulei");
			dto7.setCount(count+"");
			dto7.setNumber("7"); 
			dtos.add(dto7);
			//套西数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE  account = ? AND type = '8'";
			argsList.clear();
			argsList.add(account);
			DataTable dt8  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt8.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto8 = new ReportDTO();
			dto8.setName("套装");
			dto8.setType("taoxi");
			dto8.setCount(count+"");
			dto8.setNumber("8");
			dtos.add(dto8);
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dtos;
	}
	
	//衣橱诊断报告 上衣下装统计
	public    List<ReportDTO>  wardrobeReport4(String account ,String sex) {
 		List<ReportDTO> dtos = new ArrayList<ReportDTO>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
  		try {
			conn = dbm.getConnection();
		 
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "";
		 
			int count = 0 ;
			
			//单品统计
			//上衣数量
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND (shangyi = '2' OR shangyi = '19' OR shangyi = '20' OR shangyi = '21' OR shangyi = '22' OR shangyi = '9' OR shangyi = '10' OR shangyi = '5')";
 			argsList.clear();
			argsList.add(account); 
			DataTable dt  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto1 = new ReportDTO();
			dto1.setName("上衣");
 			dto1.setCount(count+"");
			dtos.add(dto1);
			//裙装总数
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND (kuzhuang = '1' or kuzhuang = '2' or kuzhuang = '3' or kuzhuang = '4' or qunzhuang = '1' or qunzhuang = '2')" ;
			DataTable dt2  = dbm.execSqlTrans(sql, argsList, conn);
			count = dt2.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto2 = new ReportDTO();
			dto2.setName("下装");
 			dto2.setCount(count+"");
			dtos.add(dto2);
			//单品总数
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '1'";
			DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
			count = dt3.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto3 = new ReportDTO();
			dto3.setName("单品");
 			dto3.setCount(count+"");
			dtos.add(dto3);
			
			//套装总数
			sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2'";
			DataTable dt4 = dbm.execSqlTrans(sql, argsList, conn);
			count = dt4.getRows().get(0).getInt("COUNT(ID)");
			ReportDTO dto4 = new ReportDTO();
			dto4.setName("套装");
 			dto4.setCount(count+"");
			dtos.add(dto4);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dtos;
	}
	
	//衣橱诊断报告 场景数量统计
		public  List<ReportDTO>  wardrobeReport3(String account ,String sex) {
	 		List<ReportDTO> dtos = new ArrayList<ReportDTO>();
			DatabaseMgr dbm = DatabaseMgr.getInstance();
			Connection conn = null;
			String countflag = "0"; 
			int x = 0;
	  		try {
				conn = dbm.getConnection();
			 
				ArrayList<Object> argsList = new ArrayList<Object>();
				String sql = "";
			 
				int count = 0 ;
				
				//套装统计
				//商务休闲 低配8
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND  scene LIKE '%1%'";
 				argsList.clear();
				argsList.add(account);
				DataTable dt  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto1 = new ReportDTO();
				dto1.setName("商务休闲");
				dto1.setType("scene");
				dto1.setCount(count+"");
				dto1.setNumber("1");
				if(sex.equals("0")){
					x = 12;
				}else {
					x = 8;
				}
				if(count>=x){
					countflag = "1";
				}else {
					countflag = "0";
				}
				dto1.setIsEnough(countflag);
				dtos.add(dto1);
				//私人约会数量 低配2
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND  scene LIKE '%2%'";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt2  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt2.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto2 = new ReportDTO();
				dto2.setName("日常休闲");
				dto2.setType("scene");
				dto2.setCount(count+"");
				dto2.setNumber("2");
				if(sex.equals("0")){
					x = 3;
				}else {
					x = 2;
				}
				if(count>=x){
					countflag = "1";
				}else {
					countflag = "0";
				}
				dto2.setIsEnough(countflag);
				dtos.add(dto2);
				
				//正式晚宴数量 低配2
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND  scene LIKE '%3%'";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt3  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt3.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto3 = new ReportDTO();
				dto3.setName("聚会或晚宴");
				dto3.setType("scene");
				dto3.setCount(count+"");
				dto3.setNumber("3");
				if(sex.equals("0")){
					x = 3;
				}else {
					x = 2;
				}
				if(count>=x){
					countflag = "1";
				}else {
					countflag = "0";
				}
				dto3.setIsEnough(countflag);
				dtos.add(dto3);
				
				//正式商务数量 低配4/6
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND  scene LIKE '%4%'";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt4  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt4.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto4 = new ReportDTO();
				dto4.setName("正式商务");
				dto4.setType("scene");
				dto4.setCount(count+"");
				dto4.setNumber("4");
				if(sex.equals("0")){
					x = 6;
				}else {
					x = 4;
				}
				if(count>=x){
					countflag = "1";
				}else {
					countflag = "0";
				}
				dto4.setIsEnough(countflag);
				dtos.add(dto4);
				
				//风格IP 阳光活力
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%1,%' OR style = '1')";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt5  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt5.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto5 = new ReportDTO();
				if(sex.equals("0")){
					dto5.setName("阳光甜美");

 				}else {
					dto5.setName("阳光温暖");
 				}
				dto5.setType("style");
				dto5.setCount(count+"");
				dto5.setNumber("1");
				dto5.setIsEnough(countflag);
				dtos.add(dto5);
				
				//风格IP 成熟大气
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%2%'  )";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt6  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt6.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto6 = new ReportDTO();
				if(sex.equals("0")){
					dto6.setName("摩登大气");

 				}else {
 					dto6.setName("成熟大气");
 				}
				dto6.setType("style");
				dto6.setCount(count+"");
				dto6.setNumber("2");
				dto6.setIsEnough(countflag);
				dtos.add(dto6);
				
				//风格IP 干练洒脱
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%3%'  )";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt7  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt7.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto7 = new ReportDTO();
				if(sex.equals("0")){
					dto7.setName("阳光帅气");
 				}else {
 					dto7.setName("利落洒脱"); 				}
				
				dto7.setType("style");
				dto7.setCount(count+"");
				dto7.setNumber("3");
				dto7.setIsEnough(countflag);
				dtos.add(dto7);
				if("0".equals(sex)){
					
					//风格IP 高贵典雅
					sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%4%'  )";
					argsList.clear();
					argsList.add(account);
					DataTable dt8  = dbm.execSqlTrans(sql, argsList, conn);
					count = dt8.getRows().get(0).getInt("COUNT(ID)");
					ReportDTO dto8 = new ReportDTO();
					dto8.setName("高贵典雅");
					dto8.setType("style");
					dto8.setCount(count+"");
					dto8.setNumber("4");
					dto8.setIsEnough(countflag);
					dtos.add(dto8);
					
					//风格IP内涵雅致
					sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%5%'  )";
					argsList.clear();
					argsList.add(account);
					DataTable dt9  = dbm.execSqlTrans(sql, argsList, conn);
					count = dt9.getRows().get(0).getInt("COUNT(ID)");
					ReportDTO dto9 = new ReportDTO();
					dto9.setName("内涵雅致");
					dto9.setType("style");
					dto9.setCount(count+"");
					dto9.setNumber("5");
					dto9.setIsEnough(countflag);
					dtos.add(dto9);
				}
				
				//风格IP 浪漫性感
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%6%'  )";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt10  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt10.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto10 = new ReportDTO();
				dto10.setName("浪漫性感");
				dto10.setType("style");
				dto10.setCount(count+"");
				dto10.setNumber("6");
				dto10.setIsEnough(countflag);
				dtos.add(dto10);
				
				//风格IP 自由艺术
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%7%'  )";
	 			argsList.clear();
				argsList.add(account);
				DataTable dt11  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt11.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto11 = new ReportDTO();
				dto11.setName("自由艺术");
				dto11.setType("style");
				dto11.setCount(count+"");
				dto11.setNumber("7");
				dto11.setIsEnough(countflag);
				dtos.add(dto11);
				if("1".equals(sex)){
					
					//风格IP 内涵儒雅
					sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%8%'  )";
					argsList.clear();
					argsList.add(account);
					DataTable dt12  = dbm.execSqlTrans(sql, argsList, conn);
					count = dt12.getRows().get(0).getInt("COUNT(ID)");
					ReportDTO dto12 = new ReportDTO();
					dto12.setName("儒雅内涵");
					dto12.setType("style");
					dto12.setCount(count+"");
					dto12.setNumber("8");
					dto12.setIsEnough(countflag);
					dtos.add(dto12);
					
					//风格IP 内涵儒雅
					sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%9%'  )";
					argsList.clear();
					argsList.add(account);
					DataTable dt13  = dbm.execSqlTrans(sql, argsList, conn);
					count = dt13.getRows().get(0).getInt("COUNT(ID)");
					ReportDTO dto13 = new ReportDTO();
					dto13.setName("尊贵正统");
					dto13.setType("style");
					dto13.setCount(count+"");
					dto13.setNumber("9");
					dto13.setIsEnough(countflag);
					dtos.add(dto13);
					
				}
				//风格IP 自然亲和
				sql = "SELECT COUNT(ID) FROM heso_user_wardrobe WHERE account = ? AND  suit = '2' AND (style LIKE '%0%'  )";
				argsList.clear();
				argsList.add(account);
				DataTable dt14  = dbm.execSqlTrans(sql, argsList, conn);
				count = dt14.getRows().get(0).getInt("COUNT(ID)");
				ReportDTO dto14 = new ReportDTO();
				dto14.setName("自然亲和");
				dto14.setType("style");
				dto14.setCount(count+"");
				dto14.setNumber("0");
				dto14.setIsEnough(countflag);
				dtos.add(dto14);
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return dtos;
		}
    //衣橱诊断报告 二级单品最低配置清单
	public   List<List<String>> wardrobeReport(String account ,String sex) {
		List<List<String>> lists = new ArrayList<List<String>>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		List<ReportDTO> list = new ArrayList<ReportDTO>();
	 
		try {
			conn = dbm.getConnection();
			int countup = 0;
			int countdown = 0;
			ArrayList<Object> argsList = new ArrayList<Object>();
			String sql = "";
			String countstr = "";
			int count = 0 ;
	 
			//分别统计数量
			if("1".equals(sex)){
				//风衣，低配两件
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND shangyi = '13'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				argsList.add(account);
				DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt1.getRows().get(0).getInt("COUNT(ID)");
				List<String> list1 = new ArrayList<String>();
				list1.add("风衣");
				list1.add("shangyi");
				list1.add("13");
				list1.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list1.add(countstr);
				lists.add(list1);
 				//夹克，低配三件
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND shangyi = '14'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt2.getRows().get(0).getInt("COUNT(ID)");
				List<String> list2 = new ArrayList<String>();
				list2.add("夹克");
				list2.add("shangyi");
				list2.add("14");
				list2.add(count+"");
				if(count>=3){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list2.add(countstr);
				lists.add(list2);
				
				//衬衣。低配六件  计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND (shangyi = '19' OR  shangyi = '20')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt3.getRows().get(0).getInt("COUNT(ID)");
				 
				countup = countup + count ;
				List<String> list3 = new ArrayList<String>();
				list3.add("衬衣");
				list3.add("shangyi");
				list3.add("19,20");
				list3.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list3.add(countstr);
				lists.add(list3);
				//T恤，低配八件 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND (shangyi = '21' OR  shangyi = '22')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
			//	argsList.add(account);
				DataTable dt4 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt4.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list4 = new ArrayList<String>();
				list4.add("T恤");
				list4.add("shangyi");
				list4.add("21,22");
				list4.add(count+"");
				if(count>=8){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list4.add(countstr);
				lists.add(list4);
				//套西，低配4套
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND ZHUHE= '1'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt5 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt5.getRows().get(0).getInt("COUNT(ID)");
				List<String> list5 = new ArrayList<String>();
				list5.add("套西");
				list5.add("zhuhe");
				list5.add("1");
				list5.add(count+"");
				if(count>=4){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list5.add(countstr);
				lists.add(list5);
				//休闲裤，低配8 计入下装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND KUZHUANG= '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt6 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt6.getRows().get(0).getInt("COUNT(ID)");
				countdown = countdown + count;
				List<String> list6 = new ArrayList<String>();
				list6.add("休闲裤");
				list6.add("kuzhuang");
				list6.add("4");
				list6.add(count+"");
				if(count>=8){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list6.add(countstr);
				lists.add(list6);
				//领带，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt7 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt7.getRows().get(0).getInt("COUNT(ID)");
				List<String> list7 = new ArrayList<String>();
				list7.add("领带");
				list7.add("peishi");
				list7.add("4");
				list7.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list7.add(countstr);
				lists.add(list7);
				//口袋方巾，低配4
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '5'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
			//	argsList.add(account);
				DataTable dt8 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt8.getRows().get(0).getInt("COUNT(ID)");
				List<String> list8 = new ArrayList<String>();
				list8.add("口袋方巾");
				list8.add("peishi");
				list8.add("5");
				list8.add(count+"");
				if(count>=4){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list8.add(countstr);
				lists.add(list8);
				//袖扣，低配1
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '7'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt9 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt9.getRows().get(0).getInt("COUNT(ID)");
				List<String> list9 = new ArrayList<String>();
				list9.add("袖扣");
				list9.add("peishi");
				list9.add("7");
				list9.add(count+"");
				if(count>=1){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list9.add(countstr);
				lists.add(list9);
				//皮带，低配4
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '8'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt10 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt10.getRows().get(0).getInt("COUNT(ID)");
				List<String> list10 = new ArrayList<String>();
				list10.add("皮带/腰带");
				list10.add("peishi");
				list10.add("8");
				list10.add(count+"");
				if(count>=4){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list10.add(countstr);
				lists.add(list10);
				//包包，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '9'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt11 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt11.getRows().get(0).getInt("COUNT(ID)");
				List<String> list11 = new ArrayList<String>();
				list11.add("包包");
				list11.add("peishi");
				list11.add("9");
				list11.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list11.add(countstr);
				lists.add(list11);
				//手表，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '10'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt12 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt12.getRows().get(0).getInt("COUNT(ID)");
				List<String> list12 = new ArrayList<String>();
				list12.add("手表");
				list12.add("peishi");
				list12.add("10");
				list12.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list12.add(countstr);
				lists.add(list12);
				//太阳眼镜，低配1
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi= '14'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt13 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt13.getRows().get(0).getInt("COUNT(ID)");
				List<String> list13 = new ArrayList<String>();
				list13.add("太阳眼镜");
				list13.add("peishi");
				list13.add("14");
				list13.add(count+"");
				if(count>=1){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list13.add(countstr);
				lists.add(list13);
				//凉鞋，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei= '1'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt14 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt14.getRows().get(0).getInt("COUNT(ID)");
				List<String> list14 = new ArrayList<String>();
				list14.add("凉鞋");
				list14.add("xielei");
				list14.add("1");
				list14.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list14.add(countstr);
				lists.add(list14);
				//单皮鞋，低配3
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei= '2'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt15 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt15.getRows().get(0).getInt("COUNT(ID)");
				List<String> list15 = new ArrayList<String>();
				list15.add("单皮鞋");
				list15.add("xielei");
				list15.add("2");
				list15.add(count+"");
				if(count>=3){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list15.add(countstr);
				lists.add(list15);
				//休闲鞋，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei= '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt16 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt16.getRows().get(0).getInt("COUNT(ID)");
				List<String> list16 = new ArrayList<String>();
				list16.add("休闲鞋");
				list16.add("xielei");
				list16.add("4");
				list16.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list16.add(countstr);
				lists.add(list16);
				//运动袜，低配8
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND wazi= '2'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt17 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt17.getRows().get(0).getInt("COUNT(ID)");
				List<String> list17 = new ArrayList<String>();
				list17.add("运动袜");
				list17.add("wazi");
				list17.add("2");
				list17.add(count+"");
				if(count>=8){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list17.add(countstr);
				lists.add(list17);
				//西装袜，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND wazi= '5'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt18 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt18.getRows().get(0).getInt("COUNT(ID)");
				List<String> list18 = new ArrayList<String>();
				list18.add("西装袜");
				list18.add("wazi");
				list18.add("5");
				list18.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list18.add(countstr);
				lists.add(list18);
				//polo， 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND (shangyi = '9' OR  shangyi = '10')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt19 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt19.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list19 = new ArrayList<String>();
				list19.add("Polo");
				list19.add("shangyi");
				list19.add("9,10");
				list19.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list19.add(countstr);
				//lists.add(list19);
				//西装， 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND  shangyi = '2'  ";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt20 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt20.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list20 = new ArrayList<String>();
				list20.add("西装");
				list20.add("shangyi");
				list20.add("2");
				list20.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list20.add(countstr);
				//西裤短裤牛仔裤 计入下装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND  kuzhuang = '1' AND kuzhuang = '2' AND kuzhuang = '3'  ";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt21 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt21.getRows().get(0).getInt("COUNT(ID)");
				countdown  = countdown + count ;
				 
				//lists.add(list19);
			}else {
				//风衣，低配两件
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND shangyi = '13'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				argsList.add(account);
				DataTable dt1 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt1.getRows().get(0).getInt("COUNT(ID)");
				List<String> list1 = new ArrayList<String>();
				list1.clear();
				list1.add("风衣");
				list1.add("shangyi");
				list1.add("13");
				list1.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list1.add(countstr);
				lists.add(list1);
				//夹克，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND shangyi = '14'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt2 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt2.getRows().get(0).getInt("COUNT(ID)");
				List<String> list2 = new ArrayList<String>();
				list2.add("夹克");
				list2.add("shangyi");
				list2.add("14");
				list2.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list2.add(countstr);
				lists.add(list2);
				//衬衣。低配六件 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND (shangyi = '19' OR  shangyi = '20')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt3 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt3.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list3 = new ArrayList<String>();
				list3.add("衬衣");
				list3.add("shangyi");
				list3.add("19,20");
				list3.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list3.add(countstr);
				lists.add(list3);
				//T恤，低配八件  计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND (shangyi = '21' OR  shangyi = '22')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
			//	argsList.add(account);
				DataTable dt4 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt4.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list4 = new ArrayList<String>();
				list4.add("T恤");
				list4.add("shangyi");
				list4.add("21,22");
				list4.add(count+"");
				if(count>=8){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list4.add(countstr);
				lists.add(list4);
				//套西，低配6套
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND ZHUHE= '1'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt5 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt5.getRows().get(0).getInt("COUNT(ID)");
				List<String> list5 = new ArrayList<String>();
				list5.add("套西");
				list5.add("zhuhe");
				list5.add("1");
				list5.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list5.add(countstr);
				lists.add(list5);
				
				//吊带，低配6 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ? AND shangyi= '5'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt6 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt6.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				List<String> list6 = new ArrayList<String>();
				list6.add("吊带");
				list6.add("shangyi");
				list6.add("5");
				list6.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list6.add(countstr);
				lists.add(list6);
				//休闲裤，低配6 计入下装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND KUZHUANG= '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt7 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt7.getRows().get(0).getInt("COUNT(ID)");
				List<String> list7 = new ArrayList<String>();
				list7.add("休闲裤");
				list7.add("kuzhuang");
				list7.add("4");
				list7.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list7.add(countstr);
				lists.add(list7);
				
				//晚礼服，低配4
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND QUNZHUANG= '3'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt8 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt8.getRows().get(0).getInt("COUNT(ID)");
				List<String> list8 = new ArrayList<String>();
				list8.add("晚礼服");
				list8.add("qunzhuang");
				list8.add("3");
				list8.add(count+"");
				if(count>=4){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list8.add(countstr);
				lists.add(list8);
				//半身裙，低配6 计入下装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND (QUNZHUANG= '1' or QUNZHUANG= '2' )";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt9 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt9.getRows().get(0).getInt("COUNT(ID)");
				List<String> list9 = new ArrayList<String>();
				list9.add("半身裙");
				list9.add("qunzhuang");
				list9.add("1,2");
				list9.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list9.add(countstr);
				lists.add(list9);
				//帽子，低配3
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '1'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt10 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt10.getRows().get(0).getInt("COUNT(ID)");
				List<String> list10 = new ArrayList<String>();
				list10.add("帽子");
				list10.add("peishi");
				list10.add("1");
				list10.add(count+"");
				if(count>=3){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list10.add(countstr);
				lists.add(list10);
				//丝巾，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '2'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt11 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt11.getRows().get(0).getInt("COUNT(ID)");
				List<String> list11 = new ArrayList<String>();
				list11.add("丝巾");
				list11.add("peishi");
				list11.add("2");
				list11.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list11.add(countstr);
				lists.add(list11);
				//丝巾扣，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '6'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt12 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt12.getRows().get(0).getInt("COUNT(ID)");
				List<String> list12 = new ArrayList<String>();
				list12.add("丝巾扣");
				list12.add("peishi");
				list12.add("6");
				list12.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list12.add(countstr);
				lists.add(list12);
				
				//皮带/腰带，低配5
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '8'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt13 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt13.getRows().get(0).getInt("COUNT(ID)");
				List<String> list13 = new ArrayList<String>();
				list13.add("皮带/腰带");
				list13.add("peishi");
				list13.add("8");
				list13.add(count+"");
				if(count>=5){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list13.add(countstr);
				lists.add(list13);
				
				//手表，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '10'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt14 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt14.getRows().get(0).getInt("COUNT(ID)");
				List<String> list14 = new ArrayList<String>();
				list14.add("手表");
				list14.add("peishi");
				list14.add("10");
				list14.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list14.add(countstr);
				lists.add(list14);
				//戒指，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '11'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt15 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt15.getRows().get(0).getInt("COUNT(ID)");
				List<String> list15 = new ArrayList<String>();
				list15.add("戒指");
				list15.add("peishi");
				list15.add("11");
				list15.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list15.add(countstr);
				lists.add(list15);
				
				//手镯，低配5
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '12'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt16 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt16.getRows().get(0).getInt("COUNT(ID)");
				List<String> list16 = new ArrayList<String>();
				list16.add("手镯");
				list16.add("peishi");
				list16.add("12");
				list16.add(count+"");
				if(count>=5){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list16.add(countstr);
				lists.add(list16);
				//耳环，低配10
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '13'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt17 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt17.getRows().get(0).getInt("COUNT(ID)");
				List<String> list17 = new ArrayList<String>();
				list17.add("耳环");
				list17.add("peishi");
				list17.add("13");
				list17.add(count+"");
				if(count>=10){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list17.add(countstr);
				lists.add(list17);
				//太阳眼镜，低配2
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '14'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt18 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt18.getRows().get(0).getInt("COUNT(ID)");
				List<String> list18 = new ArrayList<String>();
				list18.add("太阳眼镜");
				list18.add("peishi");
				list18.add("14");
				list18.add(count+"");
				if(count>=2){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list18.add(countstr);
				lists.add(list18);
				//胸针/胸花，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '16'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt19 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt19.getRows().get(0).getInt("COUNT(ID)");
				List<String> list19 = new ArrayList<String>();
				list19.add("胸针/胸花");
				list19.add("peishi");
				list19.add("16");
				list19.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list19.add(countstr);
				lists.add(list19);
				//项链，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND peishi = '17'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt20 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt20.getRows().get(0).getInt("COUNT(ID)");
				List<String> list20 = new ArrayList<String>();
				list20.add("项链");
				list20.add("peishi");
				list20.add("17");
				list20.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list20.add(countstr);
				lists.add(list20);
				//凉鞋，低配3
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei = '1'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt21 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt21.getRows().get(0).getInt("COUNT(ID)");
				List<String> list21 = new ArrayList<String>();
				list21.add("凉鞋");
				list21.add("xielei");
				list21.add("1");
				list21.add(count+"");
				if(count>=3){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list21.add(countstr);
				lists.add(list21);
				//单皮鞋，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei = '2'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt22 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt22.getRows().get(0).getInt("COUNT(ID)");
				List<String> list22 = new ArrayList<String>();
				list22.add("单皮鞋");
				list22.add("xielei");
				list22.add("2");
				list22.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list22.add(countstr);
				lists.add(list22);
				//休闲鞋，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND xielei = '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt23 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt23.getRows().get(0).getInt("COUNT(ID)");
				List<String> list23 = new ArrayList<String>();
				list23.add("休闲鞋");
				list23.add("xielei");
				list23.add("4");
				list23.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list23.add(countstr);
				lists.add(list23);
				//运动袜，低配8
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND wazi = '2'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt24 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt24.getRows().get(0).getInt("COUNT(ID)");
				List<String> list24 = new ArrayList<String>();
				list24.add("运动袜");
				list24.add("wazi");
				list24.add("2");
				list24.add(count+"");
				if(count>=8){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list24.add(countstr);
				lists.add(list24);
				//裤袜(丝)，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND wazi = '3'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt25 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt25.getRows().get(0).getInt("COUNT(ID)");
				List<String> list25 = new ArrayList<String>();
				list25.add("裤袜(丝)");
				list25.add("wazi");
				list25.add("3");
				list25.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list25.add(countstr);
				lists.add(list25);
				//连衣裙，低配6
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND qunzhuang = '4'";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt26 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt26.getRows().get(0).getInt("COUNT(ID)");
				List<String> list26 = new ArrayList<String>();
				list26.add("连衣裙");
				list26.add("qunzhuang");
				list26.add("4");
				list26.add(count+"");
				if(count>=6){
					countstr = "1";
				}else {
					countstr = "0";
				}
				list26.add(countstr);
				lists.add(list26);
				
				//polo， 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND (shangyi = '9' OR  shangyi = '10')";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt30 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt30.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
		 
				//lists.add(list19);
				//西装， 计入上装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND  shangyi = '2'  ";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt29 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt29.getRows().get(0).getInt("COUNT(ID)");
				countup = countup + count ;
				//西裤短裤牛仔裤 计入下装数量
				sql = "SELECT COUNT(Id) FROM heso_user_wardrobe WHERE  account = ?  AND  kuzhuang = '1' AND kuzhuang = '2' AND kuzhuang = '3'  ";
				sql = sql +" and (SEASON LIKE '%1%' OR SEASON LIKE '%2%')";
				//argsList.add(account);
				DataTable dt28 = dbm.execSqlTrans(sql, argsList, conn);
				count = dt28.getRows().get(0).getInt("COUNT(ID)");
				countdown  = countdown + count ;
			 
			}
		
			String d = "";
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lists;
	}
	
	
	public  List<CheckRecord>   checkYichuBiaoqianOrder(String account,String orderId){
		
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		ArrayList<WardrobeDTO> dtos = new ArrayList<WardrobeDTO>();
		List<CheckRecord> crlist = new ArrayList<CheckRecord>();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "";
			ArrayList<Object> argsList = new ArrayList<Object>();
		 

			argsList.clear();
			sql = "SELECT  * FROM heso_check_record  WHERE ACCOUNT = ? ";
			argsList.add(account);
			if(!orderId.isEmpty()){
				sql = sql + " and ID = ?";
				argsList.add(orderId);
			}
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,argsList, conn);
			for(int i = 0;i<dt.getRows().size();i++){
				String id = dt.getRows().get(i).getString("ID");
				String name = dt.getRows().get(i).getString("NAME");
				String status = dt.getRows().get(i).getString("STATUS");
 				String recordType = dt.getRows().get(i).getString("RECORD_TYPE");
				String iamge = dt.getRows().get(i).getString("IMAGE");
				String count = dt.getRows().get(i).getString("COUNT");
				String price = dt.getRows().get(i).getString("PRICE");
				String amount = dt.getRows().get(i).getString("AMOUNT");
				String creatime = dt.getRows().get(i).getString("CREATETIME");
				CheckRecord record = new CheckRecord();
				record.setId(id);
				record.setName(name);
				record.setStatus(status);
				record.setRecordType(recordType);
				record.setIamge(iamge);
				record.setCount(count);
				record.setPrice(price);
				record.setAmount(amount);
				record.setCreatime(creatime);
				record.setAccount(account);
				List<CheckRecordDetail> crdList = new ArrayList<CheckRecordDetail>();
				
				sql = "SELECT * FROM heso_check_record_detail WHERE record_id = ?";
				argsList.clear();
				argsList.add(id);
				DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql,argsList, conn);
				for(int j = 0;j<dtt.getRows().size();j++){
					  String did =  dtt.getRows().get(j).getString("ID");;
					  String drecordId =  dtt.getRows().get(j).getString("RECORD_ID");
					  String dwardrobeOrderId =  dtt.getRows().get(j).getString("WARDROBEORTRIPID");
					  String dimage =  dtt.getRows().get(j).getString("IMAGE");
					  String dname =  dtt.getRows().get(j).getString("NAME");
					  String dcreatime =  dtt.getRows().get(j).getString("CREATETIME");
					  String drecordType =  dtt.getRows().get(j).getString("RECORD_TYPE");
					  String daccount =  dtt.getRows().get(j).getString("ACCOUNT");
					  String dprice =  dtt.getRows().get(j).getString("PRICE");
					  CheckRecordDetail crd = new CheckRecordDetail();
					  crd.setId(did);
					  crd.setRecordId(drecordId);
					  crd.setWardrobeOrderId(dwardrobeOrderId);
					  crd.setImage(dimage);
					  crd.setName(dname);
					  crd.setCreatime(dcreatime);
					  crd.setRecordType(drecordType);
					  crd.setAccount(daccount);
					  crd.setPrice(dprice);
					  crdList.add(crd);
				}
				record.setCrdList(crdList);
				crlist.add(record);
			}
			return crlist;
			 
 		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return crlist;
		 
	}
	/**
	 * 增加衣橱
	 * 
	 * @param wardrobeDTOs
	 * @return
	 */
	public WardrobeServiceReturnObject addMyWardrobe(List<WardrobeDTO> wardrobeDTOs) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		ArrayList<WardrobeDTO> dtos = new ArrayList<WardrobeDTO>();

		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "";
			ArrayList<Object> argsList = new ArrayList<Object>();
			for (WardrobeDTO dto : wardrobeDTOs) {
				sql = "insert into heso_user_wardrobe (ACCOUNT,IMAGE,TYPE,CLOTH,SCENE,STYLE,COLOR,SUIT,UPLOAD,PATTERN,OUTLINE,NATURE,IS_GOOD,NAME,SEASON)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				argsList.clear();
				argsList.add(dto.getAccount());//

				argsList.add(dto.getImage());//
				argsList.add(dto.getType());//
				argsList.add(dto.getCloth());//
				argsList.add(dto.getScene());//
				argsList.add(dto.getStyle());//
				argsList.add(dto.getColor());//
				argsList.add(dto.getSuit());//
				argsList.add(dto.getUplaod());
				argsList.add(dto.getPattern());//
				argsList.add(dto.getOutline());//
				argsList.add(dto.getCharater());//
				argsList.add(dto.getIsGood());
				argsList.add(dto.getName());
				argsList.add(dto.getSeason());
				if("1".equals(dto.getSuit())){
					sql = "insert into heso_user_wardrobe (ACCOUNT,IMAGE,TYPE,CLOTH,SCENE,STYLE,COLOR,SUIT,UPLOAD,PATTERN,OUTLINE,NATURE,IS_GOOD,NAME,SEASON," +
							dto.getSecondTypeName() +
							")"
							+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					argsList.add(dto.getSecondType());
				}
				logger.info(">>>>>>>添加衣橱物品开始,account = " + dto.getAccount());

				if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
					throw new Exception("100303");

				argsList.clear();
				sql = "SELECT ID FROM heso_user_wardrobe WHERE account = ? ORDER BY ID DESC";
				argsList.add(dto.getAccount());
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				String id = dt.getRows().get(0).getString("ID");
				WardrobeDTO wardrobeDto = new WardrobeDTO();
				wardrobeDto.setId(id);
				dtos.add(wardrobeDto);
				logger.info(">>>>>>>添加衣橱物品成功");
			}
			wsro.setWardrobeList(dtos);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}
	
	//修改衣橱
	public WardrobeServiceReturnObject updateMyWardrobe(List<WardrobeDTO> wardrobeDTOs) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		ArrayList<WardrobeDTO> dtos = new ArrayList<WardrobeDTO>();

		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "";
			ArrayList<Object> argsList = new ArrayList<Object>();
			for (WardrobeDTO dto : wardrobeDTOs) {
				sql = "update heso_user_wardrobe set ACCOUNT=?,IMAGE=?,TYPE=?,CLOTH=?,SCENE=?,STYLE=?,COLOR=?,SUIT=?,UPLOAD=?,PATTERN=?,OUTLINE=?,NATURE=?,IS_GOOD=?,NAME=?,SEASON=? WHERE id=?";
				
				argsList.clear();
				argsList.add(dto.getAccount());//

				argsList.add(dto.getImage());//
				argsList.add(dto.getType());//
				argsList.add(dto.getCloth());//
				argsList.add(dto.getScene());//
				argsList.add(dto.getStyle());//
				argsList.add(dto.getColor());//
				argsList.add(dto.getSuit());//
				argsList.add(dto.getUplaod());
				argsList.add(dto.getPattern());//
				argsList.add(dto.getOutline());//
				argsList.add(dto.getCharater());//
				argsList.add(dto.getIsGood());
				argsList.add(dto.getName());
				argsList.add(dto.getSeason());
				if("1".equals(dto.getSuit())){
					sql = "update heso_user_wardrobe set ACCOUNT=?,IMAGE=?,TYPE=?,CLOTH=?,SCENE=?,STYLE=?,COLOR=?,SUIT=?,UPLOAD=?,PATTERN=?,OUTLINE=?,NATURE=?,IS_GOOD=?,NAME=?,SEASON=?," +
							dto.getSecondTypeName() +
							"=? WHERE id=?";
					
					argsList.add(dto.getSecondType());
				}
				argsList.add(dto.getId());
				logger.info(">>>>>>>添加衣橱物品开始,account = " + dto.getAccount());

				if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
					throw new Exception("100303");

				argsList.clear();
				sql = "SELECT ID FROM heso_user_wardrobe WHERE account = ? ORDER BY ID DESC";
				/*argsList.add(dto.getAccount());
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				String id = dt.getRows().get(0).getString("ID");*/
				WardrobeDTO wardrobeDto = new WardrobeDTO();
				wardrobeDto.setId(dto.getId());
				dtos.add(wardrobeDto);
				logger.info(">>>>>>>添加衣橱物品成功");
			}
			wsro.setWardrobeList(dtos);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}

	public List<String> getStyleName(String style) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		List<String> list = new ArrayList<String>();

		Connection conn = null;
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
					+ style + ");";
			conn = dbm.getConnection();
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			String wstyle1 = "";
			wstyle1 = dt1.getRows().get(0).getString("VALUE");
			String wstyle2 = "";
			wstyle2 = dt1.getRows().get(0).getString("LABEL");
			/*
			 * for(int j = 0;j<dt1.getRows().size();j++){
			 * if(wstyle1.equals("")||wstyle1.isEmpty()){ wstyle1 =
			 * dt1.getRows().get(j).getString("VALUE"); wstyle2 =
			 * dt1.getRows().get(j).getString("LABEL"); }else { wstyle1 =
			 * wstyle1 + " " +dt1.getRows().get(j).getString("VALUE"); wstyle2 =
			 * wstyle2 + " " +dt1.getRows().get(j).getString("LABEL"); }
			 * 
			 * }
			 */
			list.add(wstyle1);
			list.add(wstyle2);

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return list;

	}

	public static List<String> getStyleName2(String ids, Map<String, List<String>> map) {
		List<String> list = new ArrayList<String>();
		String wstyle1 = "";
		String wstyle2 = "";
		String[] sourceStrArray = ids.split(",");
		for (int i = 0; i < sourceStrArray.length; i++) {
			String styleId = sourceStrArray[i].trim();
			for (String key : map.keySet()) {
				if (styleId.equals(key)) {
					if (wstyle1.equals("") || wstyle1.isEmpty()) {
						wstyle1 = map.get(key).get(0);
						wstyle2 = map.get(key).get(1);
					} else {
						wstyle1 = wstyle1 + " " + map.get(key).get(0);
						wstyle2 = wstyle2 + " " + map.get(key).get(1);
					}

				}
			}
		}
		list.add(wstyle1);
		list.add(wstyle2);
		return list;
	}

	/**
	 * 查找衣橱
	 * 
	 * @param account
	 * @param type
	 * @param scene
	 * @param style
	 * @param suit
	 * @param pattern
	 * @param outline
	 * @param charater
	 * @return
	 */
	public WardrobeServiceReturnObject getMyWardRobe(String account,
			String type, String scene, String style, String suit,
			String pattern, String outline, String charater, String wid,
			String isGood, String season) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		ArrayList<Object> argsList = new ArrayList<Object>();
		Connection conn = null;
		ArrayList<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			conn = dbm.getConnection();
			String sql = "SELECT sex FROM heso_account WHERE account  = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dtt3 = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			String sex = dtt3.getRows().get(0).getString("sex");
			sql = "SELECT ID, SEX,NAME,VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' ";
			argsList.clear();
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn); 
			int d = dtt.getRows().size();
			for (int i = 0; i < dtt.getRows().size(); i++) {
				List<String> list = new ArrayList<String>();
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

			sql = "SELECT * FROM heso_user_wardrobe as huw where huw.ACCOUNT = ? ";
			argsList.clear();
			argsList.add(account);
			if (!type.isEmpty() && type != null) {
				sql = sql + " and TYPE = ?";// 类型 裤子、衣服
				argsList.add(type);
			}
			if (!scene.isEmpty() && scene != null) {
				sql = sql + " and SCENE like '%" + scene + "%'";// 场景
				// argsList.add(scene);
			}
			if (!style.isEmpty() && style != null) {
				sql = sql + " and STYLE like '%" + style + "%'";// 风格

			}
			if (!suit.isEmpty() && suit != null) {
				sql = sql + " and SUIT = ?";// 套装是否
				argsList.add(suit);
			}
			if (!pattern.isEmpty() && pattern != null) {
				sql = sql + " and PATTERN = ?";// 款式
				argsList.add(pattern);
			}
			if (!outline.isEmpty() && outline != null) {
				sql = sql + " and OUTLINE = ?";// 轮廓
				argsList.add(outline);
			}
			if (!charater.isEmpty() && charater != null) {
				sql = sql + " and NATURE = ?";// 性格
				argsList.add(charater);
			}
			if (!wid.isEmpty() && wid != null) {
				sql = sql + " and ID = ?";//
				argsList.add(wid);
			}
			if (!isGood.isEmpty() && isGood != null) {
				sql = sql + " and IS_GOOD = ?";//  
				argsList.add(isGood);
			}
			// 季节
			if (!season.isEmpty() && season != null) {
				String[] s = season.split(",");
				String sqll = " and (FIND_IN_SET('" + s[0] + "',SEASON)";
				for (int i = 1; i < s.length; i++) {
					sqll = sqll + " or FIND_IN_SET( '" + s[i] + "',SEASON)";
				}
				sqll = sqll + ") ";
				sql = sql + sqll;
			}
			logger.info(">>>>>>>>>>>查找衣橱物品，account = " + account+"====sql:"+sql);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			int x = dt.getRows().size();
			if (dt.getRows().size() <= 0)
				throw new Exception("001086");
			logger.info(">>>>>>>>>>>查找衣橱物品成功");
			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr1 = dt.getRows().get(i);
				String waccount = dr1.getString("ACCOUNT");
				String wimage = dr1.getString("IMAGE");
				String wtype = dr1.getString("TYPE");
				String wcloth = dr1.getString("CLOTH");
				String wscene = dr1.getString("SCENE");
				String wstyle = dr1.getString("STYLE");
				/*
				 * sql =
				 * "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
				 * + style + ");"; DataTable dt1 =
				 * DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				 * int c = dt1.getRows().size();
				 */
				String wstyle1 = "";
				String wstyle2 = "";
				if (!wstyle.isEmpty()) {
					List<String> list = getStyleName2(wstyle, map);
					wstyle1 = list.get(0);
					wstyle2 = list.get(1);
				}
				String wcolor = dr1.getString("COLOR");
				String wsuit = dr1.getString("SUIT");
				String wupload = dr1.getString("UPLOAD");
				String wpattern = dr1.getString("PATTERN");
				String woutline = dr1.getString("OUTLINE");
				String wcharater = dr1.getString("NATURE");
				String createTime = dr1.getString("CREATETIME");
				String id = dr1.getString("ID");
				String wisGood = dr1.getString("IS_GOOD");
				String wname = dr1.getString("NAME");
				WardrobeDTO dto = new WardrobeDTO();
				dto.setAccount(waccount);
				dto.setCreateTime(createTime);
				dto.setLabel(wstyle2);
				dto.setImpression(wstyle1);
				dto.setImage(wimage);
				dto.setType(wtype);
				dto.setCloth(wcloth);
				dto.setScene(wscene);
				dto.setStyle(wstyle);
				dto.setColor(wcolor);
				dto.setSuit(wsuit);
				dto.setUplaod(wupload);
				dto.setPattern(wpattern);
				dto.setOutline(woutline);
				dto.setId(id);
				dto.setCharater(wcharater);
				dto.setIsGood(wisGood);
				dto.setName(wname);
				wardrobeDTOs.add(dto);
			}
			wsro.setWardrobeList(wardrobeDTOs);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}

	
	
	
	
	public  WardrobeDTO findWardrobeDTO (String  id){
		WardrobeDTO dto = new WardrobeDTO();
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "SELECT * FROM heso_user_wardrobe WHERE id = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.clear();
			argsList.add(id);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			
			if(dt.getRows().size()<=0){
				return dto;
			}
			String account = dt.getRows().get(0).getString("account");
			
			sql = "SELECT sex FROM heso_account WHERE account  = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dtt3 = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			String sex = dtt3.getRows().get(0).getString("sex");
			
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			
			sql = "SELECT ID, VALUE,LABEL,NAME,SEX FROM heso_type WHERE KEYWORD = 'style' ";
			argsList.clear();
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			int d = dtt.getRows().size();
			for (int i = 0; i < dtt.getRows().size(); i++) {
				List<String> list = new ArrayList<String>();
				String value = dtt.getRows().get(i).getString("VALUE");
				String iid = dtt.getRows().get(i).getString("ID");
				 
				String label = dtt.getRows().get(i).getString("LABEL");
				String name = dtt.getRows().get(i).getString("NAME");
				if("1".equals(sex)){
					list.add(value.trim());					
				}else {
					list.add(name.trim());
				} 
				list.add(label.trim());
				map.put(iid, list);
			}
			
			DataRow dr1 = dt.getRows().get(0);
			String waccount = dr1.getString("ACCOUNT");
			String wimage = dr1.getString("IMAGE");
			String wtype = dr1.getString("TYPE");
			
			sql = "SELECT * FROM heso_type WHERE id = ? AND keyword = 'category'";
			argsList.clear();
			argsList.add(wtype);
			DataTable dtttt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			String categoryName = "";
			String secondType = "";
			String secondName = "";
			String secondId = "";
			if(dtttt.getRows().size()>0){
				categoryName = dtttt.getRows().get(0).getString("NAME");
				secondType = dtttt.getRows().get(0).getString("VALUE");
				secondId = dr1.getString(secondType);
				if(secondId!=null){
					sql = "SELECT * FROM heso_category_type where category_type = ? AND ID = ? ";
					argsList.clear();
					argsList.add(secondType);
					argsList.add(secondId);
					DataTable dttttt = DatabaseMgr.getInstance().execSqlTrans(sql,
							argsList, conn);
					if(dtttt.getRows().size()>0){
						secondName = dttttt.getRows().get(0).getString("NAME");
					}
					
				}
			}
 			
			
			
			
			
			String wcloth = dr1.getString("CLOTH")==null?"":dr1.getString("CLOTH");
			String wscene = dr1.getString("SCENE")==null?"":dr1.getString("SCENE");
			String wstyle = dr1.getString("STYLE")==null?"":dr1.getString("STYLE");
			String wseason = dr1.getString("SEASON")==null?"":dr1.getString("SEASON");
			
			
			String wstyle1 = "";
			String wstyle2 = "";
			if (!wstyle.isEmpty()) {
				List<String> list = getStyleName2(wstyle, map);
				wstyle1 = list.get(0);
				wstyle2 = list.get(1);
			}
			String wcolor = dr1.getString("COLOR");
			String wsuit = dr1.getString("SUIT");
			String wupload = dr1.getString("UPLOAD");
			String wisGood = dr1.getString("IS_GOOD");
			String wname = dr1.getString("NAME");
		 
			
			String styleIds = stringForSql(wstyle);
			if(!styleIds.equals("''")){
				sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'style' AND id in (" +
						styleIds +
						") AND (SEX = '2' or SEX = '" +
						sex +
						"')";
				argsList.clear();
				DataTable dtStyle = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dtStyle.getRows().size()>0){
					dto.setStyleName(dtStyle.getRows().get(0).getString("strname"));
				}else {
					dto.setStyleName("");
				}
			}else {
				dto.setStyleName("");
			}
		  
			
			String senceIds = stringForSql(wscene);
			if(!senceIds.equals("''")){
				sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'scene' AND id in (" +
						senceIds +
						") AND (SEX = '2' or SEX = '" +
						sex +
						"')";
				argsList.clear();
				DataTable dtStyle = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dtStyle.getRows().size()>0){
					dto.setSceneName(dtStyle.getRows().get(0).getString("strname"));
				}else {
					dto.setSceneName("");
				}
			}else {
				dto.setSceneName("");
			}
			
			String seasonIds = stringForSql(wseason);
			if(!seasonIds.equals("''")){
				sql = "SELECT KEYWORD,GROUP_CONCAT(NAME) AS strname FROM heso_type WHERE keyWord = 'season' AND id in (" +
						seasonIds +
						") AND (SEX = '2' or SEX = '" +
						sex +
						"')";
				argsList.clear();
				DataTable dtStyle = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
				if(dtStyle.getRows().size()>0){
					dto.setSeasonName(dtStyle.getRows().get(0).getString("strname"));
				}else {
					dto.setSeasonName("");
				}
			}else {
				dto.setSeasonName("");
			}
			
			
			dto.setAccount(waccount);
			dto.setLabel(wstyle2);
			dto.setImpression(wstyle1);
			dto.setImage(wimage);
			dto.setType(wtype);
			dto.setCloth(wcloth);
			dto.setScene(wscene);
			dto.setStyle(wstyle);
			dto.setColor(wcolor);
			dto.setSuit(wsuit);
			dto.setUplaod(wupload);
			dto.setId(id);
			dto.setIsGood(wisGood);
			dto.setName(wname);
			dto.setSex(sex);
			dto.setSeason(wseason);
			
			dto.setSecondType(secondType);
			dto.setSecondTypeId(secondId);
			//dto.setSeasonName(seasonName);
			dto.setSecondTypeName(secondName);
			dto.setCategoryName(categoryName);
			logger.info(">>>>>>>>>>>更新衣橱物品成功");
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dto;
		
		
		
	}
	/**
	 * 单品
	 * @param account
	 * @param type
	 * @param scene
	 * @param style
	 * @param suit
	 * @param pattern
	 * @param outline
	 * @param charater
	 * @param wid
	 * @param isGood
	 * @param season
	 * @return
	 */
	public  WardrobeServiceReturnObject getMyWardRobe2(String account,
			String type, String scene, String style, String suit,
			String pattern, String outline, String charater, String wid,
			String isGood, String season) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		ArrayList<Object> argsList = new ArrayList<Object>();
		Connection conn = null;
		ArrayList<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
		ArrayList<WardrobeDTO> BwardrobeDTOs = new ArrayList<WardrobeDTO>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		try { 
			conn = dbm.getConnection();
			String sql = "SELECT sex FROM heso_account WHERE account  = ?";
			argsList.clear();
			argsList.add(account);
			DataTable dtt3 = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			String sex = dtt3.getRows().get(0).getString("sex");
			
			sql = "SELECT ID, VALUE,LABEL,NAME,SEX FROM heso_type WHERE KEYWORD = 'style' ";
			argsList.clear();
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			int d = dtt.getRows().size();
			for (int i = 0; i < dtt.getRows().size(); i++) {
				List<String> list = new ArrayList<String>();
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
			if(!type.equals("0")){
				sql = "SELECT * FROM heso_type WHERE keyword = 'category' AND id not in(0,8)";
				argsList.clear();
				DataTable dd = dbm.execSqlTrans(sql, argsList, conn);
				for (int j = 0; j < dd.getRows().size(); j++) {
					String category = dd.getRows().get(j).getString("VALUE");
					String categoryId = dd.getRows().get(j).getString("ID");
					String categotyName = dd.getRows().get(j).getString("NAME");
					if(categoryId.equals(type)){
						WardrobeDTO wardrobeDTO = new WardrobeDTO();
						wardrobeDTO.setType(categoryId);
						wardrobeDTO.setTypeName(categotyName);
						sql = "SELECT * FROM heso_user_wardrobe as huw where huw.ACCOUNT = ? and huw."
								+ category
								+ " is not null "
								+ "and huw."
								+ category
								+ " !=''";
						argsList.clear();
						argsList.add(account);
						if (!type.isEmpty() && type != null) {
							sql = sql + " and TYPE = ?";// 类型 裤子、衣服
							argsList.add(type);
						}
						if (!scene.isEmpty() && scene != null) {
							sql = sql + " and SCENE like '%" + scene + "%'";// 场景
							// argsList.add(scene);
						}
						if (!style.isEmpty() && style != null) {
							sql = sql + " and STYLE like '%" + style + "%'";// 风格

						}
						if (!suit.isEmpty() && suit != null) {
							sql = sql + " and SUIT = ?";// 套装是否
							argsList.add(suit);
						}
						if (!pattern.isEmpty() && pattern != null) {
							sql = sql + " and PATTERN = ?";// 款式
							argsList.add(pattern);
						}
						if (!outline.isEmpty() && outline != null) {
							sql = sql + " and OUTLINE = ?";// 轮廓
							argsList.add(outline);
						}
						if (!charater.isEmpty() && charater != null) {
							sql = sql + " and NATURE = ?";// 性格
							argsList.add(charater);
						}
						if (!wid.isEmpty() && wid != null) {
							sql = sql + " and ID = ?";// 性格
							argsList.add(wid);
						}
						if (!isGood.isEmpty() && isGood != null) {
							sql = sql + " and IS_GOOD = ?";// 性格
							argsList.add(isGood);
						}
						// 季节
						if (!season.isEmpty() && season != null) {
							String[] s = season.split(",");
							String sqll = " and (FIND_IN_SET('" + s[0] + "',SEASON)";
							for (int i = 1; i < s.length; i++) {
								sqll = sqll + " or FIND_IN_SET( '" + s[i] + "',SEASON)";
							}
							sqll = sqll + ") ";
							sql = sql + sqll;
						}
						logger.info(">>>>>>>>>>>查找衣橱物品，account = " + account);
						DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,argsList, conn);
						int x = dt.getRows().size();
						if (dt.getRows().size() <= 0)
							continue;
							 
						logger.info(">>>>>>>>>>>查找衣橱物品成功");
						for (int i = 0; i < dt.getRows().size(); i++) {
							DataRow dr1 = dt.getRows().get(i);
							String waccount = dr1.getString("ACCOUNT");
							String wimage = dr1.getString("IMAGE");
							String wtype = dr1.getString("TYPE");
							String wcloth = dr1.getString("CLOTH");
							String wscene = dr1.getString("SCENE");
							String wstyle = dr1.getString("STYLE");
							/*
							 * sql =
							 * "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
							 * + style + ");"; DataTable dt1 =
							 * DatabaseMgr.getInstance().execSqlTrans(sql, argsList,
							 * conn); int c = dt1.getRows().size();
							 */
							String wstyle1 = "";
							String wstyle2 = "";
							if (!wstyle.isEmpty()) {
								List<String> list = getStyleName2(wstyle, map);
								wstyle1 = list.get(0);
								wstyle2 = list.get(1);
							}
							String wcolor = dr1.getString("COLOR");
							String wsuit = dr1.getString("SUIT");
							String wupload = dr1.getString("UPLOAD");
							String wpattern = dr1.getString("PATTERN");
							String woutline = dr1.getString("OUTLINE");
							String wcharater = dr1.getString("NATURE");
							String createTime = dr1.getString("CREATETIME");
							String id = dr1.getString("ID");
							String wisGood = dr1.getString("IS_GOOD");
							String wname = dr1.getString("NAME");
							String wseason = dr1.getString("SEASON");
							String secondType = dr1.getString(category);
							
							WardrobeDTO dto = new WardrobeDTO();
							dto.setAccount(waccount);
							dto.setCreateTime(createTime);
							dto.setLabel(wstyle2);
							dto.setImpression(wstyle1);
							dto.setImage(wimage);
							dto.setSecondTypeName(category);
							dto.setSecondType(secondType);
							
							//类型
							dto.setType(wtype);
							dto.setCloth(wcloth);
							//场景
							dto.setScene(wscene);
							//风格
							dto.setStyle(wstyle);
							dto.setColor(wcolor);
							dto.setSuit(wsuit);
							dto.setUplaod(wupload);
							//身形
							dto.setPattern(wpattern);
							dto.setOutline(woutline);
							dto.setId(id);
							//性格
							dto.setCharater(wcharater);
							//季节
							dto.setSeason(wseason);
							dto.setIsGood(wisGood);
							dto.setName(wname);
							BwardrobeDTOs.add(dto);
						}
						
					}
					
				}
			}else {
				sql = "SELECT * FROM heso_user_wardrobe as huw where huw.ACCOUNT = ? ";
				argsList.clear();
				argsList.add(account);
				if (!type.isEmpty() && type != null) {
					sql = sql + " and TYPE = ?";// 类型 裤子、衣服
					argsList.add(type);
				}
				if (!scene.isEmpty() && scene != null) {
					sql = sql + " and SCENE like '%" + scene + "%'";// 场景
					// argsList.add(scene);
				}
				if (!style.isEmpty() && style != null) {
					sql = sql + " and STYLE like '%" + style + "%'";// 风格

				}
				if (!suit.isEmpty() && suit != null) {
					sql = sql + " and SUIT = ?";// 套装是否
					argsList.add(suit);
				}
				if (!pattern.isEmpty() && pattern != null) {
					sql = sql + " and PATTERN = ?";// 款式
					argsList.add(pattern);
				}
				if (!outline.isEmpty() && outline != null) {
					sql = sql + " and OUTLINE = ?";// 轮廓
					argsList.add(outline);
				}
				if (!charater.isEmpty() && charater != null) {
					sql = sql + " and NATURE = ?";// 性格
					argsList.add(charater);
				}
				if (!wid.isEmpty() && wid != null) {
					sql = sql + " and ID = ?";// 性格
					argsList.add(wid);
				}
				if (!isGood.isEmpty() && isGood != null) {
					sql = sql + " and IS_GOOD = ?";// 性格
					argsList.add(isGood);
				}
				// 季节
				if (!season.isEmpty() && season != null) {
					String[] s = season.split(",");
					String sqll = " and (FIND_IN_SET('" + s[0] + "',SEASON)";
					for (int i = 1; i < s.length; i++) {
						sqll = sqll + " or FIND_IN_SET( '" + s[i] + "',SEASON)";
					}
					sqll = sqll + ") ";
					sql = sql + sqll;
				}
				logger.info(">>>>>>>>>>>查找衣橱物品，account = " + account);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				int x = dt.getRows().size();
				if (dt.getRows().size() <= 0)
					throw new Exception("001086");
				logger.info(">>>>>>>>>>>查找衣橱物品成功");
				for (int i = 0; i < dt.getRows().size(); i++) {
					DataRow dr1 = dt.getRows().get(i);
					String waccount = dr1.getString("ACCOUNT");
					String wimage = dr1.getString("IMAGE");
					String wtype = dr1.getString("TYPE");
					String wcloth = dr1.getString("CLOTH");
					String wscene = dr1.getString("SCENE");
					String wstyle = dr1.getString("STYLE");
					String wseason = dr1.getString("SEASON");
					/*
					 * sql =
					 * "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
					 * + style + ");"; DataTable dt1 =
					 * DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					 * int c = dt1.getRows().size();
					 */
					String wstyle1 = "";
					String wstyle2 = "";
					if (!wstyle.isEmpty()) {
						List<String> list = getStyleName2(wstyle, map);
						wstyle1 = list.get(0);
						wstyle2 = list.get(1);
					}
					String wcolor = dr1.getString("COLOR");
					String wsuit = dr1.getString("SUIT");
					String wupload = dr1.getString("UPLOAD");
					String wpattern = dr1.getString("PATTERN");
					String woutline = dr1.getString("OUTLINE");
					String wcharater = dr1.getString("NATURE");
					String createTime = dr1.getString("CREATETIME");
					String id = dr1.getString("ID");
					String wisGood = dr1.getString("IS_GOOD");
					String wname = dr1.getString("NAME");
					WardrobeDTO dto = new WardrobeDTO();
					dto.setAccount(waccount);
					dto.setCreateTime(createTime);
					dto.setLabel(wstyle2);
					dto.setImpression(wstyle1);
					dto.setImage(wimage);
					dto.setType(wtype);
					dto.setCloth(wcloth);
					dto.setScene(wscene);
					dto.setStyle(wstyle);
					dto.setColor(wcolor);
					dto.setSuit(wsuit);
					dto.setUplaod(wupload);
					dto.setPattern(wpattern);
					dto.setOutline(woutline);
					dto.setId(id);
					dto.setCharater(wcharater);
					dto.setIsGood(wisGood);
					dto.setName(wname);
					BwardrobeDTOs.add(dto);
				}
			}
			
			
			

			wsro.setWardrobeList(BwardrobeDTOs);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	} 
	
	
	
	public  WardrobeServiceReturnObject getSecondWardRobe(String type,String account,String sex,String secondWord,String typeSecondId,String secondName) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		ArrayList<Object> argsList = new ArrayList<Object>();
		Connection conn = null;
		ArrayList<WardrobeDTO> wardrobeDTOs = new ArrayList<WardrobeDTO>();
		ArrayList<WardrobeDTO> BwardrobeDTOs = new ArrayList<WardrobeDTO>();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		try {  
			conn = dbm.getConnection();
			String sql = "";
			 
			
			sql = "SELECT ID, VALUE,LABEL,NAME,SEX FROM heso_type WHERE KEYWORD = 'style' ";
			argsList.clear();
			DataTable dtt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			int d = dtt.getRows().size();
			for (int i = 0; i < dtt.getRows().size(); i++) {
				List<String> list = new ArrayList<String>();
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
			if(!type.equals("0")){
				sql = "SELECT * FROM heso_type WHERE keyword = 'category' AND id not in(0,8)";
				argsList.clear();
				DataTable dd = dbm.execSqlTrans(sql, argsList, conn);
			
					String category = secondWord;
					String categoryId = typeSecondId;
					String categotyName = secondName;
					
						WardrobeDTO wardrobeDTO = new WardrobeDTO();
						wardrobeDTO.setType(categoryId);
						wardrobeDTO.setTypeName(categotyName);
						sql = "SELECT * FROM heso_user_wardrobe as huw where huw.ACCOUNT = ? and huw."
								+ category
								+ " = '"
								+ typeSecondId
								+"'";
						argsList.clear();
						argsList.add(account);
						if (!type.isEmpty() && type != null) {
							sql = sql + " and TYPE = ?";// 类型 裤子、衣服
							argsList.add(type);
						}
						 
						logger.info(">>>>>>>>>>>查找衣橱物品，account = " + account);
						DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,argsList, conn);
						int x = dt.getRows().size();
					 
							 
						logger.info(">>>>>>>>>>>查找衣橱物品成功");
						for (int i = 0; i < dt.getRows().size(); i++) {
							DataRow dr1 = dt.getRows().get(i);
							String waccount = dr1.getString("ACCOUNT");
							String wimage = dr1.getString("IMAGE");
							String wtype = dr1.getString("TYPE");
							String wcloth = dr1.getString("CLOTH");
							String wscene = dr1.getString("SCENE");
							String wstyle = dr1.getString("STYLE");
							/*
							 * sql =
							 * "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
							 * + style + ");"; DataTable dt1 =
							 * DatabaseMgr.getInstance().execSqlTrans(sql, argsList,
							 * conn); int c = dt1.getRows().size();
							 */
							String wstyle1 = "";
							String wstyle2 = "";
							if (!wstyle.isEmpty()) {
								List<String> list = getStyleName2(wstyle, map);
								wstyle1 = list.get(0);
								wstyle2 = list.get(1);
							}
							String wcolor = dr1.getString("COLOR");
							String wsuit = dr1.getString("SUIT");
							String wupload = dr1.getString("UPLOAD");
							String wpattern = dr1.getString("PATTERN");
							String woutline = dr1.getString("OUTLINE");
							String wcharater = dr1.getString("NATURE");
							String createTime = dr1.getString("CREATETIME");
							String id = dr1.getString("ID");
							String wisGood = dr1.getString("IS_GOOD");
							String wname = dr1.getString("NAME");
							String secondType = dr1.getString(category);
							
							WardrobeDTO dto = new WardrobeDTO();
							dto.setAccount(waccount);
							dto.setCreateTime(createTime);
							dto.setLabel(wstyle2);
							dto.setImpression(wstyle1);
							dto.setImage(wimage);
							dto.setSecondTypeName(category);
							dto.setSecondType(secondType);
							dto.setType(wtype);
							dto.setCloth(wcloth);
							dto.setScene(wscene);
							dto.setStyle(wstyle);
							dto.setColor(wcolor);
							dto.setSuit(wsuit);
							dto.setUplaod(wupload);
							dto.setPattern(wpattern);
							dto.setOutline(woutline);
							dto.setId(id);
							dto.setCharater(wcharater);
							dto.setIsGood(wisGood);
							dto.setName(wname);
							BwardrobeDTOs.add(dto);
						}
						
					
					
				
			}else {
				sql = "SELECT * FROM heso_user_wardrobe as huw where huw.ACCOUNT = ? ";
				argsList.clear();
				argsList.add(account);
				if (!type.isEmpty() && type != null) {
					sql = sql + " and TYPE = ?";// 类型 裤子、衣服
					argsList.add(type);
				}
				 
				logger.info(">>>>>>>>>>>查找衣橱物品，account = " + account);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				int x = dt.getRows().size();
				if (dt.getRows().size() <= 0)
					throw new Exception("001086");
				logger.info(">>>>>>>>>>>查找衣橱物品成功");
				for (int i = 0; i < dt.getRows().size(); i++) {
					DataRow dr1 = dt.getRows().get(i);
					String waccount = dr1.getString("ACCOUNT");
					String wimage = dr1.getString("IMAGE");
					String wtype = dr1.getString("TYPE");
					String wcloth = dr1.getString("CLOTH");
					String wscene = dr1.getString("SCENE");
					String wstyle = dr1.getString("STYLE");
					/*
					 * sql =
					 * "SELECT VALUE,LABEL FROM heso_type WHERE KEYWORD = 'style' AND ID in("
					 * + style + ");"; DataTable dt1 =
					 * DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
					 * int c = dt1.getRows().size();
					 */
					String wstyle1 = "";
					String wstyle2 = "";
					if (!wstyle.isEmpty()) {
						List<String> list = getStyleName2(wstyle, map);
						wstyle1 = list.get(0);
						wstyle2 = list.get(1);
					}
					String wcolor = dr1.getString("COLOR");
					String wsuit = dr1.getString("SUIT");
					String wupload = dr1.getString("UPLOAD");
					String wpattern = dr1.getString("PATTERN");
					String woutline = dr1.getString("OUTLINE");
					String wcharater = dr1.getString("NATURE");
					String createTime = dr1.getString("CREATETIME");
					String id = dr1.getString("ID");
					String wisGood = dr1.getString("IS_GOOD");
					String wname = dr1.getString("NAME");
					WardrobeDTO dto = new WardrobeDTO();
					dto.setAccount(waccount);
					dto.setCreateTime(createTime);
					dto.setLabel(wstyle2);
					dto.setImpression(wstyle1);
					dto.setImage(wimage);
					dto.setType(wtype);
					dto.setCloth(wcloth);
					dto.setScene(wscene);
					dto.setStyle(wstyle);
					dto.setColor(wcolor);
					dto.setSuit(wsuit);
					dto.setUplaod(wupload);
					dto.setPattern(wpattern);
					dto.setOutline(woutline);
					dto.setId(id);
					dto.setCharater(wcharater);
					dto.setIsGood(wisGood);
					dto.setName(wname);
					BwardrobeDTOs.add(dto);
				}
			}
			
			
			

			wsro.setWardrobeList(BwardrobeDTOs);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}
	public WardrobeServiceReturnObject updateWardrobe(WardrobeDTO wardrobeDTO) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "update heso_user_wardrobe set TYPE = ? , CLOTH = ? , SCENE = ? , STYLE = ? , "
					+ "COLOR = ?, SUIT = ?, UPLOAD = ?, PATTERN = ?, OUTLINE = ?, NATURE = ? where ID = ? and ACCOUNT = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(wardrobeDTO.getType());
			argsList.add(wardrobeDTO.getCloth());
			argsList.add(wardrobeDTO.getScene());
			argsList.add(wardrobeDTO.getStyle());
			argsList.add(wardrobeDTO.getColor());
			argsList.add(wardrobeDTO.getSuit());
			argsList.add(wardrobeDTO.getUplaod());
			argsList.add(wardrobeDTO.getPattern());
			argsList.add(wardrobeDTO.getOutline());
			argsList.add(wardrobeDTO.getCharater());
			argsList.add(wardrobeDTO.getId());
			argsList.add(wardrobeDTO.getAccount());
			logger.info(">>>>>>>>>>>更新衣橱物品，account=" + wardrobeDTO.getAccount()
					+ "wardrobeId=" + wardrobeDTO.getId());
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("100303");
			logger.info(">>>>>>>>>>>更新衣橱物品成功");
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}

	/**
	 * 删除衣橱衣服
	 * 
	 * @param Id
	 * @return
	 */
	public WardrobeServiceReturnObject delMyWardrobe(String Id) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "delete from heso_user_wardrobe where ID = ?";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(Id);
			if (DatabaseMgr.getInstance().execNonSql(sql, argsList) <= 0)
				throw new Exception("001087");

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;
	}

	/**
	 * 查找用户信息
	 * 
	 * @param account
	 * @return
	 */
	public UserServiceReturnObject getUserProfiles(String account) {
		// 初始化返回对象
		UserServiceReturnObject usro = new UserServiceReturnObject();
		usro.setCode("000000");

		try {
			String sql = "select * from heso_account_profiles where account = ? ";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, argsList);
			if (dt.getRows().size() != 1)
				throw new Exception("100104");

			DataRow dr = dt.getRows().get(0);

			UserProfilesObject upo = new UserProfilesObject();
			upo.setSex(dr.getString("sex"));
			upo.setSexName("");
			upo.setBirthday(dr.getString("birthday"));
			upo.setHeight(dr.getString("height"));
			upo.setWeight(dr.getString("weight"));
			upo.setBust(dr.getString("bust"));
			upo.setWaist(dr.getString("waist"));
			upo.setHip(dr.getString("hip"));
			upo.setImage(dr.getString("image"));
			upo.setShoesize(dr.getString("shoesize"));
			upo.setMarriage(dr.getString("marriage"));
			upo.setShape(dr.getString("shape"));
			upo.setShapeName("");
			upo.setStarsign(dr.getString("starsign"));
			upo.setAnimalsign(dr.getString("animalsign"));
			upo.setFace(dr.getString("face"));
			upo.setFaceName("");
			upo.setHair(dr.getString("hair"));
			upo.setHairName("");
			usro.setUpo(upo);

		} catch (Exception e) {
			// TODO: handle exception
			usro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return usro;
	}

	public WardrobeServiceReturnObject getTripsuit(String account) {
		WardrobeServiceReturnObject wsro = new WardrobeServiceReturnObject();
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		try {
			conn = dbm.getConnection();
			String sql = "SELECT * FROM heso_tripsuit where TRIPCOUNT = (select MAX(TRIPCOUNT)  from heso_tripsuit t WHERE ACCOUNT = ? ) and ACCOUNT =?;";
			ArrayList<Object> argsList = new ArrayList<Object>();
			argsList.add(account);
			argsList.add(account);
			ArrayList<TripsuitDTO> dtos = new ArrayList<TripsuitDTO>();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			if (dt.getRows().size() <= 0)
				throw new Exception("101268");
			logger.info(">>>>>>>>>>>查找衣橱物品成功");
			int x = dt.getRows().size();
			for (int i = 0; i < dt.getRows().size(); i++) {
				DataRow dr1 = dt.getRows().get(i);
				String tripSuitId = dr1.getString("ID");
				String aaccount = dr1.getString("ACCOUNT");
				String tripDate = dr1.getString("TRIPDATE");
				String place = dr1.getString("PLACE");
				String temperature = dr1.getString("TEMPERATURE");
				String scene = dr1.getString("SCENE");
				sql = "SELECT * FROM heso_type WHERE keyword = 'scene' AND ID=?;";
				argsList.clear();
				argsList.add(scene);
				DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				scene = dt1.getRows().get(0).getString("NAME");
				String style = dr1.getString("STYLE");
				if(style == null&&"".equals(style)){
					sql = "SELECT * FROM heso_type WHERE keyword = 'style' AND ID=?;";
					argsList.clear();
					argsList.add(style);
					DataTable dt2 = DatabaseMgr.getInstance().execSqlTrans(sql,
							argsList, conn);
					style = dt2.getRows().get(0).getString("VALUE");
					
				}
				String wardrobeId = dr1.getString("WARDROBEID");
				String productId = dr1.getString("PRODUCTID");
				String flag = dr1.getString("FLAG");
				List<TripsuitDTO> wardrobeList = new ArrayList<TripsuitDTO>();
				if (!wardrobeId.isEmpty() && wardrobeId != null) {
					wardrobeList = getWardrobeDetail(wardrobeId);
				}
				List<TripsuitDTO> productList = new ArrayList<TripsuitDTO>();
				if (!productId.isEmpty() && productId != null) {
					productList = getProductDetail(productId);
				}
				TripsuitDTO dto = new TripsuitDTO();
				dto.setAccount(aaccount);
				dto.setId(tripSuitId);
				dto.setPlace(place);
				dto.setProductId(productId);
				dto.setScene(scene);
				dto.setSuit_flag(flag);
				dto.setTemperatuure(temperature);
				dto.setTripDate(tripDate);
				dto.setStyle(style);
				dto.setWardrobeId(wardrobeId);
				dto.setWardrobeList(wardrobeList);
				dto.setProductList(productList);
				dtos.add(dto);

			}
			wsro.setTripsuitList(dtos);

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return wsro;

	}

	/**
	 * 根据id获取衣橱物品详情
	 * 
	 * @param products
	 * @return
	 */
	public List<TripsuitDTO> getWardrobeDetail(String products) {
		String sql = "SELECT ID,NAME,IMAGE from heso_user_wardrobe where ID in ("
				+ products + ")";
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		List<TripsuitDTO> list = new ArrayList<TripsuitDTO>();

		Connection conn = null;
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			conn = dbm.getConnection();
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
					argsList, conn);
			if (dt.getRows().size() <= 0) {
				logger.error(">>>>>>>>>>>查询衣橱物品失败");
				throw new Exception("001089");
			}
			for (int i = 0; i < dt.getRows().size(); i++) {
				TripsuitDTO td = new TripsuitDTO();
				td.setImage(dt.getRows().get(i).getString("IMAGE"));
				td.setName(dt.getRows().get(i).getString("NAME"));
				td.setWardrobeId(dt.getRows().get(i).getString("ID"));
				list.add(td);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;

	}

	public List<TripsuitDTO> getProductDetail(String products) {

		DatabaseMgr dbm = DatabaseMgr.getInstance();
		List<TripsuitDTO> list = new ArrayList<TripsuitDTO>();

		Connection conn = null;
		ArrayList<Object> argsList = new ArrayList<Object>();
		try {
			String sql = "SELECT PRODUCT_ID,NAME,PRICE,IMG_FRONT ,DISCOUNT_PRICE FROM heso_product WHERE PRODUCT_ID = ?";
			String[] sourceStrArray = products.split(",");
			conn = dbm.getConnection();
			for (int i = 0; i < sourceStrArray.length; i++) {
				TripsuitDTO td = new TripsuitDTO();
				String productId = sourceStrArray[i];
				argsList.clear();
				argsList.add(productId);
				DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql,
						argsList, conn);
				if (dt.getRows().size() <= 0) {
					logger.error(">>>>>>>>>>>查询商品失败");
					throw new Exception("");
				}
				td.setImage(dt.getRows().get(0).getString("IMG_FRONT"));
				td.setName(dt.getRows().get(0).getString("NAME"));
				td.setProductId(dt.getRows().get(0).getString("PRODUCT_ID"));
				td.setPrice(dt.getRows().get(0).getString("PRICE"));
				td.setPrice2(dt.getRows().get(0).getString("DISCOUNT_PRICE"));
				list.add(td);
			}

		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;

	}

	/**
	 * 新增行程套装
	 * 
	 * @param dtos
	 */

	public WardrobeServiceReturnObject addTripsuit(List<TripsuitDTO> dtos,
			String account) {
		DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		SuitMatchArgsObject smao = new SuitMatchArgsObject();
		try {

			// 查询计数器
			ArrayList<Object> argsList1 = new ArrayList<Object>();
			conn = dbm.getConnection();
			String sql1 = "SELECT TRIPCOUNT FROM heso_tripsuit where ID = (select MAX(ID)  from heso_tripsuit t WHERE ACCOUNT = ? ORDER BY ID);";
			argsList1.add(account);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql1,
					argsList1, conn);
			int count = 0;
			if (dt.getRows().size() != 0) {
				count = dt.getRows().get(0).getInt("TRIPCOUNT");
			}
			count = count + 1;
			// 查询用户喜好
			// 根据喜好智能搭配套装

			UserServiceReturnObject usro = getUserProfiles(account);
			String sex = usro.getUpo().getSex();
			if (usro.getCode().equals("000000")) {
				smao.setSex(usro.getUpo().getSex());
				smao.setBust(usro.getUpo().getBust());
				smao.setHeight(usro.getUpo().getHeight());
				smao.setWaist(usro.getUpo().getWaist());
				smao.setHip(usro.getUpo().getHip());
				smao.setYard(usro.getUpo().getShoesize());
				smao.setShape(usro.getUpo().getShape());
				smao.setScene("");
				smao.setStyle("");
				smao.setAge("");
				smao.setName("");
				smao.setBust("");
				smao.setHeight("");
				smao.setWaist("");
				smao.setHip("");
				smao.setYard("");
				smao.setShape("");

			}
			String sql = "insert into heso_tripsuit (STYLE,ACCOUNT,TRIPDATE,SCENE,PLACE,TEMPERATURE,WARDROBEID,PRODUCTID,FLAG,TRIPCOUNT)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
			for (TripsuitDTO dto : dtos) {
				ArrayList<Object> argsList = new ArrayList<Object>();
				String scene = dto.getScene();
				String style = dto.getStyle();
				// 温度换算季节
				String temp = dto.getTemperatuure();
				String season = "";
				if (temp != null && !temp.isEmpty()) {
					int t = Integer.parseInt(temp);
					if (t > 22) {
						season = "2";// 夏
					} else if (15 < t && t <= 22) {
						season = "1,3"; // 春秋
					/*} else if (10 < t && t <= 15) {
						season = "3"; // 秋
*/					} else {
						season = "4";// 冬
					}
				}
				smao.setSeason(season);
				// 获取衣橱符合的物品
				WardrobeServiceReturnObject wsro = getMyWardRobe(account, "",
						scene, style, "2", "", "", "", "", "", season);
				smao.setScene(scene);
				smao.setStyle(style);
				// 智能匹配衣服
				MallServiceReturnObject msro = new MallService().matchTripsuit(
						smao, "0", "3", "", "");
				argsList.clear();
				argsList.add(style);
				argsList.add(dto.getAccount());
				argsList.add(dto.getTripDate());
				argsList.add(dto.getScene());
				argsList.add(dto.getPlace());
				argsList.add(dto.getTemperatuure());
				String flag = "0";
				if (wsro.getWardrobeList() != null
						&& wsro.getWardrobeList().size() != 0) {
					ArrayList<WardrobeDTO> listw = wsro.getWardrobeList();
					Collections.shuffle(listw);
					int y = wsro.getWardrobeList().size();
					/*if (y >= 4) {
						y = 4;
					}*/
					String productIds = "";
					for (int i = 0; i < y; i++) {
						String productId = "";
						productId = listw.get(i).getId();
						if (productIds.isEmpty()) {
							productIds = productId;
						} else {

							productIds = productIds + "," + productId;
						}

					}
					argsList.add(productIds);
					flag = "1";
				} else {
					argsList.add("");
				}
				if (msro.getPioList() != null && msro.getPioList().size() != 0) {
					ArrayList<ProductItemObject> listp = msro.getPioList();
					int x = msro.getPioList().size();
					if (msro.getPioList().size() >= 4) {
						x = 4;
					}
					Collections.shuffle(listp); 
					String productIds = "";
					for (int i = 0; i < x; i++) {

						String productId = "";
						productId = listp.get(i).getProductId();
						if (productIds.isEmpty()) {
							productIds = productId;
						} else {

							productIds = productIds + "," + productId;
						}

					}
					argsList.add(productIds);
				} else {
					argsList.add("");
				}

				argsList.add(flag);
				argsList.add(count);
				int x = DatabaseMgr.getInstance().execNonSql(sql, argsList);
				if (x <= 0)
					throw new Exception("001088");

			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			logger.error(e.getMessage());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new WardrobeServiceReturnObject();
	}

	
	public static String stringForSql(String ids) {
		
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
		List<TripsuitDTO> dtos = new ArrayList<TripsuitDTO>();
		TripsuitDTO dto = new TripsuitDTO();
		dto.setAccount("0000000000000016");
		dto.setPlace("1");
		dto.setProductId("");
		dto.setScene("1");
		dto.setSuit_flag("");
		dto.setTemperatuure("23");
		dto.setTripDate("2017-11-12");
		dto.setStyle("1");
		dto.setWardrobeId("");

		TripsuitDTO dto1 = new TripsuitDTO();
		dto1.setAccount("0000000000000016");
		dto1.setPlace("3");
		dto1.setProductId("");
		dto1.setScene("2");
		dto1.setSuit_flag("");
		dto1.setTemperatuure("23");
		dto1.setTripDate("2017-11-11");
		dto1.setStyle("2");
		dto1.setWardrobeId("");

		dtos.add(dto);
		dtos.add(dto1);
		// addTripsuit(dtos,"0000000000000909");
		WardrobeDTO dto3 = new WardrobeDTO();
		dto3.setAccount("0000000000000909");
		dto3.setImage("");
		dto3.setType("");
		dto3.setCloth("");
		dto3.setScene("");
		dto3.setStyle("");
		dto3.setColor("");
		dto3.setSuit("");
		dto3.setUplaod("");
		dto3.setPattern("");
		dto3.setOutline("");
		dto3.setCharater("");
		dto3.setIsGood("");
		dto3.setName("");
		List<WardrobeDTO> dotsDtos = new ArrayList<WardrobeDTO>();
		dotsDtos.add(dto3);
		/*
		 * WardrobeServiceReturnObject wObject =
		 * getTripsuit("0000000000000016");
		 */
		// List<TripsuitDTO> lsitDtos = getProductDetail("CS00004,CS00003");
		// WardrobeServiceReturnObject s = getTripsuit("0000000000000019");
		// updateWardrobe(wardrobeDTO);
		// WardrobeServiceReturnObject wardrobeServiceReturnObject =
		// addMyWardrobe(dotsDtos);
		// WardrobeServiceReturnObject wsro = getMyWardRobe("0000000000000913",
		// "", "", "", "", "", "", "","","");
		String ids = "1,2";
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();
		list.add("Ming1");
		list.add("kuu1");
		List<String> list2 = new ArrayList<String>();
		list2.add("ming2");
		list2.add("kuu2");
		List<String> list3 = new ArrayList<String>();
		list3.add("ming3");
		list3.add("kuu3");
		map.put("1", list);
		map.put("2", list2);
		map.put("3", list3);
		TripsuitDTO dto2 = new TripsuitDTO();
		dto2.setTemperatuure("24");
		String temp = dto2.getTemperatuure();
		System.out.println("shdjs:" + temp + "ds");
		String season = "";
		String sqll = "sql";
		if (temp != null && !temp.isEmpty()) {
			int t = Integer.parseInt(temp);
			if (t > 28) {
				season = "2";// 夏
			} else if (15 < t && t <= 28) {
				season = "1,3"; // 春秋
			} else if (10 < t && t <= 15) {
				season = "3"; // 秋
			} else {
				season = "4";// 冬
			}

		}
		if (!season.isEmpty()) {
			String[] s = season.split(",");
			String sql = "(FIND_IN_SET('" + s[0] + "',SEASON)";
			for (int i = 1; i < s.length; i++) {
				sql = sql + " or FIND_IN_SET( '" + s[i] + "',SEASON)";
			}
			sql = sql + ")";
			sqll = sqll + sql;
		}
		System.out.println(sqll);
		//WardrobeServiceReturnObject s = getMyWardRobe2("0000000000000913", "0", "", "", "1", "", "", "", "", "", "");
		// List<String> ll = getStyleName2(ids,map);
		// List<String> list = getStyleName("1,9");
		// WardrobeServiceReturnObject wsro = getMyWardRobe("0000000000000913",
		// "", "", "", "", "", "", "","","");
		// delMyWardrobe("2");
		// WardrobeServiceReturnObject
		// wardrobeServiceReturnObject=getTypeAndCount("0000000000000909","2","0");
		//List<ReportDTO>  dtos2 =	wardrobeReport2("0000000000000909", "2");
		//List<ReportDTO>  dtos2 = wardrobeReport3 ("0000000000000909", "0");
		//WardrobeDTO dto4 = findWardrobeDTO("3045");
		System.out.println("----");
	}
}
