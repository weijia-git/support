package com.support.util;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 字符串操作工具
 * 
 * @author itboy
 *
 */
public class StringUtil {

	/**
	 * 判断是否是空白字符串
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return str == null ? true : str.trim().length() == 0;
	}
	
	public static String fromBufferedReader(BufferedReader br) throws IOException{
		StringBuilder sb = new StringBuilder();
		String line = null;
		while( (line = br.readLine()) != null ){
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

}
