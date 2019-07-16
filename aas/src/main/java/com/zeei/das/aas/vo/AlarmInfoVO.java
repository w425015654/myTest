/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmInfoVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：告警信息实体
* 创建日期：2017年4月20日上午8:25:22
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日上午8:25:22 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

/**
 * 类 名 称：AlarmInfoVO 类 描 述：告警信息实体 功能描述：告警信息实体 创建作者：quanhongsheng
 */

public class AlarmInfoVO {

	// 告警ID
	private String alarmId;

	// 告警码
	private String alarmCode;

	// 告警类型
	private String alarmType;

	// 告警站点
	private String pointCode;

	// 告警因子
	private String polluteCode;

	// 开始时间
	private Date startTime;

	// 结束时间
	private Date endTime;

	// 告警值
	private String alarmValue;

	// 是否入库
	private boolean isStorage;

	
	// 告警状态，true 标识告警，false标识消警
	private boolean isNewAlarm = true;

	// 数据类型
	private String dataType;

	// 1新产生告警， 4已消警
	private int alarmStatus;
	
	// 是否到到持续时长，0-未到，1-已到
	private int isDurtime = 0;

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}

	public boolean isStorage() {
		return isStorage;
	}

	public void setStorage(boolean isStorage) {
		this.isStorage = isStorage;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public boolean isNewAlarm() {
		return isNewAlarm;
	}

	public void setNewAlarm(boolean isNewAlarm) {
		this.isNewAlarm = isNewAlarm;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
	
	public int getIsDurtime() {
		return isDurtime;
	}

	public void setIsDurtime(int isDurtime) {
		this.isDurtime = isDurtime;
	}
}
