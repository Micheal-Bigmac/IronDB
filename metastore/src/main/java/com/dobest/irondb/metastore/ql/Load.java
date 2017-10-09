package com.dobest.irondb.metastore.ql;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
//load data local inpath wyp.txt into table wyp;
public class Load {
    private String table;        // destination table name
    private String path;        //
    private String storageType;   // local data / hdfs://  data   // 需要设置 默认存储路径
    private boolean IsOveride;  // overide insert / insert

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table.replaceAll(";$","");
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType.replaceAll("\\s$","");
    }

    public boolean isOveride() {
        return IsOveride;
    }

    public void setOveride(boolean overide) {
        IsOveride = overide;
    }

    @Override
    public String toString() {
        return "Load{" +
                "table='" + table + '\'' +
                ", path='" + path + '\'' +
                ", storageType='" + storageType + '\'' +
                ", IsOveride=" + IsOveride +
                '}';
    }
}
