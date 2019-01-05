package com.heso.service.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.heso.common.GlobalCache;
import com.heso.db.DatabaseMgr;
import com.heso.service.system.entity.SystemTypeObject;

import data.DataException;
import data.DataRow;
import data.DataTable;

public class SystemType {
	private static HashMap<String, ArrayList<SystemTypeObject>> hmType = null;
	
	public static void initialize() throws Exception {
		Connection conn = DatabaseMgr.getInstance().getConnection();
		// 载入系统类型数据到全局缓存
		if (hmType == null)
			hmType = new HashMap<String, ArrayList<SystemTypeObject>>();

		hmType.clear();
		String sql = "select * from heso_type_keyword ";
		ArrayList<Object> argsList = new ArrayList<Object>();

		DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
		for (int i = 0; i < dt.getRows().size(); i++) {
			String keyword = dt.getRows().get(i).getString("keyword");
			ArrayList<SystemTypeObject> stoList = new ArrayList<SystemTypeObject>();
			sql = "select * from heso_type where keyword =?";
			argsList.clear();
			argsList.add(keyword);
			DataTable dt1 = DatabaseMgr.getInstance().execSqlTrans(sql, argsList, conn);
			for (int j = 0; j < dt1.getRows().size(); j++) {
				DataRow dr = dt1.getRows().get(j);
				SystemTypeObject sto = new SystemTypeObject();
				sto.setId(dr.getString("id"));
				sto.setName(dr.getString("name"));
				sto.setValue(dr.getString("value"));
				sto.setImage(dr.getString("image"));
				stoList.add(sto);
			}
			hmType.put(keyword.toUpperCase(), stoList);
		}

		// 写入缓存
		GlobalCache.getInstance().setValue(GlobalCache.SYSTEM_TYPE, "SystemType", hmType);
		if(conn!=null){
			conn.close();
		}
	}

	/**
	 * 返回系统类型列表对象
	 * 
	 * @return
	 */
	public static HashMap<String, ArrayList<SystemTypeObject>> getSystemTypeList() {
		return (HashMap<String, ArrayList<SystemTypeObject>>) GlobalCache.getInstance().getValue(GlobalCache.SYSTEM_TYPE, "SystemType");
	}

	/**返回类型定义说明
	 * @param keywordName
	 * @param key
	 * @return
	 */
	public static String getValue(String keywordName, String key) {
		try {
			ArrayList<SystemTypeObject> stoList = getSystemTypeList().get(keywordName);
			for (int i = 0; i < stoList.size(); i++) {
				SystemTypeObject sto = stoList.get(i);
				if (sto.getId().equals(key))
					return sto.getName();

			}
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	/**根据关键字返回类型列表
	 * @param keywordName
	 * @return
	 */
	public static ArrayList<SystemTypeObject> getTypeList(String keywordName) {
		return getSystemTypeList().get(keywordName.toUpperCase());
	}

	/**
	 * 重载数据
	 * 
	 * @throws Exception
	 */
	public static void reload() throws Exception {
		initialize();
	}

	/**
	 * @param args
	 * @throws DataException
	 * @throws SQLException
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// GlobalCache.Initialize();
		SystemType.initialize();
		System.out.println(SystemType.getValue("CATEGORY", "1"));

		// SystemType.reload();
		//System.out.println(SystemType.getValue("SCENE", "1"));
	}
}
