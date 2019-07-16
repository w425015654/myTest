/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AreaVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年8月15日上午10:33:55
* 
* 修改历史
* 1.0 quanhongsheng 2017年8月15日上午10:33:55 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：AreaVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class O38stdVO {

	// 站点编码
	private String pointCode;
	
	// 因子编码
	private String polluteCode;

	// 标准值
	private Double stdValue;

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public Double getStdValue() {
		return stdValue;
	}

	public void setStdValue(Double stdValue) {
		this.stdValue = stdValue;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}
	
	

}
