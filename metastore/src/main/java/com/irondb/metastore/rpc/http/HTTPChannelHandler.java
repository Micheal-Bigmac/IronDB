package com.irondb.metastore.rpc.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Micheal on 2017/10/2.
 */
public class HTTPChannelHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private HttpPostRequestDecoder decoder;
    private HttpHeaders headers;
    private FullHttpRequest fullHttpRequest;
    private FullHttpResponse response;
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";
    private static final Logger logger = LoggerFactory.getLogger(HTTPChannelHandler.class);
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
    private static String baseDirectory;

    public HTTPChannelHandler(String baseDirectory) {
        HTTPChannelHandler.baseDirectory = baseDirectory;
        DiskFileUpload.baseDirectory = baseDirectory;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
//        if (msg instanceof HttpRequest) {
            headers = msg.headers();
            String uri = msg.uri();
            logger.info(" http uri : " + uri);
            //去除浏览器"/favicon.ico"的干扰
            if (uri.equals(FAVICON_ICO)) {
                return;
            }
            HttpMethod method = msg.method();
            dealContentType(msg, uri, method);
            ByteBuf byteBuf = Unpooled.wrappedBuffer(SUCCESS.getBytes());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            boolean close = isClose(msg);
            if (!close) {
                response.headers().add(org.apache.http.HttpHeaders.CONTENT_LENGTH, String.valueOf(byteBuf.readableBytes()));
            }
            ChannelFuture future = ctx.write(response);
            future.addListener(ChannelFutureListener.CLOSE);
//        }
    }

    private void dealContentType(HttpRequest msg, String uri, HttpMethod method) throws Exception {
        if (method.equals(HttpMethod.GET)) {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
            Map<String, List<String>> uriAttributes = queryDecoder.parameters();
            //打印请求参数可以根据业务需求自定义处理
            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                    logger.info(attr.getKey() + "=" + attrVal);
                }
            }
        } else if (method.equals(HttpMethod.POST)) {
            fullHttpRequest = (FullHttpRequest) msg;
            String contentType = headers.get("Content-Type").toString().split(":")[0];
            if ("application/json".equals(contentType)) {
                String jsonStr = fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                logger.info(jsonStr);
            } else if ("application/x-www-form-urlencoded".equals(contentType)) {
                String param = fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
                QueryStringDecoder queryDecoder = new QueryStringDecoder(param, false);
                Map<String, List<String>> uriAttributes = queryDecoder.parameters();

                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        logger.info(attr.getKey() + "=" + attrVal);
                    }
                }
            } else if ("multipart/form-data".equals(contentType)) {
                if (decoder != null) {
                    decoder.cleanFiles();
                    decoder = null;
                }
                decoder = new HttpPostRequestDecoder(factory, msg, Charsets.toCharset(CharEncoding.UTF_8));
                List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : bodyHttpDatas) {
                    writeHttpData(data);
                }
            } else {
                logger.info("http server is not support such Content Type " + contentType);
            }
        }
    }

    private boolean isClose(HttpRequest request) {
        if (request.headers().contains(org.apache.http.HttpHeaders.CONNECTION, CONNECTION_CLOSE, true) ||
                (request.protocolVersion().equals(HttpVersion.HTTP_1_0) &&
                        !request.headers().contains(org.apache.http.HttpHeaders.CONNECTION, CONNECTION_KEEP_ALIVE, true)))
            return true;
        return false;
    }

    private void writeHttpData(InterfaceHttpData data) throws Exception {
        //后续会加上块传输（HttpChunk），目前仅简单处理
        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            String fileName = fileUpload.getFilename();
            if (fileUpload.isCompleted()) {
                //保存到磁盘
                StringBuffer fileNameBuf = new StringBuffer();
                fileNameBuf.append(DiskFileUpload.baseDirectory).append(fileName);
                fileUpload.renameTo(new File(fileNameBuf.toString()));
            }
        }
    }
}
