package com.irondb.metastore.rpc.client;

import com.irondb.metastore.rpc.transport.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Micheal on 2017/10/4.
 */
public class ClientBussinessChannelHandler extends SimpleChannelInboundHandler<byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(ClientBussinessChannelHandler.class);

    private ClientMessageHandler clientMessageHandler;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        clientMessageHandler.receiveAndProcessor(msg,ctx.channel());
    }
}
