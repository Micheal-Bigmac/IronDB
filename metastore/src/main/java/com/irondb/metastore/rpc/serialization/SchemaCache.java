package com.irondb.metastore.rpc.serialization;

import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Micheal on 2017/10/3.
 */
public class SchemaCache {
    private static SchemaCache schemaCache;
    private Cache<Class<?>, Schema<?>> cache = CacheBuilder.newBuilder().maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS).build();

    public static SchemaCache getInstance(){
        if(schemaCache ==null){
            schemaCache =new SchemaCache();
        }
        return schemaCache;
    }
    public Schema<?> getSchema(Class<?> cls) {
        Schema<?> schema = null;
        try {
            schema = cache.get(cls, new Callable<Schema<?>>() {
                @Override
                public Schema<?> call() throws Exception {
                    return RuntimeSchema.createFrom(cls);
                }
            });
        } catch (ExecutionException e) {
            // Error 自定义 有待完善
            e.printStackTrace();
        }

        return schema;
    }
}
