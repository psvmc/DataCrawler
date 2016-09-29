package com.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 文件编码获取与转换
 * 
 * @作者 张剑
 * @版本 1.0
 * @日期 2014年8月18日
 * @时间 上午11:49:17
 */
public class ZJ_TxtUtils {
	private static final Logger logger = Logger.getLogger(Object.class);

	/**
	 * 获取classpath
	 * 
	 * @return
	 */
	public static String getClassPath() {
		String classPath = "";
		classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		return classPath;
	}

	/**
	 * 读取文件内容（以指定编码读）
	 * 
	 * @param filePath
	 * @param charset
	 */
	public static void readTxtFile(String filePath, String charset) {
		if (null == charset || "".equals(charset)) {
			charset = "UTF-8";
		}
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					System.out.println(lineTxt);
				}
				read.close();
			} else {
				logger.info("找不到文件：" + filePath);
			}
		} catch (Exception e) {
			logger.info("读取文件内容出错");
			e.printStackTrace();
		}

	}

	/**
	 * 读取文件内容（智能判断文件编码）
	 * 
	 * @param filePath
	 * @param charset
	 */
	public static void readTxtFile(String filePath) {
		String charset = getFilecharset(new File(filePath));
		logger.info("文件的编码为：" + charset);
		readTxtFile(filePath, charset);
	}

	/**
	 * 获取文件编码
	 * 
	 * @param sourceFile
	 * @return
	 */
	private static String getFilecharset(File sourceFile) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(sourceFile));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8"; // 文件编码为 UTF-8
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
		} catch (IOException e) {
			logger.info("找不到文件：" + sourceFile);
			return null;
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (IOException e) {

				}
			}
		}
		return charset;
	}

	/**
	 * 读取配置文件
	 * 
	 * @param fileName
	 * @param charset
	 * @return
	 */
	public static Set<String> getConfigSet(String fileName, String charset) {
		Set<String> set = new HashSet<String>();
		if (null == charset || "".equals(charset)) {
			charset = "UTF-8";
		}
		String filePath = getClassPath() + fileName;
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!"".equals(lineTxt)) {
						set.add(lineTxt);
					}
				}
				read.close();
			} else {
				logger.info("找不到文件：" + filePath);
			}
		} catch (Exception e) {
			logger.info("读取文件内容出错");
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * 读取配置文件
	 * 
	 * @param fileName
	 * @param charset
	 * @return
	 */
	public static List<String> getConfigList(String fileName, String charset) {
		List<String> list = new LinkedList<String>();
		if (null == charset || "".equals(charset)) {
			charset = "UTF-8";
		}
		String filePath = getClassPath() + fileName;
		try {
			filePath = URLDecoder.decode(filePath, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), charset);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!"".equals(lineTxt)) {
						list.add(lineTxt);
					}
				}
				read.close();
			} else {
				logger.info("找不到文件：" + filePath);
			}
		} catch (Exception e) {
			logger.info("读取文件内容出错");
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		try {
			readTxtFile("D://1.txt");
			System.out.println(new String("我是好人".getBytes(), "UTF-8"));
			System.out.println(getClassPath());
			System.out.println(getConfigSet("excludeUrls.txt", "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
