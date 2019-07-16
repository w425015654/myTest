/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：T20x1DAO.java
* 包  名  称：com.zeei.das.dps.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月27日下午7:03:51
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月27日下午7:03:51 创建文件
*
*/

package com.zeei.das.dps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dps.vo.T20x1VO;

/**
 * 类 名 称：T20x1DAO 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：zhou.yongbo
 */

public interface T20x1DAO {

	public void insertT20x1ByBatch(@Param("table") String table, @Param("t20x1s") List<T20x1VO> t20x1s, 
			@Param("isOverride") String isOverride);

	public void insertLatestByBatch(@Param("t20x1s") List<T20x1VO> t20x1s);
}
