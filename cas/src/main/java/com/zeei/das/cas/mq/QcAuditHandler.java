package com.zeei.das.cas.mq;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.zeei.das.cas.qcaudit.QcAuditFactory;
import com.zeei.das.cas.vo.MsgQcAudit;

/**
 * @类型名称：QcAuditHandler @类型描述：
 * @功能描述：通过审核的质控命令，需要去处理相关数据作为质控结果
 * @创建作者：zhanghu
 *
 */
@Component("qcAuditHandler")
public class QcAuditHandler implements ChannelAwareMessageListener {

	private static Logger logger = LoggerFactory.getLogger(QcAuditHandler.class);
	// 接收的数据
	private static ConcurrentLinkedQueue<MsgQcAudit> qcAudi = new ConcurrentLinkedQueue<>();
	//
	private static Channel rchannel;

	@Autowired
	QcAuditFactory qcAuditFactory;

	@PostConstruct
	protected void initialize() {

		logger.info("启动队列消费者，处理质控消息");

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					storageData();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 1000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {

		// 消息标识
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		rchannel = channel;

		try {
			String json = new String(message.getBody(), "UTF-8");

			MsgQcAudit msg = JSON.parseObject(json, MsgQcAudit.class);

			if (!msg.valid()) {
				channel.basicAck(deliveryTag, false);
				logger.error("质控消息格式不正确", JSON.toJSONString(msg));
			} else {
				msg.setDeliveryTag(deliveryTag);
				// 消息入队
				qcAudi.offer(msg);
			}

		} catch (Exception e) {

			logger.error("质控消息处理异常", e);
			channel.basicAck(deliveryTag, false);
		}
	}

	/**
	 * storageData:数据存储处理 void
	 */
	public void storageData() {

		if (!qcAudi.isEmpty()) {

			try {
				MsgQcAudit msgQcAudit = qcAudi.poll();

				// 结果处理，并得到是否已经处理完成的结果（包含是否超时）
				boolean falg = qcAuditFactory.checkData(msgQcAudit);

				if (!falg) {
					// 没处理完成，重新入队
					qcAudi.offer(msgQcAudit);
				} else {
					// 处理完消息，
					if (rchannel != null && rchannel.isOpen()) {

						rchannel.basicAck(msgQcAudit.getDeliveryTag(), false);
					}
				}

			} catch (IOException e) {
				logger.error("", e);
			}
		}
	}

}
