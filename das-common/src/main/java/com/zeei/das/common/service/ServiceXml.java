/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：ServiceXml.java
* 包  名  称：com.zeei.das.common.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月13日上午8:48:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月13日上午8:48:30 创建文件
*
*/

package com.zeei.das.common.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 类 名 称：ServiceXml 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class ServiceXml {

	/**
	 * 
	 * readXmlService:读取服务配置
	 *
	 * @param xmlPath
	 * @return Map<String,Object>
	 */
	public static Map<String, Service> readXmlService() {

		Map<String, Service> pm = new HashMap<String, Service>();

		try {

			// 设置公用配置文件路径 系统变量
			String rootPath = System.getProperty("common.dir");
			String xmlPath = rootPath + "model.xml";

			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new File(xmlPath));
			Element root = document.getRootElement();
			Element modelRoot = root.element("services");
			List<?> models = modelRoot.elements("service");
			for (Object service : models) {
				Element se = (Element) service;
				String srvName = se.elementText("name");
				String srvCode = se.elementText("code");
				String threshold = se.elementText("threshold");

				Service obj = new Service();
				obj.setServiceCode(srvCode);
				obj.setServiceName(srvName);
				obj.setThreshold(Integer.valueOf(threshold));
				pm.put(srvCode, obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pm;
	}

}
