/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：UpsideDownSkEngine.java
* 包  名  称：com.zeei.das.aas.alarm.engine
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年10月25日下午3:06:01
* 
* 修改历史
* 1.0 wudahe 2018年10月25日下午3:06:01 创建文件
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
 * @类型名称：UpsideDownSkEngine
 * @类型描述：实况PM2.5,PM10倒挂告警采集
 * @功能描述：实况PM2.5,PM10倒挂告警采集
 * @创建作者：wudahe
 *
 */

@Component("upsideDownSkEngine")
public class UpsideDownSkEngine implements RuleEngine {

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	private static String SK_PM25 = "a83001";
	private static String SK_PM10 = "a83002";

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

			JSONObject skpm25 = ParamUtil.findParam(SK_PM25, params);
			JSONObject skpm10 = ParamUtil.findParam(SK_PM10, params);

			if (skpm25 == null || skpm10 == null) {

				skpm25 = ParamUtil.findParam(SK_PM25, params);
				skpm10 = ParamUtil.findParam(SK_PM10, params);

				if (skpm25 == null || skpm10 == null) {
					String err = String.format("站点:%s 实况倒挂告警因子PM2.5【%s]/PM10[%s】不存在，！", pointCode, skpm25, skpm10);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
					logger.error(err);
					return 2;
				}
			}

			// 监测值
			Double skpm25Val;
			Double skpm10Val;

			if (DataType.T2011.equalsIgnoreCase(rule.getDataType())) {
				skpm25Val = skpm25.getDouble("Rtd");
				skpm10Val = skpm10.getDouble("Rtd");
			} else {
				skpm25Val = skpm25.getDouble("Avg");
				skpm10Val = skpm10.getDouble("Avg");
			}

			rule.setAlarmValue(skpm25Val.toString());

			if (skpm25Val > skpm10Val) {
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