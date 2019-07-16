/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：QNUtil.java
* 包  名  称：com.zeei.das.com.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日下午3:29:07
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日下午3:29:07 创建文件
*
*/

package com.zeei.das.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 类 名 称：QNUtil 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class QNUtil {

	private static AtomicLong sequence = new AtomicLong(0);

	/**
	 * 
	 * getQN:TODO 获取QN
	 *
	 * @param type
	 *            类型 1表示补数，2表示其他返控
	 * @return String
	 */
	public static String getQN(String type) {

		// 类型 1表示补数，2表示其他返控

		long random = sequence.incrementAndGet();
		String timeStr = DateUtil.getCurrentDate("yyyyMMddHHmmssSSS");
		return String.format("%s%s%04d", type, timeStr, random % 10000);
	}

}
