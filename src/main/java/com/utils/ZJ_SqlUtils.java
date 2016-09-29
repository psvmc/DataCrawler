package com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vo.PageVo;

/**
 * 
 * @作者 张剑
 * @版本 1.0
 * @日期 2014年8月22日
 * @时间 下午4:49:39
 * @更新 2014年8月22日--1.0
 */
public class ZJ_SqlUtils {
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 根据不同的类型转为相应的sql形式
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String objectChanger(Object obj) {
		if (null != obj) {
			String objType = obj.getClass().getSimpleName();
			if ("Date".equals(objType)) {
				return "'" + dateTimeFormat.format(obj) + "'";
			} else if ("Integer".equals(objType)) {
				return obj.toString();
			} else if ("Long".equals(objType)) {
				return obj.toString();
			} else if ("Double".equals(objType)) {
				return obj.toString();
			} else if ("Float".equals(objType)) {
				return obj.toString();
			} else if ("String".equals(objType)) {
				if (obj.toString().trim().startsWith("'") && obj.toString().trim().endsWith("'")) {
					return obj.toString();
				} else if (obj.toString().trim().contains(",")) {
					String[] strArray = obj.toString().trim().split(",");
					String str = "";
					for (String string : strArray) {
						str += "'" + string + "',";
					}
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
					}
					return str;
				} else {
					return "'" + obj.toString() + "'";
				}
			} else if (objType.contains("[]")) {
				Object[] objArray = (Object[]) obj;
				String str = "";
				for (Object object : objArray) {
					str += "'" + object.toString() + "',";
				}
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
				}
				return str;
			} else if (objType.contains("List")) {
				List<Object> list = (List<Object>) obj;
				String str = "";
				for (Object object : list) {
					str += "'" + object.toString() + "',";
				}
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
				}
				return str;
			} else if (objType.contains("Set")) {
				Set<Object> list = (Set<Object>) obj;
				String str = "";
				for (Object object : list) {
					str += "'" + object.toString() + "',";
				}
				if (str.endsWith(",")) {
					str = str.substring(0, str.length() - 1);
				}
				return str;
			} else {
				return "'" + obj.toString() + "'";
			}
		} else {
			return null;
		}
	}

	/**
	 * 获取sql
	 * 
	 * @param page
	 * @param sql
	 * @param like
	 * @return
	 * @author 张剑
	 * @date 2014年9月18日 下午3:23:03
	 */
	public static String getTotalSql(PageVo page, String sql, Map<String, Object> like) {
		Map<String, Object> order = new HashMap<String, Object>();
		if (null != page.getSort() && !"".equals(page.getSort()) && null != page.getOrder() && !"".equals(page.getOrder())) {
			order.put(page.getSort(), page.getOrder());
		}
		return getTotalSql(sql, null, like, order);
	}

	/**
	 * 获取sql语句
	 * 
	 * @param sql
	 * @param filter
	 * @param like
	 * @param order
	 * @return
	 */
	public static String getTotalSql(String sql, Map<String, Object> filter, Map<String, Object> like, Map<String, Object> order) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(" where 1=1 ");
		if (null != filter && filter.keySet().size() > 0) {
			Set<String> keys = filter.keySet();
			for (String key : keys) {
				if (null != filter.get(key)) {
					stringBuilder.append(String.format(" and %s=%s ", key, objectChanger(filter.get(key))));
				}
			}
		}
		if (null != like && like.keySet().size() > 0) {
			Set<String> keys = like.keySet();
			for (String key : keys) {
				if (null != like.get(key)) {
					stringBuilder.append(String.format(" and %s like '%%%s%%' ", key, like.get(key)));
				}
			}
		}

		if (null != order && order.keySet().size() > 0) {
			Set<String> keys = order.keySet();

			stringBuilder.append(" order by ");
			for (String key : keys) {
				if (!"".equals(key) && null != order.get(key)) {
					stringBuilder.append(String.format(" %S %S,", key, order.get(key)));
				}
			}
		}
		sql += stringBuilder.toString();
		if (sql.trim().endsWith(",")) {
			sql = sql.trim().substring(0, sql.trim().length() - 1);
		}
		return sql;
	}

	/**
	 * 获取sql中select部分
	 * 
	 * @param sql
	 * @return
	 */
	public static String getSqlSelect(String sql) {
		if (null == sql) {
			return null;
		} else {
			if (sql.toLowerCase().contains("from")) {
				return sql.substring(0, sql.toLowerCase().indexOf("from"));
			} else {
				return sql;
			}
		}
	}

	/**
	 * 获取sql中select之外部分
	 * 
	 * @param sql
	 * @return
	 */
	public static String getSqlExceptSelect(String sql) {
		if (null == sql) {
			return null;
		} else {
			if (sql.toLowerCase().contains("from")) {
				return sql.substring(sql.toLowerCase().indexOf("from"));
			} else {
				return sql;
			}
		}
	}

	/**
	 * 把类似 1,2,3转为 '1','2','3'
	 * 
	 * @param str
	 * @return
	 */
	public static String getInStr(String str) {
		if (null == str || "".equals(str.trim()) || str.startsWith("'")) {
			return str;
		} else {
			if (str.startsWith(",")) {
				str = str.substring(1);
			}
			if (str.endsWith(",")) {
				str = str.substring(0, str.length() - 1);
			}
			String str2 = "";
			String[] temp = str.split(",");
			for (String string : temp) {
				str2 += ",'" + string + "'";
			}
			if (str2.startsWith(",")) {
				str2 = str2.substring(1, str2.length());
			}
			return str2;
		}
	}

	public static void main(String[] args) {
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("name", "张剑");
		filter.put("pwd", "123");
		Map<String, Object> like = new HashMap<String, Object>();
		like.put("bookname", "haha");
		like.put("date", new Date());
		Map<String, Object> order = new HashMap<String, Object>();
		order.put("name", "asc");
		order.put("pwd", "desc");
		System.out.println(getTotalSql("select * from tb_user", filter, like, order));
	}
}
