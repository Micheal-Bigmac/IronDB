package com.dobest.irondb.metastore.server;


import com.dobest.irondb.metastore.IronDBContext;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.io.File;


/**
 * Created by Micheal on 2017/10/2.
 */

/***
 *  自定义 channelHandler  可以注册自己的业务逻辑处理 handler
 */
public class IronDbMetastoreTCPChannelHandler extends ChannelInitializer<SocketChannel> {

    private final boolean ssl;
    private static SslContext sslContext;
    private final int keepAlive;

    public IronDbMetastoreTCPChannelHandler(IronDBContext context) throws SSLException {
        ssl = context.getBoolean("mqtt.ssl.enabled");
        final String sslCretPath = context.getString("mqtt.ssl.certPath");
        final String keyPath = context.getString("mqtt.ssl.keyPath");
        keepAlive = context.getInt("mqtt.keepalive.default", 50);
        sslContext = ssl ? SslContextBuilder.forServer(new File(sslCretPath), new File(keyPath)).build() : null;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        EventLoopGroup bussinessGroup = new DefaultEventLoopGroup(10, r -> {
            Thread thread = new Thread(r);
            thread.setName("业务线程 Bussiness Thread ");
            return thread;
        });

        ChannelPipeline pipeline = socketChannel.pipeline();
        if (ssl) {
            pipeline.addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
        }
        pipeline.addLast("idleStateHandler", new IdleStateHandler(0, 0, keepAlive));
        // 注册自己的 handler
    }
}
