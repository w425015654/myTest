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
import java.util.Collection;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataStatisticsType;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
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
@Component("ymDataStatistical")
public class YMDataStatistical {

	@Autowired
	StatisticalAlgorithm statisticalAlgorithm;

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	StroageService stroageService;
	

	@Autowired
	GeneralStatistics generalStatistics;

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(YMDataStatistical.class);

	public void statisticalHandler(StationVO station, String CN, Date dataTime) {

		try {
			String QCN = CN;
			String beginTime = null;
			String endTime = null;
			String ST = station.getST();
			String pointCode = station.getPointCode();
			String MN = station.getMN();
			List<MonitorDataVO> metadata = null;
			String tableName = null;

			switch (CN) {
			case DataType.MONTHDATA:
				QCN = DataType.DAYDATA;
				beginTime = String.format("%s-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM"));
				endTime = String.format("%s-01 00:00:00",
						DateUtil.dateToStr(DateUtil.dateAddMonth(dataTime, 1), "yyyy-MM"));

				tableName = PartitionTableUtil.getTableName(ST, QCN, pointCode, dataTime);
				if (StringUtil.isEmptyOrNull(tableName)) {
					logger.warn(String.format(" 数据统计 站点：%s 数据类型：%s 表名为空", ST, CN));
					return;
				}
				metadata = queryDataService.queryDataByCondition(tableName, pointCode, beginTime, endTime, null, ST);
				break;
			case DataType.YEARDATA:

				QCN = DataType.MONTHDATA;
				beginTime = String.format("%s-01-01 00:00:00", DateUtil.dateToStr(dataTime, "yyyy"));
				endTime = String.format("%s-01-01 00:00:00",
						DateUtil.dateToStr(DateUtil.dateAddYear(dataTime, 1), "yyyy"));
				tableName = PartitionTableUtil.getTableName(ST, QCN, pointCode, dataTime);

				if (StringUtil.isEmptyOrNull(tableName)) {
					logger.warn(String.format(" 数据统计 站点：%s 数据类型：%s 表名为空", ST, CN));
					return;
				}

				metadata = queryDataService.queryYMDataByCondition(tableName, DataType.MONTHDATA, pointCode, beginTime,
						endTime, null, ST);

				break;
			}

			if (metadata != null && metadata.size() > 0) {

				metadata = metadata.stream().filter(o -> !DssService.vFactors.contains(o.getPolluteCode()))
						.collect(Collectors.toList());

				List<MonitorDataVO> rets = new ArrayList<MonitorDataVO>();

				Map<String, DoubleSummaryStatistics> doubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();
				try {
					// 计算算术均值
					doubleSummaryStatistics = metadata.stream()
							.filter(o -> o.getAuditValue() != null && o.getIsValided() == 1)
							.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
									Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));

				} catch (Exception e) {
					e.printStackTrace();
				}

				// 废水废气 排放量统计和瞬时量统计
				for (Map.Entry<String, DoubleSummaryStatistics> entry : doubleSummaryStatistics.entrySet()) {

					String polluteCode = entry.getKey();

					if (SpecialFactor.TransmissionGroupCode.equals(polluteCode)) {
						continue;
					}

					DoubleSummaryStatistics statistics = entry.getValue();
					Double min = statistics.getMin();
					Double max = statistics.getMax();				
					double count = statistics.getCount();
					Double sum=statistics.getSum();					
					Double avg = Arith.div(count, sum);

					int valid = statisticalAlgorithm.calculationValid(ST, CN, MN, count, polluteCode);

					Collection<String> flowFactors = DssService.flowFactor.values();

					// 污染物排放量/流量统计
					if (polluteCode.endsWith("Cou") || flowFactors.contains(polluteCode)) {
						avg = statistics.getSum();
						min = statistics.getSum();
						max = statistics.getSum();
					}

					// 计算PH的均值
					if (SpecialFactor.PH.equals(polluteCode)) {
						avg = statisticalAlgorithm.calculationPHAvg(metadata, SpecialFactor.PH, valid, 0);
					}
					String dataFlag = "N";
					if (valid == 0) {

						List<MonitorDataVO> ddl = metadata.stream()
								.filter(o -> o.getDataValue() != null && o.getPolluteCode().equals(polluteCode))
								.collect(Collectors.toList());
						dataFlag = statisticalAlgorithm.calculationFlag(ST, CN, MN, ddl, polluteCode);
					}

					// 计算CO的均值
					if (AirSixParam.CO.equals(polluteCode)) {
						// CO 月年浓度计算，按百分位95计算
						List<Double> mete = metadata.stream()
								.filter(o -> o.getAuditValueRounding() != null && o.getIsValided() == 1
										&& o.getPolluteCode().equals(polluteCode))
								.map(MonitorDataVO::getAuditValueRounding).collect(Collectors.toList());
						avg = StatisticalHelper.dataBFW(mete, 95);

					}

					MonitorDataVO vo = new MonitorDataVO();
					vo.setDataTime(beginTime);
					vo.setDataValue(avg);
					vo.setMaxValue(max);
					vo.setMinValue(min);
					vo.setPolluteCode(polluteCode);
					vo.setDataFlag(dataFlag);
					vo.setDataType(CN);
					vo.setDataStatus("1");
					vo.setIsValided(valid);
					vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
					vo.setPointCode(pointCode);
					rets.add(vo);
				}				

				if (rets.size() > 0) {
					tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);
					storage(tableName, rets);

					logger.info(String.format("%s 数据统计【%s】 数据时间:%s", MN, CN, beginTime));

					Date dateTime = DateUtil.strToDate(beginTime, "yyyy-MM-dd HH:mm:ss");

					// 不是当年数据，发起年统计
					if (DataType.MONTHDATA.equals(CN)
							&& DateUtil.getYear(DateUtil.getCurrentDate()) != DateUtil.getYear(dateTime)) {

						Map<String, Object> dataCycle = new HashMap<String, Object>();
						dataCycle.put("DATATIME", dateTime);
						dataCycle.put("CN", DataType.YEARDATA);
						dataCycle.put("MN", station.getMN());
						dataCycle.put("ST", ST);
						dataCycle.put("DST", DataStatisticsType.DST_20X1G);
						String json = JSON.toJSONString(dataCycle);
						publish.send(Constant.MQ_QUEUE_CYCLE, json);
					}
				}
				
				// 统计传输个数，有效个数，连通个数,传输组数
				generalStatistics.statisticalHandler(station, CN, dataTime);
			}
		} catch (Exception e) {
			logger.error("监测数据年月统计:", e);
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
	private void storage(String tableName, List<MonitorDataVO> statisticalData) {
		double batchSize = 100d;
		if (statisticalData != null && statisticalData.size() > 0 && !StringUtil.isEmptyOrNull(tableName)) {

			//logger.info(String.format("年月数据入库:%s---%s", tableName, JSON.toJSONString(statisticalData)));

			// 批量缠绕的次数
			double loop = Math.ceil(statisticalData.size() / batchSize);

			for (int index = 0; index < loop; index++) {

				try {
					int bIndex = (int) (index * batchSize);
					int eIndex = (int) ((index + 1) * batchSize - 1);

					if (eIndex > statisticalData.size()) {
						eIndex = statisticalData.size();
					}

					stroageService.insertYMDataByBatch(tableName, statisticalData.subList(bIndex, eIndex));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
