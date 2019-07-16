/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：dps
* 文件名称：T20x1Storage.java
* 包  名  称：com.zeei.das.dps.storage
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年4月27日上午10:32:45
* 
* 修改历史
* 1.0 zhou.yongbo 2017年4月27日上午10:32:45 创建文件
*
*/

package com.zeei.das.dps.storage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zeei.das.dps.dao.T20x1DAO;
import com.zeei.das.dps.vo.T20x1VO;

/**
 * 类 名 称：T20x1Storage 类 描 述：T2031 T2051 T2061批量入库
 */
@Service("t20x1Store")
public class T20x1Storage {
	// private static Logger logger =
	// LoggerFactory.getLogger(T20x1Storage.class);

	@Autowired
	T20x1DAO t20x1dao;

	/**
	 * batchInsert:T20x1批量入库
	 *
	 * @param table
	 *            表名
	 * @param list
	 *            T20x1数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void batchInsert(String table, List<T20x1VO> t20x1s) {
		t20x1dao.insertT20x1ByBatch(table, t20x1s, null);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertLatestByBatch(List<T20x1VO> t20x1s) {
		t20x1dao.insertLatestByBatch(t20x1s);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertT20x1ByBatch(String table, List<T20x1VO> t20x1s, String isOverride) {
		t20x1dao.insertT20x1ByBatch(table, t20x1s, isOverride);
	}
}
