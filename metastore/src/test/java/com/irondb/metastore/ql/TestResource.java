package com.irondb.metastore.ql;

import java.net.URL;

public class TestResource {

    public void  main(String args[]){
        URL resource = this.getClass().getResource("IronDb.Proerties");
        System.out.println("==");
    }
}
