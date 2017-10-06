package com.irondb.metastore.rpc.http;

import com.irondb.metastore.IronDBContext;
import com.irondb.metastore.rpc.serialization.Serializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.io.Serializable;

/**
 * Created by Micheal on 2017/10/6.
 */
public class HTTPChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final String baseDir;
    private Serializer serializer;  //  http 序列化好像不需要 暂时留着
    public HTTPChannelInitializer(IronDBContext context, Serializer serializer) {
        this.serializer=serializer;
         baseDir = context.getString("IronDb.metastore.http.server.baseDirectory");
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("codec", new HttpServerCodec())  //或者使用HttpRequestDecoder & HttpResponseEncoder
                .addLast("aggregator", new HttpObjectAggregator(1024 * 1024))  //在处理 POST消息体时需要加上
                .addLast("handler", new HTTPChannelHandler(baseDir));  //业务handler
    }
}
