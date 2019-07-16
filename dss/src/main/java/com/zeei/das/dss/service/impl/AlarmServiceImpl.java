/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AlarmServiceImpl.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月21日上午11:42:13
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月21日上午11:42:13 创建文件
*
*/

package com.zeei.das.dss.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.dss.dao.AlarmDao;
import com.zeei.das.dss.service.AlarmService;
import com.zeei.das.dss.vo.AlarmVO;
import com.zeei.das.dss.vo.O38stdVO;
import com.zeei.das.dss.vo.RegularStopTimeVO;

/**
 * 类 名 称：AlarmServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */
@Service("alarmService")
public class AlarmServiceImpl implements AlarmService {

	@Autowired
	AlarmDao alarmDao;

	@Override
	public List<AlarmVO> queryAlarm(String pointCode, Date bTime, Date eTime, List<String> alarmCodes) {

		return alarmDao.queryAlarm(pointCode, bTime, eTime, alarmCodes);

	}

	@Override
	public List<AlarmVO> queryExceptionTime(String pointCode, Date bTime, Date eTime) {

		return alarmDao.queryExceptionTime(pointCode, bTime, eTime);
	}

	@Override
	public List<RegularStopTimeVO> queryRegularStopTime(String pointCode, Integer week) {

		return alarmDao.queryRegularStopTime(pointCode, week);

	}

	@Override
	public List<AlarmVO> queryAlarmByOutage(String pointCode, Date bTime, Date eTime) {

		return alarmDao.queryAlarmByOutage(pointCode, bTime, eTime);

	}

	@Override
	public List<O38stdVO> queryO38std() {

		return alarmDao.queryO38std();

	}

}
