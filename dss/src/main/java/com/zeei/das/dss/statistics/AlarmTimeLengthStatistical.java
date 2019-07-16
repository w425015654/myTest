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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.constants.StatisticsAlarmType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.service.AlarmService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.AlarmVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.RegularStopTimeVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：AlarmTimeLengthStatistical 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class AlarmTimeLengthStatistical {

	@Autowired
	AlarmService alarmService;

	@Autowired
	StroageService stroageService;

	private static Logger logger = LoggerFactory.getLogger(AlarmTimeLengthStatistical.class);

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
	public void statisticalHandler(StationVO station, Date dataTime, String dsType) {

		try {

			String pointCode = station.getPointCode();
			String alarmCode = "";
			String pulloteCode = "";

			switch (dsType) {

			case DataStatisticsType.DST_GK_Network:
				alarmCode = StatisticsAlarmType.OnlineCode;
				pulloteCode = SpecialFactor.GKOnlineCode;
				break;
			case DataStatisticsType.DST_GK_Overproof:
				alarmCode = StatisticsAlarmType.OverproofCode;
				pulloteCode = SpecialFactor.GKOverproofCode;
				break;
			case DataStatisticsType.DST_GK_ParamException:
				alarmCode = StatisticsAlarmType.ParamExceptionCode;
				pulloteCode = SpecialFactor.GKParamExceptionCode;
				break;
			case DataStatisticsType.DST_GK_Outage:
				alarmCode = StatisticsAlarmType.OnlineCode;
				pulloteCode = SpecialFactor.GKOutageCode;
				break;
			}

			List<String> alarmCodes = Arrays.asList(alarmCode);

			List<AlarmVO> alarms = null;

			Date bTime = DateUtil.strToDate(String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd")),
					"yyyy-MM-dd HH:mm:ss");
			Date eTime = DateUtil.strToDate(String.format("%s 23:59:59", DateUtil.dateToStr(dataTime, "yyyy-MM-dd")),
					"yyyy-MM-dd HH:mm:ss");

			if (DataStatisticsType.DST_GK_Outage.equalsIgnoreCase(dsType)) {
				alarms = getOutageTimes(pointCode, bTime, eTime, alarmCodes);
			} else {
				alarms = alarmService.queryAlarm(pointCode, bTime, eTime, alarmCodes);
			}

			if (alarms != null) {
				statisticalTime(alarms, pulloteCode, station, dataTime);
			}

		} catch (Exception e) {
		    logger.error("根据告警记录进行统计异常[dsType="+dsType+"]:"+e.toString());
		}

	}

	/**
	 * 
	 * getOutageTimes:获取停运时长的原始数据
	 *
	 * @param pointCode
	 *            测点编码
	 * @param dataTime
	 *            统计时间
	 * @param alarmCodes
	 *            设施停运的告警码
	 * @return List<AlarmVO>
	 */
	private List<AlarmVO> getOutageTimes(String pointCode, Date bTime, Date eTime, List<String> alarmCodes) {

		List<AlarmVO> alarms = alarmService.queryAlarm(pointCode, bTime, eTime, alarmCodes);

		if (alarms == null) {
			alarms = new ArrayList<AlarmVO>();
		}

		List<AlarmVO> alarmOutages = alarmService.queryAlarmByOutage(pointCode, bTime, eTime);

		alarms.addAll(alarmOutages);

		int week = DateUtil.getWeekOfDate(bTime);

		List<RegularStopTimeVO> regularStopTimes = alarmService.queryRegularStopTime(pointCode, week);

		if (regularStopTimes != null) {

			for (RegularStopTimeVO vo : regularStopTimes) {
				AlarmVO alarm = new AlarmVO();

				String date = DateUtil.dateToStr(bTime, "yyyy-MM-dd");

				Date bTime1 = DateUtil.strToDate(String.format("%s %s", date, vo.getStartTime()),
						"yyyy-MM-dd HH:mm:ss");
				Date eTime1 = DateUtil.strToDate(String.format("%s %s", date, vo.getEndTime()), "yyyy-MM-dd HH:mm:ss");
				alarm.setPointCode(pointCode);
				alarm.setBeginTime(bTime1);
				alarm.setEndTime(eTime1);
				alarms.add(alarm);
			}
		}

		List<AlarmVO> exceptionTimes = alarmService.queryExceptionTime(pointCode, bTime, eTime);

		alarms.addAll(exceptionTimes);

		Collections.sort(alarms, new Comparator<AlarmVO>() {

			public int compare(AlarmVO o1, AlarmVO o2) {

				if (o1.getBeginTime().compareTo(o2.getBeginTime()) == 0) {

					return o1.getEndTime().compareTo(o2.getEndTime());
				} else {
					return o1.getBeginTime().compareTo(o2.getBeginTime());
				}

			}

		});

		return alarms;

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
	 * @param CN
	 *            系统类型
	 * 
	 *            void
	 */
	private void statisticalTime(List<AlarmVO> alarms, String pulloteCode, StationVO station, Date dataTime) {

		String pointCode = station.getPointCode();

		String ST = station.getST();
		String CN = DataType.DAYDATA;

		// 最大时间
		Date minTime = RegularTime.regular(CN,station.getMN(),dataTime);
		Date maxTime = DateUtil.dateAddDay(minTime, 1);

		String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

		if (StringUtil.isEmptyOrNull(tableName)) {
			return;
		}

		Date beginTime = null;
		Date endTime = null;

		List<Date[]> times = new ArrayList<Date[]>();

		for (int i = 0; i < alarms.size(); i++) {

			AlarmVO alarm = alarms.get(i);

			Date alarmBTime = alarm.getBeginTime();
			Date alarmETime = alarm.getEndTime();

			if (i == 0) {
				beginTime = alarmBTime;
				endTime = alarmETime;

				// 开始时间小于最小时间，用最小时间代替
				if (beginTime.getTime() < minTime.getTime()) {
					beginTime = minTime;
				}

			} else {

				if (alarmBTime.getTime() > endTime.getTime()) {
					times.add(new Date[] { beginTime, endTime });
					beginTime = alarmBTime;
					endTime = alarmETime;
				} else if (alarmETime.getTime() > endTime.getTime() && alarmBTime.getTime() <= endTime.getTime()) {
					endTime = alarmETime;
				}
			}

			// 结束时间大于最大时间，跳出循环
			if (endTime.getTime() > maxTime.getTime()) {

				times.add(new Date[] { beginTime, maxTime });
				break;
			}

			if (i == alarms.size() - 1) {
				times.add(new Date[] { beginTime, endTime });
			}

		}

		double second = 0;
		for (Date[] time : times) {
			second += DateUtil.dateDiffSecond(time[0], time[1]);
		}

		Double hours = second / 60 / 60;

		hours = StatisticalHelper.dataRounding(hours, 2);

		if (hours > 24) {
			hours = 24d;
		}

		MonitorDataVO vo = new MonitorDataVO();
		vo.setDataTime(DateUtil.dateToStr(minTime, "yyyy-MM-dd HH:mm:ss"));
		vo.setDataValue(hours);
		vo.setMaxValue(hours);
		vo.setMinValue(hours);
		vo.setAuditValue(hours);
		vo.setPolluteCode(pulloteCode);
		vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		vo.setPointCode(pointCode);

		if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN) ) {
			stroageService.insertYMData(tableName, vo);
		} else {
			stroageService.insertData(tableName, vo);
		}

		switch (pulloteCode) {

		case SpecialFactor.GKOnlineCode:
			logger.info("网络异常时长：" + JSON.toJSONString(vo));
			break;
		case SpecialFactor.GKOverproofCode:
			logger.info("超标时长：" + JSON.toJSONString(vo));
			break;
		case SpecialFactor.GKParamExceptionCode:
			logger.info("参数异常时长：" + JSON.toJSONString(vo));
			break;
		case SpecialFactor.GKOutageCode:
			logger.info("停运时长：" + JSON.toJSONString(vo));
			break;

		}

	}

}
