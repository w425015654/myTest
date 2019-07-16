/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：DPS
* 文件名称：PointSiteVO.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：PointSiteVO, 对应视图V_BAS_POINTSITE_INFO
* 创建日期：2017年4月21日下午1:08:08
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月21日下午1:08:08 创建文件
*
*/

package com.zeei.das.dps.vo;

import java.util.Date;

import com.zeei.das.dps.cycle.DataCycleUtil;

/**
 * 类 名 称：PointSiteVO 类 描 述：视图V_BAS_POINTSITE_INFO的封装
 * 功能描述：视图V_BAS_POINTSITE_INFO的封装 类 名 称：PointSiteVO 类 描
 * 述：视图V_BAS_POINTSITE_INFO的封装 功能描述：TODO 请修改功能描述 创建作者：zhou.yongbo
 */

public class PointSiteVO {

	private String pointCode;
	private int systemType;
	private int stationCode;
	// stdpointcode字段
	private String mn;
	private String pointName;
	// 分钟数据周期
	private long mCycle = 0;
	// 小时数据周期
	private long hCycle = 0;
	// 天数据周期
	private long dCycle = 0;
	// 实时数据上报间隔
	private int rInterval = 30;
	// 分钟数据上报间隔
	private int mInterval = 5;
	// 小时数据上报间隔
	private int hInterval = 1;
	// 日数据上报间隔
	private int dInterval = 1;

	// 最新实时数据时间
	private Date rDataTime;
	// 最新没做数据时间
	private Date mDataTime;
	// 最新小时数据时间
	private Date hDataTime;
	// 最新日数据时间
	private Date dDataTime;

	// 是否支持不是
	private boolean isSupplement = true;

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public int getSystemType() {
		return systemType;
	}

	public void setSystemType(int systemType) {
		this.systemType = systemType;
	}

	public int getStationCode() {
		return stationCode;
	}

	public void setStationCode(int stationCode) {
		this.stationCode = stationCode;
	}

	public String getMn() {
		return mn;
	}

	public void setMn(String mn) {
		this.mn = mn;
	}

	public String getPointName() {
		return pointName;
	}

	public void setPointName(String pointName) {
		this.pointName = pointName;
	}

	public long getdCycle() {

		if (dCycle == 0 && hDataTime != null) {
			return DataCycleUtil.getCycle(hDataTime, dInterval * 24 * 60 * 60);
		}
		return dCycle;
	}

	public void setdCycle(long dCycle) {
		this.dCycle = dCycle;
	}

	public long getmCycle() {

		if (mCycle == 0 && rDataTime != null) {
			return DataCycleUtil.getCycle(rDataTime, mInterval * 60);
		}
		return mCycle;
	}

	public void setmCycle(long mCycle) {
		this.mCycle = mCycle;
	}

	public long gethCycle() {

		if (hCycle == 0 && mDataTime != null) {
			return DataCycleUtil.getCycle(mDataTime, hInterval * 60 * 60);
		}

		return hCycle;
	}

	public void sethCycle(long hCycle) {
		this.hCycle = hCycle;
	}

	public int getmInterval() {
		return mInterval;
	}

	public void setmInterval(int mInterval) {
		this.mInterval = mInterval;
	}

	public int gethInterval() {
		return hInterval;
	}

	public void sethInterval(int hInterval) {
		this.hInterval = hInterval;
	}

	public int getrInterval() {
		return rInterval;
	}

	public void setrInterval(int rInterval) {
		this.rInterval = rInterval;
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

	public int getdInterval() {
		return dInterval;
	}

	public void setdInterval(int dInterval) {
		this.dInterval = dInterval;
	}

	public boolean isSupplement() {
		return isSupplement;
	}

	public void setSupplement(boolean isSupplement) {
		this.isSupplement = isSupplement;
	}

}
