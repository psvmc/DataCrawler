package com.jfinalExt;

import java.util.List;

import org.apache.log4j.Logger;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;
import com.utils.ZJ_JacksonUtils;

public class ZJ_DbPro extends DbPro {
	Logger logger = Logger.getLogger(Object.class);

	public <T> T queryFirst(String tableName, String fieldName, String fieldValue) {
		String sql = String.format("select * from %s where %s=?", tableName, fieldName);
		List<T> result = query(sql, fieldValue);
		return (result.size() > 0 ? result.get(0) : null);
	}

	public Record findFirst(String tableName, String fieldName, Object fieldValue) {
		String sql = String.format("select * from %s where %s=?", tableName, fieldName);
		List<Record> result = find(sql, fieldValue);
		return result.size() > 0 ? result.get(0) : null;
	}

	/**
	 * 
	 * @author 张剑
	 * 
	 * @param tableName
	 * @param fieldList
	 * @param valueList
	 * @return
	 */
	public Record findFirst(String tableName, List<String> fieldList, List<Object> valueList) {
		List<Record> result = findAll(tableName, fieldList, valueList);
		return result.size() > 0 ? result.get(0) : null;
	}

	public List<Record> findAll(String tableName, List<String> fieldList, List<Object> valueList) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("select * from %s where 1=1 ", tableName));
		for (String field : fieldList) {
			stringBuilder.append(String.format(" and %s =? ", field));
		}
		logger.info("Sql:" + stringBuilder.toString());
		logger.info("pars:" + ZJ_JacksonUtils.toJson(valueList));
		List<Record> result = find(stringBuilder.toString(), valueList.toArray());
		return result;
	}

	public Record findFirst(String tableName, String fieldName, Object fieldValue, String sqlExt) {
		String sql = String.format("select * from %s where %s=? %s", tableName, fieldName, sqlExt);
		List<Record> result = find(sql, fieldValue);
		return result.size() > 0 ? result.get(0) : null;
	}

	public List<Record> findAll(String tableName, String fieldName, String fieldValue, String sqlExt) {
		if (null == sqlExt) {
			sqlExt = "";
		}
		String sql = String.format("select * from %s where %s=? %s ", tableName, fieldName, sqlExt);
		List<Record> result = find(sql, fieldValue);
		return result;
	}

	public List<Record> findAll(String tableName, String sqlExt) {
		if (null == sqlExt) {
			sqlExt = "";
		}
		String sql = String.format("select * from %s  %s ", tableName, sqlExt);
		List<Record> result = find(sql);
		return result;
	}
}
