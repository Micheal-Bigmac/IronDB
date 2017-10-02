package com.irondb.metastore;


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

    public static IronDBContext fromInputStream(InputStream inputStream) {
        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IronDBContext(Maps.fromProperties(properties));
    }

    public String get(String key) {
        return this.parameters.get(key);
    }


    public void put(String key, String value) {
        parameters.put(key, value);
    }


    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }


    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = get(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }



    public Integer getInteger(String key, Integer defaultValue) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public int getInt(String key) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return -1;
    }


    public Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = get(key);
        if (value != null) {
            return Long.parseLong(value.trim());
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public String getString(String key, String defaultValue) {
        return get(key, defaultValue);
    }

    public String getString(String key) {
        return get(key);
    }

    private String get(String key, String defaultValue) {
        String result = parameters.get(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

}
