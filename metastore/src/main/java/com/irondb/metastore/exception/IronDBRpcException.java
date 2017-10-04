package com.irondb.metastore.exception;

/**
 * Created by Micheal on 2017/10/4.
 */
public class IronDBRpcException extends RuntimeException {
    public IronDBRpcException(String message) {
        super(message);
    }

    public IronDBRpcException(String message, Throwable t) {
        super(message, t);
    }
}
