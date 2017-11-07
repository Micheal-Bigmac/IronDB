package com.dobest.irondb.metastore.schedule;

public class MyProcessor implements Processor {
    @Override
    public void processHandler() {
        System.out.println(this.getClass().getSimpleName());
        System.out.println(" 我自己定义的 processor");
    }
}
