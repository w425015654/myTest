/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：PolluteVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年1月26日下午3:21:11
* 
* 修改历史
* 1.0 luoxianglin 2018年1月26日下午3:21:11 创建文件
*
*/

package com.zeei.das.dps.vo;

/**
 * 类型名称：PolluteVO 类型描述：TODO 请修改类型描述 功能描述：TODO 请修改功能描述 创建作者：quan.hongsheng
 *
 */

public class PolluteVO {
	
	private String pointCode;

	private String polluteCode;

	private String polluteName;

	private String ST;
	
	/**
	 * hhcycletime:地表水小时数据周期
	 */
	private Integer hhcycletime;
	
	private Integer numprecision = 0;
	

	public Integer getNumprecision() {
		return numprecision;
	}

	public void setNumprecision(Integer numprecision) {
		this.numprecision = numprecision;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public Integer getHhcycletime() {
		return hhcycletime;
	}

	public void setHhcycletime(Integer hhcycletime) {
		this.hhcycletime = hhcycletime;
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

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

}
