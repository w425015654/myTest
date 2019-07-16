/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：lts
* 文件名称：LtsService.java
* 包  名  称：com.zeei.das.lts
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月8日上午8:06:47
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月8日上午8:06:47 创建文件
*
*/

package com.zeei.das.dss;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.DataType;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.mq.Publish;
import com.zeei.das.dss.service.AlarmService;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.service.StationService;
import com.zeei.das.dss.statistics.StatisticalHelper;
import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.O38stdVO;
import com.zeei.das.dss.vo.PeriodVo;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.SitePolluterVo;
import com.zeei.das.dss.vo.StationVO;
import com.zeei.das.dss.vo.SystemTableVO;

/**
 * 类 名 称：LtsService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class DssService {

	@Autowired
	StationService stationService;

	@Autowired
	SqlSessionFactory sqlSessionFactory;

	public static Logger logger = LoggerFactory.getLogger(DssService.class);

	// 系统配置信息
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	// 离群值对应因子计算标准值
	public static Map<String, Double> polluteMaxMap = new ConcurrentHashMap<String, Double>();

	// 系统配置信息
	public static Map<String, StationVO> stationMap = new ConcurrentHashMap<String, StationVO>();

	// 系统配置信息
	public static Map<String, PolluteVO> airSixParam = new ConcurrentHashMap<String, PolluteVO>();

	// 系统配置信息
	public static Map<String, SystemTableVO> tableNameMap = new ConcurrentHashMap<String, SystemTableVO>();

	// 排放量统计因子
	public static List<String> emissionFactor = new ArrayList<String>();

	public static Map<String, String> flowFactor = new HashMap<String, String>();

	// 虚拟因子
	public static List<String> vFactor = new ArrayList<String>();

	// 站点数据上传有效因子（过滤掉了站点配置不参与统计的因子ISSTAT！=1）
	public static Map<String, Set<String>> validFactor = new HashMap<String, Set<String>>();

	// 各个统计类型对应周期
	public static List<PeriodVo> periodList = new ArrayList<>();

	// O38标准浓度限值
	public static Map<String, Double> O38 = new HashMap<String, Double>();

	// 系统中所有表集
	public static List<String> systemTable = new ArrayList<String>();

	// 重污染时长统计MAP
	public static Map<String, AirHvyDayVo> polluteTimeMap = new ConcurrentHashMap<>();

	// 分钟数据AQI超标污染事件
	public static Map<String, PollIncidentVo> pollIncidentMap = new HashMap<>();

	// 是否统计小时AQI离群标识
	public static String ISLQ = "0";

	// 数据复核时长
	public static Integer reviewTime = 0;

	// 数据审核时长
	public static Integer auditTime = 0;
	// 自动审核 工况
	public static List<String> STSL = new ArrayList<>();
	// 工况的审核
	public static String STLs = "51,52";
	// 是否复核，1为是
	public static String autoReview = "0";
	// 是否审核，1为是
	public static String autoAudit = "0";
	
	// 是否要进行排量统计
	public static String isCou = "1";
		
	// 是否要进行流量统计
	public static List<String> emissionT212 = new ArrayList<String>();
	
	// 过滤掉虚拟因子数据,Cou因子除外
	public static List<String> vFactors = new ArrayList<String>();
	// 地表水小时数据的因子及周期
	public static Map<String, Map<String, List<SitePolluterVo>>> waterHourPollutes = new HashMap<>();

	// 是否统计区域aqi
	public static List<String> aqiExcludeAreaLevel = new ArrayList<String>();

	@Autowired
	Publish publish;

	@Autowired
	QueryDataService queryDataService;

	@Autowired
	AlarmService alarmService;

	static {
		// 读取配置
		cfgMap = LoadConfigUtil.readXmlParam("dss");
	}

	ScheduledExecutorService service = Executors.newScheduledThreadPool(7);

	@PostConstruct
	// 初始化服务配置信息
	public void initConfig() {
		try {

			initStationCfg(1);
			initSystemTableName(1);
			initAirSixParam(1);
			initEmissionFactor(1);
			initO38(1);
			initVFactor(1);
			initvalid(1);
			initPolluteMax(1);
			initTables(1);
			initPolluteTime(1);
			initPollutionIncident(1);
			// 这个必须放最后一个
			initStatis();

		} catch (Exception e) {

			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	/**
	 * @Title: initvalid @Description: (初始化站点对应所有虚拟因子) @param 参数 @return void
	 *         返回类型 @throws
	 */

	public void initvalid(final int loop) {

		try {
			// 污染因子
			List<SitePolluterVo> validPolls = queryDataService.queryValidPoll();

			waterHourPollutes.clear();
			validFactor.clear();

			if (validPolls != null) {

				waterHourPollutes = validPolls.stream().collect(Collectors.groupingBy(SitePolluterVo::getPointCode,
						Collectors.groupingBy(SitePolluterVo::getPolluteCode)));

				for (SitePolluterVo vo : validPolls) {

					String ponitCode = vo.getPointCode();

					String polluteCode = vo.getPolluteCode();

					if (!StringUtil.isEmptyOrNull(ponitCode) && vo.getIsstat() != null && vo.getIsstat() == 1) {

						Set<String> pollutes = validFactor.get(ponitCode);

						if (pollutes == null) {
							pollutes = new HashSet<String>();
							validFactor.put(ponitCode, pollutes);
						}
						if (!StringUtil.isEmptyOrNull(polluteCode) && vFactor != null
								&& !vFactor.contains(polluteCode)) {
							pollutes.add(polluteCode);
						}
					}

				}

			}
			logger.info("初始化审核因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initvalid(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 初始化虚拟因子
	 */
	public void initVFactor(final int loop) {

		try {
			// 查询O38小时的标准限值
			vFactor.clear();
			vFactor = queryDataService.queryVPollute();
			logger.info("初始化虚拟因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initVFactor(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * initO38:查询O38小时的标准限值 void
	 */

	public void initO38(final int loop) {

		try {

			O38.clear();

			// 查询O38小时的标准限值
			List<O38stdVO> list = alarmService.queryO38std();
			if (list != null && list.size() > 0) {
				for (O38stdVO o : list) {
					O38.put(o.getPointCode() + o.getPolluteCode(), o.getStdValue());
				}
			}

			logger.info("初始化O38小时的标准限值");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initO38(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * initStationCfg:初始化站点配置文件 void
	 */

	public void initStationCfg(final int loop) {

		try {
			stationMap.clear();
			// 初始化站点配置
			List<StationVO> stations = stationService.queryStationCfg();
			if (stations != null && stations.size() > 0) {
				for (StationVO station : stations) {
					if (!StringUtil.isEmptyOrNull(station.getMN())) {
						// 分割站点mn
						String[] mnStrs = station.getMN().split(",");
						// 新mn为多站点逗号分割
						for (String mnStr : mnStrs) {
							if (StringUtils.isNotBlank(mnStr)) {
								stationMap.put(mnStr, station);
							}
						}
					}
				}
			}

			initPeriod();

			logger.info("初始化站点信息完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initStationCfg(loop + 1);
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
			tableNameMap.clear();
			// 初始化站点配置
			List<SystemTableVO> tables = stationService.queryTableName();
			if (tables != null && tables.size() > 0) {
				for (SystemTableVO table : tables) {
					if (!StringUtil.isEmptyOrNull(table.getST())) {
						tableNameMap.put(table.getST(), table);
					}
				}
			}
			logger.info("初始化系統类型信息完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initSystemTableName(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * initParam:初始化因子配置
	 * 
	 */
	public void initAirSixParam(final int loop) {
		try {

			airSixParam.clear();
			List<PolluteVO> pollutes = queryDataService.queryPollute();
			if (pollutes != null && pollutes.size() > 0) {
				for (PolluteVO pollute : pollutes) {

					if (!StringUtil.isEmptyOrNull(pollute.getPolluteCode())) {
						airSixParam.put(pollute.getPolluteCode(), pollute);
					}
				}
			}
			logger.info("初始化空气评价因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initAirSixParam(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 
	 * initEmissionFactor:初始化排放量
	 * 
	 */
	public void initEmissionFactor(final int loop) {

		try {
			List<PolluteVO> factors = stationService.queryEmissionFactor();

			if (factors != null) {
				for (PolluteVO factor : factors) {

					if (factor != null) {

						switch (factor.getPolluteClass()) {
						// 污染物排放量统计因子
						case "PolluteClass11":
							emissionFactor.add(factor.getPolluteCode());
							break;
						// 流量因子
						case "PolluteClass12":
							if (!StringUtil.isEmptyOrNull(factor.getST())) {
								flowFactor.put(factor.getST(), factor.getPolluteCode());
							}
							break;
						}
					}
				}
			}
			logger.info("初始化排放量因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initEmissionFactor(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * @Title: initPeriod @Description: (初始化时间周期map) @param 参数 @return void
	 *         返回类型 @throws
	 */

	public void initPeriod() {

		periodList.clear();

		String[] strs = { DataType.MONTHDATA, DataType.HOURDATA, DataType.DAYDATA, DataType.MONTHDATA };
		for (Entry<String, StationVO> entry : stationMap.entrySet()) {
			// 封装的周期数据
			PeriodVo period = new PeriodVo();

			for (int i = 0; i < strs.length; i++) {

				Date startDate = StatisticalHelper.getCurrIntegralTime(strs[i]);
				String dateStr = StatisticalHelper.getEndTime(entry.getValue(), strs[i], startDate);
				Date endDate = DateUtil.strToDate(dateStr, "yyyy-MM-dd HH:mm:ss");
				period.setStation(entry.getValue());
				period.setEndTime(endDate);
				period.setBeginTime(startDate);
				period.setCN(strs[i]);

				periodList.add(period);
			}

		}

	}

	/**
	 * 
	 * initTables:初始化获取系统所有的table名称 void
	 */
	public void initTables(final int loop) {

		try {

			systemTable.clear();

			SqlSession session = sqlSessionFactory.openSession();

			String tableSchema = null;
			try {
				tableSchema = session.getConnection().getCatalog();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			systemTable = stationService.querySystemTables(tableSchema);

			logger.info("初始化系統表信息完成");

		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initTables(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 初始化因子对应的离群最大限值
	 */
	public void initPolluteMax(final int loop) {

		try {
			// 离群值的二级标准限值
			polluteMaxMap.clear();
			List<PolluteVO> maxPollValues = queryDataService.queryMaxPollValue();

			for (PolluteVO maxPollValue : maxPollValues) {
				// 加入一日数据限值
				if ("timeType03".equals(maxPollValue.getPolluteClass())) {

					polluteMaxMap.put(maxPollValue.getPolluteCode(), maxPollValue.getSmaxValue());
				} else {
					// 没有一日数据限值，就用24小时数据
					if (!polluteMaxMap.containsKey(maxPollValue.getPolluteCode())) {

						polluteMaxMap.put(maxPollValue.getPolluteCode(), maxPollValue.getSmaxValue());
					}
				}
			}

			logger.info("初始化因子离群最大限值完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPolluteMax(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 初始化重污染时间未结束的数据
	 */
	public void initPolluteTime(final int loop) {

		try {
			// 重污染时间
			polluteTimeMap.clear();
			List<AirHvyDayVo> polluteTimes = queryDataService.queryAirHvyDays();

			for (AirHvyDayVo polluteTime : polluteTimes) {
				if (StringUtils.isNotEmpty(polluteTime.getPollNameList())) {
					String[] strs = polluteTime.getPollNameList().split(",");
					Set<String> set2 = new HashSet<>();
					Collections.addAll(set2, strs);
					polluteTime.setPollutes(set2);
				}
				polluteTimeMap.put(polluteTime.getCode().trim(), polluteTime);
			}
			String end = DateUtil.getCurrentDate("yyyy-MM-dd");
			String endTime = end + " 00:00:00";
			String beginTime = DateUtil.dateToStr(DateUtil.dateAddDay(DateUtil.getCurrentDate(), -3), "yyyy-MM-dd")
					+ " 00:00:00";
			List<AqiDataVO> nowHvys = queryDataService.queryNowHvys(beginTime, endTime);

			String date = null;
			// 处理数据，获取当前连续超标数据
			for (AqiDataVO nowHvy : nowHvys) {
				String key = nowHvy.getCode().trim();
				// 不是已存在的告警
				if (!polluteTimeMap.containsKey(key)) {

					if (StringUtils.isEmpty(nowHvy.getDataTime())) {
						date = beginTime;
					} else if (nowHvy.getDeDataTime().equals(DateUtil.strToDate(endTime, "yyyy-MM-dd HH:mm:ss"))) {
						// 时间等于结束时间，则是没有超标
						continue;
					} else {
						date = DateUtil.dateToStr(DateUtil.dateAddDay(nowHvy.getDeDataTime(), 1),
								"yyyy-MM-dd HH:mm:ss");
					}
					AirHvyDayVo airHvyDay = new AirHvyDayVo();
					airHvyDay.setCode(key);
					airHvyDay.setDlevel(nowHvy.getLevel());
					airHvyDay.setEdate(end);
					airHvyDay.setSdate(date);
					polluteTimeMap.put(key, airHvyDay);
				}
			}

			logger.info("初始化重污染时间未结束的数据完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPolluteTime(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * 分钟数据AQI超标污染事件
	 */
	public void initPollutionIncident(final int loop) {

		try {
			// 重污染事件
			pollIncidentMap.clear();
			if ("1".equals(cfgMap.get("ISPI"))) {
				// 需要统计相关数据
				List<PollIncidentVo> pollTimes = queryDataService.queryPollIncident();
				for (PollIncidentVo pollTime : pollTimes) {
					pollIncidentMap.put(String.valueOf(pollTime.getPointCode()), pollTime);
				}
			}

			logger.info("初始化分钟数据AQI超标污染事件的数据完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPollutionIncident(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}

	/**
	 * initStatis:对于使用到dss缓存数据的数据初始化，需要放在这里，
	 * 放在静态代码块中，可能导致比dssService提前执行，获取不到缓存中的值。 void
	 */
	public void initStatis() {

		if (!StringUtil.isEmptyOrNull(cfgMap.get("ISLQ"))) {
			ISLQ = cfgMap.get("ISLQ");
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("reviewTime"))) {
			reviewTime = Integer.valueOf(cfgMap.get("reviewTime"));
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("auditTime"))) {
			auditTime = Integer.valueOf(cfgMap.get("auditTime"));
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("STS"))) {
			STSL = Arrays.asList(cfgMap.get("STS").split(","));
		} else {
			// 默认值设置22
			STSL.add("22");
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("STL"))) {
			STLs = cfgMap.get("STL");
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("autoReview"))) {
			autoReview = cfgMap.get("autoReview");
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("autoAudit"))) {
			autoAudit = cfgMap.get("autoAudit");
		}

		if (cfgMap.containsKey("isCou")) {
			isCou = cfgMap.get("isCou");
		}

		if (!StringUtil.isEmptyOrNull(cfgMap.get("isEmission"))) {
			emissionT212 = Arrays.asList(cfgMap.get("isEmission").split(","));
		}

		vFactors = vFactor.stream().filter(o -> !o.endsWith("Cou")).collect(Collectors.toList());

		if (!StringUtil.isEmptyOrNull(cfgMap.get("aqiExcludeAreaLevel"))) {
			String aqiExcludeAreaLevels = cfgMap.get("aqiExcludeAreaLevel");

			if (!StringUtil.isEmptyOrNull(aqiExcludeAreaLevels)) {
				aqiExcludeAreaLevel = Arrays.asList(aqiExcludeAreaLevels.split(","));
			}
		}

	}

}
