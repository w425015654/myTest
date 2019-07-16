/** 
* Copyright (C) 2012-2017 ZEEI Inc.All Rights Reserved.
* 项目名称：cas
* 文件名称：CtlMsgDao.java
* 包  名  称：com.zeei.das.cas.dao
* 文件描述：TODO 请修改文件描述
* 创建日期：2017年5月4日下午3:22:23
* 
* 修改历史
* 1.0 quanhongsheng 2017年5月4日下午3:22:23 创建文件
*
*/

package com.zeei.das.cas.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zeei.das.cas.vo.CtlRecDetailVO;
import com.zeei.das.cas.vo.Msg2021VO;
import com.zeei.das.cas.vo.Msg2062VO;
import com.zeei.das.cas.vo.Msg2063VO;
import com.zeei.das.cas.vo.Msg20656VO;
import com.zeei.das.cas.vo.Msg2070VO;
import com.zeei.das.cas.vo.Msg2076VO;

/**
 * 类 名 称：CtlMsgDao 类 描 述：TODO 请修改文件描述 功能描述：TODO 请修改功能描述 创建作者：quanhongsheng
 */

public interface CtlMsgDao {

	/**
	 * 
	 * insert2062ByBatch:批量插入站点上传监测指标核查数据
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert2062ByBatch(@Param("list") List<Msg2062VO> t2062);
	
	/**
	 * 
	 * insert20656ByBatch:批量插入站点上传零点核查2065,跨度核查2066
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert20656ByBatch(@Param("list") List<Msg20656VO> t20656);
	
	/**
	 * 
	 * insert2070ByBatch:批量插入站点质控结果数据(2070)
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert2070ByBatch(@Param("list") List<Msg2070VO> t2070);
	
	/**
	 * 
	 * insert2062ByBatch:批量插入站点上传监测指标加回收数据(2063)
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert2063ByBatch(@Param("list") List<Msg2063VO> t2063);
	
	/**
	 * 
	 * insert2021:插入站点设备状态数据
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert2021Latest(@Param("list") List<Msg2021VO> data);

	/**
	 * 
	 * insert2021ByBatch:批量插入站点设备状态数据
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insert2021ByBatch(@Param("list") List<Msg2021VO> t2021);


	/**
	 * 
	 * insert2076:插入站点日志数据
	 *
	 * @param data
	 * @return Integer
	 */

	public Integer insert2076(Msg2076VO data);

	/**
	 * 
	 * insert2076ByBacth:批量插入站点日志数据
	 *
	 * @param data
	 * @return Integer
	 */

	public Integer insert2076ByBatch(@Param("list") List<Msg2076VO> t2076);

	/**
	 * 
	 * insertCtrlRecDetail:写入返控记录详请
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insertCtrlRecDetail(CtlRecDetailVO data);

	/**
	 * 
	 * insertCtrlRecDetailByBacth:批量写入返控记录详请
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insertCtrlRecDetailByBatch(@Param("list") List<CtlRecDetailVO> ctlRecDetails);

	/**
	 * 
	 * insertCtrlRecDetailByBacth:批量写入返控记录详请
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer insertCompRecDetailByBatch(@Param("list") List<CtlRecDetailVO> ctlRecDetails);

	/**
	 * 
	 * updateCtlRec:修改返控记录状态
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer updateCtlRec(CtlRecDetailVO data);

	/**
	 * 
	 * updateCtlRecByBacth:修改返控记录状态
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer updateCtlRecByBatch(@Param("list") List<CtlRecDetailVO> ctlRecs);

	/**
	 * 
	 * updateCtlRecByBacth:修改返控记录状态
	 *
	 * @param data
	 * @return Integer
	 */
	public Integer updateCompRecByBatch(@Param("list") List<CtlRecDetailVO> ctlRecs);
	
	
	/**
	 * 
	 * insert2062ByBatch:批量删除数据
	 *
	 * @param data
	 * @return Integer
	 */
	public  void  delete2063ByBatch(Map<String,Object> map);


}
