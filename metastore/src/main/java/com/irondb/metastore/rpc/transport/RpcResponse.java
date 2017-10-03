package com.irondb.metastore.rpc.transport;

import java.io.Serializable;

/**
 * Created by Micheal on 2017/10/3.
 */
public class RpcResponse implements Serializable{
    private long requestId;
    private Throwable error;// Throable{ Error Exception}  使用Exception 也可以
    private Object result;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
