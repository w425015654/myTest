/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：das.common
* 文件名称：modelUtil.java
* 包  名  称：com.zeei.das.common.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月13日下午4:00:51
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月13日下午4:00:51 创建文件
*
*/

package com.zeei.das.common.utils;

import org.springframework.stereotype.Component;

/**
 * 类 名 称：modelUtil 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Component
public class ModelUtil {

	private static String srvCode;

	private static Integer reportCycle = 5;

	public static String getSrvCode() {
		return srvCode;
	}

	public static void setSrvCode(String srvCode) {
		ModelUtil.srvCode = srvCode;
	}

	public static Integer getReportCycle() {
		return reportCycle;
	}

	public static void setReportCycle(Integer reportCycle) {
		ModelUtil.reportCycle = reportCycle;
	}

}
