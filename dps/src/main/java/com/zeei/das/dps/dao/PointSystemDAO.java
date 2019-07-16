/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：RDPS
* 文件名称：PointSystemDAO.java
* 包  名  称：com.zeei.das.dps.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月20日下午2:26:21
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月20日下午2:26:21 创建文件
*
*/

package com.zeei.das.dps.dao;

import java.util.List;

import com.zeei.das.dps.vo.PointSystemVO;

/**
 * 类 名 称：PointSystemDAO 类 描 述：mybatis接口，对应T_DIC_POINTSYSTEM 功能描述：
 * 创建作者：zhou.yongbo
 */

public interface PointSystemDAO {
	// 根据系统类型查询PointSystemVO记录
	public PointSystemVO queryPointSystemBySystype(String systype);

	// 查找所有PointSystemVO记录
	public List<PointSystemVO> getPointSystemList();
}
