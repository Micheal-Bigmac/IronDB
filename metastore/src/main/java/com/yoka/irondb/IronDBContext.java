package com.yoka.irondb;



import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IronDBContext {
    private static Map<String, String> parameters;

    public IronDBContext() {
        parameters = Collections.synchronizedMap(new HashMap<String, String>());
    }
    public IronDBContext(Map<String, String> paramters) {
        this();
        this.putAll(paramters);
    }

    public static String getMetaStoreScript() {
        return parameters.get("IronDb.metastore.initSql.script");
    }

    public static String getUrl() {
        return parameters.get("IronDb.metastore.mysql.uri");
    }

    public void putAll(Map<String, String> map) {
        parameters.putAll(map);
    }

    public static IronDBContext fromInputStream(InputStream inputStream)
    {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IronDBContext(Maps.fromProperties(properties));
    }

    public String get(String key){
        return this.parameters.get(key);
    }

}
