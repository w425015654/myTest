/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：StationVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：站點配置信息類
* 创建日期：2017年4月21日下午5:36:36
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月21日下午5:36:36 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.Date;

/**
 * 类 名 称：StationVO 类 描 述：站點配置信息類 功能描述：站點配置信息類 创建作者：quanhongsheng
 */

public class StationVO {

	// 站点ID
	private String pointCode;

	// 站点名称
	private String pointName;
		
	// 站点MN
	private String MN;

	// 区域编码
	private String regionCode;

	// 站点数据周期
	private int rCycle;

	// 原始数据上报间隔 单位秒
	private int rInterval=5;

	// 分钟数据上报间隔 单位分
	private int mInterval=10;

	// 小时数据上报间隔
	private int hInterval = 1;

	// 最新实时数据时间
	private Date rDataTime;
	// 最新分钟数据时间
	private Date mDataTime;
	// 最新小时数据时间
	private Date hDataTime;
	// 最新天数据时间
	private Date dDataTime;
	
	//异常周期持续次数
	private int rCount=0;

	// 异常周期持续次数
	private int mCount=0;

	// 异常周期持续次数
	private int hCount = 0;


	// 站点类型
	private String ST;
	
	// 站点状态
	private String onlineStatus;
	
	// 最新心跳时间
	private Date heartTime;
	
	// 企业编码
	private String psCode;

	public int getrInterval() {
		return rInterval;
	}

	public void setrInterval(int rInterval) {
		this.rInterval = rInterval;
	}

	public int getmInterval() {
		return mInterval;
	}

	public void setmInterval(int mInterval) {
		this.mInterval = mInterval;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public int getrCycle() {
		return rCycle;
	}

	public void setrCycle(int rCycle) {
		this.rCycle = rCycle;
	}

	public Date getHeartTime() {
		return heartTime;
	}

	public void setHeartTime(Date heartTime) {
		this.heartTime = heartTime;
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public String getPsCode() {
		return psCode;
	}

	public void setPsCode(String psCode) {
		this.psCode = psCode;
	}

	public int gethInterval() {
		return hInterval;
	}

	public void sethInterval(int hInterval) {
		this.hInterval = hInterval;
	}

	public Date getrDataTime() {
		return rDataTime;
	}

	public void setrDataTime(Date rDataTime) {
		this.rDataTime = rDataTime;
	}

	public Date getmDataTime() {
		return mDataTime;
	}

	public void setmDataTime(Date mDataTime) {
		this.mDataTime = mDataTime;
	}

	public Date gethDataTime() {
		return hDataTime;
	}

	public void sethDataTime(Date hDataTime) {
		this.hDataTime = hDataTime;
	}

	public Date getdDataTime() {
		return dDataTime;
	}

	public void setdDataTime(Date dDataTime) {
		this.dDataTime = dDataTime;
	}

	public int getrCount() {
		return rCount;
	}

	public void setrCount(int rCount) {
		this.rCount = rCount;
	}

	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	public int gethCount() {
		return hCount;
	}

	public void sethCount(int hCount) {
		this.hCount = hCount;
	}

	/**
	 * 获取pointName的值
	 * 
	 * @return  返回pointName的值
	 */
	public String getPointName() {
		return pointName;
	}

	/**
	 * 设置pointName值
	 * 
	 * @param   pointName     
	 */
	public void setPointName(String pointName) {
		this.pointName = pointName;
	}	
	
	

}
