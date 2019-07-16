/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：QueryDataDao.java
* 包  名  称：com.zeei.das.dss.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月11日上午8:47:39
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月11日上午8:47:39 创建文件
*
*/

package com.zeei.das.dss.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AuditLogVO;
import com.zeei.das.dss.vo.DataTimeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;
import com.zeei.das.dss.vo.SitePolluterVo;

/**
 * 类 名 称：QueryDataDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface QueryDataDao {

	/**
	 * 
	 * queryDataMin:查询监测数据
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
	public List<MonitorDataVO> queryDataByCondition(@Param("tableName") String tableName,
			@Param("pointCode") String pointCode, @Param("beginTime") String beginTime,
			@Param("endTime") String endTime, @Param("polluteCodes") List<String> polluteCodes,
			@Param("systemType") String systemType);

	/**
	 * 
	 * queryPollute:查询气象6参数
	 *
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute();

	/**
	 * 
	 * queryVPollute:查询虚拟因子
	 *
	 * @return List<PolluteVO>
	 */
	public List<String> queryVPollute();

	/**
	 * 
	 * queryValidPoll:查询所有站点参与统计因子
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
	public List<DataTimeVO> queryDataTime(@Param("tableName") String tableName, @Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

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
	public List<MonitorDataVO> queryYMDataByCondition(@Param("tableName") String tableName,
			@Param("dataType") String dataType, @Param("pointCode") String pointCode,
			@Param("beginTime") String beginTime, @Param("endTime") String endTime,
			@Param("polluteCodes") List<String> polluteCodes, @Param("systemType") String systemType);

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
	public List<MonitorDataVO> queryRCDataByCondition(@Param("tableName") String tableName,
			@Param("pointCode") String pointCode, @Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

	/**
	 * 
	 * queryAuditLog:查询审核日志
	 * 
	 * @param pointCode
	 * @param auditTime
	 * @return List<AuditLogVO>
	 */
	public List<AuditLogVO> queryAuditLog(@Param("pointCode") String pointCode, @Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

	/**
	 * 
	 * queryAuditData:查询待审核的数据
	 * 
	 * @param tableName
	 * @param pointCode
	 * @param auditTime
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryAuditData(@Param("tableName") String tableName,
			@Param("pointCode") String pointCode, @Param("beginTime") String beginTime,
			@Param("endTime") String endTime);

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
	public List<AqiDataVO> queryNowHvys(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

}
