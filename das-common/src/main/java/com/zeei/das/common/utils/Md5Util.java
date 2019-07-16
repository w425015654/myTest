/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：Md5Util.java
* 包  名  称：com.zeei.das.aas.common.util
* 文件描述：Md5 工具
* 创建日期：2017年4月20日下午12:55:09
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日下午12:55:09 创建文件
*
*/

package com.zeei.das.common.utils;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类 名 称：Md5Util 类 描 述：Md5 工具 功能描述：Md5 工具 创建作者：quanhongsheng
 */

public class Md5Util {

	public static final Logger logger = LoggerFactory.getLogger(Md5Util.class);

	private static MessageDigest md5 = null;
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 用于获取一个String的md5值
	 * 
	 * @param string
	 * @return
	 */
	public static String getMd5(String str) {
		byte[] bs = md5.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		for (byte x : bs) {
			if ((x & 0xff) >> 4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(getMd5("类 名 称：AlarmRuleService 类 描 述：初始化告警规则配置接口 功能描述：申明初始化告警规则接口方法"));
	}
}
