/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：Msg901xVO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午3:25:01
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午3:25:01 创建文件
*
*/

package com.zeei.das.cas.vo;

import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：Msg901xVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class CtlRecDetailVO {

	// 记录流水ID
	private Integer sid;

	// QN
	private String QN;

	private String pointCode;

	// 操作时间
	private String dataTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");

	// 执行结果
	private String result;

	// 用户id
	private String userId;

	// 备注
	private String remark;

	private String cmdStatus;

	private String codeID;

	// 返回参数字符串
	private String mrqStr;

	// 执行结果标识
	private String mrqFlag;

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getCmdStatus() {
		return cmdStatus;
	}

	public void setCmdStatus(String cmdStatus) {
		this.cmdStatus = cmdStatus;
	}

	public String getCodeID() {
		return codeID;
	}

	public void setCodeID(String codeID) {
		this.codeID = codeID;
	}

	public String getMrqStr() {
		return mrqStr;
	}

	public void setMrqStr(String mrqStr) {
		this.mrqStr = mrqStr;
	}

	public String getMrqFlag() {
		return mrqFlag;
	}

	public void setMrqFlag(String mrqFlag) {
		this.mrqFlag = mrqFlag;
	}

}
