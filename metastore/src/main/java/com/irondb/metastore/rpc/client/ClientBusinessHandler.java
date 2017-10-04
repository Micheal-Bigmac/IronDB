package com.irondb.metastore.rpc.client;

import com.irondb.metastore.rpc.api.MessageHandlerAdapter;
import com.irondb.metastore.rpc.serialization.Serializer;
import com.irondb.metastore.rpc.transport.RpcRequest;
import com.irondb.metastore.rpc.transport.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by Micheal on 2017/10/4.
 */

public class ClientBusinessHandler extends MessageHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientBusinessHandler.class);
    private static final int TIME_AWAIT = 30;
    private Serializer serializer;
    private ChannelHandlerContext context;
    private Map<Long, BlockingQueue<RpcResponse>> callback;

    @Override
    public void receiveAndProcessor(byte[] request,Channel channel) {
        RpcResponse deReponse = serializer.deserializer(request, RpcResponse.class);
        if (deReponse != null && deReponse.getRequestId() > 0) {
            BlockingQueue<RpcResponse> rpcResponses = callback.get(deReponse.getRequestId());
            rpcResponses.add(deReponse);
            System.out.println(deReponse.toString());

            callback.remove(deReponse.getRequestId());
        } else {
            logger.error("not found RequestId");
        }
    }

    // 客户端阻塞等待结果返回   逻辑暂时未明白 未走通
    @Override
    public Object sendAndProcessor(RpcRequest rpcRequest,Channel channel) throws InterruptedException {
        byte[] serializer1 = serializer.serializer(rpcRequest);

        BlockingQueue<RpcResponse> queue = new LinkedBlockingDeque<>(); // client 阻塞方式应该用 BlockQueue
        callback.put(rpcRequest.getRequestId(), queue);

       context.writeAndFlush(serializer1).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    context.read();
                    logger.debug("Message succeed: Message {}  has been sent to client successfully", rpcRequest.getRequestId());
                } else {
                    logger.debug("Message failed: Message {} {} has been sent to clientsuccessfully", rpcRequest.toString(), future.cause());
                }
            }
        });

        RpcResponse poll = queue.poll(TIME_AWAIT, TimeUnit.SECONDS);
        if (poll != null) {
            if (poll.getError() != null) {
                throw new RuntimeException(poll.getError());
            } else {
                return poll.getResult();
            }
        } else {
            throw new RuntimeException("request time out");
        }
    }
}
