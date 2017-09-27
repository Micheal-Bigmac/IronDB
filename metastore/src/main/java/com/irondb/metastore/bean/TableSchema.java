package com.irondb.metastore.bean;

/**
 * Created by Micheal on 2017/9/24.
 */
public class TableSchema {
    private String tablename;
    private String  type;
    private String  column_name;
    private String suppor_function;
    private String comments;
    private long irondb_id;

    public long getIrondb_id() {
        return irondb_id;
    }

    public void setIrondb_id(long irondb_id) {
        this.irondb_id = irondb_id;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getSuppor_function() {
        return suppor_function;
    }

    public void setSuppor_function(String suppor_function) {
        this.suppor_function = suppor_function;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
