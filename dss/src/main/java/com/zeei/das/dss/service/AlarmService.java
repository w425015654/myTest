/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AlarmService.java
* 包  名  称：com.zeei.das.dss.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月21日上午11:39:52
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月21日上午11:39:52 创建文件
*
*/

package com.zeei.das.dss.service;

import java.util.Date;
import java.util.List;

import com.zeei.das.dss.vo.AlarmVO;
import com.zeei.das.dss.vo.O38stdVO;
import com.zeei.das.dss.vo.RegularStopTimeVO;

/**
 * 类 名 称：AlarmService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface AlarmService {

	/**
	 * 
	 * queryAlarm:查询告警
	 *
	 * @param pointCode
	 *            测点编号
	 * @param bTime
	 *            统计开始时间
	 * @param eTime
	 *            统计计算时间
	 * @param alarmCodes
	 *            告警码
	 * @return List<AlarmVO>
	 */
	public List<AlarmVO> queryAlarm(String pointCode, Date bTime, Date eTime, List<String> alarmCodes);

	/**
	 * 
	 * queryAlarmByOutage:查询告警处理为设施停运的告警
	 *
	 * @param pointCode
	 *            测点编号
	 * @param bTime
	 *            统计开始时间
	 * @param eTime
	 *            统计计算时间
	 * @return List<AlarmVO>
	 */
	public List<AlarmVO> queryAlarmByOutage(String pointCode, Date bTime, Date eTime);

	/**
	 * 
	 * queryExceptionTime:查询异常申报时段
	 *
	 * @param pointCode
	 *            测点id
	 * @param bTime
	 *            统计开始时间
	 * @param eTime
	 *            统计计算时间
	 * @return List<AlarmVO>
	 */

	public List<AlarmVO> queryExceptionTime(String pointCode, Date bTime, Date eTime);

	/**
	 * 
	 * queryRegularStopTime:查询规律性停产时段
	 *
	 * @param pointCode
	 *            测点编码
	 * @param week
	 *            周数
	 * @return List<RegularStopTimeVO>
	 */
	public List<RegularStopTimeVO> queryRegularStopTime(String pointCode, Integer week);
	
	/**
	 * 
	 * queryO38std:查询O38小时的标准限值
	 * 
	 * @return List<O38stdVO>
	 */
	public List<O38stdVO> queryO38std();
}
