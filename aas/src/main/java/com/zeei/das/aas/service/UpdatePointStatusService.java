/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：UpdatePointStatusService.java
* 包  名  称：com.zeei.das.aas.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月6日下午2:44:11
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月6日下午2:44:11 创建文件
*
*/

package com.zeei.das.aas.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.aas.vo.STVO;

/**
 * 类 名 称：UpdatePointStatusService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public interface UpdatePointStatusService {

	/**
	 * 
	 * updateStatusByBatch:批量更新站点涨停
	 *
	 * @param tableName
	 *            测点表名
	 * @param data
	 * @return Integer
	 */
	public Integer updateStatusByBatch(String tableName, List<STVO> data);

	/**
	 * 
	 * updateStatusByBatch2:批量更新站点涨停
	 *
	 * @param tableName
	 *            表名
	 * @param status
	 *            状态
	 * @param data
	 * @return Integer
	 */
	public Integer updateStatusByBatch2(@Param("tableName") String tableName, @Param("status") String status,
			@Param("list") List<String> data);

}
