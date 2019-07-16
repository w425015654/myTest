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
import java.util.Collections;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：DataStatistical 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class DataStatistical {

	@Autowired
	Publish publish;

	@Autowired
	StatisticalAlgorithm statisticalAlgorithm;

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	StroageService stroageService;

	// 微站控制級別
	private static List<String> microStation = Arrays.asList(new String[] { "KZJB08", "KZJB08" });

	private static Logger logger = LoggerFactory.getLogger(DataStatistical.class);

	public void statisticalHandler(StationVO station, Date dataTime, String DST) {
		try {
			String pointCode = station.getPointCode();

			String ST = station.getST();
			String CN = station.getCN();
			String MN = station.getMN();

			// 地表水小时数据不统计
			if (SystemTypeCode.RIV.equals(ST) && DataType.HOURDATA.equals(CN)) {
				return;
			}

			// 实时数据不参加统计
			if (DataType.RTDATA.equals(CN)) {
				return;
			}

			boolean isStatistical = true;

			if (!((station.isStatsMI() && DataType.MINUTEDATA.equals(CN))
					|| (station.isStatsHH() && DataType.HOURDATA.equals(CN))
					|| (station.isStatsDD() && DataType.DAYDATA.equals(CN)))) {
				isStatistical = false;
			}

			String QCN = CN;

			String endTime = "";
			String beginTime = DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss");

			switch (CN) {
			case DataType.MINUTEDATA:
				if (isStatistical) {
					QCN = DataType.RTDATA;
				}
				int interval = station.getmInterval();

				endTime = DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, interval), "yyyy-MM-dd HH:mm:ss");

				if (SystemTypeCode.AIR.equals(ST)) {
					Date tempDate = DateUtil.dateAddMin(dataTime, interval * (-1));
					tempDate = DateUtil.dateAddSecond(tempDate, station.getrInterval());
					beginTime = DateUtil.dateToStr(tempDate, "yyyy-MM-dd HH:mm:ss");
					endTime = DateUtil.dateToStr(DateUtil.dateAddSecond(dataTime, station.getrInterval()),
							"yyyy-MM-dd HH:mm:ss");
				}
				break;
			case DataType.HOURDATA:
				if (isStatistical) {
					QCN = DataType.MINUTEDATA;
				}

				endTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 1), "yyyy-MM-dd HH:mm:ss");

				if (SystemTypeCode.AIR.equals(ST)) {

					Date tempDate = DateUtil.dateAddHour(dataTime, -1);
					tempDate = DateUtil.dateAddMin(tempDate, station.getmInterval());
					beginTime = DateUtil.dateToStr(tempDate, "yyyy-MM-dd HH:mm:ss");

					endTime = DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, station.getmInterval()),
							"yyyy-MM-dd HH:mm:ss");

				}
				break;
			case DataType.DAYDATA:
				if (isStatistical) {
					QCN = DataType.HOURDATA;
				}
				// 空气站 日数据统计 01-00
				if (SystemTypeCode.AIR.equals(ST)) {
					beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
					endTime = String.format("%s 01:00:00",
							DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));

				} else {
					beginTime = String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
					endTime = String.format("%s 00:00:00",
							DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));
				}
				break;
			case DataType.MONTHDATA:
				QCN = DataType.DAYDATA;
				endTime = DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
				break;
			case DataType.YEARDATA:
				QCN = DataType.MONTHDATA;
				endTime = DateUtil.dateToStr(DateUtil.dateAddYear(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
				break;
			}

			String flowFactor = DssService.flowFactor.get(ST);

			String tableName = PartitionTableUtil.getTableName(ST, QCN, pointCode, dataTime);

			if (StringUtil.isEmptyOrNull(tableName)) {
				logger.warn(String.format(" 数据统计 站点：%s 数据类型：%s 表名为空", pointCode, CN));
				return;
			}

			List<MonitorDataVO> metadata = queryDataService.queryDataByCondition(tableName, pointCode, beginTime,
					endTime, null, ST);

			List<MonitorDataVO> rets = new ArrayList<MonitorDataVO>();

			// 查询统计周期数据
			List<MonitorDataVO> rets1 = new ArrayList<MonitorDataVO>();
			String dateTime = RegularTime.regularDateStr(CN, MN, dataTime);
			String tb = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			rets1 = queryDataService.queryDataByCondition(tb, pointCode, dateTime, dateTime.substring(0, 18) + "1",
					null, ST);

			if (rets1 != null) {
				// 过滤掉虚拟因子数据,Cou因子除外
				rets1 = rets1.stream().filter(o -> !DssService.vFactors.contains(o.getPolluteCode()))
						.collect(Collectors.toList());

			}

			if (metadata != null && metadata.size() > 0) {

				// 过滤掉虚拟因子数据,Cou因子除外
				metadata = metadata.stream().filter(o -> !DssService.vFactors.contains(o.getPolluteCode()))
						.collect(Collectors.toList());

				List<String> pollutes = metadata.stream().map(MonitorDataVO::getPolluteCode).distinct()
						.collect(Collectors.toList());

				// logger.info("---"+JSON.toJSONString(pollutes));

				Map<String, DoubleSummaryStatistics> doubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {

					if (microStation.contains(station.getControlLevel())) {
						// 计算算术均值
						doubleSummaryStatistics = metadata.stream().filter(o -> o.getDataValueRounding() != null)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding)));
					} else {
						// 计算算术均值
						doubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getDataValueRounding() != null && o.getDataValueRounding() >= 0)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding)));
					}

				} catch (Exception e) {
					logger.error("计算算术均值异常:", e);
				}

				Map<String, DoubleSummaryStatistics> validDoubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {
					if (microStation.contains(station.getControlLevel())) {

						// 计算有效数据均值
						validDoubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getDataValueRounding() != null && o.getIsValided() == 1)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding)));
					} else {

						// 计算有效数据均值
						validDoubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getDataValueRounding() != null && o.getIsValided() == 1
										&& o.getDataValueRounding() >= 0)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding)));
					}
				} catch (Exception e) {
					logger.error("计算有效数据均值异常:", e);
				}

				Map<String, DoubleSummaryStatistics> auditDoubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {

					if (microStation.contains(station.getControlLevel())) {

						// 计算审核算术均值
						auditDoubleSummaryStatistics = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
					} else {

						// 计算审核算术均值
						auditDoubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getAuditValueRounding() != null && o.getAuditValueRounding() >= 0)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
					}
				} catch (Exception e) {
					logger.error("计算审核算术均值异常:", e);
				}

				Map<String, DoubleSummaryStatistics> auditValidDoubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {

					if (microStation.contains(station.getControlLevel())) {
						// 计算审核有效数据均值
						auditValidDoubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getAuditValueRounding() != null && o.getIsValided() == 1)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
					} else {
						// 计算审核有效数据均值
						auditValidDoubleSummaryStatistics = metadata.stream()
								.filter(o -> o.getAuditValueRounding() != null && o.getIsValided() == 1
										&& o.getAuditValueRounding() >= 0)
								.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
										Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
					}
				} catch (Exception e) {
					logger.error("计算审核有效数据均值异常:", e);
				}
				// 如果存在，将流量因子放到第一位，防止计算出错
				if (StringUtils.isNotEmpty(flowFactor) && CollectionUtils.isNotEmpty(pollutes)) {

					int index = 0;
					for (int i = 0; i < pollutes.size(); i++) {
						if (pollutes.get(i).equalsIgnoreCase(flowFactor)) {
							index = i;
							break;
						}
					}
					if (index > 0) {
						Collections.swap(pollutes, index, 0);
					}
				}

				for (String polluteCode : pollutes) {

					DoubleSummaryStatistics statistics = null;
					DoubleSummaryStatistics validStatistics = null;
					DoubleSummaryStatistics auditvalidStatistics = null;
					DoubleSummaryStatistics auditStatistics = null;

					if (doubleSummaryStatistics != null) {
						statistics = doubleSummaryStatistics.get(polluteCode);
					}

					if (validDoubleSummaryStatistics != null) {
						validStatistics = validDoubleSummaryStatistics.get(polluteCode);
					}

					if (auditValidDoubleSummaryStatistics != null) {
						auditvalidStatistics = auditValidDoubleSummaryStatistics.get(polluteCode);
					}

					if (auditDoubleSummaryStatistics != null) {
						auditStatistics = auditDoubleSummaryStatistics.get(polluteCode);
					}

					double vcount = 0;

					if (auditvalidStatistics != null) {
						vcount = auditvalidStatistics.getCount();
					}

					// 通过数据数量判断有效性
					int valid = statisticalAlgorithm.calculationValid(ST, CN, MN, vcount, polluteCode);

					Double min = null;
					Double max = null;
					Double avg = null;
					Double auditAvg = null;
					Double sum = null;
					Long count = null;

					String dataFlag = "N";

					if (valid == 0) {

						if (statistics != null) {
							min = statistics.getMin();
							max = statistics.getMax();
							sum = statistics.getSum();
							count = statistics.getCount();
							// Double 計算不精確,改用BigDecimal計算
							// avg = statistics.getAverage();

							avg = Arith.div(count, sum);
						}

						List<MonitorDataVO> ddl = metadata.stream()
								.filter(o -> o.getDataValue() != null && o.getPolluteCode().equals(polluteCode))
								.collect(Collectors.toList());

						dataFlag = statisticalAlgorithm.calculationFlag(ST, CN, MN, ddl, polluteCode);

						if (auditStatistics != null) {

							sum = auditStatistics.getSum();
							count = auditStatistics.getCount();
							auditAvg = Arith.div(count, sum);
						}
					} else {

						if (validStatistics != null) {
							min = validStatistics.getMin();
							max = validStatistics.getMax();
							sum = validStatistics.getSum();
							count = validStatistics.getCount();
							avg = Arith.div(count, sum);
						}

						if (auditvalidStatistics != null) {
							sum = auditvalidStatistics.getSum();
							count = auditvalidStatistics.getCount();
							auditAvg = Arith.div(count, sum);
						}
					}

					if (DataType.DAYDATA.equals(CN) || DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
						// 其他数据类型，污染物排放量/流量统计
						if (polluteCode.endsWith("Cou") || polluteCode.equalsIgnoreCase(flowFactor)) {
							if (valid == 0) {
								if (statistics != null) {
									avg = statistics.getSum();
									min = statistics.getSum();
									max = statistics.getSum();
								}

								if (auditStatistics != null) {
									sum = auditStatistics.getSum();
									count = auditStatistics.getCount();
									auditAvg = auditStatistics.getSum();
								}

							} else {
								if (validStatistics != null) {
									avg = validStatistics.getSum();
									min = validStatistics.getSum();
									max = validStatistics.getSum();
								}
								if (auditvalidStatistics != null) {
									sum = auditvalidStatistics.getSum();
									count = auditvalidStatistics.getCount();
									auditAvg = auditvalidStatistics.getSum();
								}
							}
						}
					}

					// 计算PH的均值
					if (SpecialFactor.PH.equals(polluteCode)) {
						avg = statisticalAlgorithm.calculationPHAvg(metadata, SpecialFactor.PH, valid, 1);
						auditAvg = statisticalAlgorithm.calculationPHAvg(metadata, SpecialFactor.PH, valid, 0);
					}

					MonitorDataVO vo = new MonitorDataVO();
					vo.setDataTime(dateTime);
					vo.setPolluteCode(polluteCode);
					vo.setAuditValue(auditAvg);
					vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
					vo.setPointCode(pointCode);
					vo.setIsValided(valid);
					vo.setDataValue(avg);
					vo.setMaxValue(max);
					vo.setMinValue(min);
					vo.setDataFlag(dataFlag);

					// 污染物排放量统计 小时数据统计按实时数据统计，日数据，月数据按小时数据累计
					if (DataType.HOURDATA.equals(CN)) {

						// 污染物排放量小时数据统计
						if (DssService.emissionFactor.contains(polluteCode) && "1".equalsIgnoreCase(DssService.isCou)
								&& !StringUtil.isEmptyOrNull(flowFactor)) {

							// 没有计算流量计算流量
							Optional<MonitorDataVO> optional = rets.stream()
									.filter(o -> flowFactor.equalsIgnoreCase(o.getPolluteCode())).findFirst();

							MonitorDataVO flowVo = null;

							if (optional.isPresent()) {
								flowVo = optional.get();
							} else if (isStatistical) {
								// 非统计情况下，直接使用原始数据计算，不用统计数据计算，防止出错
								Optional<MonitorDataVO> metaOptional = metadata.stream()
										.filter(u -> flowFactor.equals(u.getPolluteCode())).findFirst();
								flowVo = metaOptional.isPresent() ? metaOptional.get() : null;
							}

							MonitorDataVO emissionVO = statisticalAlgorithm.calculationMinEmission(ST, MN, beginTime,
									endTime, vo, flowVo);
							if (emissionVO != null) {
								rets.add(emissionVO);
							}

							if (!optional.isPresent() && flowVo != null) {
								rets.add(flowVo);
							}
						}

						// 小时数据流量统计
						if (polluteCode.equalsIgnoreCase(flowFactor) && DssService.emissionT212.contains(CN)) {

							Optional<MonitorDataVO> optional = rets.stream()
									.filter(o -> o.getPolluteCode().equals(flowFactor)).findFirst();

							if (!optional.isPresent()) {

								MonitorDataVO flowVo = statisticalAlgorithm.calculationMinFlow(ST, pointCode, MN,
										beginTime, endTime, polluteCode);

								if (flowVo != null) {
									rets.add(flowVo);
								}
							}

							continue;
						}

						if (polluteCode != null && polluteCode.endsWith("Cou")) {
							continue;
						}
					}

					if (vo != null) {
						if (DataStatisticsType.DST_20X1A.equals(DST)) {
							vo.setDataStatus("1");
							rets.add(vo);
							continue;
						}
						// 根据配置文件判断是否要进行排量统计
						if (polluteCode != null && polluteCode.endsWith("Cou") && "1".equals(DssService.isCou)) {
							rets.add(vo);
							// 根据配置文件判断是否要进行流量统计
						} else if (polluteCode.equalsIgnoreCase(flowFactor) && DssService.emissionT212.contains(CN)) {
							rets.add(vo);
						} else if (isStatistical) {
							rets.add(vo);
						}
					}

				}
			}

			// 統計審核數據
			if (DataStatisticsType.DST_20X1A.equals(DST)) {
				// 合并记录集
				List<MonitorDataVO> result = mergeList(rets1, rets);
				if (result != null && result.size() > 0) {
					storageAudit(ST, CN, MN, beginTime, pointCode, result);
				}
			} else {
				// 統計原始數據
				if (rets != null && rets.size() > 0) {
					storage(station, CN, beginTime, rets);
				}
			}

		} catch (Exception e) {
			logger.error("监测数据分/小时/日 统计异常:", e);

		}
	}

	/**
	 * 
	 * mergeList:合并统计数据和数据库查询记录
	 * 
	 * @param list1
	 *            数据库查询记录
	 * @param list2
	 *            统计数据
	 * @return List<MonitorDataVO>
	 */
	private List<MonitorDataVO> mergeList(List<MonitorDataVO> list1, List<MonitorDataVO> list2) {

		try {
			if (list1 != null && list1.size() > 0) {

				// 数据库查询记录不为空，统计数据为空，修改查询记录的审核值，设置数据为无效
				if (list2 == null || list2.size() < 1) {

					for (MonitorDataVO vo : list1) {
						vo.setAuditValue(null);
						vo.setIsValided(0);
					}

					return list1;

				} else {

					List<String> pollutes2 = list2.stream().map(MonitorDataVO::getPolluteCode).distinct()
							.collect(Collectors.toList());

					// 差集，刷选数据库中存在，统计数据为空的记录
					list1 = list1.stream().filter(o -> !pollutes2.contains(o.getPolluteCode()))
							.collect(Collectors.toList());

					if (list1 != null && list1.size() > 0) {

						for (MonitorDataVO vo : list1) {
							vo.setAuditValue(null);
							vo.setIsValided(0);
						}
					}

					list1.addAll(list2);

					return list1;

				}

			} else {

				// 数据库查询记录是空 ，直接返回统计记录
				return list2;
			}
		} catch (Exception e) {
			logger.error("", e);
		}

		return null;

	}

	/**
	 * 
	 * storageAudit:单独修改审核数据
	 * 
	 * @param tableName
	 * @param statisticalData
	 *            void
	 */
	private void storageAudit(String ST, String CN, String MN, String dataTime, String pointCode,
			List<MonitorDataVO> statisticalData) {

		try {

			Date time = DateUtil.strToDate(RegularTime.regular(CN, MN, dataTime), "yyyy-MM-dd HH:mm:ss");

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, time);
			if (!StringUtil.isEmptyOrNull(tableName) && !statisticalData.isEmpty()) {
				stroageService.insertAuditDataBatch(tableName, statisticalData);
			}

			logger.info("修改审核数据:" + JSON.toJSONString(statisticalData));

		} catch (Exception e) {
			logger.error("单独修改审核数据异常:", e);
		}
	}

	/**
	 * 
	 * storage:数据存储处理
	 *
	 * @param ST
	 * @param CN
	 * @param dataTime
	 * @param statisticalData
	 *            void
	 */
	private void storage(StationVO station, String CN, String dataTime, List<MonitorDataVO> statisticalData) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Map<String, Object> CP = new HashMap<String, Object>();
			List<Map<String, Object>> params = new ArrayList<Map<String, Object>>();

			String MN = station.getMN();
			dataTime = RegularTime.regular(CN, MN, dataTime);

			data.put("ST", station.getST());
			data.put("MN", MN);
			data.put("ID", station.getPointCode());
			data.put("PW", "123456");
			data.put("CN", CN);
			// 统计数据和补数数据的区分标识
			data.put("SUPP", "F");
			data.put("QN", "");
			data.put("CP", CP);

			CP.put("Params", params);
			CP.put("DataTime", dataTime);

			for (MonitorDataVO vo : statisticalData) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("Avg", vo.getDataValue());
				param.put("ParamID", vo.getPolluteCode());
				param.put("Flag", vo.getDataFlag());
				param.put("Min", vo.getMinValue());
				param.put("Max", vo.getMaxValue());
				param.put("Round", vo.getAuditValue());
				params.add(param);
			}

			String json = JSON.toJSONString(data);

			logger.info(String.format("%s %s", CN, json));
			publish.send(CN, json);
		} catch (Exception e) {
			logger.error("存储数据异常:" + e.toString());
		}
	}

}
