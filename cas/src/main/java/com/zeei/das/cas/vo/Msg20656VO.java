/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：Msg2076VO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午3:24:31
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午3:24:31 创建文件
*
*/

package com.zeei.das.cas.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/**
 * @类 名 称：Msg2076VO
 * @类 描 述：
 * @功能描述： 零点核查2065,跨度核查2066相关数据
 * @创建作者：zhanghu
 */

public class Msg20656VO {

	// 测点代码
	private int pointCode;

	// 因子編碼
	private String ParamID;

	// 数据时间
	private String dataTime;

	// 数据标识
	private String Flag;

	// 跨度核查数据
	private Double Check;

	// 标准样浓度
	private Double StandardValue;

	// 仪器跨度值
	private Double SpanValue;

	// 数据是否有效
	private Integer isValided = 1;
	
	// 数据类型
	private Integer dataType = 0;
	
	//审核命令时间
	private  String   auditTime;
	
	// 数据是否审核
	private Integer dataStatus = 0;

	private String updateTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
	
	// 命令唯一标识
	private String qn;
	
	// 核查数据数量
	private Integer dataSum = 0;
	

	
	
	public String getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getDataSum() {
		return dataSum;
	}

	public void setDataSum(Integer dataSum) {
		this.dataSum = dataSum;
	}

	public String getQn() {
		return qn;
	}

	public void setQn(String qn) {
		this.qn = qn;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public boolean valid() {
		if (ParamID == null || ParamID.length() == 0) {
			return false;
		}
		return true;
	}

	public String getParamID() {
		return ParamID;
	}

	public void setParamID(String paramID) {
		ParamID = paramID;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
		isValided = "N".equals(flag) ? 1 : 0;
	}

	public Double getCheck() {
		return Check;
	}

	public void setCheck(Double check) {
		Check = check;
	}

	public Double getStandardValue() {
		return StandardValue;
	}

	public void setStandardValue(Double standardValue) {
		StandardValue = standardValue;
	}

	public Double getSpanValue() {
		return SpanValue;
	}

	public void setSpanValue(Double spanValue) {
		SpanValue = spanValue;
	}

	public Integer getIsValided() {
		return isValided;
	}

	public void setIsValided(Integer isValided) {
		this.isValided = isValided;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	
	public Date getDeDataTime() {
		return DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss");
	}

}
