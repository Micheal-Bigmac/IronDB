package com.irondb.metastore.rpc.api;

import com.irondb.metastore.rpc.transport.RpcRequest;
import io.netty.channel.Channel;

/**
 * Created by Micheal on 2017/10/4.
 */
public interface MessageHandler {

    void receiveAndProcessor(byte[] request, Channel channel);

    Object sendAndProcessor(RpcRequest rpcRequest,Channel channel) throws InterruptedException;
}
