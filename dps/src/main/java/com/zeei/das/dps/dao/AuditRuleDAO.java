/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：AuditRuleDAO.java
* 包  名  称：com.zeei.das.dps.dao
* 文件描述：自动审核规则 接口
* 创建日期：2017年4月27日下午5:55:56
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日下午5:55:56 创建文件
*
*/

package com.zeei.das.dps.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dps.vo.DataFlagAuditVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.ZeroNegativeVO;

/**
 * 类 名 称：AuditRuleDAO 类 描 述：自动审核规则 接口 功能描述：自动审核规则 接口 创建作者：quanhongsheng
 */

public interface AuditRuleDAO {

	/**
	 * 
	 * queryAuditRule:查询所有的自动审核规则
	 *
	 * @return List<AuditRuleVO>
	 */
	public List<ZeroNegativeVO> queryZeroAuditRule();

	/**
	 * 
	 * getAuditRule:根据站点Id 获取站点自动审核
	 *
	 * @param MN
	 * @return List<AuditRuleVO>
	 */
	public List<ZeroNegativeVO> getZeroAuditRuleByMN(@Param("pointCode") String pointCode);

	/**
	 * 
	 * queryAuditRule:查询所有的数据标识审核规则
	 *
	 * @return List<AuditRuleVO>
	 */
	public List<DataFlagAuditVO> queryDataFlagAuditRule();

	/**
	 * 
	 * queryPollute:查询系统因子编码
	 * 
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute();

}
