/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：GKStatisticsJob.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月24日上午9:59:07
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月24日上午9:59:07 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：GKStatisticsJob 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Component("gkStatisticsJob")
public class GKStatisticsJob {

	@Autowired
	AlarmTimeLengthStatistical alarmTimeLengthStatistical;

	@Autowired
	RuntimeLengthStatistical runtimeLengthStatistical;

	@Autowired
	AutoDataHandle autoDataHandle;

	private static Logger logger = LoggerFactory.getLogger(GKStatisticsJob.class);

	// 启动时执行一次，每天00:15执行
	@Scheduled(cron = "0 15 0 ? * *")
	public void jobHandler() {
		try {

			logger.info("执行工况时长统计排程!");

			List<String> STL = Arrays.asList(DssService.STLs.split(","));

			for (StationVO station : DssService.stationMap.values()) {

				String ST = station.getST();
				Date dateTime = DateUtil.dateAddDay(DateUtil.getCurrentDate(), -1);

				if (STL.contains(ST)) {

					// 统计网络异常时长
					alarmTimeLengthStatistical.statisticalHandler(station, dateTime, DataStatisticsType.DST_GK_Network);

					// 统计停运时长
					alarmTimeLengthStatistical.statisticalHandler(station, dateTime, DataStatisticsType.DST_GK_Outage);

					// 统计超标时长
					alarmTimeLengthStatistical.statisticalHandler(station, dateTime,
							DataStatisticsType.DST_GK_Overproof);

					// 统计异常参数时长
					alarmTimeLengthStatistical.statisticalHandler(station, dateTime,
							DataStatisticsType.DST_GK_ParamException);

					// 统计设备运行时长
					runtimeLengthStatistical.statisticalHandler(station, dateTime);
				}				
			}

		} catch (Exception e) {
			logger.error("执行工况时长统计排程异常:", e);
		}
	}

}
