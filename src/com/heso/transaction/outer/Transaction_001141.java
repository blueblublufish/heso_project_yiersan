package com.heso.transaction.outer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Node;

import com.heso.service.order.consume.ConsumeOrder;
import com.heso.service.order.consume.entity.ConsumeOrderObject;
import com.heso.service.order.consume.entity.ConsumeOrderReturnObject;
import com.heso.transaction.AbstractInterfaceClass;

/**
 * 新增标签派单
 * @author qinjianzhao
 *
 */
public class Transaction_001141 extends AbstractInterfaceClass {
	private static final Log logger = LogFactory.getLog(Transaction_001141.class);
	@Override
	public String execute(Node arg0, String arg1) {
		// TODO Auto-generated method stub
		try {
			//获取信息
			String productIds = arg0.selectSingleNode("productIds").getText().trim();
			String teacherId = arg0.selectSingleNode("teacherId").getText();
			String teacherName = arg0.selectSingleNode("teacherName").getText();
			//验证token
			 
			//处理信息
			List<String> Strs = new ArrayList<String>();
			StringBuffer data = new StringBuffer();
			String[] strList = productIds.split(";");
			for(int i =0 ; i<strList.length ; i++){
				Strs.add(strList[i]);
			}
				String code = new ConsumeOrder().addBiaoqianPaidan(Strs, teacherId, teacherName);
				 
			String xmlStrBody = super.buildResp(code, data.toString());
			return xmlStrBody;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return super.ERROR_RETURN;
	}

}
