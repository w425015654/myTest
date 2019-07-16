/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StationServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：
* 创建日期：2017年5月11日上午8:21:33
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:21:33 创建文件
*
*/

package com.zeei.das.dss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.dss.dao.StationDao;
import com.zeei.das.dss.service.StationService;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.StationVO;
import com.zeei.das.dss.vo.SystemTableVO;

/**
 * 类 名 称：StationServiceImpl 类 描 述：
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
	public List<String> querySystemTables(String tableSchema) {
		
		return stationDao.querySystemTables(tableSchema);
	}

	@Override
	public void createDataTable(String table, String pk) {
		
		 stationDao.createDataTable(table , pk);
	}

	@Override
	public void createIndex(String indexSql) {
		
		stationDao.createIndex(indexSql);
	}

}
