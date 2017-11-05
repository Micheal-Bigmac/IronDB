package com.dobest.irondb.metastore.executor;

import com.dobest.irondb.metastore.bean.TableSchema;
import com.dobest.irondb.metastore.util.ElasticSearchUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.dobest.irondb.metastore.util.ElasticSearchUtil.deleteIndexType;

public class TestElasticSearchUtil {

    @Test
    public void test1() throws IOException {

//        String template = ElasticSearchUtil.getJsonMapping("order",Order.class);
        List<Object> orders = new ArrayList<>();
        Order order = null;
        Random random = new Random();
        for(int i =0 ;i < 100;i++){
            order=new Order();
            order.system_id="system_"+i;
            order.finger_id="finger_"+i;
            order.merchant_id="merchaint_"+i;
            order.tx_money=random.nextDouble();
            order.pay_date="pay_date_"+i;
            order.payment_status="payment_status_"+i;
            order.settle_date=new Date();
            order.goods_count=random.nextLong();
            orders.add(order);
        }
//        ElasticSearchUtil.insert("test9","order",orders);

        List<TableSchema> schemas = new ArrayList<>();
        TableSchema schema = new TableSchema();
        schema.setColumn_name("test1");
        schema.setType("int");
        schemas.add(schema);
        TableSchema schema1 = new TableSchema();
        schema1.setColumn_name("test2");
        schema1.setType("string");
        schemas.add(schema1);
        String jsonMapping = ElasticSearchUtil.getJsonMapping("order", schemas,null);
        System.out.println(jsonMapping);
//        XContentBuilder x = XContentFactory.jsonBuilder().value(jsonMapping);
//        System.out.println(x.toString());

//        boolean b = ESUtils.setTTL("test9", "order", "2000");
//        System.out.println(b);
//        deleteIndexType("test9","order");
        deleteIndexType("irondbindex","user_image1");
        try {
//            boolean b = DoMapping("test9", "order", jsonMapping);
//            System.out.println(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ElasticSearchUtil.DoMapping("test9", "order",template);
//        addColumnOperation("test9","order","oo^oo");
//        deleteIndex("test9");

    }


    public void test3() throws IOException {
        String mapping = "{\"bla\":\"blub\"}";

    }

}
