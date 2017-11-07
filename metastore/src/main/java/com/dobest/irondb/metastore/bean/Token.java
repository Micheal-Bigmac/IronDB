package com.dobest.irondb.metastore.bean;

import java.io.Serializable;

public class Token implements Serializable {

    private String signature;
    private long timestamp;

    public Token(String signature, long timestamp) {
        if (signature == null)
            throw new IllegalArgumentException("signature can not be null");

        this.timestamp = timestamp;
        this.signature = signature;
    }

    public Token(String signature) {
        if (signature == null)
            throw new IllegalArgumentException("signature can not be null");

        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public long getTimestamp() {
        return timestamp;
    }


    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    public boolean equals(Object object) {
        if (object instanceof Token)
            return ((Token)object).signature.equals(this.signature);
        return false;
    }

    @Override
    public String toString() {
        return "Token [signature=" + signature + ", timestamp=" + timestamp
                + "]";
    }

}