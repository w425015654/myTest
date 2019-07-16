package com.zeei.das.cas.vo;

import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：HourAuditVO
* @类型描述：
* @功能描述：针对实际水样比对，集成干预核查，非集成干预核查
* @创建作者：zhanghu
*
*/
public class HourAuditVO {
	
	//测点代码
	private  Integer  pointCode;
	
	//监测项目代码
	private  String   polluteCode;
	
	//日期
	private  String   dataTime;

	//审核命令时间
	private  String   auditTime;
	
	//原始数据的日期时间
	private  String   oriTime;
	
	//数据值
	private  Double   dataValue;
	
	//数据类型，实际水样比对1，集成干预核查2，非集成干预核查3
	private  Integer  dataType;
	
	//更新时间
	private  String  updateTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
	
	//状态标识
	private  String  dataFlag;
	
	//唯一标识qn
	private  String  qn;
	
	//状态标识
	private  Integer  dataStatus;
	
	
	

	public String getQn() {
		return qn;
	}

	public void setQn(String qn) {
		this.qn = qn;
	}

	public Integer getPointCode() {
		return pointCode;
	}

	public void setPointCode(Integer pointCode) {
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

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}

	public String getOriTime() {
		return oriTime;
	}

	public void setOriTime(String oriTime) {
		this.oriTime = oriTime;
	}

	public Double getDataValue() {
		return dataValue;
	}

	public void setDataValue(Double dataValue) {
		this.dataValue = dataValue;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
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

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}
	
	
}
