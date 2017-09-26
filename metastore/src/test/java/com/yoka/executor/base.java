package com.yoka.executor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class base<T> {
    private Class<?> beanClass;

    public base() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] types = parameterizedType.getActualTypeArguments();
        beanClass = (Class<?>) types[0];
    }

    public static void main(String []args){
        base<Cat>test=new TestBean();
        System.out.println(test.beanClass);
    }
}

class TestBean extends base<Cat> {


    public TestBean() {
        super();
    }
}

class Cat {

    public Cat() {
        super();
    }
}
