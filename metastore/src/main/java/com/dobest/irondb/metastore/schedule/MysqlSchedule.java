package com.dobest.irondb.metastore.schedule;

public class MysqlSchedule extends ScheduleAdapter {

	private Processor mysqlProcessor;
	
	public MysqlSchedule(Processor mysqlProcessor) {
		super();
		this.mysqlProcessor = mysqlProcessor;
	}


	@Override
	public void start() {
		mysqlProcessor.processHandler();
	}

	
}
