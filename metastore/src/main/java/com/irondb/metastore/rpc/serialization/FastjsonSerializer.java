package com.irondb.metastore.rpc.serialization;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created by Micheal on 2017/10/3.
 */
public class FastjsonSerializer implements Serializer {

    @Override
    public <T> byte[] serializer(T obj) {
        return JSON.toJSONBytes(obj, SerializerFeature.SortField);
    }
    @Override
    public <T> T deserializer(byte[] data, Class<T> clazz) {
        return JSON.parseObject(data, clazz, Feature.SortFeidFastMatch);
    }
}
