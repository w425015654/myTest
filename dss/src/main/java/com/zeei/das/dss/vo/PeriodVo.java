package com.zeei.das.dss.vo;

import java.util.Date;


    /**
    * @ClassName: PeriodVo
    * @Description: (应传数据周期)
    * @author zhanghu
    * @date 2018年9月14日
    *
    */
    
public class PeriodVo {
	
	//站点信息
	private StationVO station;
	//时间类型
	private String CN;
	//开始时间
	private Date beginTime;
	//结束时间
	private Date endTime;
	
	
	public StationVO getStation() {
		return station;
	}
	public void setStation(StationVO station) {
		this.station = station;
	}
	public String getCN() {
		return CN;
	}
	public void setCN(String cN) {
		CN = cN;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	
}
