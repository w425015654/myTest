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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.service.AlarmRuleService;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * @类型名称：SewageAlarm
 * @类型描述：TODO 请修改类型描述
 * @功能描述：TODO 请修改功能描述
 * @创建作者：quan.hongsheng
 *
 */
@Component("aqiAlarm")
public class AqiAlarm {

	private static Logger logger = LoggerFactory.getLogger(AqiAlarm.class);

	@Autowired
	Publish publish;

	@Autowired
	AlarmRuleService alarmRuleService;

	static String alarmCode = "10000";

	static String alarmType = "13";

	static String polluteCode = "AQI";

	public void alarmHandler(JSONObject data) {

		try {

			String pointCode = data.getString("ID");

			Integer aqi = data.getInteger("AQI");

			Date dataTime = data.getDate("DataTime");
			
			if(aqi ==null){				
				return;
			}

			// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
			String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, DataType.T2061);

			int isAlarm = 0;

			// 获取站点配置告警
			List<String> cr = AasService.stationAlarm.get(pointCode);

			if (cr != null && cr.contains(alarmCode)) {

				if (aqi > AasService.aqiAlarm) {

					isAlarm = 1;
				} else {
					isAlarm = 0;
				}

			} else {
				isAlarm = 0;
			}

			AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

			// 设备告警状态 {正常0 告警1}
			if (isAlarm == 1 && alarm == null) {
				alarm = new AlarmInfoVO();
				alarm.setAlarmCode(alarmCode);
				alarm.setDataType(DataType.T2061);
				alarm.setStartTime(dataTime);
				alarm.setStorage(true);
				alarm.setNewAlarm(true);
				alarm.setPointCode(pointCode);
				alarm.setPolluteCode(polluteCode);
				alarm.setAlarmType(alarmType);
				AasService.alarmMap.put(alarmId, alarm);

				String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);

				publish.send(Constant.MQ_QUEUE_ALARM, json);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
				String info = String.format("站点:%s 小时AQI[告警]---%s", pointCode,
						DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
				logger.info(info);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

			}

			if (isAlarm == 0 && alarm != null && alarm.getStartTime().getTime() < dataTime.getTime()) {

				alarm.setEndTime(dataTime);
				alarm.setNewAlarm(false);

				String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_ALARM, json);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
				String info = String.format("站点:%s 小时AQI[消警]---%s", pointCode,
						DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
				logger.info(info);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
				AasService.alarmMap.remove(alarmId);
			}

		} catch (Exception e) {
			logger.error("", e);			
		}
	}

}
