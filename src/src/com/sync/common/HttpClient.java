package com.sync.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpClient {
	public static final String CHARSET = "UTF-8";

	// 发送get请求 url?a=x&b=xx形式
	public static String sendGet(String url, String param) throws Exception {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = "";
			if (param.length() != 0) {
				urlName = url + "?" + param;
			} else
				urlName = url;
			URL resUrl = new URL(urlName);
			URLConnection urlConnec = resUrl.openConnection();
			urlConnec.setRequestProperty("accept", "*/*");
			urlConnec.setRequestProperty("connection", "Keep-Alive");
			urlConnec.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			urlConnec.connect();
			in = new BufferedReader(new InputStreamReader(urlConnec.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			throw new Exception("http link fail", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				throw new Exception("http link fail", e);
			}
		}
		return result;
	}

	// post请求方法
	public static String sendPost(String url, String data) throws Exception {
		String response = null;
		try {
			CloseableHttpClient httpclient = null;
			CloseableHttpResponse httpresponse = null;
			try {
				httpclient = HttpClients.createDefault();
				HttpPost method = new HttpPost(url);
				StringEntity stringentity = new StringEntity(data, Charset.forName("UTF-8"));
				stringentity.setContentEncoding("UTF-8");
				method.setHeader("Content-Type", "application/json; charset=UTF-8");
				method.setEntity(stringentity);
				httpresponse = httpclient.execute(method);
				response = EntityUtils.toString(httpresponse.getEntity());
			} finally {
				if (httpclient != null) {
					httpclient.close();
				}
				if (httpresponse != null) {
					httpresponse.close();
				}
			}
		} catch (Exception e) {
			throw new Exception("http link fail", e);
		}
		return response;
	}

}