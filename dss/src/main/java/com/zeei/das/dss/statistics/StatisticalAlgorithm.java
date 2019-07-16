/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StatisticalUtil.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月25日下午3:28:06
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月25日下午3:28:06 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.SitePolluterVo;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：StatisticalUtil 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class StatisticalAlgorithm {
	private static Logger logger = LoggerFactory.getLogger(StatisticalAlgorithm.class);

	@Autowired
	QueryDataService queryDataService;

	/**
	 * 
	 * calculationMinFlow:分钟数据流量统计
	 *
	 * @param ST
	 * @param CN
	 * @param pointCode
	 * @param dataTime
	 * @return MonitorDataVO
	 */
	public MonitorDataVO calculationMinFlow(String ST, String pointCode, String MN, String beginTime, String endTime,
			String polluteCode) {

		try {

			Date time = DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss");

			String CN = DataType.RTDATA;
			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, time);

			List<MonitorDataVO> metadata = queryDataService.queryDataByCondition(tableName, pointCode, beginTime,
					endTime, Arrays.asList(new String[] { polluteCode }), ST);

			StationVO station = DssService.stationMap.get(MN);

			int interval = 30;

			if (station != null) {
				interval = station.getrInterval();
			}

			if (metadata != null && metadata.size() > 0) {

				try {

					DoubleSummaryStatistics statistics = metadata.stream()
							.filter(o -> o.getDataValue() != null && o.getDataValue() > 0)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValue));

					// 计算有效数据均值
					DoubleSummaryStatistics validStatistics = metadata.stream()
							.filter(o -> o.getDataValue() != null && o.getIsValided() == 1 && o.getDataValue() > 0)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValue));

					// 计算有效数据均值
					DoubleSummaryStatistics auditStatistics = metadata.stream()
							.filter(o -> o.getAuditValue() != null && o.getAuditValue() > 0)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValue));

					// 计算有效数据均值
					DoubleSummaryStatistics auditValidStatistics = metadata.stream()
							.filter(o -> o.getAuditValue() != null && o.getIsValided() == 1 && o.getAuditValue() > 0)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValue));

					if (statistics != null) {

						double vcount = 0;

						if (validStatistics != null) {
							vcount = validStatistics.getCount();
						}

						int valid = calculationValid(ST, CN, MN, vcount, polluteCode);

						Double min = null;
						Double max = null;
						Double sum = null;
						Double avg = null;

						Double asum = null;
						Double aavg = null;

						String dataFlag = calculationFlag(ST, CN, MN, metadata, polluteCode);

						if (valid == 1) {

							if (validStatistics != null) {
								min = validStatistics.getMin();
								max = validStatistics.getMax();
								sum = validStatistics.getSum();
								avg = validStatistics.getAverage();
							}
							if (auditValidStatistics != null) {
								asum = auditValidStatistics.getSum();
								aavg = auditValidStatistics.getAverage();
							}
							dataFlag = "N";
						} else {
							min = statistics.getMin();
							max = statistics.getMax();
							sum = statistics.getSum();
							avg = statistics.getAverage();
							if (auditStatistics != null) {
								asum = auditStatistics.getSum();
								aavg = auditStatistics.getAverage();
							}
						}

						// 流量单位 m3/h, 1(m3/h)=1/3600(m3/s)

						// 上报流量单位是m3/h

						// 样本累计方式计算
						/*
						 * if (sum != null) { sum = sum * interval / 3600; }
						 * 
						 * if (asum != null) { asum = asum * interval / 3600; }
						 */

						// 均值方法 直接去avg,注意单位换算

						// L/S->L/H avg*36000/interval

						/*if (avg != null) {							
							avg = avg * 36000 / interval;
						}
						
						if (aavg != null) {
							aavg = aavg * 36000 / interval;
						}*/
						
						
						MonitorDataVO vo = new MonitorDataVO();
						vo.setDataTime(beginTime);
						vo.setDataValue(avg);
						vo.setMaxValue(max);
						vo.setMinValue(min);
						vo.setPolluteCode(polluteCode);
						vo.setDataFlag(dataFlag);
						vo.setAuditValue(aavg);
						vo.setIsValided(valid);
						vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
						vo.setPointCode(pointCode);
						return vo;
					}
				} catch (Exception e) {
					logger.error("分钟数据流量统计异常:" + e.toString());
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 
	 * calculationMinEmission:分钟数据排放量统计
	 *
	 * @param ST
	 * @param CN
	 * @param pointCode
	 * @param dataTime
	 * @return MonitorDataVO
	 */
	public MonitorDataVO calculationMinEmission(String ST, String MN, String beginTime, String endTime,
			MonitorDataVO pullteVo, MonitorDataVO flowVo) {
		try {

			String polluteCode = pullteVo.getPolluteCode();
			String pointCode = pullteVo.getPointCode();

			// 流量因子为空计算流量
			if (flowVo == null) {

				String flowFactor = DssService.flowFactor.get(ST);

				if (StringUtil.isEmptyOrNull(flowFactor)) {
					return null;
				}

				flowVo = calculationMinFlow(ST, pointCode, MN, beginTime, endTime, flowFactor);
			}

			if (flowVo != null) {

				// 流量单位 m3/h,浓度一般 mg/L 1(m3/h)=1/3600(m3/s)
				// 1(mg/L)=1/1000(kg/m3) 1L=1000(m3)

				Double emission = null;

				Double aemission = null;

				if (flowVo.getDataValue() != null && pullteVo.getDataValue() != null) {
					emission = flowVo.getDataValue() * pullteVo.getDataValue() / 1000000;
				}

				if (flowVo.getAuditValue() != null && pullteVo.getAuditValue() != null) {
					aemission = flowVo.getAuditValue() * pullteVo.getAuditValue() / 1000000;
				}

				MonitorDataVO vo = new MonitorDataVO();

				vo.setDataValue(emission);
				vo.setMaxValue(emission);
				vo.setMinValue(emission);
				vo.setAuditValue(aemission);
				vo.setDataTime(beginTime);
				vo.setIsValided(pullteVo.getIsValided());
				vo.setPolluteCode(String.format("%s-Cou", polluteCode));
				vo.setDataFlag(pullteVo.getDataFlag());
				vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				vo.setPointCode(pointCode);

				return vo;

			}

		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 
	 * calculationFlag:数据有效性判断
	 *
	 * @param ST
	 * @param CN
	 * @param count
	 * @return String
	 */
	public int calculationValid(String ST, String CN, String MN, double count, String polluter) {

		int valid = 1;
		int total = 1;
		StationVO station = DssService.stationMap.get(MN);
		int interval = 1;
		if (station != null) {
			switch (CN) {
			case DataType.MINUTEDATA:
				interval = station.getrInterval();
				total = 60 / interval;
				break;
			case DataType.HOURDATA:
				interval = station.getmInterval();
				total = 60 / interval;
				break;
			case DataType.DAYDATA:
				interval = station.gethInterval();
				// 特殊小时周期数据处理
				if (StringUtils.isNotEmpty(polluter) && StringUtils.isNotEmpty(station.getPointCode())) {
					Map<String, List<SitePolluterVo>> siteMap = DssService.waterHourPollutes
							.get(station.getPointCode());
					if (siteMap != null && siteMap.containsKey(polluter) && !siteMap.get(polluter).isEmpty()) {
						interval = siteMap.get(polluter).get(0).getHhcycletime();
					}
				}
				total = 24 / interval;

				break;
			case DataType.MONTHDATA:
				total = 30;
				break;
			case DataType.YEARDATA:
				total = 12;
				break;
			}
		}

		Double r = (count * 1.0) / total;

		// 空气站日数据的有效性 要大于20个小时数据
		if (SystemTypeCode.AIR.equals(ST) && DataType.DAYDATA.equals(CN) && count * interval < 20) {
			valid = 0;
			// 有效个数小于75% 数据无效
		} else if (r < 0.75) {
			valid = 0;
		}

		return valid;
	}

	/**
	 * 
	 * calculationFlag:数据标识判断
	 *
	 * @param ST
	 * @param CN
	 * @param count
	 * @return String
	 */
	public String calculationFlag(String ST, String CN, String MN, List<MonitorDataVO> ddl, String polluter) {

		// 数据标识判定依据，有效个数>=75% 标识未N，否则看统计数据个数，统计数据个数<=75% 标识为H 统计个数不足,若统计个数大于75%
		// 则标识为标识个数最大的

		String dataFlag = "N";
		int total = 1;
		StationVO station = DssService.stationMap.get(MN);
		int interval = 1;
		if (station != null) {
			switch (CN) {
			case DataType.MINUTEDATA:
				interval = station.getrInterval();
				total = 60 / interval;
				break;
			case DataType.HOURDATA:
				interval = station.getmInterval();
				total = 60 / interval;
				break;
			case DataType.DAYDATA:
				interval = station.gethInterval();
				// 特殊小时周期数据处理
				if (StringUtils.isNotEmpty(polluter) && StringUtils.isNotEmpty(station.getPointCode())) {
					Map<String, List<SitePolluterVo>> siteMap = DssService.waterHourPollutes
							.get(station.getPointCode());
					if (siteMap != null && siteMap.containsKey(polluter) && !siteMap.get(polluter).isEmpty()) {
						interval = siteMap.get(polluter).get(0).getHhcycletime();
					}
				}
				total = 24 / interval;
				break;
			case DataType.MONTHDATA:
				total = 30;
				break;
			case DataType.YEARDATA:
				total = 12;
				break;
			}
		}
		// 有效数据
		List<MonitorDataVO> validList = ddl.stream().filter(
				o -> o.getAuditValueRounding() != null && o.getIsValided() == 1 && o.getAuditValueRounding() >= 0)
				.collect(Collectors.toList());
		// 统计个数
		double count = 0;
		if (validList != null) {
			count = validList.size();
		}
		Double r = (count * 1.0) / total;

		// 空气站日数据的有效性 要大于20个小时数据
		if (SystemTypeCode.AIR.equals(ST) && DataType.DAYDATA.equals(CN) && count * interval < 20) {
			dataFlag = "H";
		} else if (r < 0.75) {
			dataFlag = "H";
		} else {
			double validCount = 0;
			if (validList != null) {
				validCount = validList.size();
			}
			Double vr = (validCount * 1.0) / total;

			if (vr < 0.75 && ddl != null) {
				Map<String, Long> doubleSummaryStatistics = new HashMap<String, Long>();
				doubleSummaryStatistics = ddl.stream().filter(o -> !StringUtil.isEmptyOrNull(o.getDataFlag()))
						.collect(Collectors.groupingBy(MonitorDataVO::getDataFlag, Collectors.counting()));

				if (doubleSummaryStatistics != null && doubleSummaryStatistics.size() > 0) {
					Collection<Long> array = doubleSummaryStatistics.values();
					Long max = Collections.max(array);

					for (Map.Entry<String, Long> entry : doubleSummaryStatistics.entrySet()) {

						if (entry.getValue().equals(max)) {
							dataFlag = entry.getKey();
						}
					}
				}

			}
		}

		return dataFlag;
	}

	/**
	 * 
	 * calculationPHAvg:计算ph值的均值
	 * 
	 * @param metadata
	 *            数据集
	 * @param PH
	 *            ph的因子编码
	 * @param valid
	 *            是否是有效数据
	 * @param isaudit
	 *            是否是审核值
	 * @return Double
	 */
	public Double calculationPHAvg(List<MonitorDataVO> metadata, String PH, int valid, int isaudit) {
		Double phAvg = null;
		try {
			/*
			 * pH= -lg[H+] 反过来就是 [H+]=10^（-PH） 就是10的-PH次方就是氢离子浓度. 比如PH=3
			 * 那么氢离子浓度就是10^-3=1/1000=0.001mol/L
			 */
			OptionalDouble avgOptional = null;

			if (isaudit == 1) {
				if (valid == 1) {
					avgOptional = metadata.stream().filter(o -> PH.equals(o.getPolluteCode()) && o.getIsValided() == 1)
							.mapToDouble(o -> Math.pow(10, o.getAuditValue() * (-1))).average();
				} else {
					avgOptional = metadata.stream().filter(o -> PH.equals(o.getPolluteCode()))
							.mapToDouble(o -> Math.pow(10, o.getAuditValue() * (-1))).average();
				}
			} else {

				if (valid == 1) {
					avgOptional = metadata.stream().filter(o -> PH.equals(o.getPolluteCode()) && o.getIsValided() == 1)
							.mapToDouble(o -> Math.pow(10, o.getDataValue() * (-1))).average();
				} else {
					avgOptional = metadata.stream().filter(o -> PH.equals(o.getPolluteCode()))
							.mapToDouble(o -> Math.pow(10, o.getDataValue() * (-1))).average();
				}
			}

			if (avgOptional != null) {
				Double avg = avgOptional.getAsDouble();
				phAvg = Math.log10(avg) * (-1);
			}
		} catch (Exception e) {
			logger.error("计算ph值的均值异常:", e);
		}
		return phAvg;
	}

	/**
	 * 
	 * calculationWeightingAvg:加权平均
	 *
	 * @param polluteEmission
	 *            污染物排放量
	 * @param emission
	 *            总排放量
	 * @param ST
	 *            系统类型
	 * @return Double
	 */

	public Double calculationWeightingAvg(Double polluteEmission, Double emission, String ST) {

		if (polluteEmission == null || emission == null || emission == 0) {
			return null;
		}

		Double avg = polluteEmission / emission;

		// 废水
		if (SystemTypeCode.WAT.equals(ST)) {
			avg = avg * 1000;
		} else {
			// 废气 烟气
			avg = avg * 1000000;
		}

		return avg;
	}

}
