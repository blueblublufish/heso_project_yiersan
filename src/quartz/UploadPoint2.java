package quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import quartz.job.Upload;

public class UploadPoint2 implements Job{
	private static final Log logger = LogFactory.getLog(UploadPoint2.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.info("����:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		//��ʱͣ���Ժ�ظ�
		Upload.point2();
		logger.info("�ر�:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

}
 