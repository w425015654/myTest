/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StatisticalHelper.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月26日上午9:19:19
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月26日上午9:19:19 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：StatisticalHelper 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class StatisticalHelper {


	@Autowired
	QueryDataService queryDataService;

	/**
	 * 
	 * queryStatisticsData:查询统计数据
	 *
	 * @param station
	 * @param CCN
	 * @param DCN
	 * @param dataTime
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryStatisticsData(StationVO station, String CCN, String DCN, Date dataTime) {

		String ST = station.getST();
		String pointCode = station.getPointCode();

		// 空气站 小时数据 14点 13:00-14:00 
		// 日数据 15号 15 01-16 01 

		// 污染源 小时数据 14点 14:00-15:00 
		// 日数据 15号 15 00-16 00 

		String beginTime = DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss");
		String endTime = getEndTime(station, CCN, dataTime);

		String tableName = PartitionTableUtil.getTableName(ST, DCN, pointCode, dataTime);
		// 查询上一个周期的数据
		List<MonitorDataVO> metadata = new ArrayList<MonitorDataVO>();

		// 空气站 日数据统计 01-00
		if (SystemTypeCode.AIR.equals(ST)) {

			if (CCN.equals(DataType.HOURDATA) || CCN.equals(DataType.MINUTEDATA)) {

				beginTime = getBeginTime(station, CCN, dataTime);
				endTime = DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss");

			} else {
				beginTime = String.format("%s 01:00:00", beginTime.substring(0, 10));
				endTime = String.format("%s 01:00:00", endTime.substring(0, 10));
			}
		}

		if (!StringUtil.isEmptyOrNull(tableName)) {

			if (DCN.equals(DataType.MONTHDATA)) {
				metadata = queryDataService.queryYMDataByCondition(tableName, DCN, pointCode, beginTime, endTime, null,
						ST);
			} else {
				metadata = queryDataService.queryDataByCondition(tableName, pointCode, beginTime, endTime, null, ST);
			}

		}

		List<String> vfactor = DssService.vFactor;
		//向虚拟因子集合增加       实况因子
		//获取   标识   广西过滤 实况因子
		if(DssService.cfgMap.get("skFilter") != null && DssService.cfgMap.get("skFilter").equals("1")){
			vfactor.addAll(AQIStatistical.skPollutes);
		}
		if (metadata != null && vfactor != null) {
			// 只统计站点配置的因子,配置为空时则默认全为配置，过滤掉虚拟因子,和实况因子 即可
			metadata = metadata.stream().filter(o -> !vfactor.contains(o.getPolluteCode()))
					.collect(Collectors.toList());

		}

		return metadata;
	}

	/**
	 * 
	 * getPreCN:获取上个数据类型的CN
	 *
	 * @param CN
	 * @return String
	 */
	public static String getDataCN(String CN) {
		String QCN = "";
		switch (CN) {
		case DataType.MINUTEDATA:
			QCN = DataType.RTDATA;
			break;
		case DataType.HOURDATA:
			QCN = DataType.MINUTEDATA;
			break;
		case DataType.DAYDATA:
			QCN = DataType.HOURDATA;
			break;
		case DataType.MONTHDATA:
			QCN = DataType.DAYDATA;
			break;
		}
		return QCN;
	}

	/**
	 * 
	 * getBeginTime:获取统计数据的开始时间
	 *
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @return String
	 */
	public static String getBeginTime(StationVO station, String CN, Date dataTime) {
		String beginTime = "";
		switch (CN) {
		case DataType.MINUTEDATA:
			int interval = station.getmInterval();
			beginTime = DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, interval * (-1)), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.HOURDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, -1), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.DAYDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, -1), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.MONTHDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, -1), "yyyy-MM-dd HH:mm:ss");
			break;
		}

		return beginTime;
	}

	/**
	 * @Title: getCurrIntegralTime @Description: (获取当前时间所在的周期开始时间) @param @param
	 *         CN @param @return 参数 @return Date 返回类型 @throws
	 */

	public static Date getCurrIntegralTime(String CN) {

		Calendar ca = Calendar.getInstance();

		switch (CN) {
		case DataType.MONTHDATA:// "当月第一天点"
			ca.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONDAY), ca.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			ca.set(Calendar.DAY_OF_MONTH, ca.getActualMinimum(Calendar.DAY_OF_MONTH));
			break;
		case DataType.DAYDATA:// "当天零点"
			ca.set(Calendar.HOUR_OF_DAY, 0);
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			break;
		case DataType.HOURDATA: // "当前小时"
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			break;
		case DataType.MINUTEDATA: // "当前小时"
			ca.set(Calendar.MINUTE, 0);
			ca.set(Calendar.SECOND, 0);
			break;
		}
		Date date = ca.getTime();
		return date;
	}

	/**
	 * 
	 * getBeginTime:获取统计数据的结束时间
	 *
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @return String
	 */
	public static String getEndTime(StationVO station, String CN, Date dataTime) {
		String beginTime = "";
		switch (CN) {
		case DataType.MINUTEDATA:
			int interval = station.getmInterval();
			beginTime = DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, interval), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.HOURDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.DAYDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.MONTHDATA:
			beginTime = DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
			break;
		}

		return beginTime;
	}

	/**
	 * 
	 * dataRounding:数据修约
	 *
	 * @param value
	 * @param precision
	 *            精度
	 * @return Double
	 */
	public static Double dataRounding(Double value, int precision) {

		try {
			if (value == null) {
				return null;
			}
			BigDecimal bg = new BigDecimal(value).setScale(precision, RoundingMode.UP);

			return bg.doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * dataBFW:按百分位计算
	 * 
	 * @param metadata
	 *            数据集
	 * @param precision
	 *            百分位
	 * @return Double
	 */
	public static Double dataBFW(List<Double> metadata, int precision) {

		try {

			if (metadata == null) {
				return null;
			}

			metadata.removeAll(Collections.singleton(null));

			if (metadata.size() <= 1) {
				return metadata.get(0);
			}

			Collections.sort(metadata);
			int n = metadata.size();

			BigDecimal k = new BigDecimal(Double.toString(1 + (n - 1) * precision * 0.01));
			// 向下取整
			BigDecimal s = new BigDecimal(Integer.toString((int) Math.floor(k.doubleValue())));

			BigDecimal Xs = new BigDecimal(Double.toString(metadata.get(s.intValue() - 1)));

			BigDecimal Xs1 = new BigDecimal(Double.toString(metadata.get(s.intValue())));

			Double Xp = Xs.add(Xs1.subtract(Xs).multiply(k.subtract(s))).doubleValue();

			return Xp;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
