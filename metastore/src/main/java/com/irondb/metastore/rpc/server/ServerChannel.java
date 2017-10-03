package com.irondb.metastore.rpc.server;

import io.netty.channel.ChannelHandler;

/**
 * Created by Micheal on 2017/10/3.
 */
public interface ServerChannel {
    public void start(final  String host,final int port,ChannelHandler handler);
    public void stop();
}
