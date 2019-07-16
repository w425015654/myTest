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

package com.zeei.das.dps.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.mq.Producer;

/**
 * 类 名 称：Publish 类 描 述：发送mq队列消息 功能描述：发送mq队列消息 创建作者：quanhongsheng
 */
@Component
public class Publish {

	@Autowired
	Producer producer;

	private Logger logger = LoggerFactory.getLogger(Publish.class);

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

		try {
			switch (CN) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		//logger.info("----"+msg);
		if (producer.isExist(Constant.MQ_QUEUE_CYCLE)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_CYCLE, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_CYCLE, msg);
	}

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

	/**
	 * publishFail:发布入库失败的消息到失败队列
	 */
	public void publishFail(String msg, String queueName) {

		String name = Constant.T20X1_FAIL_QUEUES.get(queueName);

		if (producer.isExist(name)) {
			Queue queue = new Queue(name, true, false, false);
			producer.declare(queue);
		}
		producer.publish(name, msg);
	}

}
