/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：GkAlarmHandler.java
* 包  名  称：com.zeei.das.aps.alarm
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月31日上午11:34:01
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月31日上午11:34:01 创建文件
*
*/

package com.zeei.das.aps.alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.aps.service.AlarmProcessService;
import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.ExcludeTimeVO;
import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：GkAlarmHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component("gkAlarmHandler")
public class GkAlarmHandler {

	@Autowired
	AlarmProcessService service;

	private Map<Integer, List<Date[]>> exceptionTimesMap = new HashMap<Integer, List<Date[]>>();

	private Map<String, List<String[]>> regularStopTimesMap = new HashMap<String, List<String[]>>();

	public void alarmHandler() {

		try {
			Runnable runnable = new Runnable() {
				public void run() {

					try {
						Date endTime = DateUtil.getCurrentDate();
						Date beginTime = DateUtil.dateAddMonth(endTime, -1);

						List<AlarmInfoVO> alarms = service.queryAlarmInfo(beginTime, endTime);

						if (alarms != null) {

							queryExcludeTime();

							for (AlarmInfoVO alarm : alarms) {

								try {
									List<Date[]> excludeTimes = analysisTime(alarm);

									if (excludeTimes != null && excludeTimes.size() > 0) {
										splitAlarm(alarm, excludeTimes, 0);
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * splitAlarm:告警拆分
	 *
	 * @param alarm
	 *            告警对象
	 * @param excludeTimes
	 *            切分时段
	 * @param i
	 *            void
	 */

	private void splitAlarm(AlarmInfoVO alarm, List<Date[]> excludeTimes, int i) {

		if (i > excludeTimes.size() - 1) {

			processLatestAlarm(alarm);

			return;
		}

		Date[] times = excludeTimes.get(i);

		Date bTime = times[0];
		Date eTime = times[1];

		Date abTime = alarm.getStartTime();
		Date aeTime = alarm.getEndTime();

		if (aeTime == null) {
			aeTime = DateUtil.getCurrentDate();
		}

		// 告警结束时间 在异常时段之内，告警开始时间在异常时段之外
		if (aeTime.getTime() <= eTime.getTime() && aeTime.getTime() > bTime.getTime()
				&& abTime.getTime() < bTime.getTime()) {

			AlarmInfoVO vo = JSON.parseObject(JSON.toJSONString(alarm), AlarmInfoVO.class);

			vo.setEndTime(bTime);
			vo.setIsOnline(0);
			vo.setAlarmStatus(4);
			vo.setIsShielded(0);

			if (service.updateAlarmInfo(vo) == false) {
				service.insertAlarmInfo(vo);
			}

			alarm.setStartTime(bTime);
			alarm.setIsOnline(1);
			alarm.setAlarmStatus(1);
			splitAlarm(alarm, excludeTimes, i + 1);

			return;
			// 告警时段 被包含在异常时段之内
		} else if (aeTime.getTime() <= eTime.getTime() && abTime.getTime() >= bTime.getTime()) {

			// 设置告警为异常时段告警
			alarm.setIsOnline(1);
			alarm.setIsShielded(0);
			processLatestAlarm(alarm);
			return;

			// 告警开始时间在异常时段之内，告警结束时间在异常时段之外
		} else if (aeTime.getTime() > eTime.getTime() && abTime.getTime() >= bTime.getTime()
				&& abTime.getTime() < eTime.getTime()) {

			AlarmInfoVO vo = JSON.parseObject(JSON.toJSONString(alarm), AlarmInfoVO.class);

			vo.setEndTime(eTime);
			vo.setIsOnline(1);
			vo.setAlarmStatus(4);
			vo.setIsShielded(0);

			if (vo.getIsGenAlarm() == 1) {
				if (service.updateAlarmInfo(vo) == false) {
					service.insertAlarmInfo(vo);
				}
			} else {
				service.delAlarmInfo(vo);
			}

			alarm.setStartTime(eTime);
			alarm.setIsOnline(0);
			alarm.setIsShielded(0);
			alarm.setAlarmStatus(1);

			splitAlarm(alarm, excludeTimes, i + 1);

			// 告警时段包含异常时段
		} else if (aeTime.getTime() > eTime.getTime() && abTime.getTime() < bTime.getTime()) {

			// 告警拆分成3段时间
			AlarmInfoVO vo = JSON.parseObject(JSON.toJSONString(alarm), AlarmInfoVO.class);

			// 非异常时段告警
			vo.setEndTime(bTime);
			vo.setAlarmStatus(4);
			vo.setIsOnline(0);
			vo.setIsShielded(0);

			if (service.updateAlarmInfo(vo) == false) {
				service.insertAlarmInfo(vo);
			}

			if (alarm.getIsGenAlarm() == 1) {
				// 异常时段告警
				AlarmInfoVO vo1 = JSON.parseObject(JSON.toJSONString(alarm), AlarmInfoVO.class);
				vo1.setStartTime(bTime);
				vo1.setEndTime(eTime);
				vo1.setAlarmStatus(4);
				vo1.setIsOnline(1);
				vo1.setIsShielded(0);
				service.insertAlarmInfo(vo1);
			}

			// 非异常时段告警
			alarm.setStartTime(eTime);
			alarm.setIsOnline(0);
			alarm.setIsShielded(0);
			alarm.setAlarmStatus(1);
			splitAlarm(alarm, excludeTimes, i + 1);

		} else {

			if (excludeTimes.size() == i + 1) {

				processLatestAlarm(alarm);

			} else {
				splitAlarm(alarm, excludeTimes, i + 1);
			}
		}
	}

	/**
	 * 
	 * processLatestAlarm:处理最后一条告警
	 *
	 * @param alarm
	 *            void
	 */

	private void processLatestAlarm(AlarmInfoVO alarm) {

		// 非停产期间告警
		if (alarm.getIsOnline() == 0) {

			if (service.updateAlarmInfo(alarm) == false) {
				service.insertAlarmInfo(alarm);
			}

		} else {

			// 停产期间，已消警的告警
			if (alarm.getAlarmStatus() == 4) {

				// 停产期间不参生告警
				if (alarm.getIsGenAlarm() == 0) {
					service.delAlarmInfo(alarm);
				} else {
					// 停产期间产生告警
					if (service.updateAlarmInfo(alarm) == false) {
						service.insertAlarmInfo(alarm);
					}
				}

			} else {

				// 停产期间，未消警的告警，将此告警屏蔽
				int isGenAlarm = alarm.getIsGenAlarm();

				alarm.setIsShielded(isGenAlarm == 1 ? 0 : 1);

				if (service.updateAlarmInfo(alarm) == false) {
					service.insertAlarmInfo(alarm);
				}
			}

		}
	}

	/**
	 * 
	 * queryExcludeTime:查询异常申报时间和规律性停产时间
	 *
	 * @return void
	 */

	private void queryExcludeTime() {

		exceptionTimesMap.clear();
		regularStopTimesMap.clear();

		List<ExcludeTimeVO> exceptionTimes = service.queryExceptionTime();
		List<ExcludeTimeVO> regularStopTimes = service.queryRegularStopTime();

		if (exceptionTimes != null) {
			Map<Integer, List<ExcludeTimeVO>> map = exceptionTimes.stream()
					.collect(Collectors.groupingBy(ExcludeTimeVO::getPointCode, Collectors.toList()));

			if (map != null) {

				for (Map.Entry<Integer, List<ExcludeTimeVO>> entry : map.entrySet()) {

					List<ExcludeTimeVO> list = entry.getValue();

					if (list != null && list.size() > 0) {

						List<Date[]> times = new ArrayList<Date[]>();

						for (ExcludeTimeVO vo : list) {

							times.add(new Date[] { vo.getbDateTime(), vo.geteDateTime() });
						}
						exceptionTimesMap.put(entry.getKey(), times);
					}
				}
			}

		}

		// 按星期拆分时间
		if (regularStopTimes != null) {
			// 按站点和week分组
			Map<Integer, Map<Integer, List<ExcludeTimeVO>>> map = regularStopTimes.stream()
					.collect(Collectors.groupingBy(ExcludeTimeVO::getPointCode,
							Collectors.groupingBy(ExcludeTimeVO::getWeek, Collectors.toList())));

			for (Entry<Integer, Map<Integer, List<ExcludeTimeVO>>> entry : map.entrySet()) {

				Map<Integer, List<ExcludeTimeVO>> map1 = entry.getValue();

				for (Entry<Integer, List<ExcludeTimeVO>> entry1 : map1.entrySet()) {

					List<ExcludeTimeVO> list = entry1.getValue();

					if (list != null && list.size() > 0) {

						List<String[]> times = new ArrayList<String[]>();

						for (ExcludeTimeVO vo : list) {

							times.add(new String[] { vo.getStartTime(), vo.getEndTime() });
						}

						String key = String.format("%s-%s", entry.getKey(), entry1.getKey());

						regularStopTimesMap.put(key, times);
					}

				}

			}

		}

	}

	/**
	 * 
	 * analysisTime:TODO 请修改方法功能描述
	 *
	 * @param alarm
	 * @return List<Date[]>
	 */
	private List<Date[]> analysisTime(AlarmInfoVO alarm) {

		List<Date[]> excludeTimes = new ArrayList<Date[]>();

		int pointCode = alarm.getPointCode();

		if (exceptionTimesMap != null) {

			List<Date[]> times = exceptionTimesMap.get(pointCode);

			if (times != null && times.size() > 0) {
				excludeTimes.addAll(times);
			}
		}

		if (regularStopTimesMap != null) {

			Date bTime = alarm.getStartTime();
			Date eTime = alarm.getEndTime();

			if (eTime == null) {
				eTime = DateUtil.getCurrentDate();
			}

			bTime = DateUtil.strToDate(DateUtil.dateToStr(bTime, "yyyy-MM-dd"), "yyyy-MM-dd");
			eTime = DateUtil.strToDate(DateUtil.dateToStr(eTime, "yyyy-MM-dd"), "yyyy-MM-dd");

			do {

				int week = DateUtil.getWeekOfDate(bTime);
				String date = DateUtil.dateToStr(bTime, "yyyy-MM-dd");
				String key = String.format("%s-%s", alarm.getPointCode(), week);
				List<String[]> times = regularStopTimesMap.get(key);
				if (times != null && times.size() > 0) {
					for (String[] s : times) {
						Date beginTime = DateUtil.strToDate(String.format("%s %s", date, s[0]), "yyyy-MM-dd HH:mm:ss");
						Date endTime = DateUtil.strToDate(String.format("%s %s", date, s[1]), "yyyy-MM-dd HH:mm:ss");
						excludeTimes.add(new Date[] { beginTime, endTime });
					}
				}
				bTime = DateUtil.dateAddDay(bTime, 1);

			} while (bTime.getTime() <= eTime.getTime());
		}

		return mergeTime(excludeTimes);

	}

	/**
	 * 
	 * mergeTime:时间去重合并
	 *
	 * @param excludeTimes
	 *            void
	 */
	private List<Date[]> mergeTime(List<Date[]> excludeTimes) {

		Date beginTime = null;
		Date endTime = null;

		List<Date[]> times = new ArrayList<Date[]>();

		// 时间排序
		Collections.sort(excludeTimes, new Comparator<Date[]>() {

			public int compare(Date[] o1, Date[] o2) {

				if (o1[0].compareTo(o2[0]) == 0) {
					return o1[1].compareTo(o2[1]);
				} else {
					return o1[0].compareTo(o2[0]);
				}
			}

		});

		for (int i = 0; i < excludeTimes.size(); i++) {

			Date[] excludeTime = excludeTimes.get(i);

			Date cBTime = excludeTime[0];
			Date cETime = excludeTime[1];

			if (i == 0) {
				beginTime = cBTime;
				endTime = cETime;
			} else {

				if (cBTime.getTime() > endTime.getTime()) {

					long mins = DateUtil.dateDiffMin(endTime, cBTime);
					if (mins > 1) {
						times.add(new Date[] { beginTime, endTime });
						beginTime = cBTime;
						endTime = cETime;
					} else {
						endTime = cETime;
					}

				} else if (cETime.getTime() > endTime.getTime() && cBTime.getTime() <= endTime.getTime()) {
					endTime = cETime;
				}

			}

			if (i == excludeTimes.size() - 1) {
				times.add(new Date[] { beginTime, endTime });
			}
		}

		return times;

	}

}
