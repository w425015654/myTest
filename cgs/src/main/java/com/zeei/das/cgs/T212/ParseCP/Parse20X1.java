/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Parse20X1.java
* 包  名  称：com.zeei.das.cgs.common.T212。ParseCP
* 文件描述：2011,2031,2051,2061消息体解析实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212.ParseCP;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.T212.Station;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.annotation.ParseCPAnnotation;
import com.zeei.das.common.utils.DateUtil;

import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：ParseCP 类 描 述：20X1消息体解析实现 功能描述：解析2051,2011,2061,2051格式消息
 * 创建作者：quanhongsheng
 */
@ParseCPAnnotation(CN = "2011,2031,2051,2061,2062,2063,2065,2066")
@Component("parse20X1")
public class Parse20X1 implements ParseCP {

	@Autowired
	Station station;

	// 是否进行因子转换
	static boolean isChangeParam = true;

	private static Logger logger = LoggerFactory.getLogger(Parse20X1.class);

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
			logger.error("数据时间格式不对：" + cpStr);
			return null;
		}

		String DT = "";
		String regDT = "DT=(\\d{14})";
		// 编译正则表达式
		Pattern patternDT = Pattern.compile(regDT);
		Matcher matcherDT = patternDT.matcher(cpStr);

		// DataTime格式(K-V)与数据格式(P-T-V)不同，需要单独解析
		if (matcherDT.find()) {
			DT = matcherDT.group(1);
		}

		// 每一个因子的格式都是 (因子)-(类型)=(值)的格式

		// 先将pp-ZsTTT转换为ppZs-TTT
		// cpStr = cpStr.replaceAll("/-Zs/g", "Zs-");

		String regParam = "([0-9a-zA-Z\\-]*)-([a-zA-Z]+)=([0-9a-zA-Z.\\-]+)";
		// 找出全部匹配的 因子-类型-值
		Pattern pgPattern = Pattern.compile(regParam);
		Matcher pgMatcher = pgPattern.matcher(cpStr);

		// 先将因子作为tmpObj的属性，然后将此临时对象的属性转换为cpObj下的因子数组
		// 此处没有直接写数组，因为需要将不同类型的值聚合到因子下面
		JSONObject map = new JSONObject();

		while (pgMatcher.find()) {

			if (pgMatcher.groupCount() == 3) {
				String ParamID = pgMatcher.group(1);
				String type = pgMatcher.group(2);
				String value = pgMatcher.group(3);

				JSONObject ele = new JSONObject();

				// 的子对象（本因子）可能已经在前面创建完成，如果没有，在这里创建
				// 为因子添加type (例如Rtd, Avg ,...)属性
				if (map.containsKey(ParamID)) {
					ele = map.getJSONObject(ParamID);
					ele.put(type, value);
				} else {
					ele.put(type, value);
					map.put(ParamID, ele);
				}
			}
		}

		// 将paramObj下面的因子的各属性转换为cpObj.Params数组的元素（相当于列转行）

		JSONArray params = new JSONArray();

		for (Entry<String, Object> entry : map.entrySet()) {

			JSONObject element = (JSONObject) entry.getValue();

			String paramId = entry.getKey();

			// 启用因子转换
			if (isChangeParam && !StringUtil.isNullOrEmpty(paramId)) {
				paramId = CgsService.paramMap.get(paramId);
				if (StringUtil.isNullOrEmpty(paramId)) {
					paramId = entry.getKey();
				}
			}

			element.put("ParamID", paramId);

			if (!element.containsKey("Rtd") && element.containsKey("Avg")) {
				element.put("Rtd", element.get("Avg"));
			}
			
			params.add(element);
		}

		JSONObject cp = new JSONObject();

		cp.put("DataTime", DateUtil.strToDate(DataTime, "yyyyMMddHHmmss"));

		// 采样时间，洪泽定制
		if (!StringUtil.isNullOrEmpty(DT)) {
			cp.put("DT", DateUtil.strToDate(DT, "yyyyMMddHHmmss"));
		}
		cp.put("Params", params);

		return cp;

	}

	@Override
	public void ack(JSONObject msgHead, Channel channel) {

		String MN = (String) msgHead.get("MN");
		StationCfgVO cfg = station.getStationCfg(MN);

		if (cfg != null && cfg.isMsgAck()) {
			String PW = (String) msgHead.get("PW");
			String QN = (String) msgHead.get("QN");
			String ST = (String) msgHead.get("ST");

			String msg = String.format("QN=%s;ST=%s;PW=%s;MN=%s;CN=9014;CP=&&&&", QN, ST, PW, MN);

			station.send(channel, msg);
		}
	}
	
	// 测试用
	public static void main(String args[]) {
//	      AvgSpeed.init(2, "a10001,a10002");
//	      
//	      double a = AvgSpeed.setArraySpeed("a10001", "10.0", "N", "20190515100002");
//	      a = AvgSpeed.setArraySpeed("a10001", "20.0", "N", "20190515100001");
//	      a = AvgSpeed.setArraySpeed("a10001", "30.0", "N", "20190515100003");
//	      
//	      String str = ""+a;
//	      System.out.println("str = :" + str);
//	      
//	      a = AvgSpeed.setArraySpeed("a10002", "10.0", "N", "20190515100002");
//	      a = AvgSpeed.setArraySpeed("a10002", "60.0", "N", "20190515100001");
//	      a = AvgSpeed.setArraySpeed("a10002", "60.0", "N", "20190515100003");
//	      
//	      str = ""+a;
//	      System.out.println("str = :" + str);
//	      
//	      boolean b = AvgSpeed.isValidVO("a10001", "20200515100006", 3);
//	      System.out.println("b = :" + b);
//	      
//	      AvgSpeed.printf();
	}
}
