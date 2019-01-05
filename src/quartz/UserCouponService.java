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
	 * 凌晨0点0分自动判断用户优惠券是否过期
	 * 0 0 0 * * ?
	 */
	private static final Log logger = LogFactory.getLog(UserCouponService.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Upload upload=new Upload();
		
		// TODO Auto-generated method stub
		logger.info("启动:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		Upload.userCoupon();
		logger.info("关闭:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

	
}
