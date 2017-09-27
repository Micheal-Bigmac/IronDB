package com.irondb.metastore.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于生成主键唯一ID
 */
public class GenericID {
    private static final long ONE_STEP = 10;
    private static final Lock LOCK = new ReentrantLock();
    private static long lastTime = System.currentTimeMillis();
    private static short lastCount = 0;
    private static int count = 0;

    @SuppressWarnings("finally")
    public static long nextId()
    {
        LOCK.lock();
        try {
            if (lastCount == ONE_STEP) {
                boolean done = false;
                while (!done) {
                    long now = System.currentTimeMillis();
                    if (now == lastTime) {
                        try {
                            Thread.currentThread();
                            Thread.sleep(1);
                        } catch (java.lang.InterruptedException e) {
                        }
                        continue;
                    } else {
                        lastTime = now;
                        lastCount = 0;
                        done = true;
                    }
                }
            }
            count = lastCount++;
        }
        finally
        {
            LOCK.unlock();
            String result =lastTime+""+String.format("%03d",count);
            return Long.valueOf(result);
        }
    }

    public static void main(String[] args)
    {
        //测试
        for(int i=0;i<1000;i++)
        {
//            String x = nextId();
            long y =  nextId();
            System.out.println(" long is "+y);
        }
    }
}
