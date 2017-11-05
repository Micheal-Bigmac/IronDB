package com.dobest.irondb.metastore.executor;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestGeneric {

    public static void main(String []args){
        List<String>  sqls=new ArrayList<>();
        sqls.add("1--");
        sqls.add("2--");
        sqls.add("3--");
        System.out.println(StringUtils.join(sqls,"|"));


//        Class<? extends TestBean> aClass = test.getClass();
//        String typeName = aClass.getTypeName();
//        Type genericSuperclass = test.getClass().getGenericSuperclass();
//        System.out.println(genericSuperclass.getTypeName());

    }
}
