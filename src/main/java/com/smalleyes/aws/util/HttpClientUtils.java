package com.smalleyes.aws.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * HttpClientUtils
 * @author <a href="mailto:284040429@qq.com" >张明俊</a>
 * @version 0.0.1
 *
 */
public class HttpClientUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);
	
	public static String doGetWithIpHeader(String url, String ip) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("X-Forwarded-For",ip);
		headers.put("Client_IP",ip);
		return httpGet(url,headers);
	}

	public static String httpPost(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpPost httpPost = new HttpPost(url);

		if (headerMap != null) {
			for (Map.Entry<String, String> header : headerMap.entrySet()) {
				httpPost.addHeader(header.getKey(), header.getValue());
			}
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			params.add(nameValuePair);
		}

		UrlEncodedFormEntity postEntity;
		try {
			postEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(postEntity);
			HttpResponse httpResponse = client.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}
			String responseContent = IOUtils.toString(httpResponse.getEntity().getContent());
			return responseContent;
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		}
		return null;
	}

	public static String httpPost(String url, Map<String, String> headerMap, String param) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpPost httpPost = new HttpPost(url);

		for (Map.Entry<String, String> header : headerMap.entrySet()) {
			httpPost.addHeader(header.getKey(), header.getValue());
		}

		try {
			StringEntity entity = new StringEntity(param);
			httpPost.setEntity(entity);
			HttpResponse httpResponse = client.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}
			String responseContent = IOUtils.toString(httpResponse.getEntity().getContent());
			return responseContent;
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		}
		return null;
	}

	public static String httpGet(String url, Map<String, String> headerMap) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpGet httpGet = new HttpGet(url);

		for (Map.Entry<String, String> header : headerMap.entrySet()) {
			httpGet.addHeader(header.getKey(), header.getValue());
		}

		try {
			HttpResponse httpResponse = client.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}

			return IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				LOGGER.error("url is[{}]", new Object[] { url, e });
			}
		}
		return null;
	}

	public static String httpGet(String url, Map<String, String> headerMap, String encode) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpGet httpGet = new HttpGet(url);

		if (headerMap != null) {
			for (Map.Entry<String, String> header : headerMap.entrySet()) {
				httpGet.addHeader(header.getKey(), header.getValue());
			}
		}

		try {
			HttpResponse httpResponse = client.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}

			return IOUtils.toString(httpResponse.getEntity().getContent(), encode);

			// StringBuffer buffer=new StringBuffer();
			// InputStream in=httpResponse.getEntity().getContent();
			// InputStreamReader isr = new InputStreamReader(in,encode);
			// BufferedReader reader=new BufferedReader(isr);
			// String content=null;
			// while((content=reader.readLine())!=null) {
			// buffer.append(content);
			// }
			// reader.close();
			// return buffer.toString();
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				LOGGER.error("url is[{}]", new Object[] { url, e });
			}
		}
		return null;
	}

	public static String httpGet(String url) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse httpResponse = client.execute(httpGet);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}

			return IOUtils.toString(httpResponse.getEntity().getContent());
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				LOGGER.error("url is[{}]", new Object[] { url, e });
			}
		}
		return null;
	}

	public static String httpPost(String url, Map<String, String> paramMap) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			NameValuePair nameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue());
			params.add(nameValuePair);
		}

		UrlEncodedFormEntity postEntity;
		try {
			postEntity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(postEntity);
			HttpResponse httpResponse = client.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}
			String responseContent = IOUtils.toString(httpResponse.getEntity().getContent());
			return responseContent;
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		}
		return null;
	}

	public static String httpPost(String url) {
		CloseableHttpClient client = getClientByUrl(url);
		HttpPost httpPost = new HttpPost(url);

		try {
			HttpResponse httpResponse = client.execute(httpPost);
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			if (responseCode != 200) {
				return null;
			}
			String responseContent = IOUtils.toString(httpResponse.getEntity().getContent());
			return responseContent;
		} catch (Exception e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		}
		return null;
	}

	public static CloseableHttpClient getClientByUrl(String url) {
		CloseableHttpClient client = null;
		if (url.startsWith("https://")) {
			client = createHttpsClient();
		} else {
			client = HttpClients.custom().build();
		}
		return client;
	}

	private static CloseableHttpClient createHttpsClient() {

		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();
		} catch (KeyManagementException e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		} catch (KeyStoreException e) {
			LOGGER.warn("Any warn msg that u want to write", e);
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		CookieStore cookieStore = new BasicCookieStore();
		RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY).build();
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(globalConfig)
				.setDefaultCookieStore(cookieStore).setSSLSocketFactory(sslsf).build();

		return client;
	}

}
