package com.dobest.irondb.metastore.mysql;

import java.io.IOException;

/**
 * Created by Micheal on 2017/9/22.
 */
public class TestConnectPool {
    public static void main(String[] args) throws IOException {
//      URL resource1 = TestConnectPool.class.getResource("/IronDB.properties");
//        IronDBContext ctx = IronDBContext.fromInputStream(resource1.openStream());

//        IronDBContext ironDBContext = IronDBContext.fromInputStream("/IronDB.properties");
//        System.out.println(IronDBContext.get("IronDb.metastore.mysql.initSize"));
       /* IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo = new IronDbSchemeFactory.MetaStoreConnectionInfo(true, ctx, IronDbSchemeFactory.DB_MYSQL);

        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(10);
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionInfo);
        GenericObjectPool<Connection> pool = new GenericObjectPool<Connection>(connectionFactory, conf);
        for (int i = 0; i < 15; i++) {
            System.out.println(i + ":");
            try {
                Connection con = pool.borrowObject();
                System.out.println(con.isClosed());
//                pool.returnObject(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
*/
    }

}
