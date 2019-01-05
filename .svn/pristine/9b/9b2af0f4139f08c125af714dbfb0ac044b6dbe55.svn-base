package com.heso.service.yiersanSystem;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.db.DatabaseMgr;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;
import com.heso.service.yiersanSystem.entity.TypeServiceReturnObject;
import com.heso.service.yiersanSystem.entity.TypeObject;
import com.heso.utility.ErrorProcess;

import data.DataTable;

public class JobType extends SystemType {
	private static final Log logger = LogFactory.getLog(JobType.class);
	/**
	 * 获取职业类型
	 * @param keywordName
	 * @return
	 */
	public static TypeServiceReturnObject execute (String keywordName){
		TypeServiceReturnObject tsro = new TypeServiceReturnObject();
		try{
			List<SystemTypeObject> systemType = getTypeList(keywordName);
			String sql = " select name , des , requirement from heso_job where status = '0' ";
			List<Object> agrsList = new ArrayList<Object>();
			DataTable dt = DatabaseMgr.getInstance().execSql(sql, agrsList);
			if(dt.getRows().size() <=0 )
				throw new Exception("102168");
			List<TypeObject>list = new ArrayList<TypeObject>();
			for(int i = 0 ; i<systemType.size() ; i++){
				TypeObject to = new TypeObject();
				String id = systemType.get(i).getId();
				String name = systemType.get(i).getName();
				to.setId(id);
				to.setName(name);
				for(int in = 0; in< dt.getRows().size();in++){
					if(id.equals(dt.getRows().get(in).getString("name"))){
						to.setDes(dt.getRows().get(in).getString("des"));
						to.setRequirement(dt.getRows().get(in).getString("requirement"));
					}
				}
				list.add(to);
			}
			tsro.setType(list);
			
		}catch(Exception e){
			logger.error(e.getMessage());
			tsro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
			e.printStackTrace();
		}
		return tsro;
	}
	public static void main(String[] args) {
		TypeServiceReturnObject tsro = JobType.execute("APPLY_JOB");
		List<TypeObject> list = tsro.getType();
		for(TypeObject to : list){
			System.out.println(to.toString());
		}
		
	}
}
