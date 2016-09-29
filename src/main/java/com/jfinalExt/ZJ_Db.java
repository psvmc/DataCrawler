package com.jfinalExt;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;
import com.utils.ZJ_StringUtils;

public class ZJ_Db {
	private static ZJ_DbPro pro = new ZJ_DbPro();

	/**
	 * 防注入
	 * 
	 * @param par
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:04:22
	 */
	public static String filterPar(String par) {
		if (null == par) {
			return "";
		} else {
			return par.replaceAll("'", "\"");
		}
	}

	/**
	 * 
	 * @param tableName
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:30
	 */
	public static String getSql(String tableName, String sqlExt) {
		String sql = String.format("select * from %s %s", tableName, sqlExt);
		return sql;
	}

	/**
	 * 根据id获取所有子的id set(不限层)
	 * 
	 * @author 张剑
	 * 
	 * @param modelClass
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Set<String> getSonIdSet(Class<? extends Model> modelClass, String id) {
		Set<String> idSet = new HashSet<String>();
		idSet.add(id);
		boolean isHasMore = true;
		while (isHasMore) {
			int tempSize = idSet.size();
			List<Record> records = ZJ_Db.findAll(modelClass, String.format(" where pid in(%s) ", ZJ_StringUtils.getInStr(idSet)));
			for (Record record : records) {
				idSet.add(record.getStr("id"));
			}
			if (idSet.size() == tempSize) {
				isHasMore = false;
			}
		}
		return idSet;
	}

	/**
	 * 
	 * @param modelClass
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:27
	 */
	@SuppressWarnings("rawtypes")
	public static String getSql(Class<? extends Model> modelClass, String sqlExt) {
		String sql = String.format("select * from %s %s", getTableName(modelClass), sqlExt);
		return sql;
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:24
	 */
	public static String getSql(String tableName, String fieldName, String fieldValue, String sqlExt) {
		fieldValue = filterPar(fieldValue);
		String sql = String.format("select * from %s where %s='%s' %s", tableName, fieldName, fieldValue, sqlExt);
		return sql;
	}

	/**
	 * 
	 * @param modelClass
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:21
	 */
	@SuppressWarnings("rawtypes")
	public static String getSql(Class<? extends Model> modelClass, String fieldName, String fieldValue, String sqlExt) {
		fieldValue = filterPar(fieldValue);
		String sql = String.format("select * from %s where %s='%s' %s", getTableName(modelClass), fieldName, fieldValue, sqlExt);
		return sql;
	}

	/**
	 * 获取sql中select部分
	 * 
	 * @param sql
	 * @return
	 */
	private static String getSqlSelect(String sql) {
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
	private static String getSqlExceptSelect(String sql) {
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
	 * 查询第一条
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @author 张剑
	 * @date 2014年9月3日 上午11:23:52
	 */
	public static Record findFirst(String tableName, String fieldName, String fieldValue) {
		return pro.findFirst(tableName, fieldName, fieldValue);
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:16
	 */
	public static Record findFirst(String tableName, String fieldName, Object fieldValue, String sqlExt) {
		return pro.findFirst(tableName, fieldName, fieldValue, sqlExt);
	}

	/**
	 * 
	 * @param modelClass
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @author 张剑
	 * @date 2014年9月11日 下午6:05:34
	 */
	@SuppressWarnings("rawtypes")
	public static Record findFirst(Class<? extends Model> modelClass, String fieldName, Object fieldValue) {
		return pro.findFirst(getTableName(modelClass), fieldName, fieldValue);
	}

	@SuppressWarnings("rawtypes")
	public static Record findFirst(Class<? extends Model> modelClass, List<String> fieldList, List<Object> valueList) {
		return pro.findFirst(getTableName(modelClass), fieldList, valueList);
	}

	@SuppressWarnings("rawtypes")
	public static List<Record> findAll(Class<? extends Model> modelClass, List<String> fieldList, List<Object> valueList) {
		return pro.findAll(getTableName(modelClass), fieldList, valueList);
	}

	/**
	 * 
	 * @param modelClass
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:12:12
	 */
	@SuppressWarnings("rawtypes")
	public static Record findFirst(Class<? extends Model> modelClass, String fieldName, String fieldValue, String sqlExt) {
		return pro.findFirst(getTableName(modelClass), fieldName, fieldValue, sqlExt);
	}

	/**
	 * 全查询
	 * 
	 * @param tableName
	 * @return
	 * @author 张剑
	 * @date 2014年12月17日 下午2:41:06
	 */
	public static List<Record> findAll(String tableName) {
		String sql = String.format("select * from %s ", tableName);
		return Db.find(sql);
	}

	/**
	 * 全查询
	 * 
	 * @param modelClass
	 * @return
	 * @author 张剑
	 * @date 2014年12月17日 下午2:41:34
	 */
	@SuppressWarnings("rawtypes")
	public static List<Record> findAll(Class<? extends Model> modelClass) {
		return findAll(getTableName(modelClass));
	}

	/**
	 * 全查询
	 * 
	 * @param tableName
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @author 张剑
	 * @date 2014年12月17日 下午3:02:59
	 */
	@SuppressWarnings("unused")
	private static List<Record> findAll(String tableName, String fieldName, String fieldValue, String sqlExt) {
		return pro.findAll(tableName, fieldName, fieldValue, sqlExt);
	}

	/**
	 * 
	 * @param tableName
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:11:59
	 */
	public static List<Record> findAll(String tableName, String sqlExt) {
		return pro.findAll(tableName, sqlExt);
	}

	/**
	 * 全查询
	 * 
	 * @param modelClass
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 * @author 张剑
	 * @date 2014年12月17日 下午3:03:21
	 */
	@SuppressWarnings("rawtypes")
	public static List<Record> findAll(Class<? extends Model> modelClass, String fieldName, String fieldValue, String sqlExt) {
		return pro.findAll(getTableName(modelClass), fieldName, fieldValue, sqlExt);
	}

	/**
	 * 
	 * @param modelClass
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:11:55
	 */
	@SuppressWarnings("rawtypes")
	public static List<Record> findAll(Class<? extends Model> modelClass, String sqlExt) {
		return pro.findAll(getTableName(modelClass), sqlExt);
	}

	/**
	 * 
	 * @param modelClass
	 * @param sqlExt
	 * @param paras
	 * @return
	 * @author 张剑
	 * @date 2015年2月3日 上午9:38:54
	 */
	@SuppressWarnings("rawtypes")
	public static List<Record> findAll(Class<? extends Model> modelClass, String sqlExt, Object... paras) {
		String sql = getSql(modelClass, sqlExt);
		return Db.find(sql, paras);
	}

	/**
	 * 
	 * @param sql
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:11:47
	 */
	public static List<Record> findAllBySql(String sql, String fieldName, String fieldValue, String sqlExt) {
		sql += String.format(" where %s=? %s", fieldName, sqlExt);
		return pro.find(sql, fieldValue);
	}

	/**
	 * 分页查询
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sql
	 * @return
	 * @author 张剑
	 * @date 2014年9月3日 上午11:23:13
	 */
	public static Page<Record> paginate(int pageNumber, int pageSize, String sql) {
		return Db.paginate(pageNumber, pageSize, getSqlSelect(sql), getSqlExceptSelect(sql));
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param modelClass
	 * @param fieldName
	 * @param fieldValue
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:11:37
	 */
	@SuppressWarnings("rawtypes")
	public static Page<Record> paginate(int pageNumber, int pageSize, Class<? extends Model> modelClass, String fieldName, String fieldValue, String sqlExt) {
		String sql = getSql(modelClass, fieldName, fieldValue, sqlExt);
		return Db.paginate(pageNumber, pageSize, getSqlSelect(sql), getSqlExceptSelect(sql));
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param modelClass
	 * @param sqlExt
	 * @return
	 * @author 张剑
	 * @date 2015年1月23日 下午2:11:32
	 */
	@SuppressWarnings("rawtypes")
	public static Page<Record> paginate(int pageNumber, int pageSize, Class<? extends Model> modelClass, String sqlExt) {
		String sql = getSql(modelClass, sqlExt);
		return Db.paginate(pageNumber, pageSize, getSqlSelect(sql), getSqlExceptSelect(sql));
	}

	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param modelClass
	 * @param sqlExt
	 * @param paras
	 * @return
	 * @author 张剑
	 * @date 2015年1月28日 下午2:43:55
	 */
	@SuppressWarnings("rawtypes")
	public static Page<Record> paginate(int pageNumber, int pageSize, Class<? extends Model> modelClass, String sqlExt, Object... paras) {
		String sql = getSql(modelClass, sqlExt);
		return Db.paginate(pageNumber, pageSize, getSqlSelect(sql), getSqlExceptSelect(sql), paras);
	}

	/**
	 * 分页查询
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sql
	 * @param paras
	 * @return
	 */
	public static Page<Record> paginate(int pageNumber, int pageSize, String sql, Object... paras) {
		return Db.paginate(pageNumber, pageSize, getSqlSelect(sql), getSqlExceptSelect(sql), paras);
	}

	/**
	 * 获取Class对应的表名
	 * 
	 * @param modelClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getTableName(Class<? extends Model> modelClass) {
		return TableMapping.me().getTable(modelClass).getName();
	}

	/**
	 * 获取Class对应的表的主键
	 * 
	 * @param modelClass
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getPrimaryKey(Class<? extends Model> modelClass) {
		String primaryKeyName = TableMapping.me().getTable(modelClass).getPrimaryKey()[0];
		if (null == primaryKeyName || "".equals(primaryKeyName.trim())) {
			primaryKeyName = "id";
		}
		return primaryKeyName;
	}

	/**
	 * 批量删除
	 * 
	 * @param tableName
	 * @param ids
	 * @return
	 */
	public static boolean dels(String tableName, String fieldName, String ids) {
		boolean b = false;
		if (ZJ_StringUtils.isNullOrEmpty(fieldName)) {
			fieldName = "id";
		}
		if (ZJ_StringUtils.isNullOrEmpty(tableName) || ZJ_StringUtils.isNullOrEmpty(ids)) {
			return false;
		}

		if (ZJ_StringUtils.isNotEmpty(ids)) {
			String idsSql = ZJ_StringUtils.getInStr(ids);
			String sql = String.format("delete  from %s where %s in(%s)", tableName, fieldName, idsSql);
			int result = Db.update(sql);
			if (result > 0) {
				b = true;
			}
		}
		return b;
	}

	private static boolean del(String tableName, String fieldName, String fieldValue) {
		boolean b = false;
		if (ZJ_StringUtils.isNullOrEmpty(fieldName)) {
			fieldName = "id";
		}
		if (ZJ_StringUtils.isNullOrEmpty(tableName) || ZJ_StringUtils.isNullOrEmpty(fieldValue)) {
			return false;
		}

		if (ZJ_StringUtils.isNotEmpty(fieldValue)) {
			String sql = String.format("delete  from %s where %s=?", tableName, fieldName);
			int result = Db.update(sql, fieldValue);
			if (result > 0) {
				b = true;
			}
		}
		return b;
	}

	public static boolean dels(String tableName, String fieldName, Set<String> idsSet) {
		String[] ids = null;
		if (null != idsSet) {
			Object[] objs = idsSet.toArray();
			ids = new String[idsSet.size()];
			for (int i = 0; i < objs.length; i++) {
				ids[i] = objs[i].toString();
			}
			return dels(tableName, fieldName, ids);
		}
		return false;
	}

	public static boolean dels(String tableName, String fieldName, String[] ids) {
		boolean b = false;
		if (ZJ_StringUtils.isNullOrEmpty(fieldName)) {
			fieldName = "id";
		}
		if (ZJ_StringUtils.isNullOrEmpty(tableName) || ZJ_StringUtils.isNullOrEmpty(ids)) {
			return false;
		}

		if (ZJ_StringUtils.isNotEmpty(ids)) {
			String idsSql = ZJ_StringUtils.getInStr(ids);
			String sql = String.format("delete  from %s where %s in(%s)", tableName, fieldName, idsSql);
			int result = Db.update(sql);
			if (result > 0) {
				b = true;
			}
		}
		return b;
	}

	/**
	 * 批量删除
	 * 
	 * @param tableName
	 * @param ids
	 * @return
	 */
	public static boolean dels(String tableName, String ids) {
		return dels(tableName, null, ids);
	}

	/**
	 * 批量删除(推荐)
	 * 
	 * @param modelClass
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean dels(Class<? extends Model> modelClass, String ids) {
		return dels(getTableName(modelClass), getPrimaryKey(modelClass), ids);
	}

	@SuppressWarnings("rawtypes")
	public static boolean dels(Class<? extends Model> modelClass, Set<String> ids) {
		return dels(getTableName(modelClass), getPrimaryKey(modelClass), ids);
	}

	@SuppressWarnings("rawtypes")
	public static boolean dels(Class<? extends Model> modelClass, String fieldName, String ids) {
		return dels(getTableName(modelClass), fieldName, ids);
	}

	@SuppressWarnings("rawtypes")
	public static boolean del(Class<? extends Model> modelClass, String fieldName, String id) {
		return del(getTableName(modelClass), fieldName, id);
	}

	/**
	 * 批量删除(推荐)
	 * 
	 * @param modelClass
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean dels(Class<? extends Model> modelClass, String[] ids) {
		return dels(getTableName(modelClass), getPrimaryKey(modelClass), ids);
	}

	/**
	 * 更新
	 * 
	 * @param modelClass
	 * @param record
	 * @return
	 * @author 张剑
	 * @date 2014年12月31日 上午9:40:34
	 */
	@SuppressWarnings("rawtypes")
	public static boolean update(Class<? extends Model> modelClass, Record record) {
		return Db.update(getTableName(modelClass), record);

	}

	// 获取新插入的ID
	public static Long getInsertId() {
		return Long.valueOf(Db.queryFirst("select LAST_INSERT_ID()").toString());
	}
}
