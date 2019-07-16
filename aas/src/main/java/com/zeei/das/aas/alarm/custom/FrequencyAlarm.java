package com.zeei.das.aas.alarm.custom;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.Alarm;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * @类型名称：FrequencyAlarm @类型描述： 告警统计 @功能描述： 统计频率传输异常的告警
 * @创建作者：zhanghu
 *
 */
@Component("frequencyAlarm")
public class FrequencyAlarm extends Alarm {

	private static Logger logger = LoggerFactory.getLogger(FrequencyAlarm.class);

	@Autowired
	Publish publish;

	// 传输频率异常的告警 类型
	private static String ALARMTYPE = "7";
	// 对应的因子类型
	private static String polluteCode = "-1";
	
	@Override
	public void alarmHandler(JSONObject data) {

		try {
			String MN = data.getString("MN");
			String CN = data.getString("CN");
			String ST = data.getString("ST");
			String pointCode = data.getString("ID");
			JSONObject CP = data.getJSONObject("CP");

			// 只对原始数据及分钟小时数据 计算周期
			if (!(DataType.RTDATA.equals(CN) || !DataType.MINUTEDATA.equals(CN) || !DataType.HOURDATA.equals(CN))) {

				return;
			}
			// 对水系统，暂不计算传输频率异常的告警！
			if("21".equals(ST)){
				return;
			}

			if (CP == null || CP.size() < 1) {
				String err = String.format("站点【%s】数据为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			Date dataTime = CP.getDate("DataTime");

			if (dataTime ==null) {
				String err = String.format("站点【%s】数据日期为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			// 对应测点的周期及日期
			StationVO sitePeriod = AasService.stationMap.get(MN);

			Date endDate = dataTime;
			Date beginDate = null;

			int interval = 5;
			long diff = 0;

			int count = 0;

			boolean result = false;

			switch (CN) {
			case DataType.RTDATA:// 原始数据周期

				beginDate = sitePeriod.getrDataTime();
				interval = sitePeriod.getrInterval();
				count = sitePeriod.getrCount();

				if (beginDate == null || beginDate.getTime() < endDate.getTime()) {
					sitePeriod.setrDataTime(endDate);
				}

				break;
			case DataType.MINUTEDATA:// 分钟数据周期

				beginDate = sitePeriod.getmDataTime();
				interval = sitePeriod.getmInterval() * 60;
				count = sitePeriod.getmCount();

				if (beginDate == null || beginDate.getTime() < endDate.getTime()) {
					sitePeriod.setmDataTime(endDate);
				}

				break;
			case DataType.HOURDATA:// 小时数据周期

				beginDate = sitePeriod.gethDataTime();
				interval = sitePeriod.gethInterval() * 3600;

				count = sitePeriod.gethCount();

				if (beginDate == null || beginDate.getTime() < endDate.getTime()) {
					sitePeriod.sethDataTime(endDate);
				}
				break;
			}

			if (beginDate != null && beginDate.getTime() < endDate.getTime()) {

				diff = DateUtil.dateDiffSecond(beginDate, endDate);

				int oldCount = count;

				// 站点周期>平台周期，且持续几个周期告警
				if (diff > interval) {

					if (AasService.FREQUENCYNUM < count) {
						result = true;
					} else {
						count++;
					}

				} else if (diff == interval) {
					// 站点周期 =平台周期 消除告警
					result = false;
					count = 0;
				} else {
					// 站点周期<平台周期
					count = 0;
					result = true;
				}

				String dataType = DataType.T2011;
				switch (CN) {
				case DataType.RTDATA:// 原始数据周期
					sitePeriod.setrDataTime(endDate);

					if (count > oldCount) {
						sitePeriod.setrCount(count);
					}
					dataType = DataType.T2011;
					break;
				case DataType.MINUTEDATA:// 分钟数据周期
					sitePeriod.setmDataTime(endDate);
					if (count > oldCount) {
						sitePeriod.setrCount(count);
					}
					dataType = DataType.T2051;
					break;
				case DataType.HOURDATA:// 小时数据周期
					sitePeriod.sethDataTime(endDate);
					if (count > oldCount) {
						sitePeriod.setrCount(count);
					}
					dataType = DataType.T2061;
					break;
				}

				generationAlarm(result, pointCode, endDate, dataType);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

	/**
	 * disposeAlarm:处理相关告警 void polluteCode
	 */
	private void generationAlarm(boolean result, String pointCode, Date dateTime, String dataType) {

		try {
			// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
			String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, AasService.FREQUENCYCODE, polluteCode, dataType);

			// 根据规则id 获取内存告警数据
			AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

			if (result) {// 产生对应告警
				// 符合规则，内存中不存在告警数据
				if (alarm == null) {

					alarm = new AlarmInfoVO();
					alarm.setAlarmCode(AasService.FREQUENCYCODE);
					alarm.setStartTime(dateTime);
					alarm.setPointCode(pointCode);
					alarm.setPolluteCode(polluteCode);
					alarm.setAlarmType(ALARMTYPE);
					alarm.setDataType(dataType);
					alarm.setStorage(true);
					alarm.setNewAlarm(true);
					AasService.alarmMap.put(alarmId, alarm);
				}
				String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);

				publish.send(Constant.MQ_QUEUE_ALARM, json);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
				String info = String.format("站点:%s ---传输频率(%s)[告警]---%s", pointCode, dataType,
						DateUtil.dateToStr(dateTime, "yyyy-MM-dd HH:mm:ss"));
				logger.info(info);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

			} else {// 消除告警

				// 分析结果不符合规则，且内存存在告警数据
				if (alarm != null && alarm.getStartTime().getTime() < dateTime.getTime()) {

					alarm.setNewAlarm(false);
					alarm.setEndTime(dateTime);

					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);

					publish.send(Constant.MQ_QUEUE_ALARM, json);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));

					String info = String.format("站点:%s ---传输频率(%s)[消除]---%s", pointCode, dataType,
							DateUtil.dateToStr(dateTime, "yyyy-MM-dd HH:mm:ss"));
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

					// 删除告警内存数据
					AasService.alarmMap.remove(alarmId);

				}
			}

		} catch (Exception e) {		
			logger.error("",e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

}
