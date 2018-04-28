package com.support.httputil.okhttp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import okio.BufferedSink;

/**
 * 网络请求工具类
 * 
 */
public class HttpUtils {

	private static final String TAG = "HttpUtils";

	private static HttpUtils httpUtils;
	private OkHttpClient client; // OKHTTP客户端
	private HttpConfig okHttpConfig; // OKHTTP客户端配置文件
	private Map<String, Call> callMap; // 请求集合

	private static final MediaType IMAGE_JPG = MediaType.parse("image/pjpeg");
	private static final String URL_ERROR_NOT_INIT = "URL为Null或不合法";
	public static final int ERROR = -1;
	public static final int TYPE_STRING = 0;
	public static final int TYPE_JSON = 1;
	public static final int TYPE_STREAM = 2;
	public int getType = TYPE_STRING;

	/**
	 * 获取网络请求工具类
	 * 
	 * @return
	 */
	public static HttpUtils getInstance() {
		if (httpUtils == null) {
			synchronized (HttpUtils.class) {
				httpUtils = new HttpUtils();
				httpUtils.client = new OkHttpClient();
				httpUtils.callMap = new HashMap<String, Call>();
				return httpUtils;
			}
		}

		return httpUtils;
	}

	/**
	 * 已拼接好包含中文字符url需要转换后再使用
	 * 
	 * @param url
	 * @return
	 */
	public static String encode(String url) {
		String newUrl = url;
		if (url != null) {
			Uri uri = Uri.parse(url);
			if (uri != null) {
				String query = uri.getQuery();
				if (!TextUtils.isEmpty(query)) {
					newUrl = url.replaceAll(query, Uri.encode(query, "=&"));
				}
			}
		}
		return newUrl;
	}

	/**
	 * 初始化网络请求工具类
	 * 
	 * @param config
	 */
	public void init(HttpConfig config) {
		this.okHttpConfig = config;
		Cache cache = new Cache(config.getCacheDir(), config.getCacheSize());
		client.setCache(cache);
		client.setConnectTimeout(config.getConnectTimeout(), TimeUnit.SECONDS);
		client.setProxy(config.getProxy());
		client.setProxySelector(config.getProxySelector());
		client.setRetryOnConnectionFailure(config.isRetryOnConnectionFailure());
		client.setReadTimeout(config.getReadTimeOut(), TimeUnit.SECONDS);
		client.setWriteTimeout(config.getWriteTimeOut(), TimeUnit.SECONDS);
		client.setSocketFactory(config.getSocketFactory());
		client.setSslSocketFactory(config.getSslSocketFactory());
		client.setHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});
		SSLContext sc;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new MyTrustManager() },
					new SecureRandom());
			// client.setDefaultSSLSocketFactory(sc.getSocketFactory());
			client.setSslSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取HTTP请求缓存大小
	 */
	public long getCacheSize() {
		long size = 0;
		if (client.getCache() != null) {
			try {
				size = client.getCache().getSize();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size;
	}

	/**
	 * 清除HTTP请求缓存
	 */
	public void clearCache() {
		if (client.getCache() != null) {
			try {
				client.getCache().evictAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据URL取消HTTP请求
	 * 
	 * @param url
	 */
	public void cancelByUrl(String url) {
		for (Map.Entry<String, Call> call : callMap.entrySet()) {
			if (url.equals(call.getKey())) {
				call.getValue().cancel();
				callMap.remove(call.getKey());
			}
		}
	}

	/**
	 * 根据Tag取消HTTP请求
	 */
	public void cancelByTag(Object tag) {
		client.cancel(tag);
	}

	/**
	 * 检查url,返回true为不合法url
	 * 
	 * @param url
	 * @return
	 */
	private boolean checkUrl(String url) {
		return TextUtils.isEmpty(url) || !url.startsWith("http");
	}

	/**
	 * 编码转换
	 * 
	 * @param params
	 * @return
	 */
	private String formatParams(List<BasicNameValuePair> params) {
		return URLEncodedUtils.format(params, "UTF-8");
	}

	/**
	 * GET添加多个参数
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public String attachHttpGetParams(String url, Map<String, String> paramMap) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (paramMap != null && !paramMap.isEmpty()) {
			for (Map.Entry<String, String> itr : paramMap.entrySet()) {
				if (itr.getValue() != null) {
					params.add(new BasicNameValuePair(itr.getKey(), itr
							.getValue()));
				} else {
					Log.e(TAG, "key:" + itr.getKey() + "的Value取值为null");
				}
			}
			return url + "?" + formatParams(params);
		} else {
			return url;
		}
	}

	/**
	 * 获取POST请求实体
	 * 
	 * @return
	 */
	private RequestBody getPostBody(Map<String, String> params) {
		FormEncodingBuilder builder = new FormEncodingBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (param.getValue() != null) {
					builder.add(param.getKey(), param.getValue());
				} else {
					Log.e(TAG, "key:" + param.getKey() + "的Value取值为null");
				}
			}
		}
		return builder.build();
	}

	/**
	 * 构造请求Builder
	 * 
	 * @return
	 */
	private Request.Builder createBuilder(String method, String url,
			String tag, Map<String, String> headers,
			Map<String, String> params, Boolean isRefresh,
			HttpConfig okHttpConfig) {
		Request.Builder builder = new Request.Builder();
		builder.url(url);
		if (okHttpConfig != null)
			this.okHttpConfig = okHttpConfig;
		if (params != null && !params.isEmpty()) {
			if (method.equals(HttpGet.METHOD_NAME)) {
				builder.url(attachHttpGetParams(url, params));
			} else {
				builder.post(getPostBody(params));
			}
		} else if (method.equals(HttpPost.METHOD_NAME)) {
			builder.post(RequestBody.create(MediaType.parse("text/html"), ""));
		}

		// 添加默认Headers
		if (this.okHttpConfig.getHeads() != null
				&& !this.okHttpConfig.getHeads().isEmpty()) {
			for (Map.Entry<String, String> header : this.okHttpConfig
					.getHeads().entrySet()) {
				builder.header(header.getKey(), header.getValue());
			}
		}
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				builder.addHeader(header.getKey(), header.getValue());
			}
		}

		// 添加取消请求标志
		builder.tag(tag);

		if (isRefresh == null) {
			// 缓存未过期时读缓存,缓存过期时读线上最新.
			builder.cacheControl(new CacheControl.Builder().build());
		} else {
			if (isRefresh) {
				// 强制读线上最新
				builder.cacheControl(new CacheControl.Builder().maxAge(0,
						TimeUnit.SECONDS).build());
			} else {
				// 只读取缓存
				builder.cacheControl(new CacheControl.Builder()
						.maxStale(this.okHttpConfig.getCacheTime(),
								TimeUnit.SECONDS).onlyIfCached().build());
			}
		}
		return builder;
	}

	/**
	 * 构造请求Builder
	 * 
	 * @return
	 */
	private Request.Builder createBuilder(String method, String url,
			String tag, Map<String, String> headers,
			Map<String, String> params, Boolean isRefresh) {
		return createBuilder(method, url, tag, headers, params, isRefresh, null);
	}

	/**
	 * 执行请求
	 * 
	 * @param request
	 * @param httploadingListener
	 */
	private void run(final Request request, final int getType,
			final HttploadingListener httploadingListener) {
		Call call = client.newCall(request);
		callMap.put(request.url().toString(), call);
		call.enqueue(new com.squareup.okhttp.Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				callMap.remove(request.url().toString());
				httploadingListener.sendMessage(httploadingListener
						.obtainMessage(ERROR, 0, e));
				// Log.v(TAG, "IOException:" + request);
			}

			@Override
			public void onResponse(Response response) {
				callMap.remove(request.url().toString());
				int statusCode = response.code();
				// Log.v(TAG, "response:" + response);

				if (response.isSuccessful()
						|| statusCode == HttpStatus.SC_NOT_MODIFIED) {
					String str = null;
					switch (getType) {
					case TYPE_STRING:
					case TYPE_JSON:
						try {
							str = response.body().string();
							// Log.v(TAG, "" + str);
						} catch (IOException e) {
							httploadingListener.sendMessage(httploadingListener
									.obtainMessage(ERROR, statusCode, e));
							return;
						}
						httploadingListener.sendMessage(httploadingListener
								.obtainMessage(getType, statusCode, str));
						break;
					case TYPE_STREAM:
						InputStream stream = null;
						try {
							stream = response.body().byteStream();
							httploadingListener.sendMessage(httploadingListener
									.obtainMessage(getType, statusCode, stream));
						} catch (IOException e) {
							httploadingListener.sendMessage(httploadingListener
									.obtainMessage(ERROR, statusCode, e));
						}
						break;
					}
				} else {
					httploadingListener.sendMessage(httploadingListener
							.obtainMessage(ERROR, 0, new NullPointerException()));
				}
			}
		});
	}

	/**
	 * GET方式获取字符串
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param isRefresh
	 *            如果httpConfig中设置了cacheTime，该值需传false
	 * @param httpConfig
	 *            请求配置
	 * @param httploadingListener
	 */
	public void getString(String url, String tag, Map<String, String> headers,
			Map<String, String> params, Boolean isRefresh,
			HttpConfig httpConfig, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, isRefresh, httpConfig).build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * GET方式获取字符串,可读取缓存时间为服务端设定
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param httploadingListener
	 */
	public void getString(String url, String tag, Map<String, String> headers,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, null).build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * GET方式获取字符串,可读取缓存时间为服务端设定
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getString(String url, String tag,
			HttploadingListener httploadingListener) {
		getString(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取缓存中的字符串
	 * （如Response中无Cache-Control,可读缓存时间为本地设置的max-Stale时间。如果有设Cache
	 * -Control的max-age时间，可读缓存时间则为两者时间相加）
	 * 
	 * @param url
	 * @param headers
	 * @param params
	 * @param httploadingListener
	 */
	public void getExpireCacheString(String url, String tag,
			Map<String, String> headers, Map<String, String> params,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, false).build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * GET方式获取缓存中的字符串（如Response中无Cache-Control,可读缓存时间为本地设置的max-Stale时间。如果有设Cache
	 * -Control的max-age时间，可读缓存时间则为两者时间相加）
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getExpireCacheString(String url, String tag,
			HttploadingListener httploadingListener) {
		getExpireCacheString(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取最新的字符串
	 * 
	 * @param url
	 * @param heads
	 * @param httploadingListener
	 */
	public void getRefreshString(String url, String tag,
			Map<String, String> heads, Map<String, String> params,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, heads,
				params, true).build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * GET方式获取最新的字符串
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getRefreshString(String url, String tag,
			HttploadingListener httploadingListener) {
		getRefreshString(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取JSON,可读取缓存时间为服务端设定
	 * 
	 * @param url
	 * @param headers
	 * @param httploadingListener
	 */
	public void getJson(String url, String tag, Map<String, String> headers,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, null).build();
		run(request, TYPE_JSON, httploadingListener);
	}

	/**
	 * GET方式获取JSON,可读取缓存时间由服务端设定
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getJson(String url, String tag,
			HttploadingListener httploadingListener) {
		getJson(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取缓存中的JSON（如Response中无Cache-Control,可读缓存时间为本地设置的max-Stale时间。
	 * 如果有设Cache-Control的max-age时间，可读缓存时间则为两者时间相加）
	 * 
	 * @param url
	 * @param headers
	 * @param httploadingListener
	 */
	public void getExpireCacheJson(String url, String tag,
			Map<String, String> headers, Map<String, String> params,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, false).build();
		run(request, TYPE_JSON, httploadingListener);
	}

	/**
	 * GET方式获取缓存中的JSON（如Response中无Cache-Control,可读缓存时间为本地设置的max-Stale时间。
	 * 如果有设Cache-Control的max-age时间，可读缓存时间则为两者时间相加）
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getExpireCacheJson(String url, String tag,
			HttploadingListener httploadingListener) {
		getExpireCacheJson(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取最新的JSON
	 * 
	 * @param url
	 * @param headers
	 * @param httploadingListener
	 */
	public void getRefreshJson(String url, String tag,
			Map<String, String> headers, Map<String, String> params,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, true).build();
		run(request, TYPE_JSON, httploadingListener);
	}

	/**
	 * GET方式获取最新的JSON
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getRefreshJson(String url, String tag,
			HttploadingListener httploadingListener) {
		getRefreshJson(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式获取二进制流
	 * 
	 * @param url
	 * @param headers
	 * @param httploadingListener
	 */
	public void getStream(String url, String tag, Map<String, String> headers,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request request = createBuilder(HttpGet.METHOD_NAME, url, tag, headers,
				params, null).build();
		run(request, TYPE_STREAM, httploadingListener);
	}

	/**
	 * GET方式获取二进制流
	 * 
	 * @param url
	 * @param httploadingListener
	 */
	public void getStream(String url, String tag,
			HttploadingListener httploadingListener) {
		getStream(url, tag, null, null, httploadingListener);
	}

	/**
	 * GET方式 获取Response对象（注：该方法需要放在线程中执行）
	 * 
	 * @param url
	 * @param heads
	 * @param params
	 *            拼接参数
	 * @param isRefresh
	 * @return
	 * @throws IOException
	 */
	public Response get(String url, Map<String, String> heads,
			Map<String, String> params, Boolean isRefresh) throws IOException {
		Request.Builder builder = createBuilder(HttpGet.METHOD_NAME, url, url,
				heads, params, isRefresh);
		Request request = builder.build();
		return client.newCall(request).execute();
	}

	/**
	 * POST方式获取字符串
	 * 
	 * @param url
	 * @param heads
	 * @param httploadingListener
	 * @return
	 */
	public void postString(String url, String tag, Map<String, String> heads,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		Request request = builder.build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * POST方式获取缓存中的字符串
	 * 
	 * @param url
	 * @param heads
	 * @param httploadingListener
	 * @return
	 */
	// public void postExpireCacheString(String url, String tag, Map<String,
	// String> heads, Map<String, String> params,
	// HttploadingListener httploadingListener) {
	// if (checkUrl(url)) {
	// httploadingListener.onFail(new
	// IllegalStateException(URL_ERROR_NOT_INIT));
	// return;
	// }
	// Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
	// heads, params, false);
	// Request request = builder.build();
	// run(request, TYPE_STRING, httploadingListener);
	// }

	/**
	 * POST方式获取JSON
	 * 
	 * @param url
	 * @param heads
	 * @param httploadingListener
	 */
	public void postJson(String url, String tag, Map<String, String> heads,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		Request request = builder.build();
		run(request, TYPE_JSON, httploadingListener);
	}

	/**
	 * POST方式获取二进制流
	 * 
	 * @param url
	 * @param heads
	 * @param httploadingListener
	 */
	public void postStream(String url, String tag, Map<String, String> heads,
			Map<String, String> params, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		Request request = builder.build();
		run(request, TYPE_STREAM, httploadingListener);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param file
	 */
	public void uploadFile(String url, String tag, Map<String, String> heads,
			Map<String, String> params, File file, MediaType mediaType,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		builder.post(RequestBody.create(mediaType, file));
		Request request = builder.build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param filePath
	 */
	public void uploadFile(String url, String tag, Map<String, String> heads,
			Map<String, String> params, String filePath, MediaType mediaType,
			HttploadingListener httploadingListener) {
		uploadFile(url, tag, heads, params, new File(filePath), mediaType,
				httploadingListener);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param bytes
	 * @param mediaType
	 */
	public void uploadFile(String url, String tag, Map<String, String> heads,
			Map<String, String> params, byte[] bytes, MediaType mediaType,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		builder.post(RequestBody.create(mediaType, bytes));
		Request request = builder.build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param inputStream
	 */
	public void uploadFile(String url, String tag, Map<String, String> heads,
			Map<String, String> params, final InputStream inputStream,
			final MediaType mediaType, HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		RequestBody requestBody = new RequestBody() {
			@Override
			public MediaType contentType() {
				return mediaType;
			}

			@Override
			public void writeTo(BufferedSink bufferedSink) throws IOException {
				byte[] buff = new byte[1024];
				int hasRead = 0;
				while ((hasRead = inputStream.read(buff)) > 0) {
					bufferedSink.outputStream().write(buff, 0, hasRead);
				}
			}
		};

		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		builder.post(requestBody);
		Request request = builder.build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * post方式 获取Response对象（注：该方法需要放在线程中执行）
	 * 
	 * @param url
	 * @param tag
	 * @param heads
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public Response post(String url, String tag, Map<String, String> heads,
			Map<String, String> params) throws IOException {
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, params, null);
		Request request = builder.build();
		return client.newCall(request).execute();
	}

	/**
	 * 上传图片 （注：该方法需要放在线程中执行）
	 * 
	 * @param url
	 * @param tag
	 * @param heads
	 * @param bytes
	 *            图片字节数组
	 * @return
	 * @throws IOException
	 */
	public Response uploadImage(String url, String tag,
			Map<String, String> heads, byte[] bytes) throws IOException {
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addFormDataPart("file", new Date().getTime() + ".png",
						RequestBody.create(IMAGE_JPG, bytes)).build();
		Request.Builder bind = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, null, null);
		Request request = bind.post(requestBody).tag(tag).build();
		return client.newCall(request).execute();
	}

	/**
	 * 异步post请求
	 * 
	 * @param url
	 * @param tag
	 * @param heads
	 * @param content
	 * @param dataType
	 * @param httploadingListener
	 */
	public void postAsync(String url, String tag, Map<String, String> heads,
			String content, String dataType,
			HttploadingListener httploadingListener) {
		if (checkUrl(url)) {
			httploadingListener.onFail(new IllegalStateException(
					URL_ERROR_NOT_INIT));
			return;
		}
		if (heads == null) {
			heads = new HashMap<>();
		}
		// Log.v(TAG, "content:" + content);
		// heads.put("Content-Type", dataType);
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, null, null);
		builder.post(RequestBody.create(MediaType.parse(dataType), content));
		Request request = builder.build();
		run(request, TYPE_STRING, httploadingListener);
	}

	/**
	 * 同步post请求
	 * 
	 * @param url
	 * @param tag
	 * @param heads
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public Response postSync(String url, String tag, Map<String, String> heads,
			String content, String dataType) throws IOException {
		Request.Builder builder = createBuilder(HttpPost.METHOD_NAME, url, tag,
				heads, null, null);
		builder.post(RequestBody.create(MediaType.parse(dataType), content));
		Request request = builder.build();
		return client.newCall(request).execute();
	}

	/**
	 * 验证 https 证书
	 * 
	 * @param certificates
	 */
	public void setCertificates(InputStream... certificates) {
		try {
			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				keyStore.setCertificateEntry(certificateAlias,
						certificateFactory.generateCertificate(certificate));

				try {
					if (certificate != null)
						certificate.close();
				} catch (IOException e) {
				}
			}

			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());

			trustManagerFactory.init(keyStore);
			sslContext.init(null, trustManagerFactory.getTrustManagers(),
					new SecureRandom());
			client.setSslSocketFactory(sslContext.getSocketFactory());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
