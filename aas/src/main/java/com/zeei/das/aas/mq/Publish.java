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

package com.zeei.das.aas.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.mq.BasePublish;

/**
 * 类 名 称：Publish 类 描 述：发送mq队列消息 功能描述：发送mq队列消息 创建作者：quanhongsheng
 */
@Component("publish")
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
		case Constant.MQ_QUEUE_ALARM:
			publishAlarm(msg);
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
	 * publishTM212:发送告警数据
	 *
	 * @param msg
	 * 
	 *            void
	 */
	public void publishAlarm(String msg) {
		if (producer.isExist(Constant.MQ_QUEUE_ALARM)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_ALARM, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_ALARM, msg);
	}

}
