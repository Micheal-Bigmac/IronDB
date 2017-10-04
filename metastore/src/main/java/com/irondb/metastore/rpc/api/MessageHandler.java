package com.irondb.metastore.rpc.api;

import com.irondb.metastore.rpc.transport.RpcRequest;

/**
 * Created by Micheal on 2017/10/4.
 */
public interface MessageHandler {

    void receiveAndProcessor(byte[] request);

    Object sendAndProcessor(RpcRequest rpcRequest) throws InterruptedException;
}
