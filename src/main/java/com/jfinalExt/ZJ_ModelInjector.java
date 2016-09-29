package com.jfinalExt;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.ActiveRecordException;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

public class ZJ_ModelInjector {
	@SuppressWarnings("unchecked")
	public <T> T getModel(Class<T> modelClass, HttpServletRequest request) {
		return (T) ZJ_ModelInjector.inject(modelClass, request, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T inject(Class<?> modelClass, HttpServletRequest request, boolean skipConvertError) {
		String modelName = modelClass.getSimpleName();
		return (T) inject(modelClass, StrKit.firstCharToLowerCase(modelName), request, skipConvertError);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static final <T> T inject(Class<?> modelClass, String modelName, HttpServletRequest request, boolean skipConvertError) {
		Object model = null;
		try {
			model = modelClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (model instanceof Model)
			injectActiveRecordModel((Model) model, modelName, request, skipConvertError);
		else
			injectCommonModel(model, modelName, request, modelClass, skipConvertError);

		return (T) model;
	}

	@SuppressWarnings("rawtypes")
	private static final void injectActiveRecordModel(Model<?> model, String modelName, HttpServletRequest request, boolean skipConvertError) {
		Table table = TableMapping.me().getTable(model.getClass());

		String modelNameAndDot = modelName + ".";

		Map<String, String[]> parasMap = request.getParameterMap();
		for (Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndDot)) {
				String paraName = paraKey.substring(modelNameAndDot.length());
				Class colType = table.getColumnType(paraName);
				if (colType == null)
					throw new ActiveRecordException("The model attribute " + paraKey + " is not exists.");
				String[] paraValue = e.getValue();
				try {
					Object value = paraValue[0] != null ? ZJ_TypeConverter.convert(colType, paraValue[0]) : null;
					model.set(paraName, value);
				} catch (Exception ex) {
					if (skipConvertError == false)
						throw new RuntimeException("Can not convert parameter: " + modelNameAndDot + paraName, ex);
				}
			}
		}
	}

	private static final void injectCommonModel(Object model, String modelName, HttpServletRequest request, Class<?> modelClass, boolean skipConvertError) {
		Method[] methods = modelClass.getMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("set") == false) // only setter method
				continue;

			Class<?>[] types = method.getParameterTypes();
			if (types.length != 1) // only one parameter
				continue;

			String attrName = methodName.substring(3);
			String value = request.getParameter(modelName + "." + StrKit.firstCharToLowerCase(attrName));
			if (value != null) {
				try {
					method.invoke(model, ZJ_TypeConverter.convert(types[0], value));
				} catch (Exception e) {
					if (skipConvertError == false)
						throw new RuntimeException(e);
				}
			}
		}
	}
}
