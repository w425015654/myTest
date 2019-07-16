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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.rabbitmq.client.Channel;
import com.zeei.das.common.utils.ReportUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.audit.AuditFactory;
import com.zeei.das.dps.storage.StorageHandler;
import com.zeei.das.dps.vo.T20x1Message;

/**
 * 类 名 称：DataConsumer 类 描 述：T20x1消息消费者 功能描述：拉取T20x1消息，存入messages中，做为后续数据入库的数据源；
 * 异常处理，正确入库的消息，向mq发送确认，入库失败的消息，写入失败队列，然后向mq确认。 创建作者：zhou.yongbo
 */
@Scope("prototype")
public class DataConsumerPush implements ChannelAwareMessageListener {

	private static Logger logger = LoggerFactory.getLogger(DataConsumerPush.class);

	@Autowired
	private ReportUtil reportUtil;

	@Autowired
	private Publish publish;

	@Autowired
	private AuditFactory auditFactory;

	@Autowired
	StorageHandler storageHandler;

	// 接收的数据
	private ConcurrentLinkedQueue<T20x1Message> T20x1 = new ConcurrentLinkedQueue<T20x1Message>();

	private long stamp = 0;

	private Channel rchannel;

	private Long delivery = null;

	private String QUEUE_NAME = "2011";

	/** 批量入库300毫秒能完成350条数据，现执行最长时间为10秒，这个数值视情况调整大小 **/
	private final int EXECUTE_TIMEOUT = 5;

	public DataConsumerPush(String name) {
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
		String CN = "2011";
		try {

			rchannel = channel;

			reportUtil.report();

			json = new String(message.getBody(), "UTF-8");

			T20x1Message msg = JSON.parseObject(json, T20x1Message.class);

			CN = msg.getCN();

			msg.setDeliveryTag(deliveryTag);

			if (!msg.valid()) {
				channel.basicAck(deliveryTag, false);
				throw new JSONException("消息解析失败");
			}

			// 数据审核
			auditFactory.audtHandler(msg);

			T20x1.offer(msg);

		} catch (Exception e) {

			logger.error(String.format("消息：%s，失败原因：%s", json, e.toString()));
			publish.publishFail(json, CN);
			try {
				channel.basicAck(deliveryTag, false);
			} catch (IOException e1) {
				logger.error("", e1);
			}
		}
	}

	// 线程池
	private ExecutorService threadPool = null;

	// 一次性批处理消息数量
	private long batchSize = Long.parseLong(DpsService.cfgMap.getOrDefault("BatchSize", "350"));

	/**
	 * 封装一次处理的消息
	 * 
	 * @return
	 */
	private Map<String, List<T20x1Message>> packaged() {

		int size = 0;

		Map<String, List<T20x1Message>> map = new HashMap<String, List<T20x1Message>>();

		int len = T20x1.size();

		if (len == 0) {
			ack();
		}

		logger.error(String.format("[%s]内存消息数量：%s", QUEUE_NAME, len));

		while (!T20x1.isEmpty()) {

			try {
				T20x1Message msg = T20x1.poll();

				if (msg == null) {
					break;
				}
				String tableName = storageHandler.getTableName(msg);

				if (StringUtil.isEmptyOrNull(tableName)) {
					continue;
				}

				List<T20x1Message> data = map.get(tableName);
				if (data == null) {
					data = new ArrayList<T20x1Message>();
					map.put(tableName, data);
				}

				delivery = msg.getDeliveryTag();

				data.add(msg);

				if (size > batchSize) {
					break;
				}
				size++;
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		return map;
	}

	/**
	 * 入库处理流程
	 */

	public void storageHandler() {

		threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		Runnable runnable = new Runnable() {
			public void run() {
				int count = 0;
				try {

					stamp = System.currentTimeMillis();
					Map<String, List<T20x1Message>> map = packaged();

					if (!map.isEmpty()) {
						List<Future<Boolean>> tasks = new ArrayList<>();
						for (Map.Entry<String, List<T20x1Message>> entry : map.entrySet()) {

							List<T20x1Message> list = entry.getValue();

							if (list != null && !list.isEmpty()) {
								Callable<Boolean> task = new Callable<Boolean>() {

									@Override
									public Boolean call() throws Exception {
										if ("TLatest".equals(QUEUE_NAME)) {

											storageHandler.insertLatestData(list);

										} else {

											storageHandler.insertBatch(entry.getKey(), list);
										}
										return Boolean.TRUE;
									}
								};

								Future<Boolean> t = threadPool.submit(task);
								tasks.add(t);

								count += list.size();
							}
						}

						if (!tasks.isEmpty()) {
							for (Future<Boolean> t : tasks) {

								try {

									Boolean b = t.get(EXECUTE_TIMEOUT, TimeUnit.SECONDS);
									if (!(b && t.isDone())) {
										logger.info(String.format("[%s]入库线程执行超时", QUEUE_NAME));
										// t.cancel(true);
									}

								} catch (Exception e) {

									logger.error(String.format("[%s]入库异常", QUEUE_NAME), e);
								}
							}
						}

					}

				} catch (Exception e) {

					logger.error(String.format("[%s]入库异常", QUEUE_NAME), e);
				} finally {

					double diffTime = Math.ceil((System.currentTimeMillis() - stamp));
					if (ack()) {
						logger.info(String.format("入库过程耗时[%s][%s]：%s ms", QUEUE_NAME, count, diffTime));
					}
				}
			}
		};

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 0, 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 消息確認
	 */
	private boolean ack() {
		if (rchannel != null && rchannel.isOpen() && delivery != null) {
			try {
				rchannel.basicAck(delivery, true);
				delivery = null;
				return true;
			} catch (IOException e) {
				logger.error("消息确认失败:", e);
			}
		}
		return false;
	}

}
