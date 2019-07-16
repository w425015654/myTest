package com.zeei.das.cas.vo;

import java.util.Date;

public class Msg2070VO {

	private  Integer   pikd;
	//测点id
	private  Integer  pointCode;
	//命令时间
	private  Date   dataTime;
	//指控类型
	private  String   cmdType;
	//污染物类型
	private  String   polluteCode;
	//命令序列号
	private  String   qn;
	//目标值
	private  Float   tagval;
	//相应值
	private  Float   resval; 
	//偏差值
	private  Float   deval;
	//告警限制
	private  Float   alarmVal;
	//控制限制
	private  Float   ctrlVal;
	//质控来源
	private  String  qcSource; 
	//质控结果
	private  String  qcResult;
	
	
	public Integer getPikd() {
		return pikd;
	}
	public void setPikd(Integer pikd) {
		this.pikd = pikd;
	}
	public Integer getPointCode() {
		return pointCode;
	}
	public void setPointCode(Integer pointCode) {
		this.pointCode = pointCode;
	}
	public Date getDataTime() {
		return dataTime;
	}
	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}
	public String getCmdType() {
		return cmdType;
	}

	public void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}
	public String getPolluteCode() {
		return polluteCode;
	}
	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}
	public String getQn() {
		return qn;
	}
	public void setQn(String qn) {
		this.qn = qn;
	}
	public Float getTagval() {
		return tagval;
	}
	public void setTagval(Float tagval) {
		this.tagval = tagval;
	}
	public Float getResval() {
		return resval;
	}
	public void setResval(Float resval) {
		this.resval = resval;
	}
	public Float getDeval() {
		return deval;
	}
	public void setDeval(Float deval) {
		this.deval = deval;
	}
	public Float getAlarmVal() {
		return alarmVal;
	}
	public void setAlarmVal(Float alarmVal) {
		this.alarmVal = alarmVal;
	}
	public Float getCtrlVal() {
		return ctrlVal;
	}
	public void setCtrlVal(Float ctrlVal) {
		this.ctrlVal = ctrlVal;
	}
	public String getQcSource() {
		return qcSource;
	}
	public void setQcSource(String qcSource) {
		this.qcSource = qcSource;
	}
	public String getQcResult() {
		return qcResult;
	}
	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
	}
	
	
}
