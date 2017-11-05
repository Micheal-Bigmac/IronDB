package com.dobest.irondb.metastore.bean;

import java.lang.reflect.Type;

public class TestJsonToMap<T> {

    public static void main(String[] args) {
        String jsonArray = "[{\"tableName\": \"xxx\",\"tableInfo\": {\"status\": true,\"storage_type\": \"hbase\"}},{\"tableName\": \"333\",\"tableInfo\": {\"status\": true,\"storage_type\": \"BitMap\"}},{\"tableName\": \"555\",\"tableInfo\": {\"status\": true,\"storage_type\": \"ElasticSearch\"}}]";

//        List<TransFormResult> transFormResult = JSON.parseArray(jsonArray, TransFormResult.class);
        TestJsonToMap<String> test=new TestJsonToMap<>();
        Class<? extends TestJsonToMap> aClass = test.getClass();
        Type genericSuperclass = aClass.getGenericSuperclass();
        System.out.println();
    }
}
