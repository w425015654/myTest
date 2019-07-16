/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StationVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:20:02
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:20:02 创建文件
*
*/

package com.zeei.das.dss.vo;

import java.util.Date;

/**
 * 类 名 称：StationVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class StationVO {

	// 站点编码
	private String pointCode;

	// 区域编码
	private String areaCode;

	// 系统类型
	private String ST;

	// stdpointcode字段
	private String MN;

	// 分钟数据周期
	private long mCycle = 5;

	// 小时数据周期
	private long hCycle = 1;

	// 天数据周期
	private long dCycle = 1;

	// 分钟数据上报间隔
	private int rInterval = 5;

	// 分钟数据上报间隔
	private int mInterval = 10;

	// 小时数据上报间隔
	private int hInterval = 1;

	// 是否统计分钟数据
	private boolean isStatsMI;

	// 是否统计小时数据
	private boolean isStatsHH;

	// 是否统计天数据
	private boolean isStatsDD;

	// 是否统计天数据
	private boolean isVerifyPasswd;

	// 是否支持T212协议
	private boolean isSup212;

	// 是否登录校时
	private boolean isTimecal;

	// 是否进行消息应答
	private boolean isMsgAck;

	// 区域级别
	private Integer areaLevel;

	// 分钟最新数据时间
	private Date mDataTime;

	// 小时最新数据时间

	private Date hDataTime;

	// 是否进行AQI计算
	private int isRqc;

	// 判断是否是微站
	private String controlLevel;

	// 是否统计小时AQI
	private String isStatsAqiHH;

	// 是否统计日AQI
	private String isStatsAqiDD;

	private String CN;

	private String dataTime;	

	private int cInterval;

	private Long cCycle;

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

	public long getmCycle() {
		return mCycle;
	}

	public void setmCycle(long mCycle) {
		this.mCycle = mCycle;
	}

	public long gethCycle() {
		return hCycle;
	}

	public void sethCycle(long hCycle) {
		this.hCycle = hCycle;
	}

	public long getdCycle() {
		return dCycle;
	}

	public void setdCycle(long dCycle) {
		this.dCycle = dCycle;
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

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public int getrInterval() {
		return rInterval;
	}

	public void setrInterval(int rInterval) {
		this.rInterval = rInterval;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public boolean isStatsMI() {
		return isStatsMI;
	}

	public void setStatsMI(boolean isStatsMI) {
		this.isStatsMI = isStatsMI;
	}

	public boolean isStatsHH() {
		return isStatsHH;
	}

	public void setStatsHH(boolean isStatsHH) {
		this.isStatsHH = isStatsHH;
	}

	public boolean isStatsDD() {
		return isStatsDD;
	}

	public void setStatsDD(boolean isStatsDD) {
		this.isStatsDD = isStatsDD;
	}

	public boolean isVerifyPasswd() {
		return isVerifyPasswd;
	}

	public void setVerifyPasswd(boolean isVerifyPasswd) {
		this.isVerifyPasswd = isVerifyPasswd;
	}

	public boolean isSup212() {
		return isSup212;
	}

	public void setSup212(boolean isSup212) {
		this.isSup212 = isSup212;
	}

	public boolean isTimecal() {
		return isTimecal;
	}

	public void setTimecal(boolean isTimecal) {
		this.isTimecal = isTimecal;
	}

	public boolean isMsgAck() {
		return isMsgAck;
	}

	public void setMsgAck(boolean isMsgAck) {
		this.isMsgAck = isMsgAck;
	}

	public Integer getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(Integer areaLevel) {
		this.areaLevel = areaLevel;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public int getcInterval() {
		return cInterval;
	}

	public void setcInterval(int cInterval) {
		this.cInterval = cInterval;
	}

	public Long getcCycle() {
		return cCycle;
	}

	public void setcCycle(Long cCycle) {
		this.cCycle = cCycle;
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

	public int getIsRqc() {
		return isRqc;
	}

	public void setIsRqc(int isRqc) {
		this.isRqc = isRqc;
	}

	public String getControlLevel() {
		return controlLevel;
	}

	public void setControlLevel(String controlLevel) {
		this.controlLevel = controlLevel;
	}

	public String getIsStatsAqiHH() {
		return isStatsAqiHH;
	}

	public void setIsStatsAqiHH(String isStatsAqiHH) {
		this.isStatsAqiHH = isStatsAqiHH;
	}

	public String getIsStatsAqiDD() {
		return isStatsAqiDD;
	}

	public void setIsStatsAqiDD(String isStatsAqiDD) {
		this.isStatsAqiDD = isStatsAqiDD;
	}

}
