package com.dobest.irondb.metastore.ql;


import org.junit.Test;

public class TestResource {

    public void  main(String args[]){
//        URL resource = this.getClass().getResource("IronDb.Proerties");
//        System.out.println("==");
        String  type="load data local inpath wyp.txt into table wyp;";
        String s = type.replaceAll("load data (.*) inpath (.*) (.*) into table (.*)", "$0-$1-$2-$3");
        System.out.println(s);
    }
    @Test
    public void test(){
        String  type="load data local inpath D://wyp.txt into table wyp;";
        type = type.replaceAll("load[\\s]data[\\s](.*)[\\s]inpath[\\s](.*)[\\s]into[\\s]table[\\s](.*)", "$1--$2--$3");
        System.out.println(type);
    }
}
