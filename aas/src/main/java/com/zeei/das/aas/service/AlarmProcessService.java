/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：AlarmProcessService.java
* 包  名  称：com.zeei.das.aas.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月29日下午3:14:01
* 
* 修改历史
* 1.0 wudahe 2018年9月29日下午3:14:01 创建文件
*
*/

package com.zeei.das.aas.service;

import java.util.Date;
import java.util.List;

import com.zeei.das.aas.vo.AlarmInfoVO;
import com.zeei.das.aas.vo.ExcludeTimeVO;

/**
 * @类型名称：AlarmProcessService
 * @类型描述：操作告警表
 * @功能描述：操作告警表
 * @创建作者：wudahe
 *
 */

public interface AlarmProcessService {
	/**
	 * 查询告警消息
	 * 
	 * @param alarm
	 * @return AlarmInfoVO
	 */
	public AlarmInfoVO queryAlarmInfoByBsCondition(AlarmInfoVO alarm);

	/**
	 * 修改告警信息
	 * 
	 * @param alarm
	 * @return 成功返回true, 失败返回false
	 */
	public boolean updateAlarmInfo(AlarmInfoVO alarm);
}
