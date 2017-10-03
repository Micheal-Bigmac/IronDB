package com.irondb.metastore.rpc.client;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.protocol.Protocol;
import com.irondb.metastore.rpc.server.ServerChannel;

import javax.net.ssl.SSLException;
import java.io.Serializable;

/**
 * Created by Micheal on 2017/10/3.
 */
public class ClientBuilder {
    private ServerChannel client;
    private Serializable serializable;
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
        IronDbMetastoreClientChannelHandler clientChannelHandler= null;
        try {
            clientChannelHandler = new IronDbMetastoreClientChannelHandler(context);
        } catch (SSLException e) {
            e.printStackTrace();
        }
        IronDbMetaStoreClient ironDbMetaStoreClient=new IronDbMetaStoreClient();
        ironDbMetaStoreClient.start(host,port,clientChannelHandler);
        return  ironDbMetaStoreClient;
    }
}
