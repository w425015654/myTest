/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：SewageAlarm.java
* 包  名  称：com.zeei.das.aas.alarm.custom
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年12月14日下午4:32:04
* 
* 修改历史
* 1.0 luoxianglin 2017年12月14日下午4:32:04 创建文件
*
*/

package com.zeei.das.aas.alarm.custom;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.FlagRuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.Md5Util;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类型名称：SewageAlarm 类型描述：TODO 请修改类型描述 功能描述：TODO 请修改功能描述 创建作者：quan.hongsheng
 *
 */
@Component("flagAlarm")
public class FlagAlarm {

	private static Logger logger = LoggerFactory.getLogger(FlagAlarm.class);

	@Autowired
	Publish publish;

	public void alarmHandler(JSONObject data) {

		try {

			String pointCode = (String) data.get("ID");
			String MN = (String) data.get("MN");
			JSONObject CP = (JSONObject) data.get("CP");
			String CN = data.getString("CN");

			// 获取站点配置告警
			List<String> cr = AasService.stationAlarm.get(MN);

			if (cr != null && cr.contains("90096")) {

				if (CP != null && CP.size() > 0) {

					JSONArray params = CP.getJSONArray("Params");

					if (params != null && params.size() > 0) {

						Date dataTime = CP.getDate("DataTime");

						for (Object o : params) {

							JSONObject param = (JSONObject) o;

							if (param != null) {

								String flag = param.getString("Flag");

								String dataType = DataType.T2011;

								switch (CN) {
								case DataType.RTDATA:
									dataType = DataType.T2011;
									break;
								case DataType.DAYDATA:
									dataType = DataType.T2031;
									break;
								case DataType.MINUTEDATA:
									dataType = DataType.T2051;
									break;
								case DataType.HOURDATA:
									dataType = DataType.T2061;
									break;
								}

								String ruleid = Md5Util.getMd5(flag + dataType);
								FlagRuleVO rule = AasService.flagAlarmRule.get(ruleid);

								if (rule != null) {
									String polluteCode = param.getString("ParamID");
									boolean result = false;
									if (rule != null) {
										result = true;
									}
									generationAlarm(result, rule, pointCode, dataTime, polluteCode);
								}

							}

						}
					}

				}
			}

		} catch (Exception e) {
			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

	/**
	 * 
	 * generationAlarm:告警生成器
	 *
	 * @param result
	 *            数据分析结果
	 * @param rule
	 *            告警规则实体
	 * 
	 *            void
	 */
	private void generationAlarm(boolean result, FlagRuleVO rule, String pointCode, Date dataTime, String polluteCode) {

		try {

			String alarmCode = rule.getAlarmCode();

			String dataType = rule.getDataType();

			// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
			String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);

			// 根据规则id 获取内存告警数据
			AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

			if (result) {
				// 符合规则，内存中不存在告警数据
				if (alarm == null) {

					alarm = new AlarmInfoVO();
					alarm.setAlarmCode(alarmCode);
					alarm.setStartTime(dataTime);
					alarm.setPointCode(pointCode);
					alarm.setPolluteCode(polluteCode);
					alarm.setAlarmType(rule.getAlarmType());
					alarm.setDataType(dataType);
					alarm.setStorage(false);
					AasService.alarmMap.put(alarmId, alarm);

					String info = String.format("站点:%s Flag告警[%s]---%s", pointCode, polluteCode,
							DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

				}

				Date alarmDate = alarm.getStartTime();
				long durTime = DateUtil.dateDiffMin(alarmDate, dataTime);

				// 告警是否持久化,没有进行持久化处理
				if (alarm.isStorage()) {
					alarm.setEndTime(dataTime);
					return;
				}

				// 告警持续时间 大于 规则设置时间，进行持久化处理
				if (rule.getDurTime() <= durTime) {
					alarm.setStorage(true);
					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);

					if (!StringUtil.isEmptyOrNull(alarmCode)) {
						publish.send(Constant.MQ_QUEUE_ALARM, json);
					}

					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));

					String info = String.format("站点:%s Flag告警入库[%s]---%s", pointCode, polluteCode,
							DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
				}

			} else {
				// 分析结果不符合规则，且内存存在告警数据
				if (alarm != null && alarm.getStartTime().getTime() < dataTime.getTime()) {

					// 告警已经持久化，进行消警处理
					if (alarm.isStorage()) {

						alarm.setNewAlarm(false);

						if (alarm.getEndTime() == null) {
							alarm.setEndTime(dataTime);
						}

						String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
								SerializerFeature.WriteDateUseDateFormat);

						if (!StringUtil.isEmptyOrNull(alarmCode)) {
							publish.send(Constant.MQ_QUEUE_ALARM, json);
						}

						publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
					}

					String info = String.format("站点:%s Flag消除[%s]---%s", pointCode, polluteCode,
							DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

					// 删除告警内存数据
					AasService.alarmMap.remove(alarmId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

}
