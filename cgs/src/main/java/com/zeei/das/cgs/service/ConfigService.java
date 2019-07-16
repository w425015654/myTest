/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ConfigService.java
* 包  名  称：com.zeei.das.cgs.service
* 文件描述：初始化配置接口
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.service;

import java.util.List;

import com.zeei.das.cgs.vo.FormulaVo;
import com.zeei.das.cgs.vo.ParamCfgVO;
import com.zeei.das.cgs.vo.PointSystemVO;
import com.zeei.das.cgs.vo.StationCfgVO;

/**
 * 类 名 称：ConfigService 类 描 述：初始化配置接口 功能描述：申明初始化服务接口方法 创建作者：quanhongsheng
 */

public interface ConfigService {

	/**
	 * 
	 * queryStationCfg:获取站点配置
	 *
	 * @return List<StationCfgVO>
	 */
	public List<StationCfgVO> queryStationCfg();

	/**
	 * 
	 * getStationCfg:根据MN号获取站点配置
	 *
	 * @param MN
	 * @return StationCfgVO
	 */
	public StationCfgVO getStationCfg(String MN);

	/**
	 * 
	 * queryParamCfg:获取因子转码配置
	 *
	 * @return List<ParamCfgVO>
	 */
	public List<ParamCfgVO> queryParamCfg();

	/**
	 * 
	 * insertSite:新增站点
	 *
	 * @param table
	 *            表名
	 * @param MN
	 * @param ST
	 *            系统类型
	 * 
	 *            void
	 */
	public void insertSite(String table, String MN, String ST);

	/**
	 * 
	 * queryPointSystem:查询测点系统配置
	 *
	 * @return List<PointSystemVO>
	 */
	public List<PointSystemVO> queryPointSystem();
	
	/**
	 * 
	 * queryFormulaInfo:查询测点系统配置
	 *
	 * @return List<FormulaVo>
	 */
	public List<FormulaVo> queryFormulaInfo();

}
