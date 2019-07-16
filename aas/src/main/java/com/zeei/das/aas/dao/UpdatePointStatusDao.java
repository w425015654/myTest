/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：UpdatePointStatus.java
* 包  名  称：com.zeei.das.aas.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月6日下午2:40:13
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月6日下午2:40:13 创建文件
*
*/

package com.zeei.das.aas.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.aas.vo.STVO;

/**
 * 类 名 称：UpdatePointStatus 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public interface UpdatePointStatusDao {

	/**
	 * 
	 * updateStatusByBatch:批量更新站点涨停
	 *
	 * @param tableName
	 *            测点表名
	 * @param data
	 * @return Integer
	 */
	public Integer updateStatusByBatch(@Param("tableName") String tableName, @Param("list") List<STVO> data);

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
