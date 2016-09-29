package com.utils;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @文件名：GeneratorUtils.java
 * @作用：
 * @作者：张剑
 * @创建时间：2014-2-12
 */
public class ZJ_GeneratorUtils {
	private static int i = 0;

	/**
	 * 主键生成器
	 * 
	 * @return
	 */
	public static String idGenerator() {
		String str = Long.toString(System.currentTimeMillis(), 36);
		String upNumStr = Long.toString(i, 36);
		if (upNumStr.length() == 1) {
			upNumStr = "0" + upNumStr;
		}
		str += upNumStr;
		i = i < 1295 ? ++i : 0;
		return str.toUpperCase();
	}

	/**
	 * 获取下一个id
	 * 
	 * @param id
	 * @return
	 * @author 张剑
	 * @date 2014年12月1日 上午7:45:02
	 */
	public static String idNext(String id) {
		if (isNumeric(id)) {
			return Integer.valueOf(id) + 1 + "";
		} else {
			return Long.toString(Integer.valueOf(id, 36) + 1, 36);
		}
	}

	/**
	 * 判读字符串是否是正整数
	 * 
	 * @param str
	 * @return
	 * @author 张剑
	 * @date 2014年12月1日 上午7:48:44
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 数字主键生成器
	 * 
	 * @return
	 */
	public static String idNumGenerator() {
		String str = Long.toString(System.currentTimeMillis());
		String upNumStr = i + "";
		if (upNumStr.length() == 1) {
			upNumStr = "0" + upNumStr;
		}
		str += upNumStr;
		i = i < 100 ? ++i : 0;
		return str;
	}

	/**
	 * UUID生成器
	 * 
	 * @return
	 */
	public static String uuid() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "").toUpperCase();
		return uuid;
	}

	/**
	 * 数字转定长字符串
	 * 
	 * @param num
	 * @param length
	 * @return
	 */
	public static String num2string(int num, int length) {
		return String.format("%0" + length + "d", num);
	}

	/**
	 * 生成随机定长字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 生成随机定长数字字符串
	 * 
	 * @param length
	 * @return
	 */
	public static String getRandomNumString(int length) {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * length表示生成数字的位数
	 * 
	 * @param length
	 * @return
	 */
	public static Long getRandomNum(int length) {
		return (long) (Math.pow(10, length - 1) + 9 * Math.random() * Math.pow(10, length - 1));
	}

	public static void main(String[] args) {
		for (int i = 0; i < 200; i++) {
			System.out.println(idGenerator());
		}
	}

}
