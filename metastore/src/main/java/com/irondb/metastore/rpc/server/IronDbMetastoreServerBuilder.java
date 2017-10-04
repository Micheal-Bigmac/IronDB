package com.irondb.metastore.rpc.server;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.protocol.Protocol;
import com.irondb.metastore.rpc.serialization.Serializer;

import javax.net.ssl.SSLException;
import java.util.ServiceLoader;

/**
 * Created by Micheal on 2017/10/3.
 */
public final class IronDbMetastoreServerBuilder {

    private ServerChannel server;
    private Serializer serializer;
    private Protocol protocol;
    private int port;
    private String host;
    private IronDBContext context;

    public IronDbMetastoreServerBuilder(String host, int port, IronDBContext context) {
        this.port = port;
        this.context = context;
    }

    public static IronDbMetastoreServerBuilder forOptions(String host, int port, IronDBContext context) {
        return new IronDbMetastoreServerBuilder(host, port, context);
    }

    public IronDBMetastoreServer build() {
        serializer = ServiceLoader.load(Serializer.class).iterator().next();
        IronDbMetastoreServerChannelInitialzer ironDbMetastoreTCPChannelHandler = null;
        try {
            ironDbMetastoreTCPChannelHandler = new IronDbMetastoreServerChannelInitialzer(context,serializer);
        } catch (SSLException e) {
            e.printStackTrace();
        }
        IronDBMetastoreServer ironDBMetastoreTCPServer = new IronDBMetastoreServer();
        ironDBMetastoreTCPServer.start(host, port, ironDbMetastoreTCPChannelHandler);
        return ironDBMetastoreTCPServer;
    }
}
