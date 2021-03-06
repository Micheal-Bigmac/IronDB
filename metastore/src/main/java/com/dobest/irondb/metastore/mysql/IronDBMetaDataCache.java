package com.dobest.irondb.metastore.mysql;

/**
 * Created by Micheal on 2017/9/24.
 */

import com.dobest.irondb.metastore.bean.TableInfo;
import com.dobest.irondb.metastore.bean.TableSchema;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来缓存 IronDb 保存在 mysql 中的元数据
 */
public class IronDBMetaDataCache {
    private  Map<String, TableInfo> tables = null;    // 分段 并发lock  ConcurrentHashMap

    private Map<String, List<TableSchema>> table_metaData;  //ConcurrentHashMap

    public Map<String, TableInfo> getTables() {
        return tables;
    }

    public void setTables(Map<String, TableInfo> tables) {
        this.tables = new ConcurrentHashMap(tables);
    }

    public Map<String, List<TableSchema>> getTable_metaData() {
        return table_metaData;
    }

    public void setTable_metaData(Map<String, List<TableSchema>> table_metaData) {
        this.table_metaData = table_metaData;
    }
}
