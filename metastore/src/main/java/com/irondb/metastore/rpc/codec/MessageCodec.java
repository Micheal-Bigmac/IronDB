package com.irondb.metastore.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Micheal on 2017/10/3.
 * 可能存在粘包问题
 */

public class MessageCodec extends ByteToMessageCodec<byte[]> {
    private static final int Message_Length = 4;// encode 写入时 写入一个int 类型数据 4个字节
    private static final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        int dataLength = msg.length;
        out.writeInt(dataLength);
        out.writeBytes(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length = in.readableBytes();
        if (length < Message_Length) return;
        in.markReaderIndex();
        int MessageLength = in.readInt();
        if (MessageLength < 0) ctx.close();

        if (in.readableBytes() < MessageLength) {
            in.resetReaderIndex();
        }else{
            byte[] body=new byte[MessageLength];
            in.readBytes(body);
            out.add(body);
        }

    }
}
