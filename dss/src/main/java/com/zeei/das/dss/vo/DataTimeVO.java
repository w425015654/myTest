/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：DataTimeVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午9:02:05
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午9:02:05 创建文件
*
*/

package com.zeei.das.dss.vo;

import java.util.Date;

/**
 * 类 名 称：DataTimeVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class DataTimeVO {

	private String pointCode;

	private String MN;

	private String CN;

	private Date dataTime;

	// 分钟数据上报间隔
	private int rInterval = 5;

	// 分钟数据上报间隔
	private int mInterval = 5;

	// 小时数据上报间隔
	private int hInterval = 1;

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
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

	public int getrInterval() {
		return rInterval;
	}

	public void setrInterval(int rInterval) {
		this.rInterval = rInterval;
	}

	public int getmInterval() {
		return mInterval;
	}

	public void setmInterval(int mInterval) {
		this.mInterval = mInterval;
	}

	public int gethInterval() {
		return hInterval;
	}

	public void sethInterval(int hInterval) {
		this.hInterval = hInterval;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

}
