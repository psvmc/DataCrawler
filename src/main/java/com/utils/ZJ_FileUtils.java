package com.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * @文件名：ZJ_UploadUtils.java(新版)
 * @作用：不再依赖spring
 * @作者：张剑
 * @创建时间：2014-9-1
 * @更新时间：2014-12-25
 */
public class ZJ_FileUtils {
	static Logger logger = Logger.getLogger(Object.class);
	private static String tempPath = null; // 临时文件目录
	private static File tempPathFile = null;
	private static int sizeThreshold = 1024;
	private static int sizeMax = 4194304;
	private static HashMap<String, String> extMap = new HashMap<String, String>();
	static {
		extMap.put("image", "gif,jpg,jpeg,png,bmp");
		extMap.put("flash", "swf,flv");
		extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
		extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
		tempPath = getTempFilePath();
		tempPathFile = new File(tempPath);
		if (!tempPathFile.exists()) {
			tempPathFile.mkdirs();
		}
	}

	/**
	 * 文件随机命名
	 * 
	 * @return
	 * @author 张剑
	 * @date 2014年12月5日 下午4:30:42
	 */
	public static File changeFileName(File file) {
		if (null != file) {
			String fileName = file.getName();
			String str = Long.toString(System.currentTimeMillis(), 36);
			String fileExt = "";
			String newFileName = "";
			if (fileName.contains(".")) {
				fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				newFileName = str + "_" + new Random().nextInt(1000) + "." + fileExt;
			} else {
				newFileName = str + "_" + new Random().nextInt(1000);
			}

			File newFile = new File(file.getParent() + File.separator + newFileName);
			file.renameTo(newFile);
			file = newFile;
		}
		return file;
	}

	/**
	 * 修改文件名称为随机名称
	 * 
	 * @param fileName
	 * @return
	 * @author 张剑
	 * @date 2014年12月25日 下午1:12:36
	 */
	public static String changeFileStrName(String fileName) {
		String fileExt = "";
		String newFileName = "";
		String str = Long.toString(System.currentTimeMillis(), 36) + "_" + new Random().nextInt(1000);
		if (fileName.contains(".")) {
			fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			newFileName = str + "." + fileExt;
		} else {
			newFileName = str;
		}
		return newFileName;
	}

	/**
	 * 获取日期
	 * 
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午4:25:31
	 */
	private static String getDateNow() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	/**
	 * 获取相对路径
	 * 
	 * @param originalFName
	 *            原文件名
	 * @param type
	 * @return
	 */
	private static String getRelativeFile(String originalFName) {
		return getFileType(originalFName).concat(File.separator).concat(getDateNow()).concat(File.separator).concat(changeFileStrName(originalFName));
	}

	/**
	 * 关闭输入输出流
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	private static void close(InputStream inputStream, OutputStream outputStream) {
		try {
			if (null != inputStream) {
				inputStream.close();
			}
			if (null != outputStream) {
				outputStream.flush();
				outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 验证文件类型
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean isAllowUpload(String fileName, String fileType) {
		if (null == fileType) {
			fileType = "image";
		}
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (Arrays.<String> asList(extMap.get(fileType).split(",")).contains(suffix)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断上传文件类型
	 * 
	 * @param items
	 * @return
	 */
	private static Boolean isAllowUpload(FileItem item, String fileType) {
		if (!item.isFormField()) {// 如果是文件类型
			String name = item.getName();// 获得文件名 包括路径啊
			return isAllowUpload(name, fileType);
		}
		return false;
	}

	/**
	 * 根据文件名获取文件类型
	 * 
	 * @param fileName
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:03:39
	 */
	public static String getFileType(String fileName) {
		String suffix = getFileSuffix(fileName);
		if (null == suffix) {
			return "other";
		} else {
			suffix = suffix.toLowerCase();
			for (String key : extMap.keySet()) {
				if (Arrays.<String> asList(extMap.get(key).split(",")).contains(suffix)) {
					return key;
				}
			}
			return "other";
		}
	}

	/**
	 * 文件名是否为**后缀
	 * 
	 * @param fileName
	 * @param suffix
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:45:04
	 */
	public static boolean isSameSuffix(String fileName, String suffix) {
		if (null == fileName || "".equals(fileName) || null == suffix || "".equals(suffix)) {
			return false;
		} else if (fileName.trim().endsWith(suffix.trim())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取文件后缀
	 * 
	 * @param fileName
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午3:25:27
	 */
	public static String getFileSuffix(String fileName) {
		if (fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	/**
	 * 获取文件后缀
	 * 
	 * @param file
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午3:26:29
	 */
	public static String getFileSuffix(File file) {
		String fileName = file.getName();
		if (fileName.contains(".")) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	/**
	 * 获取系统临时文件目录
	 * 
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午4:10:34
	 */
	public static String getTempFilePath() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * 获取结果中的第一条
	 * 
	 * @param uploadPathList
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:20:36
	 */
	public static String getFirstPath(List<String> uploadPathList) {
		for (String string : uploadPathList) {
			return string;
		}
		return null;
	}

	/**
	 * 获取url
	 * 
	 * @param uploadURL
	 * @param relativeFilePath
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:27:03
	 */
	public static String getAllFileUrl(String uploadURL, String relativeFilePath) {
		if (uploadURL.endsWith("/")) {
			return uploadURL + relativeFilePath;
		} else {
			return uploadURL + "/" + relativeFilePath;
		}
	}

	/**
	 * 获取path
	 * 
	 * @param baseFilePath
	 * @param relativeFilePath
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:27:54
	 */
	public static String getAllFilePath(String baseFilePath, String relativeFilePath) {
		if (baseFilePath.endsWith("/")) {
			return baseFilePath + relativeFilePath;
		} else {
			return baseFilePath + File.separator + relativeFilePath;
		}
	}

	/**
	 * 设置下载头，防止文件名乱码
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 */
	public static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
		final String userAgent = request.getHeader("USER-AGENT");
		logger.info(userAgent);
		try {
			String finalFileName = null;
			if (StringUtils.contains(userAgent, "Firefox")) {// google,火狐浏览器
				finalFileName = new String(fileName.getBytes(), "ISO8859-1");
				logger.info("火狐");
			} else {
				finalFileName = URLEncoder.encode(fileName, "UTF8");// 其他浏览器
				logger.info("其他");
			}
			response.setHeader("Content-Disposition", "attachment; filename=\"" + finalFileName + "\"");// 这里设置一下让浏览器弹出下载提示框，而不是直接在浏览器中打开
		} catch (UnsupportedEncodingException e) {
		}
	}

	/**
	 * 获取编码后的文件名防止乱码
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 * @return
	 * @author 张剑
	 * @date 2014年9月1日 下午5:37:16
	 */
	public static String getFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
		final String userAgent = request.getHeader("USER-AGENT");
		try {
			String finalFileName = null;
			if (StringUtils.contains(userAgent, "Firefox")) {// google,火狐浏览器
				finalFileName = new String(fileName.getBytes(), "ISO8859-1");
			} else {
				finalFileName = URLEncoder.encode(fileName, "UTF8");// 其他浏览器
			}
			return finalFileName;
		} catch (UnsupportedEncodingException e) {
			return fileName;
		}
	}

	private static String getLocalIp() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip;
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	private static String getLocalName() {
		try {
			String name = InetAddress.getLocalHost().getHostName();
			return name;
		} catch (UnknownHostException e) {
			return "";
		}
	}

	/**
	 * 获取网站根url
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @date 2014年12月3日 下午2:51:49
	 */
	private static String getBaseUrl(HttpServletRequest request) {
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
		if (!baseFilePath.endsWith(File.separator)) {
			baseFilePath += File.separator;
		}

		return baseFilePath;
	}

	/**
	 * 文件上传
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static List<String> upload(HttpServletRequest request, String filedName, String baseFilePath, String baseFileUrl) throws Exception {
		String fileType = request.getParameter("dir");
		List<String> uploadPathList = new ArrayList<String>();
		// 检查输入请求是否为multipart表单数据。
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(sizeThreshold); // 设置缓冲区大小，这里是4kb
			factory.setRepository(tempPathFile);// 设置缓冲区目录
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("UTF-8");// 解决文件乱码问题
			upload.setSizeMax(sizeMax);// 设置最大文件尺寸
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request);
			} catch (Exception e) {
				throw new Exception("文件太大了");
			}
			Iterator<FileItem> itr = items.iterator();// 所有的表单项
			// 保存文件
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();// 循环获得每个表单项
				// 判断是表单字段还是file
				if (item.isFormField()) {
					continue;
				}
				String name = item.getName();
				if (null != name && name.equals("")) {
					throw new Exception("请选择文件");
				}
				if (isAllowUpload(item, fileType)) {// 如果允许上传
					String itemFieldName = item.getFieldName();
					if (name != null) {
						String relativeFilePath = getRelativeFile(name);
						if (null == filedName) {
							File savedFile = new File(baseFilePath + relativeFilePath);
							if (!savedFile.getParentFile().exists()) {
								savedFile.getParentFile().mkdirs();
							}
							try {
								item.write(savedFile);
							} catch (Exception e) {
								throw new Exception("文件不能写入");
							}
							uploadPathList.add(baseFileUrl + relativeFilePath.replaceAll("\\\\", "/"));
						} else if (filedName.equals(itemFieldName)) {
							File savedFile = new File(baseFilePath + relativeFilePath);
							if (!savedFile.getParentFile().exists()) {
								savedFile.getParentFile().mkdirs();
							}
							try {
								item.write(savedFile);
							} catch (Exception e) {
								throw new Exception("文件不能写入");
							}
							uploadPathList.add(baseFileUrl + relativeFilePath.replaceAll("\\\\", "/"));
						}
					}
				} else {
					throw new Exception("只允许上传" + extMap.get(fileType) + "格式的文件");
				}
			}
		} else {
			throw new Exception("表单的enctype有误");
		}
		return uploadPathList;
	}

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 * @author 张剑
	 * @throws Exception
	 * @date 2014年9月1日 下午5:10:32
	 */
	public static List<String> upload(HttpServletRequest request) throws Exception {
		return upload(request, null, getBaseFilePath(request).concat("upload").concat(File.separator), getBaseFileUrl(request).concat("upload").concat("/"));
	}

	/**
	 * 下载网站所在服务器的文件
	 * 
	 * @param path
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static HttpServletResponse download(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			// path是指欲下载的文件的路径。
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 以流的形式下载文件。
			inputStream = new FileInputStream(path);
			// 清空response
			response.reset();
			// 设置response的Header
			setFileDownloadHeader(request, response, filename);
			response.addHeader("Content-Length", "" + file.length());
			outputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			int byteLength = 0;
			byte[] buffer = new byte[1024];
			while ((byteLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteLength);
			}
		} finally {
			close(inputStream, outputStream);
		}
		return response;
	}

	/**
	 * 下载网络文件
	 * 
	 * @param fileURL
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static HttpServletResponse downloadNet(String fileURL, HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
		try {
			URL url = new URL(fileURL);
			URLConnection conn = url.openConnection();
			inputStream = conn.getInputStream();
			// 清空response
			response.reset();
			// 设置response的Header
			setFileDownloadHeader(request, response, fileName);
			outputStream = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			int byteLength = 0;
			byte[] buffer = new byte[1204];
			while ((byteLength = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteLength);
			}
		} finally {
			close(inputStream, outputStream);
		}
		return response;
	}

	/**
	 * 打开网站所在服务器的文件
	 * 
	 * @param filePath
	 * @param response
	 * @throws IOException
	 */
	public static void open(String filePath, HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				response.sendError(404, "File not found!");
				return;
			}
			inputStream = new FileInputStream(f);
			response.reset(); // 非常重要
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
			response.setHeader("Content-Disposition", "inline; filename=" + getFileName(request, response, f.getName()));
			// 文件名应该编码成UTF-8
			byte[] buf = new byte[1024];
			int byteLength = 0;
			outputStream = response.getOutputStream();
			while ((byteLength = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, byteLength);
			}
		} finally {
			close(inputStream, outputStream);
		}
	}

	/**
	 * 打开网络文件
	 * 
	 * @param fileURL
	 * @param response
	 * @throws IOException
	 */
	public static void openNet(String fileURL, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			URL url = new URL(fileURL);
			URLConnection conn = url.openConnection();
			inputStream = conn.getInputStream();
			response.reset(); // 非常重要
			response.setContentType(url.openConnection().getContentType());
			response.setHeader("Content-Disposition", "inline; filename=" + getFileName(request, response, fileName));
			byte[] buf = new byte[1024];
			int byteLength = 0;
			outputStream = response.getOutputStream();
			while ((byteLength = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, byteLength);
			}
		} finally {
			close(inputStream, outputStream);
		}
	}
}
