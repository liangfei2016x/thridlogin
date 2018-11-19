package com.example.thridlogin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

/**
 * 基于 httpclient 4.5.2版本的 http工具类
 * @author jueyimin.wang
 */
public class HttpTookit {
	private static final Logger logger=LoggerFactory.getLogger(HttpTookit.class);
	private static final CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8";

	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(1500).setSocketTimeout(15000).build();
		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

	public static String doGet(String url) {
		return doGet(url, null, CHARSET);
	}

	public static String doPost(String url) {
		return doPost(url, null, CHARSET);
	}

	public static String doGet(String url, Map<String, String> params) {
		return doGet(url, params, CHARSET);
	}

	public static String doPost(String url, Map<String, String> params) {
		return doPost(url, params, CHARSET);
	}

	/**
	 * HTTP Get 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doGet(String url, Map<String, String> params, String charset) {
		return doMethod(url, params, charset, HttpMethod.GET.toString());
	}

	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public static String doPost(String url, Map<String, String> params, String charset) {
		return doMethod(url, params, charset, HttpMethod.POST.toString());
	}
	/**
	 * 核心方法
	 * @param url
	 * @param params
	 * @param charset
	 * @param method
	 * @return
	 */
	private static String doMethod(String url, Map<String, String> params, String charset, String method) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		String passportId = "";
		String accessToken = "";
		try {
			if (params != null && !params.isEmpty()) {
				//当有参数时把参数拼接到url后
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						if(entry.getKey().equals("passportId")){
							passportId = value;
						}else if(entry.getKey().equals("accessToken")){
							accessToken = value;
						}
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpRequestBase httpRequest = null;
			if (StringUtils.equalsIgnoreCase(HttpMethod.GET.toString(), method)) {
				httpRequest = new HttpGet(url);
			} else if (StringUtils.equalsIgnoreCase(HttpMethod.POST.toString(), method)) {
				httpRequest = new HttpPost(url);
			}else{
				//其他方法抛出异常
				throw new RuntimeException("HttpTookit暂时不支持此类型的HTTP请求!");
			}
			//个人版金融界自选股特殊需求
			httpRequest.setHeader("passportId",passportId);
			httpRequest.setHeader("accessToken",accessToken);
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpRequest.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, CHARSET);
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
