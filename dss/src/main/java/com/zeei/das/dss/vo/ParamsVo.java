package com.zeei.das.dss.vo;

import java.util.Date;
import java.util.List;

import com.zeei.das.common.utils.DateUtil;


/** 
* @类型名称：ParamsVo
* @类型描述：
* @功能描述：离群值相关计算参数Vo
* @创建作者：zhanghu
*
*/
public class ParamsVo {

   //测点集合
   private	List<String> pointCodes;
   
   //测点集合
   private	String  pointCode;
   
   //区域编码
   private  String  areaCode;
   
   //等级
   private  Integer level;
   
   //开始时间
   private  Date beginTime;
   
   //结束时间
   private  Date endTime;
   
   //
   private  Integer dataStatus;
	
   
   
   
	public String getPointCode() {
		return pointCode;
	}
	
	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public List<String> getPointCodes() {
		return pointCodes;
	}
	
	public void setPointCodes(List<String> pointCodes) {
		this.pointCodes = pointCodes;
	}
	
	public String getAreaCode() {
		return areaCode;
	}
	
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	public String getBeginTime() {
		return String.format("%s:00:00", DateUtil.dateToStr(beginTime, "yyyy-MM-dd HH"));
	}
	
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	
	public String getEndTime() {
		return String.format("%s:00:00", DateUtil.dateToStr(endTime, "yyyy-MM-dd HH")); 
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public Integer getDataStatus() {
		return dataStatus;
	}
	
	public void setDataStatus(Integer dataStatus) {
		this.dataStatus = dataStatus;
	}
   
   
	
}
