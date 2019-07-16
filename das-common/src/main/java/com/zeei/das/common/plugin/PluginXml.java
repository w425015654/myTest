/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：PluginXml.java
* 包  名  称：com.zeei.das.common.plugin
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月10日下午5:56:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月10日下午5:56:30 创建文件
*
*/

package com.zeei.das.common.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 类 名 称：PluginXml 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class PluginXml {

	/**
	 * 
	 * readXmlPlugin:读取插件配置
	 *
	 * @param xmlPath
	 * @return Map<String,Plugin>
	 */
	public static Map<String, Plugin> readXmlPlugin() {

		Map<String, Plugin> pm = new HashMap<String, Plugin>();

		try {

			// 设置公用配置文件路径 系统变量
			String rootPath = System.getProperty("common.dir");
			String xmlPath = rootPath + "model.xml";

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlPath));
			Element root = document.getRootElement();
			Element modelRoot = root.element("plugins");
			String directory = modelRoot.attributeValue("directory");
			List<?> plugins = modelRoot.elements("plugin");

			for (Object pluginObj : plugins) {
				Element pluginEle = (Element) pluginObj;
				Plugin plugin = new Plugin();
				String name = pluginEle.elementText("name");
				plugin.setName(name);
				plugin.setJar(pluginEle.elementText("jar"));
				plugin.setDesc(pluginEle.elementText("desc"));
				plugin.setDirectory(directory);
				plugin.setThreshold(Integer.valueOf(pluginEle.elementText("threshold")));
				pm.put(name, plugin);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return pm;
	}

}
