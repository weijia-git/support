package com.support.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import com.support.util.FileUtil;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

public class ListCache {
	private File cacheDir;

	public ListCache(Context context, String cacheName) {
		cacheDir = FileUtil.getDiskCacheDir(context, cacheName);
	}

	public boolean put(String key, List list) {
		if (TextUtils.isEmpty(key.trim())) {
			return false;
		}
		File cfile = new File(cacheDir, Base64.encodeToString(key.getBytes(), Base64.DEFAULT));
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(cfile));
			oos.writeObject(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public List get(String key) {
		if (TextUtils.isEmpty(key.trim())) {
			return null;
		}
		File cfile = new File(cacheDir, Base64.encodeToString(key.getBytes(), Base64.DEFAULT));
		if (!cfile.exists()) {
			return null;
		}
		ObjectInputStream ois = null;
		List list;
		try {
			ois = new ObjectInputStream(new FileInputStream(cfile));
			list = (List) ois.readObject();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 清空缓存
	 */
	public void clearCache() {
		File[] caches = cacheDir.listFiles();
		for (File f : caches) {
			f.deleteOnExit();
		}
	}

}
