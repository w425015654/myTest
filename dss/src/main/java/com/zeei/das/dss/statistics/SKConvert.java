/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：DataStatistical.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:10:42
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:10:42 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：DataStatistical 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component("skConvert")
public class SKConvert {

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	StroageService stroageService;

	@Autowired
	Publish publish;

	@Autowired
	AQIStatistical aqiStatistical;

	// 6因子 O3 NO2 CO SO2 PM10 PM25
	public static String[] pulloteCodes = new String[] { AirSixParam.O3, AirSixParam.NO2, AirSixParam.CO,
			AirSixParam.SO2, AirSixParam.PM10, AirSixParam.PM25, "a01001", "a01006" };

	private static Logger logger = LoggerFactory.getLogger(SKConvert.class);

	// 溫度
	public static String tPulloteCode = "a01001";
	// 气压
	public static String pPulloteCode = "a01006";

	// 标况转实况 标准参数值
	public static Double askp = 0.9161;

	// 标准大气压
	public static Double pbk = 298.0;

	// 标准温度
	public static Double tbk = 273.0;

	/**
	 * 
	 * processSKData:数据处理
	 *
	 * @param tableNames
	 */
	public void processSKData(StationVO station, Date dataTime) {

		try {
			String pointCode = station.getPointCode();

			String ST = station.getST();
			String CN = station.getCN();

			// 非空氣站數據不進行轉換
			if (!SystemTypeCode.AIR.equals(ST)) {
				return;
			}


			String beginTime = "";
			String endTime = DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss");

			switch (CN) {
			case DataType.MINUTEDATA:
				int interval = station.getmInterval();
				beginTime = DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, interval * (-1)), "yyyy-MM-dd HH:mm:ss");
				break;
			case DataType.HOURDATA:
				beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, -1), "yyyy-MM-dd HH:mm:ss");
				break;
			case DataType.DAYDATA:
				// 空气站 日数据统计 01-00
				if (SystemTypeCode.AIR.equals(ST)) {
					endTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
					beginTime = String.format("%s 01:00:00",
							DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, -1), "yyyy-MM-dd"));

				} else {
					endTime = String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
					beginTime = String.format("%s 00:00:00",
							DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, -1), "yyyy-MM-dd"));
				}

				break;
			case DataType.MONTHDATA:
				beginTime = DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, -1), "yyyy-MM-dd HH:mm:ss");
				break;			
			}

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			if (StringUtil.isEmptyOrNull(tableName)) {
				logger.warn(String.format(" 實況数据统计 站点：%s 数据类型：%s 表名为空", pointCode, CN));
				return;
			}

			List<MonitorDataVO> metadata = queryDataService.queryDataByCondition(tableName, pointCode, beginTime,
					endTime, Arrays.asList(pulloteCodes), ST);

			Double psk = 0d;
			Double tsk = 0d;

			List<MonitorDataVO> rets = new ArrayList<MonitorDataVO>();

			if (metadata != null && metadata.size() > 0) {

				// 查询实况温度
				Optional<MonitorDataVO> oskTvo = metadata.stream()
						.filter(o -> tPulloteCode.equalsIgnoreCase(o.getPolluteCode())).findFirst();

				if (oskTvo.isPresent()) {
					tsk = oskTvo.get().getAuditValue();
				} 
				// 查询实况温度
				Optional<MonitorDataVO> oskPvo = metadata.stream()
						.filter(o -> pPulloteCode.equalsIgnoreCase(o.getPolluteCode())).findFirst();

				if (oskTvo.isPresent()) {
					psk = oskPvo.get().getAuditValue();
				} 
				
				// 固态PM2.5 PM10 标况转实况参数
				Double pp = 0d;

				for (MonitorDataVO rv : metadata) {

					MonitorDataVO vo = JSON.parseObject(JSON.toJSONString(rv), MonitorDataVO.class);

					String pulloteCode = vo.getPolluteCode();
					
					if(tPulloteCode.equalsIgnoreCase(pulloteCode) || pPulloteCode.equalsIgnoreCase(pulloteCode)){
						
						continue;
					}

					Double auditAvg = rv.getAuditValue();
					Double avg = rv.getDataValue();
					Double max = rv.getMaxValue();
					Double min = rv.getMinValue();

					switch (pulloteCode) {
					case AirSixParam.O3:
					case AirSixParam.NO2:
					case AirSixParam.CO:
					case AirSixParam.SO2:
						pp = askp;
						break;
					case AirSixParam.PM10:
					case AirSixParam.PM25:
						// result = C实况 = C标况 * (P实况/P标况) * (T标况/T实况);
						if (tsk != null && tsk != 0) {
							pp = (psk / pbk) * (tbk / tsk);
						}

						break;
					default:
						break;
					}
					
					String skPulloteCode=null;
					
					switch (pulloteCode) {
					case AirSixParam.O3:
						skPulloteCode=AirSixParam.SK_O3;
						break;
					case AirSixParam.NO2:
						skPulloteCode=AirSixParam.SK_NO2;
						break;
					case AirSixParam.CO:
						skPulloteCode=AirSixParam.SK_CO;
						break;
					case AirSixParam.SO2:
						skPulloteCode=AirSixParam.SK_SO2;				
						break;
					case AirSixParam.PM10:
						skPulloteCode=AirSixParam.SK_PM10;
						break;
					case AirSixParam.PM25:
						skPulloteCode=AirSixParam.SK_PM25;
						break;
					
					}

					if (auditAvg != null && pp != null && pp > 0) {
						auditAvg = Arith.mul(auditAvg, pp);
					}
					if (avg != null && pp != null && pp > 0) {
						avg = Arith.mul(avg, pp);
					}
					if (max != null && pp != null && pp > 0) {
						max = Arith.mul(max, pp);
					}
					if (min != null && pp != null && pp > 0) {
						min = Arith.mul(min, pp);
					}

					vo.setAuditValue(auditAvg);
					vo.setPolluteCode(skPulloteCode);
					vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
					vo.setDataValue(avg);
					vo.setMaxValue(max);
					vo.setMinValue(min);

					rets.add(vo);

				}
			}

			if (rets != null && rets.size() > 0) {

				logger.info("标况转实况:"+JSON.toJSONString(rets) );	
				
				stroageService.insertDataBatch(tableName, rets);

				
			}

		} catch (

		Exception e) {
			logger.error("标况转实况数据分/小时/日 异常:", e);

		}
	}

}
