package com.irondb.metastore.rpc.api;

/**
 * Created by Micheal on 2017/10/4.
 */
public class TestBean {
    private int count = 12;

    public int setAndGet(int num) {
        return count + num;
    }
}
