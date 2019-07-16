/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ProcessMonitorUtil.java
* 包  名  称：com.zeei.das.cgs.common.utils
* 文件描述：TODO 进程监控工具
* 创建日期：2017年4月19日上午8:58:46
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月19日上午8:58:46 创建文件
*
*/

package com.zeei.das.common.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类 名 称：ProcessMonitorUtil 类 描 述：TODO 进程监控工具 功能描述：TODO 进程监控工具
 * 创建作者：quanhongsheng
 */

public class ProcessMonitorUtil {

	public static Map<String, Object> getResources() throws Exception {

		Map<String, Object> resources = new HashMap<String, Object>();
		String pid = getCurrentPid();
		resources.put("pid", pid);
		resources.put("time", new Date());

		// 已使用的物理内存
		long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
		resources.put("mem", usedMemory);

		return resources;
	}

	/**
	 * 
	 * getCurrentPid:通过获取当前运行主机的pidName，截取获得他的pid
	 *
	 * @return
	 * @throws Exception
	 *             String
	 */
	public static String getCurrentPid() throws Exception {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String pidName = runtime.getName();
		String pid = pidName.substring(0, pidName.indexOf("@"));
		return pid;
	}

}
