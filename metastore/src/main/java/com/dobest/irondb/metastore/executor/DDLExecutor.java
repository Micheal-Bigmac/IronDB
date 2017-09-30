package com.dobest.irondb.metastore.executor;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * create Operation
 */
public class DDLExecutor implements Callable<SqlTask> {

    private GenericObjectPool<Connection> factory;
    private SqlTask task;

    public DDLExecutor(GenericObjectPool<Connection> factory, SqlTask task) {
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
            statement = con.createStatement();
            for (int i = 0; i < sqls.size(); i++) {
                statement.addBatch(sqls.get(i));
            }
            int[] rows = statement.executeBatch();
            task.setResult(rows);
            con.commit();
        } catch (SQLException e) {
            // ErrorCode 处理 返回Task 对象
//            e.printStackTrace();

            try {
                con.rollback();
            } catch (SQLException e1) {
//                e1.printStackTrace();
                throw e1;
            }
            throw e;
        } finally {
            if (statement != null) {
                statement.close();
            }
            factory.returnObject(con);
        }
        return task;
    }
}
