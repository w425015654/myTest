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

import java.util.Date;

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
@Component("ymStatisticsJob")
public class YMStatisticsJob {

	@Autowired
	YMDataStatistical ymDataStatistical;
	@Autowired
	O38Statistics o38Statistics;

	private static Logger logger = LoggerFactory.getLogger(YMStatisticsJob.class);

	// @Scheduled(cron = "0/30 * * * * ?")

	// 每月1号的01:00触发
	@Scheduled(cron = "0 0 1 1 * *")
	public void jobHandler() {
		try {

			logger.info("执行月数据统计排程！");
			for (StationVO station : DssService.stationMap.values()) {

				try {
					Date dateTime = DateUtil.dateAddMonth(DateUtil.getCurrentDate(), -1);

					String ST = station.getST();

					// 月数据统计
					ymDataStatistical.statisticalHandler(station, DataType.MONTHDATA, dateTime);

					if (SystemTypeCode.AIR.equals(ST)) {
						// O38小时滑动均值统计
						o38Statistics.statisticsHandler(station, DataType.MONTHDATA, dateTime, "0");
					}

					int month = DateUtil.getMonth(dateTime);

					// 年数据统计
					if (month == 1) {
						ymDataStatistical.statisticalHandler(station, DataType.YEARDATA,
								DateUtil.dateAddYear(dateTime, -1));

						if (SystemTypeCode.AIR.equals(ST)) {
							// O38小时滑动均值统计
							o38Statistics.statisticsHandler(station, DataType.YEARDATA,
									DateUtil.dateAddYear(dateTime, -1), "0");
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

}
