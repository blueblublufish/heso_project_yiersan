package quartz;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import quartz.job.Upload;

public class QuartzService implements Job {
	/**
	 * 发后七天后自动收货
	 * 0 0/5 * * * ?
	 */
	private static final Log logger = LogFactory.getLog(QuartzService.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Upload upload=new Upload();
		
		// TODO Auto-generated method stub
		logger.info("启动:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		//暂时停用稍后恢复
		/*upload.point2();//发货后12天定时更新会员消费额
		Upload.goods();
		Upload.coin();
		Upload.point();
		upload.statisticsLevel();//更新用户等级表
		Upload.statisticsRank();//更新表rank 
*/		logger.info("关闭:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

	
}
