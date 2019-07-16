/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：T3020RuleVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年12月15日上午8:53:10
* 
* 修改历史
* 1.0 luoxianglin 2017年12月15日上午8:53:10 创建文件
*
*/

package com.zeei.das.aas.vo;

/**
 * 类型名称：T3020RuleVO 类型描述：TODO 请修改类型描述 功能描述：TODO 请修改功能描述 创建作者：quan.hongsheng
 *
 */

public class T3020RuleVO {

	// 告警码
	private String alarmCode = "88882";

	// 告警类型
	private String alarmType = "11";

	// 持续时间
	private int durTime = 0;

	// 告警标识
	private String formula = "1";

	// 告警状态吗
	private String statusCode = "i12003";

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

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

}
