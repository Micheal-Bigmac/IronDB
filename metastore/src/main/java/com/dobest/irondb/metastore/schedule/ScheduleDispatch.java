package com.dobest.irondb.metastore.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/***
 *
 *  schedule  定时任务调度
 */
public class ScheduleDispatch extends AbstractSchedule{
    private Logger logger= LoggerFactory.getLogger(ScheduleDispatch.class);
    private Processor esProcessor;

	public ScheduleDispatch(Processor processor, long period, ScheduleManager manager) {
		super();
		setPeriod_Time(period);
		setScheduleManager(manager);
		this.esProcessor = processor;
	}

	@Override
	public void start() {
	    logger.info("start task");
		esProcessor.processHandler();
		logger.info("end task");
	}

}
