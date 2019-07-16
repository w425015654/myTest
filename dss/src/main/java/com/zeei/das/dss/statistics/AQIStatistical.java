/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AQIStatistical.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:13:40
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:13:40 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeei.das.common.constants.AirSixParam;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.SystemTypeCode;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.NumberFormatUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.DssService;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.AqiService;
import com.zeei.das.dss.utils.OutlierStatistics;
import com.zeei.das.dss.vo.AQILevelVO;
import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AirOprecDetlVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AreaVO;
import com.zeei.das.dss.vo.IAQIDataVO;
import com.zeei.das.dss.vo.IAQIRangeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.ParamsVo;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.StationVO;

/**
 * 类 名 称：AQIStatistical 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Component("aqiStatistical")
public class AQIStatistical {

	@Autowired
	AqiService aqiService;

	private static List<IAQIRangeVO> IAQIRange = null;
	private static List<AQILevelVO> AQILevel = null;

	private static List<AreaVO> areas = null;

	public static Map<String, List<MonitorDataVO>> monDataMap = new ConcurrentHashMap<>();

	public static Map<String, List<MonitorDataVO>> monSkDataMap = new ConcurrentHashMap<>();

	// 区域统计消息
	private ConcurrentLinkedQueue<JSONObject> queueArea = new ConcurrentLinkedQueue<JSONObject>();

	// 测点所属区域的其他测点数据集
	public static Map<String, List<String>> areaPointMap = new HashMap<>();

	// 参数
	public static List<String> airPollutes = Arrays.asList(new String[] { AirSixParam.O3, AirSixParam.NO2,
			AirSixParam.CO, AirSixParam.SO2, AirSixParam.PM10, AirSixParam.PM25 });
	// SK参数
	public static List<String> skPollutes = Arrays.asList(new String[] { AirSixParam.SK_O3, AirSixParam.SK_NO2,
			AirSixParam.SK_CO, AirSixParam.SK_SO2, AirSixParam.SK_PM10, AirSixParam.SK_PM25 });

	private static Logger logger = LoggerFactory.getLogger(AQIStatistical.class);

	@Autowired
	Publish publish;

	@PostConstruct
	public void init() {
		IAQIRange = aqiService.queryIAQIRange();
		AQILevel = aqiService.queryAQILevel();
		areas = aqiService.queryArea();
		initAreaPoint();
		initMonData();
		statisticalArea();
	}

	/**
	 * 统计区域AQI排程
	 */
	public void statisticalArea() {

		Runnable runnable = new Runnable() {
			public void run() {
				try {

					List<JSONObject> list = new ArrayList<JSONObject>();

					while (!queueArea.isEmpty()) {
						JSONObject msg = queueArea.poll();
						boolean b = list.stream()
								.anyMatch(u -> u.getString("CN").equals(msg.getString("CN"))
										&& u.getString("areaCode").equals(msg.getString("areaCode"))
										&& u.getInteger("level") == msg.getInteger("level")
										&& u.getInteger("dataStatus") == msg.getInteger("dataStatus")
										&& u.getDate("dataTime").getTime() == msg.getDate("dataTime").getTime());
						
						if (!b && !DssService.aqiExcludeAreaLevel.contains(msg.getString("level"))) {
							list.add(msg);
						}
					}

					if (list.size() > 0) {

						for (JSONObject msg : list) {
							try {

								logger.info("统计区域AQI:" + JSON.toJSONString(msg));

								String CN = msg.getString("CN");
								String areaCode = msg.getString("areaCode");
								Integer level = msg.getInteger("level");
								Date dataTime = msg.getDate("dataTime");
								Integer dataStatus = msg.getInteger("dataStatus");

								// 统计标况
								if (CN.equals(DataType.HOURDATA)) {
									// 計算标况
									calculationAQIHH(0, null, areaCode, level, dataTime, dataStatus);

								} else if (CN.equals(DataType.DAYDATA)) {
									// 計算标况
									calculationAQIDD(0, null, areaCode, level, dataTime, dataStatus);
								}

								if (CN.equals(DataType.HOURDATA)) {
									// 计算实况
									calculationAQIHH(1, null, areaCode, level, dataTime, dataStatus);

								} else if (CN.equals(DataType.DAYDATA)) {
									// 计算实况
									calculationAQIDD(1, null, areaCode, level, dataTime, dataStatus);
								}

							} catch (Exception e) {
								logger.error("", e);
							}
						}

					}

				} catch (Exception e) {
					logger.error("", e);

				}
			}
		};

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
	}

	public void statisticalHandler(StationVO station, Date dataTime, String CN, Integer dataStatus) {

		try {

			// 查询区域信息
			areas = aqiService.queryArea();

			String ST = station.getST();
			String pointCode = station.getPointCode();

			if (!SystemTypeCode.AIR.equals(ST)) {
				// return;
			}

			// 不进行小时aqi统计
			if ("0".equals(station.getIsStatsAqiHH()) && CN.equals(DataType.HOURDATA)) {
				return;
			}

			// 不进行日 aqi统计
			if ("0".equals(station.getIsStatsAqiDD()) && CN.equals(DataType.DAYDATA)) {
				return;
			}

			String areaCode = station.getAreaCode();

			// 计算站点AQI
			if (!StringUtil.isEmptyOrNull(pointCode)) {

				// 统计标况

				if (CN.equals(DataType.HOURDATA)) {
					// 計算标况
					calculationAQIHH(0, pointCode, null, 4, dataTime, dataStatus);

				} else if (CN.equals(DataType.DAYDATA)) {
					// 計算标况
					calculationAQIDD(0, pointCode, null, 4, dataTime, dataStatus);
				}

				if (CN.equals(DataType.HOURDATA)) {
					// 计算实况
					calculationAQIHH(1, pointCode, null, 4, dataTime, dataStatus);

				} else if (CN.equals(DataType.DAYDATA)) {
					// 计算实况
					calculationAQIDD(1, pointCode, null, 4, dataTime, dataStatus);
				}

			}

			// 统计区域AQI
			if (!StringUtil.isEmptyOrNull(areaCode)) {
				calculationAreaAQI(areaCode, dataTime, CN, dataStatus);
			}

		} catch (Exception e) {
			logger.error("空气监测系统 统计aqi异常:", e);
		}

	}

	/**
	 * statisMinHandler: 计算分钟AQI来统计超标事件
	 * 
	 * @param pointCode
	 * @param dataTime
	 *            void
	 */
	public void statisMinHandler(List<String> pointCodes, Date dataTime) {

		// 测点为空，直接跳过
		if (CollectionUtils.isEmpty(pointCodes)) {
			return;
		}
		String beginTime = String.format("%s:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm"));
		String endTime = String.format("%s:00",
				DateUtil.dateToStr(DateUtil.dateAddMin(dataTime, 15), "yyyy-MM-dd HH:mm"));
		// 实况和标况数据都查询出来
		List<String> polluteList = new ArrayList<>();
		polluteList.addAll(airPollutes);
		polluteList.addAll(skPollutes);
		String tableName = PartitionTableUtil.getTableName(SystemTypeCode.AIR, DataType.MINUTEDATA, null, dataTime);
		// 详情记录
		List<AirOprecDetlVo> oprecs = new ArrayList<>();
		List<PollIncidentVo> pollIncs = new ArrayList<>();
		// 查询所有测点的相关记录
		List<MonitorDataVO> moniResults = aqiService.queryMointorDataMM(pointCodes, tableName, polluteList, beginTime,
				endTime);
		// 将记录按测点分组
		Map<String, List<MonitorDataVO>> resultMaps = moniResults.stream()
				.collect(Collectors.groupingBy(MonitorDataVO::getPointCode));

		List<AqiDataVO> minAqiDatas = new ArrayList<>();
		// 遍历结果集，进行AQI相关计算
		for (Entry<String, List<MonitorDataVO>> resultMap : resultMaps.entrySet()) {
			// 测点id
			String pointCode = resultMap.getKey();
			PollIncidentVo pollIncident = DssService.pollIncidentMap.get(pointCode);
			// 实况和标况数据都有
			List<MonitorDataVO> allResults = resultMap.getValue();
			// 标况数据
			List<MonitorDataVO> airResults = allResults.stream().filter(u -> airPollutes.contains(u.getPolluteCode()))
					.collect(Collectors.toList());
			// AQI数据对应的所有测点集
			Map<Integer, String> aqiMap = new HashMap<>();

			// if(CollectionUtils.isNotEmpty(allResults) && pollIncident !=
			// null){
			//
			// Date dateLast = pollIncident.getLastDateTime();
			// //获取上次统计时间
			// if(dateLast == null){
			// dateLast = pollIncident.getsDeTime();
			// }
			// if(DateUtil.dateDiffMin(dateLast, dataTime)>16){
			// //入库结束时间
			// pollIncident.seteTime(DateUtil.dateToStr(DateUtil.dateAddMin(dateLast,
			// 15), "yyyy-MM-dd HH:mm:ss"));
			// pollIncs.add(pollIncident);
			// //
			// DssService.pollIncidentMap.put(pointCode, null);
			// }
			// }
			boolean flag = true;

			for (int i = 0; i < 2; i++) {

				Double so2avg = null;
				Integer so2iaqi = null;
				Double no2avg = null;
				Integer no2iaqi = null;
				Double pm10avg = null;
				Double pm1024avg = null;
				Integer pm1024iaqi = null;
				Double coavg = null;
				Integer coiaqi = null;
				Double o31avg = null;
				Integer o31iaqi = null;
				Double pm25avg = null;
				Double pm2524avg = null;
				Integer pm2524iaqi = null;
				Integer aqi = null;
				String polluteNames = null;
				String dataStatus = "0";
				// 标况或实况数据计算AQI
				if (i == 1) {
					airResults = allResults.stream().filter(u -> skPollutes.contains(u.getPolluteCode()))
							.collect(Collectors.toList());
					dataStatus = "2";
				}
				if (CollectionUtils.isNotEmpty(airResults)) {
					aqiMap.clear();
					Integer iaqi = null;
					for (MonitorDataVO airResult : airResults) {
						String polluteCode = airResult.getPolluteCode();
						iaqi = calculationIAQI(polluteCode, airResult.getDataValue(), 1);
						if (iaqi != null) {
							PolluteVO polluteVO = DssService.airSixParam.get(polluteCode);
							String polluter = polluteVO != null ? polluteVO.getPolluteName() : polluteCode;
							if (aqiMap.containsKey(iaqi)) {
								aqiMap.put(iaqi, aqiMap.get(iaqi) + "," + polluter);
							} else {
								aqiMap.put(iaqi, polluter);
							}
						}
						// 统计分钟aqi
						switch (polluteCode) {
						case AirSixParam.O3:
						case AirSixParam.SK_O3:
							o31avg = NumberFormat(airResult.getDataValue(), 2);
							o31iaqi = iaqi;
							break;
						case AirSixParam.NO2:
						case AirSixParam.SK_NO2:
							no2avg = NumberFormat(airResult.getDataValue(), 2);
							no2iaqi = iaqi;
							break;
						case AirSixParam.CO:
						case AirSixParam.SK_CO:
							coavg = NumberFormat(airResult.getDataValue(), 2);
							coiaqi = iaqi;
							break;
						case AirSixParam.SO2:
						case AirSixParam.SK_SO2:
							if (airResult.getDataValue() > 800) {
								iaqi = calculationIAQI(polluteCode,
										getSO224Avg(pointCode, polluteCode, null, dataTime, 2), 24);
							}
							so2avg = NumberFormat(airResult.getDataValue(), 2);
							so2iaqi = iaqi;
							break;
						case AirSixParam.PM10:
						case AirSixParam.SK_PM10:
							pm10avg = NumberFormat(airResult.getDataValue(), 2);
							pm1024iaqi = iaqi;
							break;
						case AirSixParam.PM25:
						case AirSixParam.SK_PM25:
							pm25avg = NumberFormat(airResult.getDataValue(), 2);
							pm2524iaqi = iaqi;
							break;
						}

					}
					// 所有的AQI结果
					if (aqiMap.size() > 0) {
						iaqi = Collections.max(aqiMap.keySet());
						aqi = iaqi;
						polluteNames = aqiMap.get(iaqi);

						Integer iaqid = null;
						if (aqi != null) {
							AQILevelVO levelVO = getAqiLevel(aqi);
							if (levelVO != null) {
								iaqid = levelVO.getAqiid();
							}
						}
						AqiDataVO aqiData = new AqiDataVO();
						aqiData.setIsValided(1);
						aqiData.setCode(pointCode);
						aqiData.setAqi(aqi);
						aqiData.setAqiId(iaqid);
						aqiData.setCo(coavg);
						aqiData.setCoIaqi(coiaqi);
						aqiData.setDataTime(String.format("%s:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH:mm")));
						aqiData.setLevel(4);
						aqiData.setNo2(no2avg);
						aqiData.setNo2Iaqi(no2iaqi);
						aqiData.setO3(o31avg);
						aqiData.setO3Iaqi(o31iaqi);
						aqiData.setPm10(pm10avg);
						aqiData.setAvgvaluePm10(pm1024avg);
						aqiData.setPm10Iaqi(pm1024iaqi);
						aqiData.setPm25(pm25avg);
						aqiData.setAvgvaluePm25(pm2524avg);
						aqiData.setPm25Iaqi(pm2524iaqi);
						aqiData.setSo2(so2avg);
						aqiData.setSo2Iaqi(so2iaqi);
						aqiData.setDataStatus(dataStatus);
						aqiData.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
						aqiData.setPolluteName(polluteNames);
						minAqiDatas.add(aqiData);

						if (i == 1 && !flag) {
							break;
						}
						if (iaqi > 100) {
							// 有一次大于100就把标识修改
							flag = false;
							if (pollIncident != null) {
								pollIncident.setLastDateTime(airResults.get(0).getDeDataTime());
								if (pollIncident.getOfType() == 0) {
									pollIncident.setOfType(1);
									// 更新连续超标标识
									pollIncs.add(pollIncident);
								}
								pollIncident.setLastOf(0);
							} else {
								pollIncident = new PollIncidentVo();
								pollIncident.setLastDateTime(airResults.get(0).getDeDataTime());
								pollIncident.setPointCode(Integer.valueOf(pointCode));
								pollIncident.setsTime(airResults.get(0).getDataTime());
								pollIncident.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
								// AQI事件入库先
								pollIncident.setRecId(aqiService.insertPollIncident(pollIncident));
								// 还得存到map里
								DssService.pollIncidentMap.put(pointCode, pollIncident);
							}
							AirOprecDetlVo oprec = new AirOprecDetlVo();
							oprec.setAqi(iaqi);
							oprec.setDataTime(airResults.get(0).getDataTime());
							oprec.setPolluteName(aqiMap.get(iaqi));
							oprec.setRecId(pollIncident.getRecId());
							oprec.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
							oprec.setdType(i * 2);
							oprecs.add(oprec);
						}
					}

				}
			}
			if (flag && pollIncident != null) {
				if (pollIncident.getOfType() > 0 && pollIncident.getLastOf() == 0) {
					// 第一次未超标，连续两次未超标就要入库结束时间
					pollIncident.setLastOf(1);
				} else {
					// AQI时间入库结束时间
					pollIncident.seteTime(beginTime);
					pollIncs.add(pollIncident);
					//
					DssService.pollIncidentMap.put(pointCode, null);
				}
			}

		}
		// 分钟aqi数据入库
		if (CollectionUtils.isNotEmpty(minAqiDatas)) {
			aqiService.insertMinAqiDatas(minAqiDatas);
		}
		// 数据详情数据存在则入库
		if (CollectionUtils.isNotEmpty(oprecs)) {
			aqiService.insertAirOprecDetls(oprecs);
		}
		if (CollectionUtils.isNotEmpty(pollIncs)) {
			aqiService.updatePollIncident(pollIncs);
		}

	}

	/**
	 * 
	 * calculationAreaAQI:区域AQI统计
	 *
	 * @param areaCode
	 * @param dataTime
	 *            void
	 */
	public void calculationAreaAQI(String areaCode, Date dataTime, String CN, Integer dataStatus) {

		Optional<AreaVO> optional = areas.stream().filter(o -> o.getAreaCode().equals(areaCode)).findFirst();

		if (optional.isPresent()) {
			AreaVO vo = optional.get();
			String code = vo.getAreaCode();
			Integer level = vo.getLevel();

			if (StringUtil.isEmptyOrNull(code)) {
				return;
			}
			JSONObject area = new JSONObject();
			area.put("areaCode", code);
			area.put("level", level);
			area.put("dataTime", dataTime);
			area.put("dataStatus", dataStatus);
			area.put("CN", CN);
			queueArea.offer(area);

			String pCode = vo.getpCode();

			if (StringUtil.isEmptyOrNull(pCode)) {
				return;
			}
			calculationAreaAQI(pCode, dataTime, CN, dataStatus);
		}

		return;
	}

	/**
	 * 
	 * findChildAreaCode: 查找区域码
	 *
	 * @param areaCode
	 * @param codes
	 *            void
	 */
	private void findChildAreaCode(String areaCode, List<String> codes) {

		List<AreaVO> list = areas.stream().filter(o -> areaCode.equals(o.getpCode())).collect(Collectors.toList());

		for (AreaVO vo : list) {
			codes.add(vo.getAreaCode());
			findChildAreaCode(vo.getAreaCode(), codes);
		}

	}

	/**
	 * 
	 * calculationAQIHH:小时AQI统计
	 *
	 * @param pointCode
	 * @param areaCode
	 * @param level
	 * @param dataTime
	 *            void
	 */
	private void calculationAQIHH(int sk, String pointCode, String areaCode, Integer level, Date dataTime,
			Integer dataStatus) {

		try {
			String beginTime = String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH"));
			String endTime = String.format("%s:00:00",
					DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 1), "yyyy-MM-dd HH"));

			List<String> polluteCodes = airPollutes;
			Map<String, List<MonitorDataVO>> calculationMaps = monDataMap;
			if (sk == 1) {
				polluteCodes = skPollutes;
				calculationMaps = monSkDataMap;
			}

			// 获取所有区域所有子节点
			List<String> areaCodes = null;

			if (!StringUtil.isEmptyOrNull(areaCode)) {
				areaCodes = new ArrayList<String>();
				findChildAreaCode(areaCode, areaCodes);
				areaCodes.add(areaCode);
			}

			List<MonitorDataVO> metadata = new ArrayList<>();
			List<String> pointCodes = new ArrayList<>();

			// 启用离群判断
			if ("1".equalsIgnoreCase(DssService.ISLQ)) {
				// 需离群的数据集
				List<MonitorDataVO> outlierList = new ArrayList<>();
				// 按测点分组
				Map<String, List<MonitorDataVO>> metadataMaps = new HashMap<>();
				// 所有测点及相关区域下的测点
				List<String> allPointCodes = new ArrayList<>();

				// level4为测点，
				if (StringUtils.isNotEmpty(pointCode) && level == 4) {

					pointCodes.add(pointCode);
					// 、、1-3为区域
				} else if (CollectionUtils.isNotEmpty(areaCodes) && level != 4) {

					Map<String, StationVO> stationMap = DssService.stationMap;
					// 遍历获取区域下所有测点
					for (Entry<String, StationVO> station : stationMap.entrySet()) {
						StationVO stationVO = station.getValue();
						if (stationVO != null && StringUtils.isNotEmpty(stationVO.getAreaCode())) {
							for (String area : areaCodes) {
								if (stationVO.getAreaCode().equals(area)
										&& StringUtils.isNotEmpty(stationVO.getPointCode())) {
									pointCodes.add(stationVO.getPointCode());
								}
							}
						}
					}

				}

				// 获取所有测点
				allPointCodes = getAllPointCodes(pointCodes);
				metadata = aqiService.queryMointorDataHH(allPointCodes, null, polluteCodes, beginTime, endTime);

				if (CollectionUtils.isNotEmpty(metadata)) {

					metadataMaps = metadata.stream().collect(Collectors.groupingBy(MonitorDataVO::getPointCode));

					// 对区域数据进行离群值过滤
					metadataMaps = OutlierStatistics.outlierData(pointCodes, polluteCodes, metadataMaps,
							calculationMaps, outlierList);

					metadata = new ArrayList<>();

					if (metadataMaps != null && metadataMaps.size() > 0) {
						for (Entry<String, List<MonitorDataVO>> metadataMs : metadataMaps.entrySet()) {

							List<MonitorDataVO> calcultList = calculationMaps.get(metadataMs.getKey());
							// 离群时间缓存刷新
							calcultList = calcultList.stream()
									.filter(u -> DateUtil.dateDiffHour(u.getDeDataTime(), dataTime) < 24)
									.collect(Collectors.toList());
							calcultList.addAll(metadataMs.getValue());
							calculationMaps.put(metadataMs.getKey(), calcultList);
							metadata.addAll(metadataMs.getValue());
						}
					}

				}
				// 若有离群数据，则进行数据库存疑标识修改
				if (CollectionUtils.isNotEmpty(outlierList)) {
					aqiService.updateOutlierHH(outlierList);
				}

			} else {

				if (!StringUtil.isEmptyOrNull(pointCode)) {
					pointCodes.add(pointCode);
				} else {
					pointCodes = null;
				}
				metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime, endTime);
			}

			Map<String, DoubleSummaryStatistics> metadataavg = null;

			Integer isValid = 1;

			if (metadata != null && metadata.size() > 0) {

				int size = metadata.size();
				// 无效数据和负值不参与计算
				metadata = metadata.stream()
						.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
						.collect(Collectors.toList());

				if (metadata != null && metadata.size() != size) {
					isValid = 0;
				}

				if (dataStatus == 1) {
					metadataavg = metadata.stream().collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
							Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
				} else {
					metadataavg = metadata.stream().collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
							Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
				}
			} else {
				isValid = 0;
			}

			Double so2avg = null;
			Integer so2iaqi = null;
			Double no2avg = null;
			Integer no2iaqi = null;
			Double pm10avg = null;
			Double pm1024avg = null;
			Integer pm1024iaqi = null;
			Double coavg = null;
			Integer coiaqi = null;
			Double o31avg = null;
			Integer o31iaqi = null;
			Double o38avg = null;
			Integer o38iaqi = null;
			Double pm25avg = null;
			Double pm2524avg = null;
			Integer pm2524iaqi = null;

			Integer iaqi = null;
			Integer iaqid = null;

			List<IAQIDataVO> iaqiData = new ArrayList<IAQIDataVO>();

			List<Integer> iaqiarr = new ArrayList<Integer>();

			if (metadataavg != null) {
				for (Map.Entry<String, DoubleSummaryStatistics> entry : metadataavg.entrySet()) {

					String polluteCode = entry.getKey();
					String polluteName = "";
					iaqi = null;
					PolluteVO polluteVO = DssService.airSixParam.get(polluteCode);
					int precision = 0;
					if (polluteVO != null) {
						polluteName = polluteVO.getPolluteName();
						precision = polluteVO.getNumPrecision();
					}

					DoubleSummaryStatistics statistics = entry.getValue();

					Double sum = null;
					Long count = null;
					Double avgValue = null;
					if (statistics != null) {
						sum = statistics.getSum();
						count = statistics.getCount();
						avgValue = Arith.div(count, sum);
						avgValue = dataRounding(avgValue, precision);
					}

					iaqi = calculationIAQI(polluteCode, avgValue, 1);

					switch (polluteCode) {
					case AirSixParam.O3:
					case AirSixParam.SK_O3:
						o31avg = avgValue;
						o31iaqi = iaqi;
						break;
					case AirSixParam.NO2:
					case AirSixParam.SK_NO2:
						no2avg = avgValue;
						no2iaqi = iaqi;
						break;
					case AirSixParam.CO:
					case AirSixParam.SK_CO:
						coavg = avgValue;
						coiaqi = iaqi;
						break;
					case AirSixParam.SO2:
					case AirSixParam.SK_SO2:
						if (avgValue > 800) {
							iaqi = calculationIAQI(polluteCode,
									getSO224Avg(pointCode, polluteCode, areaCodes, dataTime, dataStatus), 24);
						}
						so2avg = avgValue;
						so2iaqi = iaqi;
						break;
					case AirSixParam.PM10:
					case AirSixParam.SK_PM10:
						pm10avg = avgValue;
						pm1024iaqi = iaqi;
						break;
					case AirSixParam.PM25:
					case AirSixParam.SK_PM25:
						pm25avg = avgValue;
						pm2524iaqi = iaqi;
						break;
					}

					IAQIDataVO vo = new IAQIDataVO();
					vo.setDataType(1);
					vo.setIaqi(iaqi);
					vo.setPolluteCode(polluteCode);
					vo.setPolluteName(polluteName);
					vo.setDataValue(avgValue);
					iaqiData.add(vo);

					if (iaqi != null) {
						iaqiarr.add(iaqi);
					}
				}
			}

			String[] PM = new String[] { AirSixParam.PM25, AirSixParam.PM10 };
			// 實況因子
			if (sk == 1) {
				PM = new String[] { AirSixParam.SK_PM25, AirSixParam.SK_PM10 };
			}

			Map<String, DoubleSummaryStatistics> PM24MoveAvgs = getPM24MoveAvg(pointCode, PM, areaCodes, dataTime,
					dataStatus);
			if (PM24MoveAvgs != null) {
				for (Map.Entry<String, DoubleSummaryStatistics> entry : PM24MoveAvgs.entrySet()) {

					String polluteCode = entry.getKey();
					String polluteName = "";
					iaqi = null;
					PolluteVO polluteVO = DssService.airSixParam.get(polluteCode);
					int precision = 0;
					if (polluteVO != null) {
						polluteName = polluteVO.getPolluteName();
						precision = polluteVO.getNumPrecision();
					}

					DoubleSummaryStatistics statistics = entry.getValue();

					Double sum = null;
					Long count = null;
					Double avgValue = null;
					if (statistics != null) {
						sum = statistics.getSum();
						count = statistics.getCount();
						avgValue = Arith.div(count, sum);
						avgValue = dataRounding(avgValue, precision);
					}

					iaqi = calculationIAQI(polluteCode, avgValue, 24);
					IAQIDataVO vo = new IAQIDataVO();
					iaqiData.add(vo);
					switch (polluteCode) {
					case AirSixParam.PM10:
					case AirSixParam.SK_PM10:
						pm1024avg = avgValue;
						break;
					case AirSixParam.PM25:
					case AirSixParam.SK_PM25:
						pm2524avg = avgValue;
						break;
					}

					vo.setDataType(24);
					vo.setPolluteCode(polluteCode);
					vo.setPolluteName(polluteName);
					vo.setDataValue(avgValue);
				}
			}

			String O3 = AirSixParam.O3;
			// 實況因子
			if (sk == 1) {
				O3 = AirSixParam.SK_O3;
			}

			o38avg = getO38MoveAvg(pointCode, O3, areaCodes, dataTime, dataStatus);
			String polluteName = "";
			if (o38avg != null) {

				PolluteVO polluteVO = DssService.airSixParam.get(O3);
				int precision = 0;
				if (polluteVO != null) {
					polluteName = polluteVO.getPolluteName();
					precision = polluteVO.getNumPrecision();
				}
				o38avg = dataRounding(o38avg, precision);

				if (o38avg > 800) {
					o38iaqi = o31iaqi;
				} else {
					o38iaqi = calculationIAQI(O3, o38avg, 8);
				}
			} else {

				isValid = 0;
			}

			IAQIDataVO vo = new IAQIDataVO();
			vo.setDataType(8);
			vo.setIaqi(o38iaqi);
			vo.setPolluteName(polluteName);
			vo.setDataValue(o38avg);
			vo.setPolluteCode(O3);

			iaqiData.add(vo);

			// 实时报O3-8不参与统计

			/*
			 * if (o38iaqi != null) { iaqiarr.add(o38iaqi); }
			 */

			Integer aqi = null;
			if (iaqiarr != null && iaqiarr.size() > 0) {
				aqi = Collections.max(iaqiarr);
			}

			Map<String, String> pollute = new HashMap<String, String>();
			for (IAQIDataVO iaqiVO : iaqiData) {

				if (iaqiVO != null && iaqiVO.getIaqi() == aqi) {
					pollute.put(iaqiVO.getPolluteCode(), iaqiVO.getPolluteName());
				}
			}

			iaqid = null;
			if (aqi != null) {
				AQILevelVO levelVO = getAqiLevel(aqi);
				if (levelVO != null) {
					iaqid = levelVO.getAqiid();
				}
			}

			AqiDataVO aqiData = new AqiDataVO();

			String code = pointCode;
			if (StringUtil.isEmptyOrNull(code)) {
				code = areaCode;
			}

			if (dataStatus == 0 && sk == 0) {
				// 原始数据标况
				dataStatus = 0;
			} else if (dataStatus == 1 && sk == 0) {
				// 审核数据标况
				dataStatus = 1;
			} else if (dataStatus == 0 && sk == 1) {
				// 原始数据实况
				dataStatus = 2;
			} else if (dataStatus == 1 && sk == 1) {
				// 审核数据实况
				dataStatus = 3;
			}

			aqiData.setIsValided(isValid);
			aqiData.setCode(code);
			aqiData.setAqi(aqi);
			aqiData.setAqiId(iaqid);
			aqiData.setCo(coavg);
			aqiData.setCoIaqi(coiaqi);
			aqiData.setDataTime(String.format("%s:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd HH")));
			aqiData.setLevel(level);
			aqiData.setNo2(no2avg);
			aqiData.setNo2Iaqi(no2iaqi);
			aqiData.setO3(o31avg);
			aqiData.setO3Iaqi(o31iaqi);
			aqiData.setO38(o38avg);
			aqiData.setO38Iaqi(o38iaqi);
			aqiData.setPm10(pm10avg);
			aqiData.setAvgvaluePm10(pm1024avg);
			aqiData.setPm10Iaqi(pm1024iaqi);
			aqiData.setPm25(pm25avg);
			aqiData.setAvgvaluePm25(pm2524avg);
			aqiData.setPm25Iaqi(pm2524iaqi);
			aqiData.setSo2(so2avg);
			aqiData.setSo2Iaqi(so2iaqi);
			aqiData.setDataStatus(String.valueOf(dataStatus));
			aqiData.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			if (aqi != null && aqi > 50) {
				aqiData.setPolluteName(
						pollute.values().toString().substring(1, pollute.values().toString().length() - 1));
			}

			storage("H", aqiData);
		} catch (Exception e) {
			logger.error("统计AQI小时数据出现异常:" , e);
		}

	}

	/**
	 * 
	 * calculationAQIDD:日AQI统计
	 *
	 * @param pointCode
	 * @param areaCode
	 * @param level
	 * @param dataTime
	 *            void
	 */
	private void calculationAQIDD(int sk, String pointCode, String areaCode, Integer level, Date dataTime,
			Integer dataStatus) {
		try {
			String beginTime = String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
			String endTime = String.format("%s 00:00:00",
					DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));

			List<String> polluteCodes = Arrays.asList(new String[] { AirSixParam.NO2, AirSixParam.CO, AirSixParam.SO2,
					AirSixParam.PM10, AirSixParam.PM25 });

			if (sk == 1) {
				polluteCodes = Arrays.asList(new String[] { AirSixParam.SK_O3, AirSixParam.SK_NO2, AirSixParam.SK_CO,
						AirSixParam.SK_SO2, AirSixParam.SK_PM10, AirSixParam.SK_PM25 });
			}

			// 获取所有区域所有子节点
			List<String> areaCodes = null;

			if (!StringUtil.isEmptyOrNull(areaCode)) {
				areaCodes = new ArrayList<String>();
				findChildAreaCode(areaCode, areaCodes);
				areaCodes.add(areaCode);
			}
			List<MonitorDataVO> metadata = aqiService.queryMointorDataDD(pointCode, areaCodes, polluteCodes, beginTime,
					endTime);

			Map<String, DoubleSummaryStatistics> metadataavg = null;

			Integer isValid = 1;

			if (metadata != null && metadata.size() > 0) {

				int size = metadata.size();

				metadata = metadata.stream()
						.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
						.collect(Collectors.toList());

				if (metadata != null && metadata.size() != size) {
					isValid = 0;
				}

				if (dataStatus == 1) {
					metadataavg = metadata.stream().collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
							Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));

				} else {
					metadataavg = metadata.stream().collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
							Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));
				}

			} else {
				isValid = 0;
			}

			Double so2avg = null;
			Integer so2iaqi = null;
			Double no2avg = null;
			Integer no2iaqi = null;
			Double pm10avg = null;
			Integer pm10iaqi = null;
			Double coavg = null;
			Integer coiaqi = null;
			Double o31maxavg = null;
			Integer o31maxiaqi = null;
			Double o38maxavg = null;
			Integer o38maxiaqi = null;
			Double pm25avg = null;
			Integer pm25iaqi = null;

			Integer iaqi = null;
			Integer iaqid = null;

			Map<Integer, Set<String>> aqiMap = new HashMap<>();
			List<IAQIDataVO> iaqiData = new ArrayList<IAQIDataVO>();

			if (metadataavg != null) {
				for (Map.Entry<String, DoubleSummaryStatistics> entry : metadataavg.entrySet()) {

					String polluteCode = entry.getKey();
					String polluteName = "";
					iaqi = null;
					PolluteVO polluteVO = DssService.airSixParam.get(polluteCode);
					int precision = 0;
					if (polluteVO != null) {
						polluteName = polluteVO.getPolluteName();
						precision = polluteVO.getNumPrecision();
					}

					DoubleSummaryStatistics statistics = entry.getValue();

					Double sum = null;
					Long count = null;
					Double avgValue = null;
					if (statistics != null) {
						sum = statistics.getSum();
						count = statistics.getCount();
						avgValue = Arith.div(count, sum);
						avgValue = dataRounding(avgValue, precision);
					}

					iaqi = calculationIAQI(polluteCode, avgValue, 24);
					IAQIDataVO vo = new IAQIDataVO();
					vo.setDataType(1);
					vo.setIaqi(iaqi);
					vo.setPolluteCode(polluteCode);
					vo.setPolluteName(polluteName);
					vo.setDataValue(avgValue);
					iaqiData.add(vo);

					if (iaqi != null) {
						if (aqiMap.containsKey(iaqi)) {
							aqiMap.get(iaqi).add(polluteName);
						} else {
							Set<String> aqiSet = new HashSet<>();
							aqiSet.add(polluteName);
							aqiMap.put(iaqi, aqiSet);
						}
					}

					switch (polluteCode) {
					case AirSixParam.NO2:
					case AirSixParam.SK_NO2:
						no2avg = avgValue;
						no2iaqi = iaqi;
						break;
					case AirSixParam.CO:
					case AirSixParam.SK_CO:
						coavg = avgValue;
						coiaqi = iaqi;
						break;
					case AirSixParam.SO2:
					case AirSixParam.SK_SO2:
						so2avg = avgValue;
						so2iaqi = iaqi;
						break;
					case AirSixParam.PM10:
					case AirSixParam.SK_PM10:
						pm10avg = avgValue;
						pm10iaqi = iaqi;
						break;
					case AirSixParam.PM25:
					case AirSixParam.SK_PM25:
						pm25avg = avgValue;
						pm25iaqi = iaqi;
						break;
					}
				}
			}

			String O3 = AirSixParam.O3;
			if (sk == 1) {
				O3 = AirSixParam.SK_O3;
			}

			String polluteName = "";
			PolluteVO polluteVO = DssService.airSixParam.get(O3);
			int precision = 0;
			if (polluteVO != null) {
				polluteName = polluteVO.getPolluteName();
				precision = polluteVO.getNumPrecision();
			}

			o31maxavg = dataRounding(getO31MaxAvg(pointCode, O3, areaCodes, dataTime, dataStatus), precision);
			if (o31maxavg != null) {
				o31maxiaqi = calculationIAQI(O3, o31maxavg, 1);
			}

			// O3 1小时最大值不参与aqi计算（2018-09-05 13:00）
			/*
			 * IAQIDataVO vo = new IAQIDataVO(); vo.setDataType(1);
			 * vo.setIaqi(o31maxiaqi); vo.setPolluteCode(AirSixParam.O3);
			 * vo.setPolluteName(polluteName); vo.setDataValue(o31maxavg);
			 * iaqiData.add(vo);
			 * 
			 * if (o31maxiaqi != null) { iaqiarr.add(o31maxiaqi); }
			 */

			o38maxavg = dataRounding(getO38AvgMax(pointCode, O3, areaCodes, dataTime, dataStatus), precision);
			if (o38maxavg != null) {

				if (o38maxavg > 800) {
					o38maxiaqi = o31maxiaqi;
				} else {
					o38maxiaqi = calculationIAQI(O3, o38maxavg, 8);
				}
			} else {

				isValid = 0;
			}

			IAQIDataVO vo = new IAQIDataVO();
			vo.setDataType(8);
			vo.setIaqi(o38maxiaqi);
			vo.setPolluteCode(O3);
			vo.setPolluteName(polluteName);
			vo.setDataValue(o38maxavg);
			iaqiData.add(vo);

			if (o38maxiaqi != null) {
				if (aqiMap.containsKey(o38maxiaqi)) {
					aqiMap.get(o38maxiaqi).add(polluteName);
				} else {
					Set<String> aqiSet = new HashSet<>();
					aqiSet.add(O3);
					aqiMap.put(o38maxiaqi, aqiSet);
				}
			}

			// logger.info(String.format("日排序 %s", JSON.toJSON(iaqiData)));

			Integer aqi = null;

			if (aqiMap.size() > 0) {
				aqi = Collections.max(aqiMap.keySet());
			}

			Map<String, String> pollute = new HashMap<String, String>();
			for (IAQIDataVO iaqiVO : iaqiData) {

				if (iaqiVO != null && iaqiVO.getIaqi() == aqi) {
					pollute.put(iaqiVO.getPolluteCode(), iaqiVO.getPolluteName());
				}
			}

			iaqid = null;
			if (aqi != null) {
				AQILevelVO levelVO = getAqiLevel(aqi);
				if (levelVO != null) {
					iaqid = levelVO.getAqiid();
				}
			}

			if (iaqid != null && iaqid > 2) {
				isValid = 1;
			}

			AqiDataVO aqiData = new AqiDataVO();

			String code = pointCode;

			if (StringUtil.isEmptyOrNull(code)) {
				code = areaCode;
			}

			if (dataStatus == 0 && sk == 0) {
				// 原始数据标况
				dataStatus = 0;
			} else if (dataStatus == 1 && sk == 0) {
				// 审核数据标况
				dataStatus = 1;
			} else if (dataStatus == 0 && sk == 1) {
				// 原始数据实况
				dataStatus = 2;
			} else if (dataStatus == 1 && sk == 1) {
				// 审核数据实况
				dataStatus = 3;
			}

			// 原始数据标况
			if (aqi != null && dataStatus == 0) {

				// 原始数据标况
				AirHvyDayVo errHvyDay = DssService.polluteTimeMap.get(code);
				// 没有开始时间需要添加
				if (errHvyDay != null && StringUtils.isEmpty(errHvyDay.getSdateTime())) {

					String bgTime = aqiService.getBETime(code, errHvyDay.getSdate(),
							DateUtil.dateToStr(DateUtil.dateAddDay(errHvyDay.getSDedate(), 1), "yyyy-MM-dd HH:mm:ss"),
							"1");
					errHvyDay.setSdateTime(StringUtils.isEmpty(bgTime) ? errHvyDay.getSdate() : bgTime);
				}
				// 先判断数据连续性,连续超标中断的情况
				if (errHvyDay != null && (aqi <= 200 || DateUtil.dateDiffDay(errHvyDay.getEDedate(), dataTime) < 0
						|| DateUtil.dateDiffDay(errHvyDay.getEDedate(), dataTime) > 1)) {
					// 已经断层的情况下，大于等于三天对数据入库
					if (DateUtil.dateDiffDay(errHvyDay.getSDedate(), errHvyDay.getEDedate()) >= 3) {
						// 设置结束时间
						String eDate = DateUtil.dateToStr(DateUtil.dateAddDay(errHvyDay.getEDedate(), 1),
								"yyyy-MM-dd HH:mm:ss");
						String edTime = aqiService.getBETime(code, errHvyDay.getEdate(), eDate, null);
						errHvyDay.setEdateTime(StringUtils.isEmpty(edTime) ? errHvyDay.getEdate() : edTime);
						// 这里需要修改数据库
						aqiService.insertAirHvy(errHvyDay);
					}
					// 移除对应数据
					DssService.polluteTimeMap.remove(code);
					errHvyDay = null;
				}
				// 测点或区域没有超标数据，重新存入Map中
				if (aqi > 200) {
					if (errHvyDay == null) {
						errHvyDay = new AirHvyDayVo();
						errHvyDay.setCode(code);
						errHvyDay.setEdate(dataTime);
						errHvyDay.setSdate(dataTime);
						errHvyDay.setDlevel(level);
						errHvyDay.getPollutes().addAll(aqiMap.get(aqi));

						DssService.polluteTimeMap.put(code, errHvyDay);
					} else {
						errHvyDay.getPollutes().addAll(aqiMap.get(aqi));
						errHvyDay.setEdate(dataTime);
						// 判断当前日期与第一次超标时间差值是否大于等于3，连续三天或以上
						if (DateUtil.dateDiffDay(errHvyDay.getSDedate(), dataTime) == 3) {
							// 第一次入库需要计算开始时间
							String begTime = aqiService.getBETime(code, errHvyDay.getSdate(), DateUtil.dateToStr(
									DateUtil.dateAddDay(errHvyDay.getSDedate(), 1), "yyyy-MM-dd HH:mm:ss"), "1");
							errHvyDay.setSdateTime(StringUtils.isEmpty(begTime) ? errHvyDay.getSdate() : begTime);
						}
						if (DateUtil.dateDiffDay(errHvyDay.getSDedate(), dataTime) >= 3) {
							// 这里需要直接入库操作
							aqiService.insertAirHvy(errHvyDay);
						}
					}
				}
			}

			aqiData.setIsValided(isValid);
			aqiData.setCode(code);
			aqiData.setAqi(aqi);
			aqiData.setAqiId(iaqid);
			aqiData.setCo(coavg);
			aqiData.setCoIaqi(coiaqi);
			aqiData.setDataTime(String.format("%s 00:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd")));
			aqiData.setLevel(level);
			aqiData.setNo2(no2avg);
			aqiData.setNo2Iaqi(no2iaqi);
			aqiData.setO3(o31maxavg);
			aqiData.setO3Iaqi(o31maxiaqi);
			aqiData.setO38(o38maxavg);
			aqiData.setO38Iaqi(o38maxiaqi);
			aqiData.setPm10(pm10avg);
			aqiData.setPm10Iaqi(pm10iaqi);
			aqiData.setPm25(pm25avg);
			aqiData.setPm25Iaqi(pm25iaqi);
			aqiData.setSo2(so2avg);
			aqiData.setSo2Iaqi(so2iaqi);
			aqiData.setDataStatus(String.valueOf(dataStatus));
			aqiData.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
			if (aqi != null && aqi > 50) {
				aqiData.setPolluteName(
						pollute.values().toString().substring(1, pollute.values().toString().length() - 1));

			}
			storage("D", aqiData);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * dataRounding:数据修约
	 *
	 * @param value
	 * @param precision
	 *            精度
	 * @return Double
	 */
	private Double dataRounding(Double value, int precision) {

		try {
			if (value == null) {
				return null;
			}
			return NumberFormatUtil.formatByScale(value, precision);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * storage:AQI入库存储
	 *
	 * @param aqiType
	 * @param aqiData
	 *            void
	 */
	private void storage(String aqiType, AqiDataVO aqiData) {

		logger.info(String.format("AQI[%s]%s", aqiType, JSON.toJSON(aqiData)));

		if ("D".equals(aqiType)) {

			aqiService.insertAQIDD(aqiData);

		} else {

			if (aqiData.getLevel() == 4) {
				Map<String, Object> json = new HashMap<String, Object>();
				json.put("CN", "AQI");
				json.put("ID", aqiData.getCode());
				json.put("AQI", aqiData.getAqi());
				json.put("Level", aqiData.getLevel());
				json.put("DataTime", aqiData.getDataTime());

				publish.publishTM212(JSON.toJSONString(json));
			}

			aqiService.insertAQIHH(aqiData);
		}
	}

	/**
	 * 
	 * getAqiLevel:获取aqi对应的空气质量等级
	 *
	 * @param iaqi
	 *            aqi值
	 * @return AQILevelVO
	 */
	private AQILevelVO getAqiLevel(Integer iaqi) {

		if (AQILevel == null) {
			return null;
		}

		List<AQILevelVO> levels = AQILevel.stream().filter(o -> o.getMinAqi() <= iaqi && o.getMaxAqi() >= iaqi)
				.collect(Collectors.toList());
		if (levels != null && levels.size() > 0) {

			return levels.get(0);
		}
		return null;
	}

	/**
	 * 
	 * calculationIAQI:计算因子aqi分子数
	 *
	 * @param polluteCode
	 *            污染物编码
	 * @param dataValue
	 *            污染物浓度
	 * @param dataType
	 *            数据类型
	 * @return Double
	 */
	private Integer calculationIAQI(String polluteCode, Double dataValue, double dataType) {
		Integer iaqi = null;
		if (IAQIRange == null) {
			return null;
		}

		List<IAQIRangeVO> ranges = IAQIRange
				.stream().filter(o -> o.getBphi() >= dataValue && o.getBplo() <= dataValue
						&& o.getDataType() == dataType && o.getPolluteCode().equals(polluteCode))
				.collect(Collectors.toList());

		if (ranges == null || ranges.size() < 1) {

			ranges = IAQIRange.stream()
					.filter(o -> o.getDataType() == dataType && o.getPolluteCode().equals(polluteCode))
					.collect(Collectors.toList());
		}
		BigDecimal aqi = null;

		if (ranges != null && ranges.size() > 0) {

			IAQIRangeVO range = ranges.get(ranges.size() - 1);

			BigDecimal iaqihi = new BigDecimal(Double.toString(range.getIaqihi()));
			BigDecimal iaqilo = new BigDecimal(Double.toString(range.getIaqilo()));
			BigDecimal bphi = new BigDecimal(Double.toString(range.getBphi()));
			BigDecimal bplo = new BigDecimal(Double.toString(range.getBplo()));
			BigDecimal avg = new BigDecimal(Double.toString(dataValue));

			aqi = iaqihi.subtract(iaqilo).divide(bphi.subtract(bplo), 10, BigDecimal.ROUND_HALF_DOWN)
					.multiply(avg.subtract(bplo)).add(iaqilo);

		}
		if (aqi == null) {
			return null;
		}
		iaqi = aqi.setScale(0, BigDecimal.ROUND_UP).intValue();

		if (iaqi > 500) {
			iaqi = 500;
		}

		return iaqi;
	}

	/**
	 * 
	 * getPM24MoveAvg:PM2.5 PM10 24小时滑动均值
	 *
	 * @param tableName
	 * @param pointCode
	 * @param dataTime
	 *            void
	 */
	private Map<String, DoubleSummaryStatistics> getPM24MoveAvg(String pointCode, String[] poulluteCode,
			List<String> areaCodes, Date dataTime, Integer datastatus) {

		String beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, -23), "yyyy-MM-dd HH:mm:ss");
		String endTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 1), "yyyy-MM-dd HH:mm:ss");

		List<String> polluteCodes = Arrays.asList(poulluteCode);
		List<String> pointCodes = new ArrayList<>();
		if (StringUtils.isNoneBlank(pointCode)) {
			pointCodes.add(pointCode);
		}

		List<MonitorDataVO> metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime,
				endTime);

		Map<String, DoubleSummaryStatistics> doubleSummaryStatistics = new HashMap<String, DoubleSummaryStatistics>();

		if (metadata != null && metadata.size() > 0) {

			metadata = metadata.stream()
					.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
					.collect(Collectors.toList());

			if (datastatus == 1) {
				doubleSummaryStatistics = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
						.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
								Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding)));

			} else {
				doubleSummaryStatistics = metadata.stream().filter(o -> o.getDataValueRounding() != null)
						.collect(Collectors.groupingBy(MonitorDataVO::getPolluteCode,
								Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding)));
			}
		}

		return doubleSummaryStatistics;
	}

	/**
	 * 
	 * getSO224Avg 24小时均值
	 *
	 * @param tableName
	 * @param pointCode
	 * @param dataTime
	 *            void
	 */
	private Double getSO224Avg(String pointCode, String polluteCode, List<String> areaCodes, Date dataTime,
			Integer datastatus) {

		String beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
		String endTime = String.format("%s 01:00:00",
				DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));
		List<String> pointCodes = new ArrayList<>();
		if (StringUtils.isNoneBlank(pointCode)) {
			pointCodes.add(pointCode);
		}

		Double avg = null;
		List<String> polluteCodes = Arrays.asList(polluteCode.split(","));
		List<MonitorDataVO> metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime,
				endTime);
		if (metadata != null && metadata.size() > 0) {

			metadata = metadata.stream()
					.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
					.collect(Collectors.toList());

			DoubleSummaryStatistics statistics = null;
			if (datastatus == 1) {
				statistics = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
						.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding));
			} else {
				statistics = metadata.stream().filter(o -> o.getDataValueRounding() != null)
						.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding));
			}

			if (statistics != null) {
				Long count = statistics.getCount();
				Double sum = statistics.getSum();
				avg = Arith.div(count, sum);
			}

		}

		return avg;
	}

	/**
	 * 
	 * getO38MoveAvg:O3 8小时滑动均值
	 *
	 * @param tableName
	 * @param pointCode
	 * @param dataTime
	 *            void
	 */
	private Double getO38MoveAvg(String pointCode, String polluteCode, List<String> areaCodes, Date dataTime,
			Integer datastatus) {

		String beginTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, -7), "yyyy-MM-dd HH:mm:ss");
		String endTime = DateUtil.dateToStr(DateUtil.dateAddHour(dataTime, 1), "yyyy-MM-dd HH:mm:ss");
		Double avg = null;

		List<String> pointCodes = new ArrayList<>();
		if (StringUtils.isNoneBlank(pointCode)) {
			pointCodes.add(pointCode);
		}

		List<String> polluteCodes = Arrays.asList(polluteCode.split(","));
		List<MonitorDataVO> metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime,
				endTime);
		if (metadata != null && metadata.size() > 0) {

			metadata = metadata.stream()
					.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
					.collect(Collectors.toList());

			DoubleSummaryStatistics statistics = null;
			if (datastatus == 1) {
				statistics = metadata.stream().filter(o -> o.getAuditValueRounding() != null)
						.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding));

			} else {
				statistics = metadata.stream().filter(o -> o.getDataValueRounding() != null)
						.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding));

			}

			if (statistics != null) {
				Long count = statistics.getCount();
				Double sum = statistics.getSum();
				avg = Arith.div(count, sum);
			}
		}

		return avg;
	}

	/**
	 * 
	 * getO31MaxAvg:O3 最大1小时均值
	 *
	 * @param tableName
	 * @param pointCode
	 * @param dataTime
	 *            void
	 */
	private Double getO31MaxAvg(String pointCode, String polluteCode, List<String> areaCodes, Date dataTime,
			Integer datastatus) {

		String beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
		String endTime = String.format("%s 01:00:00",
				DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));

		Double maxValue = null;
		List<String> pointCodes = new ArrayList<>();
		if (StringUtils.isNoneBlank(pointCode)) {
			pointCodes.add(pointCode);
		}

		List<String> polluteCodes = new ArrayList<String>();
		polluteCodes.add(polluteCode);

		List<MonitorDataVO> metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime,
				endTime);
		if (metadata != null && metadata.size() > 0) {

			metadata = metadata.stream()
					.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
					.collect(Collectors.toList());

			Optional<MonitorDataVO> maxObjs = null;
			if (datastatus == 1) {
				maxObjs = metadata.stream()
						.collect(Collectors.maxBy(Comparator.comparingDouble(MonitorDataVO::getAuditValue)));
			} else {
				maxObjs = metadata.stream()
						.collect(Collectors.maxBy(Comparator.comparingDouble(MonitorDataVO::getAuditValue)));
			}

			if (maxObjs.isPresent()) {

				MonitorDataVO maxObj = maxObjs.get();

				if (maxObj != null) {
					maxValue = maxObj.getAuditValue();
				}
			}
		}
		return maxValue;
	}

	/**
	 * 
	 * getO38AvgMax:O3 8小时最大滑动均值
	 *
	 * @param tableName
	 *            表名
	 * @param pointCode
	 * @param dataTime
	 * @return Double
	 */
	private Double getO38AvgMax(String pointCode, String polluteCode, List<String> areaCodes, Date dataTime,
			Integer datastatus) {

		String beginTime = String.format("%s 01:00:00", DateUtil.dateToStr(dataTime, "yyyy-MM-dd"));
		String endTime = String.format("%s 01:00:00",
				DateUtil.dateToStr(DateUtil.dateAddDay(dataTime, 1), "yyyy-MM-dd"));

		List<String> pointCodes = new ArrayList<>();
		if (StringUtils.isNoneBlank(pointCode)) {
			pointCodes.add(pointCode);
		}

		List<Double> avgs = new ArrayList<Double>();
		Double maxAvg = null;
		int isValided = 0;

		List<String> polluteCodes = new ArrayList<String>();
		polluteCodes.add(polluteCode);

		List<MonitorDataVO> metadata = aqiService.queryMointorDataHH(pointCodes, areaCodes, polluteCodes, beginTime,
				endTime);

		if (metadata != null && metadata.size() > 0) {

			metadata = metadata.stream()
					.filter(o -> o.getIsValided() == 1 && o.getAuditValue() != null && o.getAuditValue() >= 0)
					.collect(Collectors.toList());

			Date qtime = DateUtil.strToDate(beginTime, "yyyy-MM-dd HH:mm:ss");

			int loop = 17;

			// 有效组数
			int count = 0;

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

				if (datastatus == 1) {
					statistics = groups.stream().filter(o -> o.getAuditValueRounding() != null)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getAuditValueRounding));

				} else {
					statistics = groups.stream().filter(o -> o.getDataValueRounding() != null)
							.collect(Collectors.summarizingDouble(MonitorDataVO::getDataValueRounding));
				}

				if (statistics != null && groups.size() >= 6) {
					size = statistics.getCount();
					sum = statistics.getSum();
					avg = Arith.div(size, sum);
					avgs.add(avg);
				}

				// 每组有效个数要大于等于6个
				if (groups.size() >= 6) {

					count++;
				}

				qtime = DateUtil.dateAddHour(qtime, 1);

				loop--;

			} while (loop > 0);

			if (CollectionUtils.isNotEmpty(avgs)) {

				maxAvg = Collections.max(avgs);
			}

			if (count >= 14) {
				isValided = 1;
			} else {

				// 小于14个 且最大值O3 8滑动均值 大于浓度限值时有效
				Double std = DssService.O38.get(pointCode + polluteCode);
				if (std != null && maxAvg > std) {
					isValided = 1;
				}
			}

			if (isValided == 0) {
				maxAvg = null;
			}

		}

		return maxAvg;
	}

	/**
	 * getAllPointCodes:获取测点相关的所有测点 void
	 */
	public List<String> getAllPointCodes(List<String> pointCodes) {

		if (CollectionUtils.isEmpty(pointCodes)) {
			return new ArrayList<>();
		}
		Set<String> allPointCodes = new HashSet<>();

		for (String code : pointCodes) {
			if (areaPointMap.containsKey(code)) {
				allPointCodes.addAll(areaPointMap.get(code));
			}
		}

		return new ArrayList<>(allPointCodes);

	}

	/**
	 * initAreaPoint:查询测点所属区域的其他测点数据集 void
	 */
	public void initAreaPoint() {

		List<ParamsVo> areaPoints = aqiService.queryAreaPoint();
		Map<String, List<String>> areMap = new HashMap<>();
		for (ParamsVo areaPoint : areaPoints) {
			List<String> Points;
			if (areMap.containsKey(areaPoint.getAreaCode())) {
				Points = areMap.get(areaPoint.getAreaCode());
			} else {
				Points = new ArrayList<>();
				areMap.put(areaPoint.getAreaCode(), Points);
			}
			Points.add(areaPoint.getPointCode());
			areaPoint.setPointCodes(Points);
		}

		Iterator<ParamsVo> iterators = areaPoints.iterator();
		// 获取所有测点数的区域
		while (iterators.hasNext()) {
			ParamsVo iterator = iterators.next();
			// 添加区域内的所有测点，
			areaPointMap.put(iterator.getPointCode(), iterator.getPointCodes());

		}
		logger.info("测点所属区域的其他测点数据集");

	}

	/**
	 * initMonData:初始化测点下一天内所有数据 void
	 */
	public void initMonData() {

		Date endData = DateUtil.getCurrentDate();
		Date beginData = DateUtil.dateAddHour(endData, -24);
		String pointCode = null;
		String polluteCode = null;
		List<MonitorDataVO> monLists = new ArrayList<>();

		List<MonitorDataVO> monitorDas = aqiService.queryMonData(DateUtil.dateToStr(beginData, "yyyy-MM-dd HH:mm:ss"),
				DateUtil.dateToStr(endData, "yyyy-MM-dd HH:mm:ss"));

		for (MonitorDataVO monitorDa : monitorDas) {
			pointCode = monitorDa.getPointCode();
			polluteCode = monitorDa.getPolluteCode();
			if (StringUtils.isEmpty(pointCode)) {
				continue;
			}
			String mapKey = pointCode;

			if (airPollutes.contains(polluteCode)) {

				if (monDataMap.containsKey(mapKey)) {
					monLists = monDataMap.get(mapKey);
				} else {
					monLists = new ArrayList<>();
					monDataMap.put(mapKey, monLists);
				}

			} else if (skPollutes.contains(polluteCode)) {

				if (monSkDataMap.containsKey(mapKey)) {
					monLists = monSkDataMap.get(mapKey);
				} else {
					monLists = new ArrayList<>();
					monSkDataMap.put(mapKey, monLists);
				}
			} else {
				continue;
			}

			monLists.add(monitorDa);
		}

		logger.info("初始化测点下一天内所有数据");

	}

	/**
	 * NumberFormat:针对数据取四舍五入
	 * 
	 * @param value
	 * @param size
	 * @return Double
	 */
	private Double NumberFormat(Double value, int size) {

		return value == null ? null : NumberFormatUtil.formatByScale(value, size);
	}

	public static void main(String args[]) throws Exception {

		BigDecimal b1 = new BigDecimal(Double.toString(50));
		BigDecimal b2 = new BigDecimal(Double.toString(0));
		BigDecimal b3 = new BigDecimal(Double.toString(2));
		BigDecimal b4 = new BigDecimal(Double.toString(0));
		BigDecimal b5 = new BigDecimal(Double.toString(1.6));

		BigDecimal b6 = b1.subtract(b2).divide(b3.subtract(b4), 10, BigDecimal.ROUND_HALF_DOWN)
				.multiply(b5.subtract(b4)).add(b2);

		Double dataValue = b6.setScale(10, BigDecimal.ROUND_UP).doubleValue();

		System.out.println(NumberFormatUtil.formatByScale(dataValue, 19));

	}

}
