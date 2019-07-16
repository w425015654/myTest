/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：CfgServiceImpl.java
* 包  名  称：com.zeei.das.dps.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月27日下午5:12:01
* 
* 修改历史
* 1.0 quanhongsheng 2017年4月27日下午5:12:01 创建文件
*
*/

package com.zeei.das.dps.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zeei.das.dps.vo.ExcludeTimeVO;
import com.zeei.das.dps.dao.AuditRuleDAO;
import com.zeei.das.dps.dao.PointSiteDAO;
import com.zeei.das.dps.dao.PointSystemDAO;
import com.zeei.das.dps.vo.DataFlagAuditVO;
import com.zeei.das.dps.vo.PointSiteVO;
import com.zeei.das.dps.vo.PointSystemVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.ZeroNegativeVO;

/**
 * 类 名 称：CfgServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

@Service("cfgService")
public class CfgServiceImpl {

	@Autowired
	PointSiteDAO pointSiteDAO;

	@Autowired
	AuditRuleDAO auditRuleDAO;

	@Autowired
	PointSystemDAO pointSystemDAO;

	/**
	 * 
	 * queryStation:获取所有站点配置信息
	 *
	 * @return List<PointSiteVO>
	 */
	public List<PointSiteVO> queryStation() {

		return pointSiteDAO.getPointSiteList();

	}

	/**
	 * 
	 * getStation:根据站点mn获取站点配置信息
	 *
	 * @param MN
	 * @return PointSiteVO
	 */
	public PointSiteVO getStation(String MN) {

		return pointSiteDAO.getStation(MN);
	}

	/**
	 * 
	 * queryAuditRule:查询所有的自动审核规则
	 *
	 * @return List<AuditRuleVO>
	 */
	public List<ZeroNegativeVO> queryZeroAuditRule() {
		return auditRuleDAO.queryZeroAuditRule();
	}

	/**
	 * 
	 * queryPollute:查询系统因子编码
	 * 
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute() {
		return auditRuleDAO.queryPollute();
	}

	/**
	 * 
	 * queryAuditRule:查询所有的自动审核规则
	 *
	 * @return List<AuditRuleVO>
	 */
	public List<DataFlagAuditVO> queryDataFlagAuditRule() {
		return auditRuleDAO.queryDataFlagAuditRule();
	}

	/**
	 * 
	 * getAuditRule:根据站点Id 获取站点自动审核
	 *
	 * @param MN
	 * @return List<AuditRuleVO>
	 */
	public List<ZeroNegativeVO> getZeroAuditRuleByMN(String pointCode) {
		return auditRuleDAO.getZeroAuditRuleByMN(pointCode);
	}

	/**
	 * 
	 * queryPointSystem:
	 *
	 * @return List<PointSystemVO>
	 */
	public List<PointSystemVO> queryPointSystem() {

		return pointSystemDAO.getPointSystemList();
	}

	/**
	 * 
	 * querySystemTables:查找系统所有的表名
	 *
	 * @param tableSchema
	 * @return List<String>
	 */
	public List<String> querySystemTables(String tableSchema) {

		return pointSiteDAO.querySystemTables(tableSchema);
	}

	/**
	 * 
	 * queryRegularStopTime:查询异常申报时段
	 * 
	 * @return List<RegularStopTimeVO>
	 */

	public List<ExcludeTimeVO> queryExceptionTime() {
		return pointSiteDAO.queryExceptionTime();
	}

	/**
	 * 
	 * queryRegularStopTime:查询规律性停产时段
	 * 
	 * @return List<RegularStopTimeVO>
	 */
	public List<ExcludeTimeVO> queryRegularStopTime() {
		return pointSiteDAO.queryRegularStopTime();
	}

}
