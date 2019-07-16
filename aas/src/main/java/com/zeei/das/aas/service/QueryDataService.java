/** 
* Copyright (C) 2012-2018 ZEEI Inc.All Rights Reserved.
* 项目名称：aas
* 文件名称：QueryMonitorDataService.java
* 包  名  称：com.zeei.das.aas.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2018年9月29日下午3:09:20
* 
* 修改历史
* 1.0 wudahe 2018年9月29日下午3:09:20 创建文件
*
*/

package com.zeei.das.aas.service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.zeei.das.aas.vo.MonitorDataVO;
import com.zeei.das.aas.vo.PolluteVO;
import com.zeei.das.aas.vo.PolluterLevelVo;

/** 
* @类型名称：QueryMonitorDataService
* @类型描述：查询监测表
* @功能描述：查询监测表
* @创建作者：wudahe
*
*/

public interface QueryDataService {
	/**
	 * 
	 * queryBeforeDataByCondition：查询左邻数据
	 *
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            测点编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param polluteCode
	 *            因子
	 * @param systemType
	 *            系统类型
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryBeforeDataByCondition(String tableName, String pointCode, Date beginTime,
			Date endTime, String polluteCode, String systemType);

	
	/**
	 * 
	 * queryNextDataByCondition：查询右邻数据
	 *
	 * @param tableName
	 *            表名
	 * @param pointCode
	 *            测点编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param polluteCode
	 *            因子
	 * @param systemType
	 *            系统类型
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryNextDataByCondition(String tableName, String pointCode, Date beginTime,
			Date endTime, String polluteCode, String systemType);
	
	
	/**
	 * queryPolluteLevel:
	 * 查询超标水质标准等级数据，即三级水质污染标准
	 * @return List<>
	 */
	public List<PolluterLevelVo> queryRivPolluteLevel();
	
	
	/**
	 * queryMonitorData:
	 * 查询数据周期内对应因子 的所有记录
	 * @param tableName
	 * @param beginTime
	 * @param endTime
	 * @param pollutes
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMonitorData(String tableName, String systemType, String beginTime, 
			String endTime, String pointCode, List<String> pollutes);
	
	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的存疑  数据标识 为1
	 * @param datas
	 * @return Integer
	 */
	public Integer updateDoubtfulHH(List<MonitorDataVO> datas);
	
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
