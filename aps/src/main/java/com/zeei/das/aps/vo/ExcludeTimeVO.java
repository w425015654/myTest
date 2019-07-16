/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：ExcludeTimeVO.java
* 包  名  称：com.zeei.das.aps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月31日上午10:58:37
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月31日上午10:58:37 创建文件
*
*/

package com.zeei.das.aps.vo;

import java.util.Date;

/**
 * 类 名 称：ExcludeTimeVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class ExcludeTimeVO {

	private int pointCode;

	private int week;

	private String startTime;

	private String endTime;

	private Date bDateTime;

	private Date eDateTime;

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Date getbDateTime() {
		return bDateTime;
	}

	public void setbDateTime(Date bDateTime) {
		this.bDateTime = bDateTime;
	}

	public Date geteDateTime() {
		return eDateTime;
	}

	public void seteDateTime(Date eDateTime) {
		this.eDateTime = eDateTime;
	}

}
