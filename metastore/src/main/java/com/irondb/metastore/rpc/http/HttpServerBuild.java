package com.irondb.metastore.rpc.http;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.protocol.Protocol;
import com.irondb.metastore.rpc.serialization.Serializer;
import com.irondb.metastore.rpc.server.ServerChannel;

import java.util.ServiceLoader;

/**
 * Created by Micheal on 2017/10/6.
 */
public class HttpServerBuild {
    private ServerChannel server;
    private Serializer serializer;
    private Protocol protocol;
    private int port;
    private String host;
    private IronDBContext context;

    public HttpServerBuild(String host, int port, IronDBContext context) {
        this.port = port;
        this.context = context;
    }

    public static HttpServerBuild forOptions(String host, int port, IronDBContext context) {
        return new HttpServerBuild(host, port, context);
    }

    public IronDBMetaStoreHTTPServer build() {
        serializer = ServiceLoader.load(Serializer.class).iterator().next();
        HTTPChannelInitializer httpChannelInitializer = null;
        httpChannelInitializer = new HTTPChannelInitializer(context,serializer);
        IronDBMetaStoreHTTPServer httpServer = new IronDBMetaStoreHTTPServer();
        httpServer.start(host, port, httpChannelInitializer);
        return httpServer;
    }
}
