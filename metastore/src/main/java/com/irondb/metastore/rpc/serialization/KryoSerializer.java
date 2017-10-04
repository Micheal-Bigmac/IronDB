package com.irondb.metastore.rpc.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Micheal on 2017/10/3.
 */
public class KryoSerializer  implements Serializer{
    @Override
    public <T> byte[] serializer(T obj) {
        Kryo kryo=new Kryo();
        kryo.setReferences(false);
        kryo.register(obj.getClass(),new JavaSerializer());
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        Output output=new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output,obj);
        output.flush();
        output.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }

    @Override
    public <T> T deserializer(byte[] data, Class<T> clazz) {
        Kryo kryo=new Kryo();
        kryo.setReferences(false);
        kryo.register(clazz,new JavaSerializer());
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(data);
        Input input =new Input(byteArrayInputStream);
        return (T) kryo.readClassAndObject(input);

    }

}
