package com.utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * json对象映射工具类之jackson封装
 * 
 * @author 张剑
 * 
 */
public class ZJ_JacksonUtils {

	private static final Logger log = Logger.getLogger(ZJ_JacksonUtils.class);

	final static ObjectMapper objectMapper;

	/**
	 * 是否打印美观格式
	 */
	static boolean isPretty = true;

	static {
		objectMapper = new ObjectMapper(null, null, null);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * Java对象转Json字符串
	 * 
	 * @param object
	 *            Java对象，可以是对象，数组，List,Map等
	 * @return json 字符串
	 */
	@SuppressWarnings("deprecation")
	public static String toJson(Object object) {
		String jsonString = "";
		try {
			if (isPretty) {
				jsonString = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(object);
			} else {
				jsonString = objectMapper.writeValueAsString(object);
			}
		} catch (Exception e) {
			log.warn("json error:" + e.getMessage());
		}
		return jsonString;

	}

	/**
	 * Json字符串转Java对象
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static Object json2Object(String jsonString, Class<?> clazz) {
		if (jsonString == null || "".equals(jsonString)) {
			return "";
		} else {
			try {
				return objectMapper.readValue(jsonString, clazz);
			} catch (Exception e) {
				log.warn("json error:" + e.getMessage());
				return "";
			}

		}
	}

	/**
	 * JSON串转换为Java泛型对象，可以是各种类型，此方法最为强大。用法看测试用例。
	 * 
	 * @param <T>
	 * @param jsonString
	 *            JSON字符串
	 * @param tr
	 *            TypeReference,例如: new TypeReference< List<FamousUser> >(){}
	 * @return List对象列表
	 */
	@SuppressWarnings("unchecked")
	public static <T> T json2GenericObject(String jsonString, TypeReference<T> tr) {

		if (jsonString == null || "".equals(jsonString)) {
			return null;
		} else {
			try {
				return (T) objectMapper.readValue(jsonString, tr);
			} catch (Exception e) {
				log.warn("json error:" + e.getMessage());
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, String> m = new HashMap<String, String>();
		m.put("name", null);
		m.put("性别", "男");
		m.put("url", "http://aa/bb.jsp");
		System.out.println(toJson(m));
		System.out.println(json2Object(toJson(m), Map.class));
	}

}