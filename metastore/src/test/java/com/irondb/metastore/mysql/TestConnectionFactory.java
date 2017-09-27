package com.irondb.metastore.mysql;

import com.irondb.metastore.IronDBContext;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

/**
 * Created by Micheal on 2017/9/23.
 */
public class TestConnectionFactory {

    public void main(String []args) throws IOException {
        URL resource1 = TestConnectPool.class.getResource("/IronDB.properties");
        IronDBContext ctx = IronDBContext.fromInputStream(resource1.openStream());

        IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo = new IronDbSchemeFactory.MetaStoreConnectionInfo(true, ctx, IronDbSchemeFactory.DB_MYSQL);

        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(10);
        ConnectionFactory connectionFactory=new ConnectionFactory(connectionInfo);
        GenericObjectPool<Connection> pool = new GenericObjectPool<Connection>(connectionFactory, conf);
        for(int i=0;i<15;i++){
            System.out.println(i+":");
            try {
                Connection con = pool.borrowObject();
                System.out.println(con.isClosed());
                pool.returnObject(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
