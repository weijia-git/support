package com.support.http;

import com.squareup.okhttp.Response;

public interface ResponseHandler {
	public void onSuccess( Response res );
	public void onFailed(Throwable e);
}
