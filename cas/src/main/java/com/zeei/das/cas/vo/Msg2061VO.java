package com.zeei.das.cas.vo;

import java.util.Date;

import com.zeei.das.common.utils.DateUtil;

/** 
* @类型名称：Msg2061VO
* @类型描述：
* @功能描述：小时数据
* @创建作者：zhanghu
*
*/
public class Msg2061VO {

	//测点代码
	private  Integer  pointCode;
	
	//监测项目代码
	private  String   polluteCode;
	
	//日期
	private  String   dataTime;
	
	//数据值
	private  Double   dataValue;
	
	//状态标识
	private  String  dataFlag;

	
	public Integer getPointCode() {
		return pointCode;
	}

	public void setPointCode(Integer pointCode) {
		this.pointCode = pointCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
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

	public Double getDataValue() {
		return dataValue;
	}

	public void setDataValue(Double dataValue) {
		this.dataValue = dataValue;
	}

	public String getDataFlag() {
		return dataFlag;
	}

	public void setDataFlag(String dataFlag) {
		this.dataFlag = dataFlag;
	}
	
	
}
