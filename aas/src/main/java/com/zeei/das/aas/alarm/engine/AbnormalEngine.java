/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AbnormalEngine.java
* 包  名  称：com.zeei.das.aas.alarm.engine
* 文件描述：数据异常告警采集
* 创建日期：2017年4月21日上午8:40:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日上午8:40:39 创建文件
*
*/

package com.zeei.das.aas.alarm.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.Alarm;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.alarm.ParamUtil;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.aas.vo.RegionDataVO;
import com.zeei.das.aas.vo.RuleVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.Md5Util;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：AbnormalEngine 类 描 述：数据异常告警采集 功能描述：数据异常告警采集 创建作者：quanhongsheng
 */

@Component("abnormalEngine")
public class AbnormalEngine implements RuleEngine {

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(Alarm.class);

	@Override
	public int analysis(AlarmRuleVO rule, JSONArray params) {

		try {
			String MN = rule.getMN();

			Date dataTime = rule.getDataTime();
			String polluteCode = rule.getPolluteCode();
			String pointCode = rule.getPointCode();

			String expression = rule.getFormula();

			if (StringUtil.isEmptyOrNull(expression)) {
				logger.warn(String.format("站点：%s 数据异常告警异常倍数为空！", rule.getPointCode()));
				return 2;
			}

			// 获取内存站点配置信息
			StationVO station = AasService.stationMap.get(MN);

			// 区域编码
			String regionCode = "";

			if (station != null) {
				regionCode = station.getRegionCode();
			} else {
				return 2;
			}

			if (StringUtil.isEmptyOrNull(regionCode)) {
				return 2;
			}

			// 获取内存区域站点统计数据
			RegionDataVO region = AasService.regionMap.get(regionCode);

			if (region == null || region.getTotal() < 3) {
				return 2;
			}

			// 数据时间规整到小时
			String regularDate = DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss");

			// 生成规则id ,区域ID和因子， 规整后的时间进行MD5计算
			String ruleId = Md5Util.getMd5(String.format("%s%s%s", regionCode, polluteCode, regularDate));

			// 获取内存区域站点监测数据
			RegionDataVO regionData = AasService.regionDataMap.get(ruleId);

			// 内存监测数据为空，区域站点个数大于2
			if (regionData == null) {
				regionData = new RegionDataVO();
				regionData.setDataTime(regularDate);
				regionData.setTotal(region.getTotal());
				regionData.setRegionCode(regionCode);
				AasService.regionDataMap.put(ruleId, regionData);
			}

			// 站点监测数据
			Map<String, Double> stationData = regionData.getStations();

			if (stationData == null) {

				stationData = new HashMap<String, Double>();
				regionData.setStations(stationData);

			}

			List<Double> kv = regionData.getParams();

			if (kv == null) {
				kv = new ArrayList<Double>();
				regionData.setParams(kv);
			}

			// 判断站点数据是否存在，存在则丢失

			JSONObject param = ParamUtil.findParam(polluteCode, params);

			if (param != null) {
				Double value = 0d;
				if (DataType.T2011.equals(rule.getDataType())) {
					value = param.getDouble("Rtd");
				} else {
					value = param.getDouble("Avg");
				}
				stationData.put(pointCode, value);

				kv.add(value);

			}

			String dataType = rule.getDataType();
			// 进行告警分析
			generationAlarm(rule.getAlarmCode(), ruleId, dataType, polluteCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 2;
	}

	/**
	 * 
	 * generationAlarm:数据异常告警生成
	 *
	 * @param alarmCode
	 *            告警码
	 * @param id
	 *            区域内存数据ID
	 *
	 */
	private void generationAlarm(String alarmCode, String id, String dataType, String polluteCode) {

		try {

			RegionDataVO data = AasService.regionDataMap.get(id);

			if (data != null) {

				// 区域站点总数
				int total = 3;

				// 当前时段已上传数据的站点数
				int count = data.getStations().size();

				String regionCode = data.getRegionCode();

				if (StringUtil.isEmptyOrNull(regionCode)) {
					return;
				}

				// 获取区域站点总数
				RegionDataVO region = AasService.regionMap.get(regionCode);

				if (region != null) {
					total = region.getTotal();
				}

				Date dataTime = DateUtil.strToDate(data.getDataTime(), "yyyy-MM-dd HH:mm:ss");

				// 数据驻留内存时间，单位小时
				long hours = DateUtil.dateDiffHour(dataTime, new Date());

				// 区域监测点数据全部上报，或者数据驻留时间大于48小时且上报站点个数大于2 ，进行异常数据告警分析
				if (total == count || (hours > 48 && count > 2)) {

					List<Double> taotalMap = data.getParams();
					Double ess = taotalMap.stream().mapToDouble(e -> new Double(e)).sum();

					for (String pointCode : data.getStations().keySet()) {

						// 当期监测值
						Double value = data.getStations().get(pointCode);

						// 区域均值，除本站点外的所有站点算术均值
						Double avg = (ess - value) / (count - 1);

						// 告警规则ID， 站点编码+告警码+因子编码 取md5值
						String ruleId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);

						// 根据站点编码和告警规则id 获取规则信息
						AlarmRuleVO rule = findAlarmRule(pointCode, ruleId);

						// 根据规则ID ，获取内存告警信息
						AlarmInfoVO alarm = AasService.alarmMap.get(ruleId);

						// 异常倍数
						int multiple = 2;

						// 默认持续时间
						int defalutDurTime = 0;

						if (rule != null) {
							defalutDurTime = rule.getDurTime();
							multiple = Integer.valueOf(rule.getFormula());
						}

						if (value > avg * multiple) {

							if (alarm == null) {

								alarm = new AlarmInfoVO();
								alarm.setAlarmCode(alarmCode);
								alarm.setAlarmValue(value.toString());
								alarm.setPointCode(pointCode);
								alarm.setDataType(DataType.T2061);
								alarm.setPolluteCode(polluteCode);
								alarm.setStartTime(dataTime);
								alarm.setAlarmType(rule.getAlarmType());
								alarm.setStorage(false);
								AasService.alarmMap.put(ruleId, alarm);

								String info = String.format("站点:%s 异常数据告警[%s|%s]---%s", pointCode, alarmCode,
										polluteCode, DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
								logger.info(info);
								publish.send(Constant.MQ_QUEUE_LOGS,
										LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
							}

							Date alarmDate = alarm.getStartTime();
							long durTime = DateUtil.dateDiffMin(dataTime, alarmDate);

							if (alarm.isStorage()) {
								alarm.setEndTime(dataTime);
								continue;
							}

							if (defalutDurTime <= durTime) {
								alarm.setStorage(true);
								String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
										SerializerFeature.WriteDateUseDateFormat);

								if (!StringUtil.isEmptyOrNull(alarmCode)) {
									publish.send(Constant.MQ_QUEUE_ALARM, json);
								}

								publish.send(Constant.MQ_QUEUE_LOGS,
										LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));

								String info = String.format("站点:%s 异常数据告警入库[%s|%s]---%s", pointCode, alarmCode,
										polluteCode, DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
								logger.info(info);
								publish.send(Constant.MQ_QUEUE_LOGS,
										LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
							}

						} else {
							if (alarm != null && alarm.isStorage()
									&& alarm.getStartTime().getTime() < dataTime.getTime()) {
								alarm.setNewAlarm(false);

								if (alarm.getEndTime() == null) {
									alarm.setEndTime(dataTime);
								}

								String json = JSON.toJSONStringWithDateFormat(alarm, "yyyy-MM-dd HH:mm:ss",
										SerializerFeature.WriteDateUseDateFormat);

								if (!StringUtil.isEmptyOrNull(alarmCode)) {
									publish.send(Constant.MQ_QUEUE_ALARM, json);
								}

								publish.send(Constant.MQ_QUEUE_LOGS,
										LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
							}

							String info = String.format("站点:%s 异常数据告警消除[%s|%s]---%s", pointCode, alarmCode, polluteCode,
									DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
							logger.info(info);
							publish.send(Constant.MQ_QUEUE_LOGS,
									LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
							AasService.alarmMap.remove(ruleId);
						}
					}

					AasService.regionDataMap.remove(id);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * findAlarmRule:查询内存告警规则
	 *
	 * @param pointCode
	 *            站点Id
	 * @param ruleId
	 *            规则ID
	 * @return AlarmRuleVO
	 */
	private AlarmRuleVO findAlarmRule(String pointCode, String ruleId) {

		String MN = "";

		AlarmRuleVO alarmRule = null;

		for (StationVO station : AasService.stationMap.values()) {
			if (pointCode.equalsIgnoreCase(station.getPointCode())) {
				MN = station.getMN();
			}
		}

		if (!StringUtil.isEmptyOrNull(MN)) {

			RuleVO rule = AasService.ruleMap.get(MN);
			if (rule != null) {
				List<AlarmRuleVO> alramRules = rule.getR2061();

				if (alramRules != null) {
					for (AlarmRuleVO tempAlramRule : alramRules) {
						if (ruleId.equalsIgnoreCase(tempAlramRule.getRuleId())) {
							alarmRule = tempAlramRule;
						}
					}
				}
			}
		}
		return alarmRule;
	}

}
