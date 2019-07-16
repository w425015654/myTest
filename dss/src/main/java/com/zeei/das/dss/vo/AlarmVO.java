/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AlarmVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月21日上午11:31:55
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月21日上午11:31:55 创建文件
*
*/

package com.zeei.das.dss.vo;

import java.util.Date;

/**
 * 类 名 称：AlarmVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class AlarmVO {

	private String alarmCode;

	private String alarmType;

	private String dataType;

	private String pointCode;

	private Date beginTime;

	private Date endTime;

	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
