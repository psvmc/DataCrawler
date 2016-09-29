package com.utils;

/**
 * Class工具类
 * 
 * @author 张剑
 * 
 */
public class ZJ_ClazzUtils {
	/**
	 * 判断类不为空
	 * 
	 * @param o
	 * @return
	 */
	public static boolean notNullOrEmpty(Object o) {
		if (null == o || "".equals(o)) {
			return false;
		} else {
			return true;
		}
	}
}
