/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeRuleVO.java
* 包  名  称：com.zeei.das.aps.vo
* 文件描述：通知规则实体
* 创建日期：2017年5月2日上午8:21:59
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午8:21:59 创建文件
*
*/

package com.zeei.das.aps.vo;

/**
 * 类 名 称：NoticeRuleVO 类 描 述：通知规则实体 功能描述：通知规则实体 创建作者：quanhongsheng
 */

public class NoticeRuleVO {

	// 通知规则id
	private String ruleId;

	// 通知规则名称
	private String ruleName;

	// 通知站点MN
	private String pointCode;

	// 告警类型
	private String alarmCode;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCodes(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

}
