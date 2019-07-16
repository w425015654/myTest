/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmProcessServiceImpl.java
* 包  名  称：com.zeei.das.aas.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月29日下午3:14:48
* 
* 修改历史
* 1.0 wudahe 2018年9月29日下午3:14:48 创建文件
*
*/

package com.zeei.das.aas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.aas.dao.AlarmInfoDAO;
import com.zeei.das.aas.service.AlarmProcessService;
import com.zeei.das.aas.vo.AlarmInfoVO;

/**
 * @类型名称：AlarmProcessServiceImpl
 * @类型描述：操作告警表
 * @功能描述：操作告警表
 * @创建作者：wudahe
 *
 */

@Service("alarmProcessService")
public class AlarmProcessServiceImpl implements AlarmProcessService {
	private static Logger logger = LoggerFactory.getLogger(AlarmProcessServiceImpl.class);

	@Autowired
	AlarmInfoDAO alarmInfoDAO;

	@Override
	public AlarmInfoVO queryAlarmInfoByBsCondition(AlarmInfoVO alarm) {

		return alarmInfoDAO.queryAlarmInfoByBsCondition(alarm);
	}

	@Override
	public boolean updateAlarmInfo(AlarmInfoVO alarm) {
		try {
			return alarmInfoDAO.updateAlarmInfo(alarm) > 0 ? true : false;
		} catch (Exception e) {
			logger.error("更新告警消息失败:" + e.toString());
			return false;
		}

	}
}
