package com.yoka.executor;

import com.yoka.mysql.ResultCode;

import java.util.List;

/**
 * Created by Micheal on 2017/9/23.
 */
public class SqlTask {

    private ResultCode errorCode;
    private Object result;

    public Object getResult() {
        return result;
    }

    private List<String> sqls;

    public SqlTask(List<String> sqls) {
        this.sqls = sqls;
    }

    public List<String> getSqls() {
        return sqls;
    }
    public void setResult(Object result) {
        this.result = result;
    }

    public void setErrorCode(ResultCode errorCode) {
        this.errorCode = errorCode;
    }
    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }
}
