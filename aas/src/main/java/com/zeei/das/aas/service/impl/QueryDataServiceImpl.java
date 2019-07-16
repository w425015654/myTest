/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：QueryMonitorDataServiceImpl.java
* 包  名  称：com.zeei.das.aas.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月29日下午3:10:18
* 
* 修改历史
* 1.0 wudahe 2018年9月29日下午3:10:18 创建文件
*
*/

package com.zeei.das.aas.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeei.das.aas.dao.QueryDataDao;
import com.zeei.das.aas.service.QueryDataService;
import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.PolluterLevelVo;

/** 
* @类型名称：QueryMonitorDataServiceImpl
* @类型描述：查询监测表
* @功能描述：查询监测表
* @创建作者：wudahe
*
*/
@Service("queryDataService")
public class QueryDataServiceImpl implements QueryDataService{
	@Autowired
	QueryDataDao queryDataDao;

	@Override
	public List<MonitorDataVO> queryBeforeDataByCondition(String tableName, String pointCode, Date beginTime,
			Date endTime, String polluteCode, String systemType) {
		return queryDataDao.queryBeforeDataByCondition(tableName, pointCode, beginTime, endTime, polluteCode,systemType);
	}
	
	@Override
	public List<MonitorDataVO> queryNextDataByCondition(String tableName, String pointCode, Date beginTime,
			Date endTime, String polluteCode, String systemType) {
		return queryDataDao.queryNextDataByCondition(tableName, pointCode, beginTime, endTime, polluteCode,systemType);
	}

	@Override
	public List<PolluterLevelVo> queryRivPolluteLevel() {
		
		return queryDataDao.queryRivPolluteLevel();
	}

	@Override
	public List<MonitorDataVO> queryMonitorData(String tableName,String systemType, String beginTime, String endTime,
			String pointCode, List<String> pollutes) {
		
		return queryDataDao.queryMonitorData(tableName,systemType, beginTime, endTime, pointCode, pollutes);
	}
	
	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的存疑  数据标识 为1
	 * @param datas
	 * @return Integer
	 */
	@Override
	@Transactional
	public Integer updateDoubtfulHH(List<MonitorDataVO> datas){
		
		return  queryDataDao.updateDoubtfulHH(datas);
	}
	
	/**
	 * 
	 * queryPollute:查询系统因子编码
	 * 
	 * @return List<PolluteVO>
	 */
	@Override
	public List<PolluteVO> queryPollute() {
		return queryDataDao.queryPollute();
	}
	
	@Override
	public List<PolluteVO> queryHourPollute() {
		
		return queryDataDao.queryHourPollute();
	}
	
}
