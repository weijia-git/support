package cn.wangrq.myweb.utils;

import java.util.List;

public class ListUtil {
	
	public static boolean isEmpty(List<?> list){
		return null == list ? true : list.size() == 0;
	}

	public static String toString(List<?> list){
		return toString(list,',');
	}

	public static String toString(List<?> list,char decollator){
		StringBuilder sb = new StringBuilder();

		if( list == null ){
			sb.append("null");
			return sb.toString();
		}

		if( list.size() == 0 ){
			sb.append("[]");
			return sb.toString();
		}

		sb.append("[");
		for (Object item:list){
			sb.append(item.toString()).append(decollator);
		}

		sb.setCharAt(sb.length()-1,']');

		return sb.toString();
	}
	
//	public static <E> boolean isEmpty(E[] array){
//		return null == array ? true : array.length == 0;
//	}
}
