/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：IAQIRangeVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月19日下午3:34:15
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月19日下午3:34:15 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：IAQIRangeVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class IAQIRangeVO {

	// 因子Id
	private String polluteCode;
	// 数据类型
	private double dataType;
	// 浓度上限
	private double bphi;
	// 浓度下限
	private double bplo;
	// IAQI上限
	private double iaqihi;
	// IAQI下限
	private double iaqilo;

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public double getDataType() {
		return dataType;
	}

	public void setDataType(double dataType) {
		this.dataType = dataType;
	}

	public double getBphi() {
		return bphi;
	}

	public void setBphi(double bphi) {
		this.bphi = bphi;
	}

	public double getBplo() {
		return bplo;
	}

	public void setBplo(double bplo) {
		this.bplo = bplo;
	}

	public double getIaqihi() {
		return iaqihi;
	}

	public void setIaqihi(double iaqihi) {
		this.iaqihi = iaqihi;
	}

	public double getIaqilo() {
		return iaqilo;
	}

	public void setIaqilo(double iaqilo) {
		this.iaqilo = iaqilo;
	}

}
