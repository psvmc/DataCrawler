package com.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class ZJ_PinyinUtils {

	/**
	 * 中文转拼音
	 * 
	 * @param name
	 * @param IgnorePinyin
	 *            是否忽略字符串中的拼音
	 * @return
	 */
	public static String HanyuToPinyin(String name, boolean IgnorePinyin) {
		String pinyinName = "";
		char[] nameChar = name.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (!IgnorePinyin) {
					pinyinName += nameChar[i];
				}
			}
		}
		return pinyinName;
	}

	/**
	 * 中文获取首字母
	 * 
	 * @param name
	 * @param IgnorePinyin
	 *            是否忽略字符串中的拼音
	 * @return
	 */
	public static String HanyuToPinyinFirst(String name, boolean IgnorePinyin) {
		String pinyinName = "";
		char[] nameChar = name.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (!IgnorePinyin) {
					pinyinName += nameChar[i];
				}
			}
		}
		return pinyinName;
	}

	public static void main(String[] args) {
		System.out.println(HanyuToPinyin("s张三丰", true));
		System.out.println(HanyuToPinyin("s张三丰", false));
		System.out.println(HanyuToPinyinFirst("s张三丰", true));
		System.out.println(HanyuToPinyinFirst("s张三丰", false));
	}
}