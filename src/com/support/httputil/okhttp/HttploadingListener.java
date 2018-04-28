package com.support.httputil.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Http请求工具类回调接口
 * 
 */
public abstract class HttploadingListener {

    private Handler handler;

    public HttploadingListener() {
        if (Looper.myLooper() != null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    HttploadingListener.this.handleMessage(msg);
                }
            };
        }
    }

    protected void sendMessage(Message msg) {
        if (handler != null) {
            handler.sendMessage(msg);
        } else {
            handleMessage(msg);
        }
    }

    protected Message obtainMessage(int responseMessage, int statusCode, Object response) {
        Message msg = null;
        if (handler != null) {
            msg = this.handler.obtainMessage(responseMessage, statusCode, 0, response);
        } else {
            msg = new Message();
            msg.what = responseMessage;
            msg.arg1 = statusCode;
            msg.obj = response;
        }
        return msg;
    }

    protected void handleMessage(Message msg) {
        switch (msg.what) {
        case HttpUtils.TYPE_STRING:
            onSuccess2String(msg.arg1, (String) msg.obj);
            break;
        case HttpUtils.TYPE_JSON:
            String str = (String) msg.obj;
            onSuccess2String(msg.arg1, str);
            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            try {
                jsonObject = new JSONObject(str);
            } catch (JSONException e) {
                jsonObject = null;
            }
            try {
                jsonArray = new JSONArray(str);
            } catch (JSONException e) {
                jsonArray = null;
            }
            if (jsonObject == null && jsonArray == null) {
                onFail(new JSONException("is not json"));
            } else if (jsonObject != null) {
                onSuccess2JsonObject(msg.arg1, jsonObject);
            } else if (jsonArray != null) {
                onSuccess2JsonArray(msg.arg1, jsonArray);
            }
            break;
        case HttpUtils.TYPE_STREAM:
            onSuccess2Stream(msg.arg1, (InputStream) msg.obj);
            break;
        case HttpUtils.ERROR:
            onFail((Exception) msg.obj);
            break;
        }
    }

    public void onSuccess2String(int statusCode, String str) {

    }

    public void onSuccess2JsonObject(int statusCode, JSONObject jsonObject) {

    }

    public void onSuccess2JsonArray(int statusCode, JSONArray jsonArray) {

    }

    public void onSuccess2Stream(int statusCode, InputStream inputStream) {

    }

    public void onFail(Exception e) {

    }

}
