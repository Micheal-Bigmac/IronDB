package com.irondb.metastore.rpc.serialization;

/**
 * Created by Micheal on 2017/10/3.
 */

public interface Serializer {
   <T> byte[] serializer(T obj);
    <T> T deserializer(byte[] data ,Class<T> clazz);
}
