package com.yoka.executor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Micheal on 2017/9/23.
 */
public class DQLExecutor implements Callable<SqlTask> {
    private GenericObjectPool<Connection> factory;
    private SqlTask task;

    public DQLExecutor(GenericObjectPool<Connection> factory, SqlTask task) {
        this.factory = factory;
        this.task = task;
    }

    @Override
    public SqlTask call() throws Exception {
        Connection con = null;
        List<String> sqls = task.getSqls();
        JSONArray array = new JSONArray();
        try {
            con = factory.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Statement statement = null;
        try {
            statement = con.createStatement();
            JSONObject jsonObj = null;
            ResultSet resultSet = null;
            ResultSetMetaData metaData = null;
            for (String Sql : sqls) {
                resultSet = statement.executeQuery(Sql);
                metaData = resultSet.getMetaData();

                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    jsonObj = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        String value = resultSet.getString(columnName);
                        jsonObj.put(columnName, value);
                    }
                    array.add(jsonObj);
                    resultSet.close();
                }
            }
            task.setResult(array.toJSONString());
        } catch (SQLException e) {
            // ErrorCode 处理 返回Task 对象
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            statement.close();
            factory.returnObject(con);
        }
        return task;
    }
}
