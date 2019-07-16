/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：ParamDao.java
* 包  名  称：com.zeei.das.cas.dao
* 文件描述：
* 创建日期：2017年5月3日下午3:47:53
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月3日下午3:47:53 创建文件
*
*/

package com.zeei.das.cas.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.cas.vo.ParamVO;

/**
 * 类 名 称：ParamDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface ParamDao {

	/**
	 * 
	 * queryParam:查询系统中所有因子
	 *
	 * @return List<ParamVO>
	 */
	public List<ParamVO> queryParam();

	/**
	 * 
	 * queryParamByMN:根据站点mn查询站点配置因子
	 *
	 * @param MN
	 * @return List<ParamVO>
	 */
	public List<ParamVO> queryParamByMN(@Param("MN") String MN);

	/**
	 * 
	 * queryParamMapping:查询系统中所有因子的转码
	 *
	 * @return Map<String,String>
	 */
	public List<Map<String, String>> queryParamMapping();

}
