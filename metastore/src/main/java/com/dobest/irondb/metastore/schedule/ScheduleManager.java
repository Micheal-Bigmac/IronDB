package com.dobest.irondb.metastore.schedule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/***
 *  这个类需要在 代码中 添加 Hook 关闭时 关闭线程队列 或者 添加到 spring bean 中 
 * 
 * 相关日志 暂未添加详细信息 
 * @author Administrator
 *
 */

public class ScheduleManager {
    private static Logger logger = Logger.getLogger(ScheduleManager.class);
    
	private List<Schedule> schedules=new ArrayList<>();
	private Map<String, Timer> timers=new ConcurrentHashMap();
	
	
	public List<Schedule> getSchedules() {
		return schedules;
	}


	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}


	public Map<String, Timer> getTimers() {
		return timers;
	}


	public void setTimers(Map<String, Timer> timers) {
		this.timers = timers;
	}

	public void trigger() {
		for(Schedule schedule : schedules) {
			Timer timer=new Timer();
			TimerTask task=new TimerTask() {
				@Override
				public void run() {
					schedule.start();
				}
			};
			long period = schedule.period();
			Date startDate=new Date(period);
			timer.schedule(task,startDate);
			timers.put(schedule.getName(), timer);
		}
	}
	
	public void destory() {
		for(Schedule schedule : schedules) {
			schedule.remove();
		}
	}
}
