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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SpecialFactor;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：ConnectivityRatioStatistics 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class ConnectivityStatistics {

	@Autowired
	StroageService stroageService;

	@Autowired
	StatisticalHelper statisticalHelper;

	private static Logger logger = LoggerFactory.getLogger(ConnectivityStatistics.class);

	public void statisticalHandler(StationVO station, String CN, Date dataTime) {

		try {
			String ST = station.getST();
			String MN = station.getMN();

			// 分钟数据,实时数据，非油烟系统 不进行统计

			if (DataType.RTDATA.equals(CN) || DataType.MINUTEDATA.equals(CN) || !SystemTypeCode.SMK.equals(ST)) {
				return;
			}

			// 查询上个周期的数据
			List<MonitorDataVO> metadata = statisticalHelper.queryStatisticsData(station, CN, DataType.MINUTEDATA,
					dataTime);

			String pointCode = station.getPointCode();

			// 连通个数
			int count = 0;

			// 风机个数
			// Double FJ_C = 0d;
			// 风机状态为的个数
			// Double JHQ_C = 0d;
			// 净化器个数
			Double FJ_C_V = 0d;
			// 净化器状态为的个数
			Double JHQ_C_V = 0d;

			// 风机净化器传输个数
			Double JHQ_FJ = 0d;

			if (metadata != null && metadata.size() > 0) {

				List<MonitorDataVO> tempRet = metadata.stream()
						.filter(o -> (SpecialFactor.FJ.equals(o.getPolluteCode())
								|| SpecialFactor.JHQ.equals(o.getPolluteCode())) && o.getDataValue() == 1)
						.collect(Collectors.toList());

				// 就是风机状态为1 精净化器状态也为1的时候 就是联动
				if (tempRet != null && tempRet.size() > 0) {
					Map<String, Long> map = (Map<String, Long>) tempRet.stream()
							.collect(Collectors.groupingBy(MonitorDataVO::getDataTime, Collectors.counting()));

					for (Long v : map.values()) {
						if (v >= 2) {
							count += 1;
						}
					}

					// 精净化器数据个数
					FJ_C_V = (double) tempRet.stream().filter(o -> SpecialFactor.FJ.equals(o.getPolluteCode())).count();

					// 精净化器数据为1个数
					JHQ_C_V = (double) tempRet.stream().filter(o -> SpecialFactor.JHQ.equals(o.getPolluteCode()))
							.count();
				}

				// 风机数据为1个数
				JHQ_FJ = (double) metadata.stream()
						.filter(o -> SpecialFactor.FJ.equals(o.getPolluteCode())
								|| SpecialFactor.JHQ.equals(o.getPolluteCode()))
						.map(MonitorDataVO::getDataTime).distinct().count();
			}

			List<MonitorDataVO> datas = new ArrayList<MonitorDataVO>();

			Double dcount = Double.valueOf(count);

			MonitorDataVO vo = new MonitorDataVO();
			vo.setDataTime(RegularTime.regularDateStr(CN,MN, dataTime));
			vo.setDataValue(dcount);
			vo.setMaxValue(dcount);
			vo.setMinValue(dcount);
			vo.setAuditValue(dcount);
			vo.setDataType(CN);
			vo.setPolluteCode(SpecialFactor.CONNECTIVITYPOLLUTECODE);
			vo.setPointCode(pointCode);
			vo.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			datas.add(vo);

			MonitorDataVO vo2 = JSON.parseObject(JSON.toJSONString(vo), MonitorDataVO.class);
			vo2.setDataValue(FJ_C_V);
			vo2.setMaxValue(FJ_C_V);
			vo2.setMinValue(FJ_C_V);
			vo2.setAuditValue(FJ_C_V);			
			vo2.setPolluteCode(String.format("%s-Vt", SpecialFactor.FJ));
			datas.add(vo2);

			MonitorDataVO vo4 = JSON.parseObject(JSON.toJSONString(vo), MonitorDataVO.class);
			vo4.setDataValue(JHQ_C_V);
			vo4.setMaxValue(JHQ_C_V);
			vo4.setMinValue(JHQ_C_V);
			vo4.setAuditValue(JHQ_C_V);
			vo4.setPolluteCode(String.format("%s-Vt", SpecialFactor.JHQ));
			datas.add(vo4);

			MonitorDataVO vo5 = JSON.parseObject(JSON.toJSONString(vo), MonitorDataVO.class);
			vo5.setDataValue(JHQ_FJ);
			vo5.setMaxValue(JHQ_FJ);
			vo5.setMinValue(JHQ_FJ);
			vo5.setAuditValue(JHQ_FJ);
			vo5.setPolluteCode(SpecialFactor.FJ_JHQ);
			datas.add(vo5);

			String tableName = PartitionTableUtil.getTableName(ST, CN, pointCode, dataTime);

			if (DataType.MONTHDATA.equals(CN) || DataType.YEARDATA.equals(CN)) {
				stroageService.insertYMDataByBatch(tableName, datas);
			} else {
				stroageService.insertDataBatch(tableName, datas);
			}

			logger.info("油烟数据统计：" + JSON.toJSONString(datas));

		} catch (Exception e) {
			logger.error("油烟系统 统计连通个数异常:"+e.toString());
		}

	}
}
