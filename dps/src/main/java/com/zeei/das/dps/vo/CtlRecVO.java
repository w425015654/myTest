/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：CtlRecVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午11:16:37
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午11:16:37 创建文件
*
*/

package com.zeei.das.dps.vo;

import java.util.Date;

/**
 * 类 名 称：CtlRecVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class CtlRecVO {

	// QN
	private String QN;

	// 标准测点代码
	private String MN;

	// 时间
	private Date dataTime;

	private String msg;

	// 用户id
	private String userId;

	// 命令状态
	private String cmdStatus = "1";

	// 备注
	private String remark;

	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCmdStatus() {
		return cmdStatus;
	}

	public void setCmdStatus(String cmdStatus) {
		this.cmdStatus = cmdStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
