/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AqiDataVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月18日下午1:35:48
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月18日下午1:35:48 创建文件
*
*/

package com.zeei.das.dss.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：AqiDataVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class AqiDataVO {

	private String code;

	private String dataTime;

	private Integer aqi;

	private Integer pm25Iaqi;

	private Double pm25;

	private Double pm10;

	private Integer pm10Iaqi;

	private Double so2;

	private Integer so2Iaqi;

	private Double no2;

	private Integer no2Iaqi;

	private Double co;

	private Integer coIaqi;

	private Double o3;

	private Integer o3Iaqi;

	private Double o38;

	private Integer o38Iaqi;

	private String polluteName;

	private Integer aqiId;

	private String updateTime;

	private Integer level;

	private Double avgvaluePm25;

	private Double avgvaluePm10;

	private String dataStatus = "0";

	private Integer isValided;
	

	public Integer getIsValided() {
		return isValided;
	}

	public void setIsValided(Integer isValided) {
		this.isValided = isValided;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDataTime() {
		return dataTime;
	}
	
	public Date getDeDataTime() {
		return DateUtil.strToDate(dataTime, "yyyy-MM-dd HH:mm:ss");
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public Integer getAqi() {
		return aqi;
	}

	public void setAqi(Integer aqi) {
		this.aqi = aqi;
	}

	public Integer getPm25Iaqi() {
		return pm25Iaqi;
	}

	public void setPm25Iaqi(Integer pm25Iaqi) {
		this.pm25Iaqi = pm25Iaqi;
	}

	public Double getPm25() {
		return pm25;
	}

	public void setPm25(Double pm25) {
		this.pm25 = pm25;
	}

	public Double getPm10() {
		return pm10;
	}

	public void setPm10(Double pm10) {
		this.pm10 = pm10;
	}

	public Integer getPm10Iaqi() {
		return pm10Iaqi;
	}

	public void setPm10Iaqi(Integer pm10Iaqi) {
		this.pm10Iaqi = pm10Iaqi;
	}

	public Double getSo2() {
		return so2;
	}

	public void setSo2(Double so2) {
		this.so2 = so2;
	}

	public Integer getSo2Iaqi() {
		return so2Iaqi;
	}

	public void setSo2Iaqi(Integer so2Iaqi) {
		this.so2Iaqi = so2Iaqi;
	}

	public Double getNo2() {
		return no2;
	}

	public void setNo2(Double no2) {
		this.no2 = no2;
	}

	public Integer getNo2Iaqi() {
		return no2Iaqi;
	}

	public void setNo2Iaqi(Integer no2Iaqi) {
		this.no2Iaqi = no2Iaqi;
	}

	public Double getCo() {
		return co;
	}

	public void setCo(Double co) {
		this.co = co;
	}

	public Integer getCoIaqi() {
		return coIaqi;
	}

	public void setCoIaqi(Integer coIaqi) {
		this.coIaqi = coIaqi;
	}

	public Double getO3() {
		return o3;
	}

	public void setO3(Double o3) {
		this.o3 = o3;
	}

	public Integer getO3Iaqi() {
		return o3Iaqi;
	}

	public void setO3Iaqi(Integer o3Iaqi) {
		this.o3Iaqi = o3Iaqi;
	}

	public Double getO38() {
		return o38;
	}

	public void setO38(Double o38) {
		this.o38 = o38;
	}

	public Integer getO38Iaqi() {
		return o38Iaqi;
	}

	public void setO38Iaqi(Integer o38Iaqi) {
		this.o38Iaqi = o38Iaqi;
	}

	public String getPolluteName() {
		return polluteName;
	}

	public void setPolluteName(String polluteName) {
		this.polluteName = polluteName;
	}

	public Integer getAqiId() {
		return aqiId;
	}

	public void setAqiId(Integer aqiId) {
		this.aqiId = aqiId;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Double getAvgvaluePm25() {
		return avgvaluePm25;
	}

	public void setAvgvaluePm25(Double avgvaluePm25) {
		this.avgvaluePm25 = avgvaluePm25;
	}

	public Double getAvgvaluePm10() {
		return avgvaluePm10;
	}

	public void setAvgvaluePm10(Double avgvaluePm10) {
		this.avgvaluePm10 = avgvaluePm10;
	}

	public String getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}

}
