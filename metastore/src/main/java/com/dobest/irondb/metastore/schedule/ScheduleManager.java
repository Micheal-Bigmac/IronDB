package com.dobest.irondb.metastore.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.apache.log4j.Logger;

public class ScheduleManager {
    private static Logger logger = Logger.getLogger(ScheduleManager.class);

	private List<Schedule> schedules=new ArrayList<>();
	private Map<String, Timer> timers=new HashMap<>();
	
	public void trigger() {
		
	}

}
