package com.support.http;

import java.io.IOException;

import com.squareup.okhttp.Response;

public abstract class StringHandler implements ResponseHandler {
	public abstract void onSuccess(String content);

	@Override
	public void onSuccess(Response res) {
		if (res.isSuccessful()) {
			try {
				onSuccess(res.body().string());
			} catch (IOException e) {
				onFailed(e);
			}
		} else {
			onFailed(new Exception("request failed:" + res.toString()));
		}
	}
}
