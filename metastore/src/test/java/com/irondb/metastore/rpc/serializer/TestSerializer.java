package com.irondb.metastore.rpc.serializer;

import com.irondb.metastore.rpc.serialization.Serializer;
import org.junit.Test;

import java.util.ServiceLoader;

/**
 * Created by Micheal on 2017/10/3.
 */
public class TestSerializer {

    @Test
    public  void testFastJson(){

        ServiceLoader<Serializer> load = ServiceLoader.load(Serializer.class);
        for(Serializer tmp : load){
            System.out.println(tmp.getClass().getCanonicalName());
        }

    }
}
