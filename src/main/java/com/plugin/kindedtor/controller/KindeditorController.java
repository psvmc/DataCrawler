package com.plugin.kindedtor.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.render.JsonRender;
import com.plugin.kindedtor.util.NameComparator;
import com.plugin.kindedtor.util.SizeComparator;
import com.plugin.kindedtor.util.TypeComparator;
import com.utils.ZJ_FileUtils;

public class KindeditorController extends Controller {
	public void upload() {
		HashMap<String, Object> obj = new HashMap<String, Object>();

		try {
			List<String> urls = ZJ_FileUtils.upload(getRequest());
			obj.put("error", 0);
			obj.put("url", urls.get(0));
			obj.put("message", "上传成功");
		} catch (Exception e) {
			obj.put("error", 1);
			obj.put("url", "");
			obj.put("message", e.getMessage());
		}
		System.out.println(JsonKit.toJson(obj));
		render(new JsonRender(obj).forIE());

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void manager() {
		HttpServletRequest request = getRequest();
		ObjectMapper objectMapper = new ObjectMapper(null, null, null);
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		String imageRootPath = ZJ_FileUtils.getBaseFilePath(request) + "upload" + File.separator;
		String imageRootURL = ZJ_FileUtils.getBaseFileUrl(request) + "upload/";
		// 根目录路径，可以指定绝对路径，比如 /var/www/attached/
		String rootPath = imageRootPath;
		// 根目录URL，可以指定绝对路径，比如 http://www.yoursite.com/attached/

		String rootUrl = imageRootURL;
		// 图片扩展名
		String[] fileTypes = new String[] { "gif", "jpg", "jpeg", "png", "bmp" };

		String dirName = request.getParameter("dir");
		if (dirName != null) {
			if (!Arrays.<String> asList(new String[] { "image", "flash", "media", "file" }).contains(dirName)) {
				renderText("Invalid Directory name.");
				return;
			}
			rootPath += dirName + "/";
			rootUrl += dirName + "/";
			File saveDirFile = new File(rootPath);
			if (!saveDirFile.exists()) {
				saveDirFile.mkdirs();
			}
		}
		// 根据path参数，设置各路径和URL
		String path = request.getParameter("path") != null ? request.getParameter("path") : "";
		String currentPath = rootPath + path;
		String currentUrl = rootUrl + path;
		String currentDirPath = path;
		String moveupDirPath = "";
		if (!"".equals(path)) {
			String str = currentDirPath.substring(0, currentDirPath.length() - 1);
			moveupDirPath = str.lastIndexOf("/") >= 0 ? str.substring(0, str.lastIndexOf("/") + 1) : "";
		}

		// 排序形式，name or size or type
		String order = request.getParameter("order") != null ? request.getParameter("order").toLowerCase() : "name";

		// 不允许使用..移动到上一级目录
		if (path.indexOf("..") >= 0) {
			renderText("Access is not allowed.");
			return;
		}
		// 最后一个字符不是/
		if (!"".equals(path) && !path.endsWith("/")) {
			renderText("Parameter is not valid.");
			return;
		}
		// 目录不存在或不是目录
		File currentPathFile = new File(currentPath);
		if (!currentPathFile.isDirectory()) {
			renderText("Directory does not exist.");
			return;
		}

		// 遍历目录取的文件信息
		List<Hashtable> fileList = new ArrayList<Hashtable>();
		if (currentPathFile.listFiles() != null) {
			for (File file : currentPathFile.listFiles()) {
				Hashtable<String, Object> hash = new Hashtable<String, Object>();
				String fileName = file.getName();
				if (file.isDirectory()) {
					hash.put("is_dir", true);
					hash.put("has_file", (file.listFiles() != null));
					hash.put("filesize", 0L);
					hash.put("is_photo", false);
					hash.put("filetype", "");
				} else if (file.isFile()) {
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					hash.put("is_dir", false);
					hash.put("has_file", false);
					hash.put("filesize", file.length());
					hash.put("is_photo", Arrays.<String> asList(fileTypes).contains(fileExt));
					hash.put("filetype", fileExt);
				}
				hash.put("filename", fileName);
				hash.put("datetime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(file.lastModified()));
				fileList.add(hash);
			}
		}

		if ("size".equals(order)) {
			Collections.sort(fileList, new SizeComparator());
		} else if ("type".equals(order)) {
			Collections.sort(fileList, new TypeComparator());
		} else {
			Collections.sort(fileList, new NameComparator());
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("moveup_dir_path", moveupDirPath);
		result.put("current_dir_path", currentDirPath);
		result.put("current_url", currentUrl);
		result.put("total_count", fileList.size());
		result.put("file_list", fileList);
		render(new JsonRender(result).forIE());
	}

}
