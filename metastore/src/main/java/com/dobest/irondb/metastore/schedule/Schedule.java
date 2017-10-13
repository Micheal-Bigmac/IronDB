package com.dobest.irondb.metastore.schedule;


public interface Schedule {

	long period();
	void start();
	void stop();
	String getName();
	void remove();
	
	
}
