/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：DPS
* 文件名称：NewPointCode.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：新测点VO
* 创建日期：2017年4月25日上午11:16:33
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月25日上午11:16:33 创建文件
*
*/

package com.zeei.das.dps.vo;

/**
 * 类  名  称：NewPointCode
 * 类  描  述：新测点VO
 * 功能描述：新建测点，pontCode对应POINTCODE字段，mn对应STDPOINTCODE
 * 创建作者：zhou.yongbo
 */

public class NewPointCode {
	private int pointCode;
	private String mn;
	private int systemType;
	// yyyy-MM-dd HH:mm:ss
	private String createTime;
	
	public int getPointCode() {
		return pointCode;
	}
	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}
	public String getMn() {
		return mn;
	}
	public void setMn(String mn) {
		this.mn = mn;
	}
	public int getSystemType() {
		return systemType;
	}
	public void setSystemType(int systemType) {
		this.systemType = systemType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}	
}
