package com.dobest.irondb.metastore.bean;

import java.util.Comparator;

public class TableSchemaComparator implements Comparator<TableSchema>{

    @Override
    public int compare(TableSchema o1, TableSchema o2) {
        int i = o1.getColumn_name().compareTo(o2.getColumn_name());
        if(i>0){
            return 1;
        }else{
            return -1;
        }
    }
}
