package com.dobest.irondb.metastore.ql;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.IronDBContext;
import com.dobest.irondb.metastore.bean.TableInfo;
import com.dobest.irondb.metastore.executor.MysqlTakMonitor;
import com.dobest.irondb.metastore.executor.SqlTask;
import com.dobest.irondb.metastore.mysql.IronDBMetaDataCache;
import com.dobest.irondb.metastore.mysql.IronDbSchemeFactory;
import com.dobest.irondb.metastore.util.JSONToMap;
import com.dobest.irondb.metastore.util.TokenUtil;
import com.dobest.irondb.metastore.bean.ResultCode;
import com.dobest.irondb.metastore.bean.TableSchema;
import com.dobest.irondb.metastore.executor.DQLExecutor;
import com.dobest.irondb.metastore.mysql.ConnectionFactory;
import com.dobest.irondb.metastore.mysql.MetaStoreTool;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.merge.MergeInsert;
import net.sf.jsqlparser.statement.merge.MergeUpdate;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Dispatcher {
    private IronDBContext context;
    private ExecutorService executorService;      // 线程调度池
    private IronDBMetaDataCache metaDataCache;
    private GenericObjectPool<Connection> connectPool;  // 数据库连接池

//    private IronDBMetastoreServer metastoreServer;  // 交给Dubbo  不需要创建该对象

//    private IronDBMetastoreHttpServer metastoreHttpServer; // 交给Dubbo  不需要创建该对象

    public Dispatcher(IronDBContext context) {
        this.context = context;
        metaDataCache = new IronDBMetaDataCache();
        this.init();
        this.initTableMetaData();
    }

    private synchronized void initTableMetaData() {
        // 获取IronDb 表的信息
        List<String> tables = new ArrayList<>();
        String tablesInfo = "SELECT id, tablename, status, storage_type,table_options FROM irondbtables";
        tables.add(tablesInfo);

        SqlTask tablesInfoTask = new SqlTask(tables);
        DQLExecutor tableInfoQuery = new DQLExecutor(connectPool, tablesInfoTask);
        Future<SqlTask> resultTableInfo = executorService.submit(tableInfoQuery);
        while (!resultTableInfo.isDone()) {
            SqlTask sqlTask = null;
            try {
                sqlTask = resultTableInfo.get();
                String result1 = (String) sqlTask.getResult();
                Map<String, TableInfo> tableInfoMap = JSONToMap.StringToMapForTableInfo(result1);
                this.metaDataCache.setTables(tableInfoMap); // Map 转换成ConcurrentHashMap
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 获取IronDb 表的字段信息
        List<String> tableScheme = new ArrayList<>();
        String tableSchemaInfo = "SELECT tablename,column_name,type,suppor_function,comments FROM irondbtables,irondbcolumns where irondbtables.id=irondbcolumns.irondb_id";
        tableScheme.add(tableSchemaInfo);
        SqlTask tablesSchemaTask = new SqlTask(tableScheme);
        DQLExecutor tableSchemaQuery = new DQLExecutor(connectPool, tablesSchemaTask);
        Future<SqlTask> resultTableSchema = executorService.submit(tableSchemaQuery);
        while (!resultTableSchema.isDone()) {
            SqlTask sqlTask = null;
            try {
                sqlTask = resultTableSchema.get();
                String result1 = (String) sqlTask.getResult();
                Map<String, List<TableSchema>> result = JSONToMap.StringToMapForTableMetaData(result1);
                this.metaDataCache.setTable_metaData(result); // Map 转换成ConcurrentHashMap
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * init  Metastore Server
     * init  ThreadPoolExecutor
     * init MetaDataCache
     */
    public synchronized void init() {
        int poolSize = Integer.valueOf(context.get("IronDb.metastore.threadPool.Size"));
        int poolMaxSize = Integer.valueOf(context.get("IronDb.metastore.threadPool.MaxSize"));
        int keepAliveTime = Integer.valueOf(context.get("IronDb.metastore.threadPool.keepAliveTime"));

        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("DBMetaData" + "-pool-" + threadNumber.getAndAdd(1));
                return thread;
            }
        };

        executorService = new ThreadPoolExecutor(poolSize, poolMaxSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(10),threadFactory); // 是否指定 任务等待队列大小 目前是10个//        pool.submit()


        // 初始化 数据库链接池
        IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo = new IronDbSchemeFactory.MetaStoreConnectionInfo(true, context, IronDbSchemeFactory.DB_MYSQL);
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(10);
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionInfo);
        connectPool = new GenericObjectPool<Connection>(connectionFactory, conf);

        Runtime.getRuntime().addShutdownHook(new Thread("ShutDown hook") {
            @Override
            public void run() {
                // log  write
                connectPool.close();              // 关闭数据库连接池
                executorService.shutdown();       // 关闭work 线程池
                TokenUtil.scheduler.shutdown();  // 关闭 定时器调度
            }
        });
    }


    public ResultCode parseSql(String sql) {
        ResultCode code=null;
        try {
            Statement parse = CCJSqlParserUtil.parse(sql);
            if (parse instanceof CreateTable) {
                MysqlTakMonitor.CreateOperation(code,parse,metaDataCache,connectPool,executorService);
            } else if (parse instanceof Select) {

            } else if (parse instanceof Insert) {

            } else if (parse instanceof Update) {

            } else if (parse instanceof Delete) {

            } else if (parse instanceof Alter) {

            } else if (parse instanceof AlterView) {

            } else if (parse instanceof CreateView) {

            } else if (parse instanceof CreateIndex) {

            } else if (parse instanceof Drop) {
                MysqlTakMonitor.DropOptions(code,parse,metaDataCache,connectPool,executorService);
            } else if (parse instanceof Merge) {  // not support

            } else if (parse instanceof MergeInsert) { // not support

            } else if (parse instanceof MergeUpdate) {  // not support

            } else if (parse instanceof Replace) {  // not  support

            } else if (parse instanceof Truncate) {
                MysqlTakMonitor.TruncateOptions(code,parse,metaDataCache,connectPool,executorService);
            } else if (parse instanceof Upsert) {  // not support

            } else {

            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return code;
    }


    public static void main(String[] args) {
        String sql2 = "create table user_image8(uid LONG max,name INT  bitmap,birthday Date,first_logintime long,province array auto_dimension,love_game map auto_dimension) PRIMARY_KEY(uid,name) TTL 86400;";
        String sql3="   create table test( uid string 唯一id ,nid string ,name string, province string,city string,day string) GROUP_SETS ('time,provice,city','time,provice')";

//        String sql3="drop table user_image";
//        String sql3="TRUNCATE TABLE table_name;";
//        String sql3="load data into table override inpath /root/path";
        IronDBContext ironDBContext = IronDBContext.fromInputStream(MetaStoreTool.class.getResourceAsStream("/IronDB.properties"));
        Dispatcher dispatcher = new Dispatcher(ironDBContext);
        dispatcher.parseSql(sql3);
    }



}
