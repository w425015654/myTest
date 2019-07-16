/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：RegularTime.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月10日下午1:43:54
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月10日下午1:43:54 创建文件
*
*/

package com.zeei.das.dps.storage;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.vo.PointSiteVO;

/**
 * 类 名 称：RegularTime 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class RegularTime {

	private static Logger logger = LoggerFactory.getLogger(RegularTime.class);

	/**
	 * 
	 * regularTime:时间规整
	 *
	 * @param CN
	 *            数据类型
	 * @param dateStr
	 * @return String
	 */
	public static String regular(String CN, String MN, String dateStr) {

		Date dataTime = DateUtil.strToDate(dateStr, "yyyy-MM-dd HH:mm:ss");

		String timeStr = regularDateStr(CN, MN, dataTime);

		return timeStr;
	}

	/**
	 * 
	 * regularTime:时间规整
	 *
	 * @param CN
	 *            数据类型
	 * @param dateStr
	 * @return String
	 */
	public static Date regular(String CN, String MN, Date dataTime) {

		String timeStr = regularDateStr(CN, MN, dataTime);

		return DateUtil.strToDate(timeStr, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 
	 * regularTime:时间规整
	 *
	 * @param CN
	 *            数据类型
	 * @param dateStr
	 * @return String
	 */
	public static String regularDateStr(String CN, String MN, Date dataTime) {

		String timeStr = null;

		PointSiteVO station = DpsService.stationCfgMap.get(MN);

		switch (CN) {
		case DataType.RTDATA:
		case DataType.MINUTEDATA:
		case DataType.HOURDATA:

			// 站点数据上报周期
			int interval = 5;

			if (station != null) {

				if (DataType.RTDATA.equalsIgnoreCase(CN)) {
					interval = station.getrInterval() * 1000;
				} else if (DataType.MINUTEDATA.equalsIgnoreCase(CN)) {
					interval = station.getmInterval() * 60000;
				} else if (DataType.HOURDATA.equalsIgnoreCase(CN)) {
					interval = station.gethInterval() * 3600000;
				}
			}

			// 原时间毫秒数
			long l = dataTime.getTime();
			// 取整周期
			long r = l / interval;
			// 取整后的时间
			Date rd = new Date();
			rd.setTime(r * interval);

			timeStr = DateUtil.dateToStr(rd, "yyyy-MM-dd HH:mm:ss");
			break;
		case DataType.DAYDATA:
			timeStr = String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
			break;
		case DataType.MONTHDATA:
			timeStr = String.format("%s-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM"));
			break;
		case DataType.YEARDATA:
			timeStr = String.format("%s-01-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy"));
			break;
		}

		// logger.info(String.format("%s-----%s", DateUtil.dateToStr(dataTime,
		// "yyyy-MM-dd HH:mm:ss"),timeStr));

		return timeStr;
	}

	public static void main(String[] args) {

		@SuppressWarnings("deprecation")
		Date d = new Date("2019/2/21 9:01:54");

		long l = d.getTime();

		int i = 60 * 1000 * 60 * 2;

		long r = l / i;

		Date rd = new Date();
		rd.setTime(r * i);

		System.out.println(DateUtil.dateToStr(rd, "yyyy-MM-dd HH:mm:ss"));
	}
}
