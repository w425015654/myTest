/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ConfigUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：初始化配置类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zeei.das.cgs.mq.Publish;
import com.zeei.das.cgs.service.ConfigService;
import com.zeei.das.cgs.vo.ChannelVO;
import com.zeei.das.cgs.vo.FormulaVo;
import com.zeei.das.cgs.vo.ParamCfgVO;
import com.zeei.das.cgs.vo.PointSystemVO;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：ConfigUtil 类 描 述：初始化服务配置类 功能描述：主要初始化站点配置，因子编码配置 创建作者：quanhongsheng
 */

@Service
public class CgsService {

	private static Logger logger = LoggerFactory.getLogger(CgsService.class);
	// 站点配置信息
	public static Map<String, StationCfgVO> stationMap = new ConcurrentHashMap<String, StationCfgVO>();
	// 因子配置信息
	public static Map<String, String> paramMap = new ConcurrentHashMap<String, String>();
	// 连接通道配置信息
	public static Map<String, ChannelVO> channelMap = new ConcurrentHashMap<String, ChannelVO>();

	// 系统配置信息
	public static Map<String, String> cfgMap = new ConcurrentHashMap<String, String>();

	// 原始数据解析相关公式配置
	public static Map<String, List<FormulaVo>> formulaMap = new ConcurrentHashMap<>();

	// 系统配置信息
	public static Map<String, PointSystemVO> pointSystemMap = new ConcurrentHashMap<String, PointSystemVO>();

	@Autowired
	ConfigService configService;

	@Autowired
	Publish publish;

	static ScheduledExecutorService service = Executors.newScheduledThreadPool(3);

	static {
		// 读取配置
		cfgMap = LoadConfigUtil.readXmlParam("cgs");
	}

	@PostConstruct
	public void initConfig() {
		try {

			initStationCfg(1);
			initPointSystemMap(1);
			initParamMap(1);
			initFormulaMap(1);

		} catch (Exception e) {
			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e));
		}
	}

	/**
	 * 
	 * initStationCfg:初始化站点配置文件 void
	 * 
	 * @param loop
	 *            异常循环次数
	 */
	public void initStationCfg(final int loop) {
		// 初始化站点配置
		try {
			stationMap.clear();
			List<StationCfgVO> stations = new ArrayList<StationCfgVO>();
			stations = configService.queryStationCfg();
			if (stations != null && stations.size() > 0) {
				for (StationCfgVO station : stations) {
					String MN = station.getMN();
					if (!StringUtil.isEmptyOrNull(MN)) {
						String[] mnStrs = MN.split(",");
						// 新mn为多站点逗号分割
						for (String mnStr : mnStrs) {
							if (!StringUtil.isEmptyOrNull(mnStr)) {
								stationMap.put(mnStr, station);
							}
						}
					}
				}
			}
			logger.info(JSON.toJSONString(stationMap));
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
	 * initParamMap:初始化因子关系 void
	 * 
	 * @param loop
	 *            异常循环次数
	 */
	public void initParamMap(final int loop) {

		try {
			paramMap.clear();
			// 初始化因子转码信息
			List<ParamCfgVO> params = configService.queryParamCfg();
			if (params != null && params.size() > 0) {
				for (ParamCfgVO param : params) {
					String paramId = param.getOldCode();
					if (!StringUtil.isEmptyOrNull(paramId)) {
						paramMap.put(paramId, param.getNewCode());
					}
				}
			}
			logger.info("初始化因子完成");
		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initParamMap(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

	}

	/**
	 * 
	 * initParamMap:初始化系统配置信息
	 * 
	 * @param loop
	 *            异常循环次数
	 */
	public void initPointSystemMap(final int loop) {

		try {
			pointSystemMap.clear();
			// 初始化因子转码信息
			List<PointSystemVO> params = configService.queryPointSystem();
			if (params != null && params.size() > 0) {
				for (PointSystemVO param : params) {
					String ST = param.getST();
					if (!StringUtil.isEmptyOrNull(ST)) {
						pointSystemMap.put(ST, param);
					}
				}
			}

			logger.info("初始化系统配置信息完成");

		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initPointSystemMap(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

	}

	/**
	 * initFormulaMap:初始化站点计算公式配置
	 * 
	 * @param loop
	 *            异常循环次数
	 */
	public void initFormulaMap(final int loop) {

		try {
			formulaMap.clear();
			// 初始化站点计算公式配置
			List<FormulaVo> formulas = configService.queryFormulaInfo();

			formulaMap = formulas.stream().collect(Collectors.groupingBy(FormulaVo::getPointCode));

			logger.info("初始化站点计算公式配置信息完成");

		} catch (Exception e) {

			logger.error("", e);

			service.schedule(new Runnable() {
				@Override
				public void run() {
					initFormulaMap(loop + 1);
				}
			}, 10 * loop, TimeUnit.SECONDS);
		}

	}

}
