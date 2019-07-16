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

package com.zeei.das.cgs.mq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.T212Code;
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
		case "2070":
		case "9011":
		case "9012":
		case "2013":
		case "2021":
		case "2076":
		case "2062":
		case "2063":
		case "2065":
		case "2066":
		case "3041":
			publishT(Constant.MQ_QUEUE_TC212,msg);
			break;
		case "3020":
			publishT(Constant.MQ_QUEUE_TC212,msg);
			publishT(Constant.MQ_QUEUE_TM212,msg);
			break;
		case Constant.MQ_QUEUE_TP212:
			publishT(Constant.MQ_QUEUE_TP212,msg);
			break;
		case Constant.MQ_QUEUE_TE212:
			publishT(Constant.MQ_QUEUE_TE212,msg);
			break;
		case Constant.MQ_QUEUE_LOGS:
			publishLog(msg);
			break;
		case Constant.MQ_QUEUE_TT212:
			publishT(Constant.MQ_QUEUE_TT212,msg);
			break;
		case Constant.MQ_QUEUE_REPORT:
			publishReport(msg);
			break;
		}
	}
	
	
	/**
	 * 发送最新数据
	 * @param msg
	 */
	public void publishTL(String CN, String msg) {
		
		if (!T212Code.T2011.equalsIgnoreCase(CN) && !T212Code.T2051.equalsIgnoreCase(CN)
				&& !T212Code.T2061.equalsIgnoreCase(CN) && !T212Code.T2031.equalsIgnoreCase(CN)) {
			return;
		}

		if (producer.isExist("TLatest")) {
			Queue queue = new Queue("TLatest", true, false, false);
			producer.declare(queue);
		}
		producer.publish("TLatest", msg);
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
	 * MQ_QUEUE_TQ212:质控结果数据
	 * MQ_QUEUE_TM212:发送告警分析数据
	 * MQ_QUEUE_TT212:发送跟踪数据，网关服务接收到的数据
	 * MQ_QUEUE_TC212:发送网关服务接收到的控制信息（9011,9012,2021,2076）
	 * MQ_QUEUE_TP212:补传超时数据
	 * MQ_QUEUE_TE212:发送解析异常数据
	 * @param msg void
	 */
	public void publishT(String CN,String msg) {

		if (producer.isExist(CN)) {
			Queue queue = new Queue(CN, true, false, false);
			producer.declare(queue);
		}
		producer.publish(CN, msg);
	}

}
