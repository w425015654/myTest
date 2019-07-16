/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：ParamVO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月3日下午3:42:04
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午3:42:04 创建文件
*
*/

package com.zeei.das.cas.vo;

/**
 * 类 名 称：ParamVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class ParamVO {

	// 采集类型
	private String inftype;

	// 采集设备名称
	private String devname;

	// 采集通道ID
	private String channelid;

	// 系统ID
	private Integer sysid;

	// 因子分组ID
	private String fgid;

	// 因子通道ID
	private String fid;

	// 因子名称
	private String fname;

	// 单位
	private String unit;

	// 数值格式
	private Integer dformat;

	// 测点ID
	private Integer stationid;

	// 量程下限
	private Integer devmin;

	// 量程上限
	private Integer devmax;

	// 斜率
	private Integer slope;

	// 截距
	private Integer intercept;

	public String getInftype() {
		return inftype;
	}

	public void setInftype(String inftype) {
		this.inftype = inftype;
	}

	public String getDevname() {
		return devname;
	}

	public void setDevname(String devname) {
		this.devname = devname;
	}

	public String getChannelid() {
		return channelid;
	}

	public void setChannelid(String channelid) {
		this.channelid = channelid;
	}

	public Integer getSysid() {
		return sysid;
	}

	public void setSysid(Integer sysid) {
		this.sysid = sysid;
	}

	public String getFgid() {
		return fgid;
	}

	public void setFgid(String fgid) {
		this.fgid = fgid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getDformat() {
		return dformat;
	}

	public void setDformat(Integer dformat) {
		this.dformat = dformat;
	}

	public Integer getStationid() {
		return stationid;
	}

	public void setStationid(Integer stationid) {
		this.stationid = stationid;
	}

	public Integer getDevmin() {
		return devmin;
	}

	public void setDevmin(Integer devmin) {
		this.devmin = devmin;
	}

	public Integer getDevmax() {
		return devmax;
	}

	public void setDevmax(Integer devmax) {
		this.devmax = devmax;
	}

	public Integer getSlope() {
		return slope;
	}

	public void setSlope(Integer slope) {
		this.slope = slope;
	}

	public Integer getIntercept() {
		return intercept;
	}

	public void setIntercept(Integer intercept) {
		this.intercept = intercept;
	}

}
