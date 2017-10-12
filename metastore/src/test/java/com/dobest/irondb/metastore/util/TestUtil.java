package com.dobest.irondb.metastore.util;

import com.dobest.irondb.metastore.IronDBContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    @Test
    public  void testHbaseUtil(){
        IronDBContext.fromInputStream();
//        List<String> familys=new ArrayList<>();
//        familys.add("t");
//        HbaseUtil.createHbaseTable("IronDbTest",familys,0,0);
        HbaseUtil.dropTable("IronDbTest");
    }
}
