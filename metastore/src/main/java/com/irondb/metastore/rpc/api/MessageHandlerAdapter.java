package com.irondb.metastore.rpc.api;

import com.irondb.metastore.rpc.transport.RpcRequest;

/**
 * Created by Micheal on 2017/10/4.
 */
public class MessageHandlerAdapter implements MessageHandler {

    @Override
    public void receiveAndProcessor(byte[] request) {

    }

    @Override
    public Object sendAndProcessor(RpcRequest rpcRequest) throws InterruptedException {
        return null;
    }
}
