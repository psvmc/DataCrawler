package com.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Jxls工具类（利用模版导出）
 * 
 * @author 张剑
 * 
 */
public class ZJ_JxlsUtils {
	/**
	 * 设置下载头，防止文件名乱码
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 */
	public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
		final String userAgent = request.getHeader("USER-AGENT");
		try {
			String finalFileName = null;
			if (StringUtils.contains(userAgent, "Firefox")) {// google,火狐浏览器
				finalFileName = new String(fileName.getBytes(), "ISO8859-1");
			} else {
				finalFileName = URLEncoder.encode(fileName, "UTF8");// 其他浏览器
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");// 这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开
		} catch (UnsupportedEncodingException e) {
		}
	}

	/**
	 * 导出
	 * 
	 * @author 张剑
	 * 
	 * @param templateFile
	 * @param beans
	 * @param os
	 */
	public static void exportExcel(String templateFile, Map<String, Object> beans, OutputStream os) {
		XLSTransformer transformer = new XLSTransformer();
		InputStream is = ZJ_JxlsUtils.class.getClassLoader().getResourceAsStream(templateFile);
		try {
			Workbook workbook = transformer.transformXLS(is, beans);
			workbook.write(os);
		} catch (Exception e) {
			throw new RuntimeException("导出excel错误!");
		}
	}

	/**
	 * 根据模版导出到response
	 * 
	 * @author 张剑
	 * 
	 * @param fileName
	 * @param request
	 * @param response
	 * @param templateFile
	 * @param beans
	 * @throws IOException
	 */
	public static void createExcel(String fileName, HttpServletRequest request, HttpServletResponse response, String templateFile, Map<String, Object> beans) throws IOException {
		setFileDownloadHeader(request, response, fileName);
		response.setContentType("application/x-download");
		response.setCharacterEncoding("UTF-8");
		OutputStream os = response.getOutputStream();
		exportExcel(templateFile, beans, os);
	}

	public static void main(String[] args) throws IOException {
		OutputStream os = new FileOutputStream("new.xls");
		String templateFile = "template/jxls_template.xls";
		Map<String, Object> beans = new HashMap<String, Object>();

		// fruits
		List<Map<String, String>> fruitList = new ArrayList<Map<String, String>>();

		Map<String, String> fruit = null;
		fruit = new HashMap<String, String>();
		fruit.put("name", "苹果");
		fruit.put("price", "100");
		fruitList.add(fruit);

		fruit = new HashMap<String, String>();
		fruit.put("name", "香蕉");
		fruit.put("price", "200");
		fruitList.add(fruit);

		beans.put("fruits", fruitList);
		exportExcel(templateFile, beans, os);
	}
}
