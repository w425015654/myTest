/** 
* Copyright (C) 2012-2019 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：AvgSpeedVO.java
* 包  名  称：com.zeei.das.cgs.vo
* 文件描述：
* 创建日期：2019年5月15日上午8:42:59
* 
* 修改历史
* 1.0 lian.wei 2019年5月15日上午8:42:59 创建文件
*
*/

package com.zeei.das.cgs.vo;

import io.netty.channel.Channel;

/**
 * @类型名称：AvgSpeedVO @类型描述： @功能描述：
 * @创建作者：lian.wei
 */

public class AvgSpeedVO implements Comparable {
	// 因子
	private String pollCode;

	// 值
	private String value;

	// 时间
	private String date;

	/**
	 * 获取pollCode的值
	 * 
	 * @return 返回pollCode的值
	 */
	public String getPollCode() {
		return pollCode;
	}

	/**
	 * 设置pollCode值
	 * 
	 * @param pollCode
	 */
	public void setPollCode(String pollCode) {
		this.pollCode = pollCode;
	}

	/**
	 * 获取value的值
	 * 
	 * @return 返回value的值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置value值
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获取date的值
	 * 
	 * @return 返回date的值
	 */
	public String getDate() {
		return date;
	}

	/**
	 * 设置date值
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	// 按时间排序
	public int compareTo(Object o) { 
		AvgSpeedVO s = (AvgSpeedVO)o; 
		int result = date.compareTo(s.date); 
		return result; 
	}
}
