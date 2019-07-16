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

package com.zeei.das.cgs.vo;

/**
 * @类型名称：AvgSpeedKeyVO
 * @类型描述：
 * @功能描述：
 * @创建作者：lian.wei
 */

public class AvgSpeedArrVO {
	public int speedCnt = 0;
	public AvgSpeedVO[] arraySpeed = null;
	/**
	 * 获取speedCnt的值
	 * 
	 * @return  返回speedCnt的值
	 */
	public int getSpeedCnt() {
		return speedCnt;
	}
	/**
	 * 设置speedCnt值
	 * 
	 * @param   speedCnt     
	 */
	public void setSpeedCnt(int speedCnt) {
		this.speedCnt = speedCnt;
	}
	/**
	 * 获取arraySpeed的值
	 * 
	 * @return  返回arraySpeed的值
	 */
	public AvgSpeedVO[] getArraySpeed() {
		return arraySpeed;
	}
	/**
	 * 设置arraySpeed值
	 * 
	 * @param   arraySpeed     
	 */
	public void setArraySpeed(AvgSpeedVO[] arraySpeed) {
		this.arraySpeed = arraySpeed;
	}
	
}
