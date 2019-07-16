/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：StroageServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月18日下午1:29:58
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月18日下午1:29:58 创建文件
*
*/

package com.zeei.das.dss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.dss.dao.StorageDao;
import com.zeei.das.dss.service.StroageService;
import com.zeei.das.dss.vo.MonitorDataVO;

/**
 * 类 名 称：StroageServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Service("stroageService")
public class StroageServiceImpl implements StroageService {

	@Autowired
	StorageDao storageDao;

	@Override
	public Integer insertData(String tableName, MonitorDataVO data) {

		return storageDao.insertData(tableName, data);
	}

	@Override
	public Integer insertDataBatch(String tableName, List<MonitorDataVO> datas) {

		return storageDao.insertDataBatch(tableName, datas);
	}

	@Override
	public Integer insertYMDataByBatch(String tableName, List<MonitorDataVO> data) {

		return storageDao.insertYMDataByBatch(tableName, data);
	}

	@Override
	public Integer insertYMData(String tableName, MonitorDataVO data) {

		return storageDao.insertYMData(tableName, data);

	}

	@Override
	public Integer insertAuditDataBatch(String tableName, List<MonitorDataVO> datas) {

		return storageDao.insertAuditDataBatch(tableName, datas);

	}

	@Override
	public Integer reviewData(String tableName, String pointCode, String beginTime, String endTime) {

		return storageDao.reviewData(tableName, pointCode, beginTime, endTime);

	}

	@Override
	public Integer auditData(String tableName, String pointCode, String beginTime, String endTime) {

		return storageDao.auditData(tableName, pointCode, beginTime, endTime);
		
	}

}
