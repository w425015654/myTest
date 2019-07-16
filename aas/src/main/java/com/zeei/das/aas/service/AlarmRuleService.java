/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmRuleService.java
* 包  名  称：com.zeei.das.aas.service
* 文件描述：初始化告警规则配置接口
* 创建日期：2017年4月20日下午12:41:45
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月20日下午12:41:45 创建文件
*
*/

package com.zeei.das.aas.service;

import java.util.List;
import java.util.Map;

import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.AlarmRuleVO;
import com.zeei.das.aas.vo.ExcludeTimeVO;
import com.zeei.das.aas.vo.RegionDataVO;
import com.zeei.das.aas.vo.STVO;
import com.zeei.das.aas.vo.StationVO;

/**
 * 类 名 称：AlarmRuleService 类 描 述：初始化告警规则配置接口 功能描述：申明初始化告警规则接口方法
 * 创建作者：quanhongsheng
 */

public interface AlarmRuleService {
	/**
	 * 
	 * queryAlarmRule:获取站点告警配置
	 *
	 * @return List<AlarmRuleVO>
	 */
	public List<AlarmRuleVO> queryAlarmRule();
	
	/**
	 * 
	 * queryCustomAlarmRule:获取定制告警配置
	 *
	 * @return List<AlarmRuleVO>
	 */
	public List<AlarmRuleVO> queryCustomAlarmRule();

	/**
	 * 
	 * getAlarmRule:根据站点编号获取告警配置
	 *
	 * @param MN
	 *            站点MN
	 * @return AlarmRuleVO
	 */
	public List<AlarmRuleVO> getAlarmRule(String MN);

	/**
	 * 
	 * queryStation:获取站点配置
	 *
	 * @return List<AlarmRuleVO>
	 */
	public List<StationVO> queryStation();

	/**
	 * 
	 * getStation:根据站点编号获取站点配置
	 *
	 * @param MN
	 *            站点MN
	 * @return AlarmRuleVO
	 */
	public StationVO getStation(String MN);

	/**
	 * 
	 * queryRegionStation:获取区域站点数据
	 *
	 * @return RegionDataVO
	 */
	public List<RegionDataVO> queryRegionStation();

	/**
	 * 
	 * getRegionStation:根据区域编码获取区域站点数据
	 *
	 * @param regionCode
	 * @return RegionDataVO
	 */
	public RegionDataVO getRegionStation(String regionCode);

	/**
	 * 
	 * queryStatusMapAlarmCode:告警状态码关系
	 *
	 * @return List<Map<String,String>>
	 */
	public List<Map<String, String>> queryStatusMapAlarmCode();

	/**
	 * 
	 * queryAlarm:初始化系统中告警
	 *
	 * @return List<AlarmInfoVO>
	 */
	public List<AlarmInfoVO> queryAlarm();

	/**
	 * 
	 * querySTInfo:查询系统配置信息
	 *
	 * @return List<STVO>
	 */
	public List<STVO> querySTInfo();

	/**
	 * 
	 * queryExceptionTime:查询异常申报时段
	 *
	 * @return List<AlarmVO>
	 */

	public List<ExcludeTimeVO> queryExceptionTime();

	/**
	 * 
	 * queryRegularStopTime:查询规律性停产时段
	 * 
	 * @return List<RegularStopTimeVO>
	 */
	public List<ExcludeTimeVO> queryRegularStopTime();

	/**
	 * 
	 * queryDrainageTime:查询企业排水时段
	 * 
	 * @return String
	 */
	public String queryDrainageTime(String psCode, Integer yearMN);
	
	/**
	 * 
	 * queryUpsideAlmRule:查询倒挂告警站点
	 * 
	 * @return List<STVO>
	 */
	public List<STVO> queryUpsideAlmRule();
	/**
	 * 
	 * queryUpsideAlmRule:查询区域超标告警告警站点
	 * 
	 * @return List<STVO>
	 */
	public List<STVO> queryAreaExceedAlmRule();
}
