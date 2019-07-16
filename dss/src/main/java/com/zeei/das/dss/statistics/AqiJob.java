/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：YMStatisticsJob.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月8日上午8:29:00
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月8日上午8:29:00 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：YMStatisticsJob 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component("aqiJob")
public class AqiJob {

	@Autowired
	AQIStatistical aqiStatistical;

	private static Logger logger = LoggerFactory.getLogger(AqiJob.class);

	// 微站，即参与分钟超标事件统计的站点
	private static List<String> statisPoint = Arrays.asList(new String[] { "kzjb08", "kzjb09" });

	// 每个小时的第五分钟触发统计
	@Scheduled(cron = "0 5 * * * ?")
	public void jobHandler() {
		try {

			logger.info("执行小时aqi数据统计排程！");
					
			Date dataTime = DateUtil.strToDate(DateUtil.getCurrentDate("yyyy-MM-dd HH") + ":00:00",
					"yyyy-MM-dd HH:mm:ss");
			for (StationVO station : DssService.stationMap.values()) {

				try {					
					
					if (SystemTypeCode.AIR.equals(station.getST()) && station.getIsRqc() == 1) {
												
						aqiStatistical.statisticalHandler(station, dataTime, DataType.HOURDATA, 0);
						// 空气站配置了自动审核数据，则无法通过页面下发审核数据的AQI统计指令，需要在这里统计
						if (DssService.STSL.contains(station.getST()) && "1".equals(DssService.autoAudit)) {
							// 审核数据的AQI统计
							aqiStatistical.statisticalHandler(station, dataTime, DataType.HOURDATA, 1);
						}
					}

				} catch (Exception e) {
					logger.error("", e);
				}
			}
			

		} catch (Exception e) {
			logger.error("", e);
		}
	}

	// 每天的02:00触发
	@Scheduled(cron = "0 0 2 * * ?")
	public void dayAQIHandler() {
		try {

			logger.info("执行日aqi数据统计排程！");
			for (StationVO station : DssService.stationMap.values()) {

				try {
					if (SystemTypeCode.AIR.equals(station.getST())) {
						Date dataTime = DateUtil.strToDate(DateUtil.getCurrentDate("yyyy-MM-dd ") + "00:00:00",
								"yyyy-MM-dd HH:mm:ss");
						aqiStatistical.statisticalHandler(station, dataTime, DataType.DAYDATA, 0);
						// 空气站配置了自动审核数据，则无法通过页面下发审核数据的AQI统计指令，需要在这里统计
						if (DssService.STSL.contains(station.getST()) && "1".equals(DssService.autoAudit)) {
							// 审核数据的AQI统计
							aqiStatistical.statisticalHandler(station, dataTime, DataType.DAYDATA, 1);
						}
					}

				} catch (Exception e) {
					logger.error(e.toString());
				}
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	// 每小时的4,19,34,49分钟触发
	@Scheduled(cron = "0 4,19,34,49 * * * ? ")
	public void minAQIHandler() {
		try {

			if ("1".equals(DssService.cfgMap.get("ISPI"))) {

				List<String> pointCodes = new ArrayList<>();
				// 获取所有空气站
				for (StationVO station : DssService.stationMap.values()) {
					if (SystemTypeCode.AIR.equals(station.getST()) && StringUtils.isNotBlank(station.getPointCode())
							&& StringUtils.isNotBlank(station.getControlLevel())
							&& statisPoint.contains(station.getControlLevel().toLowerCase())) {
						pointCodes.add(station.getPointCode());
					}
				}
				Date dataTime = DateUtil.dateAddMin(DateUtil.getCurrentDate(), -4);
				aqiStatistical.statisMinHandler(pointCodes, dataTime);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
