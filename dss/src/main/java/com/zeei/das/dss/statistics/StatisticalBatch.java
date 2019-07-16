/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StatisticalBatch.java
* 包  名  称：com.zeei.das.dss.statistics
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年9月20日下午12:55:09
* 
* 修改历史
* 1.0 quanhongsheng 2017年9月20日下午12:55:09 创建文件
*
*/

package com.zeei.das.dss.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.zeei.das.dss.mq.CycleHandler;

/**
 * 类 名 称：StatisticalBatch 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Component
public class StatisticalBatch {

	@Autowired
	StatisticsHanlder statisticsHanlder;

	@Autowired
	ManualStatistics manualStatistics;

	private static Logger logger = LoggerFactory.getLogger(StatisticalBatch.class);

	private static String ISMANUAL = "1";

	// 一次性批处理消息数量
	private int batchSize = 500;

	private Long delivery = null;
	// 线程池
	ExecutorService threadPool = null;

	private long times;

	@PostConstruct
	public void handlerBatch() {

		int singleSize = Runtime.getRuntime().availableProcessors();

		threadPool = Executors.newFixedThreadPool(singleSize);

		Runnable runnable = new Runnable() {
			public void run() {
				try {

					int size = 0;
					times = System.currentTimeMillis();

					List<Future<Boolean>> tasks = new ArrayList<>();

					while (!CycleHandler.TccMsgs.isEmpty()) {
						try {

							JSONObject msg = CycleHandler.TccMsgs.poll();
							String type = msg.getString("type");
							delivery = msg.getLong("deliveryTag");

							// logger.info("处理消息:"+JSON.toJSONString(msg));

							Callable<Boolean> task = new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {

									// logger.info("处理消息:"+JSON.toJSONString(msg));
									if (ISMANUAL.equals(type)) {
										// 手动统计
										manualStatistics.handler(msg);
									} else {
										// 自动统计
										statisticsHanlder.handler(msg);
									}

									return Boolean.TRUE;
								}
							};

							Future<Boolean> t = threadPool.submit(task);
							tasks.add(t);
							if (size > batchSize) {
								break;
							}
							size++;

						} catch (Exception e) {

							Channel channel = CycleHandler.channel0;
							if (channel != null && channel.isOpen()) {
								channel.basicAck(delivery, false);
							}
							logger.error("", e);
						}

					}

					if (!tasks.isEmpty()) {
						for (Future<Boolean> t : tasks) {
							try {
								Boolean b = t.get(5, TimeUnit.SECONDS);
								if (!(b && t.isDone())) {
									logger.info("统计线程执行超时");
									// t.cancel(true);
								}

							} catch (Exception e) {
								logger.error("统计异常", e);
							}
						}
					}

				} catch (Exception e) {
					logger.error("", e);

				} finally {

					double diffTime = Math.ceil((System.currentTimeMillis() - times));

					try {
						Channel channel = CycleHandler.channel0;

						if (channel != null && channel.isOpen() && delivery != null) {
							channel.basicAck(delivery, true);
							logger.info(String.format("消息确认！ 耗时：%s ms", diffTime));
						}
						delivery = null;

					} catch (Exception e) {
						logger.error("", e);
					}
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 1, 100, TimeUnit.MILLISECONDS);
	}

}
