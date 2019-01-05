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
	 * ����������Զ��ջ�
	 * 0 0/5 * * * ?
	 */
	private static final Log logger = LogFactory.getLog(QuartzService.class);
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Upload upload=new Upload();
		
		// TODO Auto-generated method stub
		logger.info("����:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
		//��ʱͣ���Ժ�ָ�
		/*upload.point2();//������12�춨ʱ���»�Ա���Ѷ�
		Upload.goods();
		Upload.coin();
		Upload.point();
		upload.statisticsLevel();//�����û��ȼ���
		Upload.statisticsRank();//���±�rank 
*/		logger.info("�ر�:"+new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date())+"***********");
	}

	
}
