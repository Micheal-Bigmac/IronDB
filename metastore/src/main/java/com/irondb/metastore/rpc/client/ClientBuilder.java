package com.irondb.metastore.rpc.client;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.protocol.Protocol;
import com.irondb.metastore.rpc.serialization.Serializer;
import com.irondb.metastore.rpc.server.ServerChannel;
import com.irondb.metastore.rpc.transport.RpcRequest;
import io.netty.channel.Channel;

import javax.net.ssl.SSLException;
import java.lang.reflect.InvocationHandler;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Created by Micheal on 2017/10/3.
 */
public class ClientBuilder<T> {
    private ServerChannel client;
    private Serializer serializer;
    private Protocol protocol;
    private int port;
    private String host;
    private IronDBContext context;
    private ClientMessageHandler clientMessageHandler;
    private AtomicLong atomicLong= new AtomicLong(0);
    private final Class<T> clientClass;
    private Channel channel;

    public ClientBuilder(Class<T> clientClass) {
        this.clientClass = clientClass;
    }

    public static <T> ClientBuilder<T> buildClass(Class<T> clientClass) {
        return new ClientBuilder(clientClass);
    }

    public  ClientBuilder<T> forOptions(String host, int port, IronDBContext context, Channel channel) {
        this.port = port;
        this.host = host;
        this.context = context;
        this.channel = channel;
        return this;
    }

    public T build() {
        serializer = ServiceLoader.load(Serializer.class).iterator().next();
        IronDbMetastoreClientChannelInitalizer clientChannelHandler = null;
        try {
            clientChannelHandler = new IronDbMetastoreClientChannelInitalizer(context, serializer);
        } catch (SSLException e) {
            e.printStackTrace();
        }
        IronDbMetaStoreClient ironDbMetaStoreClient = new IronDbMetaStoreClient();
        ironDbMetaStoreClient.start(host, port, clientChannelHandler);
        return getClientProxy();
    }

    T getClientProxy() {
        final InvocationHandler clientInvocationHandler = (proxy, method, args) -> {
            RpcRequest request = new RpcRequest();
            request.setRequestId(atomicLong.incrementAndGet());
            request.setClassName(clientClass.getName());
            request.setMethodName(method.getName());
            request.setParamterType(method.getParameterTypes());
            request.setParamters(args);

            Object result = clientMessageHandler.sendAndProcessor(request, channel);
            return result;
        };

        return (T) newProxyInstance(this.getClass().getClassLoader(), new Class[]{clientClass}, clientInvocationHandler);
    }
}
