package com.yoka.ql;

import com.yoka.executor.DQLExecutor;
import com.yoka.executor.SqlTask;
import com.yoka.irondb.IronDBContext;
import com.yoka.irondb.bean.TableInfo;
import com.yoka.irondb.bean.TableSchema;
import com.yoka.mysql.ConnectionFactory;
import com.yoka.mysql.IronDBMetaDataCache;
import com.yoka.mysql.IronDbSchemeFactory;
import javafx.collections.transformation.SortedList;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        this.init();
        this.initTableMetaData();
    }

    private void initTableMetaData() {
        // 获取IronDb 表的信息
        List<String> tables=new ArrayList<>();
        String tablesInfo="select * from IronDBtables";
        tables.add(tablesInfo);
        SqlTask tablesInfoTask=new SqlTask(tables);
        DQLExecutor tableInfoQuery=new DQLExecutor(connectPool,tablesInfoTask);
        Future<SqlTask> resultTableInfo = executorService.submit(tableInfoQuery);
        while (resultTableInfo.isDone()){
            SqlTask sqlTask = null;
            try {
                sqlTask = resultTableInfo.get();
                Map<String, TableInfo> result = (Map<String, TableInfo>) sqlTask.getResult();
                this.metaDataCache.setTables(result); // Map 转换成ConcurrentHashMap
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 获取IronDb 表的字段信息
        List<String> tableScheme=new ArrayList<>();
        String tableSchemaInfo="select tablename,columnName,type,storage_type from IronDBTables,IronDBColumns where IronDBTables.id=IronDBColumns.IronDBTables_ID";
        tableScheme.add(tableSchemaInfo);
        SqlTask tablesSchemaTask=new SqlTask(tables);
        DQLExecutor tableSchemaQuery=new DQLExecutor(connectPool,tablesSchemaTask);
        Future<SqlTask> resultTableSchema = executorService.submit(tableSchemaQuery);
        while (resultTableSchema.isDone()){
            SqlTask sqlTask = null;
            try {
                sqlTask = resultTableSchema.get();
                Map<String, SortedList<TableSchema>> result = (Map<String, SortedList<TableSchema>>) sqlTask.getResult();
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
    public synchronized void init(){
        int poolSize=Integer.valueOf(context.get("IronDb.metastore.threadPool.Size"));
        int poolMaxSize=Integer.valueOf(context.get("IronDb.metastore.threadPool.MaxSize"));
        int keepAliveTime=Integer.valueOf(context.get("IronDb.metastore.threadPool.keepAliveTime"));

        ThreadFactory threadFactory=new ThreadFactory() {
            private  final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread  thread=new Thread();
                thread.setName("DBMetaData"+"-pool-"+ threadNumber.getAndAdd(1));
                return thread;
            }
        };
        executorService=new ThreadPoolExecutor(poolSize,poolMaxSize,keepAliveTime, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(10),threadFactory); // 是否指定 任务等待队列大小 目前是10个//        pool.submit()


       // 初始化 数据库链接池
        IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo = new IronDbSchemeFactory.MetaStoreConnectionInfo(true, context, IronDbSchemeFactory.DB_MYSQL);
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(10);
        ConnectionFactory connectionFactory=new ConnectionFactory(connectionInfo);
        connectPool  = new GenericObjectPool<Connection>(connectionFactory, conf);

        Runtime.getRuntime().addShutdownHook(new Thread("ShutDown hook"){
            @Override
            public void run(){
                // log  write
                connectPool.close();
                executorService.shutdown();
            }
        });




    }
}
