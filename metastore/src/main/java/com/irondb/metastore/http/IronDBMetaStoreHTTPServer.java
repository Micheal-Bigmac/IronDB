package com.irondb.metastore.http;

import com.irondb.metastore.rpc.server.ServerChannel;
import io.netty.channel.ChannelHandler;

/**
 * Created by Micheal on 2017/10/2.
 */

/**
 *  用户http service
 */
public class IronDBMetaStoreHTTPServer implements ServerChannel {



    @Override
    public void start(final  String host,final int port,ChannelHandler handler) {

    }

    @Override
    public void stop() {

    }
}
