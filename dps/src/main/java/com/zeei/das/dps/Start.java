/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：DPS
* 文件名称：Starter.java
* 包  名  称：com.zeei.das.dps
* 文件描述：T2011 T2051 T2061 T2031数据入库服务入口
* 创建日期：2017年4月26日上午10:13:23
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月26日上午10:13:23 创建文件
*
*/

package com.zeei.das.dps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.zeei.das.common.utils.BeanUtil;
import com.zeei.das.common.utils.LoadConfigUtil;
import com.zeei.das.common.utils.StringUtil;
import com.zeei.das.dps.complement.CompCmdJob;
import com.zeei.das.dps.complement.LatestDataTimeJob;

/**
 * 类 名 称：Starter 类 描 述：启动服务 功能描述：根据配置文件设置的参数启动相应服务
 */

@Component
class StartEvent implements ApplicationListener<ContextRefreshedEvent> {

	private static Logger logger = LoggerFactory.getLogger(Start.class);
	// 是否进行自动补数
	private static String complement = "0";

	static {
		if (!StringUtil.isEmptyOrNull(DpsService.cfgMap.get("complement"))) {
			complement = DpsService.cfgMap.get("complement");
		}
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (event.getApplicationContext().getParent() == null) {

			logger.info("启动业务程序！");
			BeanUtil.getBean("latestDataTimeJob", LatestDataTimeJob.class).jobHandler();

			if ("1".equals(complement)) {
				BeanUtil.getBean("compCmdJob", CompCmdJob.class).jobHandler();
			}
		}

	}
}

public class Start {

	private static final Logger logger = LoggerFactory.getLogger(Start.class);

	public static void main(String[] args) {

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
