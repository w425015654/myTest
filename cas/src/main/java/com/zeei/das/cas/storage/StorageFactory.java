/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：storageFactory.java
* 包  名  称：com.zeei.das.cas.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:27:37
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午4:27:37 创建文件
*
*/

package com.zeei.das.cas.storage;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.zeei.das.cas.mq.Publish;
import com.zeei.das.cas.vo.TccDataVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：storageFactory 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */
@Component("storageFactory")
public class StorageFactory {

	@Autowired
	StorageHandler901x storageHandler901x;
	@Autowired
	StorageHandler2021 storageHandler2021;
	@Autowired
	StorageHandler2070 storageHandler2070;
	@Autowired
	StorageHandler2076 storageHandler2076;
	@Autowired
	StorageHandler3020 storageHandler3020;
	@Autowired
	StorageHandler2062 storageHandler2062;
	@Autowired
	StorageHandler2063 storageHandler2063;
	@Autowired
	StorageHandler20656 storageHandler20656;

	@Autowired
	Publish publish;

	private static Logger logger = LoggerFactory.getLogger(StorageFactory.class);

	// 解析好的数据
	static ConcurrentLinkedQueue<TccDataVO> tccDatas = new ConcurrentLinkedQueue<TccDataVO>();

	/**
	 * 
	 * storageHandler:TODO 请修改方法功能描述
	 *
	 * @param msg
	 *            void
	 */
	public void storageHandler(String msg, long deliveryTag, Channel channel) {

		try {

			JSONObject msgObj = JSON.parseObject(msg);

			if (msgObj != null) {

				StorageHandler handler = null;

				String CN = msgObj.getString("CN");

				switch (CN) {

				case "2021":
					handler = storageHandler2021;
					break;
				case "2070":
					handler = storageHandler2070;
					break;
				case "2076":
					handler = storageHandler2076;
					break;
				case "3020":
				case "3041":
					handler = storageHandler3020;
					break;
				case "2062":
					handler = storageHandler2062;
					break;
				case "2063":
					handler = storageHandler2063;
					break;
				case "2065":
				case "2066":
					handler = storageHandler20656;
					break;
				case "9011":
				case "9012":
				case "9034":
					handler = storageHandler901x;
					break;
				}
				if (handler != null) {
					handler.storage(msgObj);

					String info = String.format("存储消息：%s", msg);
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
				} else {
					String info = String.format("不支持此类型消息：%s", msg);
					logger.info(info);
					publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, info));
				}
				if (channel != null && channel.isOpen()) {
					channel.basicAck(deliveryTag, false);
				}
			}
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}

	public boolean parserHandler(String msg, long deliveryTag) {

		try {
			JSONObject msgObj = JSON.parseObject(msg);

			if (msgObj != null) {

				StorageHandler handler = null;

				String CN = msgObj.getString("CN");

				switch (CN) {

				case "2021":
					handler = storageHandler2021;
					break;
				case "2076":
					handler = storageHandler2076;
					break;
				case "3020":
				case "3041":
					handler = storageHandler3020;
					break;
				case "2070":
					handler = storageHandler2070;
					break;
				case "2062":
					handler = storageHandler2062;
					break;
				case "2063":
					handler = storageHandler2063;
					break;
				case "2065":
				case "2066":
					handler = storageHandler20656;
					break;
				case "9011":
				case "9012":
				case "9034":
					handler = storageHandler901x;
					break;
				}
				if (handler != null) {
					List<String> rl = handler.parser(msgObj);

					if (rl != null && rl.size() > 0) {

						TccDataVO vo = new TccDataVO();
						vo.setBody(rl);
						vo.setCN(CN);
						vo.setDeliveryTag(deliveryTag);

						tccDatas.offer(vo);

						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
