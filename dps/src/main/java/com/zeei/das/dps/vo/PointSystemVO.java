/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：DPS
* 文件名称：PointSystemVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：测点系统类型封装
* 创建日期：2017年4月20日下午2:16:42
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月20日下午2:16:42 创建文件
*
*/

package com.zeei.das.dps.vo;

/**
 * 类  名  称：PointSystemVO
 * 类  描  述：T_DIC_POINTSYSTEM
 * 功能描述：T_DIC_POINTSYSTEM表的数据封装
 * 创建作者：zhou.yongbo
 */

/**
 * 类 名 称：PointSystemVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：zhou.yongbo
 */
public class PointSystemVO {
	// 系统类型
	private String systemType;
	// 系统名称
	private String systemName;
	// 实时数据表
	private String realtimeTable;
	// 分钟数据表
	private String minuteTable;
	// 小时数据表
	private String hourTable;
	// 天数据
	private String dayTable;
	// 测站名称
	private String stationName;
	// 测点名称
	private String pointName;

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getRealtimeTable() {
		return realtimeTable;
	}

	public void setRealtimeTable(String realtimeTable) {
		this.realtimeTable = realtimeTable;
	}

	public String getMinuteTable() {
		return minuteTable;
	}

	public void setMinuteTable(String minuteTable) {
		this.minuteTable = minuteTable;
	}

	public String getHourTable() {
		return hourTable;
	}

	public void setHourTable(String hourTable) {
		this.hourTable = hourTable;
	}

	public String getDayTable() {
		return dayTable;
	}

	public void setDayTable(String dayTable) {
		this.dayTable = dayTable;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

}
