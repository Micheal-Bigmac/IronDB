package com.irondb.metastore.rpc.server;

import com.irondb.metastore.rpc.api.MessageHandlerAdapter;
import com.irondb.metastore.rpc.serialization.Serializer;
import com.irondb.metastore.rpc.transport.RpcRequest;
import com.irondb.metastore.rpc.transport.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Micheal on 2017/10/4.
 */
public class ServerMessageHandler extends MessageHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);

    private Serializer serializer;
    private ChannelHandlerContext context;

    @Override
    public void receiveAndProcessor(byte[] request) {
        RpcRequest deRequst = serializer.deserializer(request, RpcRequest.class);
        RpcResponse response = new RpcResponse();
        response.setRequestId(deRequst.getRequestId());
        String methodName = deRequst.getMethodName();
        Method method=null;
        try {
            Class<?> aClass = Class.forName(deRequst.getClassName());
            Object o = aClass.newInstance();
            method= aClass.getMethod(deRequst.getMethodName(), deRequst.getParamterType());
            if(method!=null){
                Object result = method.invoke(o, deRequst.getParamters());
                response.setResult(result);
            }
        } catch (ClassNotFoundException e) {
            logger.info("ClassNotFound " + e.getMessage());
            response.setError(e);
        } catch (NoSuchMethodException e) {
            logger.info("NotSuchMethod " + e.getMessage());
            response.setError(e);
        } catch (InstantiationException e) {
            logger.error("Instantiation Exception" + e.getMessage());
            response.setError(e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccess Exception " + e.getMessage());
            response.setError(e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTarget Exception " + e.getMessage());
            response.setError(e);
        }
        context.writeAndFlush(response);
    }
}
