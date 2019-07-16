/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StroageService.java
* 包  名  称：com.zeei.das.dss.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月18日下午1:28:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月18日下午1:28:45 创建文件
*
*/

package com.zeei.das.dss.service;

import java.util.List;

import com.zeei.das.dss.vo.MonitorDataVO;

/**
 * 类 名 称：StroageService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface StroageService {

	/**
	 * 
	 * insertData:写入单条数据
	 *
	 * @param tableName
	 * @param data
	 * @return List<MonitorDataVO>
	 */
	public Integer insertData(String tableName, MonitorDataVO data);

	/**
	 * 
	 * insertData:批量写入数据
	 *
	 * @param tableName
	 * @param data
	 * @return List<MonitorDataVO>
	 */
	public Integer insertDataBatch(String tableName, List<MonitorDataVO> datas);

	/**
	 * 
	 * insertData:批量写入审核数据
	 *
	 * @param tableName
	 * @param data
	 * @return List<MonitorDataVO>
	 */
	public Integer insertAuditDataBatch(String tableName, List<MonitorDataVO> datas);

	/**
	 * 
	 * insertYMDataByBatch:批量写入月/年统计数据
	 *
	 * @param tableName
	 * @param data
	 * @return Integer
	 */
	public Integer insertYMDataByBatch(String tableName, List<MonitorDataVO> data);

	/**
	 * 
	 * insertYMData:插入单条月年数据
	 *
	 * @param tableName
	 * @param data
	 * @return Integer
	 */
	public Integer insertYMData(String tableName, MonitorDataVO data);

	/**
	 * 
	 * reviewData:自动复核数据
	 * 
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            测点编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return Integer
	 */

	public Integer reviewData(String tableName, String pointCode, String beginTime, String endTime);

	/**
	 * 
	 * auditData:自动审核数据
	 * 
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            测点编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return Integer
	 */

	public Integer auditData(String tableName, String pointCode, String beginTime, String endTime);
}
