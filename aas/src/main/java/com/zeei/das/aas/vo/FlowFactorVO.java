/** 
* Copyright (C) 2012-2019 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：AvgSpeedKeyVO.java
* 包  名  称：com.zeei.das.cgs.vo
* 文件描述：
* 创建日期：2019年5月15日下午5:36:45
* 
* 修改历史
* 1.0 lian.wei 2019年5月15日下午5:36:45 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

/**
 * @类型名称：FlowFactorVO
 * @类型描述：
 * @功能描述：
 * @创建作者：lian.wei
 */

public class FlowFactorVO {
	private Double beforFlow = 0.0;  // 上次的流量
	private String polluteCode;
	private Date beforDate;
	/**
	 * 获取beforFlow的值
	 * 
	 * @return  返回beforFlow的值
	 */
	public Double getBeforFlow() {
		return beforFlow;
	}
	/**
	 * 设置beforFlow值
	 * 
	 * @param   beforFlow     
	 */
	public void setBeforFlow(Double beforFlow) {
		this.beforFlow = beforFlow;
	}
	/**
	 * 获取polluteCode的值
	 * 
	 * @return  返回polluteCode的值
	 */
	public String getPolluteCode() {
		return polluteCode;
	}
	/**
	 * 设置polluteCode值
	 * 
	 * @param   polluteCode     
	 */
	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}
	/**
	 * 获取beforDate的值
	 * 
	 * @return  返回beforDate的值
	 */
	public Date getBeforDate() {
		return beforDate;
	}
	/**
	 * 设置beforDate值
	 * 
	 * @param   beforDate     
	 */
	public void setBeforDate(Date beforDate) {
		this.beforDate = beforDate;
	}
}
