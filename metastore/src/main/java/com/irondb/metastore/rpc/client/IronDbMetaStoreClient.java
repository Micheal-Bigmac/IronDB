package com.irondb.metastore.rpc.client;

import com.irondb.metastore.rpc.server.ServerChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Micheal on 2017/10/3.
 */
public class IronDbMetaStoreClient implements ServerChannel {
    private static final Logger LOGGER = LoggerFactory.getLogger(IronDbMetaStoreClient.class);

    private EventLoop bossGroup;
    @Override
    public void start(final  String host,final int port, ChannelHandler handler) {

        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(bossGroup).channel(NioSocketChannel.class).handler(handler);
        try {
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {
        if(!bossGroup.isShutdown()){
            bossGroup.shutdownGracefully();
        }
    }
}
