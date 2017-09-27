package com.irondb.metastore.executor;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Micheal on 2017/9/23.
 */

/**
 * Insert、delete、update operation
 * 需要修改 不同类型返回结果
 */
public class DMLExecutor implements Callable<SqlTask> {
    private GenericObjectPool<Connection> factory;
    private SqlTask task;

    public DMLExecutor(GenericObjectPool<Connection> factory, SqlTask task) {
        this.factory = factory;
        this.task = task;
    }

    @Override
    public SqlTask call() throws Exception {
        Connection con = null;
        List<String> sqls = task.getSqls();
        try {
            con = factory.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Statement statement = null;
        try {
            con.setAutoCommit(false);
            for (int i = 0; i < sqls.size(); i++) {
                statement = con.createStatement();
                statement.addBatch(sqls.get(i));
            }
            int[] rows = statement.executeBatch();
            task.setResult(rows);
            con.commit();
        } catch (SQLException e) {
            // ErrorCode 处理 返回Task 对象
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (statement != null)
                statement.close();
            factory.returnObject(con);
        }
        return task;
    }

}
