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

import java.text.DecimalFormat;

/**
 * @类型名称：FlowAlarm
 * @类型描述：
 * @功能描述：
 * @创建作者：lian.wei
 */

import java.util.Date;
import java.util.Map.Entry;

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
import com.zeei.das.aas.vo.FlowFactorVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * @类型名称：日流量异常告警 @功能描述：
 * @创建作者：wudahe
 *
 */
@Component("flowAlarm")
public class FlowAlarm {

	private static Logger logger = LoggerFactory.getLogger(FlowAlarm.class);

	@Autowired
	Publish publish;

	static String alarmType = "8"; // 异常数据
	static Double bFlow = 0.0; // 上次的流量

	public void alarmHandler(JSONObject data) {

		try {
			if ((AasService.flowCode != null) && (AasService.range > 0)) {
				String MN = data.getString("MN");
				String CN = data.getString("CN");
				String ST = data.getString("ST"); // 流量因子，一个ST配置一个，不用累加
				
				// 流量告警只针对日数据
				if (!DataType.DAYDATA.equals(CN)) {
					return;
				}

				// 没有配置流量因子
				FlowFactorVO vo = findFlowFactorVO(ST);
				if (vo == null) {
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
				
				double flow = 0;
				String polluteCode = "";

				boolean isFind = false;
				
				for (Object p : params) {

					JSONObject param = (JSONObject) p;
					String tmpPolluteCode = param.getString("ParamID");

					if (vo.getPolluteCode().contains(tmpPolluteCode)) {
						// 流量
						flow = param.getDouble("Avg");
						polluteCode = tmpPolluteCode;
						isFind = true;
						break;
					}
				}

				if (!isFind)
				{
					return;
				}
				
				String pointCode = "";

				StationVO station = AasService.stationMap.get(MN);

				if (station != null) {
					pointCode = station.getPointCode();
				}

				int isAlarm = 0;

				boolean result = isFlowAlarm(MN, CN, polluteCode, flow, dataTime);

				if (result) {
					isAlarm = 1;
				}

				// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
				String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, AasService.flowCode, polluteCode,
						DataType.DAYDATA);

				AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

				// 告警状态 {正常0 告警1}
				if (isAlarm == 1 && alarm == null) {
					alarm = new AlarmInfoVO();
					alarm.setAlarmCode(AasService.flowCode);
					alarm.setDataType(DataType.DAYDATA);
					alarm.setStartTime(dataTime);
					alarm.setStorage(true);
					alarm.setNewAlarm(true);
					alarm.setAlarmValue(String.valueOf(flow));
					alarm.setPointCode(pointCode);
					alarm.setPolluteCode(polluteCode);
					alarm.setAlarmType(alarmType);
					AasService.alarmMap.put(alarmId, alarm);

					String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);

					publish.send(Constant.MQ_QUEUE_ALARM, json);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
					String info = String.format("站点:%s 日流量与上一日流量进行对比，超过设置的门限[告警]---%s", pointCode,
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
					String info = String.format("站点:%s 日流量与上一日流量进行对比，超过设置的门限[消警]---%s", pointCode,
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

	public static boolean isFlowAlarm(String mn, String cn, String polluteCode, double now, Date dataTime) {
		boolean result = false;

		String key = mn + cn + polluteCode;
		FlowFactorVO vo = findFlowFactorMap(key);
		if (vo == null) { // 第一次
			FlowFactorVO tmp = new FlowFactorVO();
			tmp.setBeforFlow(now);
			tmp.setPolluteCode(polluteCode);
			tmp.setBeforDate(dataTime);
			AasService.mapFlow.put(key, tmp);
			result = false;
		} else {
			Double bFlow = vo.getBeforFlow();
			Date bDate = vo.getBeforDate();
			
			// 振幅
			if (AasService.range > 0) {
				// 相隔一天，不考虑补数
				if ((dataTime.getTime() > bDate.getTime()) && (DateUtil.dateDiffDay(bDate, dataTime) == 1)) {
					result = compareData(now, bFlow);
				}
			} else {
				result = false;
			}
			vo.setBeforFlow(now); // 更新
			vo.setBeforDate(dataTime);
		}
		return result;
	}

	public static FlowFactorVO findFlowFactorVO(String ST) {
		if ((AasService.flowFactor != null) && (AasService.flowFactor.size() > 0)) {
			for (Entry<String, FlowFactorVO> it : AasService.flowFactor.entrySet()) {
				if (it.getKey().equals(ST)) {
					return it.getValue();
				}
			}
		}
		return null;
	}

	public static FlowFactorVO findFlowFactorMap(String pointCode) {
		if ((AasService.mapFlow != null) && (AasService.mapFlow.size() > 0)) {
			for (Entry<String, FlowFactorVO> it : AasService.mapFlow.entrySet()) {
				if (it.getKey().equals(pointCode)) {
					return it.getValue();
				}
			}
		}
		return null;
	}

	public static boolean compareData(double now, double before) {
		if (before == 0) {
			return false;
		}

		double minus = Math.abs(now - before);
		double persent = minus / before;

		if (persent > AasService.range) {
			return true;
		} else {
			return false;
		}
	}
}
