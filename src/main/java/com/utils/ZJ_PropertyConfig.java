package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.jfinal.kit.PathKit;

/**
 * 
 * PropertyConfig: read properties file file path: ../WEB-INFO/ or classpath
 * 
 */
@SuppressWarnings("deprecation")
public class ZJ_PropertyConfig {

	private ConcurrentMap<String, Object> properties = new ConcurrentHashMap<String, Object>();

	private static ZJ_PropertyConfig config = new ZJ_PropertyConfig();

	private ZJ_PropertyConfig() {
	}

	public static ZJ_PropertyConfig me() {
		return config;
	}

	public void loadPropertyFile(String file) {
		Properties property = new Properties();
		if ((null== file)||("".equals(file)))
			throw new IllegalArgumentException("Parameter of file can not be blank");
		if (file.contains(".."))
			throw new IllegalArgumentException("Parameter of file can not contains \"..\"");

		InputStream inputStream = null;
		String fullFile; // String fullFile = PathUtil.getWebRootPath() + file;
		if (file.startsWith(File.separator))
			fullFile = PathKit.getWebRootPath() + File.separator + "WEB-INF" + file;
		else
			fullFile = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + file;

		try {
			inputStream = new FileInputStream(new File(fullFile));
			property.load(inputStream);
		} catch (Exception eOne) {
			try {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				property.load(loader.getResourceAsStream(file));
			} catch (IOException eTwo) {
				throw new IllegalArgumentException("Properties file loading failed: " + eTwo.getMessage());
			}
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (property != null) {
			System.out.println("--------------加载的配置开始--------------");
			for (Entry<Object, Object> entry : property.entrySet()) {
				this.properties.put(entry.getKey().toString(), entry.getValue());
				System.out.println(entry);
			}
			System.out.println("--------------加载的配置结束--------------");
			
		}
	}

	public String getProperty(String key) {
		if (this.properties.containsKey(key)) {
			return properties.get(key).toString();
		}
		return null;
	}

	public String getProperty(String key, String defaultValue) {
		if (this.properties.containsKey(key)) {
			return properties.get(key).toString();
		}
		return defaultValue;
	}

	public Integer getPropertyToInt(String key) {
		Integer resultInt = null;
		String resultStr = this.getProperty(key);
		if (resultStr != null)
			resultInt = Integer.parseInt(resultStr);
		return resultInt;
	}

	public Integer getPropertyToInt(String key, Integer defaultValue) {
		Integer result = getPropertyToInt(key);
		return result != null ? result : defaultValue;
	}

	public Boolean getPropertyToBoolean(String key) {
		String resultStr = this.getProperty(key);
		Boolean resultBool = null;
		if (resultStr != null) {
			if (resultStr.trim().equalsIgnoreCase("true"))
				resultBool = true;
			else if (resultStr.trim().equalsIgnoreCase("false"))
				resultBool = false;
		}
		return resultBool;
	}

	public Boolean getPropertyToBoolean(String key, boolean defaultValue) {
		Boolean result = getPropertyToBoolean(key);
		return result != null ? result : defaultValue;
	}
}