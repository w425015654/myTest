/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StationService.java
* 包  名  称：com.zeei.das.dss.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午8:20:16
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:20:16 创建文件
*
*/

package com.zeei.das.aas.service;

import java.util.List;

import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.SystemTableVO;

/**
 * 类 名 称：StationService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface StationService {

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
	public StationVO getStationCfg(String MN);

	/**
	 * 
	 * queryTableName:查询系统配置的表名
	 *
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
	 * queryEmissionFactor:查询流量因子
	 *
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryFlowFactor();

}
