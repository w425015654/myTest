/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：QualityControlStatistics.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:15:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:15:30 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.Date;
import java.util.List;
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
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：QualityControlStatistics 类 描 述：统计质控合格个数 创建作者：quanhongsheng
 */
@Component
public class QualityControlStatistics {

	private final static int VALIDED = 1;

	private final static String AUDITTYPE = "0";

	@Autowired
	StroageService stroageService;

	@Autowired
	StatisticalHelper statisticalHelper;

	private static Logger logger = LoggerFactory.getLogger(QualityControlStatistics.class);

	public void statisticalHandler(StationVO station, Date dataTime) {

		try {

			String ST = station.getST();
			String CN = station.getCN();
			String MN = station.getMN();
			String DCN = StatisticalHelper.getDataCN(CN);
			List<MonitorDataVO> metadata = statisticalHelper.queryStatisticsData(station, CN, DCN, dataTime);

			String pointCode = station.getPointCode();

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			double valided = 0d;
			// 排除站点未配置的因子及配置不参与统计的因子
			if (CollectionUtils.isNotEmpty(metadata)) {

				Set<String> pollutes = DssService.validFactor.get(station.getPointCode());

				if (pollutes != null) {
					// 排除站点未配置的因子
					metadata = metadata.stream().filter(o -> pollutes.contains(o.getPolluteCode()))
							.collect(Collectors.toList());
				}
			}

			valided = metadata.stream().filter(o -> o.getIsValided() == VALIDED && o.getIsAudit() == 1
					&& !(StringUtil.isEmptyOrNull(o.getDataStatus()) || AUDITTYPE.equalsIgnoreCase(o.getDataStatus())))
					.count();

			/*
			 * Map<String, DoubleSummaryStatistics> doubleSummaryStatistics =
			 * new HashMap<String, DoubleSummaryStatistics>(); try { // 计算算术均值
			 * doubleSummaryStatistics = metadata.stream().filter(o ->
			 * o.getIsValided() != null && o.getIsAudit() == 1)
			 * .collect(Collectors.groupingBy(MonitorDataVO::getDataTime,
			 * Collectors.summarizingDouble(MonitorDataVO::getIsValided))); }
			 * catch (Exception e) { e.printStackTrace(); }
			 * 
			 * 
			 * // 质控合格个数，是人工审核后有效的数据
			 * 
			 * // 质控不合格个数 Map<String, Long> qcMap = metadata.stream() .filter(o
			 * -> o.getIsAudit() == 1 &&
			 * (StringUtil.isEmptyOrNull(o.getDataStatus()) ||
			 * AUDITTYPE.equalsIgnoreCase(o.getDataStatus())))
			 * .collect(Collectors.groupingBy(MonitorDataVO::getDataTime,
			 * Collectors.counting()));
			 * 
			 * for (Map.Entry<String, DoubleSummaryStatistics> entry :
			 * doubleSummaryStatistics.entrySet()) {
			 * 
			 * DoubleSummaryStatistics statistics = entry.getValue();
			 * 
			 * String key = entry.getKey(); Long qc = qcMap.get(key);
			 * 
			 * Double min = statistics.getMin();
			 * 
			 * // logger.info(String.format("质控 %s [%s ：%s}", key, min, qc));
			 * 
			 * if (min == VALIDED && (qc == null || qc == 0)) { valided += 1; }
			 * }
			 */

			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(RegularTime.regular(CN, MN, DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm:ss")));
			vo.setDataValue(valided);
			vo.setMaxValue(valided);
			vo.setMinValue(valided);
			vo.setAuditValue(valided);
			vo.setPolluteCode(SpecialFactor.TransmissionQCCode);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			vo.setPointCode(pointCode);
			vo.setDataType(CN);

			if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
				stroageService.insertYMData(tableName, vo);
			} else {
				stroageService.insertData(tableName, vo);
			}

			logger.info(String.format("质控个数[%s]：%s", CN, JSON.toJSONString(vo)));

		} catch (Exception e) {
			logger.error("统计质控合格个数异常:", e);
		}

	}
}
