/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：RejectAndRplyToRequeueRecoverer.java
* 包  名  称：com.zeei.das.mq
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月15日上午11:23:40
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月15日上午11:23:40 创建文件
*
*/

package com.zeei.das.common.mq;

import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;

import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：RejectAndRplyToRequeueRecoverer 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public class RejectAndRplyToRequeueRecoverer extends RejectAndDontRequeueRecoverer {

	/** 用于发送拒绝消息状态给请求者 */
	RabbitTemplate replyToTemplate;

	@Override
	public void recover(Message message, Throwable cause) {
		MessageProperties mp = message.getMessageProperties();
		if (mp != null && StringUtil.isEmptyOrNull(mp.getReplyTo()) && replyToTemplate != null) {
			String reply = "rabbit.replyto.interceptor.illegal.request";
			Message rejectRespMsg = new Message(reply.getBytes(), mp);
			Address address = new Address(mp.getReplyTo());
			replyToTemplate.convertAndSend(address.getExchangeName(), address.getRoutingKey(), rejectRespMsg);
		}
		super.recover(message, cause);
	}

	public void setReplyToTemplate(RabbitTemplate replyToTemplate) {
		this.replyToTemplate = replyToTemplate;
	}

}
