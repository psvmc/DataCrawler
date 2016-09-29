package com.jfinalExt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.utils.ZJ_MultipartItem;
import com.utils.ZJ_WebUtils;

public class ZJ_File {
	public static List<UploadFile> getUploadFileListByName(List<UploadFile> upfiles, String name) {
		List<UploadFile> files = new ArrayList<UploadFile>();
		for (UploadFile uploadFile : files) {
			if (uploadFile.getParameterName().equals(name)) {
				files.add(uploadFile);
			}
		}
		return files;
	}

	public static List<String> getUrlListByName(List<UploadFile> upfiles, String name, String saveDic, HttpServletRequest request) {
		String baseUrl = ZJ_WebUtils.getBaseUrl(request);
		List<String> fileStrList = new ArrayList<String>();
		for (UploadFile uploadFile : upfiles) {
			if (uploadFile.getParameterName().equals(name)) {
				fileStrList.add(baseUrl + "upload/" + saveDic + uploadFile.getFileName());
			}
		}
		return fileStrList;
	}

	public static String getUrlsByName(List<UploadFile> upfiles, String name, String saveDic, HttpServletRequest request) {
		String baseUrl = ZJ_WebUtils.getBaseUrl(request);
		String urls = "";
		for (UploadFile uploadFile : upfiles) {
			if (uploadFile.getParameterName().equals(name)) {
				urls += baseUrl + "upload/" + saveDic + uploadFile.getFileName() + ",";
			}
		}
		if (urls.endsWith(",")) {
			urls = urls.substring(0, urls.lastIndexOf(","));
		}
		return urls;
	}
	// 获取保存路径
	public static String getSaveDirectory(String saveDirectory) {
		if (saveDirectory.startsWith("/") || saveDirectory.indexOf(":") == 1)
			return saveDirectory;
		else {
			return PathKit.getWebRootPath() + File.separator + "upload" + File.separator + saveDirectory;
		}
	}

	/**
	 * 获取上传列表
	 * 
	 * @author 张剑
	 * 
	 * @param request
	 * @param saveDirectory
	 * @param maxPostSize
	 * @param encoding
	 * @return
	 */
	public static List<UploadFile> getUploadFiles(List<ZJ_MultipartItem> upfiles) {
		List<UploadFile> uploadFiles = new ArrayList<UploadFile>();
		for (ZJ_MultipartItem multipartItem : upfiles) {
			UploadFile uploadFile = new UploadFile(multipartItem.getParName(), multipartItem.getSaveDirectory(), multipartItem.getFileName(), multipartItem.getOriginalFileName(), multipartItem.getContentType());
			uploadFiles.add(uploadFile);
		}
		return uploadFiles;
	}
}
