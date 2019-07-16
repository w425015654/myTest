/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：Publish.java
* 包  名  称：com.zeei.das.cgs。common.mq
* 文件描述：发送mq队列消息
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.dss.mq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.mq.BasePublish;

/**
 * 类 名 称：Publish 类 描 述：发送mq队列消息 功能描述：发送mq队列消息 创建作者：quanhongsheng
 */
@Component
public class Publish extends BasePublish {

	/**
	 * 
	 * publish:发送消息
	 *
	 * @param CN
	 *            消息类型
	 * @param msg
	 *            消息
	 * 
	 *            void
	 */
	public void send(String CN, String msg) {
		switch (CN) {
		case "2011":
			publishT212(Constant.MQ_QUEUE_TT2011, msg);
			break;
		case "2031":
			publishT212(Constant.MQ_QUEUE_TT2031, msg);
			break;
		case "2051":
			publishT212(Constant.MQ_QUEUE_TT2051, msg);
			break;
		case "2061":
			publishT212(Constant.MQ_QUEUE_TT2061, msg);
			break;
		case Constant.MQ_QUEUE_CYCLE:
			publishCycle(msg);
			break;
		case Constant.MQ_QUEUE_LOGS:
			publishLog(msg);
			break;
		case Constant.MQ_QUEUE_REPORT:
			publishReport(msg);
			break;
		}

	}

	/**
	 * 
	 * publishT212:发送监测数据
	 *
	 * @param CN
	 *            消息类型(2011,2031,2051,2061)
	 * @param msg
	 *            消息
	 * 
	 *            void
	 */
	public void publishT212(String CN, String msg) {

		if (producer.isExist(CN)) {
			Exchange exchange = new FanoutExchange(CN, true, false);
			producer.declare(exchange);
		}
		producer.publish(CN, null, msg);
	}

	/**
	 * 
	 * publishTM212:发送告警分析数据
	 *
	 * @param msg
	 * 
	 *            void
	 */
	public void publishTM212(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_TM212)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_TM212, true, false, false);
			producer.declare(queue);
		}

		producer.publish(Constant.MQ_QUEUE_TM212, msg);
	}

	/**
	 * 
	 * publishCycle:发送数据周期
	 *
	 * @param msg
	 * 
	 *            void
	 */
	public void publishCycle(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_CYCLE)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_CYCLE, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_CYCLE, msg);
	}

}
