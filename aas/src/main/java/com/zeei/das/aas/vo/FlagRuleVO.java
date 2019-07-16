/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：FlagRuleVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年12月15日上午8:54:01
* 
* 修改历史
* 1.0 luoxianglin 2017年12月15日上午8:54:01 创建文件
*
*/

package com.zeei.das.aas.vo;

import com.zeei.das.common.constants.DataType;

/**
 * 类型名称：FlagRuleVO 类型描述：TODO 请修改类型描述 功能描述：TODO 请修改功能描述 创建作者：quan.hongsheng
 *
 */

public class FlagRuleVO {

	// 数据类型
	private String dataType = DataType.RTDATA;

	// 告警类型
	private String alarmType = "12";

	// 告警码
	private String alarmCode = "88881";

	// 持续时间
	private int durTime = 0;

	// flag 标识吗
	private String flagCode = "F";

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public int getDurTime() {
		return durTime;
	}

	public void setDurTime(int durTime) {
		this.durTime = durTime;
	}

	public String getFlagCode() {
		return flagCode;
	}

	public void setFlagCode(String flagCode) {
		this.flagCode = flagCode;
	}
}
