package com.yoka.irondb.bean;

import java.util.Comparator;

public class TableSchemaComparator implements Comparator<TableSchema>{

    @Override
    public int compare(TableSchema o1, TableSchema o2) {
        int i = o1.getColumnName().compareTo(o2.getColumnName());
        if(i>0){
            return 1;
        }else{
            return -1;
        }
    }
}
