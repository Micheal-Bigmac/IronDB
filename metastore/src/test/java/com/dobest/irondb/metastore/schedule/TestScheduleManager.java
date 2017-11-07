package com.dobest.irondb.metastore.schedule;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TestScheduleManager {


    @Test
    public void test(){
        Processor processor=new MyProcessor();

        ScheduleManager manager=new ScheduleManager();


        long currentTimeMillis = System.currentTimeMillis();
        Date beforeDate=new Date(currentTimeMillis);

        long afterTime = currentTimeMillis + 1 * 1000;
        Date afterDate=new  Date(afterTime);

        System.out.println(beforeDate +" "+ afterDate );
        ScheduleDispatch dispatch=new ScheduleDispatch(processor,afterTime,manager);
        manager.getSchedules().add(dispatch);
        manager.trigger();




    }
}
