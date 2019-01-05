package quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import quartz.job.Upload;

public class UploadCoins implements Job{
	private static final Log logger = LogFactory.getLog(UploadCoins.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.info("启动:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		//暂时停用稍后回复
		Upload.coin();
		logger.info("关闭:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

}
 