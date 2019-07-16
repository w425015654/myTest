package com.zeei.das.aps.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.AlarmSyncVO;
import com.zeei.das.aps.vo.ExcludeTimeVO;

public interface AlarmInfoDAO {

	public AlarmInfoVO queryAlarmInfoByCondition(AlarmInfoVO baseAlarm);

	public Integer insertAlarmInfo(AlarmInfoVO baseAlarm);

	public Integer updateAlarmInfo(AlarmInfoVO baseAlarm);
	
	public Integer updateAlarmPush();
	
	public Integer cannelAlarm(AlarmInfoVO baseAlarm);

	public Integer delAlarmInfo(AlarmInfoVO baseAlarm);

	/**
	 * 
	 * queryAlarmInfo:
	 *
	 * @param beginTime
	 * @param endTime
	 * @return List<AlarmInfoVO>
	 */
	public List<AlarmInfoVO> queryAlarmInfo(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);
	
	/**
	 * 
	 * scanAlarmInfo:
	 *
	 * @param beginTime
	 * @param endTime
	 * @return List<AlarmInfoVO>
	 */
	public List<AlarmInfoVO> scanAlarmInfo(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

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
