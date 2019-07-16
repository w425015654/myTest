/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：logger.java
* 包  名  称：com.zeei.das.cgs.log
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月8日下午1:12:52
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月8日下午1:12:52 创建文件
*
*/

package com.zeei.das.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 类 名 称：logger 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class LoggerUtil {

	public static String getLogInfo(String logType, Object msg) {

		String srvCode = ModelUtil.getSrvCode();
		Map<String, Object> log = new HashMap<String, Object>();

		log.put("srvCode", srvCode);
		log.put("logType", logType);
		log.put("logContent", msg);
		return JSON.toJSONStringWithDateFormat(log, "yyyyMMddHHmmss", SerializerFeature.WriteDateUseDateFormat);
	}

}
