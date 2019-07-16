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

package com.zeei.das.aas.utils;

import java.util.Date;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：RegularTime 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class RegularTime {

	/**
	 * 
	 * regularTime:时间规整
	 *
	 * @param CN
	 *            数据类型
	 * @param dateStr
	 * @return String
	 */
	public static String regular(String CN, String dateStr) {

		Date dataTime = DateUtil.strToDate(dateStr, "yyyy-MM-dd HH:mm:ss");

		String timeStr = null;

		switch (CN) {
		case DataType.MINUTEDATA:
			timeStr = String.format("%s:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm"));
			break;
		case DataType.HOURDATA:
			timeStr = String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH"));
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
	public static Date regular(String CN, Date dataTime) {

		String timeStr = null;

		switch (CN) {
		case DataType.MINUTEDATA:
			timeStr = String.format("%s:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm"));
			break;
		case DataType.HOURDATA:
			timeStr = String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH"));
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

		return DateUtil.strToDate(timeStr, "yyyy-MM-dd HH:mm");
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
	public static String regularDateStr(String CN, Date dataTime) {

		String timeStr = null;

		switch (CN) {
		case DataType.MINUTEDATA:
			timeStr = String.format("%s:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm"));
			break;
		case DataType.HOURDATA:
			timeStr = String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH"));
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

		return timeStr;
	}
}
