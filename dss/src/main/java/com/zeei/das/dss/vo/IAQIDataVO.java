/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：IAQIDataVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月19日下午4:51:29
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月19日下午4:51:29 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：IAQIDataVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class IAQIDataVO {

	// 因子Id
	private String polluteCode;

	// 污染物名称
	private String polluteName;

	// 数据类型
	private Integer dataType;

	// iaqi的值
	private Integer iaqi;

	// 检测值
	private Double dataValue;

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getIaqi() {
		return iaqi;
	}

	public void setIaqi(Integer iaqi) {
		this.iaqi = iaqi;
	}

	public String getPolluteName() {
		return polluteName;
	}

	public void setPolluteName(String polluteName) {
		this.polluteName = polluteName;
	}

	public Double getDataValue() {
		return dataValue;
	}

	public void setDataValue(Double dataValue) {
		this.dataValue = dataValue;
	}

}
