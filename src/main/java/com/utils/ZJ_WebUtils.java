package com.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取网站相关信息
 * 
 * @author 张剑
 * @datetime 2014年12月3日 下午2:35:15
 * @version 1.0
 */
public class ZJ_WebUtils {
	public static final String BaseUrl = "BaseUrl";
	public static final String baseFilePath = "baseFilePath";
	public static final String baseFileUrl = "baseFileUrl";
	public static final String localIp = "localIp";
	public static final String localName = "localName";
	private static Map<String, String> webMap = null;

	public static Map<String, String> init(HttpServletRequest request) {
		if (null == webMap) {
			webMap = new HashMap<String, String>();
			webMap.put("baseUrl", getBaseUrl(request));
			webMap.put("baseFilePath", getBaseFilePath(request));
			webMap.put("baseFileUrl", getBaseFileUrl(request));
			webMap.put("localIp", getLocalIp());
			webMap.put("localName", getLocalName());
		}
		System.out.println(webMap);
		return webMap;
	}

	/**
	 * 获取网站根url
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @date 2014年12月3日 下午2:51:49
	 */
	public static String getBaseUrl(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName());
		if (request.getServerPort() != 80) {
			sb.append(":").append(request.getServerPort());
		}
		sb.append(request.getContextPath());
		sb.append("/");
		String basePath = sb.toString();
		return basePath;
	}

	/**
	 * 获取网站根url(会把localhost或127.0.0.1换成真实ip)
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @date 2014年12月3日 下午2:51:49
	 */
	public static String getBaseFileUrl(HttpServletRequest request) {
		String baseUrl = getBaseUrl(request);
		String ip = getLocalIp();
		if (baseUrl.contains("localhost")) {
			baseUrl = baseUrl.replaceFirst("localhost", ip);
		} else if (baseUrl.contains("127.0.0.1")) {
			baseUrl = baseUrl.replaceFirst("127.0.0.1", ip);
		} else if (baseUrl.contains(getLocalName().toLowerCase())) {
			baseUrl = baseUrl.replaceFirst(getLocalName().toLowerCase(), ip);
		}
		return baseUrl;
	}

	/**
	 * 获取网站的根目录磁盘路径
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @date 2014年12月3日 下午3:23:48
	 */
	public static String getBaseFilePath(HttpServletRequest request) {
		String baseFilePath = request.getSession().getServletContext().getRealPath("/");
		baseFilePath = baseFilePath.replaceAll("\\\\", "/");
		if (!baseFilePath.endsWith("/")) {
			baseFilePath += "/";
		}

		return baseFilePath;
	}

	public static String getLocalIp() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip;
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	public static String getLocalName() {
		try {
			String name = InetAddress.getLocalHost().getHostName();
			return name;
		} catch (UnknownHostException e) {
			return "";
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @date 2014年12月19日 下午4:06:04
	 */
	public static String getUrl(HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
		url.append(request.getRequestURL());
		Map<String, String[]> paraMap = request.getParameterMap();
		Set<String> keySet = paraMap.keySet();
		if (keySet.size() > 0) {
			url.append("?");
			for (String key : keySet) {
				url.append(key);
				url.append("=");
				url.append(paraMap.get(key)[0]);
				url.append("&");
			}
		}
		if (url.lastIndexOf("&") == url.length() - 1) {
			url.deleteCharAt(url.length() - 1);
		}

		return url.toString();
	}
}
