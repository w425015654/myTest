/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：RuleParamVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月27日下午2:18:03
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日下午2:18:03 创建文件
*
*/

package com.zeei.das.dps.vo;

/**
 * 类 名 称：RuleParamVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class RuleParamVO implements Comparable<RuleParamVO>{

	// 规则编码
	private String ruleCode;

	// 审核类型
	private String auditType;

	// 站点mn
	private String pointMN;

	// 站点mn
	private String pointCode;

	// 站点mn
	private String pointName;

	// 数据类型
	private String dataType;

	// 因子编码
	private String polluteCode;

	// 因子编码
	private String polluteName;

	// 规则参数
	private Object parameter;
	

	// 规则优先级
	private int priority = 0;

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	public String getPointMN() {
		return pointMN;
	}

	public void setPointMN(String pointMN) {
		this.pointMN = pointMN;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Object getParameter() {
		return parameter;
	}

	public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public String getAuditType() {
		return auditType;
	}

	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getPolluteName() {
		return polluteName;
	}

	public void setPolluteName(String polluteName) {
		this.polluteName = polluteName;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(RuleParamVO o) {
		int i = this.getPriority() - o.getPriority();// 先按照年龄排序
		return i;
	}

}
