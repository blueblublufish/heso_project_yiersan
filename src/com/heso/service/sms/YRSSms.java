package com.heso.service.sms;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.heso.db.DatabaseMgr;
import com.heso.service.message.MessageService;
import com.heso.service.message.entity.MessageObject;
import com.heso.service.message.entity.MessageServiceReturnObject;
import com.heso.utility.ErrorProcess;

import data.DataTable;

public class YRSSms extends MessageService {
	private static final Log logger = LogFactory.getLog(SmsService.class);
	/**
	 * 获取收到信息
	 * @param account
	 * @param accountSource
	 * @return
	 */
	public MessageServiceReturnObject readMessage(String account ,Integer page , Integer pageSize ,String  status){
		MessageServiceReturnObject msro = new MessageServiceReturnObject();
		Connection conn = DatabaseMgr.getInstance().getConnection();
		try {
			String sql = "update heso_message set status = '1'  where account_target = ? and status = '0' ";
			List<Object> list = new ArrayList<Object>();
			list.add(account);
			DatabaseMgr.getInstance().execNonSqlTrans(sql , list, conn);
			 
			//获取新的消息
					 sql = " select ha.image , ha.user_id , hm.message , hm.status , hm.create_time , hm.account_source , hm.account_target , hm.message_type " +
						     	 " from heso_message hm left outer join heso_account ha "
						       + " on (hm.account_source = ha.account) " +
							     " where hm.account_target = ?  " +
							     " order by create_time desc " +
							     " limit ? , ? " ;
				
			int index = (page - 1) * pageSize;
			//接收信息
			list.clear();
			list.add(account);
			list.add(index);
			list.add(pageSize);
			DataTable dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
			
			if(dt.getRows().size() <= 0)
				throw new Exception("101128");
			
			List<MessageObject> readMessages = new ArrayList<MessageObject>();
			MessageObject mo = new MessageObject();
			
			
		
			readMessages.add(mo);
				for(int i = 0 ; i < dt.getRows().size() ; i++){
					MessageObject message = new MessageObject();
					message.setAccountSource(dt.getRows().get(i).getString("account_source"));
					message.setAccountTarget(dt.getRows().get(i).getString("account_target"));
					message.setMessage(dt.getRows().get(i).getString("message"));
					message.setCreateTime(dt.getRows().get(i).getString("create_time"));
					message.setSourceUserId(dt.getRows().get(i).getString("user_id"));
					message.setImage(dt.getRows().get(i).getString("image"));
					message.setMessageType(dt.getRows().get(i).getString("message_type"));
					message.setStatus(dt.getRows().get(i).getString("status"));
					readMessages.add(message);
				}
				
				sql = "select count(message) total from heso_message where account_target = ? ";
				list.clear();
				list.add(account);
				dt = DatabaseMgr.getInstance().execSqlTrans(sql, list, conn);
				mo.setCount(dt.getRows().get(0).getInt("total"));
			msro.setMessages(readMessages);

		} catch (Exception e) {
			// TODO: handle exception
			msro.setCode(String.valueOf(ErrorProcess.execute(e.getMessage())));
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
		return msro;
	}
}
