package com.irondb.metastore.rpc.client;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.protocol.Protocol;
import com.irondb.metastore.rpc.serializer.Serializer;
import com.irondb.metastore.rpc.server.ServerChannel;

import javax.net.ssl.SSLException;
import java.util.ServiceLoader;

/**
 * Created by Micheal on 2017/10/3.
 */
public class ClientBuilder {
    private ServerChannel client;
    private Serializer serializer;
    private Protocol protocol;
    private int port;
    private String host;
    private IronDBContext context;

    public ClientBuilder(String host, int port, IronDBContext context) {
        this.port = port;
        this.host = host;
        this.context = context;
    }

    public static ClientBuilder forOptions(String host, int port, IronDBContext context) {
        return new ClientBuilder(host, port, context);
    }

    public IronDbMetaStoreClient build(){
        serializer= ServiceLoader.load(Serializer.class).iterator().next();
        IronDbMetastoreClientChannelInitalizer clientChannelHandler= null;
        try {
            clientChannelHandler = new IronDbMetastoreClientChannelInitalizer(context,serializer);
        } catch (SSLException e) {
            e.printStackTrace();
        }
        IronDbMetaStoreClient ironDbMetaStoreClient=new IronDbMetaStoreClient();
        ironDbMetaStoreClient.start(host,port,clientChannelHandler);
        return  ironDbMetaStoreClient;
    }
}
