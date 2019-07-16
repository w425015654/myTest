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

package com.zeei.das.aas.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.PolluterLevelVo;

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
	public List<MonitorDataVO> queryNextDataByCondition(@Param("tableName") String tableName,
			@Param("pointCode") String pointCode, @Param("beginTime") Date beginTime,
			@Param("endTime") Date endTime, @Param("polluteCode") String polluteCode,@Param("systemType") String systemType);
	
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
	public List<MonitorDataVO> queryBeforeDataByCondition(@Param("tableName") String tableName,
			@Param("pointCode") String pointCode, @Param("beginTime") Date beginTime,
			@Param("endTime") Date endTime, @Param("polluteCode") String polluteCode,@Param("systemType") String systemType);
	
	/**
	 * queryPolluteLevel:
	 * 查询超标水质标准等级数据，即三级水质污染标准
	 * @return List<>
	 */
	public List<PolluterLevelVo>  queryRivPolluteLevel();
	
	
	/**
	 * queryMonitorData:
	 * 查询数据周期内对应因子 的所有记录
	 * @param tableName
	 * @param beginTime
	 * @param endTime
	 * @param pollutes
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMonitorData(@Param("tableName") String tableName,@Param("systemType") String systemType,
			@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("pointCode") String pointCode, @Param("polluteCodes") List<String> pollutes);
	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的存疑  数据标识 为1
	 * @param datas
	 * @return Integer
	 */
	public Integer updateDoubtfulHH(@Param("datas") List<MonitorDataVO> datas);
	
	/**
	 * 
	 * queryPollute:查询系统因子编码
	 * 
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute();
	
	/**
	 * queryHourPollute:查询因子小时数据周期
	 * 
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryHourPollute();
}
