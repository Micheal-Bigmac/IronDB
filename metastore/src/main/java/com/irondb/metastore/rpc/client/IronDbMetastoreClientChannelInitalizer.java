package com.irondb.metastore.rpc.client;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.codec.MessageCodec;
import com.irondb.metastore.rpc.serialization.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.io.File;

/**
 * Created by Micheal on 2017/10/3.
 */
public class IronDbMetastoreClientChannelInitalizer extends ChannelInitializer<SocketChannel> {
    private final boolean ssl;
    private static SslContext sslContext = null;
    private final int keepAlive;
    private Serializer serializer;

    public IronDbMetastoreClientChannelInitalizer(IronDBContext context,Serializer serializer) throws SSLException {
        this.serializer=serializer;

        ssl = context.getBoolean("mqtt.ssl.enabled");
        final String sslCretPath = context.getString("mqtt.ssl.certPath");
        final String keyPath = context.getString("mqtt.ssl.keyPath");
        keepAlive = context.getInt("mqtt.keepalive.default", 50);
        sslContext = ssl ? SslContextBuilder.forServer(new File(sslCretPath), new File(keyPath)).build() : null;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();
        if (ssl) {
            pipeline.addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, keepAlive));
        pipeline.addLast("messagecodec", new MessageCodec(serializer));
        pipeline.addLast("bussiness",new ClientBussinessChannelHandler());
        // 注册自己的 bussiness handler
    }
}
