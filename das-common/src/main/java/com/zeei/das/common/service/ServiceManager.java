/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：mds
* 文件名称：ServiceManager.java
* 包  名  称：com.zeei.das.mds.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月24日下午3:03:20
* 
* 修改历史
* 1.0 zhouyongbo 2017年5月24日下午3:03:20 创建文件
*
*/

package com.zeei.das.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类 名 称：ServiceManager 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：zhouyongbo
 */

public class ServiceManager {

	private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);

	// 根据配置文件的命令启动服务
	public static void startService(String serviceName) {
		if (serviceName == null) {
			return;
		}
		if (isStarted(serviceName)) {
			logger.error(String.format("%s服务已启动。", serviceName));
			return;
		}

		Runtime runtime = Runtime.getRuntime();
		String startCmd = String.format("cmd /c sc start %s", serviceName);
		logger.info("执行命令: " + startCmd);
		try {
			Process process = runtime.exec(startCmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "gb2312"));
			String inline;
			while ((inline = br.readLine()) != null) {
				logger.info(inline);
			}
			br.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public static boolean isStarted(String serviceName) {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec(String.format("cmd /c sc query %s", serviceName));
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), "gb2312"));
			String inline;
			while ((inline = br.readLine()) != null) {
				logger.info(inline);
				String[] segs = inline.split(":");
				if (segs != null && segs.length == 2 && segs[0].trim().equalsIgnoreCase("STATE")) {
					if (segs[1].trim().contains("STOPPED")) {
						return false;
					} else if (segs[1].trim().contains("RUNNING")) {
						return true;
					}
				}
			}
			br.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.error(String.format("%s的状态未知，按未启动处理", serviceName));
		return false;
	}
}
