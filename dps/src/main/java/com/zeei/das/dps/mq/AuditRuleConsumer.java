/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：MDataConsumer.java
* 包  名  称：com.zeei.das.dps.mq
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月27日上午10:35:19
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:35:19 创建文件
*
*/

package com.zeei.das.dps.mq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.dps.DpsService;

/**
 * 类 名 称：MDataConsumer 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Component
public class AuditRuleConsumer implements ChannelAwareMessageListener {

	private static Logger logger = LoggerFactory.getLogger(AuditRuleConsumer.class);

	@Autowired
	DpsService dpsService;

	@Autowired
	Publish publish;

	public void onMessage(Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {

			String msg = new String(message.getBody(), "UTF-8");
			logger.info("更新审核规则：" + msg);

			dpsService.initAuditCfg(1);

		} catch (Exception e) {
			logger.error("",e);
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
