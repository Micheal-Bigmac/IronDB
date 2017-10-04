package com.irondb.metastore.rpc.client;

import com.irondb.metastore.rpc.api.TestBean;
import org.junit.Test;

/**
 * Created by Micheal on 2017/10/4.
 */
public class TestClientBuilder {
    @Test
    public void testClient(){
        ClientBuilder<TestBean> testBeanClientBuilder = ClientBuilder.buildClass(TestBean.class);
        ClientBuilder<TestBean> localhost = testBeanClientBuilder.forOptions("localhost", 9999, null, null);
        TestBean build = localhost.build();
//       .build();
//        ClientBuilder.forOptions("localhost",9999,null, TestBean.class,null);
    }
}
