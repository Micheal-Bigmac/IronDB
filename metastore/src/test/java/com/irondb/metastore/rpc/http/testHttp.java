package com.irondb.metastore.rpc.http;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by Micheal on 2017/10/6.
 */
public class testHttp  {

    private static void sendPostJson() throws Exception{
        String path = "http://127.0.0.1:8080";
        JSONObject obj = new JSONObject();
        obj.put("id", "10001");
        obj.put("sex", "boy");
        String jsonStr = obj.toJSONString();
        byte[] data = jsonStr.getBytes();
        java.net.URL url = new java.net.URL(path);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode() == 200){
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = in.readLine();
            System.out.println("msg: " + msg);
            in.close();
        }
        conn.disconnect();
    }

    private static void sendPostForm() throws Exception{
        String path = "http://127.0.0.1:8080/";
        String parm = "id=10001&sex=boy";
        byte[] data = parm.getBytes();
        java.net.URL url = new java.net.URL(path);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Connection", "close");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        if(conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String msg = in.readLine();
            System.out.println("msg: " + msg);
            in.close();
        }
        conn.disconnect();
    }

    private static void sendGet() throws Exception{
        String path = "http://127.0.0.1:8080/";
        String reqUrl = path + "?id=10001&sex=boy";
        java.net.URL url = new java.net.URL(reqUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.connect();
        if (conn.getResponseCode() == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String msg = in.readLine();
            System.out.println(msg);
            in.close();
        }
        conn.disconnect();
    }

}
