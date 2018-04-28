package com.support.http;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.support.util.LogUtil;

public class HttpUtil {
	
	public static final String tag = "HttpUtil";

	private static ExecutorService mPool;

	private static OkHttpClient http;

	static {
		mPool = Executors.newCachedThreadPool();
		http = new OkHttpClient();
	}

	public static void get(final String url, final ResponseHandler resHandler) {
		mPool.execute(new Runnable() {
			@Override
			public void run() {
				Request request = new Request.Builder().url(url).build();

				try {
					Response response = http.newCall(request).execute();
					resHandler.onSuccess(response);
				} catch (Exception e) {
					resHandler.onFailed(e);
				}
			}
		});
	}

	public static void postJson(final String url, final String json, final ResponseHandler resHandler) {
		LogUtil.d(tag,url);
		LogUtil.d(tag, json);
		mPool.execute(new Runnable() {

			@Override
			public void run() {
				RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"), json.getBytes());
				Request req = new Request.Builder().url(url).post(reqBody).build();
				try {
					Response res = http.newCall(req).execute();
					resHandler.onSuccess(res);
				} catch (IOException e) {
					resHandler.onFailed(e);
				}
			}
		});

	}

	public static Response get(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		return http.newCall(request).execute();
	}

	public static String getString(String url) throws IOException {
		Response res = get(url);
		if (res.isSuccessful()) {
			return res.body().string();
		} else {
			throw new IOException("request failed:" + res.toString());
		}
	}

}
