/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeRuleService.java
* 包  名  称：com.zeei.das.aps.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月2日上午10:06:21
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午10:06:21 创建文件
*
*/

package com.zeei.das.aps.service;

import java.util.List;

import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.NoticeRuleVO;
import com.zeei.das.aps.vo.NoticeUserVO;
import com.zeei.das.aps.vo.PolluteVO;
import com.zeei.das.aps.vo.StationVO;

/**
 * 类 名 称：NoticeRuleService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public interface NoticeRuleService {

	/**
	 * 
	 * queryNoticeRule:获取所有告警通知规则
	 *
	 * @return List<NoticeRuleVO>
	 */
	public List<NoticeRuleVO> queryNoticeRule();

	/**
	 * 
	 * getNoticeRule:根据ID获取所有告警通知规则
	 *
	 * @param ruleId
	 *            通知规则ID
	 * @return NoticeRuleVO
	 */
	public NoticeRuleVO getNoticeRule(String ruleId);

	/**
	 * queryNoticeRuleUser:获取通知规则-人员
	 *
	 * @return List<NoticeVO>
	 */
	public List<NoticeUserVO> queryNoticeRuleUser();

	/**
	 * getNoticeRuleUser:根据id获取通知规则-人员
	 *
	 * @param ruleId
	 * @return List<NoticeVO>
	 */
	public List<NoticeUserVO> getNoticeRuleUser(String ruleId);
	
	public List<AlarmDefVO> queryAlarmDef();

	public List<StationVO> queryStations();

	public List<PolluteVO> queryPollutes();
}
