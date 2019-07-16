/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：LostDataRecordVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月22日下午4:03:44
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月22日下午4:03:44 创建文件
*
*/

package com.zeei.das.dps.vo;

import java.util.Date;

/**
 * 类 名 称：LostDataRecordVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public class LostDataRecordVO {

	private String pointCode;

	private String MN;

	private String CN;

	private Date dataTime;

	private Date preDataTime;

	private int interval;

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

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}

	public Date getPreDataTime() {
		return preDataTime;
	}

	public void setPreDataTime(Date preDataTime) {
		this.preDataTime = preDataTime;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

}
