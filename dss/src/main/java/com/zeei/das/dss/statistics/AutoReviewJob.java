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
import org.springframework.stereotype.Component;

import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.StationVO;

/**
 * @类 名 称：AutoReviewJob
 * @类 描 述：自动复核
 * @功能描述：自动复核
 * @创建作者：quanhongsheng
 */
@Component("autoReviewJob")
public class AutoReviewJob {

	@Autowired
	StroageService stroageService;

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	Publish publish;

	@Autowired
	AutoDataHandle autoDataHandle;

	private static Logger logger = LoggerFactory.getLogger(AutoReviewJob.class);

	// 每10分钟执行
	// @Scheduled(cron = "0 0/10 * * * ?")
	public void jobHandler() {
		logger.info(String.format("开始自动审核...,%s-%s  %s", DssService.autoAudit, DssService.autoReview, DssService.STSL));
		for (StationVO station : DssService.stationMap.values()) {

			try {
				Date dateTime = DateUtil.getCurrentDate();

				String MN = station.getMN();
				String pointCode = station.getPointCode();
				String ST = station.getST();

				if (DssService.STSL.contains(ST)) {

					if ("1".equals(DssService.autoAudit)) {
						autoDataHandle.autoAudit(MN, pointCode, dateTime, ST);
					}

					if ("1".equals(DssService.autoReview)) {
						autoDataHandle.autoReview(MN, pointCode, dateTime, ST);
					}

				}

			} catch (Exception e) {
				logger.error(e.toString());
			}
		}

	}

}
