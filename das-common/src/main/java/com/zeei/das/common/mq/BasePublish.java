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

package com.zeei.das.common.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;


/**
 * 类 名 称：Publish 类 描 述：发送mq队列消息 功能描述：发送mq队列消息 创建作者：quanhongsheng
 */

@Component
public class BasePublish {

	@Autowired
	public Producer producer;

	/**
	 * 
	 * publishLog:发送系统log数据
	 *
	 * @param msg
	 *            void
	 */
	public void publishLog(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_LOGS)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_LOGS, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_LOGS, msg);
	}

	/**
	 * 
	 * publishReport:发送服务报告数据
	 *
	 * @param msg
	 *            void
	 */
	public void publishReport(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_REPORT)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_REPORT, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_REPORT, msg);
	}

}
