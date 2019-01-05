package quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import quartz.job.Upload;

public class UploadOrder implements Job{
	private static final Log logger = LogFactory.getLog(UploadOrder.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		logger.info("����:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		//��ʱͣ���Ժ�ظ�
		Upload.payType();
		logger.info("�ر�:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

}
 