/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：PlaceholderConfigurerUtil.java
* 包  名  称：com.zeei.das.commom.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月1日上午9:25:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月1日上午9:25:24 创建文件
*
*/

package com.zeei.das.common.utils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 类 名 称：PlaceholderConfigurerUtil 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public class LoadConfigUtil {

	private static String xmlPath = null;

	private static ClassPathXmlApplicationContext applicationContext;
	

	static {

		// 获取工作目录
		String root = System.getProperty("user.dir");
		root = new File(root).getParent();
		// 默认公用配置文件路径
		root = root + File.separator + "conf" + File.separator;
		// Log4j 加载.properties 文件来加载配置文件，
		System.out.println("加载log4j配置.....");
		PropertyConfigurator.configure(root + "log4j.properties");

		// 设置公用配置文件路径 系统变量
		System.setProperty("common.dir", root);
		xmlPath = root + "model.xml";
	}

	public static void loadConfig() {

		try {
			// 加载Spring xml
			
			System.out.println("加载配置.....");
			applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

		} catch (Exception e) {

			System.out.println("服务异常重启成！");
			refresh();
			e.printStackTrace();
		}
	}

	public static void loadConfig(String xmlPath) {

		try {
			// 加载Spring xml
			System.out.println("加载配置.....");
			applicationContext = new ClassPathXmlApplicationContext(xmlPath);

		} catch (Exception e) {
			System.out.println("服务异常重启成！");
			refresh();
			e.printStackTrace();
		}
	}

	public static void refresh() {

		if (applicationContext == null) {

			loadConfig();

		} else {
			
			System.out.println("加载配置.....");
			
			applicationContext.refresh();
		}
	}

	/**
	 * 
	 * readXmlParam:读取模块参数配置
	 *
	 * @param modelName
	 *            服务名称
	 * @param xmlPath
	 * @return Map<String,Object>
	 */
	public static Map<String, String> readXmlParam(String modelName) {

		Map<String, String> pm = new HashMap<String, String>();

		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlPath));
			Element root = document.getRootElement();
			Element modelRoot = root.element("models");
			List<?> models = modelRoot.elements("model");

			if (models != null && models.size() > 0) {

				for (Object param : models) {
					Element paramEle = (Element) param;
					String id = paramEle.attributeValue("id");
					if (modelName.equals(id)) {
						List<?> params = paramEle.elements();
						if (params != null && params.size() > 0) {
							for (Object p : params) {
								Element pe = (Element) p;
								String name = pe.getName();
								String value = pe.getText();
								pm.put(name, value);

							}
						}
					}
				}
			}

			String reportCycle = pm.get("reportCycle");

			if (!StringUtil.isEmptyOrNull(reportCycle) && !"null".equalsIgnoreCase(reportCycle)) {
				ModelUtil.setReportCycle(Integer.valueOf(reportCycle));
			}

			String srvCode = pm.get("srvCode");
			ModelUtil.setSrvCode(srvCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pm;
	}

}
