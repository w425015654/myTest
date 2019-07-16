/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：QueryDataServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午9:29:01
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午9:29:01 创建文件
*
*/

package com.zeei.das.dss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.dss.dao.QueryDataDao;
import com.zeei.das.dss.service.QueryDataService;
import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AuditLogVO;
import com.zeei.das.dss.vo.DataTimeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.SitePolluterVo;

/**
 * 类 名 称：QueryDataServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Service("queryDataService")
public class QueryDataServiceImpl implements QueryDataService {

	@Autowired
	QueryDataDao queryDataDao;

	@Override
	public List<MonitorDataVO> queryDataByCondition(String tableName, String pointCode, String beginTime,
			String endTime, List<String> polluteCodes, String systemType) {
		return queryDataDao.queryDataByCondition(tableName, pointCode, beginTime, endTime, polluteCodes, systemType);
	}

	@Override
	public List<PolluteVO> queryPollute() {
		return queryDataDao.queryPollute();
	}

	@Override
	public List<DataTimeVO> queryDataTime(String tableName, String beginTime, String endTime) {
		return queryDataDao.queryDataTime(tableName, beginTime, endTime);
	}

	@Override
	public List<MonitorDataVO> queryYMDataByCondition(String tableName, String dataType, String pointCode,
			String beginTime, String endTime, List<String> polluteCodes, String systemType) {

		return queryDataDao.queryYMDataByCondition(tableName, dataType, pointCode, beginTime, endTime, polluteCodes,
				systemType);

	}

	@Override
	public List<MonitorDataVO> queryRCDataByCondition(String tableName, String pointCode, String beginTime,
			String endTime) {

		return queryDataDao.queryRCDataByCondition(tableName, pointCode, beginTime, endTime);

	}

	@Override
	public List<String> queryVPollute() {

		return queryDataDao.queryVPollute();

	}

	@Override
	public List<AuditLogVO> queryAuditLog(String pointCode, String beginTime, String endTime) {

		return queryDataDao.queryAuditLog(pointCode, beginTime, endTime);

	}

	@Override
	public List<MonitorDataVO> queryAuditData(String tableName, String pointCode, String beginTime, String endTime) {

		return queryDataDao.queryAuditData(tableName, pointCode, beginTime, endTime);

	}

	@Override
	public List<SitePolluterVo> queryValidPoll() {

		return queryDataDao.queryValidPoll();
	}

	@Override
	public List<PolluteVO> queryMaxPollValue() {

		return queryDataDao.queryMaxPollValue();
	}

	@Override
	public List<AirHvyDayVo> queryAirHvyDays() {

		return queryDataDao.queryAirHvyDays();
	}

	@Override
	public List<PollIncidentVo> queryPollIncident() {

		return queryDataDao.queryPollIncident();
	}

	@Override
	public List<AqiDataVO> queryNowHvys(String beginTime, String endTime) {

		return queryDataDao.queryNowHvys(beginTime, endTime);
	}

}
