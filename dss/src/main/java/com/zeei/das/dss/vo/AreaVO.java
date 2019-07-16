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

public class AreaVO {

	// 区域编码
	private String areaCode;

	// 区域父编码
	private String pCode;

	// 区域级别
	private Integer level;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getpCode() {
		return pCode;
	}

	public void setpCode(String pCode) {
		this.pCode = pCode;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

}
