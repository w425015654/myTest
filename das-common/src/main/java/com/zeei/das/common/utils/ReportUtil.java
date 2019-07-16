/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ReportUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：服务状态报告工具类
* 创建日期：2017年4月19日下午4:42:47
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月19日下午4:42:47 创建文件
*
*/

package com.zeei.das.common.utils;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.common.constants.LogType;
import com.zeei.das.common.mq.BasePublish;

/**
 * 类 名 称：ReportUtil 类 描 述：TODO 服务状态报告工具类 功能描述：TODO 服务状态报告工具类 创建作者：quanhongsheng
 */

@Component("reportUtil")
public class ReportUtil {

	@Autowired
	BasePublish basePublish;

	/**
	 * 
	 * report:TODO 发送状态数据 void
	 */
	public void report() {

		// 服务心跳上报周期
		int reportCycle = ModelUtil.getReportCycle();
		// 服务编码
		String srvCode = ModelUtil.getSrvCode();

		if (DateUtil.getCurrentMin() % reportCycle == 0) {
			try {
				Map<String, Object> resources = ProcessMonitorUtil.getResources();
				resources.put("code", srvCode);
				String json = JSON.toJSONStringWithDateFormat(resources, "yyyyMMddHHmmss",
						SerializerFeature.WriteDateUseDateFormat);
				basePublish.publishReport(json);

			} catch (Exception e) {
				e.printStackTrace();
				basePublish.publishLog(LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
			}
		}
	}

	/**
	 * 
	 * cycleReport:周期性上报心跳数据 void
	 */
	public void cycleReport() {

		try {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						report();
					} catch (Exception e) {
						e.printStackTrace();
						basePublish.publishLog(LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
					}
				}
			};
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
			// 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
			service.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.MINUTES);
		} catch (Exception e) {
			
			e.printStackTrace();			
			basePublish.publishLog(LoggerUtil.getLogInfo(LogType.LOG_TYPE_EXCEPTION, e.toString()));
		}
	}
}
