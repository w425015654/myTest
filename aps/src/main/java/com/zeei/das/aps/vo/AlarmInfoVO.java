package com.zeei.das.aps.vo;

import java.util.Date;

public class AlarmInfoVO {

	// 测点代码
	private int pointCode;
	// 告警吗
	private String alarmCode;
	// 服务告警的服务代码
	private String serviceCode;

	private String dataType;

	// 服务状态 online offline
	private String serviceStatus;
	// 告警类型
	private String alarmType;
	// 检测项目代码
	private String polluteCode;
	// 告警产生时间
	private Date startTime;
	// 告警结束时间
	private Date endTime;
	// 告警值
	private Double alarmValue;
	// 新告警
	private boolean newAlarm;
	// 1新产生告警， 4已消警
	private int alarmStatus;
	// 0不屏蔽告警，1屏蔽告警
	private int isShielded = 0;

	private int isOnline = 0;

	private int isPush = 0;
	
	// 是否产生告警
	private int isGenAlarm = 1;

	public int getPointCode() {
		return pointCode;
	}

	public void setPointCode(int pointCode) {
		this.pointCode = pointCode;
	}

	public String getAlarmCode() {
		return alarmCode;
	}

	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getAlarmValue() {
		return alarmValue;
	}

	public void setAlarmValue(Double alarmValue) {
		this.alarmValue = alarmValue;
	}

	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public int getIsShielded() {
		return isShielded;
	}

	public void setIsShielded(int isShielded) {
		this.isShielded = isShielded;
	}

	public boolean isNewAlarm() {
		return newAlarm;
	}

	public void setNewAlarm(boolean newAlarm) {
		this.newAlarm = newAlarm;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}

	public int getIsGenAlarm() {
		return isGenAlarm;
	}

	public void setIsGenAlarm(int isGenAlarm) {
		this.isGenAlarm = isGenAlarm;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		if (pointCode > 0) {
			sb.append(String.format("pointCode: %d, ", pointCode));
		}
		if (serviceCode != null) {
			sb.append(String.format("serviceCode: %s, ", serviceCode));
		}
		return new String(sb);
	}

	/**
	 * 获取isPush的值
	 * 
	 * @return  返回isPush的值
	 */
	public int getIsPush() {
		return isPush;
	}

	/**
	 * 设置isPush值
	 * 
	 * @param   isPush     
	 */
	public void setIsPush(int isPush) {
		this.isPush = isPush;
	}

}
