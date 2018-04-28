package com.support.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast 统一管理类
 * 
 * @author itboy
 *
 */
public class T {

	/** 不可实例化 */
	private T() {
	}

	private static Toast t;
	private static Context context;
	
	/**
	 * 初始化 Toast 工具类
	 * @param context
	 */
	public static void init(Context context){
		T.context = context;
	}

	private static void showToast(Context context, String text, int duration) {
		if (null == t) {
			synchronized (T.class) {
				if (null == t) {
					t = Toast.makeText(context, null, duration);
				}
			}
		}
		t.setText(text);
		t.setDuration(duration);
		t.show();
	}

	/**
	 * 取消 Toast
	 */
	public static void cancle() {
		t.cancel();
	}

	/**
	 * 显示短 Toast
	 * 
	 * @param msgs
	 */
	public static void show(String text) {
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	/**
	 * 显示长 Toast
	 * 
	 * @param text
	 */
	public static void showLong(String text) {
		showToast(context, text, Toast.LENGTH_LONG);
	}

}
