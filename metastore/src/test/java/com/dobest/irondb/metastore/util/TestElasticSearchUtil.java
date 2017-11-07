package com.dobest.irondb.metastore.util;

import com.alibaba.fastjson.JSON;
import com.dobest.irondb.metastore.bean.TableSchema;
import com.dobest.irondb.metastore.executor.Order;
import com.dobest.irondb.metastore.util.ESUtils;
import com.dobest.irondb.metastore.util.ElasticSearchUtil;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.dobest.irondb.metastore.util.ElasticSearchUtil.*;

public class TestElasticSearchUtil {
    private static final Logger log = LoggerFactory.getLogger(TestElasticSearchUtil.class);

    @Test
    public void test1() {

//        String template = ElasticSearchUtil.getJsonMapping("order",Order.class);
        List<Object> orders = new ArrayList<>();
        Order order = null;
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            order = new Order();
            order.system_id = "system_" + i;
            order.finger_id = "finger_" + i;
            order.merchant_id = "merchaint_" + i;
            order.tx_money = random.nextDouble();
            order.pay_date = "pay_date_" + i;
            order.payment_status = "payment_status_" + i;
            order.settle_date = new Date();
            order.goods_count = random.nextLong();
            orders.add(order);
        }
//        ElasticSearchUtil.insert("irondbindex","order",orders);

        List<TableSchema> schemas = new ArrayList<>();
        TableSchema schema = new TableSchema();
        schema.setColumn_name("test1");
        schema.setType("int");
        schemas.add(schema);
        TableSchema schema1 = new TableSchema();
        schema1.setColumn_name("test2");
        schema1.setType("array");
        schemas.add(schema1);
//        truncate("irondbindex","order");
        try {
//            String jsonMapping = ElasticSearchUtil.getJsonMapping("order", schemas, "100");
            ElasticSearchUtil.existsIndex("irondbindex");
//            System.out.println(jsonMapping);
        } catch (Exception e) {
            log.error("================="+e);
        }
//        XContentBuilder x = XContentFactory.jsonBuilder().value(jsonMapping);
//        System.out.println(x.toString());

//        boolean b = ESUtils.setTTL("test9", "order", "2000");
//        System.out.println(b);
//        deleteIndexType("test9","order");
//        deleteIndexType("irondbindex","order");
//        try {
//            boolean b = DoMapping("irondbindex", "order", jsonMapping);
//            System.out.println(b);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        ElasticSearchUtil.DoMapping("test9", "order",template);
//        addColumnOperation("test9","order","oo^oo");
//        deleteIndex("test9");

    }


    @Test
    public void test3() throws IOException {
        String mapping = "sfasdfsadf.";
        System.out.println(mapping.replaceAll("(.*).$", "$1"));
    }

}
