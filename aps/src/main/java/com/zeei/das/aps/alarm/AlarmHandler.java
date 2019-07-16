/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：AlarmHandler.java
* 包  名  称：com.zeei.das.aps.alarm
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月2日下午3:24:02
* 
* 修改历史
* 1.0 zhou.yongbo 2017年5月2日下午3:24:02 创建文件
*
*/

package com.zeei.das.aps.alarm;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aps.ApsService;
import com.zeei.das.aps.mq.Publish;
import com.zeei.das.aps.notice.NoticeHandler;
import com.zeei.das.aps.service.AlarmProcessService;
import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.AlarmSyncVO;
import com.zeei.das.aps.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.StatisticsAlarmType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：AlarmHandler 类 描 述：告警消息处理类
 */

@Service
public class AlarmHandler {

	private static Logger logger = LoggerFactory.getLogger(AlarmHandler.class);

	@Autowired
	AlarmProcessService alarmProcessService;

	@Autowired
	NoticeHandler noticeHandler;

	@Autowired
	Publish publish;

	public boolean handler(AlarmInfoVO alarm) {

		boolean flag = false;
		try {
			// 服务告警
			if (alarm.getServiceCode() != null) {
				noticeHandler.handler(alarm);
				flag = true;
			} else {
				if (StringUtil.isEmptyOrNull(alarm.getPolluteCode())) {
					alarm.setPolluteCode("-1");
				}

				Date dateTime = null;

				// 新告警
				if (alarm.isNewAlarm()) {
					noticeHandler.handler(alarm);
					alarm.setAlarmStatus(1);
					flag = alarmProcessService.insertAlarmInfo(alarm);

					String info = String.format("告警入库%s  %s", flag ? "成功" : "失败", JSON.toJSONStringWithDateFormat(alarm,
							"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

					dateTime = alarm.getStartTime();
					
				}

				// 消警
				else {
					alarm.setAlarmStatus(4);
					flag = alarmProcessService.cannelAlarm(alarm);
					String info = String.format("消警入库%s  %s", flag ? "成功" : "失败", JSON.toJSONStringWithDateFormat(alarm,
							"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

					dateTime = alarm.getEndTime();

				}
				String alarmType = alarm.getAlarmType();

				// 发送告警时长统计消息
				if (dateTime != null && !StringUtil.isEmptyOrNull(alarmType)
						&& !DateUtil.getCurrentDate("yyyy-mm-dd").equals(DateUtil.dateToStr(dateTime, "yyyy-mm-dd"))) {

					StationVO station = ApsService.stationMap.get(alarm.getPointCode());
					String MN = "";
					String ST = "";
					if (station != null) {
						MN = station.getMN();
						ST = station.getST();
					}

					Map<String, Object> dataCycle = new HashMap<String, Object>();
					dataCycle.put("MN", MN);
					dataCycle.put("CN", DataType.RTDATA);
					dataCycle.put("ST", ST);
					dataCycle.put("DATATIME", dateTime);

					// 发送离线告警通知
					if (Arrays.asList(StatisticsAlarmType.OnlineCode.split(",")).contains(alarmType)) {
						dataCycle.put("DST", DataStatisticsType.DST_GK_Network);
						String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
								SerializerFeature.WriteDateUseDateFormat);
						publish.send(Constant.MQ_QUEUE_CYCLE, json);
					}

					// 发送超标告警通知
					if (Arrays.asList(StatisticsAlarmType.OverproofCode.split(",")).contains(alarmType)) {
						dataCycle.put("DST", DataStatisticsType.DST_GK_Overproof);
						String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
								SerializerFeature.WriteDateUseDateFormat);
						publish.send(Constant.MQ_QUEUE_CYCLE, json);
					}

					// 发送参数异常告警通知
					if (Arrays.asList(StatisticsAlarmType.ParamExceptionCode.split(",")).contains(alarmType)) {
						dataCycle.put("DST", DataStatisticsType.DST_GK_ParamException);
						String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
								SerializerFeature.WriteDateUseDateFormat);
						publish.send(Constant.MQ_QUEUE_CYCLE, json);
					}

					// 发送停运告警通知
					if (Arrays.asList(StatisticsAlarmType.OutageCode.split(",")).contains(alarmType)) {
						dataCycle.put("DST", DataStatisticsType.DST_GK_Outage);
						String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
								SerializerFeature.WriteDateUseDateFormat);
						publish.send(Constant.MQ_QUEUE_CYCLE, json);
					}

				}
			}
		} catch (DataAccessException e) {
			logger.error(String.format("%s %s", alarm.toString(), e.getMessage()));
		}

		return flag;
	}

}
