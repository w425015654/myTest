/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：aasService.java
* 包  名  称：com.zeei.das.aas
* 文件描述：服务初始化类
* 创建日期：2017年4月20日上午11:31:58
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日上午11:31:58 创建文件
*
*/

package com.zeei.das.aas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aas.alarm.AlarmIDUtil;
import com.zeei.das.aas.alarm.custom.RivEarlyWarn;
import com.zeei.das.aas.mq.Publish;
import com.zeei.das.aas.service.AlarmRuleService;
import com.zeei.das.aas.service.QueryDataService;
import com.zeei.das.aas.service.StationService;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.aas.vo.DoubtfulVo;
import com.zeei.das.aas.vo.FlagRuleVO;
import com.zeei.das.aas.vo.FlowFactorVO;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.PolluterLevelVo;
import com.zeei.das.aas.vo.RegionDataVO;
import com.zeei.das.aas.vo.RuleVO;
import com.zeei.das.aas.vo.STVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.SystemTableVO;
import com.zeei.das.aas.vo.T3020RuleVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.Md5Util;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：aasService 类 描 述：服务初始化类 功能描述：服务初始化类 创建作者：quanhongsheng
 */
@Component
public class AasService {

	@Autowired
	AlarmRuleService alarmRuleService;

	@Autowired
	Publish publish;

	@Autowired
	StationService stationService;

	@Autowired
	QueryDataService queryDataService;

	public static Logger logger = LoggerFactory.getLogger(AasService.class);

	// 站点告警 规则信息
	public static Map<String, RuleVO> ruleMap = new ConcurrentHashMap<String, RuleVO>();

	// 内存告警信息
	public static Map<String, AlarmInfoVO> alarmMap = new ConcurrentHashMap<String, AlarmInfoVO>();

	// 站点配置信息
	public static Map<String, StationVO> stationMap = new ConcurrentHashMap<String, StationVO>();

	// 系统配置信息
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	// 空气的 站点下的 存储 数据值持续不变开始时间和 一直不变的那个值
	public static Map<String, DoubtfulVo> doubtfulMap = new ConcurrentHashMap<String, DoubtfulVo>();

	// 系统污染物编码信息
	public static Map<String, PolluteVO> polluteMap = new ConcurrentHashMap<String, PolluteVO>();

	// 系统配置信息
	public static Map<String, SystemTableVO> tableNameMap = new ConcurrentHashMap<String, SystemTableVO>();

	// 区域站点数据统计
	public static Map<String, RegionDataVO> regionMap = new ConcurrentHashMap<String, RegionDataVO>();

	// 区域站点数据
	public static Map<String, RegionDataVO> regionDataMap = new ConcurrentHashMap<String, RegionDataVO>();

	public static Map<String, FlowFactorVO> flowFactor = new HashMap<String, FlowFactorVO>();

	// 2021告警数据
	public static Map<String, Object> T2021AlarmMap = new ConcurrentHashMap<String, Object>();

	// 告警码-状态编码
	public static Map<String, String> STMap = new ConcurrentHashMap<String, String>();

	// 系统编码
	public static Map<String, String> statusAlarm = new ConcurrentHashMap<String, String>();

	public static Map<String, AlarmRuleVO> alarmRules = new ConcurrentHashMap<String, AlarmRuleVO>();

	// 3020告警规则
	public static Map<String, T3020RuleVO> t3020AlarmRule = new ConcurrentHashMap<String, T3020RuleVO>();

	// flag告警规则
	public static Map<String, FlagRuleVO> flagAlarmRule = new ConcurrentHashMap<String, FlagRuleVO>();

	public static Map<String, FlowFactorVO> mapFlow = new HashMap<String, FlowFactorVO>();

	// 站点定制告警配置
	public static Map<String, List<String>> stationAlarm = new ConcurrentHashMap<String, List<String>>();

	// 波动告警数据
	public static Map<String, List<Double>> fluctuationMap = new ConcurrentHashMap<String, List<Double>>();

	// 水质污染三级评价标准
	public static Map<String, PolluterLevelVo> rivPolluteLevelMap = new ConcurrentHashMap<String, PolluterLevelVo>();

	// 站点离线告警编码
	public static String onlineCode = "10001";

	// 排除不产生告警的告警码
	private static List<String> excludedAlarmCode = new ArrayList<String>();

	// 小时aqi的门限值
	public static Integer aqiAlarm = 100;

	private static Map<String, Date> brokenTime = new HashMap<String, Date>();

	// 传输频率异常的告警，异常周期个数
	public static int FREQUENCYNUM = 3;
	// 传输频率异常的告警
	public static String FREQUENCYCODE = "90088";
	// 站点离线告警的时间设定
	public static int onlineTime = 30;
	// 站点离线告警的告警码
	public static String onlineType = "2";
	// 离线告警是否和停产相关
	public static int isTC = 0;
	// 倒挂告警的告警码
	public static String UPSIDEDOWNCODE = "90093";
	// 异常数据告警的告警码
	public static String ABNORMALCODE = "90094";
	// 波动告警告警码
	public static String FLUCTUATIONCODE = "90095";
	// 地表水小时数据周期
	public static Map<Integer, List<String>> waterHourPolluteMaps = new HashMap<>();

	public static String slowCode = null;
	public static int interval = 30;
	public static String flowCode = null;
	public static double range = 0.5;

	static {
		// 读取配置
		cfgMap = LoadConfigUtil.readXmlParam("aas");

		if (!StringUtil.isEmptyOrNull(cfgMap.get("excludedAlarmCode"))) {
			excludedAlarmCode = Arrays.asList(cfgMap.get("excludedAlarmCode").split(","));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("frequencyCode"))) {
			FREQUENCYCODE = cfgMap.get("frequencyCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("frequencyNum"))) {
			FREQUENCYNUM = Integer.valueOf(cfgMap.get("frequencyNum"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("isTC"))) {
			isTC = Integer.valueOf(cfgMap.get("isTC"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("onlineTime"))) {
			onlineTime = Integer.valueOf(cfgMap.get("onlineTime"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("onlineCode"))) {
			onlineCode = cfgMap.get("onlineCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("onlineType"))) {
			onlineType = cfgMap.get("onlineType");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("upsideDownCode"))) {
			UPSIDEDOWNCODE = cfgMap.get("upsideDownCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("abnormalCode"))) {
			ABNORMALCODE = cfgMap.get("abnormalCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("fluctuationCode"))) {
			FLUCTUATIONCODE = cfgMap.get("fluctuationCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("slowCode"))) {
			slowCode = cfgMap.get("slowCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("interval"))) {
			interval = Integer.valueOf(cfgMap.get("interval"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("flowCode"))) {
			flowCode = cfgMap.get("flowCode");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("range"))) {
			range = Double.valueOf(cfgMap.get("range"));
		}
	}
	ScheduledExecutorService service = Executors.newScheduledThreadPool(6);

	// 初始化服务配置信息
	@PostConstruct
	public void initConfig() {

		try {

			// 初始化告警信息
			initStatusMapAlarmCode(1);
			initAlarmInfo(1);
			initStationCfg(1);
			initRegionData(1);
			initFlowFactor(1);
			initAlarmRule();
			initSTInfo(1);
			initRivPolluteLevel(1);
			initSystemTableName(1);
			initPolluteCfg(1);
			logger.info("初始化完成！");

		} catch (Exception e) {
			logger.error("初始化系统告警信息异常:", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}

	}

	/**
	 * 
	 * initStatusMapAlarmCode:初始化状态吗-告警码 void
	 */
	public void initStatusMapAlarmCode(final int loop) {

		try {

			statusAlarm.clear();

			List<Map<String, String>> list = alarmRuleService.queryStatusMapAlarmCode();
			for (Map<String, String> map : list) {

				String key = "";
				String value = "";
				for (Map.Entry<String, String> m : map.entrySet()) {
					if ("statusCode".equals(m.getKey())) {
						key = String.valueOf(m.getValue());
					} else {
						value = String.valueOf(m.getValue());
					}
				}
				if (!StringUtil.isEmptyOrNull(key)) {
					statusAlarm.put(key, value);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initStatusMapAlarmCode(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

		logger.info("初始化状态吗-告警码完成");
	}

	/**
	 * 
	 * initAlarmInfo:初始化告警信息
	 */
	public void initAlarmInfo(final int loop) {
		try {

			alarmMap.clear();

			List<AlarmInfoVO> list = alarmRuleService.queryAlarm();
			for (AlarmInfoVO alarm : list) {

				String pointCode = alarm.getPointCode();
				String alarmCode = alarm.getAlarmCode();
				String polluteCode = alarm.getPolluteCode();
				String dataType = alarm.getDataType();

				// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
				String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);

				alarm.setStorage(true);
				// 判断规则是否已存在告警，进行告警信息初始化
				alarmMap.put(alarmId, alarm);

				// 2021告警 初始化映射关系
				if (statusAlarm.containsValue(alarmCode)) {
					@SuppressWarnings("unchecked")
					List<String> map = (List<String>) AasService.T2021AlarmMap.get(pointCode);
					if (map == null) {
						map = new ArrayList<String>();
						T2021AlarmMap.put(pointCode, map);
					}
					map.add(alarmId);
				}

				Date beginTime = alarm.getStartTime();

				// 设置站点离线时间
				if (onlineCode.equals(alarmCode) && beginTime != null) {

					brokenTime.put(pointCode, beginTime);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initAlarmInfo(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化告警信息完成");
		logger.info(JSON.toJSONString(brokenTime));
	}

	/**
	 * 
	 * initStationCfg:初始化站点配置
	 */
	public void initStationCfg(final int loop) {

		try {
			// 初始化站点配置
			List<StationVO> stations = alarmRuleService.queryStation();

			if (stations != null && stations.size() > 0) {
				for (StationVO station : stations) {

					if (station != null && !StringUtil.isEmptyOrNull(station.getMN())) {

						String pointCode = station.getPointCode();

						if (brokenTime.containsKey(pointCode)) {

							Date beginTime = brokenTime.get(pointCode);

							if (station.getHeartTime() == null) {
								station.setHeartTime(beginTime);
							}

							logger.info(String.format("站点：%s[%s]  离线时间：%s", pointCode, station.getMN(),
									beginTime == null ? "NULL" : DateUtil.dateToStr(beginTime, "yyyy-MM-dd HH:mm:ss")));
						}

						String[] mnStrs = station.getMN().split(",");
						// 新mn为多站点逗号分割
						for (String mnStr : mnStrs) {
							if (StringUtils.isNotBlank(mnStr)) {
								stationMap.put(mnStr, station);
							}
						}
						stationMap.put(station.getPointCode(), station);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initStationCfg(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化站点配置完成");
	}

	/**
	 * 
	 * initRegionData:初始化区域站点统计数据 void
	 */
	public void initRegionData(final int loop) {
		try {
			// 初始化空气站区域站点统计数据
			List<RegionDataVO> regions = alarmRuleService.queryRegionStation();
			if (regions != null && regions.size() > 0) {
				for (RegionDataVO region : regions) {
					regionMap.put(region.getRegionCode(), region);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initRegionData(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化区域站点统计数据");
	}

	/**
	 * 
	 * initSTInfo:初始化系统类型配置
	 */
	public void initSTInfo(final int loop) {
		try {
			List<STVO> stInfos = alarmRuleService.querySTInfo();

			if (stInfos != null && stInfos.size() > 0) {
				for (STVO st : stInfos) {

					if (!StringUtil.isEmptyOrNull(st.getST()) && !StringUtil.isEmptyOrNull(st.getTableName())) {
						STMap.put(st.getST(), st.getTableName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initSTInfo(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化系统类型配置");
	}

	/**
	 * 
	 * initPolluteCfg:初始化污染物编码配置文件 void
	 */
	public void initPolluteCfg(final int loop) {
		// 初始化站点配置
		try {
			polluteMap.clear();
			List<PolluteVO> pollutes = queryDataService.queryPollute();
			if (pollutes != null && pollutes.size() > 0) {
				for (PolluteVO pollute : pollutes) {

					String ST = pollute.getST();
					String polluteCode = pollute.getPolluteCode();

					String key = String.format("%s%s", ST, polluteCode);

					key = polluteCode;
					if (!StringUtil.isEmptyOrNull(key)) {
						polluteMap.put(key, pollute);
					}
				}
			}
			logger.info("初始化污染物编码信息完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPolluteCfg(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * initSystemTableName:初始化系统表名配置文件
	 * 
	 */
	public void initSystemTableName(final int loop) {
		try {
			// 初始化站点配置
			List<SystemTableVO> tables = stationService.queryTableName();
			if (tables != null && tables.size() > 0) {
				for (SystemTableVO table : tables) {
					if (!StringUtil.isEmptyOrNull(table.getST())) {
						tableNameMap.put(table.getST(), table);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initSystemTableName(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化系统表名配置文件");
	}

	/**
	 * 
	 * initAlarmRule:初始化告警规则
	 */
	public void initAlarmRule() {

		// 初始化规则告警
		initRuleAlarm(1);

		// 初始化自定义告警
		initCustomAlarmRule(1);

		// 消警处理
		cannelAlarm();
	}

	/**
	 * 
	 * initRuleAlarm:初始化规则告警 void
	 */
	public void initRuleAlarm(final int loop) {
		try {
			// 初始化站点告警规则
			List<AlarmRuleVO> rules = alarmRuleService.queryAlarmRule();

			ruleMap.clear();
			alarmRules.clear();

			if (rules != null && rules.size() > 0) {
				for (AlarmRuleVO rule : rules) {

					String dataType = rule.getDataType();
					String MN = rule.getMN();
					String pointCode = rule.getPointCode();
					String alarmCode = rule.getAlarmCode();
					String polluteCode = rule.getPolluteCode();

					if (StringUtil.isEmptyOrNull(MN)) {
						continue;
					}

					if (StringUtil.isEmptyOrNull(dataType)) {
						continue;
					}

					if (excludedAlarmCode.contains(alarmCode)) {
						continue;
					}

					// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
					String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);
					rule.setRuleId(alarmId);

					RuleVO vo = ruleMap.get(MN);

					if (vo == null) {
						vo = new RuleVO();
						ruleMap.put(MN, vo);
					}

					switch (dataType) {
					case DataType.T2011:
						vo.getR2011().add(rule);
						break;
					case DataType.T2051:
						vo.getR2051().add(rule);
						break;
					case DataType.T2061:
						vo.getR2061().add(rule);
						break;
					case DataType.T2031:
						vo.getR2031().add(rule);
						break;
					}
					alarmRules.put(alarmId, rule);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initRuleAlarm(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化规则告警");
		logger.info("告警规则：" + JSON.toJSONString(ruleMap));

	}

	/**
	 * 
	 * initCustomAlarmRule:初始化自定义告警 void
	 */
	public void initCustomAlarmRule(final int loop) {

		try {
			// 初始化站点告警规则
			List<AlarmRuleVO> rules = alarmRuleService.queryCustomAlarmRule();

			if (rules != null && rules.size() > 0) {

				for (AlarmRuleVO rule : rules) {

					try {

						String polluteCode = "-1";
						String dataType = DataType.T2061;
						String formula = rule.getFormula();
						String pointCode = rule.getPointCode();
						String alarmCode = rule.getAlarmCode();
						String MN = rule.getMN();
						Integer almTypeId = rule.getAlmTypeId();

						RuleVO srules = ruleMap.get(MN);

						if (srules == null) {
							srules = new RuleVO();
							ruleMap.put(MN, srules);
						}

						List<String> cr = stationAlarm.get(MN);

						List<String> cr1 = stationAlarm.get(pointCode);

						if (cr == null) {

							cr = new ArrayList<String>();
							cr1 = new ArrayList<String>();
							stationAlarm.put(MN, cr);
							stationAlarm.put(pointCode, cr1);
						}

						// 站点上报数据标识告警规则
						if (30 == almTypeId) {

							cr.add(alarmCode);
							cr1.add(alarmCode);

							JSONArray array = JSON.parseArray(formula);
							for (Object o : array) {
								JSONObject oo = (JSONObject) o;
								if (oo != null) {

									JSONArray dataTypes = oo.getJSONArray("dataTypeList");
									String dataFlag = oo.getString("dataFlag");
									if (dataTypes != null && dataTypes.size() > 0) {

										for (Object type : dataTypes) {
											dataType = (String) type;

											if (!StringUtil.isEmptyOrNull(dataType)) {

												switch (dataType) {

												case "R":
													dataType = DataType.T2011;
													break;
												case "M":
													dataType = DataType.T2051;
													break;
												case "H":
													dataType = DataType.T2061;
													break;
												case "D":
													dataType = DataType.T2031;
													break;
												}
												FlagRuleVO r = new FlagRuleVO();

												r.setAlarmCode(alarmCode);
												r.setDataType(dataType);
												r.setFlagCode(dataFlag);
												r.setDurTime(0);

												flagAlarmRule.put(Md5Util.getMd5(dataFlag + dataType), r);
											}
										}
									}
								}
							}
							// }
						}

						// 初始化倒挂告警规则
						if (9 == almTypeId) {
							polluteCode = "-1";
							dataType = DataType.T2061;
							// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
							String ruleId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);
							AlarmRuleVO upsideDownRule = new AlarmRuleVO();
							upsideDownRule.setAlarmCode(alarmCode);
							upsideDownRule.setDataType(dataType);
							upsideDownRule.setDurTime(0);
							upsideDownRule.setMN(MN);
							upsideDownRule.setRuleId(ruleId);
							upsideDownRule.setPointCode(pointCode);
							upsideDownRule.setAlarmType("9");
							srules.getR2061().add(upsideDownRule);
						}

						// 数据波动异常或长期不变的告警采集规则
						if (10 == almTypeId && !StringUtil.isEmptyOrNull(formula)) {

							JSONArray array = JSON.parseArray(formula);
							for (Object o : array) {
								JSONObject oo = (JSONObject) o;
								if (oo != null) {

									JSONArray dataTypes = oo.getJSONArray("dataTypeList");
									polluteCode = oo.getString("polluteCode");
									String amplitude = oo.getString("amplitude");
									int timeLength = oo.getIntValue("timeLength");

									if (dataTypes != null && dataTypes.size() > 0) {

										for (Object type : dataTypes) {
											dataType = (String) type;

											if (!StringUtil.isEmptyOrNull(dataType)) {

												// 根据站点ID，告警码和因子ID，取md5 作为规则的ID
												String ruleId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode,
														polluteCode, dataType);
												AlarmRuleVO upsideDownRule = new AlarmRuleVO();
												upsideDownRule.setAlarmCode(alarmCode);
												upsideDownRule.setDurTime(timeLength);
												upsideDownRule.setFormula(amplitude);
												upsideDownRule.setPolluteCode(polluteCode);
												upsideDownRule.setMN(MN);
												upsideDownRule.setRuleId(ruleId);
												upsideDownRule.setPointCode(pointCode);

												switch (dataType) {
												case "R":
													dataType = DataType.T2011;
													upsideDownRule.setDataType(dataType);
													srules.getR2011().add(upsideDownRule);
													break;
												case "M":
													dataType = DataType.T2051;
													upsideDownRule.setDataType(dataType);
													srules.getR2051().add(upsideDownRule);
													break;
												case "H":
													dataType = DataType.T2061;
													upsideDownRule.setDataType(dataType);
													srules.getR2061().add(upsideDownRule);
													break;
												case "D":
													dataType = DataType.T2031;
													upsideDownRule.setDataType(dataType);
													srules.getR2031().add(upsideDownRule);
													break;
												}
											}
										}
									}
								}
							}
						}

						// 3020告警规则
						if (31 == almTypeId && !StringUtil.isEmptyOrNull(formula)) {

							cr.add(alarmCode);
							cr1.add(alarmCode);
							JSONArray array = JSON.parseArray(formula);
							for (Object o : array) {
								JSONObject oo = (JSONObject) o;
								if (oo != null) {
									String code = oo.getString("polluteCode");
									String status = oo.getString("status");
									T3020RuleVO r = new T3020RuleVO();
									r.setAlarmCode(alarmCode);
									r.setStatusCode(status);
									t3020AlarmRule.put(Md5Util.getMd5(code + status), r);
								}
							}
							// }
						}

						// aqi告警规则
						if (32 == almTypeId) {

							if (!StringUtil.isEmptyOrNull(formula)) {
								aqiAlarm = Integer.valueOf(formula);
							}
							cr.add(alarmCode);
							cr1.add(alarmCode);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initCustomAlarmRule(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

		logger.info("初始化自定义告警");
	}

	/**
	 * 
	 * cannelAlarm:消警处理 void
	 */
	public void cannelAlarm() {

		List<String> alarmCodes = Arrays.asList(new String[] { "10001", "90096", "85008", "85013", "90007" });

		for (AlarmInfoVO alarm : alarmMap.values()) {

			String alarmCode = alarm.getAlarmCode();
			String polluteCode = alarm.getPolluteCode();
			String pointCode = alarm.getPointCode();
			String dataType = alarm.getDataType();

			// 通用告警
			String alarmId = AlarmIDUtil.generatingAlarmID(pointCode, alarmCode, polluteCode, dataType);

			if (!alarmRules.containsKey(alarmId) && !alarmCodes.contains(alarmCode)) {
				alarm.setNewAlarm(false);
				alarm.setEndTime(DateUtil.getCurrentDate());
				String json = JSON.toJSONStringWithDateFormat(alarm, "yyyyMMddHHmmss",
						SerializerFeature.WriteDateUseDateFormat);
				publish.send(Constant.MQ_QUEUE_ALARM, json);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_ALARM, alarm));
				logger.info(String.format("告警消除:%s", json));
			}

		}
	}

	/**
	 * 
	 * initRivPolluteLevel:初始化水质三级评判标准，当有水质趋势预警配置时
	 * 
	 */
	public void initRivPolluteLevel(final int loop) {
		try {

			if (StringUtils.isNotBlank(cfgMap.get("rivEarlyWarn"))) {
				RivEarlyWarn.periodNum = Integer.valueOf(cfgMap.get("rivEarlyWarn"));
				// 初始水质等级
				List<PolluterLevelVo> pollutes = queryDataService.queryRivPolluteLevel();
				if (CollectionUtils.isNotEmpty(pollutes)) {
					for (PolluterLevelVo pollute : pollutes) {
						rivPolluteLevelMap.put(pollute.getPolluteCode(), pollute);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initRivPolluteLevel(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化水质三级评判标准");
	}

	/**
	 * 
	 * initRivPolluteLevel:初始化因子小时数据周期
	 * 
	 */
	public void initWaterHourPollutes(final int loop) {
		try {

			List<PolluteVO> polluteHhs = queryDataService.queryHourPollute();

			if (CollectionUtils.isNotEmpty(polluteHhs)) {
				for (PolluteVO polluteH : polluteHhs) {

					List<String> waterHourPollutes = new ArrayList<>();
					if (waterHourPolluteMaps.containsKey(polluteH.getHhcycletime())) {
						waterHourPollutes = waterHourPolluteMaps.get(polluteH.getHhcycletime());
					} else {
						waterHourPolluteMaps.put(polluteH.getHhcycletime(), waterHourPollutes);
					}
					waterHourPollutes.add(polluteH.getPolluteCode());
				}
			}

		} catch (Exception e) {
			logger.error("", e);
			service.schedule(new Runnable() {
				@Override
				public void run() {
					initWaterHourPollutes(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
		logger.info("初始化因子小时数据周期");
	}

	/**
	 * 
	 * initFlowFactor:初始化流量
	 * 
	 */
	public void initFlowFactor(final int loop) {

		try {
			List<PolluteVO> factors = stationService.queryFlowFactor();

			if (factors != null) {
				for (PolluteVO factor : factors) {

					if (factor != null) {

						switch (factor.getPolluteClass()) {
						// 流量因子
						case "PolluteClass12":
							if (!StringUtil.isEmptyOrNull(factor.getST())) {
								FlowFactorVO vo = new FlowFactorVO();
								vo.setPolluteCode(factor.getPolluteCode());
								flowFactor.put(factor.getST(), vo);
							}
							break;
						}
					}
				}
			}
			logger.info("初始化流量因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initFlowFactor(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}
}
