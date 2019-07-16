/** 
* Copyright (C) 2012-2019 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：DoubtfulAlarm.java
* 包  名  称：com.zeei.das.aas.alarm.custom
* 文件描述：
* 创建日期：2019年6月14日上午10:16:14
* 
* 修改历史
* 1.0 lian.wei 2019年6月14日上午10:16:14 创建文件
*
*/

package com.zeei.das.aas.alarm.custom;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.aas.AasService;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.service.QueryDataService;
import com.zeei.das.aas.utils.PartitionTableUtil;
import com.zeei.das.aas.vo.DoubtfulVo;
import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.AuditLogVO;

/**
 * @类型名称：DoubtfulAlarm @类型描述： @功能描述：
 * @创建作者：lian.wei
 */
@Component("doubtfulAlarm")
public class DoubtfulAlarm {
	private static Logger logger = LoggerFactory.getLogger(DoubtfulAlarm.class);

	public static Map<String, Integer> errTimeMap = new HashMap<>();
	// 初始化时间集，表示污染物在多长时间未改变则增加存疑标识
	static {
		errTimeMap.put(AirSixParam.CO, 6);
		errTimeMap.put(AirSixParam.O3, 6);
		errTimeMap.put(AirSixParam.NO, 6);
		errTimeMap.put(AirSixParam.NO2, 6);
		errTimeMap.put(AirSixParam.SO2, 6);

		errTimeMap.put(AirSixParam.SK_CO, 6);
		errTimeMap.put(AirSixParam.SK_O3, 6);
		errTimeMap.put(AirSixParam.SK_NO, 6);
		errTimeMap.put(AirSixParam.SK_NO2, 6);
		errTimeMap.put(AirSixParam.SK_SO2, 6);

		errTimeMap.put(AirSixParam.PM10, 4);
		errTimeMap.put(AirSixParam.PM25, 4);
		errTimeMap.put(AirSixParam.SK_PM10, 4);
		errTimeMap.put(AirSixParam.SK_PM25, 4);
	}

	@Autowired
	Publish publish;

	@Autowired
	QueryDataService queryDataService;

	public void alarmHandler(JSONObject data) {
		try {
			String MN = data.getString("MN");
			String CN = data.getString("CN");
			String ST = data.getString("ST");
			String pointCode = data.getString("ID");
			StationVO station = AasService.stationMap.get(MN);
			
			// 只针对小时数据
			if (!DataType.HOURDATA.equals(CN)) {
				return;
			}

			JSONObject CP = data.getJSONObject("CP");

			if (CP == null || CP.size() < 1) {
				String err = String.format("站点【%s】数据为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			JSONArray params = CP.getJSONArray("Params");

			if (params == null || params.size() < 1) {
				String err = String.format("站点【%s】数据为空！", MN);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, err));
				return;
			}

			// 对日期取整
			Date dataTime = DateUtil.strToDate(DateUtil.dateToStr(CP.getDate("DataTime"), "yyyy-MM-dd HH") + ":00:00",
					"yyyy-MM-dd HH:mm:ss");

			for (Object p : params) {

				JSONObject param = (JSONObject) p;
				String polluteCode = param.getString("ParamID");

				// 只比较SO2、NO、O3、CO、pm25和pm10的数值，否则跳过
				if (errTimeMap.containsKey(polluteCode)) {
					Double auditData = param.getDouble("Avg");

					// 站点mn加污染因子编码组合key
					String douKey = MN + "+" + polluteCode;
					DoubtfulVo doubtfulVo = AasService.doubtfulMap.get(douKey);
					// 判断map是否已存在站点对应因子记录，有则进一步判断，没有则新增
					if (doubtfulVo != null && doubtfulVo.getAudValue() != null
							&& doubtfulVo.getAudValue().equals(auditData)) {

						// 与上次时间间隔超过一小时，即中间缺数，重新开始计算
						if (DateUtil.dateDiffHour(doubtfulVo.getLastDataTime(), dataTime) > 1) {

							doubtfulVo.setAudValue(auditData);
							doubtfulVo.setDataTime(dataTime);
							doubtfulVo.setLastDataTime(dataTime);
							return;
						}
						
						// 过滤掉重复上报的小时数据
						if ((dataTime.getTime() > doubtfulVo.getLastDataTime().getTime()) && (DateUtil.dateDiffHour(doubtfulVo.getLastDataTime(), dataTime) == 1)) {
							// 判断数值是否连续不变且时间超过限定，不是则跳过
							if (DateUtil.dateDiffHour(doubtfulVo.getDataTime(), dataTime) >= errTimeMap.get(polluteCode)) {

								// 存疑数据连同前面的六小时（四小时）一起打上标识
								Date beginTime = DateUtil.dateAddHour(dataTime, (0 - (errTimeMap.get(polluteCode) + 1 )));
								String sBeginTime = String.format("%s:00:00", DateUtil.dateToStr(beginTime, "yyyy-MM-dd HH"));
								String sEndTime =String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH"));
								String tableName = PartitionTableUtil.getTableName(ST, DataType.HOURDATA, pointCode,
										dataTime);
								updateDoubtfulHH(tableName, sBeginTime, sEndTime, station, polluteCode);
							}
						}
						
						// 设置上次数据时间
						doubtfulVo.setLastDataTime(dataTime);
					} 
					else {
						// 过滤掉重复上报的小时数据，数据库入库时，相同的只取第一个
						if ((doubtfulVo == null) || ((doubtfulVo != null) && (doubtfulVo.getLastDataTime() != null)
								&& (dataTime.getTime() > doubtfulVo.getLastDataTime().getTime()) && (DateUtil.dateDiffHour(doubtfulVo.getLastDataTime(), dataTime) == 1)))
						{
							doubtfulVo = new DoubtfulVo();
							doubtfulVo.setAudValue(auditData);
							doubtfulVo.setDataTime(dataTime);
							doubtfulVo.setLastDataTime(dataTime);
							AasService.doubtfulMap.put(douKey, doubtfulVo);
							String sDataTime = String.format("%s:00:00", DateUtil.dateToStr(doubtfulVo.getDataTime(), "yyyy-MM-dd HH"));
							String sLastDataTime = String.format("%s:00:00", DateUtil.dateToStr(doubtfulVo.getLastDataTime(), "yyyy-MM-dd HH"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	private void updateDoubtfulHH(String tableName, String beginTime, String endTime, StationVO station, 
			String polluteCode) {
		logger.error("进入updateDoubtfulHH");
		
		List<String> pollutes = new ArrayList<>();
		pollutes.add(polluteCode);
		
		List<MonitorDataVO> monDatas = queryDataService.queryMonitorData(tableName, SystemTypeCode.AIR, beginTime,
				endTime, station.getPointCode(), pollutes);

		if (CollectionUtils.isNotEmpty(monDatas)) {
			queryDataService.updateDoubtfulHH(monDatas);
			
			for (MonitorDataVO it : monDatas)
			{
				// 发送审核日志
				PolluteVO pollute = AasService.polluteMap.get(it.getPolluteCode());

				String polluteName = "";
				if (pollute != null) {
					polluteName = pollute.getPolluteName();
				}
				AuditLogVO log = new AuditLogVO();
				String info = String.format(
						"由于%s下%s因子数据长时间%s未发生变化(SO2、NO、O3、CO为6小时，PM10、PM2.5为4小时)数据审核设置为可疑数据",
						station.getPointName(), polluteName,
						it.getDataTime());

				log.setPointCode(it.getPointCode());
				log.setPolluteCode(it.getPolluteCode());
				log.setRemark(info);
				log.setDataTime(DateUtil.strToDate(it.getDataTime(),
						"yyyy-MM-dd HH:mm:ss"));

				Map<String, Object> log_map = new HashMap<String, Object>();
				log_map.put("logType", LogType.LOG_TYPE_AUDIT);
				log_map.put("logContent", log);
				logger.info(info);
				publish.send(Constant.MQ_QUEUE_LOGS, JSON.toJSONString(log_map));
			}
		}
	}
}
