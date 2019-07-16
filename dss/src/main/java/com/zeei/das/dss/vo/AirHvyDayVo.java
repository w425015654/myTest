package com.zeei.das.dss.vo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：AirHvyDayVo
* @类型描述：统计空气日AQI超标时长
* @功能描述：
* @创建作者：zhanghu
*
*/
public class AirHvyDayVo {

	//测点代码或区域编码
	private String code;
	
	//开始日期
	private String sdate;
	
	//结束日期
	private String edate;
	
	//开始时间
	private String sdateTime;
	
	//结束时间
	private String edateTime;
	
	//更新时间
	private String updateTime;
	
	//0大区1省级 2城市3区县4测点
	private Integer dlevel;
	
	//首要污染物集合
	private String pollNameList;         
	
	//所有污染物
	private Set<String> pollutes = new HashSet<>();
	
	

	public String getPollNameList() {
		return pollNameList;
	}

	public void setPollNameList(String pollNameList) {
		this.pollNameList = pollNameList;
	}

	public Set<String> getPollutes() {
		return pollutes;
	}

	public void setPollutes(Set<String> pollutes) {
		this.pollutes = pollutes;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSdate() {
		return sdate;
	}
	
	public Date getSDedate() {
		return DateUtil.strToDate(sdate + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
	}
	
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}

	public void setSdate(Date sdate) {
		this.sdate = DateUtil.dateToStr(sdate, "yyyy-MM-dd HH:mm:ss");
	}

	public String getEdate() {
		return edate + " 00:00:00";
	}
	
	public Date getEDedate() {
		return DateUtil.strToDate(edate+" 00:00:00", "yyyy-MM-dd HH:mm:ss");
	}
	
	public void setEdate(String edate) {
		this.edate = edate;
	}

	public void setEdate(Date edate) {
		this.edate = DateUtil.dateToStr(edate, "yyyy-MM-dd");
	}

	public String getSdateTime() {
		return sdateTime;
	}

	public void setSdateTime(String sdateTime) {
		this.sdateTime = sdateTime;
	}

	public String getEdateTime() {
		return edateTime;
	}

	public void setEdateTime(String edateTime) {
		this.edateTime = edateTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getDlevel() {
		return dlevel;
	}

	public void setDlevel(Integer dlevel) {
		this.dlevel = dlevel;
	}
	
	
}
