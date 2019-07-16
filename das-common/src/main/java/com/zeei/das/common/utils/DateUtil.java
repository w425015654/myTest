/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：DateUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：TODO 处理时间的工具类
* 创建日期：2017年4月19日上午8:48:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月19日上午8:48:45 创建文件
*
*/

package com.zeei.das.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 类 名 称：DateUtil 类 描 述：TODO 处理时间的工具类 功能描述：TODO 处理时间的工具类 创建作者：quanhongsheng
 */

public class DateUtil {

	/**
	 * 
	 * getCurrentMin:获取当前分钟数
	 *
	 * @return int
	 */
	public static int getCurrentMin() {
		Calendar calendar = Calendar.getInstance();
		int min = calendar.get(Calendar.MINUTE);
		return min;
	}

	/**
	 * 
	 * getCurrentMin:获取当前时间
	 *
	 * @return int
	 */
	public static Date getCurrentDate() {
		return new Date();
	}

	/**
	 * 
	 * getCurrentDate:按指定格式获取当前时间
	 *
	 * @param format
	 *            时间格式
	 * @return String
	 */
	public static String getCurrentDate(String format) {

		if (StringUtil.isEmptyOrNull(format)) {
			format = "yyyyMMddHHmmss";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
		return df.format(new Date());
	}

	/**
	 * 
	 * dateToStr:按指定格式将日期转成字符串
	 *
	 * @param format
	 *            时间格式
	 * @return String
	 */
	public static String dateToStr(Date date, String format) {

		if (StringUtil.isEmptyOrNull(format)) {
			format = "yyyyMMddHHmmss";
		}
		SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
		return df.format(date);
	}

	/**
	 * 
	 * strToDate:字符串转换成日期
	 *
	 * @param dateStr
	 * @return Date
	 * @throws ParseException
	 */
	public static Date strToDate(String dateStr, String format) {

		if (StringUtil.isEmptyOrNull(format)) {
			format = "yyyyMMddHHmmss";
		}

		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();

		}
		return date;
	}

	public static long dateDiffSecond(Date startTime, Date endTime) {
		long diff = (endTime.getTime() - startTime.getTime()) / 1000;
		return diff;
	}

	public static long dateDiffMin(Date startTime, Date endTime) {
		long diff = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
		return diff;
	}

	public static long dateDiffHour(Date startTime, Date endTime) {
		long diff = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60);
		return diff;
	}

	public static long dateDiffDay(Date startTime, Date endTime) {
		long diff = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
		return diff;
	}

	public static Date dateAddSecond(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.SECOND, interval);//
		return c.getTime();
	}

	public static Date dateAddMin(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.MINUTE, interval);//
		return c.getTime();
	}

	public static Date dateAddHour(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.HOUR_OF_DAY, interval);//
		return c.getTime();
	}

	public static Date dateAddDay(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.DAY_OF_MONTH, interval);//
		return c.getTime();
	}

	public static Date dateAddMonth(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.MONTH, interval);//
		return c.getTime();
	}

	public static Date dateAddYear(Date dateTime, int interval) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		c.add(Calendar.YEAR, interval);//
		return c.getTime();
	}

	public static int getMinute(Date dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		return c.get(Calendar.MINUTE);
	}

	public static int getHour(Date dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		return c.get(Calendar.HOUR);
	}

	public static int getMonth(Date dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		return c.get(Calendar.MONTH);
	}

	public static int getYear(Date dateTime) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateTime);
		return c.get(Calendar.YEAR);
	}

	/**
	 * 根据日期获得星期
	 * 
	 * @param date
	 * @return
	 */
	public static Integer getWeekOfDate(Date date) {
		// String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
		// "星期六" };
		Integer[] weekDaysCode = { 0, 1, 2, 3, 4, 5, 6 };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		return weekDaysCode[intWeek];
	}
	
	
	public static void main(String args[]) {
		
		Date dataTime=strToDate("2017-10-26 15:29:41","yyyy-MM-dd HH:mm:ss");
		Date currentDate=strToDate("2017-10-27 13:51:19","yyyy-MM-dd HH:mm:ss");
		
		long s= dateDiffMin(dataTime, currentDate);
		
		System.out.println(s);
	}

}
