package com.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ZJ_SqlKit {
	private static Map<String, String> sqlMap = null;
	static {
		String filePath = getClassPath() + "sqlGroup.xml";
		try {
			filePath=URLDecoder.decode(filePath,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			filePath = URLDecoder.decode(filePath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		sqlMap = getSqlMap(filePath);
	}

	/**
	 * 获取classpath
	 * 
	 * @return
	 */
	private static String getClassPath() {
		String classPath = "";
		classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		return classPath;
	}

	/**
	 * 读取xml
	 * 
	 * @param path
	 * @return
	 * @author 张剑
	 * @date 2014年9月5日 下午4:33:30
	 */
	private static Element getElementByXML(String path) {
		Element element = null;
		File file = new File(path);
		// documentBuilder为抽象不能直接实例化(将XML文件转换为DOM文件)
		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;

		try {
			// 得到DOM解析器的工厂实例
			// 得到javax.xml.parsers.DocumentBuilderFactory；类的实例就是我们要的解析器工厂
			dbf = DocumentBuilderFactory.newInstance();
			// 从DOM工厂获得DOM解析器
			// 通过javax.xml.parsers.DocumentBuilderFactory实例的静态方法newDocumentBuilder（）得到DOM解析器
			db = dbf.newDocumentBuilder();
			// 得到一个DOM并返回给document对象
			Document dt = db.parse(file);
			// 得到XML文档的根节点
			// 在DOM中只有根节点是一个org.w3c.dom.Element对象。
			element = dt.getDocumentElement();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return element;
	}

	/**
	 * 获取属性值
	 * 
	 * @param node
	 * @param attrName
	 * @return
	 * @author 张剑
	 * @date 2014年9月5日 下午4:33:42
	 */
	private static String getAttrValue(Node node, String attrName) {
		NamedNodeMap attributes = node.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getNodeName().equals(attrName)) {
				return attribute.getNodeValue();
			}
		}
		return "";
	}

	/**
	 * 获取sqlMap
	 * 
	 * @param filePath
	 * @return
	 * @author 张剑
	 * @date 2014年9月5日 下午4:33:57
	 */
	private static Map<String, String> getSqlMap(String filePath) {
		Map<String, String> map = new TreeMap<String, String>();
		Element element = getElementByXML(filePath);
		NodeList nodeList = element.getElementsByTagName("sql");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			String grounpId = getAttrValue(node.getParentNode(), "id");
			String sqlId = getAttrValue(node, "id");
			
			map.put(grounpId + "." + sqlId, node.getFirstChild().getNodeValue());
		}
		return map;
	}

	public static String sql(String name) {
		return sqlMap.get(name);
	}

	public static void main(String[] args) {
		System.out.println(sql("zj_zhiwei.zhiwei_list"));
	}
}
