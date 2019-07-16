/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：UpdatePointStatusServiceImpl.java
* 包  名  称：com.zeei.das.aas.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年6月6日下午2:45:36
* 
* 修改历史
* 1.0 quanhongsheng 2017年6月6日下午2:45:36 创建文件
*
*/

package com.zeei.das.aas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.aas.dao.UpdatePointStatusDao;
import com.zeei.das.aas.service.UpdatePointStatusService;
import com.zeei.das.aas.vo.STVO;

/**
 * 类 名 称：UpdatePointStatusServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Service("updatePointStatusService")
public class UpdatePointStatusServiceImpl implements UpdatePointStatusService {

	@Autowired
	UpdatePointStatusDao updatePointStatusDao;

	@Override
	public Integer updateStatusByBatch(String tableName, List<STVO> data) {

		return updatePointStatusDao.updateStatusByBatch(tableName, data);

	}

	@Override
	public Integer updateStatusByBatch2(String tableName, String status, List<String> data) {

		return updatePointStatusDao.updateStatusByBatch2(tableName, status, data);

	}

}
