package com.irondb.metastore.mysql;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Micheal on 2017/9/23.
 */
public class ConnectionFactory implements PooledObjectFactory<Connection> {
    private IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo;

    public ConnectionFactory(IronDbSchemeFactory.MetaStoreConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public PooledObject<Connection> makeObject() throws Exception {
        Connection connectionToMetastore = IronDbSchemeFactory.getConnectionToMetastore(connectionInfo.getUsername(), connectionInfo.getPassword(), connectionInfo.getUrl(), connectionInfo.getDriver(), connectionInfo.getPrintInfo());
        return new DefaultPooledObject(connectionToMetastore);
    }

    @Override
    public void destroyObject(PooledObject<Connection> pooledObject) throws Exception {
        Connection object = pooledObject.getObject();
        if(isActive(object)){
            object.close();
        }
    }
    public boolean isActive(Connection conn) {
        try {
            if (conn != null || conn.isClosed()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean validateObject(PooledObject<Connection> pooledObject) {
        return false;
    }

    @Override
    public void activateObject(PooledObject<Connection> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Connection> pooledObject) throws Exception {

    }
}
