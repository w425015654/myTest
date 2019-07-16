/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StationServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午8:21:33
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:21:33 创建文件
*
*/

package com.zeei.das.aas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.aas.dao.StationDao;
import com.zeei.das.aas.service.StationService;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.StationVO;
import com.zeei.das.aas.vo.SystemTableVO;

/**
 * 类 名 称：StationServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Service("StationService")
public class StationServiceImpl implements StationService {

	@Autowired
	StationDao stationDao;

	@Override
	public List<StationVO> queryStationCfg() {
		return stationDao.queryStationCfg();

	}

	@Override
	public StationVO getStationCfg(String MN) {
		return stationDao.getStationCfg(MN);
	}

	@Override
	public List<SystemTableVO> queryTableName() {
		return stationDao.queryTableName();

	}

	@Override
	public List<PolluteVO> queryEmissionFactor() {

		return stationDao.queryEmissionFactor();

	}
	
	@Override
	public List<PolluteVO> queryFlowFactor() {

		return stationDao.queryFlowFactor();

	}
}
