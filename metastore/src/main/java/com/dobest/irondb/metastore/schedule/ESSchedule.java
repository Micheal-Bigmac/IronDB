package com.dobest.irondb.metastore.schedule;

public class ESSchedule extends ScheduleAdapter{
	private Processor esProcessor;

	
	public ESSchedule(Processor esProcessor) {
		super();
		this.esProcessor = esProcessor;
	}


	@Override
	public void start() {
		esProcessor.processHandler();
	}

}
