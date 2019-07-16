/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：PolluteVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月24日下午5:14:50
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月24日下午5:14:50 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：PolluteVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class PolluteVO {

	private String polluteCode;

	private String polluteName;
	
	private String ST;

	private int numPrecision;
	
	private Double smaxValue;

	private String polluteClass;
	
	
	public Double getSmaxValue() {
		return smaxValue;
	}

	public void setSmaxValue(Double smaxValue) {
		this.smaxValue = smaxValue;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public String getPolluteName() {
		return polluteName;
	}

	public void setPolluteName(String polluteName) {
		this.polluteName = polluteName;
	}

	public int getNumPrecision() {
		return numPrecision;
	}

	public void setNumPrecision(int numPrecision) {
		this.numPrecision = numPrecision;
	}

	public String getPolluteClass() {
		return polluteClass;
	}

	public void setPolluteClass(String polluteClass) {
		this.polluteClass = polluteClass;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}
	
	

}
