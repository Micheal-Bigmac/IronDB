package com.yoka.executor;

import java.util.List;

/**
 * Created by Micheal on 2017/9/23.
 */

/**
 * DMLExecutor   SqlTask<T>  返回结果为 int[].class  List<Sql> Sqls 执行结果返回值</Sql></Sqls></>
 *DQLExecutor SqlTask<T> </T>
 */

public class SqlTask<T> {

    private List<String> sqls;

    private T result;
    private Class<?> clazz;

    public SqlTask(List<String> sqls, Class<?> clazz) {
        this.sqls = sqls;
        this.clazz = clazz;
    }

    public void setSqls(List<String> sqls) {
        this.sqls = sqls;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public T getResult() {
        return result;
    }


    public List<String> getSqls() {
        return sqls;
    }
    public void setResult(T result) {
        this.result = result;
    }

}
