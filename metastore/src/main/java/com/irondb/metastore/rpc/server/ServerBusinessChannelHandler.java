package com.irondb.metastore.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * Created by Micheal on 2017/10/4.
 */
public class ServerBusinessChannelHandler extends SimpleChannelInboundHandler<byte[]> {
    private static final Logger logger = LoggerFactory.getLogger(ServerBusinessChannelHandler.class);
    private ExecutorService executorService;  // 业务异步处理线程池
    private ServerMessageHandler messageHandler;   // Server 消息处理逻辑

    public ServerBusinessChannelHandler(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
        logger.info("ServiceBusinessChannelHandler start deal message");
    }
}
