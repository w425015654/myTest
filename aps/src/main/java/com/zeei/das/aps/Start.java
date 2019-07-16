/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：Start.java
* 包  名  称：com.zeei.das.cgs
* 文件描述：服务启动类
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.aps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.zeei.das.aps.alarm.GkAlarmHandler;
import com.zeei.das.aps.mq.Publish;
import com.zeei.das.common.constants.Constant;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.utils.BeanUtil;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.LoggerUtil;

/**
 * 类 名 称：Start 类 描 述：启动服务 功能描述：初始化配置，启动服务
 */

public class Start {

	private static Logger logger = LoggerFactory.getLogger(Start.class);

	public static void main(String args[]) throws Exception {

		try {
			// 加载Spring xml
			LoadConfigUtil.loadConfig();

		} catch (Exception e) {
			logger.info("',e");
			// 异常重启
			logger.info("服务异常重启成！");
			LoadConfigUtil.refresh();
		}
	}

}

@Component
class StartEvent implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(StartEvent.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (event.getApplicationContext().getParent() == null) {

			logger.info("启动业务程序！");

			BeanUtil.getBean("gkAlarmHandler", GkAlarmHandler.class).alarmHandler();
			// BeanUtil.getBean("scanAlarmHandler",
			// ScanAlarmHandler.class).alarmHandler();

			Publish publish = BeanUtil.getBean("publish", Publish.class);

			publish.send(Constant.MQ_QUEUE_LOGS, LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, "服务启动完成！"));
		}

	}
}
