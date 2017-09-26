package com.yoka.irondb.bean;

public class TransFormResult {

    private String tableName;
    private TableInfo tableInfo;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    @Override
    public String toString() {
        return "TransFormResult{" +
                "tableName='" + tableName + '\'' +
                ", tableInfo=" + tableInfo +
                '}';
    }
}
