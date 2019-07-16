/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：statisticsHanlder.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日下午5:33:25
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日下午5:33:25 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：statisticsHanlder 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class StatisticsHanlder {

	@Autowired
	DataStatistical dataStatistical;

	@Autowired
	ConnectivityStatistics connectivityStatistics;

	@Autowired
	GeneralStatistics generalStatistics;

	@Autowired
	QualityControlStatistics qualityControlStatistics;
	@Autowired
	AlarmTimeLengthStatistical alarmTimeLengthStatistical;
	@Autowired
	RuntimeLengthStatistical runtimeLengthStatistical;
	@Autowired
	AQIStatistical aqiStatistical;
	@Autowired
	YMDataStatistical ymDataStatistical;
	@Autowired
	StatisticalHelper statisticalHelper;
	@Autowired
	O38Statistics o38Statistics;
	@Autowired
	AutoDataHandle autoDataHandle;
	@Autowired
	SKConvert skConvert;

	@Autowired
	AntipateDataStatis antipateDataStatis;

	private static Logger logger = LoggerFactory.getLogger(StatisticsHanlder.class);

	public boolean handler(JSONObject obj) {
		try {

			String CN = obj.getString("CN");
			String MN = obj.getString("MN");
			Date dataTime = obj.getDate("DATATIME");

			// 是否是手動
			String manual = obj.getString("MANUAL");

			if (obj.containsKey("manual")) {
				manual = obj.getString("manual");
			}

			String ST = obj.getString("ST");

			StationVO station = DssService.stationMap.get(MN);

			if (station == null) {

				return true;
			}
			String pointCode = station.getPointCode();
			station.setCN(CN);

			// 数据统计类型
			String DST = obj.getString("DST");

			switch (DST) {
			case DataStatisticsType.DST_20X1G:
			case DataStatisticsType.DST_20X1A:
				switch (CN) {
				case DataType.MONTHDATA:
					// 月统计
					dataTime = obj.getDate("DATATIME");
					ymDataStatistical.statisticalHandler(station, CN, dataTime);
					// 油烟系统 统计连通个数
					connectivityStatistics.statisticalHandler(station, CN, dataTime);

					if (SystemTypeCode.AIR.equals(ST)) {
						// O38小时滑动均值统计
						o38Statistics.statisticsHandler(station, CN, dataTime, "0");
					}

					// 自动审核
					if (DssService.STSL.contains(ST) && "1".equals(DssService.autoAudit)) {
						autoDataHandle.autoAuditData(MN, CN, pointCode, dataTime, ST);
					}

					break;
				case DataType.YEARDATA:
					// 年统计
					dataTime = obj.getDate("DATATIME");
					ymDataStatistical.statisticalHandler(station, CN, dataTime);
					// 油烟系统 统计连通个数
					connectivityStatistics.statisticalHandler(station, CN, dataTime);

					if (SystemTypeCode.AIR.equals(ST)) {
						// O38小时滑动均值统计
						o38Statistics.statisticsHandler(station, CN, dataTime, "0");
					}

					// 自动审核
					if (DssService.STSL.contains(ST) && "1".equals(DssService.autoAudit)) {
						autoDataHandle.autoAuditData(MN, CN, pointCode, dataTime, ST);
					}

					break;
				default:
					// 监测数据分/小时/日 统计
					dataStatistical.statisticalHandler(station, dataTime, DST);
					break;
				}
				break;

			case DataStatisticsType.DST_AQI:
				// 空气监测系统 统计aqi
				dataTime = obj.getDate("DATATIME");
				aqiStatistical.statisticalHandler(station, dataTime, CN, 0);

				// 数据自动审核
				if (DssService.STSL.contains(ST) && DataType.HOURDATA.equals(CN)) {

					if ("1".equals(DssService.autoAudit)) {
						autoDataHandle.autoAudit(MN, pointCode, dataTime, ST);
						if (SystemTypeCode.AIR.equals(ST)) {
							aqiStatistical.statisticalHandler(station, dataTime, CN, 1);
						}
					}
					if ("1".equals(DssService.autoReview)) {
						autoDataHandle.autoReview(MN, pointCode, dataTime, ST);
					}
				}

				break;
			case DataStatisticsType.DST_General:

				dataTime = obj.getDate("DATATIME");

				if (DataType.DAYDATA.equals(CN)) {

					if (SystemTypeCode.AIR.equals(ST)) {
						// O38小时滑动均值统计
						o38Statistics.statisticsHandler(station, CN, dataTime, "0");
					}
					// 日数自动审核
					if (DssService.STSL.contains(ST) && "1".equals(DssService.autoAudit)) {
						autoDataHandle.multiAudit(MN, pointCode, dataTime, ST);
						// 统计质控合格个数
						qualityControlStatistics.statisticalHandler(station, dataTime);

						if (SystemTypeCode.AIR.equalsIgnoreCase(ST)) {
							// 空气监测系统 统计aqi
							aqiStatistical.statisticalHandler(station, dataTime, DataType.DAYDATA, 1);
							// O38小时滑动均值统计
							o38Statistics.statisticsHandler(station, CN, dataTime, "1");
						}
					}
				} else {
					// 数据自动审核
					if (DssService.STSL.contains(ST)) {

						if ("1".equals(DssService.autoAudit)) {

							autoDataHandle.autoAudit(MN, pointCode, dataTime, ST);

							if (SystemTypeCode.AIR.equals(ST)) {
								aqiStatistical.statisticalHandler(station, dataTime, CN, 1);
							}
						}
						if ("1".equals(DssService.autoReview)) {
							autoDataHandle.autoReview(MN, pointCode, dataTime, ST);
						}
					}
				}

				// 统计传输个数，有效个数，连通个数,传输组数
				generalStatistics.statisticalHandler(station, CN, dataTime);
				// 油烟系统 统计连通个数
				connectivityStatistics.statisticalHandler(station, CN, dataTime);
				break;
			case DataStatisticsType.DST_QC:

				if (DataType.DAYDATA.equals(CN)) {
					dataTime = obj.getDate("DATATIME");

					// 监测数据分/小时/日 统计
					dataStatistical.statisticalHandler(station, dataTime, DataStatisticsType.DST_20X1A);

					// 级联审核日，月，年数据
					autoDataHandle.multiAudit(MN, station.getPointCode(), dataTime, ST);

					// 手動統計，排除其他關聯統計
					if (!"1".equals(manual)) {

						// 统计质控合格个数
						qualityControlStatistics.statisticalHandler(station, dataTime);
						// 统计传输个数，有效个数，连通个数,传输组数
						generalStatistics.statisticalHandler(station, CN, dataTime);

						// AQI統計
						if (SystemTypeCode.AIR.equalsIgnoreCase(ST)) {
							for (int i = 0; i < 24; i++) {
								Date htime = DateUtil.dateAddHour(dataTime, i);
								if (htime.getTime() < DateUtil.getCurrentDate().getTime()) {
									aqiStatistical.statisticalHandler(station, htime, DataType.HOURDATA, 1);
								}
							}
						}
					}

					if (SystemTypeCode.AIR.equalsIgnoreCase(ST)) {
						// 空气监测系统 统计aqi
						aqiStatistical.statisticalHandler(station, dataTime, DataType.DAYDATA, 1);

						// O38小时滑动均值统计
						o38Statistics.statisticsHandler(station, CN, dataTime, "1");
					}
				} else if (DataType.HOURDATA.equals(CN)) {

					// 空气监测系统 统计aqi
					dataTime = obj.getDate("DATATIME");
					autoDataHandle.autoAudit(MN, pointCode, dataTime, ST);
					if (SystemTypeCode.AIR.equals(ST)) {
						aqiStatistical.statisticalHandler(station, dataTime, CN, 1);
					}
				}

				break;
			case DataStatisticsType.DST_GK_Network:
			case DataStatisticsType.DST_GK_Outage:
			case DataStatisticsType.DST_GK_Overproof:
			case DataStatisticsType.DST_GK_ParamException:
				// 统计工况 网络异常，停运，超标，参数异常的时长
				dataTime = obj.getDate("DATATIME");
				alarmTimeLengthStatistical.statisticalHandler(station, dataTime, DST);
				break;
			case DataStatisticsType.DST_GK_Runtime:
				// 统计工况运行时长
				dataTime = obj.getDate("DATATIME");
				runtimeLengthStatistical.statisticalHandler(station, dataTime);
				break;
			case DataStatisticsType.DST_YCGS:
				// 统计应传个数
				dataTime = obj.getDate("DATATIME");
				antipateDataStatis.calculationShouldTransmission(station, CN, dataTime);
				break;

			case DataStatisticsType.DST_SK:
				// 统计实况数据
				dataTime = obj.getDate("DATATIME");
				skConvert.processSKData(station, dataTime);
				break;
			}

		} catch (Exception e) {
			logger.error("统计异常:", e);
		}

		return true;
	}

}
