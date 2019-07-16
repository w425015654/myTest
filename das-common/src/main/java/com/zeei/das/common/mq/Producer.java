/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：MqUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月18日上午8:34:37
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月18日上午8:34:37 创建文件
*
*/

package com.zeei.das.common.mq;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类 名 称：MqUtil 类 描 述： 请修改文件描述 功能描述： 请修改功能描述 创建作者：quanhongsheng
 */

@Service("producer")
public class Producer {

	public static final Logger logger = LoggerFactory.getLogger(Producer.class);

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	RabbitAdmin admin;

	/**
	 * 
	 * publish:发送对象
	 *
	 * @param name
	 * @param obj
	 *            void
	 */
	public void publish(String name, Object obj) {
		amqpTemplate.convertAndSend(name, obj);
	}

	/**
	 * 
	 * publish:发送字符串
	 *
	 * @param name
	 * @param msg
	 *            void
	 */

	public void publish(String name, String msg) {
		MessageProperties properties = new MessageProperties();
		try {
			Message msgObj = new Message(msg.getBytes("utf-8"), properties);
			amqpTemplate.send(name, msgObj);
					
		} catch (UnsupportedEncodingException e) {		
			logger.error("",e);
		}
	}

	/**
	 * 
	 * publish::发送字符串
	 *
	 * @param exchange
	 * @param routingKey
	 * @param message
	 *            void
	 */

	public void publish(String exchange, String routingKey, String message) {

		try {
			MessageProperties properties = new MessageProperties();
			Message msgObj = new Message(message.getBytes("utf-8"), properties);
			amqpTemplate.send(exchange, null, msgObj);
		} catch (UnsupportedEncodingException e) {
			logger.error("",e);
		}

	}

	/**
	 * 
	 * declare:申明队列
	 *
	 * @param queue
	 * 
	 *            void
	 */
	public void declare(Queue queue) {
		admin.declareQueue(queue);
	}

	/**
	 * 
	 * declare:申明交换机
	 *
	 * @param exchange
	 * 
	 *            void
	 */
	public void declare(Exchange exchange) {
		admin.declareExchange(exchange);
	}

	/**
	 * 
	 * isExist:判断队列是否存在
	 *
	 * @param queueName
	 * @return boolean
	 */
	public boolean isExist(String queueName) {
		if (admin.getQueueProperties(queueName) == null) {
			return true;
		} else {
			return false;
		}
	}

}
