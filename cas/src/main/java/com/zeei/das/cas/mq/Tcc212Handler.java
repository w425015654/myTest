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

package com.zeei.das.cas.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.zeei.das.cas.storage.StorageFactory;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：StationCfgHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component
public class Tcc212Handler implements ChannelAwareMessageListener {

	@Autowired
	Publish publish;

	@Autowired
	StorageFactory storageFactory;

	public static Channel channel0;

	// 参数中使用@Header获取mesage
	public void onMessage(Message message, Channel channel) {
		try {

			channel0 = channel;
			long deliveryTag = message.getMessageProperties().getDeliveryTag();
			String msg = new String(message.getBody(), "UTF-8");
			
			boolean isSucess = storageFactory.parserHandler(msg, deliveryTag);

			if (!isSucess) {
				if (channel != null && channel.isOpen()) {
				channel.basicAck(deliveryTag, false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}
}
