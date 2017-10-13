package com.dobest.irondb.metastore.schedule;

import java.util.Timer;

public abstract class AbstractSchedule implements Schedule{
	private long period_Time;
	// 不同定时器不同类型  暂时未实现
	private ScheduleManager scheduleManager;
	private boolean flag=false;

	public long getPeriod_Time() {
		return period_Time;
	}

	public void setPeriod_Time(long period_Time) {
		this.period_Time = period_Time;
	}

	public ScheduleManager getScheduleManager() {
		return scheduleManager;
	}

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}


	@Override
	public void stop() {
		if(!flag) {
			Timer timer = scheduleManager.getTimers().get(getName());
			timer.cancel();
			flag=true;
		}
	}

	@Override
	public long period() {
		return period_Time;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
//		return this.getName();
		return this.getClass().getSimpleName();
	}

	//  处理定时器 只执行一次的 timer
	@Override
	public void remove() {
		stop();
		scheduleManager.getTimers().remove(getName());
	}

}
