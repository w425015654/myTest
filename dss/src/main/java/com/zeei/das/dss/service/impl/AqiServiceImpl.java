/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AqiServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月18日下午1:38:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月18日下午1:38:39 创建文件
*
*/

package com.zeei.das.dss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.common.utils.DateUtil;
import com.zeei.das.dss.dao.AqiDao;
import com.zeei.das.dss.service.AqiService;
import com.zeei.das.dss.vo.AQILevelVO;
import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AirOprecDetlVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AreaVO;
import com.zeei.das.dss.vo.IAQIRangeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.ParamsVo;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;

/**
 * 类 名 称：AqiServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Service("aqiService")
public class AqiServiceImpl implements AqiService {

	@Autowired
	AqiDao aqiDao;

	@Override
	public Integer insertAQIDD(AqiDataVO data) {

		return aqiDao.insertAQIDD(data);
	}

	@Override
	public Integer insertAQIHH(AqiDataVO data) {

		return aqiDao.insertAQIHH(data);

	}

	@Override
	public List<MonitorDataVO> queryMointorDataHH(List<String> pointCode, List<String> areaCodes, List<String> polluteCodes,
			String beginTime, String endTime) {

		return aqiDao.queryMointorDataHH(pointCode, areaCodes, polluteCodes, beginTime, endTime);

	}

	@Override
	public List<MonitorDataVO> queryMointorDataDD(String pointCode, List<String> areaCodes, List<String> polluteCodes,
			String beginTime, String endTime) {
		return aqiDao.queryMointorDataDD(pointCode, areaCodes, polluteCodes, beginTime, endTime);

	}

	@Override
	public List<IAQIRangeVO> queryIAQIRange() {

		return aqiDao.queryIAQIRange();

	}

	@Override
	public List<AQILevelVO> queryAQILevel() {

		return aqiDao.queryAQILevel();

	}

	@Override
	public List<AreaVO> queryArea() {

		return aqiDao.queryArea();

	}
	
	@Override
	public List<PolluteVO> queryPollute() {

		return aqiDao.queryPollute();
	}

	@Override
	public List<ParamsVo> queryAreaPoint() {

		return aqiDao.queryAreaPoint();
	}

	@Override
	public List<MonitorDataVO> queryMonData(String beginTime, String endTime) {
		
		return aqiDao.queryMonData(beginTime, endTime);
	}

	@Override
	public Integer updateOutlierHH(List<MonitorDataVO> datas) {
		
		return aqiDao.updateOutlierHH(datas);
	}

	@Override
	public String getBETime(String code, String beginDate, String endDate, String flag) {
		
		return aqiDao.getBETime(code, beginDate, endDate, flag);
	}

	@Override
	public Integer insertAirHvy(AirHvyDayVo data) {
		
		data.setUpdateTime(DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		data.setPollNameList(String.join(",", data.getPollutes()));
		return aqiDao.insertAirHvy(data);
	}

	@Override
	public List<MonitorDataVO> queryMointorDataMM(List<String> pointCodes, String tableName, List<String> polluteCodes,
			String beginTime, String endTime) {
		
		return aqiDao.queryMointorDataMM(tableName,pointCodes,beginTime, endTime,polluteCodes);
	}

	@Override
	public Integer insertPollIncident(PollIncidentVo data) {
		
		 aqiDao.insertPollIncident(data);
		 
		 return data.getRecId();
	}

	@Override
	public Integer updatePollIncident(List<PollIncidentVo> datas) {
		
		return aqiDao.updatePollIncident(datas);
	}

	@Override
	public Integer insertAirOprecDetls(List<AirOprecDetlVo> datas) {
		
		return aqiDao.insertAirOprecDetls(datas);
	}

	@Override
	public Integer insertMinAqiDatas(List<AqiDataVO> datas) {
		
		return aqiDao.insertMinAqiDatas(datas);
	}


}
