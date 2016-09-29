package com.utils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author 张剑
 * @date 2014年8月14日
 * @time 下午5:49:52
 */
public class ZJ_StringUtils {

	public static boolean isNotEmpty(Object obj) {
		if (null == obj || "".equals(obj.toString().trim())) {
			return false;
		} else {
			return true;
		}
	}
	
	public static String getFileName(String url){
		String fileName = url.substring(url.lastIndexOf("/"));
		if(fileName.startsWith("/")){
			fileName = fileName.replaceFirst("/", "");
		}
		return fileName;
	}

	public static boolean isNullOrEmpty(Object obj) {
		if (null == obj || "".equals(obj.toString().trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static String removeLastChar(String sourceStr, String removeStr) {
		if (null != sourceStr && null != removeStr && sourceStr.endsWith(removeStr)) {
			sourceStr = sourceStr.substring(0, sourceStr.length() - removeStr.length());
		}
		return sourceStr;
	}

	public static String removeFirstChar(String sourceStr, String removeStr) {
		if (null != sourceStr && null != removeStr && sourceStr.startsWith(removeStr)) {
			sourceStr = sourceStr.replaceFirst(removeStr, "");
		}
		return sourceStr;
	}

	/**
	 * 字符串Set中是否包含字符串
	 * 
	 * @param strSet
	 * @param str
	 * @return
	 */
	public static boolean isHasStr(Set<String> strSet, String str) {
		for (String string : strSet) {
			if (string.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 把类似 1,2,3转为 '1','2','3'
	 * 
	 * @param str
	 * @return
	 */
	public static String getInStr(String str) {
		if (null == str || "".equals(str.trim()) || str.startsWith("'")) {
			return str;
		} else {
			if (str.startsWith(",")) {
				str = str.substring(1);
			}
			if (str.endsWith(",")) {
				str = str.substring(0, str.length() - 1);
			}
			String str2 = "";
			String[] temp = str.split(",");
			for (String string : temp) {
				str2 += ",'" + string + "'";
			}
			if (str2.startsWith(",")) {
				str2 = str2.substring(1, str2.length());
			}
			return str2;
		}
	}

	/**
	 * 获取类似'1','2','3'的字符串
	 * 
	 * @param strSet
	 * @return
	 * @author 张剑
	 * @date 2014年9月22日 上午10:53:12
	 */
	public static String getInStr(Set<String> strSet) {
		if (strSet.size() == 0) {
			return "''";
		} else {
			String result = "";
			for (String str : strSet) {
				result += "'" + str + "',";
			}
			result = removeLastChar(result, ",");
			return result;
		}
	}

	public static String getInStr(String[] strArr) {
		if (strArr.length == 0) {
			return "''";
		} else {
			String result = "";
			for (String str : strArr) {
				result += "'" + str + "',";
			}
			result = removeLastChar(result, ",");
			return result;
		}
	}

	public static String listToStr(List<String> strList) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : strList) {
			stringBuilder.append(string + ",");
		}
		return removeLastChar(stringBuilder.toString(), ",");
	}
	
	public static String listToStr(List<String> strList,String separate) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : strList) {
			stringBuilder.append(string + separate);
		}
		return removeLastChar(stringBuilder.toString(), separate);
	}

	public static String setToStr(Set<String> set) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : set) {
			stringBuilder.append(string + ",");
		}
		return removeLastChar(stringBuilder.toString(), ",");
	}

	public static void main(String[] args) {
		String aa = ",1,2,3,4,";
		System.out.println(removeLastChar(aa, ","));
		System.out.println(removeFirstChar(aa, ","));
		System.out.println(getInStr("1,2,,3,"));
		Set<String> set = new TreeSet<String>();
		set.add("1");
		set.add("4");
		set.add("5");
		System.out.println(getInStr(set));
		System.out.println(getFileName("fsfsdfsdfsfsd/fsdfsf/ffff.jpg"));
	}
}
