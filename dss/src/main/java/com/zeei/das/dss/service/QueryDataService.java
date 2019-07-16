/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：QueryDataService.java
* 包  名  称：com.zeei.das.dss.service.impl
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午9:27:30
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午9:27:30 创建文件
*
*/

package com.zeei.das.dss.service;

import java.util.List;

import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AuditLogVO;
import com.zeei.das.dss.vo.DataTimeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.SitePolluterVo;

/**
 * 类 名 称：QueryDataService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

public interface QueryDataService {
	/**
	 * 
	 * queryDataMin:查询分钟数据
	 *
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            测点编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryDataByCondition(String tableName, String pointCode, String beginTime,
			String endTime, List<String> polluteCodes, String systemType);

	/**
	 * 
	 * queryPollute:查询气象6参数
	 *
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute();

	/**
	 * 
	 * queryVPollute:查询虚拟因子编码
	 *
	 * @return List<PolluteVO>
	 */
	public List<String> queryVPollute();

	/**
	 * 
	 * queryValidPoll:查询站点配置的虚拟编码
	 *
	 * @return List<String>
	 */
	public List<SitePolluterVo> queryValidPoll();

	/**
	 * 
	 * queryDataTime:TODO 请修改方法功能描述
	 *
	 * @param tableName
	 * @param beginTime
	 * @param endTime
	 * @return List<DataTimeVO>
	 */
	public List<DataTimeVO> queryDataTime(String tableName, String beginTime, String endTime);

	/**
	 * 
	 * queryYMDataByCondition:查询年/月数据
	 *
	 * @param tableName
	 * @param dataType
	 * @param pointCode
	 * @param beginTime
	 * @param endTime
	 * @param polluteCodes
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryYMDataByCondition(String tableName, String dataType, String pointCode,
			String beginTime, String endTime, List<String> polluteCodes, String systemType);

	/**
	 * 
	 * queryRCDataByCondition:查询电流大于额定电流的原始数据
	 *
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            站点名称
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            接收时间
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryRCDataByCondition(String tableName, String pointCode, String beginTime,
			String endTime);

	/**
	 * 
	 * queryAuditLog:查询审核日志
	 * 
	 * @param pointCode
	 * @param auditTime
	 * @return List<AuditLogVO>
	 */
	public List<AuditLogVO> queryAuditLog(String pointCode, String beginTime, String endTime);

	/**
	 * 
	 * queryAuditData:查询待审核的数据
	 * 
	 * @param tableName
	 * @param pointCode
	 * @param auditTime
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryAuditData(String tableName, String pointCode, String beginTime, String endTime);

	/**
	 * queryMaxPollValue:查询因子对应的二级最大标准值
	 * 
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryMaxPollValue();

	/**
	 * queryAirHvyDays:查询重污染未结束的数据
	 * 
	 * @return List<AirHvyDayVo>
	 */
	public List<AirHvyDayVo> queryAirHvyDays();

	/**
	 * queryPollIncident: 获取分钟污染事件未结束的
	 * 
	 * @return List<PollIncidentVo>
	 */
	public List<PollIncidentVo> queryPollIncident();

	/**
	 * queryNowHvys: 查询数据库中已经连续超标的数据
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return List<AqiDataVO>
	 */
	public List<AqiDataVO> queryNowHvys(String beginTime, String endTime);
}
