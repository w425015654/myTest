/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：LostDataServiceImpl.java
* 包  名  称：com.zeei.das.dps.service
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月26日上午10:05:33
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月26日上午10:05:33 创建文件
*
*/

package com.zeei.das.dps.service;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zeei.das.dps.dao.LostDataDao;
import com.zeei.das.dps.vo.CtlRecVO;
import com.zeei.das.dps.vo.DataTimeVO;
import com.zeei.das.dps.vo.LostDataRecordVO;
import com.zeei.das.dps.vo.PolluteVO;
import com.zeei.das.dps.vo.T20x1VO;

/**
 * 类 名 称：LostDataServiceImpl 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述
 * 创建作者：quanhongsheng
 */

@Service("lostDataService")
public class LostDataServiceImpl {

	@Autowired
	LostDataDao lostDataDao;

	/**
	 * 
	 * insertDataTimeBatch:批量更新最新数据时间
	 *
	 * @param list
	 * @return Integer
	 */
	@Transactional
	public Integer insertDataTimeBatch(List<DataTimeVO> list) {
		int batchSize = 200;
		int insertNumber = 0;
		int loopTime = list.size() / batchSize;
		int resident = list.size() % batchSize;

		int loop = 0;

		for (; loop < loopTime; loop++) {
			insertNumber += lostDataDao.insertDataTimeBatch(list.subList(loop * batchSize, (loop + 1) * batchSize));
		}
		if (resident > 0) {
			insertNumber += lostDataDao
					.insertDataTimeBatch(list.subList(loop * batchSize, loop * batchSize + resident));
		}
		return insertNumber;
	}

	/**
	 * 
	 * insertLostDataBatch:批量写入丢失数据时间
	 *
	 * @param list
	 * @return Integer
	 */
	@Transactional
	public Integer insertLostDataBatch(List<LostDataRecordVO> list) {
		int batchSize = 200;
		int insertNumber = 0;
		int loopTime = list.size() / batchSize;
		int resident = list.size() % batchSize;

		int loop = 0;

		for (; loop < loopTime; loop++) {
			insertNumber += lostDataDao.insertLostDataBatch(list.subList(loop * batchSize, (loop + 1) * batchSize));
		}
		if (resident > 0) {
			insertNumber += lostDataDao
					.insertLostDataBatch(list.subList(loop * batchSize, loop * batchSize + resident));
		}
		return insertNumber;
	}

	/**
	 * 
	 * deleteLostData:补数完成，删除丢失数据表
	 *
	 * @param vo
	 * @return Integer
	 */
	@Transactional
	public Integer deleteLostData(LostDataRecordVO vo) {
		return lostDataDao.deleteLostData(vo);
	}

	/**
	 * 
	 * queryLostData:查询缺失数据表
	 *
	 * @param beginTime
	 * @param endTime
	 * @return List<LostDataRecordVO>
	 */
	@Transactional
	public List<LostDataRecordVO> queryLostData(String beginTime, String endTime) {
		return lostDataDao.queryLostData(beginTime, endTime);
	}

	/**
	 * 
	 * insertLostDataCmdBatch:批量写入补数命令
	 *
	 * @param list
	 * @return Integer
	 */
	@Transactional
	public Integer insertLostDataCmdBatch(List<CtlRecVO> list) {
		int batchSize = 200;
		int insertNumber = 0;
		int loopTime = list.size() / batchSize;
		int resident = list.size() % batchSize;

		int loop = 0;

		for (; loop < loopTime; loop++) {
			insertNumber += lostDataDao.insertLostDataCmdBatch(list.subList(loop * batchSize, (loop + 1) * batchSize));
		}
		if (resident > 0) {
			insertNumber += lostDataDao
					.insertLostDataCmdBatch(list.subList(loop * batchSize, loop * batchSize + resident));
		}
		return insertNumber;
	}
	
	/**
	 * querySuppData:
	 * 查询补数相关数据，用于计算补数是否存疑
	 * @param pointCode
	 * @param polluters
	 * @param beginTime
	 * @param endTime
	 * @return List<LostDataRecordVO>
	 */
	@Transactional
	public List<T20x1VO> querySuppData(String pointCode, Set<String> polluters,
			 String beginTime, String endTime,String tableName) {
		return lostDataDao.querySuppData(pointCode ,polluters ,beginTime ,endTime ,tableName);
	}
	
	
	/**
	 * updateOutlierHH:
	 * 批量更新   小时数据的存疑  数据标识 为1
	 * @param datas
	 * @return Integer
	 */
	@Transactional
	public Integer updateDoubtfulHH(List<T20x1VO> datas){
		
		return  lostDataDao.updateDoubtfulHH(datas);
	}
	
	/**
	 * queryHhCycletime:测点下的因子小时周期
	 * 
	 * @param pointCode
	 * @return List<PolluteVO>
	 */
	@Transactional
	public List<PolluteVO> queryHhCycletime(String pointCode){
		
		return  lostDataDao.queryHhCycletime(pointCode);
	}

}
