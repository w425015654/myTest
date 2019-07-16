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
import com.zeei.das.aas.service.AlarmRuleService;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * @类型名称：SewageAlarm
 * @类型描述：原水泵告警
 * @功能描述：TODO 请修改功能描述
 * @创建作者：quan.hongsheng
 *
 */
@Component("qgAlarm")
public class QGAlarm {

	private static Logger logger = LoggerFactory.getLogger(QGAlarm.class);

	@Autowired
	Publish publish;

	@Autowired
	AlarmRuleService alarmRuleService;

	static String alarmCode = "85008";

	// 原水泵：e30101
	static String pump = "e30101";

	static String alarmType = "13";

	public void alarmHandler(JSONObject data) {

		try {

			String MN = data.getString("MN");
			String CN = data.getString("CN");
			String ST = data.getString("ST");

			if (!(DataType.RTDATA.equals(CN) && SystemTypeCode.YQYG.equalsIgnoreCase(ST))) {
				return;
			}

			JSONObject CP = data.getJSONObject("CP");

			if (CP == null || CP.size() < 1) {
				String err = String.format("站点【%s】数据为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			JSONArray params = CP.getJSONArray("Params");

			if (params == null || params.size() < 1) {
				String err = String.format("站点【%s】数据为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			Date dataTime = CP.getDate("DataTime");

			int yearMN = DateUtil.getYear(dataTime);

			String yyr = DateUtil.dateToStr(dataTime, "yyyy-MM-dd");

			for (Object p : params) {

				JSONObject param = (JSONObject) p;
				String polluteCode = param.getString("ParamID");

				if (pump.equals(polluteCode)) {

					StationVO station = AasService.stationMap.get(MN);

					String psCode = "";
					String pointCode = "";

					if (station != null) {
						psCode = station.getPsCode();
						pointCode = station.getPointCode();
					}

					Double state = param.getDouble("Rtd");

					String times = alarmRuleService.queryDrainageTime(psCode, yearMN);

					int isAlarm = 0;

					if (!StringUtil.isEmptyOrNull(times)) {

						String[] arr = times.split("-");

						if (arr != null && arr.length == 2) {

							Date beginTime = DateUtil.strToDate(String.format("%s %s:00", yyr, arr[0]),
									"yyyy-MM-dd HH:mm:ss");
							Date endTime = DateUtil.strToDate(String.format("%s %s:00", yyr, arr[1]),
									"yyyy-MM-dd HH:mm:ss");

							// 非排水时段，原水泵开启
							if (!(beginTime.getTime() <= dataTime.getTime() && endTime.getTime() >= dataTime.getTime())
									&& state == 0) {
								isAlarm = 1;
							} else {
								isAlarm = 0;
							}
						}
					}

					// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
					String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, DataType.T2011);

					AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

					// 设备告警状态 {正常0 告警1}
					if (isAlarm == 1 && alarm == null) {
						alarm = new AlarmInfoVO();
						alarm.setAlarmCode(alarmCode);
						alarm.setDataType(DataType.T2011);
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
						String info = String.format("站点:%s 非排水时段原水泵开启[告警]---%s", pointCode,
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
						String info = String.format("站点:%s 非排水时段原水泵开启[消警]---%s", pointCode,
								DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
						logger.info(info);
						publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
						AasService.alarmMap.remove(alarmId);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

}
