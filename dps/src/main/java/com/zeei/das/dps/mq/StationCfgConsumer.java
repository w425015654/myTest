/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：StationCfgConsumer.java
* 包  名  称：com.zeei.das.dps.mq
* 文件描述：更新内存治站点配置信息
* 创建日期：2017年4月27日上午10:35:06
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日上午10:35:06 创建文件
*
*/

package com.zeei.das.dps.mq;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.DpsService;
import com.zeei.das.dps.service.CfgServiceImpl;
import com.zeei.das.dps.vo.PointSiteVO;

/**
 * 类 名 称：StationCfgConsumer 类 描 述：更新内存治站点配置信息 功能描述：更新内存治站点配置信息
 * 创建作者：quanhongsheng
 */
@Component
public class StationCfgConsumer implements ChannelAwareMessageListener {

	private static Logger logger = LoggerFactory.getLogger(StationCfgConsumer.class);

	@Autowired
	CfgServiceImpl cfgService;

	@Autowired
	Publish publish;

	public void onMessage(Message message, Channel channel) {
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		try {

			String msg = new String(message.getBody(), "UTF-8");
			logger.info("更新站点信息：" + msg);

			JSONObject obj = JSON.parseObject(msg);
			if (obj == null || StringUtil.isEmptyOrNull(obj.getString("mN"))) {
				return;
			}
			String MN = obj.getString("mN");

			PointSiteVO station = cfgService.getStation(MN);
			if (station != null && StringUtil.isEmptyOrNull(station.getMn())) {
				PointSiteVO memStation = DpsService.stationCfgMap.get(message);
				if (memStation == null) {
					DpsService.stationCfgMap.put(MN, station);
				} else {
					memStation.sethInterval(station.gethInterval());
					memStation.setmInterval(station.getmInterval());
					memStation.setSystemType(station.getSystemType());
					memStation.setSupplement(station.isSupplement());
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		} finally {
			try {
				channel.basicAck(deliveryTag, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
