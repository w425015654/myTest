/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：ApsService.java
* 包  名  称：com.zeei.das.aps
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月28日下午2:16:26
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月28日下午2:16:26 创建文件
*
*/

package com.zeei.das.aps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.aps.mq.Publish;
import com.zeei.das.aps.service.NoticeRuleService;
import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.NoticeRuleVO;
import com.zeei.das.aps.vo.NoticeUserVO;
import com.zeei.das.aps.vo.PolluteVO;
import com.zeei.das.aps.vo.StationVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：ApsService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class ApsService {

	private static Logger logger = LoggerFactory.getLogger(ApsService.class);

	// 系统配置信息
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	// 告警通知规则配置信息
	public static List<NoticeRuleVO> noticeRule = new ArrayList<NoticeRuleVO>();

	public static List<NoticeUserVO> noticeUser = new ArrayList<NoticeUserVO>();

	public static Map<String, StationVO> stationMap = new ConcurrentHashMap<String, StationVO>();

	public static Map<String, AlarmDefVO> alarmDefMap = new ConcurrentHashMap<String, AlarmDefVO>();

	public static Map<String, PolluteVO> polluteMap = new ConcurrentHashMap<String, PolluteVO>();

	// 是否进行告警同步
	public static String alarmSync = "0";

	@Autowired
	NoticeRuleService noticeRuleService;

	@Autowired
	Publish publish;

	ScheduledExecutorService service = Executors.newScheduledThreadPool(4);

	static {
		cfgMap = LoadConfigUtil.readXmlParam("aps");
		if (!StringUtil.isEmptyOrNull(cfgMap.get("alarmSync"))) {
			alarmSync = cfgMap.get("alarmSync");
		}
	}

	@PostConstruct
	public void initConfig() {
		try {

			logger.info("開始初始化配置");
			// 初始化通知规则配置信息
			initNoticeRule(1);
			initStationCfg(1);
			initAlarmDef(1);
			initPollute(1);
			logger.info("結束初始化配置");
		} catch (Exception e) {
			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

	/**
	 * 
	 * initNoticeRule:初始化通知规则配置信息 void
	 */
	public void initNoticeRule(final int loop) {

		try {

			// 获取通知配置信息
			noticeRule.clear();
			noticeRule = noticeRuleService.queryNoticeRule();
			// 获取通知配置-人员信息
			noticeUser.clear();
			noticeUser = noticeRuleService.queryNoticeRuleUser();

		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initNoticeRule(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化通知规则配置信息");
	}

	/**
	 * 
	 * initStationCfg:初始化站点配置
	 */
	public void initStationCfg(final int loop) {

		try {

			stationMap.clear();
			// 获取通知配置信息
			List<StationVO> list = noticeRuleService.queryStations();

			if (list != null) {

				for (StationVO vo : list) {

					String pointCode = vo.getPointCode();

					if (!StringUtil.isEmptyOrNull(pointCode)) {
						stationMap.put(pointCode, vo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initStationCfg(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

		logger.info("初始化站點配置:" + JSON.toJSONString(stationMap));
	}

	/**
	 * 
	 * initAlarmDef:初始化告警定义
	 */
	public void initAlarmDef(final int loop) {
		try {
			alarmDefMap.clear();
			// 获取通知配置信息
			List<AlarmDefVO> list = noticeRuleService.queryAlarmDef();

			if (list != null) {

				for (AlarmDefVO vo : list) {

					String alarmCode = vo.getAlarmCode();

					if (!StringUtil.isEmptyOrNull(alarmCode)) {
						alarmDefMap.put(alarmCode, vo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initAlarmDef(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化告警定義:" + JSON.toJSONString(alarmDefMap));
	}

	/**
	 * 
	 * initPollute:初始化因子信息
	 */
	public void initPollute(final int loop) {

		try {
			polluteMap.clear();
			// 获取通知配置信息
			List<PolluteVO> list = noticeRuleService.queryPollutes();

			if (list != null) {

				for (PolluteVO vo : list) {

					String polluteCode = vo.getPolluteCode();

					if (!StringUtil.isEmptyOrNull(polluteCode)) {
						polluteMap.put(polluteCode, vo);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPollute(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化因子:" + JSON.toJSONString(polluteMap));
	}

}
