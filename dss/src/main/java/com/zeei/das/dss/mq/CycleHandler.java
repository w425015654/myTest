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

package com.zeei.das.dss.mq;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.ReportUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dss.statistics.ManualStatistics;
import com.zeei.das.dss.statistics.StatisticsHanlder;

/**
 * 类 名 称：StationCfgHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class CycleHandler implements ChannelAwareMessageListener {

	@Autowired
	Publish publish;

	@Autowired
	ReportUtil reportUtil;


	public static Channel channel0 = null;

	private static Logger logger = LoggerFactory.getLogger(StatisticsHanlder.class);

	@Autowired
	StatisticsHanlder statisticsHanlder;

	@Autowired
	ManualStatistics manualStatistics;

	// 接收的数据

	public static ConcurrentLinkedQueue<JSONObject> TccMsgs = new ConcurrentLinkedQueue<JSONObject>();

	// 参数中使用@Header获取mesage
	public void onMessage(Message message, Channel channel) {

		channel0 = channel;
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {

			// channel.basicQos(1);

			// 发送心跳数据
			reportUtil.report();
			String msg = new String(message.getBody(), "UTF-8");

			if (!StringUtil.isEmptyOrNull(msg)) {

				//logger.info("接收消息：" + msg);

				JSONObject obj = JSON.parseObject(msg);

				String type = obj.getString("type");
				obj.put("originalStr", msg);

				obj.put("deliveryTag", deliveryTag);

				
				boolean exist = TccMsgs.stream().anyMatch(o -> o.getString("originalStr").equals(msg));
				// 不存在入列
				if (!exist) {
					TccMsgs.offer(obj);
				}
				
				if (("1".equals(type) || exist) &&  channel != null && channel.isOpen() ) {
					channel.basicAck(deliveryTag, false);
				}
				
				
			}

		} catch (Exception e) {

			try {
				channel.basicAck(deliveryTag, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			logger.error("", e);
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		} finally {
			// channel.basicAck(deliveryTag, false);
		}
	}
}
