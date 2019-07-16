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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.annotation.ParseCPAnnotation;
import com.zeei.das.common.utils.DateUtil;

import io.netty.channel.Channel;


/** 
* @类型名称：Parse3041
* @类型描述：
* @功能描述：3041消息解析
* @创建作者：zhanghu
*/
@ParseCPAnnotation(CN = "3041")
@Component
public class Parse3041 implements ParseCP {

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

		String regParam = "([0-9a-zA-Z\\-]*)=([0-9a-zA-Z.\\-]+)";
		// 找出全部匹配的 因子-值
		Pattern pgPattern = Pattern.compile(regParam);
		Matcher pgMatcher = pgPattern.matcher(cpStr);
		
		JSONObject items = new JSONObject();
        //寻找所有因子和值
		while (pgMatcher.find()) {

			if (pgMatcher.groupCount() == 2) {
				String ParamID = pgMatcher.group(1);
				String value = pgMatcher.group(2);
				JSONObject item = new JSONObject();
				//因子转换
				switch (ParamID) {
				case "Lng"://经度
					ParamID = "e01101";
					break;
                case "Lat"://纬度
                	ParamID = "e01102";
					break;
                case "Volt"://电压(系统)
                	ParamID = "e01004";
					break;
                case "Temp"://温度
                	ParamID = "e01001";
					break;
                case "Hum"://湿度
                	ParamID = "e01002";
					break;
					
				default:
					continue;
				}
				
				item.put("i32001", value);
				items.put(ParamID, item);
			}
		}	
		cpMap.put("Item", items);

		return cpMap;
	}

	public void ack(JSONObject msgHead, Channel channel) {

	}

}
