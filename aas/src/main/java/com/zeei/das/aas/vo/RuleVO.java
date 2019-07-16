/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：RuleVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月20日上午11:35:22
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日上午11:35:22 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 类 名 称：RuleVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class RuleVO {

	// 站点ID
	private String pointCode;

	// 2011 数据规则
	private List<AlarmRuleVO> R2011 = new ArrayList<AlarmRuleVO>();

	// 2031 数据规则
	private List<AlarmRuleVO> R2031 = new ArrayList<AlarmRuleVO>();

	// 2051 数据规则
	private List<AlarmRuleVO> R2051 = new ArrayList<AlarmRuleVO>();

	// 2061 数据规则
	private List<AlarmRuleVO> R2061 = new ArrayList<AlarmRuleVO>();

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public List<AlarmRuleVO> getR2011() {
		return R2011;
	}

	public void setR2011(List<AlarmRuleVO> r2011) {
		R2011 = r2011;
	}

	public List<AlarmRuleVO> getR2031() {
		return R2031;
	}

	public void setR2031(List<AlarmRuleVO> r2031) {
		R2031 = r2031;
	}

	public List<AlarmRuleVO> getR2051() {
		return R2051;
	}

	public void setR2051(List<AlarmRuleVO> r2051) {
		R2051 = r2051;
	}

	public List<AlarmRuleVO> getR2061() {
		return R2061;
	}

	public void setR2061(List<AlarmRuleVO> r2061) {
		R2061 = r2061;
	}

}
