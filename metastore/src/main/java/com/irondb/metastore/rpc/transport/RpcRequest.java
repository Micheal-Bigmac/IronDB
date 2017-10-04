package com.irondb.metastore.rpc.transport;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Micheal on 2017/10/3.
 */
public class RpcRequest implements Serializable {
    private long requestId;
    private String className;
    private String methodName;
    private String version;
    private Class<?>[] paramterType;
    private Object[] paramters;

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class<?>[] getParamterType() {
        return paramterType;
    }

    public void setParamterType(Class<?>[] paramterType) {
        this.paramterType = paramterType;
    }

    public Object[] getParamters() {
        return paramters;
    }

    public void setParamters(Object[] paramters) {
        this.paramters = paramters;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId=" + requestId +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", version='" + version + '\'' +
                ", paramterType=" + Arrays.toString(paramterType) +
                ", paramters=" + Arrays.toString(paramters) +
                '}';
    }
}
