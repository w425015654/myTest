/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dss
* 文件名称：AqiService.java
* 包  名  称：com.zeei.das.dss.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月18日下午1:38:00
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月18日下午1:38:00 创建文件
*
*/

package com.zeei.das.dss.service;

import java.util.List;

import com.zeei.das.dss.vo.AQILevelVO;
import com.zeei.das.dss.vo.AirHvyDayVo;
import com.zeei.das.dss.vo.AirOprecDetlVo;
import com.zeei.das.dss.vo.AqiDataVO;
import com.zeei.das.dss.vo.AreaVO;
import com.zeei.das.dss.vo.IAQIRangeVO;
import com.zeei.das.dss.vo.MonitorDataVO;
import com.zeei.das.dss.vo.ParamsVo;
import com.zeei.das.dss.vo.PollIncidentVo;
import com.zeei.das.dss.vo.PolluteVO;

/**
 * 类 名 称：AqiService 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface AqiService {

	/**
	 * 
	 * insertAQIDD:写入aqi日报数据
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insertAQIDD(AqiDataVO data);

	/**
	 * 
	 * insertAQIHH:写入aqi实时报数据
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insertAQIHH(AqiDataVO data);

	/**
	 * 
	 * queryMointorDataDD:查询空气站小时数据
	 *
	 * @param pointCode
	 *            测点编码
	 * @param areaCode
	 *            区域编码
	 * @param polluteCodes
	 *            污染物编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMointorDataHH(List<String> pointCodes, List<String> areaCodes, List<String> polluteCodes,
			String beginTime, String endTime);

	/**
	 * 
	 * queryMointorDataDD:查询空气站日数据
	 *
	 * @param pointCode
	 *            测点编码
	 * @param areaCode
	 *            区域编码
	 * @param polluteCodes
	 *            污染物编码
	 * @param beginTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMointorDataDD(String pointCode, List<String> areaCodes, List<String> polluteCodes,
			String beginTime, String endTime);

	/**
	 * queryMonData:获取当前时间及往前一天的数据
	 * 
	 * @param beginTime
	 * @param endTime
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMonData(String beginTime,String endTime);

	/**
	 * 
	 * queryIAQIRange:获取aqi 计算参数
	 *
	 * @return List<IAQIRangeVO>
	 */
	public List<IAQIRangeVO> queryIAQIRange();

	/**
	 * 
	 * queryAQILevel:获取空气质量等级标准
	 *
	 * @return List<AQILevelVO>
	 */
	public List<AQILevelVO> queryAQILevel();

	/**
	 * 
	 * queryArea:查询区域
	 *
	 * @return List<AreaVO>
	 */
	public List<AreaVO> queryArea();
	
	/**
	 * queryPollute:初始化查询因子
	 *
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryPollute();

	/**
	 * queryAreaPoint:查二级区域及旗下测点
	 *
	 * @return List<ParamsVo>
	 */
	public List<ParamsVo> queryAreaPoint();

	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的离群   数据标识
	 * @param datas
	 * @return Integer
	 */
	public Integer updateOutlierHH(List<MonitorDataVO> datas);
	
	
	/**
	 * getBETime:
	 * 获取小时AQI在一段时间内，AQI超过200的最开始时间或结束时间
	 * flag为null表示计算结束时间
	 * flag为1表示计算开始时间
	 * @return String
	 */
	public String getBETime(String code, String beginDate, String endDate ,String flag);
	

	/**
	 * insertAirHvy:重污染事件   时间信息入库
	 * 
	 * @param data
	 * @return Integer
	 */
	public Integer insertAirHvy(AirHvyDayVo data);
	
	/**
	 * queryMointorDataMM:
	 * 查询分钟数据库的相关分钟数据
	 *  * 去掉无效数据和负值的 非审核数据  
	 * @param pointCode
	 * @param tableName
	 * @param polluteCodes
	 * @param beginTime
	 * @param endTime
	 * @return List<MonitorDataVO>
	 */
	public List<MonitorDataVO> queryMointorDataMM(List<String> pointCode, String tableName, List<String> polluteCodes,
			String beginTime, String endTime);
	
	
	/**
	 * insertPollIncident:
	 * 入库分钟数据统计的污染事件
	 * @param data
	 * @return Integer
	 */
	public Integer insertPollIncident(PollIncidentVo data);
	
	/**
	 * updatePollIncident:
	 * 更新数据 结束时间和连续超标标识
	 * @param data
	 * @return Integer
	 */
	public Integer updatePollIncident(List<PollIncidentVo> datas);
	
	/**
	 * insertAirOprecDetls:
	 * 入库分钟AQI连续超标事件对应的详情数据
	 * @param datas
	 * @return Integer
	 */
	public Integer insertAirOprecDetls(List<AirOprecDetlVo> datas);
	
	
	/**
	 * insertMinAqiDatas:入库分钟aqi数据
	 * 
	 * @param datas
	 * @return Integer
	 */
	public Integer insertMinAqiDatas(List<AqiDataVO> datas);

}
