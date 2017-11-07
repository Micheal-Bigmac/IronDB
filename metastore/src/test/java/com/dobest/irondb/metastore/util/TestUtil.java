package com.dobest.irondb.metastore.util;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.IronDBContext;
import com.dobest.irondb.metastore.bean.JobTask;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class TestUtil {

    @Test
    public  void testHbaseUtil(){
        IronDBContext.getInstance();
        List<String> familys=new ArrayList<>();
        familys.add("t");
//        HbaseUtil.createHbaseTable("IronDbTest",familys,0,0);
//        HbaseUtil.dropTable("IronDbTest");
        try {
            HbaseUtil.truncateHbaseTable("IronDbTest");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testListJson(){
        List<List<String>> test=new ArrayList<>();
        List<String> index_one=new ArrayList<>();
        index_one.add("abc");
        index_one.add("bcd");
        index_one.add("cde");
        test.add(index_one);
        List<String> index_two=new ArrayList<>();
        index_two.add("one");
        index_two.add("two");
        index_two.add("three");
        test.add(index_two);

        System.out.println(JSON.toJSONString(test));
    }

    @Test
    public void TestMapToObject(){
        Map<String,Object> map_one=new HashMap<>();
        map_one.put("id",1);
        map_one.put("status","waiting");
        map_one.put("failed_reason","no reason");
        map_one.put("create_time",new Date());
        String mapStr=JSON.toJSONString(map_one);
        System.out.println(mapStr);

        JobTask task=JSON.parseObject(mapStr,JobTask.class);
        System.out.println(task.toString());
    }

    @Test
    public  void testStrToObj(){
        String str="[{\"create_time\":\"2017-10-25 13:45:43.0\",\"id\":\"1508910343855000\",\"status\":\"waiting\"}]";
        List<JobTask> jobTasks = JSON.parseArray(str, JobTask.class);
        System.out.println(jobTasks.size());
    }

    @Test
    public  void testStrSub(){
        String str="adsf|bbb|ccC|ddd|";
        System.out.println(str.substring(0,str.length()-1));
    }
}
