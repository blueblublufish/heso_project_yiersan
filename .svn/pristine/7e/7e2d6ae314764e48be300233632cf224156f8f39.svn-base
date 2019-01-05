import com.heso.utility.HttpUtil;


public class test1 {
	
	public static void main(String[] args) {
		//String url ="http://112.74.98.190:8081/heso_project_yiersan/servlet/TransactionServlet";
	 String url = "http://127.0.0.1:8080/heso_project_yiersan/servlet/TransactionServlet";
	 
	StringBuffer sb = new StringBuffer();
	sb.append("<?xml version='1.0' encoding='utf-8'?>");
	sb.append("<message>");
	sb.append("<head>");
	sb.append("<type>000000</type>");
	sb.append("<messageId>21354</messageId>");
	sb.append("<agentId>02</agentId>");
	sb.append("<digest>MD5</digest>");
	sb.append("</head>");
	sb.append("<body>");
	sb.append("<account></account>");
	sb.append("<scene></scene>");
	sb.append("<style></style>");
	sb.append("<name>Ì××°</name>");
	sb.append("<recStart> </recStart>");
	sb.append("<recCount> </recCount>");
	sb.append("<orderBy>   </orderBy>");
	sb.append("<category> </category>");
	sb.append("<type>2</type>");
	sb.append("</body>");
	sb.append("</message>");
	String s=sb.toString().trim();
	StringBuffer ss = HttpUtil.submitPost(url, sb.toString());
	}

}
