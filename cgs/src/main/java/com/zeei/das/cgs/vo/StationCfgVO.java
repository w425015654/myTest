package com.zeei.das.cgs.vo;

import java.util.Date;

public class StationCfgVO {
	
	//测点id
	private String pointCode;

	private String MN;

	private String ID;

	private String ST;

	private String Pwd = "123456";

	// 分钟数据周期
	private long mCycle = 5;

	// 小时数据周期
	private long hCycle = 1;

	// 天数据周期
	private long dCycle = 1;

	// 分钟数据上报间隔
	private int rInterval = 5;

	// 分钟数据上报间隔
	private int mInterval = 5;

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

	// 最新实时数据时间
	private Date rDataTime;
	// 最新分钟数据时间
	private Date mDataTime;
	// 最新小时数据时间
	private Date hDataTime;
	// 最新天数据时间
	private Date dDataTime;
	
	// 最新数据时间
	private Date dataTime;
	
	
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

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getPwd() {
		return Pwd;
	}

	public void setPwd(String pwd) {
		Pwd = pwd;
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

	public int gethInterval() {
		return hInterval;
	}

	public void sethInterval(int hInterval) {
		this.hInterval = hInterval;
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

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
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

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}
	
	
}
