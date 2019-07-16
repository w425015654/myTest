package com.zeei.das.dps;
/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：DpsService.java
* 包  名  称：
* 文件描述：入库服务初始化类
* 创建日期：2017年4月27日下午12:17:10
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日下午12:17:10 创建文件
*
*/

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.mq.Publish;
import com.zeei.das.dps.service.CfgServiceImpl;
import com.zeei.das.dps.vo.AuditRuleVO;
import com.zeei.das.dps.vo.DataFlagAuditVO;
import com.zeei.das.dps.vo.DoubtfulVo;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PointSystemVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.RuleParamVO;
import com.zeei.das.dps.vo.ZeroNegativeVO;

/**
 * 类 名 称：DpsService 类 描 述：入库服务初始化类 功能描述：入库服务初始化类 创建作者：quanhongsheng
 */
@Component
public class DpsService {

	@Autowired
	CfgServiceImpl cfgService;

	@Autowired
	Publish publish;

	// 站点配置信息
	public static Map<String, PointSiteVO> stationCfgMap = new ConcurrentHashMap<String, PointSiteVO>();

	// 系统基本参数
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	// 自动审核规则
	public static Map<String, AuditRuleVO> auditRuleMap = new ConcurrentHashMap<String, AuditRuleVO>();

	// 系统配置信息
	public static Map<String, PointSystemVO> tableNameMap = new ConcurrentHashMap<String, PointSystemVO>();

	// 空气的 站点下的 存储 数据值持续不变开始时间和 一直不变的那个值
	public static Map<String, DoubtfulVo> doubtfulMap = new ConcurrentHashMap<String, DoubtfulVo>();

	// 地表水的 站点下的 存储 数据值持续不变开始时间和 一直不变的那个值
	public static Map<String, DoubtfulVo> rivCfMap = new ConcurrentHashMap<String, DoubtfulVo>();

	// 系统污染物编码信息
	public static Map<String, PolluteVO> polluteMap = new ConcurrentHashMap<String, PolluteVO>();

	public static List<String> systemTable = new ArrayList<String>();

	// 是否统计日AQI
	public static Integer isAQID = 1;

	// 是否统计小时AQI
	public static Integer isAQIH = 1;

	// 是否进行自动补数
	public static String complement = "0";

	// 是否啟用异常和停产时段，数据无效修约
	public static int isTC = 1;

	@Autowired
	SqlSessionFactory sqlSessionFactory;

	static {
		// 读取配置
		cfgMap = LoadConfigUtil.readXmlParam("dps");

		if (!StringUtil.isEmptyOrNull(cfgMap.get("isAQID"))) {
			isAQID = Integer.valueOf(cfgMap.get("isAQID"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("isAQIH"))) {
			isAQIH = Integer.valueOf(cfgMap.get("isAQIH"));
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("complement"))) {
			complement = cfgMap.get("complement");
		}
		if (!StringUtil.isEmptyOrNull(cfgMap.get("isTC"))) {
			isTC = Integer.valueOf(cfgMap.get("isTC"));
		}
	}

	private static Logger logger = LoggerFactory.getLogger(DpsService.class);

	ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

	@PostConstruct
	public void initConfig() {
		try {
			initStationCfg(1);
			initAuditCfg(1);
			initTables(1);
			initSystemTableName(1);
			initPolluteCfg(1);

		} catch (Exception e) {
			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
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
			List<PointSystemVO> tables = cfgService.queryPointSystem();
			if (tables != null && tables.size() > 0) {
				for (PointSystemVO table : tables) {
					if (table != null && !StringUtil.isEmptyOrNull(table.getSystemType())) {
						tableNameMap.put(table.getSystemType(), table);
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
	 * initStationCfg:初始化站点配置文件 void
	 */
	public void initStationCfg(final int loop) {
		// 初始化站点配置
		try {
			stationCfgMap.clear();

			List<PointSiteVO> stations = cfgService.queryStation();
			if (stations != null && stations.size() > 0) {
				for (PointSiteVO station : stations) {
					if (!StringUtil.isEmptyOrNull(station.getMn())) {
						// 分割站点mn
						String[] mnStrs = station.getMn().split(",");
						// 新mn为多站点逗号分割
						for (String mnStr : mnStrs) {
							if (StringUtils.isNotBlank(mnStr)) {
								stationCfgMap.put(mnStr, station);
							}
						}
					}
				}
			}
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
	 * initPolluteCfg:初始化污染物编码配置文件 void
	 */
	public void initPolluteCfg(final int loop) {
		// 初始化站点配置
		try {
			polluteMap.clear();
			List<PolluteVO> pollutes = cfgService.queryPollute();
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

			systemTable = cfgService.querySystemTables(tableSchema);

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
	 * 
	 * initAuditCfg:初始化自动审核规则信息 void
	 */
	public void initAuditCfg(final int loop) {

		try {
			// 初始化站点配置
			List<ZeroNegativeVO> rules = cfgService.queryZeroAuditRule();

			auditRuleMap.clear();

			if (rules != null && rules.size() > 0) {
				for (ZeroNegativeVO rule : rules) {

					String dataType = DataType.HOURDATA;
					String MN = rule.getMN();
					String ruleCode = rule.getRuleCode();
					String polluteCode = rule.getPolluteCode();

					String pointName = "";

					if (StringUtil.isEmptyOrNull(MN)) {
						continue;
					}

					AuditRuleVO vo = auditRuleMap.get(MN);

					if (vo == null) {
						vo = new AuditRuleVO();
						auditRuleMap.put(MN, vo);
					}

					PointSiteVO station = stationCfgMap.get(MN);

					if (station != null) {
						pointName = station.getPointName();
					}

					RuleParamVO paramVO = new RuleParamVO();
					paramVO.setDataType(dataType);
					paramVO.setParameter(rule);
					paramVO.setPolluteCode(polluteCode);
					paramVO.setRuleCode(ruleCode);
					paramVO.setPointCode(rule.getPointCode());
					paramVO.setPointMN(MN);
					paramVO.setPointName(pointName);

					if (StringUtil.isEmptyOrNull(dataType)) {
						continue;
					}

					/*switch (dataType) {
					case DataType.RTDATA:
						vo.getR2011().add(paramVO);
						break;
					case DataType.MINUTEDATA:
						vo.getR2051().add(paramVO);
						break;
					case DataType.HOURDATA:
						vo.getR2061().add(paramVO);
						break;
					case DataType.DAYDATA:
						vo.getR2031().add(paramVO);
						break;
					}*/
					
					vo.getR2011().add(paramVO);
					vo.getR2051().add(paramVO);
					vo.getR2061().add(paramVO);
					vo.getR2031().add(paramVO);
				}
			}

			List<DataFlagAuditVO> rules1 = cfgService.queryDataFlagAuditRule();

			// 数据标识修约
			if (rules1 != null && rules1.size() > 0) {
				for (DataFlagAuditVO rule : rules1) {

					String dataType = rule.getDataType();
					String MN = rule.getMN();
					String ruleCode = rule.getRuleCode();

					if (StringUtil.isEmptyOrNull(MN)) {
						continue;
					}

					AuditRuleVO vo = auditRuleMap.get(MN);

					if (vo == null) {
						vo = new AuditRuleVO();
						auditRuleMap.put(MN, vo);
					}
					String pointName = "";
					PointSiteVO station = stationCfgMap.get(MN);

					if (station != null) {
						pointName = station.getPointName();
					}

					RuleParamVO paramVO = new RuleParamVO();
					paramVO.setDataType(dataType);
					paramVO.setParameter(rule);
					paramVO.setRuleCode(ruleCode);
					paramVO.setPointCode(rule.getPointCode());
					paramVO.setPointMN(MN);
					paramVO.setPointName(pointName);
					if (StringUtil.isEmptyOrNull(dataType)) {
						continue;
					}

					switch (dataType) {
					case "1":
						vo.getR2011().add(paramVO);
						break;
					case "2":
						vo.getR2051().add(paramVO);
						break;
					case "3":
						vo.getR2061().add(paramVO);
						break;
					case "4":
						vo.getR2031().add(paramVO);
						break;
					}
				}
			}
			logger.info("初始化修约规则信息完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initAuditCfg(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}
	}
}
