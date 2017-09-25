package com.yoka.irondb;

import java.io.IOException;

public class ValidateCheck {

    public static boolean isEmpty(String param){
        if (param == null || "".equals(param)) {
            try {
                throw new IOException("Empty " + param);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  true;
        }
        return  false;
    }

}
