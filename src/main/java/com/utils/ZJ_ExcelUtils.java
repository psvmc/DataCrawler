package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

/**
 * Excel工具类
 * 
 * @作者 张剑
 * @版本 1.5
 * @日期 2014年8月22日
 * @时间 上午10:39:10
 * @更新 张剑--2014年8月22日--1.1--添加读取Excel为String[][]
 * @更新 张剑--2014年8月22日--1.2--关闭输入输出流 抛出异常
 * @更新 张剑--2014年8月25日--1.3--修改读取代码
 * @更新 张剑--2015年4月21日--1.4--添加行列导入
 * @更新 张剑--2015年5月26日--1.5--兼容excel2007
 */
public class ZJ_ExcelUtils {
	public static Integer getInteger(String str) {
		try {
			if (null == str || "".equals(str)) {
				return null;
			} else {

				return Double.valueOf(str).intValue();
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static Double getDouble(String str) {
		try {
			if (null == str || "".equals(str)) {
				return null;
			} else {
				return Double.valueOf(str);
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 创建Workbook
	 * 
	 * @author 张剑
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	private static Workbook getWorkbook(InputStream in) throws IOException, InvalidFormatException {
		if (!in.markSupported()) {
			in = new PushbackInputStream(in, 8);
		}
		try {
			if (POIFSFileSystem.hasPOIFSHeader(in)) {
				return new HSSFWorkbook(in);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("你的excel版本目前poi解析不了");
		}

		try {
			return new XSSFWorkbook(OPCPackage.open(in));
		} catch (Exception e) {
			throw new IllegalArgumentException("你的excel版本目前poi解析不了");

		}

	}

	/**
	 * 设置下载头，防止文件名乱码
	 * 
	 * @param request
	 * @param response
	 * @param fileName
	 */
	private static void setFileDownloadHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
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
	 * *
	 * 
	 * @param filename
	 *            保存到客户端的文件名 例：用户.xls
	 * @param title
	 *            标题行 例：String[]{"名称","地址"}
	 * @param key
	 *            从查询结果List取得的MAP的KEY顺序， 需要和title顺序匹配，
	 *            例：String[]{"name","address"}
	 * @param values
	 *            List<Map<String, String>> values=new
	 *            ArrayList<Map<String,String>>(); Map<String, String> m=new
	 *            HashMap<String, String>(); m.put("name", "zhangjian");
	 *            m.put("address", "郑州"); values.add(m);
	 * @param httpServletResponse
	 * @throws IOException
	 */
	public static void createExcel(String fileName, String[] title, String[] key, List<Map<String, String>> values, HttpServletRequest request, HttpServletResponse response) throws IOException {
		setFileDownloadHeader(request, response, fileName);
		HSSFWorkbook workbook = null;
		response.setContentType("application/x-download");
		response.setCharacterEncoding("UTF-8");
		workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		row = sheet.createRow(0);
		for (int i = 0; title != null && i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(new HSSFRichTextString(title[i]));
		}
		Map<String, String> map = null;
		for (int i = 0; values != null && i < values.size(); i++) {
			row = sheet.createRow(i + 1);
			map = values.get(i);
			for (int i2 = 0; i2 < key.length; i2++) {
				cell = row.createCell(i2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (map.get(key[i2]) == null) {
					cell.setCellValue(new HSSFRichTextString(""));
				} else {
					cell.setCellValue(new HSSFRichTextString(map.get(key[i2]).toString()));
				}
			}
		}
		ServletOutputStream servletOutputStream = null;
		try {
			servletOutputStream = response.getOutputStream();
			workbook.write(servletOutputStream);
		} finally {
			if (null != servletOutputStream) {
				servletOutputStream.flush();
				servletOutputStream.close();
			}
		}
	}

	/**
	 * 导出到文件
	 * 
	 * @param outputStream
	 * @param title
	 *            String[]
	 * @param key
	 *            String[]
	 * @param values
	 *            List<Map<String, String>>
	 * @throws IOException
	 */
	public static void createExcel(OutputStream outputStream, String[] title, String[] key, List<Map<String, String>> values) throws IOException {
		HSSFWorkbook workbook = null;
		workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		row = sheet.createRow(0);
		for (int i = 0; title != null && i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(new HSSFRichTextString(title[i]));
		}
		Map<String, String> map = null;
		for (int i = 0; values != null && i < values.size(); i++) {
			row = sheet.createRow(i + 1);
			map = values.get(i);
			for (int i2 = 0; i2 < key.length; i2++) {
				cell = row.createCell(i2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (map.get(key[i2]) == null) {
					cell.setCellValue(new HSSFRichTextString(""));
				} else {
					cell.setCellValue(new HSSFRichTextString(map.get(key[i2]).toString()));
				}
			}
		}
		ServletOutputStream servletOutputStream = null;
		try {
			workbook.write(outputStream);
		} finally {
			if (null != servletOutputStream) {
				servletOutputStream.flush();
				servletOutputStream.close();
			}
		}
	}

	/**
	 * 导出到文件
	 * 
	 * @param outputStream
	 * @param values
	 * @throws IOException
	 */
	public static void createExcel(OutputStream outputStream, List<Map<String, String>> values) throws IOException {
		HSSFWorkbook workbook = null;
		workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		row = sheet.createRow(0);
		Object[] title = null;
		// 添加标题
		if (values.size() > 0) {
			title = values.get(0).keySet().toArray();
			for (int i = 0; title != null && i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(title[i].toString());
			}
		}

		Map<String, String> map = null;
		for (int i = 0; values != null && i < values.size(); i++) {
			row = sheet.createRow(i + 1);
			map = values.get(i);
			for (int i2 = 0; i2 < title.length; i2++) {
				cell = row.createCell(i2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (map.get(title[i2]) == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(map.get(title[i2]).toString());
				}
			}
		}
		ServletOutputStream servletOutputStream = null;
		try {
			workbook.write(outputStream);
		} finally {
			if (null != servletOutputStream) {
				servletOutputStream.flush();
				servletOutputStream.close();
			}
		}
	}

	/**
	 * 导出到文件
	 * 
	 * @param outputStream
	 * @param title
	 *            String[]
	 * @param values
	 *            List<String[]>
	 * @throws IOException
	 */
	public static void createExcel(OutputStream outputStream, String[] title, List<String[]> values) throws IOException {
		HSSFWorkbook workbook = null;
		workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row = null;
		Cell cell = null;
		row = sheet.createRow(0);
		for (int i = 0; title != null && i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(new HSSFRichTextString(title[i]));
		}
		String[] strArray = null;
		for (int i = 0; values != null && i < values.size(); i++) {
			row = sheet.createRow(i + 1);
			strArray = values.get(i);
			for (int i2 = 0; i2 < strArray.length; i2++) {
				cell = row.createCell(i2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if (strArray[i2] == null) {
					cell.setCellValue(new HSSFRichTextString(""));
				} else {
					cell.setCellValue(strArray[i2].toString());
				}
			}
		}
		ServletOutputStream servletOutputStream = null;
		try {
			workbook.write(outputStream);
		} finally {
			if (null != servletOutputStream) {
				servletOutputStream.flush();
				servletOutputStream.close();
			}
		}
	}

	/**
	 * 读取excel（第一行必须为标题）
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 */
	public static List<Map<String, String>> getMapListByExcelFilePath(String filePath) throws FileNotFoundException, IOException, InvalidFormatException {
		File file = new File(filePath);
		List<Map<String, String>> list = getMapListByExcelFile(file);
		return list;
	}

	/**
	 * 读取excel（第一行必须为标题）
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 */
	public static List<Map<String, String>> getMapListByExcelFile(File file) throws FileNotFoundException, IOException, InvalidFormatException {
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();
		FileInputStream fileInputStream = null;
		try {
			// 创建对Excel工作簿文件的引用
			fileInputStream = new FileInputStream(file);
			Workbook wookbook = getWorkbook(fileInputStream);
			// 在Excel文档中，第一张工作表的缺省索引是0
			Sheet sheet = wookbook.getSheetAt(0);

			// 获取到Excel文件中的所有行数
			int rows = sheet.getPhysicalNumberOfRows();

			int columns = 0;
			Row titleRow = null;
			if (rows > 0) {
				titleRow = sheet.getRow(0);
				columns = titleRow.getPhysicalNumberOfCells();
			}
			// 遍历行
			for (int i = 1; i < rows; i++) {
				Map<String, String> map = new HashMap<String, String>();
				Row row = sheet.getRow(i);
				if (row != null) {

					// 遍历列
					for (int j = 0; j < columns; j++) {
						// 获取到列的值
						Cell cell = row.getCell(j);
						if (cell != null) {
							map.put(titleRow.getCell(j).getStringCellValue(), getCellValue(cell));
						}
					}
					list.add(map);
				}
			}
			return list;
		} finally {
			if (null != fileInputStream) {
				fileInputStream.close();
			}
		}
	}

	/**
	 * 读取Excel转为二维数组
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 */
	public static String[][] getStrArrayByExcelFile(File file) throws FileNotFoundException, IOException, InvalidFormatException {
		String[][] strArray = null;
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(file);
		try {
			// 创建对Excel工作簿文件的引用
			Workbook wookbook = getWorkbook(fileInputStream);
			// 在Excel文档中，第一张工作表的缺省索引是0
			Sheet sheet = wookbook.getSheetAt(0);
			// 获取到Excel文件中的所有行数
			int rows = sheet.getPhysicalNumberOfRows();
			int cellNum = 0;
			if (rows > 0) {
				cellNum = sheet.getRow(0).getPhysicalNumberOfCells();
				strArray = new String[rows][cellNum];
			}
			// 遍历行
			for (int i = 0; i < rows; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					// 遍历列
					for (int j = 0; j < cellNum; j++) {
						// 获取到列的值
						Cell cell = row.getCell(j);
						if (cell != null) {
							strArray[i][j] = getCellValue(cell);
						}
					}
				}
			}
			return strArray;
		} finally {
			if (null != fileInputStream) {
				fileInputStream.close();
			}
		}
	}

	/**
	 * 获取Cell的值
	 * 
	 * @author 张剑
	 * 
	 * @param cell
	 * @return
	 */
	private static String getCellValue(Cell cell) {
		String value = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				double vD = cell.getNumericCellValue();
				int vI = (int) cell.getNumericCellValue();
				if (vD == vI) {
					value = BigDecimal.valueOf(vI).toString() + "";
				} else {
					value = BigDecimal.valueOf(vD).toString() + "";
				}
				break;
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				double valueD = cell.getNumericCellValue();
				int valueI = (int) cell.getNumericCellValue();
				if (valueD == valueI) {
					value = BigDecimal.valueOf(valueI).toString() + "";
				} else {
					value = BigDecimal.valueOf(valueD).toString() + "";
				}
				break;
			case Cell.CELL_TYPE_BLANK:
				value = "";
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue() + "";
				break;
			default:
				value = "";
				break;
			}
		}
		return value;
	}

	// 获取某行某列Cell的值
	private static String getCellValue(Sheet sheet, int rowIndex, int columnIndex) {
		Row row = sheet.getRow(rowIndex);
		if (null != row) {
			Cell cell = row.getCell(columnIndex);
			if (null != cell) {
				return getCellValue(cell);
			} else {
				return "";
			}
		} else {
			return "";
		}

	}

	// 获取包含某个名字的Sheet
	private static Sheet getSheetByNameLike(Workbook workbook, String sheetName) {
		boolean b = true;
		int index = 0;

		while (b) {
			Sheet sheet = workbook.getSheetAt(index);
			if (null != sheet) {
				String sheetNameAll = sheet.getSheetName();
				if (sheetNameAll.contains(sheetName)) {
					return sheet;
				} else {
					index++;
				}
			} else {
				b = false;
			}
		}
		return null;
	}

	// 获取行数和列数
	private static Map<String, Integer> getRowAndColumnByStr(String str) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		str = str.toUpperCase();
		String[] strArr = str.split("-");
		Integer rowIndex = 0;
		Integer columnIndex = 0;
		rowIndex = Integer.valueOf(strArr[0]) - 1;
		if (strArr[1].length() == 1) {
			columnIndex = (int) strArr[1].charAt(0) - 65;
		} else if (strArr[1].length() == 2) {
			columnIndex = ((int) strArr[1].charAt(0) - 65 + 1) * 26 + ((int) strArr[1].charAt(1) - 65);
		}
		map.put("row", rowIndex);
		map.put("column", columnIndex);
		return map;
	}

	/**
	 * 判断几个cell的值是否符合
	 * 
	 * @author 张剑
	 * 
	 * @param sheet
	 * @param map
	 * @return
	 */
	private static boolean isFuhe(Sheet sheet, Map<String, String> map) {
		boolean b = true;
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			String value = map.get(key);
			Map<String, Integer> rowAndColumn = getRowAndColumnByStr(key);
			int row = rowAndColumn.get("row");
			int column = rowAndColumn.get("column");
			String cellValue = getCellValue(sheet, row, column);
			if (!cellValue.contains(value)) {
				return false;
			}
		}
		return b;

	}

	// 获取包含某些点的Sheet
	// map的值 例如{"13-A":"哈哈","12-B":"ff"}
	private static Sheet getSheetByCells(Workbook workbook, Map<String, String> map) {
		boolean b = true;
		int index = 0;

		while (b) {
			Sheet sheet = null;
			try {
				sheet = workbook.getSheetAt(index);
				if (null != sheet) {
					if (isFuhe(sheet, map)) {
						return sheet;
					} else {
						index++;
					}
				} else {
					b = false;
				}
			} catch (Exception e) {
				return null;
			}

		}
		return null;
	}

	/**
	 * 读取Excel转为list
	 * 
	 * @author 张剑
	 * 
	 * @param file
	 * @param rowBegin
	 *            从1开始
	 * @param rowEnd
	 *            包含结束行（设置为0 取总行数）
	 * @param columnBegin
	 *            从1开始
	 * @param columnEnd
	 *            包含结束列（设置为0 取总列数）
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> getStrListByExcelFile(File file, int rowBegin, int rowEnd, int columnBegin, int columnEnd) throws Exception {
		List<List<String>> sheetList = new ArrayList<List<String>>();
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(file);
		try {
			// 创建对Excel工作簿文件的引用
			Workbook wookbook = getWorkbook(fileInputStream);
			// 在Excel文档中，第一张工作表的缺省索引是0

			Sheet sheet = null;
			sheet = wookbook.getSheetAt(0);
			sheetList = getStrListBySheet(sheet, rowBegin, rowEnd, columnBegin, columnEnd);

			return sheetList;
		} finally {
			if (null != fileInputStream) {
				fileInputStream.close();
			}
		}
	}

	/**
	 * 
	 * @author 张剑
	 * 
	 * @param file
	 * @param sheetName
	 * @param rowBegin
	 * @param rowEnd
	 * @param columnBegin
	 * @param columnEnd
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> getStrListByExcelFile(File file, String sheetName, int rowBegin, int rowEnd, int columnBegin, int columnEnd) throws Exception {
		List<List<String>> sheetList = new ArrayList<List<String>>();
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(file);
		try {
			// 创建对Excel工作簿文件的引用
			Workbook wookbook = getWorkbook(fileInputStream);
			// 在Excel文档中，第一张工作表的缺省索引是0

			Sheet sheet = null;

			if (null == sheetName) {
				sheet = wookbook.getSheetAt(0);
			} else {
				sheet = getSheetByNameLike(wookbook, sheetName);

			}
			sheetList = getStrListBySheet(sheet, rowBegin, rowEnd, columnBegin, columnEnd);

			return sheetList;
		} finally {
			if (null != fileInputStream) {
				fileInputStream.close();
			}
		}
	}

	/**
	 * 读取excel生成List对象
	 * 
	 * @author 张剑
	 * 
	 * @param file
	 * @param map
	 *            校验的map
	 *            {@code  
	 *   			Map<String, String> map = new HashMap<String,String>();
	 *   			map.put("1-B", "组织机构代码"); 
	 *   			map.put("1-C", "单位详细名称");
	 *   			map.put("1-D", "地址");
	 *				}
	 * @param rowBegin
	 *            开始行 从1开始
	 * @param rowEnd
	 *            结束行 开始行 从1开始 (0:获取总行数)
	 * @param columnBegin
	 *            开始列 开始行 从1开始
	 * @param columnEnd
	 *            结束列 开始行 从1开始
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> getStrListByExcelFile(File file, Map<String, String> map, int rowBegin, int rowEnd, int columnBegin, int columnEnd) throws Exception {
		List<List<String>> sheetList = new ArrayList<List<String>>();
		FileInputStream fileInputStream = null;
		fileInputStream = new FileInputStream(file);
		try {
			// 创建对Excel工作簿文件的引用
			Workbook wookbook = getWorkbook(fileInputStream);
			// 在Excel文档中，第一张工作表的缺省索引是0
			if (null != wookbook) {
				Sheet sheet = null;
				sheet = getSheetByCells(wookbook, map);
				if (null != sheet) {
					sheetList = getStrListBySheet(sheet, rowBegin, rowEnd, columnBegin, columnEnd);
				}
			}
			return sheetList;
		} finally {
			if (null != fileInputStream) {
				fileInputStream.close();
			}
		}
	}

	private static List<List<String>> getStrListBySheet(Sheet sheet, int rowBegin, int rowEnd, int columnBegin, int columnEnd) throws Exception {
		List<List<String>> sheetList = new ArrayList<List<String>>();
		if (null == sheet) {
			throw new Exception("Excel格式有误");
		}
		if (rowBegin == 0) {
			rowBegin = 1;
		}
		if (columnBegin == 0) {
			columnBegin = 1;
		}
		if (rowEnd == 0) {
			rowEnd = sheet.getPhysicalNumberOfRows();
		}
		if (rowEnd > 0 && columnEnd == 0) {
			columnEnd = sheet.getRow(0).getPhysicalNumberOfCells();
		}
		// 遍历行
		for (int i = rowBegin; i <= rowEnd; i++) {
			Row row = sheet.getRow(i - 1);

			if (row != null) {
				List<String> rowList = new ArrayList<String>();
				// 遍历列
				for (int j = columnBegin; j <= columnEnd; j++) {
					// 获取到列的值
					Cell cell = row.getCell(j - 1);
					if (cell != null) {
						rowList.add(getCellValue(cell));
					} else {
						rowList.add("");
					}
				}
				sheetList.add(rowList);
			}
		}
		return sheetList;
	}

	/**
	 * 读取Excel转为二维数组
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 */
	public static String[][] getStrArrayByExcelFilePath(String filePath) throws FileNotFoundException, IOException, InvalidFormatException {
		File file = new File(filePath);
		String[][] strArray = getStrArrayByExcelFile(file);
		return strArray;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, InvalidFormatException {
		// 演示1
		String[] title = { "姓名", "密码" };
		List<String[]> values = new ArrayList<String[]>();
		values.add(new String[] { "zhangjian", "123456" });
		values.add(new String[] { "lisi", "654321" });
		createExcel(new FileOutputStream("D:/测试1.xls"), title, values);
		// ------------------------------------------------------------------
		// 演示2
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("账号", "zhangjian");
		m1.put("密码", "123456");
		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("账号", "lisi");
		m2.put("密码", "654321");
		list.add(m1);
		list.add(m2);
		createExcel(new FileOutputStream("D:/测试2.xls"), list);
		// ------------------------------------------------------------------
		// 演示3
		String[] title3 = { "账号", "密码" };
		String[] key3 = { "code", "pwd" };
		List<Map<String, String>> values3 = new ArrayList<Map<String, String>>();
		Map<String, String> v1 = new HashMap<String, String>();
		v1.put("code", "zhangjian");
		v1.put("pwd", "123456");
		Map<String, String> v2 = new HashMap<String, String>();
		v2.put("code", "lisi");
		v2.put("pwd", "654321");
		values3.add(v1);
		values3.add(v2);
		createExcel(new FileOutputStream("D:/测试3.xls"), title3, key3, values3);

		// 读取测试1
		List<Map<String, String>> list4 = getMapListByExcelFilePath("D:/1.xls");
		System.out.println(list4.get(0));

		// 读取测试2
		String[][] strArray = getStrArrayByExcelFilePath("D:/1.xls");
		for (String[] strings : strArray) {
			for (String string : strings) {
				System.out.print(String.format("%5s", string) + "\t");
			}
			System.out.println("");
		}
	}

	@Test
	public void test1() {
		String str = "13-Z";
		str = str.toUpperCase();
		String[] strArr = str.split("-");
		Integer rowIndex = 0;
		Integer columnIndex = 0;
		rowIndex = Integer.valueOf(strArr[0]) - 1;
		if (strArr[1].length() == 1) {
			columnIndex = (int) strArr[1].charAt(0) - 65;
		} else if (strArr[1].length() == 2) {
			columnIndex = ((int) strArr[1].charAt(0) - 65 + 1) * 26 + ((int) strArr[1].charAt(1) - 65);
		}
		System.out.println(rowIndex);
		System.out.println(columnIndex);

	}
}