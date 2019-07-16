/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ParseT212.java
* 包  名  称：com.zeei.das.cgs.common.T212
* 文件描述：T212 消息处理
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.T212.ParseCP.AvgSpeed;
import com.zeei.das.cgs.mq.Publish;
import com.zeei.das.cgs.service.ConfigService;
import com.zeei.das.cgs.vo.ChannelVO;
import com.zeei.das.cgs.vo.FormulaVo;
import com.zeei.das.cgs.vo.PointSystemVO;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.T212Code;
import com.zeei.das.common.utils.CrcUtil;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;

import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：T212 类 描 述：T212消息处理 功能描述：T212消息验证，站点配置更新 创建作者：quanhongsheng
 */

@Component("t212")
public class T212 {

	private static Logger logger = LoggerFactory.getLogger(T212.class);

	@Autowired
	ConfigService configService;
	@Autowired
	ParseT212 parseT212;
	@Autowired
	Publish publish;
	@Autowired
	Station stationUtil;

	// 是否进行长度验证
	static int isLen = 1;
	// 是否进行CRC验证
	static int isCrc = 1;
	// 补传天数
	static Long passbackDays = 0L;
	// 轮侧因子
	static List<String> lcPollutes = new ArrayList<String>();
	// 步长
	static int step = 10;
	static double range = 0;
	static String pollCodes = null;

	static {

		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("isLen"))) {
			isLen = Integer.valueOf(CgsService.cfgMap.get("isLen"));
		}

		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("isCrc"))) {
			isCrc = Integer.valueOf(CgsService.cfgMap.get("isCrc"));
		}

		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("passbackDays"))) {
			passbackDays = Long.valueOf(CgsService.cfgMap.get("passbackDays"));
		}

		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("lcPollutes"))) {
			lcPollutes = Arrays.asList(CgsService.cfgMap.get("lcPollutes").split(","));
		} else {
			// cod：w01018;總磷：w21011，氨氮：w21003，总氮：w21001
			lcPollutes = Arrays.asList(new String[] { "w01018", "w21003", "w21011", "w21001"});
		}

		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("step"))) {
			step = Integer.valueOf(CgsService.cfgMap.get("step"));
		}
		
		if (!StringUtil.isNullOrEmpty(CgsService.cfgMap.get("pollCodes"))) {
			pollCodes = CgsService.cfgMap.get("pollCodes");
		}

		if ((pollCodes != null) && (step > 0)) {
			AvgSpeed.init(step, pollCodes);
		}
	};

	/**
	 * 
	 * onT212Msg:netty 处理类直接调用消息处理函数
	 *
	 * @param msg
	 *            void
	 */
	public void onT212Msg(String msg, Channel channel) {
		try {
			
			logger.info(msg);

			publish.send(Constant.MQ_QUEUE_TT212, msg);

			String regT212Pkg = "##([0-9a-fA-F]{4})(.*)([0-9a-fA-F]{4})";

			Pattern pattern = Pattern.compile(regT212Pkg);
			Matcher matcher = pattern.matcher(msg);
			int len = matcher.groupCount();

			if (matcher.find() && len == 3) {

				int msgLen = Integer.valueOf(matcher.group(1));
				String msgBody = matcher.group(2);
				String msgCRC = matcher.group(3);
				
				int calcLen = msgBody.getBytes("GB2312").length ;

				if (msgLen != calcLen && isLen == 1) {

					String error = String.format("消息长度(%s,%s)不正确！[%s]", msgLen, calcLen, msg);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
					logger.warn(error);
					publish.send("TE212", msg);
					return;
				}

				String crc = CrcUtil.Crc16Calc(msgBody);

				if (!msgCRC.equalsIgnoreCase(crc) && isCrc == 1) {

					String error = String.format("CRC不正确！(%s - %s)[%s]", msgCRC, crc, msg);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
					logger.warn(error);
					publish.send(Constant.MQ_QUEUE_TE212, msg);
					return;
				}

				JSONObject msgObj = parseT212.parseT212Msg(msgBody, channel);

				if (msgObj != null) {

					// 判断是否为补传数据,同时补传时间是否在规定的有效时间范围内
					String CN = (String) msgObj.get("CN");
					String MN = (String) msgObj.get("MN");

					// 验证通过数据进一步入库操作等处理
					if (StringUtil.isNullOrEmpty(CN)) {
						return;
					}

					JSONObject cp = msgObj.getJSONObject("CP");

					if (cp != null) {

						Object DT = cp.get("DT");

						// 对数据进行公式计算，得到新的统计因子
						StationCfgVO stationCfg = CgsService.stationMap.get(MN);

						Date DataTime = cp.getDate("DataTime");

						// 只有监测数据才进行，补数限制及公式计算
						if (T212Code.T2011.equalsIgnoreCase(CN) || T212Code.T2051.equalsIgnoreCase(CN)
								|| T212Code.T2061.equalsIgnoreCase(CN) || T212Code.T2031.equalsIgnoreCase(CN)) {

							JSONArray params = cp.getJSONArray("Params");
							if (DataTime == null || params == null || params.size() < 1) {
								return;
							}

							// 判断是否为补传数据,同时补传时间是否在规定的有效时间范围内
							Long diffDay = 0l;

							if (DataTime != null) {
								diffDay = DateUtil.dateDiffDay(DataTime, new Date());
							}

							if (passbackDays < diffDay && passbackDays > 0) {
								String error = String.format("补传数据超过有效天数！有效天数为%s天,实际天数为%s[%s]", passbackDays, diffDay,
										msg);
								publish.send(Constant.MQ_QUEUE_LOGS,
										LoggerUtil.getLogInfo(LogType.LOG_TYPE_PASSBACK, error));
								logger.warn(error);
								return;
							}

							List<FormulaVo> formulas = stationCfg == null ? null
									: CgsService.formulaMap.get(stationCfg.getPointCode());
                            
							
							if (CollectionUtils.isNotEmpty(formulas)) {
								params = FormulaComputing.dataParser(CN, formulas, params);
								cp.put("Params", params);
							}
							
							// 中天钢流量/流速因子平滑处理
							ztgSmooth(MN, CN, DataTime, params);
							
						}

						// 针对洪泽项目进行定制，将采用时间作为轮侧因子的监测时间，将运来一个数据包拆成2个数据包进行发送
						if (DT != null && !StringUtil.isNullOrEmpty(String.valueOf(DT))) {
							ParseHZ(msgObj, stationCfg);
						} else {

							String json = JSON.toJSONStringWithDateFormat(msgObj, "yyyy-MM-dd HH:mm:ss",
									SerializerFeature.WriteDateUseDateFormat);

							logger.info(json);
							publish.send(CN, json);
							// 发送最新数据
							if ((T212Code.T2011.equalsIgnoreCase(CN) || T212Code.T2051.equalsIgnoreCase(CN)
									|| T212Code.T2061.equalsIgnoreCase(CN) || T212Code.T2031.equalsIgnoreCase(CN))) {

								Date cDataTime = stationCfg.getDataTime();

								JSONArray params = cp.getJSONArray("Params");
								if (DataTime == null || params == null || params.size() < 1) {
									return;
								}
								if (cDataTime == null) {
									cDataTime = DataTime;
								}
								if (DataTime.getTime() >= cDataTime.getTime()) {
									publish.publishTL(CN, json);
								}
							}
						}
					}

				}
			}

		} catch (Exception e) {
			logger.error("", e);
			logger.error(msg);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e));
			publish.send(Constant.MQ_QUEUE_TE212, msg);
		}
	}

	/**
	 * 
	 * onNewConnection:新建建立，初始化连接信息
	 *
	 * @param msg
	 *            接收消息
	 * @param channel
	 *            连接通道
	 * 
	 */
	public void onNewConnection(String msg, Channel channel) {

		String channelId = channel.id().asShortText();
		String MN = stationUtil.getMN(msg);
		if (!StringUtil.isNullOrEmpty(MN)) {

			StationCfgVO station = null;
			// 初始化站点配置
			if (CgsService.stationMap.containsKey(MN)) {
				station = CgsService.stationMap.get(MN);

				if (station == null) {
					station = new StationCfgVO();
				}
				station.setMN(MN);
			} else {
				station = configService.getStationCfg(MN);

				if (station == null) {

					String ST = stationUtil.getST(msg);
					String table = "";

					if (!StringUtil.isNullOrEmpty(ST)) {

						PointSystemVO sys = CgsService.pointSystemMap.get(ST);

						if (sys != null && !StringUtil.isNullOrEmpty(sys.getTableName())) {
							table = sys.getTableName();

							configService.insertSite(table, MN, ST);
							station = configService.getStationCfg(MN);
						}

					}
					if (station == null) {
						station = new StationCfgVO();
					}
				}
				station.setMN(MN);
				CgsService.stationMap.put(station.getMN(), station);
			}

			ChannelVO channelVO = new ChannelVO();
			channelVO.setChannel(channel);
			channelVO.setMN(MN);
			channelVO.setID(station.getID());
			CgsService.channelMap.put(channelId, channelVO);
			// 连接建立，首次收到数据时进行站点校时
			stationUtil.calibrationTime(MN, channel);
			// 消息处理
			onT212Msg(msg, channel);

		} else {
			publish.send(Constant.MQ_QUEUE_TE212, msg);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, msg));
		}
	}

	/**
	 * 洪泽定制，发送轮侧因子
	 * 
	 * @param msgObj
	 * @param station
	 */
	private void ParseHZ(JSONObject msgObj, StationCfgVO station) {

		// 判断是否为补传数据,同时补传时间是否在规定的有效时间范围内
		String CN = (String) msgObj.get("CN");

		JSONObject cp = msgObj.getJSONObject("CP");

		if (cp != null) {
			Date DT = cp.getDate("DT");

			Date DataTime = cp.getDate("DataTime");

			JSONArray params = cp.getJSONArray("Params");

			if (DataTime == null || params == null || params.size() < 1) {
				return;
			}

			Date cDataTime = station.getDataTime();

			if (cDataTime == null) {
				cDataTime = DataTime;
			}

			if (params != null) {

				JSONArray pt = new JSONArray();
				JSONArray lc = new JSONArray();

				for (Object el : params) {

					JSONObject element = (JSONObject) el;

					if (element != null) {

						if (element.containsKey("ParamID")) {
							String paramId = element.getString("ParamID");

							if (lcPollutes != null && lcPollutes.contains(paramId)) {
								lc.add(element);
							} else {
								pt.add(element);
							}
						}
					}
				}

				if (!pt.isEmpty()) {
					// 发送普通因子数据
					Map<String, Object> pt_cp = new HashMap<String, Object>();
					pt_cp.put("DataTime", DataTime);
					pt_cp.put("DT", DT);
					pt_cp.put("Params", pt);
					msgObj.put("CP", pt_cp);
					String json = JSON.toJSONStringWithDateFormat(msgObj, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);
					logger.info(json);
					publish.send(CN, json);
					publish.publishTL(CN, json);
				}

				if (!lc.isEmpty()) {
					// 发送轮侧因子数据
					Map<String, Object> lc_cp = new HashMap<String, Object>();
					lc_cp.put("DataTime", DataTime);
					lc_cp.put("DT", DT);
					lc_cp.put("Params", lc);
					msgObj.put("CP", lc_cp);
					String json1 = JSON.toJSONStringWithDateFormat(msgObj, "yyyy-MM-dd HH:mm:ss",
							SerializerFeature.WriteDateUseDateFormat);
					logger.info(json1);
					publish.send(CN, json1);
					publish.publishTL(CN, json1);
				}

			}
		}
	}
	
	/**
	 * 中天钢平滑
	 * 
	 * @param params
	 */
	private void ztgSmooth(String MN, String CN, Date DataTime, JSONArray params) {
		if ((AvgSpeed.pollCodes != null) && (AvgSpeed.step > 0)) {
			String pollCode = "";
			String value ="";
			String flag = "";
			
			for (Object el : params) {

				JSONObject element = (JSONObject) el;

				if (element != null) {
					if (CN.equals("2011")) {
						if (element.containsKey("Rtd")) {
							value = element.getString("Rtd");
						}
					}
					else
					{
						if (element.containsKey("Avg")) {
							value = element.getString("Avg");
						}
					}
					
					if (element.containsKey("ParamID")) {
						pollCode = element.getString("ParamID");
					}
				}
				
				if (AvgSpeed.pollCodes.contains(pollCode))
				{
					if (!StringUtil.isNullOrEmpty(value))
					{
						// logger.info("平滑前 : MN = " + MN + " CN = " + CN + " pollCode = " + pollCode + " value = " + value + " pollCodes = " + AvgSpeed.pollCodes + " step = " + AvgSpeed.step);
						double res = AvgSpeed.setArraySpeed(MN, CN, pollCode, value, flag, DateUtil.dateToStr(DataTime, "yyyyMMddHHmmss"));
						// logger.info("平滑后 : MN = " + MN + " CN = " + CN + " pollCode = " + pollCode + " res = " + res);
						if (CN.equals("2011")) {
							element.put("Rtd", (""+res));
						}
						else
						{
							element.put("Avg", (""+res));
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取step的值
	 * 
	 * @return 返回step的值
	 */
	public static int getStep() {
		return step;
	}

	/**
	 * 设置step值
	 * 
	 * @param step
	 */
	public static void setStep(int step) {
		T212.step = step;
	}

	/**
	 * 获取pollCodes的值
	 * 
	 * @return 返回pollCodes的值
	 */
	public static String getPollCodes() {
		return pollCodes;
	}

	/**
	 * 设置pollCodes值
	 * 
	 * @param pollCodes
	 */
	public static void setPollCodes(String pollCodes) {
		T212.pollCodes = pollCodes;
	}
}
