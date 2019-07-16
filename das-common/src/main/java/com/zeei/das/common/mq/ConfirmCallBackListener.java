package com.zeei.das.common.mq;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;
 
@Service("confirmCallBackListener")
public class ConfirmCallBackListener implements ConfirmCallback{
	
	private static Logger logger = LoggerFactory.getLogger(ConfirmCallBackListener.class);
	
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {	
		
		if(!ack){
			logger.error("------",cause);
		}
	}
}