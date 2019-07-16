/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：PropertiesUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：TODO 读取配置文件
* 创建日期：2017年4月18日上午8:21:59
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月18日上午8:21:59 创建文件
*
*/

package com.zeei.das.common.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 类 名 称：PropertiesUtil 类 描 述：读取配置文件 功能描述：读取配置文件 创建作者：quanhongsheng
 */

public class PropertiesUtil {

	// 根据Key读取Value
	public static String getValueByKey(String filePath, String key) {
		Properties pps = new Properties();

		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			pps.load(in);
			String value = pps.getProperty(key);
			System.out.println(key + " = " + value);
			return value;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 读取Properties的全部信息
	public static Map<String, String> getAllProperties(String filePath) {
		Properties pps = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		try {

			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			pps.load(in);
			Enumeration<?> en = pps.propertyNames(); // 得到配置文件的名字

			while (en.hasMoreElements()) {
				String strKey = (String) en.nextElement();
				String strValue = pps.getProperty(strKey);

				map.put(strKey, strValue);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;

	}

	// 写入Properties信息
	public static void writeProperties(String filePath, String pKey, String pValue) {
		Properties pps = new Properties();
		try {
			InputStream in = new FileInputStream(filePath);
			// 从输入流中读取属性列表（键和元素对）
			pps.load(in);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream out = new FileOutputStream(filePath);
			pps.setProperty(pKey, pValue);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			pps.store(out, "Update " + pKey + " name");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
