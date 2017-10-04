package com.irondb.metastore.rpc.api;

import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by Micheal on 2017/10/4.
 */
public class TestReflect {
    @Test
    public void testReflect() throws Exception {
        TestBean bean=new TestBean();
        Class<?> aClass = bean.getClass();
        Object o = aClass.newInstance();
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for(Method tmp : declaredMethods){
            Class<?>[] parameterTypes = tmp.getParameterTypes();
            Object invoke = tmp.invoke(o, 1);
            System.out.println(invoke);
        }


    }
}
