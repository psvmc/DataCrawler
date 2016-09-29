package com.utils.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzTest {
	public static void main(String[] args) {

		TestJob job = new TestJob();
		String job_name = "11";
		try {
			System.out.println("【系统启动】");
			ZJ_QuartzUtils.addJob(job_name, job, "0/5 * * * * ?");

			Thread.sleep(10000);
			System.out.println("【修改时间】");
			ZJ_QuartzUtils.modifyJobTime(job_name, "0/10 * * * * ?");
			Thread.sleep(20000);
			System.out.println("【移除定时】");
			ZJ_QuartzUtils.removeJob(job_name);
			Thread.sleep(10000);

			System.out.println("\n【添加定时任务】");
			ZJ_QuartzUtils.addJob(job_name, job, "0/5 * * * * ?");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ZJ_QuartzUtils.addJob("job2", new Job() {

				@Override
				public void execute(JobExecutionContext arg0) throws JobExecutionException {

				}
			}, "0/5 * * * * ?");
		} catch (Exception e) {
			
		}
	}
}
