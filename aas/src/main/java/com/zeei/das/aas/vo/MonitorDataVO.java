/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：MonitorDataVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午8:52:56
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:52:56 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.NumberFormatUtil;

/**
 * 类 名 称：MonitorDataVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class MonitorDataVO implements Cloneable {

	// 测点代码
	private String pointCode;

	// 监测项目代码
	private String polluteCode;

	// 数据时间
	private String dataTime;

	// 最大值
	private Double maxValue;

	// 最小值
	private Double minValue;

	// 均值
	private Double dataValue;

	// 状态标识
	private String dataFlag = "N";

	// 审核值
	private Double auditValue;

	// 数据状态
	private String dataStatus;

	// 是否有效
	private Integer isValided = 1;

	// 是否是审核因子
	private Integer isAudit = 0;

	// 数据类型
	private String dataType;

	// 更新时间
	private String updateTime;

	// 保留小数位
	private Integer numPrecision;

	private String ST;

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

	public String getDataTime() {
		return dataTime;
	}
	
	public Date getDeDataTime() {
		return DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss");
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getDataValue() {
		return dataValue;
	}
	
	public Double getDataValueRounding() {
		
		if(dataValue==null){
			return null;
		}
		
		return NumberFormatUtil.formatByScale(dataValue, numPrecision);
	}

	public void setDataValue(Double dataValue) {
		this.dataValue = dataValue;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getDataFlag() {
		return dataFlag;
	}

	public void setDataFlag(String dataFlag) {
		this.dataFlag = dataFlag;
	}

	public String getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}

	public Integer getIsValided() {
		return isValided;
	}

	public void setIsValided(Integer isValided) {
		this.isValided = isValided;
	}

	public Double getAuditValue() {
		return auditValue;
	}

	public Double getAuditValueRounding() {
		
		if(auditValue==null){
			return null;
		}
		
		return NumberFormatUtil.formatByScale(auditValue, numPrecision);
	}

	public void setAuditValue(Double auditValue) {
		this.auditValue = auditValue;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}

	public Integer getNumPrecision() {
		return numPrecision;
	}

	public void setNumPrecision(Integer numPrecision) {
		this.numPrecision = numPrecision;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public Object clone() {
		MonitorDataVO o = null;
		try {
			o = (MonitorDataVO) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}

}
