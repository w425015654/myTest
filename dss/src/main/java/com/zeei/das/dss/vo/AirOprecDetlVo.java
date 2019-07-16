package com.zeei.das.dss.vo;

/** 
* @类型名称：AirOprecDetlVo
* @类型描述：
* @功能描述：环境质量数据-测点超标记录
* @创建作者：zhanghu
*
*/
public class AirOprecDetlVo {
	
	
	//id
	private  int  decId;

	//超标记录id
	private  int  recId;
	
	//污染指数
	private  int  aqi;
	
	//监测项目代码
	private  String  polluteName;
	
	//数据时间
	private  String  dataTime;
	
	//更新时间
	private  String  updateTime;
	
	//0标况2实况
	private  int  dType = 0;

	public int getDecId() {
		return decId;
	}

	public void setDecId(int decId) {
		this.decId = decId;
	}

	public int getRecId() {
		return recId;
	}

	public void setRecId(int recId) {
		this.recId = recId;
	}

	public int getAqi() {
		return aqi;
	}

	public void setAqi(int aqi) {
		this.aqi = aqi;
	}

	public String getPolluteName() {
		return polluteName;
	}

	public void setPolluteName(String polluteName) {
		this.polluteName = polluteName;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public int getdType() {
		return dType;
	}

	public void setdType(int dType) {
		this.dType = dType;
	}
	
	
}
