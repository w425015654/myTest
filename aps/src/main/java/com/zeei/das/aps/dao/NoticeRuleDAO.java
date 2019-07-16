/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：aps
* 文件名称：NoticeRuleDAO.java
* 包  名  称：com.zeei.das.aps.dao
* 文件描述：mybatis 通知规则更新处理接口
* 创建日期：2017年4月28日下午3:15:58
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月28日下午3:15:58 创建文件
*
*/

package com.zeei.das.aps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.aps.vo.AlarmDefVO;
import com.zeei.das.aps.vo.NoticeRuleVO;
import com.zeei.das.aps.vo.NoticeUserVO;
import com.zeei.das.aps.vo.PolluteVO;
import com.zeei.das.aps.vo.StationVO;

/**
 * 类 名 称：NoticeRuleDAO 类 描 述：mybatis 通知规则更新处理接口 功能描述：mybatis 通知规则更新处理接口
 * 创建作者：quanhongsheng
 */

public interface NoticeRuleDAO {

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
	public NoticeRuleVO getNoticeRule(@Param("ruleId") String ruleId);

	/**
	 * 
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
	public List<NoticeUserVO> getNoticeRuleUser(@Param("ruleId") String ruleId);

	public List<AlarmDefVO> queryAlarmDef();

	public List<StationVO> queryStations();

	public List<PolluteVO> queryPollutes();

}
