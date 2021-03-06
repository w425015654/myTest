/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：StationCfgHandler.java
* 包  名  称：com.zeei.das.cgs.mq
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月15日下午1:21:17
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月15日下午1:21:17 创建文件
*
*/

package com.zeei.das.aas.mq;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.zeei.das.aas.alarm.AlarmFactory;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.ReportUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StationCfgHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component("dataAnalysisHandler")
public class DataAnalysisHandler implements ChannelAwareMessageListener {

	@Autowired
	Publish publish;

	@Autowired
	AlarmFactory alarmFactory;

	@Autowired
	ReportUtil reportUtil;

	// 参数中使用@Header获取mesage
	public void onMessage(Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {

			// 上报心跳数据
			reportUtil.report();
			String msg = new String(message.getBody(), "UTF-8");
			if (!StringUtil.isEmptyOrNull(msg)) {
				alarmFactory.alarmHandler(msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		} finally {
			try {
				if (channel != null && channel.isOpen()) {
					channel.basicAck(deliveryTag, false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
