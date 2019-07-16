/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：Msg2076VO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午3:24:31
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午3:24:31 创建文件
*
*/

package com.zeei.das.cas.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/**
 * @类 名 称：Msg2063VO
 * @类 描 述：TODO 请修改文件描述
 * @功能描述：TODO 请修改功能描述
 * @创建作者：quanhongsheng
 */

public class Msg2063VO {

	// 测点代码
	private int pointCode;

	// 因子編碼
	private String ParamID;

	// 数据标识
	private String Flag;

	// 加标回收数据，单位为mg/L
	private Double Check;

	// 加标母液浓度，单位为mg/L
	private Double Chroma;

	// 加标体积，单位为ml
	private Double Volume;

	// 加标水杯定容体积，单位为ml
	private Double DVolume;

	// 加标前水样测试数据时间，该数据时间从在线分析仪表中读取并保持一致
	private String waterTime;

	// 加标前水样测试值，单位为mg/L
	private Double Water;

	// 数据时间
	private String dataTime;
	
	//命令唯一标识
	private String qn;
	
	// 数据类型
	private Integer dataType = 0;
	
	// 数据是否审核
	private Integer dataStatus = 0;

	// 数据是否有效
	private Integer isValided = 1;

	private String updateTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");

	
	
	public boolean valid() {
		if (ParamID == null || ParamID.length() == 0) {
			return false;
		}
		return true;
	}
	
	public String getQn() {
		return qn;
	}



	public void setQn(String qn) {
		this.qn = qn;
	}

	public String getParamID() {
		return ParamID;
	}

	public void setParamID(String paramID) {
		ParamID = paramID;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}

	public String getFlag() {
		return Flag;
	}

	public void setFlag(String flag) {
		Flag = flag;
		isValided = "N".equals(flag) ? 1 : 0;
	}

	public Double getCheck() {
		return Check;
	}

	public void setCheck(Double check) {
		Check = check;
	}

	public Double getVolume() {
		return Volume;
	}

	public void setVolume(Double volume) {
		Volume = volume;
	}

	public Double getDVolume() {
		return DVolume;
	}

	public void setDVolume(Double dVolume) {
		DVolume = dVolume;
	}

	public Integer getIsValided() {
		return isValided;
	}

	public void setIsValided(Integer isValided) {
		this.isValided = isValided;
	}

	public Double getChroma() {
		return Chroma;
	}

	public void setChroma(Double chroma) {
		Chroma = chroma;
	}

	public String getWaterTime() {
		return waterTime;
	}

	public void setWaterTime(String waterTime) {
		this.waterTime = waterTime;
	}

	public Double getWater() {
		return Water;
	}

	public void setWater(Double water) {
		Water = water;
	}

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}
	
	public Date getDeDataTime() {
		return DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss");
	}

}
