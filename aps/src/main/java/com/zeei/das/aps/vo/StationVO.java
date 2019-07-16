/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：StationVO.java
* 包  名  称：com.zeei.das.aps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月28日下午1:18:54
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月28日下午1:18:54 创建文件
*
*/

package com.zeei.das.aps.vo;

/**
 * 类 名 称：StationVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class StationVO {

	private String pointCode;

	private String pointName;

	private String enterprise;

	private String MN;

	private String ST;

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public String getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

}
