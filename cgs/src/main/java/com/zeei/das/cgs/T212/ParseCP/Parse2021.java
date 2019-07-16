/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse2021.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：2021消息体解析实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.utils.DateUtil;

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseCP 类 描 述：2021消息体解析实现 功能描述：解析2021格式消息 创建作者：quanhongsheng
 */

@com.zeei.das.common.annotation.ParseCPAnnotation(CN="2021")
@Component("parse2021")
public class Parse2021 implements ParseCP {

	@Override
	public JSONObject parseT212Body(String cpStr) {

		String DataTime = "";
		String regDataTime = "DataTime=(\\d{14})";
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regDataTime);
		Matcher matcher = pattern.matcher(cpStr);

		// DataTime格式(K-V)与数据格式(P-T-V)不同，需要单独解析
		if (matcher.find()) {
			DataTime = matcher.group(1);
		} else {
			return null;
		}

		JSONObject cpMap = new JSONObject();
		cpMap.put("DataTime", DateUtil.strToDate(DataTime, "yyyyMMddHHmmss"));

		String regParam = "([0-9a-zA-Z\\-]*)-([a-zA-Z0-9\\-]+)=([0-9a-zA-Z.\\-]+)";
		// 找出全部匹配的 因子-类型-值
		pattern = Pattern.compile(regParam);
		matcher = pattern.matcher(cpStr);

		while (matcher.find()) {

			if (matcher.groupCount() == 3) {
				String key = matcher.group(1) + "-" + matcher.group(2);
				String value = matcher.group(3);
				cpMap.put(key, value);
			}
		}

		return cpMap;
	}

	
	@Override
	public void ack(JSONObject msgHead,Channel channel) {
	}

}
