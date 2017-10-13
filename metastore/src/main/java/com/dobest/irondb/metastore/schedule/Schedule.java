package com.dobest.irondb.metastore.schedule;


import java.util.Date;

public interface Schedule {

	long period();
	void start();
	void stop();
	String getName();
	void remove();
	
	
}
