package quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import quartz.job.Upload;

public class UserCouponService implements Job {
	/**
	 * �賿0��0���Զ��ж��û��Ż�ȯ�Ƿ����
	 * 0 0 0 * * ?
	 */
	private static final Log logger = LogFactory.getLog(UserCouponService.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Upload upload=new Upload();
		
		// TODO Auto-generated method stub
		logger.info("����:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		Upload.userCoupon();
		logger.info("�ر�:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

	
}
