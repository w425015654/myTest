/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：lts
* 文件名称：AuditLogVO.java
* 包  名  称：com.zeei.das.lts.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月8日上午9:59:36
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月8日上午9:59:36 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：AuditLogVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class AuditLogVO {

	// 审核ID
	private String auditId;

	// 数据时间
	private Date dataTime;
	// 测点代码
	private String pointCode;

	// 污染物代码
	private String polluteCode;

	// 原始值
	private Double dataRT;

	// 审核类型
	private Integer auditType = 0;

	// 审核时间
	private Date auditTime = DateUtil.getCurrentDate();

	// 审核值
	private Double auditData;

	// 审核人
	private String auditPerson = "1";

	// 备注
	private String remark;

	public String getAuditId() {
		return auditId;
	}

	public void setAuditId(String auditId) {
		this.auditId = auditId;
	}

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
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

	public Double getDataRT() {
		return dataRT;
	}

	public void setDataRT(Double dataRT) {
		this.dataRT = dataRT;
	}

	public Integer getAuditType() {
		return auditType;
	}

	public void setAuditType(Integer auditType) {
		this.auditType = auditType;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Double getAuditData() {
		return auditData;
	}

	public void setAuditData(Double auditData) {
		this.auditData = auditData;
	}

	public String getAuditPerson() {
		return auditPerson;
	}

	public void setAuditPerson(String auditPerson) {
		this.auditPerson = auditPerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
