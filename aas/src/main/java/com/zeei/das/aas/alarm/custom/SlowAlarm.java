/** 
* Copyright (C) 2012-2019 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：SlowAlarm.java
* 包  名  称：com.zeei.das.aas.alarm.custom
* 文件描述：
* 创建日期：2019年6月11日上午10:00:36
* 
* 修改历史
* 1.0 lian.wei 2019年6月11日上午10:00:36 创建文件
*
*/

package com.zeei.das.aas.alarm.custom;

/**
 * @类型名称：SlowAlarm
 * @类型描述：
 * @功能描述：
 * @创建作者：lian.wei
 */

import java.util.Date;

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
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * @类型名称：上报数据缓慢告警 @功能描述：该告警针对站点，不针对单个因子
 * @创建作者：wudahe
 *
 */
@Component("slowAlarm")
public class SlowAlarm {

	private static Logger logger = LoggerFactory.getLogger(SlowAlarm.class);

	@Autowired
	Publish publish;

	static String alarmType = "3"; // 网络异常

	public void alarmHandler(JSONObject data) {

		try {
			if ((AasService.slowCode != null) && (AasService.interval > 0)) {
				String MN = data.getString("MN");
				String CN = data.getString("CN");
				String pointCode = data.getString("ID");

				if (!DataType.RTDATA.equals(CN)) {
					return;
				}

				JSONObject CP = data.getJSONObject("CP");

				if (CP == null || CP.size() < 1) {
					String err = String.format("站点【%s】数据为空！", MN);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
					return;
				}

				String dataType = getDataType(CN);

				Date dataTime = CP.getDate("DataTime");
				Date endDate = new Date();

				int isAlarm = 0;

				// 系统时间 - 上报时间 > 间隔
				int interval = AasService.interval;

				long diff = DateUtil.dateDiffSecond(dataTime, endDate);

				if (diff > (interval * 60)) {
					isAlarm = 1;
				}

				// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
				String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, AasService.slowCode, null, dataType);

				AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

				// 告警状态 {正常0 告警1}
				if (isAlarm == 1 && alarm == null) {
					alarm = new AlarmInfoVO();
					alarm.setAlarmCode(AasService.slowCode);
					alarm.setDataType(dataType);
					alarm.setStartTime(dataTime);
					alarm.setStorage(true);
					alarm.setNewAlarm(true);
					alarm.setPointCode(pointCode);
					alarm.setAlarmType(alarmType);
					AasService.alarmMap.put(alarmId, alarm);

					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);

					publish.send(Constant.MQ_QUEUE_ALARM, json);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
					String info = String.format("站点:%s 下位机上报数据的时间与当前系统时间差距超过设置的门限[告警]---%s", pointCode,
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
					String info = String.format("站点:%s 下位机上报数据的时间与当前系统时间差距超过设置的门限[消警]---%s", pointCode,
							DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
					AasService.alarmMap.remove(alarmId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	private String getDataType(String CN) {
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

		return dataType;
	}

}
