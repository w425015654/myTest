/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：STVO.java
* 包  名  称：com.zeei.das.aas.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月6日下午2:16:14
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月6日下午2:16:14 创建文件
*
*/

package com.zeei.das.aas.vo;

/**
 * 类 名 称：STVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class STVO {

	// 系统类型
	private String ST;

	// 测点表名
	private String tableName;

	// 测点编码
	private String pointCode;

	// 测点状态【-1:删除，1：在线，0离线】
	private int status;

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPointCode() {
		return pointCode;
	}

	public void setPointCode(String pointCode) {
		this.pointCode = pointCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
