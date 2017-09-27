package com.irondb.metastore;

import com.google.common.io.Resources;

import java.io.IOException;

public class IronDbMetaStore {



    public  static void main(String []args) throws IOException {
            IronDBContext ctx = IronDBContext.fromInputStream( Resources.getResource("IronDB.proerties").openStream());
    }
}
