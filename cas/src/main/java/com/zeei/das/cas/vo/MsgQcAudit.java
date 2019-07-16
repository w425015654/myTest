/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：T20x1Message.java
* 包  名  称：com.zeei.das.dps.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午4:24:24
* 
* 修改历史
* 1.0 zhou.yongbo 2017年5月4日下午4:24:24 创建文件
*
*/

package com.zeei.das.cas.vo;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * 类 名 称：MsgQcAudit 类 描 述：MsgQcAudit消息封装 
 */
public class MsgQcAudit {
	private String ST;
	private String pointCode;
	private String MN;
	private String dataType;
	private String CN;
	private String dataTime;
	private String QN;
	
	private long deliveryTag;

	private List<String> polluteList = new ArrayList<>();
	
	

	public List<String> getPolluteList() {
		return polluteList;
	}

	public void setPolluteList(List<String> polluteList) {
		this.polluteList = polluteList;
	}

	public String getQN() {
		return QN;
	}

	public void setQN(String qN) {
		QN = qN;
	}
	
	public long getDeliveryTag() {
		return deliveryTag;
	}

	public void setDeliveryTag(long deliveryTag) {
		this.deliveryTag = deliveryTag;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getMN() {
		return MN;
	}

	public void setMN(String mN) {
		MN = mN;
	}

	public String getCN() {
		return CN;
	}

	public void setCN(String cN) {
		CN = cN;
	}

	public String getDataTime() {

		return dataTime;
	}

	public void setDataTime(String dataTime) {

		this.dataTime = dataTime;
	}
	
	public boolean valid() {
		
		if(StringUtils.isEmpty(dataTime) || StringUtils.isEmpty(ST) || StringUtils.isEmpty(pointCode)
				|| StringUtils.isEmpty(dataType) || StringUtils.isEmpty(CN)){
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "T20x1Message [ST=" + ST + ", pointCode=" + pointCode  + ", CN=" + CN 
				+ ",dataType" + dataType + ",dataTime" + dataTime + "]" ;
	}


}