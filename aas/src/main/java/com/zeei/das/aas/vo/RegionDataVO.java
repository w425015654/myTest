/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：RegionDataVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：区域监测数据实体
* 创建日期：2017年4月25日下午3:35:02
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月25日下午3:35:02 创建文件
*
*/

package com.zeei.das.aas.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类 名 称：RegionDataVO 类 描 述：区域监测数据实体 功能描述：区域监测数据实体创建作者：quanhongsheng
 */

public class RegionDataVO {

	// 区域代码
	private String regionCode;

	// 区域站点总数
	private int total;

	// 数据时间
	private String dataTime;

	// 监测数据
	private List<Double> Params = new ArrayList<Double>();

	// 站点mn列表
	private Map<String, Double> stations = new HashMap<String, Double>();

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public List<Double> getParams() {
		return Params;
	}

	public void setParams(List<Double> params) {
		Params = params;
	}

	public Map<String, Double> getStations() {
		return stations;
	}

	public void setStations(Map<String, Double> stations) {
		this.stations = stations;
	}

}
