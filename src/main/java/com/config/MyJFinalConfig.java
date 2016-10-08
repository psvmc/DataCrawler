package com.config;

import com.interceptor.GlobalInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.model.TArticle;
import com.utils.ZJ_PropertyConfig;

public class MyJFinalConfig extends JFinalConfig {
	boolean devMode = false;
	boolean isUseOracle = false;
	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载数据库配置文件		
		ZJ_PropertyConfig config = ZJ_PropertyConfig.me();
		config.loadPropertyFile("config.properties");
		
		devMode = config.getPropertyToBoolean("devMode", false);
		isUseOracle = config.getPropertyToBoolean("isUseOracle", false);
		// 设定为开发者模式
		me.setDevMode(devMode);
		me.setViewType(ViewType.FREE_MARKER);
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add(new HtRoutes()); // 后台路由
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
//		// 连接池插件
//		ZJ_PropertyConfig config = ZJ_PropertyConfig.me();
//		String jdbcUrl = config.getProperty("jdbcUrl");
//		String user = config.getProperty("user");
//		String password = config.getProperty("password");
//		
//		DruidPlugin druid = null;
//		if(isUseOracle){
//			 jdbcUrl = config.getProperty("oracleUrl");
//			 user = config.getProperty("oracleUser");
//			 password = config.getProperty("oraclePwd");
//			 druid = new DruidPlugin(jdbcUrl, user, password);
//			 druid.setDriverClass("oracle.jdbc.driver.OracleDriver");
//			 me.add(druid);
//		}else{
//			druid = new DruidPlugin(jdbcUrl, user, password);
//			me.add(druid);
//		}
//		
//		// 表绑定插件
//		ActiveRecordPlugin arp = new ActiveRecordPlugin(druid);
//		//开发者模式时显示sql语句
//		arp.setShowSql(devMode);
//		if(isUseOracle){
//			// 配置Oracle方言
//			arp.setDialect(new OracleDialect());
//		}else{
//			arp.setDialect(new MysqlDialect());
//		}
//
//		// 配置属性名(字段名)大小写不敏感容器工厂
//		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
//		arp.addMapping("t_article", TArticle.class);
//
//		// 视图映射
//		me.add(arp);

	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new GlobalInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
	}

}
