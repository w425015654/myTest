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

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dps.vo.ExcludeTimeVO;
import com.zeei.das.dps.vo.PointSiteVO;

/**
 * 类 名 称：PointSiteDAO 类 描 述：mybatis接口，对应V_POINTSITE_INFO 功能描述： 创建作者：zhou.yongbo
 */

public interface PointSiteDAO {

	// 查找所有PointSystemVO记录
	public List<PointSiteVO> getPointSiteList();

	// 获取下一个测点主键
	public Integer nextPointCode();

	// 新建一个测点
	public void insertSite(@Param("table") String table, @Param("MN") String MN, @Param("ST") String ST);

	// 根据站点MN号查询站点信息
	public PointSiteVO getStation(@Param("MN") String MN);

	// 创建数据表
	public void createDataTable(@Param("table") String table, @Param("pkname") String pk);

	// 创建数据表索引
	public void createIndex(@Param("indexSql") String indexSql);

	/**
	 * 
	 * querySystemTables:查找系统所有的表名
	 *
	 * @param tableSchema
	 * @return List<String>
	 */
	public List<String> querySystemTables(@Param("tableSchema") String tableSchema);
	
	/**
	 * 
	 * queryExceptionTime:查询异常申报时段
	 *
	 * @return List<AlarmVO>
	 */

	public List<ExcludeTimeVO> queryExceptionTime();

	/**
	 * 
	 * queryRegularStopTime:查询规律性停产时段
	 * 
	 * @return List<RegularStopTimeVO>
	 */
	public List<ExcludeTimeVO> queryRegularStopTime();
}
