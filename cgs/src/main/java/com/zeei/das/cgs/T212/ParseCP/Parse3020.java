/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse3020.java
* 包  名  称：com.zeei.das.cgs.T212.ParseCP
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月14日上午8:45:44
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月14日上午8:45:44 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.annotation.ParseCPAnnotation;
import com.zeei.das.common.utils.DateUtil;

import io.netty.channel.Channel;

/**
 * 类 名 称：Parse3020 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@ParseCPAnnotation(CN = "3020")
@Component
public class Parse3020 implements ParseCP {

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

		String polId = "PolId";
		List<Integer> indexs = new ArrayList<Integer>();

		// *第一个出现的索引位置
		int a = cpStr.indexOf(polId);
		while (a != -1) {

			indexs.add(a);
			// *从这个索引往后开始第一个出现的位置
			a = cpStr.indexOf(polId, a + 1);
		}

		String[] regs = new String[] { "([0-9a-zA-Z\\-]*)=//(.*)//", "([0-9a-zA-Z\\-]*)=([0-9a-zA-Z.\\-]+)" };

		JSONObject items = new JSONObject();

		for (int i = 0; i < indexs.size(); i++) {

			JSONObject item = new JSONObject();

			int beginIndex = indexs.get(i);

			String str = "";

			if (i == indexs.size() - 1) {
				str = cpStr.substring(beginIndex);
			} else {
				int endIndex = indexs.get(i + 1);
				str = cpStr.substring(beginIndex, endIndex);
			}

			for (String regParam : regs) {
				// 找出全部匹配的 因子-类型-值
				pattern = Pattern.compile(regParam);
				matcher = pattern.matcher(str);

				while (matcher.find()) {

					if (matcher.groupCount() == 2) {
						String key = matcher.group(1);
						String value = matcher.group(2);
						if (key.endsWith("-Info")) {
							key = key.substring(0, key.lastIndexOf("-Info"));
							item.put(key, value);
						} else {
							item.put(key, value);
						}
					}
				}
			}

			String code = item.getString("PolId");
			item.remove("PolId");

			if (items.containsKey(code)) {
				items.getJSONObject(code).putAll(item);
			} else {
				items.put(code, item);
			}

		}

		cpMap.put("Item", items);

		return cpMap;

	}

	public void ack(JSONObject msgHead, Channel channel) {

	}

}
