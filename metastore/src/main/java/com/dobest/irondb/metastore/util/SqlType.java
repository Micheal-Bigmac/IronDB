package com.dobest.irondb.metastore.util;

/**
 * Created by Micheal on 2017/9/23.
 */

/***
 *  used for  judge Sql Type(select create insert ...)
 *  Fasted than JSqlParser  with SqlUtil.getSqlType method
 */
public enum  SqlType {
    CREATE(0),
    SELECT(1),
    INSERT(2),
    UPDATE(3),
    DELETE(4),
    ALTER(5),
    DESC(6),
    LOAD(7),
    NONE(8);

    private final int value;
    SqlType(int i) {
        this.value=i;
    }

    public static SqlType valueOf(int i){
        SqlType[] values = values();
        for(SqlType type :values){
            if(type.value==i){
                return type;
            }
        }
        throw new IllegalArgumentException("UnCorrect value");
    }
}
