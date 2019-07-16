/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmRuleVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月20日上午8:25:40
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日上午8:25:40 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

/**
 * 类 名 称：AlarmRuleVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class AlarmRuleVO {

	// 规则ID
	private String ruleId;

	// 数据类型
	private String dataType;

	// 告警类型
	private String alarmType;

	// 告警码
	private String alarmCode;

	// 告警站点
	private String pointCode;

	// 告警MN
	private String MN;

	// 告警因子
	private String polluteCode;

	// 持续时间
	private int durTime = 5;

	// 表达式
	private String formula;

	// 告警值
	private String alarmValue;

	// 数据时间
	private Date dataTime;

	// 是否受规律停产影响1是0否
	private Integer isEffect = 0;

	// 是否产生告警
	private Integer isGenAlarm = 1;

	// 日志输出
	private boolean isOut = false;

	// 告警类型ID
	private Integer almTypeId;
	
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

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

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(String alarmValue) {
		this.alarmValue = alarmValue;
	}

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}

	public Integer getIsEffect() {
		return isEffect;
	}

	public void setIsEffect(Integer isEffect) {
		this.isEffect = isEffect;
	}

	public Integer getIsGenAlarm() {
		return isGenAlarm;
	}

	public void setIsGenAlarm(Integer isGenAlarm) {
		this.isGenAlarm = isGenAlarm;
	}

	public boolean isOut() {
		return isOut;
	}

	public void setOut(boolean isOut) {
		this.isOut = isOut;
	}

	public Integer getAlmTypeId() {
	    return almTypeId;
	}

	public void setAlmTypeId(Integer almTypeId) {
	    this.almTypeId = almTypeId;
	}


}
