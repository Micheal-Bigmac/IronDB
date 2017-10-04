package com.irondb.metastore.rpc.serialization;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.dyuproject.protostuff.runtime.RuntimeSchema.getSchema;

/**
 * Created by Micheal on 2017/10/3.
 */
public class ProtobufSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);
    @Override
    public <T> byte[] serializer(T obj) {


        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = (Schema<T>) SchemaCache.getInstance().getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }
    @Override
    public <T> T deserializer(byte[] data, Class<T> clazz) {
        try {
            T obj = clazz.newInstance();
            Schema<T> schema =getSchema(clazz);
             ProtostuffIOUtil.mergeFrom(data,obj,schema);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
