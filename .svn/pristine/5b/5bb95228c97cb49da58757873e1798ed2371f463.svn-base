package com.heso.transaction.outer;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.yiersanSystem.JobType;
import com.heso.service.yiersanSystem.entity.TypeObject;
import com.heso.service.yiersanSystem.entity.TypeServiceReturnObject;
import com.heso.transaction.AbstractInterfaceClass;
/**
 * 获取职位描述
 * @author 
 *
 */
public class Transaction_001033 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001033.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String keyWordName = arg0.selectSingleNode("keyWordName").getText();
			//处理信息
			TypeServiceReturnObject tsro = JobType.execute(keyWordName);
			//返回信息
			StringBuffer data = new StringBuffer();
			if("000000".equals(tsro.getCode())){
				List<TypeObject> list = tsro.getType();
				for(TypeObject to : list){
					data.append("<jobs>");
					data.append("<id>"+to.getId()+"</id>");
					data.append("<des>"+to.getDes()+"</des>");
					data.append("<name>"+to.getName()+"</name>");
					data.append("<requirement>"+to.getRequirement()+"</requirement>");
					data.append("</jobs>");
				}
			}
			String xmlStrBody = super.buildResp(tsro.getCode(), data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
