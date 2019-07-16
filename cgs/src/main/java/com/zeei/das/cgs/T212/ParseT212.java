/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ParseT212.java
* 包  名  称：com.zeei.das.cgs.common.T212
* 文件描述：T212 消息解析类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.T212;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.T212.ParseCP.ParseCP;
import com.zeei.das.cgs.mq.Publish;
import com.zeei.das.cgs.vo.ChannelVO;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;

import io.netty.channel.Channel;

/**
 * 类 名 称：ParseT212 类 描 述：T212 消息解析类 功能描述：T212 消息组包，解析工作 创建作者：quanhongsheng
 */

@Component("parseT212")
public class ParseT212 {

	private static Logger logger = LoggerFactory.getLogger(T212.class);

	@Autowired
	Publish publish;

	/**
	 * 
	 * parseT212Msg:解析T212 消息
	 *
	 * @param msgStr
	 *            消息内容
	 * @return Map<String,Object>
	 */

	@Autowired
	ParseFactory parseFactory;

	public JSONObject parseT212Msg(String msgStr, Channel channel) {

		JSONObject msgMap = new JSONObject();
		try {
			// 解析消息头
			msgMap = parseT212Head(msgStr);

			String MN = (String) msgMap.get("MN");
			String CN = (String) msgMap.get("CN");

			StationCfgVO cfg = CgsService.stationMap.get(MN);

			// 平台站点存在则以平台站点的ST为主，否则以站点上报的ST
			if (cfg != null) {
				//logger.info(MN+"--"+JSON.toJSONString(cfg));
				msgMap.put("ID", cfg.getID());
				msgMap.put("ST", cfg.getST());
			}

			String cps = (String) msgMap.get("CP");

			if (StringUtil.isEmptyOrNull(cps)) {
				String error = String.format("上传的数据包CP为空  :[%s] ", msgStr);
				logger.info(error);
				return null;
			}

			String cpStr = packetConcatente(msgMap, channel);

			if (StringUtil.isEmptyOrNull(cpStr)) {
				return null;
			}

			ParseCP parseCp = parseFactory.createParse(CN);

			if (parseCp == null) {

				String error = String.format("不支持消息格式！%s", msgStr);
				logger.error(error);
				publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
				publish.send("TE212", msgStr);
			} else {

				// 解析消息体
				JSONObject cp = parseCp.parseT212Body(cpStr);

				// 消息应答
				parseCp.ack(msgMap, channel);
				msgMap.put("CP", cp);

				return msgMap;
			}

		} catch (Exception e) {

			publish.send("TE212", msgStr);
			String error = String.format("T212消息内容未解析！%s", msgStr);
			logger.error(error);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
		}
		return null;
	}

	/**
	 * 
	 * parseT212Head:解析消息头
	 *
	 * @param msg
	 *            消息内容
	 * @return Map<String,Object>
	 */

	public JSONObject parseT212Head(String msg) {

		JSONObject msgMap = new JSONObject();

		// 头部统一为（Key)=(Value) 格式的表达式
		String regHeadElement = "([a-zA-Z]+)=([-|0-9a-zA-Z]+|&&.*&&)";

		// 找出全部匹配的 K-V
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regHeadElement);
		Matcher matcher = pattern.matcher(msg);
		int len = matcher.groupCount();
		if (len < 1) {
			return null;
		}

		while (matcher.find()) {
			// 将解析出来的k-V作为Head对象的属性
			String key = matcher.group(1);
			String value = matcher.group(2);
			if (!StringUtil.isEmptyOrNull(value)) {
				value = value.replaceAll("(?i)&&", "");
			}
			msgMap.put(key, value);
		}

		// 是否进行心跳检测
		msgMap.put("HT", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));

		return msgMap;
	}

	/**
	 * 
	 * packetConcatente:T212 消息组包
	 *
	 * @param msgMap
	 *            消息内容 map格式
	 * @return String
	 */
	public String packetConcatente(JSONObject msgMap, Channel channel) {

		String MN = msgMap.getString("MN");

		String channelId = channel.id().asShortText();

		ChannelVO channelVO = CgsService.channelMap.get(channelId);

		String cpStr = msgMap.getString("CP");

		if (StringUtil.isEmptyOrNull(cpStr)) {
			return null;
		}

		Integer PNO = null;
		Integer PNUM = null;

		if (msgMap.get("PNO") != null) {
			PNO = msgMap.getInteger("PNO");
		}

		if (msgMap.get("PNUM") != null) {
			PNUM = msgMap.getInteger("PNUM");
		}

		if (PNO == null || PNO == 0) {
			PNO = 1;
		}

		if (PNUM == null || PNUM == 0) {
			PNUM = 1;
		}

		if (PNUM == 1) {
			// 不分包
			channelVO.setStrPartialMsg(null);
			channelVO.setNPNO(1);
			return cpStr;
		}

		if (PNO == 1 || StringUtil.isEmptyOrNull(channelVO.getStrPartialMsg())) {
			// 分包，但本分包是第一包
			channelVO.setStrPartialMsg(cpStr);
			channelVO.setNPNO(PNO);
			return null;
		}

		// 接收的分包编号已存在丢失该包
		if (channelVO.getNPNO() == PNO && channelVO.getNPNO() > 1) {

			String error = "212组包收包重复:[" + MN + "] " + PNO;
			logger.error(error);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));
			return null;
		}

		if (channelVO.getNPNO() + 1 != PNO) {

			String error = "212组包收包不连续:[" + MN + "] PNO:" + PNO + "--NPNO:" + channelVO.getNPNO();
			logger.error(error);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, error));

			return null;
		}

		channelVO.setNPNO(PNO);

		if (PNO < PNUM) {
			// 本包不是第一包，但尚未收到最后一包
			channelVO.setStrPartialMsg(channelVO.getStrPartialMsg() + cpStr.substring(23));
			return null;
		}

		// 分包，且本包是最后一包
		cpStr = channelVO.getStrPartialMsg() + cpStr.substring(23);
		channelVO.setStrPartialMsg(null);
		channelVO.setNPNO(1);
		return cpStr;
	}

}
