/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：lts
* 文件名称：Start.java
* 包  名  称：com.zeei.das.lts
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月8日上午8:06:58
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月8日上午8:06:58 创建文件
*
*/

package com.zeei.das.dss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.zeei.das.common.utils.LoadConfigUtil;

/**
 * 类 名 称：Start 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class Start {

	private static Logger logger = LoggerFactory.getLogger(Start.class);

	public static void main(String args[]) {
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
		}

	}
}