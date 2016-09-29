package com.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 对HttpServletRequest进行扩展
 * 
 * @功能 支持多文件上传
 * 
 * @功能 支持类型为multipart/form-data的form中普通字段的获取
 * 
 * @author 张剑
 * 
 */
public class ZJ_MultipartRequest extends HttpServletRequestWrapper {
	private String saveDirectory;
	private long maxPostSize;
	private String encoding = "UTF-8";
	private HttpServletRequest request;

	Map<String, String[]> paraMap = new HashMap<String, String[]>();

	public ZJ_MultipartRequest(HttpServletRequest request, String saveDirectory, long maxPostSize, String encoding) {
		super(request);
		if (null == encoding) {
			encoding = "UTF-8";
		}
		this.encoding = encoding;
		this.request = request;
		this.saveDirectory = saveDirectory;
		this.maxPostSize = maxPostSize;
	}

	public ZJ_MultipartRequest(HttpServletRequest request, String saveDirectory) {
		super(request);
		this.request = request;
		this.saveDirectory = saveDirectory;
	}

	// 禁止上传jsp文件
	private static Boolean isAllowUpload(FileItem item) {
		if (!item.isFormField()) {// 如果是文件类型
			String name = item.getName();// 获得文件名 包括路径啊
			if (name.endsWith(".jsp")) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean isExitFile(String filePath) {
		File f = new File(filePath);
		return f.exists();
	}

	public List<ZJ_MultipartItem> upload() throws Exception {
		List<ZJ_MultipartItem> uploadItemList = new ArrayList<ZJ_MultipartItem>();
		// 检查输入请求是否为multipart表单数据。
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setSizeThreshold(1024); // 设置缓冲区大小，这里是4kb
			factory.setRepository(new File(System.getProperty("java.io.tmpdir")));// 设置缓冲区目录
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(encoding);// 解决文件乱码问题
			upload.setSizeMax(maxPostSize);// 设置最大文件尺寸
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request);
			} catch (Exception e) {
				throw new Exception("文件太大了");
			}
			Iterator<FileItem> itr = items.iterator();// 所有的表单项
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			// 保存文件
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();// 循环获得每个表单项
				// 判断是表单字段还是file
				if (item.isFormField()) {
					String key = item.getFieldName();
					String value = item.getString();
					value = new String(value.getBytes("ISO-8859-1"), encoding);
					if (map.containsKey(key)) {
						map.get(key).add(value);
					} else {
						List<String> list = new ArrayList<String>();
						list.add(value);
						map.put(key, list);
					}
				} else {
					String fileName = item.getName();
					String originalFileName = fileName;
					if (null != fileName && fileName.equals("")) {
						continue;
					}
					if (isAllowUpload(item)) {// 如果允许上传
						String itemFieldName = item.getFieldName();
						if (fileName != null) {
							// 处理同名情况
							int tempIndex = 1;
							int index = fileName.length();
							if (fileName.contains(".")) {
								index = fileName.lastIndexOf(".");
							}
							String fileExt = fileName.substring(index).toLowerCase();
							String filePreName = fileName.substring(0, index).toLowerCase();
							while (isExitFile(saveDirectory + File.separator + fileName)) {
								fileName = filePreName + "_" + tempIndex + fileExt;
								tempIndex += 1;
							}
							fileName = ZJ_PinyinUtils.HanyuToPinyin(fileName, false);
							File savedFile = new File(saveDirectory + File.separator + fileName);

							if (!savedFile.getParentFile().exists()) {
								savedFile.getParentFile().mkdirs();
							}
							try {
								ZJ_MultipartItem fileItem = new ZJ_MultipartItem();
								item.write(savedFile);
								fileItem.setFileName(fileName);
								fileItem.setOriginalFileName(originalFileName);
								fileItem.setParName(itemFieldName);
								fileItem.setSaveDirectory(saveDirectory);
								fileItem.setContentType(item.getContentType());

								uploadItemList.add(fileItem);
							} catch (Exception e) {
								throw new Exception("文件不能写入");
							}

						}
					} else {
						throw new Exception("禁止上传该格式的文件");
					}
				}

			}
			for (String key : map.keySet()) {
				Object[] objArray = map.get(key).toArray();
				String[] strArray = new String[objArray.length];
				for (int i = 0; i < strArray.length; i++) {
					strArray[i] = objArray[i].toString();
				}
				paraMap.put(key, strArray);
			}
		} else {
			throw new Exception("表单的enctype有误");
		}
		return uploadItemList;
	}

	/**
	 * Methods to replace HttpServletRequest methods
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getParameterNames() {
		StringBuilder parameterNames = new StringBuilder();
		Set<String> set = paraMap.keySet();
		for (String string : set) {
			parameterNames.append(string + " ");
		}
		return new StringTokenizer(parameterNames.toString().trim());
	}

	public String getParameter(String name) {
		String[] values = paraMap.get(name);
		if (null != values && values.length > 0) {
			return values[0];
		} else {
			return null;
		}
	}

	public String[] getParameterValues(String name) {
		return this.paraMap.get(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getParameterMap() {
		return this.paraMap;
	}
}
