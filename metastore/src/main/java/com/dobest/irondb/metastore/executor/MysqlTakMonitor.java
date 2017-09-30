package com.dobest.irondb.metastore.executor;

import com.dobest.irondb.metastore.bean.ErrorType;
import com.dobest.irondb.metastore.bean.ResultCode;
import com.dobest.irondb.metastore.mysql.IronDBMetaDataCache;
import com.dobest.irondb.metastore.ql.SqlParser;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 这个类可以做一些metric 的指标监控  用时 等相关操作
 */
public class MysqlTakMonitor {
    //

    public static void CreateOperation(ResultCode code, Statement parse, IronDBMetaDataCache metaDataCache, GenericObjectPool<Connection> connectPool, ExecutorService executorService) {
        List<String> strings = SqlParser.parseCreatTableForMysql(parse, metaDataCache);
        if (strings == null) {
            code = new ResultCode();
            code.ResultCodeSeting(ErrorType.TableExits, null);
        } else { //(strings != null && strings.size() > 0)
            code = new ResultCode();
            SqlTask task = new SqlTask(strings);
            DMLExecutor dmlExecutor = new DMLExecutor(connectPool, task);
            Future<SqlTask> dmltask = executorService.submit(dmlExecutor);

            while (!dmltask.isDone()) {
                SqlTask sqlTask = null;
                try {
                    sqlTask = dmltask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    code.ResultCodeSeting(ErrorType.InsertError, e);
                    return;
                }
                int[] result = (int[]) sqlTask.getResult();
                // ErrorCode 处理
//                    Collections.addAll(arrTolist,result);
            }
//            code = new ResultCode();
            code.ResultCodeSeting(ErrorType.Success, null);
        }
    }

    public static void DropOptions(ResultCode code, Statement parse, IronDBMetaDataCache metaDataCache, GenericObjectPool<Connection> connectPool, ExecutorService executorService) {
        List<String> strings = SqlParser.parseDropTableForMysql(parse, metaDataCache);
        println(strings);
        if (strings == null) {
            code = new ResultCode();
            code.ResultCodeSeting(ErrorType.TableExits, null);
        } else { //(strings != null && strings.size() > 0)
            code = new ResultCode();
            SqlTask task = new SqlTask(strings);
            DDLExecutor dmlExecutor = new DDLExecutor(connectPool, task);
            Future<SqlTask> dmltask = executorService.submit(dmlExecutor);
            while (!dmltask.isDone()) {
                SqlTask sqlTask = null;
                try {
                    sqlTask = dmltask.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    code.ResultCodeSeting(ErrorType.DropError, e);
                    return;
                }
                int[] result = (int[]) sqlTask.getResult();
                code.ResultCodeSeting(ErrorType.Success, null);
            }
        }
    }
    public static void println(List<String> sqls) {
        for (String tmp : sqls) {
            System.out.println(tmp);
        }
    }

    public static void TruncateOptions(ResultCode code, Statement parse, IronDBMetaDataCache metaDataCache, GenericObjectPool<Connection> connectPool, ExecutorService executorService) {

    }
}
