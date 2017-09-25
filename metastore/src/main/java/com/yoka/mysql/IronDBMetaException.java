package com.yoka.mysql;

public class IronDBMetaException extends Exception {
    public IronDBMetaException() {
    }

    public IronDBMetaException(String message) {
        super(message);
    }

    public IronDBMetaException(Throwable cause) {
        super(cause);
    }

    public IronDBMetaException(String message, Throwable cause) {
        super(message, cause);
    }
}