/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeRuleServiceImpl.java
* 包  名  称：com.zeei.das.aps.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月2日上午9:59:03
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月2日上午9:59:03 创建文件
*
*/

package com.zeei.das.aps.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeei.das.aps.dao.NoticeRuleDAO;
import com.zeei.das.aps.service.NoticeRuleService;
import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.NoticeRuleVO;
import com.zeei.das.aps.vo.NoticeUserVO;
import com.zeei.das.aps.vo.PolluteVO;
import com.zeei.das.aps.vo.StationVO;

/**
 * 类 名 称：NoticeRuleServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Service("noticeRuleService")
public class NoticeRuleServiceImpl implements NoticeRuleService {

	@Autowired
	NoticeRuleDAO noticeRuleDAO;

	/**
	 * queryNoticeRule:获取所有告警通知规则
	 *
	 * @return List<NoticeRuleVO>
	 */
	@Transactional(readOnly = true)
	public List<NoticeRuleVO> queryNoticeRule() {

		return noticeRuleDAO.queryNoticeRule();
	}

	/**
	 * 
	 * getNoticeRule:根据ID获取所有告警通知规则
	 *
	 * @param ruleId
	 *            通知规则ID
	 * @return NoticeRuleVO
	 */
	@Transactional(readOnly = true)
	public NoticeRuleVO getNoticeRule(String ruleId) {
		return noticeRuleDAO.getNoticeRule(ruleId);
	}

	/**
	 * 
	 * queryNoticeRuleUser:获取通知规则-人员
	 *
	 * @return List<NoticeVO>
	 */
	@Transactional(readOnly = true)
	public List<NoticeUserVO> queryNoticeRuleUser() {
		return noticeRuleDAO.queryNoticeRuleUser();
	}

	/**
	 * getNoticeRuleUser:根据id获取通知规则-人员
	 *
	 * @param ruleId
	 * @return List<NoticeVO>
	 */
	@Transactional(readOnly = true)
	public List<NoticeUserVO> getNoticeRuleUser(String ruleId) {
		return noticeRuleDAO.getNoticeRuleUser(ruleId);
	}

	
	@Override
	public List<AlarmDefVO> queryAlarmDef() {
		return noticeRuleDAO.queryAlarmDef();
	}

	@Override
	public List<StationVO> queryStations() {
		return noticeRuleDAO.queryStations();
	}

	@Override
	public List<PolluteVO> queryPollutes() {
		return noticeRuleDAO.queryPollutes();
	}
}
