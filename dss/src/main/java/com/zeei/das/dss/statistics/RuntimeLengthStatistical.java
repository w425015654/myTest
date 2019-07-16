/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AlarmTimeLengthStatistical.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月21日上午11:26:18
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月21日上午11:26:18 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：AlarmTimeLengthStatistical 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class RuntimeLengthStatistical {

	@Autowired
	QueryDataService dataService;

	@Autowired
	StroageService stroageService;

	private static Logger logger = LoggerFactory.getLogger(RuntimeLengthStatistical.class);

	/**
	 * 
	 * statisticalHandler:根据告警记录进行统计
	 *
	 * @param station
	 *            测点对象
	 * @param dataTime
	 *            统计时间 单位是天
	 * @param CN
	 *            系统类型
	 * @param dsType
	 *            统计类型
	 * 
	 *            void
	 */
	public void statisticalHandler(StationVO station, Date dataTime) {

		try {

			String pointCode = station.getPointCode();

			String ST = station.getST();

			String tableName = PartitionTableUtil.getTableName(ST, DataType.RTDATA, pointCode, dataTime);

			if (StringUtil.isEmptyOrNull(tableName)) {
				return;
			}

			String date = DateUtil.dateToStr(dataTime, "yyyy-MM-dd");

			String bTime = String.format("%s 00:00:00", date);
			String eTime = String.format("%s 23:59:59", date);

			List<MonitorDataVO> metadata = dataService.queryRCDataByCondition(tableName, pointCode, bTime, eTime);

			if (metadata != null) {
				statisticalTime(metadata, station, dataTime);
			}

		} catch (Exception e) {
			logger.error("统计设备运行时长异常:" + e.toString());
		}

	}

	/**
	 * 
	 * statisticalTime:根据原始数据，统计时长
	 *
	 * @param alrams
	 *            要统计的原始数据列表
	 * @param pulloteCode
	 *            虚拟因子编码
	 * @param station
	 *            测点对象
	 * @param dataTime
	 *            统计时间
	 * 
	 *            void
	 */
	private void statisticalTime(List<MonitorDataVO> metadata, StationVO station, Date dataTime) {

		try {
			String pointCode = station.getPointCode();

			String ST = station.getST();
			String CN = DataType.DAYDATA;
			String MN = station.getMN();
			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			if (StringUtil.isEmptyOrNull(tableName)) {
				return;
			}

			Date pTime = null;
			Date bTime = null;

			double second = 0;

			int interval = station.getrInterval();

			for (int i = 0; i < metadata.size(); i++) {

				MonitorDataVO vo = metadata.get(i);

				if (i == 0) {
					pTime = DateUtil.strToDate(vo.getDataTime(), "yyyy-MM-dd HH:mm:ss");
					bTime = DateUtil.strToDate(vo.getDataTime(), "yyyy-MM-dd HH:mm:ss");
					continue;
				}

				Date cTime = DateUtil.strToDate(vo.getDataTime(), "yyyy-MM-dd HH:mm:ss");

				if (DateUtil.dateDiffSecond(pTime, cTime) > interval) {
					second += DateUtil.dateDiffSecond(bTime, pTime) + interval;
					pTime = cTime;
					bTime = cTime;
					continue;
				}

				if (i == metadata.size() - 1) {
					second += DateUtil.dateDiffSecond(bTime, cTime) + interval;
				}

				pTime = cTime;
			}

			Double hours = second / 60 / 60;

			hours = StatisticalHelper.dataRounding(hours, 2);

			if (hours > 24) {
				hours = 24d;
			}

			Date minTime = RegularTime.regular(CN, MN, dataTime);

			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(DateUtil.dateToStr(minTime, "yyyy-MM-dd HH:mm:ss"));
			vo.setDataValue(hours);
			vo.setMaxValue(hours);
			vo.setMinValue(hours);
			vo.setAuditValue(hours);
			vo.setPolluteCode(SpecialFactor.GKRuntimeCode);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo.setPointCode(pointCode);

			if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
				stroageService.insertYMData(tableName, vo);
			} else {
				stroageService.insertData(tableName, vo);
			}

			logger.info("运行时长：" + JSON.toJSONString(vo));
		} catch (Exception e) {
			logger.error("运行时长:", e);
		}

	}

}
