/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：EffectivenessStatistics.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:15:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:15:30 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：EffectivenessStatistics 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Component
public class GeneralStatistics {

	@Autowired
	StroageService stroageService;

	@Autowired
	StatisticalHelper statisticalHelper;

	private static Logger logger = LoggerFactory.getLogger(GeneralStatistics.class);

	public void statisticalHandler(StationVO station, String CN, Date dataTime) {

		try {

			/*
			 * 空气站 小时数据 14点 13:00-14:00 当前小时传的当前小时数据 日数据 15号 15 01-16 01
			 * 当天传的是前一天的数据
			 * 
			 * 污染源 小时数据 14点 14:00-15:00 当前小时传的前一个小时数据 日数据 15号 15 00-16 00
			 * 当天传的是前一天的数据
			 */

			String ST = station.getST();
			String MN = station.getMN();
			String pointCode = station.getPointCode();
			String DCN = StatisticalHelper.getDataCN(CN);		
			
			// 查询周期的数据
			List<MonitorDataVO> metadata = statisticalHelper.queryStatisticsData(station, CN, DCN, dataTime);

			if (CollectionUtils.isNotEmpty(metadata)) {

				Set<String> pollutes = DssService.validFactor.get(station.getPointCode());

				if (pollutes != null) {
					// 排除站点未配置的因子
					metadata = metadata.stream().filter(o -> pollutes.contains(o.getPolluteCode()))
							.collect(Collectors.toList());
				}
			}

			// 统计组数
			Double group = calculationTransmissionGroup(metadata);

			// 统计有效个数
			Double valided = calculationEffectiveness(metadata);

			// 查询当前时刻的数据
			// List<MonitorDataVO> metadata1 =
			// statisticalHelper.queryStatisticsData(station, CN, CN, dataTime);
			// metadata.addAll(metadata1);

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			// 统计个数
			Double c = calculationTransmission(metadata);

			List<MonitorDataVO> list = new ArrayList<MonitorDataVO>();

			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
			vo.setDataValue(c);
			vo.setMaxValue(c);
			vo.setMinValue(c);
			vo.setAuditValue(c);
			vo.setDataType(CN);
			vo.setPolluteCode(SpecialFactor.TransmissionCode);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo.setPointCode(pointCode);
			list.add(vo);

			MonitorDataVO vo1 = JSON.parseObject(JSON.toJSONString(vo), MonitorDataVO.class);
			vo1.setDataValue(group);
			vo1.setMaxValue(group);
			vo1.setMinValue(group);
			vo1.setAuditValue(group);
			vo1.setPolluteCode(SpecialFactor.TransmissionGroupCode);
			list.add(vo1);

			MonitorDataVO vo2 = JSON.parseObject(JSON.toJSONString(vo), MonitorDataVO.class);
			vo2.setDataValue(valided);
			vo2.setMaxValue(valided);
			vo2.setMinValue(valided);
			vo2.setAuditValue(valided);
			vo2.setPolluteCode(SpecialFactor.EffectivenessCode);
			list.add(vo2);

			if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
				stroageService.insertYMDataByBatch(tableName, list);
			} else {
				stroageService.insertDataBatch(tableName, list);
			}

			logger.info(String.format("通用统计[%s]：%s", CN, JSON.toJSONString(list)));

		} catch (Exception e) {
			logger.error("通用统计异常:",e);
		}

	}

	/**
	 * 
	 * calculationTransmission:传输个数
	 *
	 * @param metadata
	 * @return Double
	 */
	public Double calculationTransmission(List<MonitorDataVO> metadata) {
		Double c = 0d;
		try {
			if (metadata != null && metadata.size() > 0) {

				if (metadata != null && metadata.size() > 0) {
					c = (double) metadata.stream().filter(o -> !DssService.vFactor.contains(o.getPolluteCode()))
							.count();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return c;
	}

	/**
	 * 
	 * calculationTransmissionGroup:传输组数
	 *
	 * @param metadata
	 * @return Double
	 */
	public Double calculationTransmissionGroup(List<MonitorDataVO> metadata) {

		double group = 0d;
		try {
			if (metadata != null && metadata.size() > 0) {
				// 传输组数统计
				if (metadata != null && metadata.size() > 0) {
					group = (double) metadata.stream().filter(o -> !DssService.vFactor.contains(o.getPolluteCode()))
							.collect(Collectors.groupingBy(MonitorDataVO::getDataTime, Collectors.toList())).size();
				}
				if (group > 24) {
					group = 24d;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return group;
	}

	/**
	 * 
	 * calculationEffectiveness:有效个数
	 *
	 * @param metadata
	 * @return Double
	 */
	public Double calculationEffectiveness(List<MonitorDataVO> metadata) {

		double valided = 0d;
		try {
			if (metadata != null && metadata.size() > 0) {

				Map<String, DoubleSummaryStatistics> doubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {
					// 计算算术均值

					doubleSummaryStatistics = metadata.stream()
							.filter(o -> o.getIsValided() != null && !DssService.vFactor.contains(o.getPolluteCode()))
							.collect(Collectors.groupingBy(MonitorDataVO::getDataTime,
									Collectors.summarizingDouble(MonitorDataVO::getIsValided)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 废水废气 排放量统计和瞬时流量统计

				for (Map.Entry<String, DoubleSummaryStatistics> entry : doubleSummaryStatistics.entrySet()) {
					DoubleSummaryStatistics statistics = entry.getValue();
					Double min = statistics.getMin();
					if (min == 1) {
						valided += 1;
					}
				}
			}
		} catch (Exception e) {
			logger.error("统计有效个数异常:" + e.toString());

		}

		return valided;
	}

}
