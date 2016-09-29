package com.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.vo.AdminInfoVo;

/**
 * 全局拦截器类
 * 
 * @author 张剑
 * 
 */
public class GlobalInterceptor implements Interceptor {
	Logger logger = Logger.getLogger(Object.class);

	

	/**
	 * 获取网站根url
	 * 
	 * @author 张剑
	 * @param request
	 * @return
	 */
	private String getBasePath(HttpServletRequest request) {
		String basePath = "/";
		StringBuilder sb = new StringBuilder().append(request.getScheme()).append("://").append(request.getServerName());
		if (request.getServerPort() != 80) {
			sb.append(":").append(request.getServerPort());
		}
		sb.append(request.getContextPath());
		sb.append("/");
		basePath = sb.toString();
		return basePath;

	}

	@Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
//		String actionKey = inv.getActionKey();
		HttpServletRequest request = controller.getRequest();
//		HttpServletResponse response = controller.getResponse();
		String basePath = getBasePath(request);
		AdminInfoVo adminInfoVo = (AdminInfoVo) controller.getSession().getAttribute("adminInfo");
		controller.setAttr("adminInfo", adminInfoVo);
		controller.setAttr("basePath", basePath);
		inv.invoke();
	}

}
