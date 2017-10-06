package com.irondb.metastore.rpc.http;

import com.irondb.metastore.rpc.server.ServerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Micheal on 2017/10/2.
 */

/**
 * 用户 http server
 */
public class IronDBMetaStoreHTTPServer implements ServerChannel {
    private static final Logger logger = LoggerFactory.getLogger(IronDBMetaStoreHTTPServer.class);
    private ThreadFactory bossFactory = new DefaultThreadFactory("netty.accept.boss");
    private ThreadFactory workFactory = new DefaultThreadFactory("netty.accept.work");
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, bossFactory);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(0, workFactory);


    @Override
    public void start(final String host, final int port, ChannelHandler handler) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true);
            if(handler!=null){
                bootstrap.childHandler(handler);
            }
            ChannelFuture future = bootstrap.bind(host,port).sync();
            logger.info("Netty-http server listening on port " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (!bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (!workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
    }
}
