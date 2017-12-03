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
    private static IronDBContext context;
    private int restClientDefaultMaxPerRoute;
    private int restClientMaxTotal;
    private int cacheSyncRetrys;
    private String[] restServers;
    private String zookeeperBasePath;
    private String metadataUrlPrefix;
    private String zookeeperConnectString;

    public IronDBContext() {
        parameters = Collections.synchronizedMap(new HashMap<String, String>());
        context=this;
    }

    public IronDBContext(Map<String, String> paramters) {
        this();
        this.putAll(paramters);
    }

    public static String getMetaStoreScript() {
        return get("IronDb.metastore.initSql.script");
    }

    public static String getUrl() {
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

    public static IronDBContext fromInputStream() {
        Properties properties = new Properties();
        InputStream resAsStream=null;
        try {
            resAsStream= IronDBContext.class.getResourceAsStream("/IronDB.properties");
            properties.load(resAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(resAsStream!=null) {
                try {
                    resAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new IronDBContext(Maps.fromProperties(properties));
    }

    public static String get(String key) {
        return context.parameters.get(key);
    }


    public  static void put(String key, String value) {
        context.parameters.put(key, value);
    }


    public static  boolean containsKey(String key) {
        return context.parameters.containsKey(key);
    }


    public  static Boolean getBoolean(String key, Boolean defaultValue) {
        String value =  context.parameters.get(key);
        if (value != null) {
            return Boolean.parseBoolean(value.trim());
        }
        return defaultValue;
    }

    public  static Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }



    public  static Integer getInteger(String key, Integer defaultValue) {
        String value =  context.parameters.get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public static  int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public static  int getInt(String key) {
        String value = get(key);
        if (value != null) {
            return Integer.parseInt(value.trim());
        }
        return -1;
    }


    public  Integer getInteger(String key) {
        return getInteger(key, null);
    }

    public  Long getLong(String key, Long defaultValue) {
        String value =  context.parameters.get(key);
        if (value != null) {
            return Long.parseLong(value.trim());
        }
        return defaultValue;
    }

    public  Long getLong(String key) {
        return getLong(key, null);
    }

//    public  String getString(String key, String defaultValue) {
//        return get(key, defaultValue);
//    }

    public static  String getString(String key) {
        return context.parameters.get(key);
    }

    private  String get(String key, String defaultValue) {
        String result =  context.parameters.get(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    public int getRestClientDefaultMaxPerRoute() {
        return restClientDefaultMaxPerRoute;
    }

    public void setRestClientDefaultMaxPerRoute(int restClientDefaultMaxPerRoute) {
        this.restClientDefaultMaxPerRoute = restClientDefaultMaxPerRoute;
    }

    public int getRestClientMaxTotal() {
        return restClientMaxTotal;
    }

    public void setRestClientMaxTotal(int restClientMaxTotal) {
        this.restClientMaxTotal = restClientMaxTotal;
    }

    public int getCacheSyncRetrys() {
        return cacheSyncRetrys;
    }

    public void setCacheSyncRetrys(int cacheSyncRetrys) {
        this.cacheSyncRetrys = cacheSyncRetrys;
    }

    public String[] getRestServers() {
        return restServers;
    }

    public void setRestServers(String[] restServers) {
        this.restServers = restServers;
    }

    public String getZookeeperBasePath() {
        return zookeeperBasePath;
    }

    public void setZookeeperBasePath(String zookeeperBasePath) {
        this.zookeeperBasePath = zookeeperBasePath;
    }

    public String getMetadataUrlPrefix() {
        return metadataUrlPrefix;
    }

    public void setMetadataUrlPrefix(String metadataUrlPrefix) {
        this.metadataUrlPrefix = metadataUrlPrefix;
    }

    public String getZookeeperConnectString() {
        return zookeeperConnectString;
    }

    public void setZookeeperConnectString(String zookeeperConnectString) {
        this.zookeeperConnectString = zookeeperConnectString;
    }
}
