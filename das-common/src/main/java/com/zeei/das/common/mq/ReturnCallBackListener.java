package com.zeei.das.common.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.stereotype.Service;

@Service("returnCallBackListener")
public class ReturnCallBackListener implements ReturnCallback {

	private static Logger logger = LoggerFactory.getLogger(ReturnCallBackListener.class);

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {

		logger.info("发送失败：" + new String(message.getBody()));

	}
}
