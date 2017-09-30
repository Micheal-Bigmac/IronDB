package com.dobest.irondb.metastore.bean;

public enum ErrorType {
    TableExits(0),
    InsertError(1),
    UpdateError(2),
    DropError(3),
    Success(8);
    private int value;

    public int getValue() {
        return value;
    }

    ErrorType(int s) {
        this.value = s;
    }

    public static ErrorType valueOf(int i) {
        ErrorType[] values = values();
        for (ErrorType type : values) {
            if (type.value == i) {
                return type;
            }
        }
        return Success;
    }
}