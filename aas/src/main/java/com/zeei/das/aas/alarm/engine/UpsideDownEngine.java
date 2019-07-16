/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：UpsideDownEngine.java
* 包  名  称：com.zeei.das.aas.alarm
* 文件描述：PM2.5,PM10倒挂告警采集
* 创建日期：2017年4月21日上午8:40:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:40:39 创建文件
*
*/

package com.zeei.das.aas.alarm.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aas.alarm.Alarm;
import com.zeei.das.aas.alarm.ParamUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：UpsideDownEngine 类 描 述：PM2.5,PM10倒挂告警采集 功能描述：PM2.5,PM10倒挂告警采集
 * 创建作者：quanhongsheng
 */

@Component("upsideDownEngine")
public class UpsideDownEngine implements RuleEngine {

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	private static String PM25 = "a34004";
	private static String PM10 = "a34002";

	@Autowired
	Publish publish;

	@Override
	public int analysis(AlarmRuleVO rule, JSONArray params) {

		try {

			String pointCode = rule.getPointCode();

			if (params == null || params.size() < 1) {
				String err = String.format("站点【%s】数据为空,倒挂告警！", pointCode);
				logger.error(err);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return 2;
			}

			JSONObject pm25 = ParamUtil.findParam(PM25, params);
			JSONObject pm10 = ParamUtil.findParam(PM10, params);

			if (pm25 == null || pm10 == null) {
				String err = String.format("站点:%s 倒挂告警因子PM2.5【%s]/PM10[%s】不存在，！", pointCode, pm25, pm10);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				logger.error(err);
				return 2;
			}

			// 监测值
			Double pm25Val;
			Double pm10Val;

			if (DataType.T2011.equalsIgnoreCase(rule.getDataType())) {
				pm25Val = pm25.getDouble("Rtd");
				pm10Val = pm10.getDouble("Rtd");
			} else {
				pm25Val = pm25.getDouble("Avg");
				pm10Val = pm10.getDouble("Avg");
			}

			rule.setAlarmValue(pm25Val.toString());

			if (pm25Val > pm10Val) {
				return 1;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getCause().toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
			return 2;
		}
	}

}
