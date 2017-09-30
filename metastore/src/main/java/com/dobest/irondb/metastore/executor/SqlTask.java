package com.dobest.irondb.metastore.executor;

import java.util.List;

/**
 * Created by Micheal on 2017/9/23.
 */

/**
 * DMLExecutor   SqlTask<T>  返回结果为 int[].class  List<Sql> Sqls 执行结果返回值</Sql></Sqls></>
 *DQLExecutor SqlTask<T> </T>
 */

public class SqlTask {

    private List<String> sqls;

    private Object result;

    public SqlTask(List<String> sqls) {
        this.sqls = sqls;
    }
    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }

    public List<String> getSqls() {
        return sqls;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
