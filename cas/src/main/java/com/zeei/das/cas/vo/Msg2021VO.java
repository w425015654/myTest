/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：Msg2011VO.java
* 包  名  称：com.zeei.das.cas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午3:24:15
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午3:24:15 创建文件
*
*/

package com.zeei.das.cas.vo;

import com.zeei.das.common.utils.DateUtil;

/**
 * 类 名 称：Msg2011VO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class Msg2021VO {

	// 测点代码
	private String pointCode;

	// 监测项目代码
	private String polluteCode;

	// 状态编码
	private String statusCode;

	// 数据时间
	private String dataTime;

	// 状态值
	private String dataValue;

	// 更新时间
	private String updateTime = DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public String getPolluteCode() {
		return polluteCode;
	}

	public void setPolluteCode(String polluteCode) {
		this.polluteCode = polluteCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public String getDataValue() {
		return dataValue;
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getGroupKey() {
		return String.format("%s-%s-%s", this.pointCode, this.polluteCode, this.statusCode);
	}

}
