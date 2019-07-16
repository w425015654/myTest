/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：StationUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：TODO 站点工具类
* 创建日期：2017年4月19日上午11:32:13
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月19日上午11:32:13 创建文件
*
*/

package com.zeei.das.cgs.T212;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.mq.Publish;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.CrcUtil;
import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.QNUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：StationUtil 类 描 述：TODO 站点工具类 功能描述：TODO 站点工具类 创建作者：quanhongsheng
 */

@Component
public class Station {

	private static Logger logger = LoggerFactory.getLogger(Station.class);

	// 服务心跳上报周期
	static String onlineCycle;

	static {
		onlineCycle = CgsService.cfgMap.get("reportCycle");
		if (StringUtil.isNullOrEmpty(onlineCycle)) {
			onlineCycle = "5";
		}
	};

	@Autowired
	Publish publish;

	/**
	 * 
	 * getStationCfg:获取内存站点配置信息
	 *
	 * @param MN
	 * @return StationCfgVO
	 */
	public StationCfgVO getStationCfg(String MN) {
		StationCfgVO cfg = CgsService.stationMap.get(MN);
		return cfg;
	}

	/**
	 * 
	 * send:下发消息给站点
	 *
	 * @param channel
	 * @param msg
	 *            void
	 */
	public void send(Channel channel, String msg) {

		if (channel == null) {
			String info = String.format("下行消息：[失败]%s (%s)", msg, "连接尚未建立!");
			logger.error(info);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_CMD, info));
			return;
		}

		// 组装T212 消息消息
		byte[] b = makeT212Msg(msg);

		ByteBuf buf = channel.alloc().buffer(b.length);
		buf.writeBytes(b);
		try {
			channel.writeAndFlush(buf).sync().addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture future) throws Exception {
					String info = String.format("下行消息：[成功]%s",new String(b));
					logger.trace(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_CMD, info));
				}
			});
		} catch (InterruptedException e) {			
			logger.error("",e);
			String info = String.format("下行消息：[失败]%s (%s)", msg, e.getMessage());
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_CMD, info));
		}

	}

	/**
	 * 
	 * calibrationTime:站点校时
	 *
	 * @param MN
	 *            void
	 */
	public void calibrationTime(String MN, Channel channel) {

		StationCfgVO cfg = getStationCfg(MN);

		if (cfg != null && cfg.isTimecal()) {

			String QN = QNUtil.getQN("1");
			String PW = cfg.getPwd();
			String DateTime = DateUtil.getCurrentDate("yyyyMMddHHmmss");
			String msg = String.format("QN=%s;ST=32;CN=1012;PW=%s;MN=%s;Flag=1;CP=&&SystemTime=%s&&", QN, PW, MN,
					DateTime);
			send(channel, msg);
		}
	}

	/**
	 * 
	 * makeT212Msg:封装T212消息消息
	 *
	 * @param msg
	 * @return byte[]
	 */
	public static byte[] makeT212Msg(String msg) {
		String len = String.format("%04d", msg.length());
		String crc = CrcUtil.Crc16Calc(msg);
		String t212Msg = String.format("##%s%s%s\r\n", len, msg, crc);
		return t212Msg.getBytes();
	}

	/**
	 * 
	 * getMN:获取消息的MN号
	 *
	 * @param msg
	 * @return String
	 */
	public String getMN(String msg) {
		String MN = null;

		// 编译正则表达式 获取MN
		String regMN = "MN=([-|0-9a-zA-Z]*);";
		Pattern mnPattern = Pattern.compile(regMN);
		Matcher mnMatcher = mnPattern.matcher(msg);
		if (mnMatcher.find()) {
			MN = mnMatcher.group(1);
		}

		return MN;
	}

	/**
	 * 
	 * getMN:获取消息的ST
	 *
	 * @param msg
	 * @return String
	 */
	public String getST(String msg) {
		String ST = null;

		// 编译正则表达式 获取MN
		String regST = "ST=([-|0-9a-zA-Z]+);";
		Pattern stPattern = Pattern.compile(regST);
		Matcher stMatcher = stPattern.matcher(msg);
		if (stMatcher.find()) {
			ST = stMatcher.group(1);
		}

		return ST;
	}

}
