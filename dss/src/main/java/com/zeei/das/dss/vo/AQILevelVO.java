/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AQILevelVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月19日下午3:37:07
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月19日下午3:37:07 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：AQILevelVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class AQILevelVO {

	// 等级Id
	private Integer aqiid;
	// 等级
	private String aqiLevel;
	// 最小AQI值
	private int minAqi;
	// 最大AQI值
	private int maxAqi;
	// 类型
	private String aqiType;
	// 表示颜色
	private String showColor;
	// 影响
	private String effect;
	// 颜色链接
	private String colorUrl;
	// 活动建议
	private String suggestion;

	public Integer getAqiid() {
		return aqiid;
	}

	public void setAqiid(Integer aqiid) {
		this.aqiid = aqiid;
	}

	public String getAqiLevel() {
		return aqiLevel;
	}

	public void setAqiLevel(String aqiLevel) {
		this.aqiLevel = aqiLevel;
	}

	public int getMinAqi() {
		return minAqi;
	}

	public void setMinAqi(int minAqi) {
		this.minAqi = minAqi;
	}

	public int getMaxAqi() {
		return maxAqi;
	}

	public void setMaxAqi(int maxAqi) {
		this.maxAqi = maxAqi;
	}

	public String getAqiType() {
		return aqiType;
	}

	public void setAqiType(String aqiType) {
		this.aqiType = aqiType;
	}

	public String getShowColor() {
		return showColor;
	}

	public void setShowColor(String showColor) {
		this.showColor = showColor;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public String getColorUrl() {
		return colorUrl;
	}

	public void setColorUrl(String colorUrl) {
		this.colorUrl = colorUrl;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

}
