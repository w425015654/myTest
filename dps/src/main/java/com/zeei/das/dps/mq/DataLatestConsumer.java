/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：DataConsumer.java
* 包  名  称：com.zeei.das.dps.mq
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午2:45:50
* 
* 修改历史
* 1.0 zhou.yongbo 2017年5月4日下午2:45:50 创建文件
*
*/

package com.zeei.das.dps.mq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.rabbitmq.client.Channel;
import com.zeei.das.common.utils.ReportUtil;
import com.zeei.das.dps.audit.AuditFactory;
import com.zeei.das.dps.storage.StorageHandler;
import com.zeei.das.dps.vo.T20x1Message;

/**
 * 类 名 称：DataConsumer 类 描 述：T20x1消息消费者 功能描述：拉取T20x1消息，存入messages中，做为后续数据入库的数据源；
 * 异常处理，正确入库的消息，向mq发送确认，入库失败的消息，写入失败队列，然后向mq确认。 创建作者：zhou.yongbo
 */
public class DataLatestConsumer implements ChannelAwareMessageListener {

	private static Logger logger = LoggerFactory.getLogger(DataLatestConsumer.class);

	@Autowired
	private ReportUtil reportUtil;

	@Autowired
	private AuditFactory auditFactory;

	@Autowired
	StorageHandler storageHandler;

	// 接收的数据

	public static Map<String, T20x1Message> T20x1 = new ConcurrentHashMap<String, T20x1Message>();

	private String QUEUE_NAME = "TLatest";

	public DataLatestConsumer(String name) {
		this.QUEUE_NAME = name;
	}

	@PostConstruct
	protected void initialize() {
		logger.info(String.format("启动%s队列消费者", QUEUE_NAME));
		storageHandler();
	}

	/**
	 * 消息消费的handler函数，把消息存入
	 */
	@Override
	public void onMessage(Message message, Channel channel) {

		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		String json = "";
		try {

			reportUtil.report();

			json = new String(message.getBody(), "UTF-8");

			T20x1Message msg = JSON.parseObject(json, T20x1Message.class);

			msg.setDeliveryTag(deliveryTag);

			if (!msg.valid()) {
				channel.basicAck(deliveryTag, false);
				throw new JSONException("消息解析失败");
			}

			// 数据审核
			auditFactory.audtHandler(msg);

			T20x1.put(msg.getMN(), msg);

		} catch (Exception e) {
			logger.error(String.format("消息：%s，失败原因：%s", json, e.toString()));
		} finally {
			try {
				channel.basicAck(deliveryTag, false);
			} catch (IOException e1) {
				logger.error("", e1);
			}

		}
	}

	/**
	 * 入库处理流程
	 */

	public void storageHandler() {

		Runnable runnable = new Runnable() {
			public void run() {

				try {
					List<T20x1Message> data = new ArrayList<T20x1Message>();

					for (T20x1Message msg : T20x1.values()) {

						if (msg != null) {
							data.add(msg);
						}
					}
					T20x1.clear();

					if (data != null && !data.isEmpty()) {
						storageHandler.insertLatestData(data);
					}

				} catch (Exception e) {
					logger.error("", e);
				} 
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 2000, TimeUnit.MILLISECONDS);
	}

}
