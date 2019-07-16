/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse901X.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：9011,9012消息体解析实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.annotation.ParseCPAnnotation;

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseCP 类 描 述：201X消息体解析实现 功能描述：解析9011,9012格式消息 创建作者：quanhongsheng
 */
@ParseCPAnnotation(CN = "9011,9012")
@Component("parse901X")
public class Parse901X implements ParseCP {

	@Override
	public JSONObject parseT212Body(String cpStr) {
		JSONObject cpMap = new JSONObject();

		String regParam = "(QN|QnRtn|ExeRtn|VER|ConWorkTime)=([\\w]*)";
		// 找出全部匹配的 因子-类型-值
		Pattern pattern = Pattern.compile(regParam);
		Matcher matcher = pattern.matcher(cpStr);

		while (matcher.find()) {

			if (matcher.groupCount() == 2) {
				String key = matcher.group(1);
				String value = matcher.group(2);
				cpMap.put(key, value);
			}
		}
		// MValue=XX,SValue=XX,Result=XX
		String R_MSR = "(MValue=[\\w]*,SValue=[\\w]*,Result=[\\w]*)";
		Pattern P_MSR = Pattern.compile(R_MSR);
		Matcher M_MSR = P_MSR.matcher(cpStr);

		List<String> array = new ArrayList<String>();
		while (M_MSR.find()) {
			if (M_MSR.groupCount() > 0) {
				array.add(M_MSR.group(1));
			}
		}

		String result = "";
		if (!array.isEmpty()) {

			String str = array.get(0);

			String R_Result = "Result=([\\w]*)";
			Pattern P_Result = Pattern.compile(R_Result);
			Matcher M_Result = P_Result.matcher(str);
			while (M_Result.find()) {
				if (M_Result.groupCount() > 0) {
					result = M_Result.group(1);
				}
			}
		}
		cpMap.put("mrqStr", StringUtils.join(array, ";"));
		cpMap.put("mrqFlag", result);

		return cpMap;
	}

	@Override
	public void ack(JSONObject msgHead, Channel channel) {
	}
}
