/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：ConnectivityRatioStatistics.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:14:56
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:14:56 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.AqiService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * @类名称：O38Statistics @类描述：O3 8小时滑动均值统计/1小时最大值 @功能描述：O3 8小时滑动均值统计/1小时最大值
 * @创建作者：quanhongsheng
 */
@Component
public class O38Statistics {

	@Autowired
	StroageService stroageService;

	@Autowired
	StatisticalHelper statisticalHelper;

	@Autowired
	AqiService aqiService;

	private static Logger logger = LoggerFactory.getLogger(O38Statistics.class);

	public void statisticsHandler(StationVO station, String CN, Date dataTime, String dataStatus) {

		try {

			String pointCode = station.getPointCode();
			String beginTime = null;
			String endTime = null;

			List<String> polluteCodes = new ArrayList<String>();

			List<MonitorDataVO> metadata = new ArrayList<MonitorDataVO>();

			Map<String, List<MonitorDataVO>> map = null;

			switch (CN) {
			case DataType.MONTHDATA:
				polluteCodes.add(AirSixParam.SK_O3 + "-8H");
				polluteCodes.add(AirSixParam.O3 + "-8H");
				beginTime = String.format("%s-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM"));
				endTime = String.format("%s-01 00:00:00",
						DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, 1), "yyyy-MM"));
				metadata = aqiService.queryMointorDataDD(pointCode, null, polluteCodes, beginTime, endTime);

				if (metadata != null && metadata.size() > 0) {
					map = metadata.stream()
							.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
							.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode, Collectors.toList()));
					if (map != null) {
						for (Map.Entry<String, List<MonitorDataVO>> entry : map.entrySet()) {
							List<MonitorDataVO> list = entry.getValue();
							O38MonthHandler(station, CN, dataTime, entry.getKey(), list, dataStatus);
						}
					}
				}
				break;
			case DataType.YEARDATA:
				polluteCodes.add(AirSixParam.SK_O3 + "-8H");
				polluteCodes.add(AirSixParam.O3 + "-8H");
				beginTime = String.format("%s-01-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy"));
				endTime = String.format("%s-01-01 00:00:00",
						DateUtil.dateToStr(DateUtil.dateAddYear(dataTime, 1), "yyyy"));
				metadata = aqiService.queryMointorDataDD(pointCode, null, polluteCodes, beginTime, endTime);

				if (metadata != null && metadata.size() > 0) {
					map = metadata.stream()
							.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
							.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode, Collectors.toList()));
					if (map != null) {
						for (Map.Entry<String, List<MonitorDataVO>> entry : map.entrySet()) {
							List<MonitorDataVO> list = entry.getValue();
							O38YearHandler(station, CN, dataTime, entry.getKey(), list, dataStatus);
						}
					}
				}

				break;
			default:
				polluteCodes.add(AirSixParam.SK_O3);
				List<String> pointCodes = new ArrayList<>();
				if (StringUtils.isNoneBlank(pointCode)) {
					pointCodes.add(pointCode);
				}
				polluteCodes.add(AirSixParam.O3);
				beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
				endTime = String.format("%s 01:00:00",
						DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));
				metadata = aqiService.queryMointorDataHH(pointCodes, null, polluteCodes, beginTime, endTime);

				if (metadata != null && metadata.size() > 0) {
					map = metadata.stream()
							.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
							.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode, Collectors.toList()));
					if (!map.isEmpty()) {
						for (Map.Entry<String, List<MonitorDataVO>> entry : map.entrySet()) {
							List<MonitorDataVO> list = entry.getValue();
							O38DayHandler(station, CN, dataTime, entry.getKey(), list, dataStatus);
							O31Handler(station, CN, dataTime, entry.getKey(), list, dataStatus);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("O3数据通计:", e);
		}

	}

	/**
	 * 
	 * O31DayHandler:日数据O31最大值
	 * 
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @param metadata
	 *            void
	 */
	private void O31Handler(StationVO station, String CN, Date dataTime, String polluteCode,
			List<MonitorDataVO> metadata, String dataStatus) {

		try {
			String pointCode = station.getPointCode();
			String MN=station.getMN();
			int isValided = 1;

			MonitorDataVO vo = new MonitorDataVO();

			if (metadata != null && metadata.size() > 0) {

				// 按审核值倒序
				Collections.sort(metadata, new Comparator<MonitorDataVO>() {
					public int compare(MonitorDataVO arg0, MonitorDataVO arg1) {
						Double value0 = arg0.getAuditValue();
						Double value1 = arg1.getAuditValue();
						if (value1 > value0) {
							return 1;
						} else if (value1 == value0) {
							return 0;
						} else {
							return -1;
						}
					}
				});

				// 设置审核值
				vo.setAuditValue(metadata.get(0).getAuditValue());

				if ("1".equalsIgnoreCase(dataStatus)) {
					isValided = metadata.get(0).getIsValided();
				}

				// 按原始值倒序
				Collections.sort(metadata, new Comparator<MonitorDataVO>() {
					public int compare(MonitorDataVO arg0, MonitorDataVO arg1) {
						Double value0 = arg0.getDataValue();
						Double value1 = arg1.getDataValue();
						if (value1 > value0) {
							return 1;
						} else if (value1 == value0) {
							return 0;
						} else {
							return -1;
						}
					}
				});

				if ("0".equalsIgnoreCase(dataStatus)) {
					isValided = metadata.get(0).getIsValided();
				}

				vo.setDataValue(metadata.get(0).getDataValue());
				vo.setMaxValue(metadata.get(0).getDataValue());
				vo.setMinValue(metadata.get(0).getDataValue());

			}

			vo.setDataType(CN);
			vo.setPolluteCode(polluteCode + "-1H");
			vo.setPointCode(pointCode);
			vo.setIsValided(isValided);
			vo.setDataStatus(dataStatus);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));

			String ST = station.getST();
			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			if (!StringUtil.isEmptyOrNull(tableName)) {
				stroageService.insertData(tableName, vo);

				logger.info("O31最大值数据统计：" + JSON.toJSONString(vo));
			}

		} catch (Exception e) {

			logger.error("O31最大值数据统计:", e);
			logger.error(JSON.toJSONString(metadata));
		}
	}

	/**
	 * 
	 * O38DayHandler:日数据O38
	 * 
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @param metadata
	 *            void
	 */
	private void O38DayHandler(StationVO station, String CN, Date dataTime, String polluteCode,
			List<MonitorDataVO> metadata, String dataStatus) {

		try {
			List<Double> avgs = new ArrayList<Double>();
			List<Double> avgs1 = new ArrayList<Double>();
			Double maxAvg = null;
			Double maxAvg1 = null;
			String pointCode = station.getPointCode();
			String beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
			String MN=station.getMN();
			// 有效组数
			int count = 0;
			int isValided = 0;
			Double std=null;
			if (metadata != null && metadata.size() > 0) {

				Date qtime = DateUtil.strToDate(beginTime, "yyyy-MM-dd HH:mm:ss");

				int loop = 17;

				do {

					Date time = qtime;
					Date etime = DateUtil.dateAddHour(qtime, 7);

					List<MonitorDataVO> groups = metadata.stream().filter(o -> DateUtil
							.strToDate(o.getDataTime(), "yyyy-MM-dd HH:mm:ss").getTime() >= time.getTime()
							&& DateUtil.strToDate(o.getDataTime(), "yyyy-MM-dd HH:mm:ss").getTime() <= etime.getTime())
							.collect(Collectors.toList());

					Double avg = null;
					Double sum = null;
					Long size = null;
					DoubleSummaryStatistics statistics = null;

					statistics = groups.stream().filter(o -> o.getAuditValueRounding() != null)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding));

					if (statistics != null && groups.size() >= 6) {
						size = statistics.getCount();
						sum = statistics.getSum();
						avg = Arith.div(size, sum);
						avgs.add(avg);
					}

					Double avg1 = null;
					Double sum1 = null;
					Long size1 = null;
					DoubleSummaryStatistics statistics1 = null;
					statistics1 = groups.stream().filter(o -> o.getAuditValueRounding() != null)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding));

					if (statistics1 != null && groups.size() >= 6) {
						size1 = statistics1.getCount();
						sum1 = statistics1.getSum();
						avg1 = Arith.div(size1, sum1);
						avgs1.add(avg1);
					}

					// 每组有效个数要大于等于6个
					if (groups.size() >= 6) {
						count++;
					}

					qtime = DateUtil.dateAddHour(qtime, 1);
					
					//if(pointCode.equals("29")){
					//	logger.info(String.format("O38日数据统计--%s", JSON.toJSONString(groups)));			
					//}

					loop--;

				} while (loop > 0);

				maxAvg = CollectionUtils.isNotEmpty(avgs) ? Collections.max(avgs) : maxAvg;
				maxAvg1 = CollectionUtils.isNotEmpty(avgs1) ? Collections.max(avgs1) : maxAvg1;
				
				
				if (count >= 14) {
					isValided = 1;
				} else {

					// 小于14个 且最大值O3 8滑动均值 大于浓度限值时有效
					std = DssService.O38.get(pointCode + polluteCode);
					if (std != null && maxAvg1 > std) {
						isValided = 1;
					}
				}
			}			
			
			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
			vo.setDataValue(maxAvg);
			vo.setMaxValue(maxAvg);
			vo.setMinValue(maxAvg);
			vo.setAuditValue(maxAvg1);
			vo.setDataType(CN);
			vo.setPolluteCode(polluteCode + "-8H");
			vo.setPointCode(pointCode);
			vo.setIsValided(isValided);
			vo.setDataStatus(dataStatus);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

			String ST = station.getST();

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			if (!StringUtil.isEmptyOrNull(tableName)) {
				stroageService.insertData(tableName, vo);

				logger.info("O38日数据统计：" + JSON.toJSONString(vo));				
				
			}

		} catch (Exception e) {
			logger.error("O38日数据统计:", e);
			logger.error(JSON.toJSONString(metadata));
		}
	}

	/**
	 * 
	 * O38MonthHandler:月数据O38
	 * 
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @param metadata
	 *            void
	 */
	private void O38MonthHandler(StationVO station, String CN, Date dataTime, String polluteCode,
			List<MonitorDataVO> metadata, String dataStatus) {

		try {
			String pointCode = station.getPointCode();
			String MN=station.getMN();
			Double avg = null;
			Double avg1 = null;
			int isValided = 0;

			if (metadata != null && metadata.size() > 0) {

				// O3 8h滑动均值，按百分位90计算
				List<Double> mete = metadata.stream().filter(o -> o.getDataValueRounding() != null)
						.map(MonitorDataVO::getDataValueRounding).collect(Collectors.toList());
				avg = StatisticalHelper.dataBFW(mete, 90);

				List<Double> mete1 = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
						.map(MonitorDataVO::getAuditValueRounding).collect(Collectors.toList());
				avg1 = StatisticalHelper.dataBFW(mete1, 90);

				if ((DateUtil.getMonth(dataTime) == 2 && metadata.size() >= 25) || metadata.size() >= 27) {
					isValided = 1;
				}

				MonitorDataVO vo = new MonitorDataVO();
				vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
				vo.setDataValue(avg);
				vo.setMaxValue(avg);
				vo.setMinValue(avg);
				vo.setAuditValue(avg1);
				vo.setDataType(CN);
				vo.setPolluteCode(polluteCode);
				vo.setPointCode(pointCode);
				vo.setDataStatus(dataStatus);
				vo.setIsValided(isValided);
				vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

				String ST = station.getST();

				String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

				stroageService.insertYMData(tableName, vo);

				logger.info("O38月数据统计：" + JSON.toJSONString(vo));
			}
		} catch (Exception e) {
			logger.error("O38月数据统计:", e);
		}
	}

	/**
	 * 
	 * O38YearHandler:年数据O38
	 * 
	 * @param station
	 * @param CN
	 * @param dataTime
	 * @param metadata
	 *            void
	 */
	private void O38YearHandler(StationVO station, String CN, Date dataTime, String polluteCode,
			List<MonitorDataVO> metadata, String dataStatus) {

		try {
			String pointCode = station.getPointCode();
			String MN=station.getMN();
			Double avg = null;
			Double avg1 = null;
			int isValided = 0;

			if (metadata != null && metadata.size() > 0) {

				// O3 8h滑动均值，按百分位90计算
				List<Double> mete = metadata.stream().filter(o -> o.getDataValueRounding() != null)
						.map(MonitorDataVO::getDataValueRounding).collect(Collectors.toList());

				avg = StatisticalHelper.dataBFW(mete, 90);

				List<Double> mete1 = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
						.map(MonitorDataVO::getAuditValueRounding).collect(Collectors.toList());
				avg1 = StatisticalHelper.dataBFW(mete1, 90);

				if (metadata.size() >= 324) {
					isValided = 1;
				}

				MonitorDataVO vo = new MonitorDataVO();
				vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
				vo.setDataValue(avg);
				vo.setMaxValue(avg);
				vo.setMinValue(avg);
				vo.setAuditValue(avg1);
				vo.setDataType(CN);
				vo.setPolluteCode(polluteCode);
				vo.setPointCode(pointCode);
				vo.setDataStatus(dataStatus);
				vo.setIsValided(isValided);
				vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

				String ST = station.getST();

				String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

				stroageService.insertYMData(tableName, vo);

				logger.info("O38年数据统计：" + JSON.toJSONString(vo));
			}
		} catch (Exception e) {
			logger.error("O38年数据统计:", e);
		}
	}
}
