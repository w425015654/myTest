/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：StationCfgHandler.java
* 包  名  称：com.zeei.das.cgs.mq
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月15日下午1:21:17
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月15日下午1:21:17 创建文件
*
*/

package com.zeei.das.cgs.mq;

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
import com.zeei.das.cgs.CgsService;
import com.zeei.das.cgs.service.ConfigService;
import com.zeei.das.cgs.vo.StationCfgVO;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.LoggerUtil;

import io.netty.util.internal.StringUtil;

/**
 * 类 名 称：StationCfgHandler 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Component("stationCfgHandler")
public class StationCfgHandler implements ChannelAwareMessageListener {

	@Autowired
	Publish publish;
	
	@Autowired
	CgsService cgsService;

	@Autowired
	ConfigService configService;

	private static Logger logger = LoggerFactory.getLogger(StationCfgHandler.class);
	
	public static final String CALCULA_REFRESH = "Calculation-refresh";

	/**
	 * 
	 * cfgNoticeHandler:消费站点配置更新消息 void
	 */

	// 参数中使用@Header获取mesage
	@Override
	public void onMessage(Message message, Channel channel) {

		long deliveryTag = message.getMessageProperties().getDeliveryTag();

		try {

			String msg = new String(message.getBody(), "UTF-8");

			JSONObject obj = JSON.parseObject(msg);
			if (obj == null || StringUtil.isNullOrEmpty(obj.getString("mN"))) {

				return;
			}
			String MN = obj.getString("mN");
			
			//刷新公式配置信息
			if(CALCULA_REFRESH.equals(MN) && CALCULA_REFRESH.equals(obj.getString("msgBody"))){
				cgsService.initFormulaMap(1);
				return;
			}

			StationCfgVO temp = configService.getStationCfg(MN);
			if (temp != null) {

				StationCfgVO cfg = CgsService.stationMap.get(MN);

				if (cfg == null) {
					CgsService.stationMap.put(MN, temp);
				} else {
					cfg.setID(temp.getID());
					cfg.setMsgAck(temp.isMsgAck());
					cfg.setPwd(temp.getPwd());
					cfg.setVerifyPasswd(temp.isVerifyPasswd());
					cfg.setTimecal(temp.isTimecal());
				}

				logger.info("更新站点信息：" + msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		} finally {
			try {
				if (channel != null && channel.isOpen()) {
				channel.basicAck(deliveryTag, false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
