/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmRuleServiceImpl.java
* 包  名  称：com.zeei.das.aas.service.impl
* 文件描述：初始化告警规则配置接口实现
* 创建日期：2017年4月20日下午12:43:19
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日下午12:43:19 创建文件
*
*/

package com.zeei.das.aas.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.aas.dao.AlarmRuleDao;
import com.zeei.das.aas.service.AlarmRuleService;
import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.aas.vo.ExcludeTimeVO;
import com.zeei.das.aas.vo.RegionDataVO;
import com.zeei.das.aas.vo.STVO;
import com.zeei.das.aas.vo.StationVO;

/**
 * 类 名 称：AlarmRuleServiceImpl 类 描 述：初始化告警规则配置接口实现 功能描述：初始化告警规则配置接口实现
 * 创建作者：quanhongsheng
 */

@Service("alarmRuleService")
public class AlarmRuleServiceImpl implements AlarmRuleService {

	@Autowired
	AlarmRuleDao alarmRuleDao;

	public List<AlarmRuleVO> queryAlarmRule() {
		return alarmRuleDao.queryAlarmRule();
	}

	public List<AlarmRuleVO> getAlarmRule(String MN) {
		return alarmRuleDao.getAlarmRule(MN);
	}

	@Override
	public List<StationVO> queryStation() {
		return alarmRuleDao.queryStation();
	}

	@Override
	public StationVO getStation(String MN) {

		return alarmRuleDao.getStation(MN);
	}

	@Override
	public List<RegionDataVO> queryRegionStation() {
		return alarmRuleDao.queryRegionStation();
	}

	@Override
	public RegionDataVO getRegionStation(String regionCode) {
		return alarmRuleDao.getRegionStation(regionCode);
	}

	@Override
	public List<Map<String, String>> queryStatusMapAlarmCode() {
		return alarmRuleDao.queryStatusMapAlarmCode();
	}

	@Override
	public List<AlarmInfoVO> queryAlarm() {
		return alarmRuleDao.queryAlarm();
	}

	@Override
	public List<STVO> querySTInfo() {

		return alarmRuleDao.querySTInfo();

	}

	@Override
	public List<ExcludeTimeVO> queryExceptionTime() {
		return alarmRuleDao.queryExceptionTime();
	}

	@Override
	public List<ExcludeTimeVO> queryRegularStopTime() {
		return alarmRuleDao.queryRegularStopTime();
	}

	@Override
	public String queryDrainageTime(String psCode, Integer yearMN) {

		return alarmRuleDao.queryDrainageTime(psCode, yearMN);
	}

	@Override
	public List<AlarmRuleVO> queryCustomAlarmRule() {

		return alarmRuleDao.queryCustomAlarmRule();

	}

	/**
	 * 
	 * queryUpsideAlmRule:查询倒挂告警站点
	 * 
	 * @return List<STVO>
	 */
	@Override
	public List<STVO> queryUpsideAlmRule(){

		return alarmRuleDao.queryUpsideAlmRule();
	}
	
	/**
	 * 
	 * queryUpsideAlmRule:查询区域超标告警告警站点
	 * 
	 * @return List<STVO>
	 */
	public List<STVO> queryAreaExceedAlmRule(){

		return alarmRuleDao.queryAreaExceedAlmRule();
	}
}
