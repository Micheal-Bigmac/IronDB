package com.dobest.irondb.metastore.schedule;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.dobest.irondb.metastore.bean.ResultCode;
import com.dobest.irondb.metastore.executor.MysqlTakMonitor;
import com.dobest.irondb.metastore.mysql.IronDBMetaDataCache;

public class MysqlProcessor implements Processor {
	private ResultCode code;
	private String tableName;
	private IronDBMetaDataCache metaDataCache;
	private GenericObjectPool<Connection> connectPool;
	private ExecutorService executorService;
	

	public MysqlProcessor(ResultCode code, String tableName, IronDBMetaDataCache metaDataCache,
			GenericObjectPool<Connection> connectPool, ExecutorService executorService) {
		super();
		this.code = code;
		this.tableName = tableName;
		this.metaDataCache = metaDataCache;
		this.connectPool = connectPool;
		this.executorService = executorService;
	}


	@Override
	public void processHandler() {
		// TODO Auto-generated method stub
//		MysqlTakMonitor.DropOptions(code, tableName, metaDataCache, connectPool, executorService);
	}

}
