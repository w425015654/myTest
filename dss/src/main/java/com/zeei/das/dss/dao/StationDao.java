/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StationDao.java
* 包  名  称：com.zeei.das.dss.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月10日下午5:17:17
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月10日下午5:17:17 创建文件
*
*/

package com.zeei.das.dss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.StationVO;
import com.zeei.das.dss.vo.SystemTableVO;

/**
 * 类 名 称：StationDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface StationDao {

	/**
	 * 
	 * queryStationCfg:获取站点配置
	 *
	 * @return List<StationCfgVO>
	 */
	public List<StationVO> queryStationCfg();

	/**
	 * 
	 * getStationCfg:根据MN号获取站点配置
	 *
	 * @param MN
	 * @return StationCfgVO
	 */
	public StationVO getStationCfg(@Param("MN") String MN);

	/**
	 * 
	 * queryTableName:查询系统配置的表名
	 *
	 * @param MN
	 * @return SystemTableVO
	 */
	public List<SystemTableVO> queryTableName();

	/**
	 * 
	 * queryEmissionFactor:查询排量统计因子
	 *
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryEmissionFactor();
	
	/**
	 * 
	 * querySystemTables:查找系统所有的表名
	 *
	 * @param tableSchema
	 * @return List<String>
	 */
	public List<String> querySystemTables(@Param("tableSchema") String tableSchema);
	
	/**
	 * createDataTable:// 创建数据表
	 * 
	 * @param table
	 * @param pk void
	 */
	public void createDataTable(@Param("table") String table, @Param("pkname") String pk);
	
	/**
	 * createIndex:// 创建数据表索引
	 * 
	 * @param indexSql void
	 */
	public void createIndex(@Param("indexSql") String indexSql);

}
