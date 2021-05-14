package com.feeye.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * need HttpClient version 4.5.x+
 * 
 * @author owner
 *
 */
public class HttpTool {

	private static Logger logger = LoggerFactory.getLogger(HttpTool.class);

	private static PoolingHttpClientConnectionManager CONNECTION_MANAGER;
	private static CloseableHttpClient client;
	private static String DEFAULT_CHARSET = "utf-8";

	static {
		initHttpConnMgrPool();
		client = HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
				.setConnectionManagerShared(true).build();
	}

	public static Map<String, Object> callApiReturnMap(boolean debug,
			String url, String type) {
		return callApiReturnMap(debug, url, type, null, null);
	}

	public static Map<String, Object> callApiReturnMap(boolean debug,
			String url, String type, Map<String, Object> paramMap) {
		return callApiReturnMap(debug, url, type, null, paramMap);
	}

	public static Map<String, Object> callApiReturnMap(boolean debug,
			String url, String type, Map<String, Object> headerMap,
			Map<String, Object> paramMap) {
		if ("Get".equalsIgnoreCase(type)) {
			return JSON.parseObject(
					callApiByGet(debug, url, DEFAULT_CHARSET, headerMap,
							paramMap),
					new TypeReference<Map<String, Object>>() {
					});
		} else {
			return JSON.parseObject(
					callApiByPost(debug, url, DEFAULT_CHARSET, headerMap,
							paramMap),
					new TypeReference<Map<String, Object>>() {
					});
		}
	}

	public static String callApiByGet(boolean debug, String url) {
		return callApiByGet(debug, url, DEFAULT_CHARSET, null, null);
	}

	public static String callApiByGet(boolean debug, String url,
			Map<String, Object> paramMap) {
		return callApiByGet(debug, url, DEFAULT_CHARSET, null, paramMap);
	}

	public static String callApiByGet(boolean debug, String url,
			String charset, Map<String, Object> headerMap,
			Map<String, Object> paramMap) {
		String rs = "";
		CloseableHttpResponse response = null;
		if (StringUtils.isEmpty(charset)) {
			charset = DEFAULT_CHARSET;
		}
		try {
			if (paramMap != null && !paramMap.isEmpty()) {
				url += "?t=" + System.currentTimeMillis();
				for (String s : paramMap.keySet()) {
					if (paramMap.get(s) != null) {
						url += "&"
								+ s
								+ "="
								+ URLEncoder.encode(paramMap.get(s).toString(),
										charset);
					}
				}
			}

			HttpGet httpGet = new HttpGet(url);
			if (headerMap != null && !headerMap.isEmpty()) {
				for (String s : headerMap.keySet()) {
					if (headerMap.get(s) != null) {
						httpGet.setHeader(s, headerMap.get(s).toString());
					}
				}
			}

			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				rs = EntityUtils.toString(entity, charset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (debug) {
			logger.info("rs --- >>>>>>>>>> " + rs);
		}
		return rs;
	}

	public static String callApiByPost(boolean debug, String url) {
		return callApiByPost(debug, url, DEFAULT_CHARSET, null, null);
	}

	public static String callApiByPost(boolean debug, String url,
			Map<String, Object> paramMap) {
		return callApiByPost(debug, url, DEFAULT_CHARSET, null, paramMap);
	}

	public static String callApiByPost(boolean debug, String url,
			String charset, Map<String, Object> headerMap,
			Map<String, Object> paramMap) {
		String rs = "";
		CloseableHttpResponse response = null;
		if (StringUtils.isEmpty(charset)) {
			charset = DEFAULT_CHARSET;
		}
		try {
			HttpPost httpPost = new HttpPost(url + "?t=" + System.currentTimeMillis());

			if (headerMap != null && !headerMap.isEmpty()) {
				for (String s : headerMap.keySet()) {
					if (headerMap.get(s) != null) {
						httpPost.setHeader(s, headerMap.get(s).toString());
					}
				}
			}

			List<NameValuePair> nvps = initNameValuePair();
			if (paramMap != null && !paramMap.isEmpty()) {
				for (String s : paramMap.keySet()) {
					if (paramMap.get(s) != null) {
						nvps.add(new BasicNameValuePair(s, paramMap.get(s).toString()));
					}
				}
			}

			httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
			response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				rs = EntityUtils.toString(entity, charset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (debug) {
			logger.info("rs --- >>>>>>>>>> " + rs);
		}
		return rs;
	}

	public static String callApiByPostBody(boolean debug, String url,
			String boby) {
		return callApiByPostBody(debug, url, DEFAULT_CHARSET, null, boby);
	}

	public static String callApiByPostBody(boolean debug, String url,
			Map<String, Object> headerMap, String boby) {
		return callApiByPostBody(debug, url, DEFAULT_CHARSET, headerMap, boby);
	}

	public static String callApiByPostBody(boolean debug, String url,
			String charset, Map<String, Object> headerMap, String boby) {
		String rs = "";
		CloseableHttpResponse response = null;
		if (StringUtils.isEmpty(charset)) {
			charset = DEFAULT_CHARSET;
		}
		try {
			HttpPost httpPost = new HttpPost(url + "?t=" + System.currentTimeMillis());

			if (headerMap != null && !headerMap.isEmpty()) {
				for (String s : headerMap.keySet()) {
					if (headerMap.get(s) != null) {
						httpPost.setHeader(s, headerMap.get(s).toString());
					}
				}
			}

			httpPost.setEntity(new StringEntity(boby, charset));
			response = client.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				rs = EntityUtils.toString(entity, charset);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (debug) {
			logger.info("rs --- >>>>>>>>>> " + rs);
		}
		return rs;
	}

	private static List<NameValuePair> initNameValuePair() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// TODO add your owner initial code
		return nvps;
	}

	protected static void init4TwoWayAuth(String keyStoreFile,
			String keyStorePass, String keyPass, String trustStoreFile,
			String trustStorePass) {
		try {
			InputStream ksIs = HttpTool.class.getResourceAsStream(keyStoreFile);
			InputStream tsIs = HttpTool.class
					.getResourceAsStream(trustStoreFile);

			KeyStore ks = KeyStore.getInstance("PKCS12");
			ks.load(ksIs, keyStorePass.toCharArray());

			KeyStore ts = KeyStore.getInstance("JKS");
			ts.load(tsIs, trustStorePass.toCharArray());

			ksIs.close();
			tsIs.close();

			SSLContext sslContext = new SSLContextBuilder()
					.loadKeyMaterial(ks, keyPass.toCharArray())
					.loadTrustMaterial(ts, new TrustSelfSignedStrategy())
					.build();

			SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(
					sslContext);

			Registry<ConnectionSocketFactory> registry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", sslCsf).build();

			CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
					registry);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static void init4TrustAll() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) {
							return true;
						}
					}).build();

			SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(
					sslContext);

			Registry<ConnectionSocketFactory> registry = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", sslCsf).build();

			CONNECTION_MANAGER = new PoolingHttpClientConnectionManager(
					registry);

		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void initHttpConnMgrPool() {
		if (CONNECTION_MANAGER == null) {
			InputStream in = HttpTool.class
					.getResourceAsStream("/ca.properties");
			if(in != null) {
				try {
					Properties properties = new Properties();
					properties.load(in);
	
					String mode = properties.getProperty("ca.mode");
					if ("TwoWayAuth".equalsIgnoreCase(mode)) {
						String keyStoreFile = properties
								.getProperty("ca.keyStoreFile");
						String keyStorePass = properties
								.getProperty("ca.keyStorePass");
						String keyPass = properties.getProperty("ca.keyPass");
						String trustStoreFile = properties
								.getProperty("ca.trustStoreFile");
						String trustStorePass = properties
								.getProperty("ca.trustStorePass");
						init4TwoWayAuth(keyStoreFile, keyStorePass, keyPass,
								trustStoreFile, trustStorePass);
					} else {
						init4TrustAll();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				init4TrustAll();
			}
		}
		// 连接池最大并发连接数
		CONNECTION_MANAGER.setMaxTotal(200);
		// 单路由最大并发数
		CONNECTION_MANAGER.setDefaultMaxPerRoute(20);
	}
}
