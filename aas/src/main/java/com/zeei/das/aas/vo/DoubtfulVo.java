package com.zeei.das.aas.vo;


import java.util.Date;




/** 
* @类型名称：DoubtfulVo
* @类型描述：
* @功能描述：空气的  站点下的 存储 数据值持续不变开始时间和 一直不变的那个值
* @创建作者：zhanghu
*
*/
public class DoubtfulVo {

	// 对应不变的值
	private Double audValue;

	// 数据时间
	private Date dataTime;
	
	// 上次数据时间
	private Date lastDataTime;
	

	public Double getAudValue() {
		return audValue;
	}

	public void setAudValue(Double audValue) {
		this.audValue = audValue;
	}

	public Date getDataTime() {
		return dataTime;
	}

	public void setDataTime(Date dataTime) {
		this.dataTime = dataTime;
	}

	public Date getLastDataTime() {
		return lastDataTime;
	}

	public void setLastDataTime(Date lastDataTime) {
		this.lastDataTime = lastDataTime;
	}

	
}

