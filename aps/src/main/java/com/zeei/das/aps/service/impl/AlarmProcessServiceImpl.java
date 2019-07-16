package com.zeei.das.aps.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zeei.das.aps.ApsService;
import com.zeei.das.aps.dao.AlarmInfoDAO;
import com.zeei.das.aps.service.AlarmProcessService;
import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.AlarmInfoVO;
import com.zeei.das.aps.vo.AlarmSyncVO;
import com.zeei.das.aps.vo.ExcludeTimeVO;
import com.zeei.das.aps.vo.StationVO;

@Service("alarmProcessService")
public class AlarmProcessServiceImpl implements AlarmProcessService {
	private static Logger logger = LoggerFactory.getLogger(AlarmProcessServiceImpl.class);

	@Autowired
	AlarmInfoDAO alarmInfoDAO;

	@Override
	public boolean insertAlarmInfo(AlarmInfoVO alarm) {

		try {
			AlarmInfoVO existed = alarmInfoDAO.queryAlarmInfoByCondition(alarm);
			if (existed == null) {

				if (alarm.getEndTime() != null) {
					alarm.setAlarmStatus(4);
				}
				
				if(alarmInfoDAO.insertAlarmInfo(alarm) > 0){
					//告警同步，中天纲中广核项目
					if("1".equals(ApsService.alarmSync)){
						AlarmSyncVO alarmSync = new AlarmSyncVO();
						StationVO station = ApsService.stationMap.get(String.valueOf(alarm.getPointCode()));
						AlarmDefVO alarmDef = ApsService.alarmDefMap.get(alarm.getAlarmCode());
						String pointName = station==null?String.valueOf(alarm.getPointCode()):station.getPointName();
						String alarmNme = alarmDef==null?alarm.getAlarmCode():alarmDef.getAlarmDesc();
						alarmSync.setSmsTitle(pointName + alarmNme);
						alarmSync.setSmsContent(JSON.toJSONStringWithDateFormat(alarm,
							"yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteDateUseDateFormat));
						
						alarmInfoDAO.insertAlarmSyncInfos(alarmSync);
					}
					return  true;
				}else{
					return  false;
				}
				
			} else {
				logger.info(String.format("告警消息已经入库，此条消息将被忽略。 %s", JSON.toJSONString(alarm)));
				return true;
			}
		} catch (Exception e) {
			logger.error("新增告警失败:"+e.toString());
			return false;
		}
	}

	@Override
	public boolean updateAlarmInfo(AlarmInfoVO alarm) {

		try {
			if (alarm.getEndTime() != null) {
				alarm.setAlarmStatus(4);
			}
			return alarmInfoDAO.updateAlarmInfo(alarm) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("更新告警消息失败:"+ e.toString());
			return false;
		}

	}
	
	@Override
	public boolean updateAlarmPush() {

		try {
			return alarmInfoDAO.updateAlarmPush() > 0 ? true : false;
		} catch (Exception e) {
			logger.error("更新告警消息失败:"+ e.toString());
			return false;
		}

	}
	
	@Override
	public List<AlarmInfoVO> queryAlarmInfo(Date beginTime, Date endTime) {

		return alarmInfoDAO.queryAlarmInfo(beginTime, endTime);

	}
	
	@Override
	public List<AlarmInfoVO> scanAlarmInfo(Date beginTime, Date endTime) {

		return alarmInfoDAO.scanAlarmInfo(beginTime, endTime);

	}
	
	@Override
	public List<ExcludeTimeVO> queryExceptionTime() {

		return alarmInfoDAO.queryExceptionTime();
	}

	@Override
	public List<ExcludeTimeVO> queryRegularStopTime() {

		return alarmInfoDAO.queryRegularStopTime();

	}

	@Override
	public boolean delAlarmInfo(AlarmInfoVO alarm) {
		try {
			return alarmInfoDAO.delAlarmInfo(alarm) > 0 ? true : false;
		} catch (Exception e) {
		    	logger.error("删除告警失败:" + e.toString());
			return false;
		}

	}

	@Override
	public boolean cannelAlarm(AlarmInfoVO alarm) {

		try {
			if (alarm.getEndTime() != null) {
				alarm.setAlarmStatus(4);
			}
			return alarmInfoDAO.cannelAlarm(alarm) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("消警失败:" + e.toString());
			return false;
		}

	}

	@Override
	public void insertAlarmSyncInfos(AlarmSyncVO alarmSync) {
		
		alarmInfoDAO.insertAlarmSyncInfos(alarmSync);
	}

}
