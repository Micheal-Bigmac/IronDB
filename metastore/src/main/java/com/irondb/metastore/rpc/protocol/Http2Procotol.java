package com.irondb.metastore.rpc.protocol;

import io.netty.handler.codec.http2.Http2FrameCodec;

/**
 * Created by Micheal on 2017/10/4.
 */
public class Http2Procotol extends Http2FrameCodec implements Protocol {

    public Http2Procotol(boolean server) {
        super(server);
    }
}
