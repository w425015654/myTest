/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：ZeroNegativeVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月27日上午10:30:21
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月27日上午10:30:21 创建文件
*
*/

package com.zeei.das.dps.vo;

/**
 * 类 名 称：ZeroNegativeVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class ZeroNegativeVO {

	private String ruleId;

	private String ruleCode;

	private String MN;

	private String pointCode;

	private String polluteCode;

	private Double smaxValue;

	private Double sminValue;

	private Double auditValue;

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Double getSmaxValue() {
		return smaxValue;
	}

	public void setSmaxValue(Double smaxValue) {
		this.smaxValue = smaxValue;
	}

	public Double getSminValue() {
		return sminValue;
	}

	public void setSminValue(Double sminValue) {
		this.sminValue = sminValue;
	}

	public Double getAuditValue() {
		return auditValue;
	}

	public void setAuditValue(Double auditValue) {
		this.auditValue = auditValue;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

}
