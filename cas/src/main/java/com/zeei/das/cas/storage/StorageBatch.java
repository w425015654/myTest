/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：StorageBath.java
* 包  名  称：com.zeei.das.dps.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年8月10日上午9:02:11
* 
* 修改历史
* 1.0 quanhongsheng 2017年8月10日上午9:02:11 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.zeei.das.cas.CasService;
import com.zeei.das.cas.mq.Tcc212Handler;
import com.zeei.das.cas.vo.CtlRecDetailVO;
import com.zeei.das.cas.vo.Msg2021VO;
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.cas.vo.Msg2070VO;
import com.zeei.das.cas.vo.Msg2076VO;
import com.zeei.das.cas.vo.TccDataVO;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：StorageBath 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component
public class StorageBatch {

	private static Logger logger = LoggerFactory.getLogger(StorageBatch.class);

	// 一次性批处理消息数量
	private long batchSize = Long.parseLong(CasService.cfgMap.getOrDefault("BatchSize", "1000"));

	@Autowired
	StorageHandler901x storageHandler901x;
	@Autowired
	StorageHandler2021 storageHandler2021;
	@Autowired
	StorageHandler2076 storageHandler2076;
	@Autowired
	StorageHandler2070 storageHandler2070;
	@Autowired
	StorageHandler3020 storageHandler3020;
	@Autowired
	StorageHandler2062 storageHandler2062;
	@Autowired
	StorageHandler2063 storageHandler2063;
	@Autowired
	StorageHandler20656 storageHandler20656;

	private long stamp = 0;

	long delivery = 0;

	int batch = 1000;

	// 线程池
	ExecutorService threadPool = null;

	public Map<String, List<?>> packageData() {

		List<CtlRecDetailVO> L90x1_Comp = new ArrayList<CtlRecDetailVO>();
		List<CtlRecDetailVO> L90x1_Ctrl = new ArrayList<CtlRecDetailVO>();
		List<Msg2021VO> L2021 = new ArrayList<Msg2021VO>();
		List<Msg2062VO> L2062 = new ArrayList<Msg2062VO>();
		List<Msg20656VO> L20656 = new ArrayList<Msg20656VO>();
		List<Msg2063VO> L2063 = new ArrayList<Msg2063VO>();
		List<Msg2076VO> L2076 = new ArrayList<Msg2076VO>();
		List<Msg2070VO> L2070 = new ArrayList<Msg2070VO>();

		Map<String, List<?>> map = new HashMap<String, List<?>>();
		map.put("L3020", L2021);
		map.put("L2062", L2062);
		map.put("L2063", L2063);
		map.put("L20656", L20656);
		map.put("L2076", L2076);
		map.put("L2070", L2070);
		map.put("L90x1_Comp", L90x1_Comp);
		map.put("L90x1_Ctrl", L90x1_Ctrl);

		int size = 0;

		// 拉取打包数据
		while (!StorageFactory.tccDatas.isEmpty()) {
			try {

				TccDataVO msg = StorageFactory.tccDatas.poll();
				String CN = msg.getCN();

				for (String json : msg.getBody()) {

					switch (CN) {
					case "2021":
					case "3020":
					case "3041":
						L2021.add(JSON.parseObject(json, Msg2021VO.class));
						break;
					case "2076":
						L2076.add(JSON.parseObject(json, Msg2076VO.class));
						break;
					case "2070":
						L2070.add(JSON.parseObject(json, Msg2070VO.class));
						break;
					case "2062":
						L2062.add(JSON.parseObject(json, Msg2062VO.class));
						break;
					case "2063":
						L2063.add(JSON.parseObject(json, Msg2063VO.class));
						break;
					case "2065":
					case "2066":
						L20656.add(JSON.parseObject(json, Msg20656VO.class));
						break;
					case "9011":
					case "9012":
					case "9034":

						CtlRecDetailVO vo = JSON.parseObject(json, CtlRecDetailVO.class);

						if (vo != null && !StringUtil.isEmptyOrNull(vo.getQN())) {

							// 一位类型+年月日时分秒毫秒+4位随机，类型
							// 1表示补数，2表示其他返控
							String QN = vo.getQN();

							if ("1".equals(QN.substring(0, 1))) {
								L90x1_Comp.add(vo);
							} else {
								L90x1_Ctrl.add(vo);
							}
						}

						break;
					}
				}

				if (delivery < msg.getDeliveryTag()) {
					delivery = msg.getDeliveryTag();
				}

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
	@PostConstruct
	public void storageHandler() {
		threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Runnable runnable = new Runnable() {
			public void run() {

				try {
					stamp = System.currentTimeMillis();
					// 获取
					Map<String, List<?>> map = packageData();
					List<Future<Boolean>> tasks = new ArrayList<>();
					for (Map.Entry<String, List<?>> m : map.entrySet()) {
						try {

							String CN = m.getKey();

							List<?> list = m.getValue();

							if (list != null && list.size() > 0) {

								Callable<Boolean> task = new Callable<Boolean>() {
									@SuppressWarnings("unchecked")
									@Override
									public Boolean call() throws Exception {

										switch (CN) {
										case "L3020":
										case "L3041":
											// 写入3020消息
											storageHandler3020.storageBatch(list);
											// 写入3020最新数据
											storageHandler3020.insertLatest((List<Msg2021VO>) list);
											break;
										case "L2076":
											// 写入2076数据
											storageHandler2076.storageBatch(list);
											break;
										case "L2070":
											// 写入2070数据
											storageHandler2070.storageBatch(list);
											break;
										case "L2062":
											// 写入2062数据
											storageHandler2062.storageBatch(list);
											break;
										case "L20656":
											// 写入2065和2066数据
											storageHandler20656.storageBatch(list);
											break;
										case "L2063":
											storageHandler2063.storageBatch(list);
											break;
										case "L90x1_Ctrl":
											// 写入90x1数据
											storageHandler901x.storageBatch((List<CtlRecDetailVO>) list, 2);
											// 修改90x1最新数据
											storageHandler901x.updateBatch((List<CtlRecDetailVO>) list, 2);
											break;
										case "L90x1_Comp":
											// 写入90x1 补数数据
											storageHandler901x.storageBatch((List<CtlRecDetailVO>) list, 1);
											// 修改90x1 补数最新数据
											storageHandler901x.updateBatch((List<CtlRecDetailVO>) list, 1);
											break;
										}
										return Boolean.TRUE;
									}
								};

								Future<Boolean> t = threadPool.submit(task);
								tasks.add(t);
							}
						} catch (Exception e) {
							logger.error("", e);
						}

					}

					if (!tasks.isEmpty()) {
						for (Future<Boolean> t : tasks) {
							try {
								Boolean b = t.get(10, TimeUnit.SECONDS);
								if (!(b && t.isDone())) {
									logger.info("统计线程执行超时");
									// t.cancel(true);
								}

							} catch (Exception e) {
								logger.error("入库异常", e);
							}
						}

					}

				} catch (Exception e) {
					logger.error("", e);

				} finally {
					// 所有的子线程都结束了！
					acknowledge();
				}
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(runnable, 1, 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 */
	private void acknowledge() {

		try {

			if (delivery > 0) {

				Channel channel = Tcc212Handler.channel0;
				if (channel != null && channel.isOpen()) {
					channel.basicAck(delivery, true);
					delivery = 0;
				}
				logger.info(String.format("整个入库过程耗时：%sS", Math.ceil((System.currentTimeMillis() - stamp) / 1000)));
			}

		} catch (IOException e) {

			logger.error("", e);
		}

	}
}
