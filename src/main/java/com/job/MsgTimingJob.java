package com.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MsgTimingJob implements Job {
	Logger logger = Logger.getLogger(Object.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

	}
}
