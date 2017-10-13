package com.dobest.irondb.metastore.schedule;

public class HbaseSchedule extends ScheduleAdapter {

	private Processor hbaseProcessor;
	
	public HbaseSchedule(Processor hbaseProcessor) {
		super();
		this.hbaseProcessor = hbaseProcessor;
	}

	@Override
	public void start() {
		hbaseProcessor.processHandler();
	}

}
