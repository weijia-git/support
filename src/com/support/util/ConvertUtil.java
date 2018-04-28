package com.support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * ��ʽת�������ࣺ��ͬ��λ���ת�������涼�Ǿ�̬������ȫ�����ǿ�ֱ��ʹ�õ�
 * 
 * @author xj
 */
public class ConvertUtil {
	/**
	 * StringתDate���ַ�����ʽ��"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param str
	 * @return
	 */
	public static Date String2Date(String str) {
		if (str == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * StringתDate
	 * 
	 * @param str
	 *            �����ַ���
	 * @param pattern
	 *            �ַ�����ʽ��"yyyy-MM-dd HH:mm:ss" ���� "yyyy-MM-dd"
	 * @return
	 */
	public static Date String2Date(String str, String pattern) {
		if (str == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * DateתString ת����ʽ��yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String Date2String(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	/**
	 * DateתString ת����ʽ��yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @param pattern
	 *            ת����ʽ�����磺"yyyy-MM-dd HH:mm:ss"��"yyyy-MM-dd"
	 * @return
	 */
	public static String Date2String(Date date, String pattern) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * ��Bitmapת����InputStream
	 * 
	 * @param bm
	 * @param quality
	 * @return
	 */
	public static InputStream Bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/**
	 * ��pxֵת��Ϊdip��dpֵ����֤�ߴ��С����
	 * 
	 * @param pxValue
	 * @param scale
	 *            ��DisplayMetrics��������density��
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * ��dip��dpֵת��Ϊpxֵ����֤�ߴ��С����
	 * 
	 * @param dipValue
	 * @param scale
	 *            ��DisplayMetrics��������density��
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * ��pxֵת��Ϊspֵ����֤���ִ�С����
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * ��spֵת��Ϊpxֵ����֤���ִ�С����
	 * 
	 * @param spValue
	 * @param fontScale
	 *            ��DisplayMetrics��������scaledDensity��
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

}
