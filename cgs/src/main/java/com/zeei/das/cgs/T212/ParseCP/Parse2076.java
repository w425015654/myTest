/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse2076.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：2076消息体解析实现
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

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseCP 类 描 述：2076消息体解析实现 功能描述：解析2076格式消息 创建作者：quanhongsheng
 */
@com.zeei.das.common.annotation.ParseCPAnnotation(CN="2076")
@Component("parse2076")
public class Parse2076 implements  ParseCP {

	@Override
	public JSONObject parseT212Body(String cpStr) {

		JSONObject cpMap = new JSONObject();

		String regParam = "(SN|LogTime|LoginName|LogType|LogAct)=([\\-\\w\\,\\=]*)";
		// 找出全部匹配的 因子-类型-值
		Pattern pattern = Pattern.compile(regParam);
		Matcher matcher = pattern.matcher(cpStr);

		while (matcher.find()) {

			if (matcher.groupCount() == 2) {

				String key = matcher.group(1);
				String value = matcher.group(2);
				if ("LoginName".equals(key) && !isNumeric(value)) {
					value = "-1";
				}
				cpMap.put(key, value);
			}
		}

		return cpMap;
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	@Override
	public void ack(JSONObject msgHead,Channel channel) {
	}
}
