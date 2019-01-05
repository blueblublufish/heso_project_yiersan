import com.heso.utility.HttpUtil;


public class test2 {
	
	public static void main(String[] args) {
		//String url ="http://112.74.98.190:8081/heso_project_yiersan/servlet/TransactionServlet";
	 String sql = "select * from heso_product where type = 2 and status='1'  order by create_time desc";
		//sql = sql.replace("order by suit_price desc ", "order by create_time desc");
		sql = sql.replace("order by create_time desc", "order by suit_price desc");
		System.out.println(sql);
	}

}
