/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：TimeCounter.java
* 包  名  称：com.zeei.das.com.utils
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月24日上午9:15:24
* 
* 修改历史
* 1.0 zhou.yongbo 2017年5月24日上午9:15:24 创建文件
*
*/

package com.zeei.das.common.utils;

/**
 * 类  名  称：TimeCounter
 * 类  描  述：用于性能分析的计时器
 * 功能描述：TODO 请修改功能描述
 * 创建作者：zhou.yongbo
 */

public class TimeCounter {
	// 其实时间
	private long start;
	
	private long last_take;
	
	public void start(){
		start = System.currentTimeMillis();
		last_take = start;
	}
	
	// 两次条用take之间的时间间隔，第一次调用时为从start
	public long take(){
		long cur = System.currentTimeMillis();
		long t = cur - last_take;
		last_take = cur;
		return t;
	}
	
	public long total(){
		return System.currentTimeMillis() - start;
	}

}
