package com.irondb.metastore.util;

import com.alibaba.fastjson.JSON;
import com.irondb.metastore.bean.TableInfo;
import com.irondb.metastore.bean.TableSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONToMap {
    public static Map<String, TableInfo> StringToMapForTableInfo(String param) {
        List<TableInfo> transFormResult = JSON.parseArray(param, TableInfo.class);
        Map<String, TableInfo> tableInfoMap = new HashMap<>();
        for (TableInfo result : transFormResult) {
            tableInfoMap.put(result.getTablename(), result);
        }
        return tableInfoMap;
    }

    public static Map<String, List<TableSchema>> StringToMapForTableMetaData(String param) {
        Map<String, List<TableSchema>> table_metaData=new HashMap<>();
        List<TableSchema> transFormResult = JSON.parseArray(param, TableSchema.class);
        for(TableSchema schema : transFormResult){
            String tablename = schema.getTablename();
            if(!table_metaData.containsKey(tablename)){
                List<TableSchema> tableSchemas=new ArrayList<>();
                tableSchemas.add(schema);
            }else{
                table_metaData.get(tablename).add(schema);
            }
        }
        return table_metaData;
    }
}
