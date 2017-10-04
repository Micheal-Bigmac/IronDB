package com.irondb.metastore.rpc.server;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.codec.MessageCodec;
import com.irondb.metastore.rpc.serializer.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * Created by Micheal on 2017/10/2.
 */

/**
 * 自定义 channelHandler  可以注册自己的业务逻辑处理 handler
 * <p/>
 * 把业务逻辑处理分离出来 创建一个专门的任务队列 处理完的消息直接发送出去
 */
public class IronDbMetastoreServerChannelInitialzer extends ChannelInitializer<SocketChannel> {

    private final boolean ssl;
    private static SslContext sslContext = null;
    private final int keepAlive;

    private ExecutorService executorService;
    private Serializer serializer;

    public IronDbMetastoreServerChannelInitialzer(IronDBContext context, Serializer serializer) throws SSLException {
        this.serializer = serializer;

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat(" Bussiness Thread ").build();
        executorService = Executors.newFixedThreadPool(100, threadFactory); //Executors.newCachedThreadPool(threadFactory);

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
        pipeline.addLast("messagecodec", new MessageCodec(serializer));  // LengthFrameBase 也可以
        pipeline.addLast("bussiness", new ServerBusinessChannelHandler(executorService));

        // 注册自己的 bussiness handler
    }
}
