package com.zeei.das.dss.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：PollIncident
* @类型描述：
* @功能描述：统计分钟数据超标的事件
* @创建作者：zhanghu
*
*/
public class PollIncidentVo {
	
	//超标记录id
	private int recId;
	
	//测点代码
	private int pointCode;
	
	//记录开始时间
	private String  sTime;
	
	//记录结束时间
	private String  eTime;
	
	//连续超标标识1表示连续超标，0表示不是
	private int ofType = 0;
	
	//数据更新时间
	private String updateTime;
	
	//上次更新的数据时间
	private Date lastDateTime;
	
	//已经未超标次数
	private int lastOf = 0;

	public int getRecId() {
		return recId;
	}

	public void setRecId(int recId) {
		this.recId = recId;
	}

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public String getsTime() {
		return sTime;
	}
	
	public Date getsDeTime() {
		return DateUtil.strToDate(sTime, "yyyy-MM-dd HH:mm:ss");
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String geteTime() {
		return eTime;
	}

	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	public int getOfType() {
		return ofType;
	}

	public void setOfType(int ofType) {
		this.ofType = ofType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastDateTime() {
		return lastDateTime;
	}

	public void setLastDateTime(Date lastDateTime) {
		this.lastDateTime = lastDateTime;
	}

	public int getLastOf() {
		return lastOf;
	}

	public void setLastOf(int lastOf) {
		this.lastOf = lastOf;
	}
	
	

}
