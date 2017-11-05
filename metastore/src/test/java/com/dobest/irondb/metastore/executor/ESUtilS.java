package com.dobest.irondb.metastore.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * elasticsearch 相关操作工具类
 *
 * @author lzg
 * @date 2016年6月12日
 */
public class ESUtilS {


    /**
     * es服务器的host
     */
    private static final String host = "10.241.95.218";

    /**
     * es服务器暴露给client的port
     */
    private static final int port = 9300;

    /**
     * jackson用于序列化操作的mapper
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 获得连接
     *
     * @return
     * @throws UnknownHostException
     */
    private static Client getClient() throws UnknownHostException {
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").put("index.number_of_shards", 3).put("index.number_of_replicas", 2).build();
        Client client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
        return client;
    }

    public static boolean createIndex(Client client, String index, String type) {
        // mapping
        XContentBuilder mappingBuilder;
        try {
            mappingBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject("name").field("type", "string").field("store", "yes").endObject()
                    .startObject("sex").field("type", "string").field("store", "yes").endObject()
                    .startObject("college").field("type", "string").field("store", "yes").endObject()
                    .startObject("age").field("type", "integer").field("store", "yes").endObject()
                    .startObject("school").field("type", "string").field("store", "yes").field("index", "not_analyzed").endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (Exception e) {
            System.out.println("--------- createIndex 创建 mapping 失败：" + e);
            return false;
        }
        IndicesAdminClient indicesAdminClient = client.admin().indices();
        IndicesExistsRequest existsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse indicesExistsResponse = indicesAdminClient.exists(existsRequest).actionGet();
        if (indicesExistsResponse.isExists()) {
            PutMappingRequest source = Requests.putMappingRequest(index).type(type).source(mappingBuilder);
            PutMappingResponse putMappingResponse = indicesAdminClient.putMapping(source).actionGet();
            return putMappingResponse.isAcknowledged();
        } else {
            CreateIndexResponse createIndexResponse = indicesAdminClient.prepareCreate(index)
                    .addMapping(type, mappingBuilder)
                    .get();
            return createIndexResponse.isAcknowledged();
        }

    }

    public static boolean deleteIndex(Client client, String indexName,String indexType) {
        if (!isIndexExists(client, indexName)) {
            System.out.println(indexName + " not exists");
        } else {
//            DeleteMappingResponse dResponse = client.admin().indices().prepareDeleteMapping(indexName).setType(indexType).execute().actionGet();  // 删除索引下面的type
            DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(indexName).execute().actionGet();   // 删除索引
            if (dResponse.isAcknowledged()) {
                System.out.println("delete index " + indexName + "  successfully!");
                return true;
            } else {
                System.out.println("Fail to delete index " + indexName);
                return false;
            }
        }
        return true;
    }


    // 判断索引是否存在 传入参数为索引库名称
    public static boolean isIndexExists(Client client, String indexName) {
        boolean flag = false;

        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);

        IndicesExistsResponse inExistsResponse = client.admin().indices()
                .exists(inExistsRequest).actionGet();

        if (inExistsResponse.isExists()) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }


    public static void main(String[] args) throws UnknownHostException {
        Client client = ESUtilS.getClient();
        deleteIndex(client, "test9","person");
//        ESUtilS.createIndex(client, "test9", "person");
    }
}