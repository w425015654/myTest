/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：ManualStatistics.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月10日上午11:07:13
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月10日上午11:07:13 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：ManualStatistics 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Component
public class ManualStatistics {

	@Autowired
	StatisticsHanlder statisticsHanlder;

	@Autowired
	YMDataStatistical ymDataStatistical;
	
	@Autowired
	ConnectivityStatistics connectivityStatistics;
	
	@Autowired
	O38Statistics o38Statistics;

	@Autowired
	Publish publish;
	
		
	public void handler(JSONObject obj) {

		try {
			String CN = obj.getString("CN");
			Date beginTime = obj.getDate("beginTime");
			Date endTime = obj.getDate("endTime");
			String MN = obj.getString("MN");
			String DST = obj.getString("DST");
			String ST = obj.getString("ST");

			Date dateTime = beginTime;

			Collection<StationVO> stations = DssService.stationMap.values();

			if (!StringUtil.isEmptyOrNull(MN)) {
				stations = stations.stream().filter(o -> Arrays.asList(MN.split(",")).contains(o.getMN()))
						.collect(Collectors.toList());

			}

			if (!StringUtil.isEmptyOrNull(ST)) {
				stations = stations.stream().filter(o -> Arrays.asList(ST.split(",")).contains(o.getST()))
						.collect(Collectors.toList());
			}

			// 站点数据上报周期
			for (StationVO station : stations) {

				try {
					while (dateTime.getTime() <= endTime.getTime()) {
						try {
							
							//logger.info("手动统计时间："+DateUtil.dateToStr(dateTime, "yyyy-MM-dd HH:mm:ss") +"-----"+CN);
							
							if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
								ymDataStatistical.statisticalHandler(station, CN, dateTime);
								
								// 油烟系统 统计连通个数
								connectivityStatistics.statisticalHandler(station, CN, dateTime);

								if (SystemTypeCode.AIR.equals(ST)) {
									// O38小时滑动均值统计
									o38Statistics.statisticsHandler(station, CN, dateTime, "1");
								}

							} else {
								JSONObject dataCycle = new JSONObject();
								dataCycle.put("DATATIME", DateUtil.dateToStr(dateTime, "yyyy-MM-dd HH:mm:ss"));
								dataCycle.put("CN", CN);
								dataCycle.put("MN", station.getMN());
								dataCycle.put("ST", station.getST());
								dataCycle.put("DST", DST);
								dataCycle.put("MANUAL", "1");
								// statisticsHanlder.handler(dataCycle);
								String json = JSON.toJSONString(dataCycle);
								
								
								//System.out.println("手工统计："+json);
								publish.send(Constant.MQ_QUEUE_CYCLE, json);

							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						switch (CN) {

						case DataType.MINUTEDATA:
							dateTime = DateUtil.dateAddMin(dateTime, station.getmInterval());
							break;
						case DataType.HOURDATA:
							dateTime = DateUtil.dateAddHour(dateTime, station.gethInterval());
							break;
						case DataType.DAYDATA:
							dateTime = DateUtil.dateAddDay(dateTime, 1);
							break;
						case DataType.MONTHDATA:
							dateTime = DateUtil.dateAddMonth(dateTime, 1);
							break;
						case DataType.YEARDATA:
							dateTime = DateUtil.dateAddYear(dateTime, 1);
							break;
						}
					}

					dateTime = beginTime;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
