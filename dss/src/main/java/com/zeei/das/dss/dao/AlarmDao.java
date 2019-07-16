/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AlarmDao.java
* 包  名  称：com.zeei.das.dss.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年7月21日上午11:31:02
* 
* 修改历史
* 1.0 quanhongsheng 2017年7月21日上午11:31:02 创建文件
*
*/

package com.zeei.das.dss.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dss.vo.AlarmVO;
import com.zeei.das.dss.vo.O38stdVO;
import com.zeei.das.dss.vo.RegularStopTimeVO;

/**
 * 类 名 称：AlarmDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface AlarmDao {

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
	public List<AlarmVO> queryAlarm(@Param("pointCode") String pointCode, @Param("bTime") Date bTime,
			@Param("eTime") Date eTime, @Param("alarmCodes") List<String> alarmCodes);

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
	public List<AlarmVO> queryAlarmByOutage(@Param("pointCode") String pointCode, @Param("bTime") Date bTime,
			@Param("eTime") Date eTime);

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

	public List<AlarmVO> queryExceptionTime(@Param("pointCode") String pointCode, @Param("bTime") Date bTime,
			@Param("eTime") Date eTime);

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
	public List<RegularStopTimeVO> queryRegularStopTime(@Param("pointCode") String pointCode,
			@Param("week") Integer week);

	/**
	 * 
	 * queryO38std:查询O38小时的标准限值
	 * 
	 * @return List<O38stdVO>
	 */
	public List<O38stdVO> queryO38std();

}
