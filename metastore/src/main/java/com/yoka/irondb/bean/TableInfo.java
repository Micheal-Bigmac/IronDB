package com.yoka.irondb.bean;

/**
 * Created by Micheal on 2017/9/24.
 */
public class TableInfo {
    private String tableName;  // 该字段不用   存储在Map 方便判断是否存在表

    private boolean status;
    private String	storage_type;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
}
