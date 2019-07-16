/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cgs
* 文件名称：ConfigServiceImpl.java
* 包  名  称：com.zeei.das.cgs.service.impl
* 文件描述：初始化配置接口实现
* 创建日期：2017年4月17日下午4:36:24
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月17日下午4:36:24 创建文件
*
*/

package com.zeei.das.cgs.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.cgs.dao.ConfigDao;
import com.zeei.das.cgs.service.ConfigService;
import com.zeei.das.cgs.vo.FormulaVo;
import com.zeei.das.cgs.vo.ParamCfgVO;
import com.zeei.das.cgs.vo.PointSystemVO;
import com.zeei.das.cgs.vo.StationCfgVO;

/**
 * 类 名 称：ConfigServiceImpl 类 描 述：初始化配置接口实现 功能描述：实现初始化服务接口方法 创建作者：quanhongsheng
 */

@Service("configService")
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	ConfigDao configDao;

	@Override
	public List<StationCfgVO> queryStationCfg() {
		return configDao.queryStationCfg();
	}

	@Override
	public StationCfgVO getStationCfg(String MN) {
		return configDao.getStationCfg(MN);
	}

	@Override
	public List<ParamCfgVO> queryParamCfg() {
		return configDao.queryParamCfg();
	}

	@Override
	public void insertSite(String table, String MN, String ST) {
		configDao.insertSite(table, MN, ST);
	}

	@Override
	public List<PointSystemVO> queryPointSystem() {

		return configDao.queryPointSystem();

	}

	@Override
	public List<FormulaVo> queryFormulaInfo() {
		
		return configDao.queryFormulaInfo();
	}

}
