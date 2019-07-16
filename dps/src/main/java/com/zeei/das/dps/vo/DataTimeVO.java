/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：DataTimeVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午9:58:25
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午9:58:25 创建文件
*
*/

package com.zeei.das.dps.vo;

import java.util.Date;

/**
 * 类 名 称：DataTimeVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class DataTimeVO {

	private String pointCode;

	private Date rDataTime;

	private Date mDataTime;

	private Date hDataTime;

	private Date dDataTime;

	private Date upTime;

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public Date getrDataTime() {
		return rDataTime;
	}

	public void setrDataTime(Date rDataTime) {
		this.rDataTime = rDataTime;
	}

	public Date getmDataTime() {
		return mDataTime;
	}

	public void setmDataTime(Date mDataTime) {
		this.mDataTime = mDataTime;
	}

	public Date gethDataTime() {
		return hDataTime;
	}

	public void sethDataTime(Date hDataTime) {
		this.hDataTime = hDataTime;
	}

	public Date getdDataTime() {
		return dDataTime;
	}

	public void setdDataTime(Date dDataTime) {
		this.dDataTime = dDataTime;
	}

	public Date getUpTime() {
		return upTime;
	}

	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

}
