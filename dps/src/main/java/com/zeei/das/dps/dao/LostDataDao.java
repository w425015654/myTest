/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：LostDataDao.java
* 包  名  称：com.zeei.das.dps.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午9:56:38
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午9:56:38 创建文件
*
*/

package com.zeei.das.dps.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.dps.vo.CtlRecVO;
import com.zeei.das.dps.vo.DataTimeVO;
import com.zeei.das.dps.vo.LostDataRecordVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1VO;

/**
 * 类 名 称：LostDataDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface LostDataDao {

	/**
	 * 
	 * insertDataTimeBatch:批量更新最新数据时间
	 *
	 * @param list
	 * @return Integer
	 */
	public Integer insertDataTimeBatch(List<DataTimeVO> list);

	/**
	 * 
	 * insertLostDataBatch:批量写入丢失数据时间
	 *
	 * @param list
	 * @return Integer
	 */
	public Integer insertLostDataBatch(List<LostDataRecordVO> list);

	/**
	 * 
	 * deleteLostData:补数完成，删除丢失数据表
	 *
	 * @param vo
	 * @return Integer
	 */
	public Integer deleteLostData(LostDataRecordVO vo);

	/**
	 * 
	 * queryLostData:查询缺失数据表
	 *
	 * @param beginTime
	 * @param endTime
	 * @return List<LostDataRecordVO>
	 */
	public List<LostDataRecordVO> queryLostData(@Param("beginTime") String beginTime, @Param("endTime") String endTime);

	/**
	 * 
	 * insertLostDataCmdBatch:批量写入补数命令
	 *
	 * @param list
	 * @return Integer
	 */
	public Integer insertLostDataCmdBatch(List<CtlRecVO> list);
	
	
	/**
	 * querySuppData:
	 * 查询补数相关数据，用于计算补数是否存疑
	 * @param beginTime
	 * @param endTime
	 * @return List<LostDataRecordVO>
	 */
	public List<T20x1VO> querySuppData(@Param("pointCode") String pointCode,@Param("polluters") Set<String> polluters,
			@Param("beginTime") String beginTime, @Param("endTime") String endTime,@Param("tableName") String tableName);
	
	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的存疑  数据标识 为1
	 * @param datas
	 * @return Integer
	 */
	public Integer updateDoubtfulHH(@Param("datas") List<T20x1VO> datas);

	/**
	 * queryHhCycletime:测点下的因子小时周期
	 * 
	 * @param pointCode
	 * @return List<PolluteVO>
	 */
	public List<PolluteVO> queryHhCycletime(@Param("pointCode") String pointCode);
}
