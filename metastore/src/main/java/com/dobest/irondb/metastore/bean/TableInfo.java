package com.dobest.irondb.metastore.bean;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * Created by Micheal on 2017/9/24.
 */
public class TableInfo {
    private long id;
    private String tablename;  // 该字段不用   存储在Map 方便判断是否存在表
    private boolean status;
    private String	storage_type;
    private String table_options;

    private Map<String,Object> tableOptionsMap;

    public String getTable_options() {
        return table_options;
    }

    public void setTable_options(String table_options) {
        this.table_options = table_options;
        this.tableOptionsMap= JSON.parseObject(table_options,Map.class);
    }

    public Map<String, Object> getTableOptionsMap() {
        return tableOptionsMap;
    }

    public void setTableOptionsMap(Map<String, Object> tableOptionsMap) {
        this.tableOptionsMap = tableOptionsMap;
        this.table_options=JSON.toJSONString(tableOptionsMap);
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }
    //权限部分

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStorage_type() {
        return storage_type;
    }

    public void setStorage_type(String storage_type) {
        this.storage_type = storage_type;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "tablename='" + tablename + '\'' +
                ", status=" + status +
                ", storage_type='" + storage_type + '\'' +
                '}';
    }
}
