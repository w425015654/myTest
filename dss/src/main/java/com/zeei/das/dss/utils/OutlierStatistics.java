package com.zeei.das.dss.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.statistics.AQIStatistical;
import com.zeei.das.dss.vo.MonitorDataVO;

/**
 * @类型名称：OutlierStatistics @类型描述：
 * @功能描述：离群计算工具类
 * @创建作者：zhanghu
 *
 */
@Component
public class OutlierStatistics {

	/**
	 * outlierDataPoint:对测点数据进行离群
	 * 
	 * @param metadatas
	 * @param paramsVo
	 *            void
	 */
	public static Map<String, List<MonitorDataVO>> outlierData(List<String> pointCodes, List<String> polluteCodes,
			Map<String, List<MonitorDataVO>> metadataMaps, Map<String, List<MonitorDataVO>> calculationMaps,
			List<MonitorDataVO> outlierList) {

		Map<String, List<MonitorDataVO>> resultMaps = new HashMap<>();
		Date dateV;

		// 查出这些测点所在区域的测点集，通过代码匹配对应计算数据
		for (String pointCode : pointCodes) {

			List<MonitorDataVO> monitorDataVOs = metadataMaps.get(pointCode);
			if (CollectionUtils.isEmpty(monitorDataVOs)) {
				return null;
			} else {
				dateV = monitorDataVOs.get(0).getDeDataTime();
			}
			List<String> areaPoints = AQIStatistical.areaPointMap.get(pointCode);
			List<MonitorDataVO> othPoint = new ArrayList<>();
			// 当前测点所属区域的测点数小于2，不参与判断
			if (CollectionUtils.isEmpty(areaPoints)) {
				// 和自己比24
				oneselfData(monitorDataVOs, calculationMaps, pointCode, dateV, outlierList);
				resultMaps.put(pointCode, monitorDataVOs);
				continue;
			} else {
				// 遍历测点集，拿到同区域下其他测点的数据
				for (String areaPoint : areaPoints) {

					List<MonitorDataVO> everyPoint = metadataMaps.get(areaPoint);
					if (!pointCode.equals(areaPoint) && CollectionUtils.isNotEmpty(everyPoint)) {
						othPoint.addAll(everyPoint);
					}
				}
			}
			int hours;
			// 根据测点分组，确定测点数量
			Map<String, List<MonitorDataVO>> otherdaMap = othPoint.stream()
					.collect(Collectors.groupingBy(MonitorDataVO::getPointCode));
			// 除了本身测点还有大于3，则该区域测点大于等于5
			if (otherdaMap.size() > 3) {
				hours = -24;
			} else if (otherdaMap.size() > 0) {
				hours = -1;
			} else {
				// 和自己比24
				oneselfData(monitorDataVOs, calculationMaps, pointCode, dateV, outlierList);
				resultMaps.put(pointCode, monitorDataVOs);
				continue;
			}
			morePointOutlier(monitorDataVOs, othPoint, polluteCodes, calculationMaps, hours, pointCode, dateV,
					outlierList);
			resultMaps.put(pointCode, monitorDataVOs);
		}

		return resultMaps;
	}

	/**
	 * oneselfData:和自身测点24小时内数据平均值比较大小判断是否离群
	 * 
	 * @param metadatas
	 *            void
	 */
	public static void oneselfData(List<MonitorDataVO> metadatas, Map<String, List<MonitorDataVO>> calculationMaps,
			String pointCode, Date dateV, List<MonitorDataVO> outlierList) {

		Double avgValue;
		// 获取当前测点前24小时数据，进行数据是否离群判断
		List<MonitorDataVO> oldDatas = calculationMaps.get(pointCode);
		oldDatas = oldDatas.stream().filter(u -> !u.getDeDataTime().equals(dateV)).collect(Collectors.toList());
		if (CollectionUtils.isEmpty(oldDatas)) {
			return;
		}
		// 对不同因子下数据计数求和
		Map<String, MonitorDataVO> oldValues = new HashMap<>();
		for (MonitorDataVO oldData : oldDatas) {

			MonitorDataVO monitorData = null;
			if (oldValues.containsKey(oldData.getPolluteCode())) {
				monitorData = oldValues.get(oldData.getPolluteCode());
			} else {
				monitorData = new MonitorDataVO();
				oldValues.put(oldData.getPolluteCode(), monitorData);
				// 借用做数据和及个数统计
				monitorData.setAuditValue(0d);
				monitorData.setNumPrecision(0);
			}
			monitorData.setAuditValue(monitorData.getAuditValue() + oldData.getAuditValue());
			monitorData.setNumPrecision(monitorData.getNumPrecision() + 1);

		}

		Iterator<MonitorDataVO> iterator = metadatas.iterator();
		while (iterator.hasNext()) {
			MonitorDataVO itera = iterator.next();
			avgValue = itera.getAuditValue();
			Double sMaxV = DssService.polluteMaxMap.get(itera.getPolluteCode());
			// 判断当前值是否超过该指标的二级标准值
			if (sMaxV != null && sMaxV >= avgValue) {
				continue;
			}
			MonitorDataVO monitorData = oldValues.get(itera.getPolluteCode());
			// 大于自身24小时内均值，离群
			if (monitorData != null && monitorData.getNumPrecision() > 0
					&& avgValue > (4 * monitorData.getAuditValue() / monitorData.getNumPrecision())) {
				outlierList.add(itera);
				iterator.remove();
			}

		}
	}

	/**
	 * morePointOutlier:测点的离群
	 * 
	 * @param metadatas
	 *            要进行离群比较的测点数据集
	 * @param otherdatas
	 *            void 和离群比较的测点数据集 hours为-24时则为城市测点大于等于5的数据，否则hours-1为城市2-4的数据
	 */
	public static void morePointOutlier(List<MonitorDataVO> metadatas, List<MonitorDataVO> otherdatas,
			List<String> polluteCodes, Map<String, List<MonitorDataVO>> calculationMaps, int hours, String pointCode,
			Date dateV, List<MonitorDataVO> outlierList) {

		if (CollectionUtils.isEmpty(metadatas) || CollectionUtils.isEmpty(otherdatas)) {
			return;
		}

		// 保存同区域其他点位数据，按因子分类
		Map<String, Double> otherdataavg = new HashMap<>();
		Double avgValue;
		Double othavgValue;
		// 获取当前测点前24小时或前一小时数据，进行数据是否离群判断
		List<MonitorDataVO> oldDatas = calculationMaps.get(pointCode);
		if (hours == -24) {
			oldDatas = oldDatas.stream().filter(u -> !u.getDeDataTime().equals(dateV)).collect(Collectors.toList());
		} else {
			oldDatas = oldDatas.stream().filter(u -> u.getDeDataTime().equals(DateUtil.dateAddHour(dateV, -1)))
					.collect(Collectors.toList());
		}
		if (CollectionUtils.isEmpty(oldDatas)) {
			return;
		}
		// 对不同因子下数据计数求和
		Map<String, MonitorDataVO> oldValues = new HashMap<>();
		for (MonitorDataVO oldData : oldDatas) {

			MonitorDataVO monitorData = null;
			if (oldValues.containsKey(oldData.getPolluteCode())) {
				monitorData = oldValues.get(oldData.getPolluteCode());
			} else {
				monitorData = new MonitorDataVO();
				oldValues.put(oldData.getPolluteCode(), monitorData);
				// 借用做数据和及个数统计
				monitorData.setAuditValue(0d);
				monitorData.setNumPrecision(0);
			}
			monitorData.setAuditValue(monitorData.getAuditValue() + oldData.getAuditValue());
			monitorData.setNumPrecision(monitorData.getNumPrecision() + 1);

		}

		// 其他测点数据 每个因子 对应的值
		otherdataavg = otherdatas.stream().collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
				Collectors.averagingDouble(MonitorDataVO::getAuditValue)));

		Iterator<MonitorDataVO> iterator = metadatas.iterator();
		while (iterator.hasNext()) {
			MonitorDataVO itera = iterator.next();

			othavgValue = otherdataavg.get(itera.getPolluteCode());
			avgValue = itera.getAuditValue();
			Double sMaxV = DssService.polluteMaxMap.get(itera.getPolluteCode());
			// 判断当前值是否超过该指标的二级标准值
			if (sMaxV != null && sMaxV >= avgValue) {
				continue;
			}
			MonitorDataVO monitorData = oldValues.get(itera.getPolluteCode());

			int index = 3;
			if (hours == -24) {
				// 24小时则是测点大于等于5个，按测点大于等于5个的城市逻辑处理
				if (monitorData != null && monitorData.getNumPrecision() >= 4) {
					if (avgValue <= (3 * monitorData.getAuditValue() / monitorData.getNumPrecision())) {
						continue;
					}
				} else {
					index = 4;
				}
			} else {
				// 1小时则是测点2-4个，按测点2-4的城市逻辑处理
				if (monitorData != null) {
					if (avgValue <= (3 * monitorData.getAuditValue() / monitorData.getNumPrecision())) {
						continue;
					}
				} else {
					index = 4;
				}
			}
			// 判断当前值和其他测点均值 的倍数 比大小
			if (avgValue != null && othavgValue != null && avgValue > othavgValue * index) {
				outlierList.add(itera);
				iterator.remove();
			}
		}

	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {

		if (v1 == 0) {
			return 0;
		}
		// 默认保留10位
		return div(v1, v2, 10);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}

		if (v1 == 0) {
			return 0;
		}

		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b2.divide(b1, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
