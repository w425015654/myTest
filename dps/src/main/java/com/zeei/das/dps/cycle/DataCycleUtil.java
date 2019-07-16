/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：DataCycleUtil.java
* 包  名  称：com.zeei.das.dps.common
* 文件描述：计算数据的时间周期
* 创建日期：2017年4月27日上午11:29:56
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午11:29:56 创建文件
*
*/

package com.zeei.das.dps.cycle;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.T20x1Message;

/**
 * 类 名 称：DataCycleUtil 类 描 述：计算数据的时间周期 功能描述：计算数据的时间周期 创建作者：quanhongsheng
 */

@Component
public class DataCycleUtil {

	@Autowired
	Publish publish;

	private Logger logger = LoggerFactory.getLogger(DataCycleUtil.class);

	/**
	 * 
	 * cycleHandler:监测数据周期处理
	 *
	 * @param data
	 *            T212 消息数据
	 * 
	 *            void
	 */
	public void cycleHandler(T20x1Message data) {

		try {

			String MN = data.getMN();
			String CN = data.getCN();
			String SCN = CN;
			Date date = data.getCP().getDataTime();
			String ST = data.getST();

			// 获取内存站点配置数据
			PointSiteVO station = DpsService.stationCfgMap.get(MN);

			if (station == null) {
				return;
			}

			// 站点数据上报周期
			int interval = 5;
			// 站点数据周期
			long preCycle = 0;

			switch (CN) {
			case DataType.RTDATA:
				interval = station.getmInterval() * 60;
				preCycle = station.getmCycle();
				SCN = DataType.MINUTEDATA;
				break;
			case DataType.MINUTEDATA:
				interval = station.gethInterval() * 60 * 60;
				preCycle = station.gethCycle();
				SCN = DataType.HOURDATA;
				break;
			case DataType.HOURDATA:
				interval = station.getdInterval() * 24 * 60 * 60;
				preCycle = station.getdCycle();
				SCN = DataType.DAYDATA;
				break;
			}

			// 获取当前数据周期
			long cycle = getCycle(date, interval);

			if (cycle > preCycle) {
				// 新数据过来，有些统计要用上一周期的时间
				switch (CN) {
				case DataType.RTDATA:
					station.setmCycle(cycle);
					break;
				case DataType.MINUTEDATA:
					station.sethCycle(cycle);
					break;
				case DataType.HOURDATA:
					station.setdCycle(cycle);
					break;
				}
			}

			/*
			 * 空气站 小时数据 14点 13:00-14:00 当前小时传的当前小时数据 日数据 15号 15 01-16 01
			 * 当天传的是前一天的数据
			 * 
			 * 污染源 小时数据 14点 14:00-15:00 当前小时传的前一个小时数据 日数据 15号 15 00-16 00
			 * 当天传的是前一天的数据
			 */

			//logger.info(String.format("%s %s %s %s", CN, station.getPointCode(), cycle, preCycle));

			// 2个周期不同时，发送监测数据统计消息和通用统计消息
			if (cycle != preCycle
					&& (CN.equals(DataType.RTDATA) || CN.equals(DataType.MINUTEDATA) || CN.equals(DataType.HOURDATA))) {

				Date preDate = date;

				if (cycle > preCycle && preCycle > 0) {
					// 当前周期大于上一个周期，表示上个周期的数据已传输完毕，发送上一个周期的统计消息
					preDate = new Date(interval * preCycle * 1000);

					if (SystemTypeCode.AIR.equals(ST)
							&& (DataType.MINUTEDATA.equals(SCN) || DataType.HOURDATA.equals(SCN))) {
						preDate = new Date(interval * (preCycle + 1) * 1000);
					}

				} else {
					// 补数时，发送当前数据周期的统计消息
					preDate = date;

					if (SystemTypeCode.AIR.equals(ST)
							&& (DataType.MINUTEDATA.equals(SCN) || DataType.HOURDATA.equals(SCN))) {
						preDate = new Date(interval * (cycle + 1) * 1000);
					}
				}

				// 发送监测数据统计消息
				Map<String, Object> dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", SCN);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_20X1G);
				dataCycle.put("DATATIME", preDate);
				String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_CYCLE, json);

				// 发送通用统计消息
				dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", SCN);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_General);
				dataCycle.put("DATATIME", preDate);
				json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_CYCLE, json);
			}

			// 发送 传输个数,统计消息
			if (DataType.DAYDATA.equals(CN)) {

				Map<String, Object> dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", CN);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_General);
				dataCycle.put("DATATIME", date);
				String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_CYCLE, json);
			}

			// 发送当前周期，AQI统计消息
			if (SystemTypeCode.AIR.equals(ST) && ((DataType.DAYDATA.equals(CN) && DpsService.isAQID == 1)
					|| (DataType.HOURDATA.equals(CN) && DpsService.isAQIH == 1))) {
				Map<String, Object> dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", CN);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_AQI);
				dataCycle.put("DATATIME", date);
				String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);

				publish.send(Constant.MQ_QUEUE_CYCLE, json);
			}

			// 补数时，不是当天的数据，发送当前数据周期工况运行时长统计消息
			if (DataType.RTDATA.equals(CN)
					&& !DateUtil.getCurrentDate("yyyy-MM-dd").equals(DateUtil.dateToStr(date, "yyyy-MM-dd"))) {
				Map<String, Object> dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", DataType.RTDATA);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_GK_Runtime);
				dataCycle.put("DATATIME", date);
				String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_CYCLE, json);
			}

			// 补数时，不是当前月份，发送当前数据月统计消息
			if (DataType.DAYDATA.equals(CN)
					&& DateUtil.getMonth(DateUtil.getCurrentDate()) != DateUtil.getMonth(date)) {
				Map<String, Object> dataCycle = new HashMap<String, Object>();
				dataCycle.put("MN", MN);
				dataCycle.put("CN", DataType.MONTHDATA);
				dataCycle.put("ST", ST);
				dataCycle.put("DST", DataStatisticsType.DST_20X1G);
				dataCycle.put("DATATIME", date);
				String json = JSON.toJSONStringWithDateFormat(dataCycle, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_CYCLE, json);
			}

		} catch (Exception e) {

			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

	/**
	 * 
	 * getCycle:根据上报时间间隔， 计算数据隶属周期
	 *
	 * @param date
	 *            数据时间
	 * @param interval
	 *            上报时间间隔
	 * @return long
	 */
	public static long getCycle(Date date, int interval) {
		// 默认值
		if (interval == 0) {
			interval = 5;
		}
		interval = interval * 1000;
		Long time = date.getTime();
		long cycle = time / interval;

		//System.out.println(time + "---" + interval + "---" + cycle);

		return cycle;
	}

	public static void main(String[] args) {
		// 默认值

		String format = "yyyy-MM-dd HH:mm:ss";
		Date d = DateUtil.strToDate("2019-05-15 18:00:00", format);

		int i = 60 * 60;

		long c = getCycle(d, i);

		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(c * i, 0, ZoneOffset.ofHours(8));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

		System.out.println(localDateTime.format(formatter));

	}

}
