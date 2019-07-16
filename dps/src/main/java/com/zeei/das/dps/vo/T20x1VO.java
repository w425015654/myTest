/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：DPS
* 文件名称：T2011VO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：T2011 T2051 T2061 T2031消息的封装
* 创建日期：2017年4月20日上午10:09:52
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月20日上午10:09:52 创建文件
*
*/

package com.zeei.das.dps.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.common.utils.StringUtil;

/**
 * 类 名 称：T20x1VO 功能描述：T2051, T2061, T2031消息的封装 创建作者：zhou.yongbo
 */

public class T20x1VO {
	// 测点代码
	private int pointCode;
	// 监测项目代码
	private String polluteCode;

	private String dataTime;

	private String samplingTime;

	private String updateTime;

	private Double dataValue;

	private Double auditValue;

	private Double minValue;

	private Double maxValue;

	private String dataFlag;
	
	private String originalFlag;

	private String dataStatus;

	private Integer isValided;
	
	//数据存疑标识
	private Integer doubtful;

	private String dataType;

	public String getSamplingTime() {

		if (StringUtil.isEmptyOrNull(samplingTime)) {
			return dataTime;
		}

		return samplingTime;
	}

	public void setSamplingTime(String samplingTime) {
		this.samplingTime = samplingTime;
	}

	public T20x1VO() {
	}

	public T20x1VO(T20x1Message.MessageBody.Parameter para) {
		polluteCode = para.getParamID();
		auditValue = para.getRound();
		minValue = para.getMin();
		maxValue = para.getMax();
		dataFlag = StringUtils.isEmpty(para.getDataFlag())?para.getFlag():para.getDataFlag();
		originalFlag = para.getFlag();
		dataStatus = para.getDataStatus();
		isValided = para.getIsValided();
		doubtful = para.getDoubtful();
		if (para.getRtd() != null) {
			dataValue = para.getRtd();
		} else {
			dataValue = para.getAvg();
		}
	}

	// 主键相同，即认为是同一条数据
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof T20x1VO) {
			T20x1VO vo = (T20x1VO) o;
			return pointCode == vo.pointCode && polluteCode.equals(vo.polluteCode) && dataTime.equals(vo.dataTime);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pointCode + polluteCode.hashCode() + dataTime.hashCode();
	}

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Date getDeDataTime() {
		return DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss");
	}
	
	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Double getDataValue() {
		return dataValue;
	}

	public void setDataValue(Double dataValue) {
		this.dataValue = dataValue;
	}

	public Double getAuditValue() {
		return auditValue;
	}

	public void setAuditValue(Double auditValue) {
		this.auditValue = auditValue;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public String getOriginalFlag() {
		return originalFlag;
	}

	public void setOriginalFlag(String originalFlag) {
		this.originalFlag = originalFlag;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getDoubtful() {
		return doubtful;
	}

	public void setDoubtful(Integer doubtful) {
		this.doubtful = doubtful;
	}
	
	
}
