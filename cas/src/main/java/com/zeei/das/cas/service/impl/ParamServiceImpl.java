/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：ParamServiceImpl.java
* 包  名  称：com.zeei.das.cas.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月3日下午3:55:10
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午3:55:10 创建文件
*
*/

package com.zeei.das.cas.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.cas.dao.ParamDao;
import com.zeei.das.cas.service.ParamService;
import com.zeei.das.cas.vo.ParamVO;

/**
 * 类 名 称：ParamServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Service("paramService")
public class ParamServiceImpl implements ParamService {

	@Autowired
	ParamDao paramDao;

	@Override
	public List<ParamVO> queryParam() {
		return paramDao.queryParam();
	}

	@Override
	public List<ParamVO> queryParamByMN(String MN) {
		return paramDao.queryParamByMN(MN);
	}

	@Override
	public List<Map<String, String>> queryParamMapping() {
		return paramDao.queryParamMapping();
	}

}
