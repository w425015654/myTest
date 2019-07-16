/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeHandler.java
* 包  名  称：com.zeei.das.aps.notice
* 文件描述：告警通知处理类
* 创建日期：2017年5月2日上午10:58:07
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午10:58:07 创建文件
*
*/

package com.zeei.das.aps.notice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aps.ApsService;
import com.zeei.das.aps.alarm.ScanAlarmHandler;
import com.zeei.das.aps.mq.Publish;
import com.zeei.das.aps.service.NoticeRuleService;
import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.NoticeRuleVO;
import com.zeei.das.aps.vo.NoticeUserVO;
import com.zeei.das.aps.vo.NoticeVO;
import com.zeei.das.aps.vo.PolluteVO;
import com.zeei.das.aps.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.NotificationMode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：NoticeHandler 类 描 述：告警通知处理类 功能描述：告警通知处理类 创建作者：quanhongsheng
 */

@Service("noticeHandler")
public class NoticeHandler {
	private static Logger logger = LoggerFactory.getLogger(NoticeHandler.class);

	@Autowired
	NoticeRuleService noticeRuleService;

	@Autowired
	Publish publish;

	public void handler(AlarmInfoVO alarm) {
		if (alarm.getServiceCode() != null) {
			return;
		}

		try {
			String pointCode = String.valueOf(alarm.getPointCode());
			String polluteCode = alarm.getPolluteCode();
			String alarmCode = alarm.getAlarmCode();
			Double alarmValue = alarm.getAlarmValue();
			Date startTime = alarm.getStartTime();

			// logger.info("通知告警:"+JSON.toJSONString(alarm));

			String pointName = "";

			if (!StringUtil.isEmptyOrNull(pointCode)) {

				StationVO station = ApsService.stationMap.get(pointCode);
				if (station != null) {
					pointName = station.getPointName();
				}
			}

			String alarmDesc = "";
			if (!StringUtil.isEmptyOrNull(alarmCode)) {

				AlarmDefVO alarmDef = ApsService.alarmDefMap.get(alarmCode);
				if (alarmDef != null) {
					alarmDesc = alarmDef.getAlarmDesc();
				}
			}

			String polluteName = "";
			if (!StringUtil.isEmptyOrNull(polluteCode)) {

				PolluteVO pollute = ApsService.polluteMap.get(polluteCode);
				if (pollute != null) {
					polluteName = pollute.getPolluteName();
				}
			}

			// 消息内容
			String content = "";

			List<NoticeRuleVO> rules = ApsService.noticeRule.stream()
					.filter(o -> Arrays.asList(o.getAlarmCode().split(",")).contains(alarmCode)
							&& Arrays.asList(o.getPointCode().split(",")).contains(pointCode))
					.collect(Collectors.toList());

			for (NoticeRuleVO rule : rules) {

				if (rule != null) {

					String ruleId = rule.getRuleId();

					List<NoticeUserVO> nruList = ApsService.noticeUser.stream()
							.filter(o -> o.getRuleId().equalsIgnoreCase(ruleId)).collect(Collectors.toList());

					if (nruList != null) {
						for (NoticeUserVO nru : nruList) {

							String noticeType = nru.getNoticeType();

							if (StringUtil.isEmptyOrNull(noticeType)) {
								noticeType = NotificationMode.NOTIFICATION_EMAIL;
							}

							NoticeVO notice = new NoticeVO();

							notice.setNoticeType(noticeType);

							notice.setRuleId(ruleId);

							String address = "";

							switch (noticeType) {
							case NotificationMode.NOTIFICATION_EMAIL:
								address = nru.getEmail();
								break;
							case NotificationMode.NOTIFICATION_PHONE:
								address = nru.getPhone();
								break;
							}

							if (StringUtil.isEmptyOrNull(address)) {
								continue;
							}

							notice.setSendAddress(address);

							content = String.format("%s%s%s", pointName,
									DateUtil.dateToStr(startTime, "yyyy-MM-dd HH:mm:ss"), alarmDesc);

							if (!StringUtil.isEmptyOrNull(polluteName)) {
								content = content + polluteName;
							}

							if (alarmValue != null) {
								content = content + alarmValue;
							}

							notice.setContent(content);

							String title = String.format("%s%s通知", pointName, alarmDesc);
							notice.setTitle(title);

							publish.send(Constant.MQ_QUEUE_NOTICE, JSON.toJSONStringWithDateFormat(notice,
									"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));

							logger.info(String.format("通知消息:[%s-%s] %s", ruleId, address, content));
						}
					}
				}
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	// 给管委会内部人员发短信
	public void scanHandler(AlarmInfoVO alarm) {
		if (alarm.getServiceCode() != null) {
			return;
		}

		try {
			String pointCode = String.valueOf(alarm.getPointCode());
			String polluteCode = alarm.getPolluteCode();
			String alarmCode = alarm.getAlarmCode();
			Double alarmValue = alarm.getAlarmValue();
			Date startTime = alarm.getStartTime();

			// logger.info("通知告警:"+JSON.toJSONString(alarm));

			String pointName = "";

			if (!StringUtil.isEmptyOrNull(pointCode)) {

				StationVO station = ApsService.stationMap.get(pointCode);
				if (station != null) {
					pointName = station.getPointName();
				}
			}

			String alarmDesc = "";
			if (!StringUtil.isEmptyOrNull(alarmCode)) {

				AlarmDefVO alarmDef = ApsService.alarmDefMap.get(alarmCode);
				if (alarmDef != null) {
					alarmDesc = alarmDef.getAlarmDesc();
				}
			}

			String polluteName = "";
			if (!StringUtil.isEmptyOrNull(polluteCode)) {

				PolluteVO pollute = ApsService.polluteMap.get(polluteCode);
				if (pollute != null) {
					polluteName = pollute.getPolluteName();
				}
			}

			// 消息内容
			String content = "";
			logger.info("进入scanHandler: AlarmCode = " + alarmCode + "pointCode = " + pointCode);
			List<NoticeRuleVO> rules = ApsService.noticeRule.stream()
					.filter(o -> Arrays.asList(o.getAlarmCode().split(",")).contains(alarmCode)
							&& Arrays.asList(o.getPointCode().split(",")).contains(pointCode))
					.collect(Collectors.toList());

			for (NoticeRuleVO rule : rules) {

				if (rule != null) {
					logger.info("rule.getRuleId() = " + rule.getRuleId());

					String ruleId = rule.getRuleId();

					String phones = ScanAlarmHandler.scanJson.getString(ruleId);

					if (!StringUtil.isEmptyOrNull(phones)) {

						String[] phoneArr = phones.split(",");

						if (phoneArr != null && phoneArr.length > 0) {

							for (int i = 0; i < phoneArr.length; i++) {

								String phone = phoneArr[i];

								if (StringUtil.isEmptyOrNull(phone)) {
									continue;
								}

								NoticeVO notice = new NoticeVO();

								notice.setNoticeType(NotificationMode.NOTIFICATION_PHONE);

								notice.setRuleId(ruleId);

								notice.setSendAddress(phone);

								content = String.format("%s%s%s", pointName,
										DateUtil.dateToStr(startTime, "yyyy-MM-dd HH:mm:ss"), alarmDesc);

								if (!StringUtil.isEmptyOrNull(polluteName)) {
									content = content + polluteName;
								}

								if (alarmValue != null) {
									content = content + alarmValue;
								}

								notice.setContent(content);

								String title = String.format("%s%s通知", pointName, alarmDesc);
								notice.setTitle(title);

								publish.send(Constant.MQ_QUEUE_NOTICE, JSON.toJSONStringWithDateFormat(notice,
										"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));

								logger.info(String.format("管委会通知消息:[%s-%s] %s", ruleId, phone, content));
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}
}
