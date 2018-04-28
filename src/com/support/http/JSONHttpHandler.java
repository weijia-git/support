package com.support.http;

import org.json.JSONException;
import org.json.JSONObject;

import com.support.util.LogUtil;

public abstract class JSONHttpHandler extends StringHandler {
	public abstract void onSuccess(JSONObject json);
	@Override
	public void onSuccess(String content) {
		LogUtil.d("HttpUtil",content);
		try {
			onSuccess(new JSONObject(content));
		} catch (JSONException e) {
			onFailed(e);
		}
	}
}
