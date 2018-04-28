package com.support.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil
{
	/**
	 * �������ֻ�������ļ���
	 */
	public static final String FILE_NAME = "share_data";
	
	private static Context context = null;
	
	public static void init(Context context){
		SPUtil.context = context;
	}

	/**
	 * �������ݵķ�����������Ҫ�õ��������ݵľ������ͣ�Ȼ��������͵��ò�ͬ�ı��淽��
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public static void put(String key, Object object)
	{

		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if (object instanceof String)
		{
			editor.putString(key, (String) object);
		} else if (object instanceof Integer)
		{
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean)
		{
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float)
		{
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long)
		{
			editor.putLong(key, (Long) object);
		} else
		{
			editor.putString(key, object.toString());
		}

		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * �õ��������ݵķ��������Ǹ���Ĭ��ֵ�õ���������ݵľ������ͣ�Ȼ���������ڵķ�����ȡֵ
	 * 
	 * @param context
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public static Object get( String key, Object defaultObject)
	{
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);

		if (defaultObject instanceof String)
		{
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer)
		{
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean)
		{
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float)
		{
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long)
		{
			return sp.getLong(key, (Long) defaultObject);
		}

		return null;
	}

	/**
	 * �Ƴ�ĳ��keyֵ�Ѿ���Ӧ��ֵ
	 * @param context
	 * @param key
	 */
	public static void remove(String key)
	{
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * �����������
	 * @param context
	 */
	public static void clear(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * ��ѯĳ��key�Ƿ��Ѿ�����
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains( String key)
	{
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * �������еļ�ֵ��
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String, ?> getAll()
	{
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * ����һ�����SharedPreferencesCompat.apply������һ��������
	 * 
	 * @author zhy
	 * 
	 */
	private static class SharedPreferencesCompat
	{
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * �������apply�ķ���
		 * 
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod()
		{
			try
			{
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e)
			{
			}

			return null;
		}

		/**
		 * ����ҵ���ʹ��applyִ�У�����ʹ��commit
		 * 
		 * @param editor
		 */
		public static void apply(SharedPreferences.Editor editor)
		{
			try
			{
				if (sApplyMethod != null)
				{
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e)
			{
			} catch (IllegalAccessException e)
			{
			} catch (InvocationTargetException e)
			{
			}
			editor.commit();
		}
	}

}
