package com.dobest.irondb.metastore.schedule;

import com.dobest.irondb.metastore.bean.ResultCode;

public class HbaseProcessor implements Processor {

	private ResultCode code;
	private String tableName;
	
	public HbaseProcessor(ResultCode code, String tableName) {
		super();
		this.code = code;
		this.tableName = tableName;
	}

	@Override
	public void processHandler() {
//		HbaseMonitor.DropOption(code, tableName);
	}

}
