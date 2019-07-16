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

package com.zeei.das.cas.mq;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.zeei.das.cas.vo.MsgFormatVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.mq.BasePublish;

/**
 * 类 名 称：Publish 类 描 述：发送mq队列消息 功能描述：发送mq队列消息 创建作者：quanhongsheng
 */

@Service("publish")
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
		case Constant.MQ_QUEUE_LOGS:
			publishLog(msg);
			break;
		case Constant.MQ_QUEUE_REPORT:
			publishReport(msg);
			break;
		case Constant.MQ_QUEUE_TCC:
			publishCmd(msg);
			break;
		case Constant.MQ_QUEUE_STATIONCFG:
			publishStationCfg(msg);
			break;
		case Constant.MQ_QUEUE_PARAMRULE:
			publishParamRule(msg);
			break;
		case Constant.MQ_QUEUE_AUDITRULE:
			publishAuditRule(msg);
			break;
		case Constant.MQ_QUEUE_NOTICERULE:
			publishNoticeRule(msg);
			break;
		case Constant.MQ_QUEUE_TM212:
			publishTM212(msg);
			break;
		case Constant.MQ_QUEUE_ALARMRULE:
			publishAlarmRule(msg);
			break;
		case Constant.MQ_QUEUE_QC:
			publishQC(msg);
			break;
		default:
			publishMN(msg);
			break;
		}
	}

	/**
	 * 
	 * publish:站点直连mq的消息发送
	 *
	 * @param msg
	 *            void
	 */
	public void publishMN(String msg) {

		MsgFormatVO msgFormat = JSON.parseObject(msg, MsgFormatVO.class);
		String name = msgFormat.getMN();
		if (producer.isExist(name)) {
			Queue queue = new Queue(name, true, false, false);
			producer.declare(queue);
		}
		producer.publish(name, msg);
	}

	/**
	 * 
	 * publishStationCfg:发送站点配置更新通知
	 *
	 * @param msg
	 *            void
	 */
	public void publishStationCfg(String msg) {

		String name = Constant.MQ_QUEUE_STATIONCFG;
		if (producer.isExist(name)) {
			Exchange exchange = new FanoutExchange(name, true, false);
			producer.declare(exchange);
		}
		producer.publish(name, null, msg);
	}

	/**
	 * 
	 * publishAlarmRule:发送告警规则更新通知
	 *
	 * @param msg
	 *            void
	 */
	public void publishAlarmRule(String msg) {

		String name = Constant.MQ_QUEUE_ALARMRULE;

		if (producer.isExist(name)) {
			Queue queue = new Queue(name, true, false, false);
			producer.declare(queue);
		}

		producer.publish(name, msg);
	}

	/**
	 * 
	 * publishAuditRule:发送自动审核规则更新通知
	 *
	 * @param msg
	 *            void
	 */
	public void publishAuditRule(String msg) {

		String name = Constant.MQ_QUEUE_AUDITRULE;

		if (producer.isExist(name)) {
			Queue queue = new Queue(name, true, false, false);
			producer.declare(queue);
		}

		producer.publish(name, msg);
	}

	/**
	 * 
	 * publishNoticeRule:发送通知规则更新通知
	 *
	 * @param msg
	 *            void
	 */
	public void publishNoticeRule(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_NOTICERULE)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_NOTICERULE, true, false, false);
			producer.declare(queue);
		}

		producer.publish(Constant.MQ_QUEUE_NOTICERULE, msg);
	}

	/**
	 * 
	 * publishParamRule:发送因子映射关系更新通知
	 *
	 * @param msg
	 *            void
	 */
	public void publishParamRule(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_PARAMRULE)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_PARAMRULE, true, false, false);
			producer.declare(queue);
		}

		producer.publish(Constant.MQ_QUEUE_PARAMRULE, msg);
	}

	/**
	 * 
	 * publishCmd:发送返控命令
	 *
	 * @param msg
	 *            void
	 */
	public void publishCmd(String msg) {

		if (producer.isExist(Constant.MQ_QUEUE_TCC)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_TCC, true, false, false);
			producer.declare(queue);
		}

		producer.publish(Constant.MQ_QUEUE_TCC, msg);
	}

	/**
	 * 
	 * publishTM212:发送告警数据
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
	 * publishQC:质控命令下发队列
	 * 
	 * @param msg void
	 */
	public void publishQC(String msg) {
		if (producer.isExist(Constant.MQ_QUEUE_QC)) {
			Queue queue = new Queue(Constant.MQ_QUEUE_QC, true, false, false);
			producer.declare(queue);
		}
		producer.publish(Constant.MQ_QUEUE_QC, msg);
	}

}
