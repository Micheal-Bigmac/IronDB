package com.dobest.irondb.metastore.schedule;

import com.dobest.irondb.metastore.bean.ResultCode;

public class ESProcessor implements Processor{
	private ResultCode code;
	private String typeName;
	

	public ESProcessor(ResultCode code, String typeName) {
		super();
		this.code = code;
		this.typeName = typeName;
	}

	@Override
	public void processHandler() {
		// TODO Auto-generated method stub
//		ESTaskMonitor.DropOption(code, typeName);
	}

}
