/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：GKCancelAlarm.java
* 包  名  称：com.zeei.das.aas.alarm.custom
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年3月28日下午12:29:47
* 
* 修改历史
* 1.0 luoxianglin 2018年3月28日下午12:29:47 创建文件
*
*/

package com.zeei.das.aas.alarm.custom;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.alarm.ExcludeTimeHandler;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类型名称：GKCancelAlarm 类型描述：TODO 请修改类型描述 功能描述：TODO 请修改功能描述 创建作者：quan.hongsheng
 *
 */
@Component("gkCancelAlarm")
public class GKCancelAlarm {

	@Autowired
	ExcludeTimeHandler excludeTimeHandler;

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(GKCancelAlarm.class);

	public void alarmHandler() {

		try {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						cancelAlarm();
					} catch (Exception e) {
						logger.error("", e);
					}
				}
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleAtFixedRate(runnable, 0, 5, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancelAlarm() {

		if (!AasService.alarmRules.isEmpty()) {

			List<AlarmRuleVO> rs = AasService.alarmRules.values().stream()
					.filter(o -> o.getIsEffect() == 1 && o.getIsGenAlarm() == 0).collect(Collectors.toList());

			if (rs != null && rs.size() > 0) {

				for (AlarmRuleVO r : rs) {

					try {

						String dataType = r.getDataType();
						String pointCode = r.getPointCode();
						String alarmCode = r.getAlarmCode();
						String polluteCode = r.getPolluteCode();

						// 根据站点ID，告警码和因子ID，取md5 作为规则的ID

						String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);

						if (AasService.alarmMap.containsKey(alarmId)) {

							Date dataTime = DateUtil.getCurrentDate();
							// 判断告警时间是否在规律性停产和异常申报时段内
							boolean isExclude = excludeTimeHandler.excludeTime(pointCode, dataTime);

							if (isExclude) {

								// 根据规则id 获取内存告警数据
								AlarmInfoVO alarm = AasService.alarmMap.get(alarmId);

								// 分析结果不符合规则，且内存存在告警数据
								if (alarm != null && alarm.getStartTime().getTime() < dataTime.getTime()) {
									r.setOut(false);
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

										publish.send(Constant.MQ_QUEUE_LOGS,
												LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
									}

									String info = String.format("站点:%s 规律性停产消警[%s|%s](%s)---%s", pointCode,
											r.getAlarmCode(), r.getPolluteCode(), r.getFormula(),
											DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss"));
									logger.info(info);
									publish.send(Constant.MQ_QUEUE_LOGS,
											LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));

									// 删除告警内存数据
									AasService.alarmMap.remove(alarmId);
								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
