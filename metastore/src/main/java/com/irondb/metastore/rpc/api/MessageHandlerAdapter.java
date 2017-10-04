package com.irondb.metastore.rpc.api;

import com.irondb.metastore.rpc.transport.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Micheal on 2017/10/4.
 */
public class MessageHandlerAdapter implements MessageHandler {

    @Override
    public void receiveAndProcessor(byte[] request, Channel channel) {

    }

    @Override
    public Object sendAndProcessor(RpcRequest rpcRequest,Channel channel) throws InterruptedException {
        return null;
    }
}
