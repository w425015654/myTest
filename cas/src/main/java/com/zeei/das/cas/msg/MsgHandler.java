/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：MsgHandler.java
* 包  名  称：com.zeei.das.cas.msg
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月3日下午2:27:11
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午2:27:11 创建文件
*
*/

package com.zeei.das.cas.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.vo.MsgFormatVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.constants.NoticeMsgType;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：MsgHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Component
public class MsgHandler {

	private static Logger logger = LoggerFactory.getLogger(MsgHandler.class);

	// 站点协议类型 1 tcp 2 mq
	private static final String TCP = "1";

	// 消息来源 1标识是web发送，2 标识站点发送
	private static final String STATION = "2";

	@Autowired
	StationCfgMsg stationCfgMsg;

	@Autowired
	Publish publish;

	public void handler(String msg) {

		try {
			MsgFormatVO msgObj = JSON.parseObject(msg, MsgFormatVO.class);

			logger.info("接收消息：" + msg);

			String msgType = msgObj.getMsgType();
			String msgFrom = msgObj.getMsgFrom();
			String protocolType = msgObj.getProtocolType();

			if (STATION.equals(msgFrom)) {
				MsgGeneration generation = null;

				// 获取站点配置
				switch (msgType) {
				case "1":
					generation = stationCfgMsg;
					break;
				}
				if (generation != null) {
					String MN = msgObj.getMN();
					generation.generation(MN);
				}

			} else {

				String CN = "MN";

				switch (msgType) {
				// 站点更新通知
				case NoticeMsgType.NOTICE_TYPE_STATION:
					CN = Constant.MQ_QUEUE_STATIONCFG;
					break;
				// 通知规则更新通知
				case NoticeMsgType.NOTICE_TYPE_NOTICE:
					CN = Constant.MQ_QUEUE_NOTICERULE;
					break;
				// 自动审核规则更新通知
				case NoticeMsgType.NOTICE_TYPE_AUDIT:
					CN = Constant.MQ_QUEUE_AUDITRULE;
					break;
				// 返控命令
				case NoticeMsgType.NOTICE_TYPE_CMD:

					if (TCP.equals(protocolType)) {
						CN = Constant.MQ_QUEUE_TCC;
						msg = (String) msgObj.getMsgBody();
					} else {
						msg = makeMQCmd(msg);
					}

					break;
				// 因子关系更新通知
				case NoticeMsgType.NOTICE_TYPE_POLLUTE:
					CN = Constant.MQ_QUEUE_PARAMRULE;
					break;
				// 告警规则更新通知
				case NoticeMsgType.NOTICE_TYPE_ALARM:
					CN = Constant.MQ_QUEUE_ALARMRULE;
					break;
				}

				if (!TCP.equals(protocolType)) {
					CN = "MN";
				}

				publish.send(CN, msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("消息错误：" + msg);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}

	/**
	 * 
	 * makeMQCmd:封装MQ 直连站点下行消息
	 *
	 * @param msg
	 * @return String
	 */
	private String makeMQCmd(String msg) {

		// 站点直连mq的下行消息组装

		// QN=201705020958220016903077505166;ST=32;CN=2061;PW=123456;MN=0001;Flag=0;CP=&&BeginTime=20170502000000001,EndTime=20170502095822001&&
		Map<String, Object> msgMap = new HashMap<String, Object>();
		// 头部统一为（Key)=(Value) 格式的表达式
		String regHeadElement = "([a-zA-Z]+)=([-|0-9a-zA-Z]+|&&.*&&)";
		// 找出全部匹配的 K-V
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regHeadElement);
		Matcher matcher = pattern.matcher(msg);
		while (matcher.find()) {
			// 将解析出来的k-V作为Head对象的属性
			String key = matcher.group(1);
			String value = matcher.group(2);
			if ("CP".equals(key)) {
				value = value.replaceAll("(?i)&&", "");
				Map<String, String> cpMap = new HashMap<String, String>();
				Matcher matcher1 = pattern.matcher(value);
				while (matcher1.find()) {
					String id = matcher1.group(1);
					String text = matcher1.group(2);
					cpMap.put(id, text);
				}
				msgMap.put(key, cpMap);
			} else {
				msgMap.put(key, value);
			}
		}
		msg = JSON.toJSONString(msgMap);

		return msg;
	}

}
