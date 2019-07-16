/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：SystemTableVO.java
* 包  名  称：com.zeei.das.dss.vo
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午8:25:51
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:25:51 创建文件
*
*/

package com.zeei.das.dss.vo;

/**
 * 类 名 称：SystemTableVO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public class SystemTableVO {

	// 系统代码
	private String ST;

	// 实时数据表名
	private String tableNameRT;

	// 分钟数据表名
	private String tableNameMin;

	// 小时数据表名
	private String tableNameHH;

	// 日 时数据表名
	private String tableNameDD;

	// 年月数据表
	private String tableNameMY;

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getTableNameRT() {
		return tableNameRT;
	}

	public void setTableNameRT(String tableNameRT) {
		this.tableNameRT = tableNameRT;
	}

	public String getTableNameMin() {
		return tableNameMin;
	}

	public void setTableNameMin(String tableNameMin) {
		this.tableNameMin = tableNameMin;
	}

	public String getTableNameHH() {
		return tableNameHH;
	}

	public void setTableNameHH(String tableNameHH) {
		this.tableNameHH = tableNameHH;
	}

	public String getTableNameDD() {
		return tableNameDD;
	}

	public void setTableNameDD(String tableNameDD) {
		this.tableNameDD = tableNameDD;
	}

	public String getTableNameMY() {
		return tableNameMY;
	}

	public void setTableNameMY(String tableNameMY) {
		this.tableNameMY = tableNameMY;
	}

}
