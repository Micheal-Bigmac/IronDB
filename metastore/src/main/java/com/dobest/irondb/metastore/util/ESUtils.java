package com.dobest.irondb.metastore.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ESUtils {

	private static final String ES_URL = "http://10.241.95.218:9200/";
	private static final Log log = LogFactory.getLog(ESUtils.class);
	
	@SuppressWarnings("unchecked")
	public static boolean setTTL(String index,String type,String ttl){
		StringBuffer newUrl = new StringBuffer(ES_URL);
		newUrl.append(index).append("/").append(type).append("/_mapping");
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			conn = (HttpURLConnection)new URL(newUrl.toString()).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			StringBuffer sb = new StringBuffer();
			sb.append("{\""+type+"\":{\"_ttl\":{\"enabled\":true,\"default\":\"1000\"}}}");
			System.out.println(sb);
			conn.getOutputStream().write(sb.toString().getBytes());
			if(conn.getResponseCode() == 200){
				in = conn.getInputStream();
			}
			if (in != null) {
				byte[] buff = new byte[10240];
				int len = 0;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				while ((len = in.read(buff)) > 0) {
					out.write(buff, 0, len);
				}
				byte[] bytes = out.toByteArray();
				String jsonResult = new String(bytes);
				Map<String, Object> jsonMap = JSON.parseObject(jsonResult, Map.class);
				Object result = jsonMap.get("acknowledged");
				if(result!=null&&result instanceof Boolean){
					return Boolean.parseBoolean(String.valueOf(result));
				}
			}
		} catch (Exception e) {
			log.error(e);
		} finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
		return false;
	}
}
