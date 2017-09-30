package com.dobest.irondb.metastore.util;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.bean.TableSchema;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchUtil {
    private static final Logger log = LoggerFactory.getLogger(ElasticSearchUtil.class);
    private static Client client = null;
    private static IndicesAdminClient adminClient;
    private static final String host = "10.241.95.218";

    private static Client getClient() throws UnknownHostException {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
        return client;
    }

    static {
        try {
            client = getClient();
            adminClient = client.admin().indices();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断ElasticSearch中的索引是否存在   同时还需要判断 index 下面的 type 是否存在
     */
    private static boolean existsIndex(String index) {
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        if (response.isExists()) {
            return true;
        } else
            return false;
    }

    private static boolean existsIndexType(String index, String type) {
        TypesExistsResponse typesExistsResponse = adminClient.typesExists(new TypesExistsRequest(new String[]{index}, type)).actionGet();
        if (typesExistsResponse.isExists()) {
            return true;
        } else
            return false;
    }

    private static boolean createIndex(Client client, String indexName, String type, String mapping) {
        if (existsIndex(indexName)) {
            if (existsIndexType(indexName, type)) {
                return false;
            } else {
                return createIndexTypeWhenIndexExits(indexName, type, mapping);
            }
        } else {
            return createIndexTypWhenIndexNotExits(indexName, type, mapping);
        }
    }

    private static boolean createIndexTypeWhenIndexExits(String indexName, String indexType, String mappingBuilder) {
        PutMappingRequest source = Requests.putMappingRequest(indexName).type(indexType).source(mappingBuilder);
        PutMappingResponse putMappingResponse = adminClient.putMapping(source).actionGet();
        return putMappingResponse.isAcknowledged();
    }

    private static boolean createIndexTypWhenIndexNotExits(String indexName, String indexType, String mappingBuilder) {
        CreateIndexResponse createIndexResponse = adminClient.prepareCreate(indexName)
                .addMapping(indexType, mappingBuilder)
                .get();
        return createIndexResponse.isAcknowledged();
    }

    public static String getJsonMapping(String indexType,Class object) {
        String template = "{\"{indexType}\":{\"properties\":{{#}}}}";
        template = template.replace("{indexType}", indexType);
        String fieldString = getClassMapping(object);
        template = template.replace("{#}", fieldString);
        return template;
    }

    // 向ES index 中 indexType  mapping 添加 一列
    public static String getJsonMapping(String indexType, List<TableSchema> schemas){
        String template = "{\"{indexType}\":{\"properties\":{{#}}}}";
        template = template.replace("{indexType}", indexType);
        StringBuilder fieldstring = new StringBuilder();
        for(int i=0; i< schemas.size();i++) {
            TableSchema schema = schemas.get(i);
            fieldstring.append("\"" + schema.getColumn_name().toLowerCase() + "\": {")
                    .append("\"type\": \"")
                    .append(GetElasticSearchMappingType(schema.getType())).append("");
            if (i ==  schemas.size() - 1) {
                fieldstring.append("}");
            } else {
                fieldstring.append("},");
            }
        }
        String json=fieldstring.toString();
        template = template.replace("{#}",json);
        return template;
    }

    public static boolean DoMapping(String indexName, String indexType, String template) {
        System.out.println(template);
        try {
            if (existsIndex(indexName)) {
                log.info(indexName + "索引已经存在！");
                if (!existsIndexType(indexName, indexType)) {
                    return createIndexTypeWhenIndexExits(indexName, indexType, template);
                } else {
                    return createIndexTypeWhenIndexExits(indexName, indexType, template);  // index  下的 indexType 已经存在  // 但是可以 用来添加字段
                }
            } else {
                return createIndexTypWhenIndexNotExits(indexName, indexType, template);
            }
        } finally {
            System.out.println("创建ElasticSearch Mapping完成！！！");
            log.info("创建ElasticSearch Mapping完成！！！");
        }
    }

    public static void close() {
        client.close();
    }

    public static boolean deleteIndex(String indexName) {
        if (existsIndex(indexName)) {
            DeleteIndexResponse deleteIndexResponse = adminClient.prepareDelete(indexName).execute().actionGet();
            if (deleteIndexResponse.isAcknowledged()) {
                return true;
            } else {
                return false;
            }
        } else
            return true;
    }

    public static boolean deleteIndexType(String indexName, String indexType) {
        if (existsIndexType(indexName, indexType)) {
            DeleteMappingResponse dResponse = client.admin().indices().prepareDeleteMapping(indexName).setType(indexType).execute().actionGet();  // 删除索引下面的type
            if (dResponse.isAcknowledged()) {
                return true;
            } else {
                return false;
            }
        } else
            return true;
    }

    /**
     * 从类的字段映射处elasticsearch中的字段
     *
     * @return
     */
    public static String getClassMapping(Class object) {
        StringBuilder fieldstring = new StringBuilder();
        Field[] fields = object.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fieldstring.append("\"" + fields[i].getName().toLowerCase() + "\": {");
            fieldstring.append("\"type\": \"").append(GetElasticSearchMappingType(fields[i].getType().getSimpleName())).append("");
            if (i == fields.length - 1) {
                fieldstring.append("}");
            } else {
                fieldstring.append("},");
            }
        }
        return fieldstring.toString();
    }

    private static String GetElasticSearchMappingType(String varType) {
//        String es = "String";
        StringBuffer buffer = new StringBuffer();
        switch (varType) {
            case "Date":
                buffer.append("date\",\"format\":\"yyyy-MM-dd HH:mm:ss\",\"null_value\":\"1900-01-01 00:00:01\"");
                break;
            case "Double":
                buffer.append("double\",\"null_value\":\"NaN\"");
                break;
            case "Long":
                buffer.append("long\"");
                break;
            default:
                buffer.append("string\"").append(",").append("\"index\":\"not_analyzed\"");
                break;
        }
        return buffer.toString();
    }

    public static void insert(String index, String indexType,List<Object> items){
        IndexRequestBuilder indexRequestBuilder = client.prepareIndex(index, indexType);
        for(int i=0;i<items.size();i++){
            Object o = items.get(i);
            String s = JSON.toJSONString(o);
            indexRequestBuilder.setSource(s).execute().actionGet();
        }
    }
}