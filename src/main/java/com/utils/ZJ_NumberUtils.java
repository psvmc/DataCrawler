package com.utils;

public class ZJ_NumberUtils {
	public static Integer getInteger(String str) {
		try {
			if (null == str || "".equals(str)) {
				return null;
			} else {

				return Double.valueOf(str).intValue();
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Double getDouble(String str) {
		try {
			if (null == str || "".equals(str)) {
				return null;
			} else {
				return Double.valueOf(str);
			}
		} catch (Exception e) {
			return null;
		}
	}

}
