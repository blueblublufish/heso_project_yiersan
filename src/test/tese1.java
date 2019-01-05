package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
 
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.heso.db.DatabaseMgr;
import com.heso.service.system.SystemType;
import com.heso.service.system.entity.SystemTypeObject;


 

public class tese1 {
	public static void main(String[] args) throws SQLException {
		
		ArrayList<String> list = new  ArrayList<>();
		list.add("ßã");
		list.add("ßè");
		list.add("¸Â");
		list.add("Îû");
		list.add("¹þ");
		list.add("ºô");
		System.out.println(list.get(0));
		Collections.shuffle(list);
 		/*DatabaseMgr dbm = DatabaseMgr.getInstance();
		Connection conn = null;
		conn = dbm.getConnection();
		conn.setAutoCommit(false);
		conn.close();*/
		System.out.println(list.get(0));
		
	}
}
