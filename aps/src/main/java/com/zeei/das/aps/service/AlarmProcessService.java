/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：AlarmProcess.java
* 包  名  称：com.zeei.das.aps.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月2日下午3:35:58
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日下午3:35:58 创建文件
*
*/

package com.zeei.das.aps.service;

import java.util.Date;
import java.util.List;

import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.AlarmSyncVO;
import com.zeei.das.aps.vo.ExcludeTimeVO;

/**
 * 类 名 称：AlarmProcess 类 描 述：mybatis告警入库接口
 */

public interface AlarmProcessService {

	/**
	 * 向数据库写入一条告警消息
	 * 
	 * @param alarm
	 * @return 成功返回true, 失败返回false
	 */
	public boolean insertAlarmInfo(AlarmInfoVO alarm);

	/**
	 * 修改告警信息
	 * 
	 * @param alarm
	 * @return 成功返回true, 失败返回false
	 */
	public boolean updateAlarmInfo(AlarmInfoVO alarm);
	
	/**
	 * 修改告警信息isPush字段为1
	 * 
	 * @param alarm
	 * @return 成功返回true, 失败返回false
	 */
	public boolean updateAlarmPush();
	
	/**
	 * 对参数alarm代表的告警进行消警
	 * 
	 * @param alarm
	 * @return 成功返回true, 失败返回false
	 */
	public boolean cannelAlarm(AlarmInfoVO alarm);

	/**
	 * 
	 * delAlarmInfo:TODO 请修改方法功能描述
	 *
	 * @param baseAlarm
	 * @return Integer
	 */
	public boolean delAlarmInfo(AlarmInfoVO baseAlarm);

	/**
	 * 
	 * queryAlarmInfo:
	 *
	 * @param beginTime
	 * @param endTime
	 * @return List<AlarmInfoVO>
	 */
	public List<AlarmInfoVO> queryAlarmInfo(Date beginTime, Date endTime);

	/**
	 * 查询未发送短信的告警消息
	 * 
	 * @param alarm
	 * @return scanAlarmInfo
	 */
	public List<AlarmInfoVO> scanAlarmInfo(Date beginTime, Date endTime);
	
	
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
	 * insertAlarmSyncInfos:插入告警同步信息
	 *  void
	 */
	public void insertAlarmSyncInfos(AlarmSyncVO alarmSync);

}
