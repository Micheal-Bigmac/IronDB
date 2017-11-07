package com.dobest.irondb.metastore;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

public class IronDBContext {
    private Map<String, String> parameters;
    public static IronDBContext context;

    public IronDBContext() {
        parameters = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public IronDBContext(Map<String, String> paramters) {
        this();
        this.putAll(paramters);
    }

    public String getMetaStoreScript() {
        return get("IronDb.metastore.initSql.script");
    }

    public String getUrl() {
        return get("IronDb.metastore.mysql.uri");
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

    public static IronDBContext getInstance() {
        if (context == null) {
            Properties properties = new Properties();
            InputStream resAsStream = null;
            try {
                resAsStream = IronDBContext.class.getResourceAsStream("/IronDB.properties");
                properties.load(resAsStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (resAsStream != null) {
                    try {
                        resAsStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            context = new IronDBContext(Maps.fromProperties(properties));
        }
        return context;
    }

    public String get(String key) {
        return parameters.get(key);
    }


    public void put(String key, String value) {
        parameters.put(key, value);
    }


    public boolean containsKey(String key) {
        return containsKey(key);
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

//    public  String getString(String key, String defaultValue) {
//        return get(key, defaultValue);
//    }

    public String getString(String key) {
        return get(key);
    }

    private String get(String key, String defaultValue) {
        String result = get(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }
}
